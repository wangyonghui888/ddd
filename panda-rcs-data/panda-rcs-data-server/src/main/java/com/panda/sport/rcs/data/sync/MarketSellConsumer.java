package com.panda.sport.rcs.data.sync;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.config.RedissonManager;
import com.panda.sport.rcs.data.mapper.*;
import com.panda.sport.rcs.data.mq.RcsConsumer;
import com.panda.sport.rcs.data.service.MarketCategorySetService;
import com.panda.sport.rcs.data.service.RcsCategorySetTraderWeightService;
import com.panda.sport.rcs.data.service.RcsStandardSportMarketSellService;
import com.panda.sport.rcs.pojo.*;
import com.panda.sport.rcs.pojo.bo.FindMarketCategoryListAndNamesBO;
import com.panda.sport.rcs.pojo.bo.GetPerformanceSetPlaysBO;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateAcceptConfigSettle;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateAcceptConfigVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 赛事盘口开售推送
 * @Author: Vecotr
 * @Date: 2019/12/30
 */

@Slf4j
@Component
@RocketMQMessageListener(
        topic = MqConstants.MATCH_ADVANCE_SALE,
        consumerGroup = "RCS_DATA_MATCH_ADVANCE_SALE_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MarketSellConsumer extends RcsConsumer<Request<RcsStandardSportMarketSell>> {

    @Autowired
    RcsStandardSportMarketSellService standardSportMarketSellService;
    
    @Autowired
    private RcsStandardSportMarketSellMapper rcsStandardSportMarketSellMapper;

    @Autowired
    private RcsTournamentTemplateMapper rcsTournamentTemplateMapper;

    @Autowired
    private RcsTournamentTemplateAcceptConfigMapper configMapper;

    @Autowired
    private RcsTournamentTemplateAcceptConfigVoMapper configVoMapper;

    @Autowired
    private RcsTournamentTemplateAcceptConfigSettleMapper configSettleMapper;

    @Autowired
    private RcsCategorySetTraderWeightService rcsCatregorySetTraderWeightService;

    @Autowired
    MarketCategorySetService marketCategorySetService;

    @Autowired
    private MongoTemplate mongotemplate;

    @Autowired
    protected RedissonManager redissonManager;

    private static final String RCS_DATA_KEY_CACHE_KEY = RedisKeys.RCS_DATA_KEY_CACHE_KEY;
    private static final String OPEN_SELL_LOCK = "openSellLock";

    @Override
    protected String getTopic() {
        return MqConstants.MATCH_ADVANCE_SALE;
    }

    /**
     * @Description: 盘口开售
     * @Author: Vector
     * @Date: 2019/12/12
     **/
    @Override
    public Boolean handleMs(Request<RcsStandardSportMarketSell> msg) {
        log.info("::{}::datasync-赛事盘口开售推送", "RDMADSG_"+msg.getLinkId()+"_"+msg.getData().getMatchInfoId());
        try {
            RcsStandardSportMarketSell data = msg.getData();
            if(null!=data.getMatchInfoId()){
                String key = String.format(RCS_DATA_KEY_CACHE_KEY, OPEN_SELL_LOCK, data.getMatchInfoId());
                try {
                    redissonManager.lock(key);
                    long time = System.currentTimeMillis();
                    data.setCreateTime(time);
                    data.setModifyTime(time);
                    QueryWrapper<RcsStandardSportMarketSell> queryWrapper = new QueryWrapper<>();
                    queryWrapper.lambda().eq(RcsStandardSportMarketSell::getMatchInfoId, data.getMatchInfoId());
                    RcsStandardSportMarketSell oldBean = standardSportMarketSellService.getOne(queryWrapper);
                    standardSportMarketSellService.insertOrUpdate(data);

                    if("Not_Set".equals(data.getPreTraderStatus()) || "Not_Set".equals(data.getLiveTraderStatus())) {
                        rcsStandardSportMarketSellMapper.updateNosetBtTraderInfo(data);
                    }
                    //kir-bug29771
                    buildTemplateDataSource(data,msg.getLinkId());
                    //初始化绩效玩法指派
                    initPerformancePlayAssignments(data,oldBean,msg.getLinkId());
                } catch (Exception e) {
                    log.error("::{}::,{},{}","RDMADSG_"+msg.getLinkId(),e.getMessage(), e);
                }finally {
                    redissonManager.unlock(key);
                }
            }
        } catch (Exception e) {
            log.error("::{}::datasync-赛事盘口开售推送错误：{}{}{}" , "RDMADSG_"+msg.getLinkId()+"_"+msg.getData().getMatchInfoId(),JsonFormatUtils.toJson(msg), e.getMessage(), e);
            return false;
        }
        return true;
    }


    /*初化玩法权重
     * @param newBean
     * @param oldBean
     */
    private void initPerformancePlayAssignments(RcsStandardSportMarketSell newBean, RcsStandardSportMarketSell oldBean, String linkId) {
        try {
            log.info("::{}::,oldDataToNewData0","RDMADSG_"+linkId+"_"+newBean.getMatchInfoId());
            Long matchInfoId = newBean.getMatchInfoId();
            //老权重转和成新权重
            oldDataToNewData(linkId,newBean);
            QueryWrapper<RcsCategorySetTraderWeight> rcsCategorySetTraderWeightWrapper = new QueryWrapper<>();
            rcsCategorySetTraderWeightWrapper.lambda().eq(RcsCategorySetTraderWeight::getMatchId,matchInfoId);
            List<RcsCategorySetTraderWeight> list = rcsCatregorySetTraderWeightService.list(rcsCategorySetTraderWeightWrapper);
            if(null==list){list=new ArrayList<>();}
            List<GetPerformanceSetPlaysBO> performanceSetPlays = marketCategorySetService.getPerformanceSetPlays(newBean.getSportId());
            log.info("::{}::,oldDataToNewData4","RDMADSG_"+linkId+"_"+newBean.getMatchInfoId());
            Map<String, List<RcsCategorySetTraderWeight>> collect = list.stream().collect(Collectors.groupingBy(e -> String.valueOf(e.getMarketType())));
            List<RcsCategorySetTraderWeight> addList = new ArrayList<>();
            List<RcsCategorySetTraderWeight> updateList2 = new ArrayList<>();

            //如果权重为空新增
            if(!CollectionUtils.isEmpty(performanceSetPlays)){
                for (GetPerformanceSetPlaysBO performanceSetPlay : performanceSetPlays) {
                    if(CollectionUtils.isEmpty(collect.get("0"))&& StringUtils.isNotBlank(newBean.getLiveTraderId())&&StringUtils.isNotBlank(newBean.getLiveTrader())){
                        RcsCategorySetTraderWeight newRcsCategorySetTraderWeight = new RcsCategorySetTraderWeight();
                        newRcsCategorySetTraderWeight.setSportId(newBean.getSportId());
                        newRcsCategorySetTraderWeight.setMarketType(0);
                        newRcsCategorySetTraderWeight.setTraderId(Long.valueOf(newBean.getLiveTraderId()));
                        newRcsCategorySetTraderWeight.setTraderCode(newBean.getLiveTrader());
                        newRcsCategorySetTraderWeight.setMatchId(newBean.getMatchInfoId());
                        if(null==performanceSetPlay.getPlayId()){
                            log.info("::{}::玩法集玩法为空 :{}","RDMADSG_"+linkId+"_"+newBean.getMatchInfoId(), performanceSetPlay.getSetNo());
                            continue;
                        }
                        newRcsCategorySetTraderWeight.setTypeId(performanceSetPlay.getPlayId());
                        newRcsCategorySetTraderWeight.setSetNo(performanceSetPlay.getSetNo());
                        newRcsCategorySetTraderWeight.setVersion(1);
                        newRcsCategorySetTraderWeight.setWeight(100);
                        addList.add(newRcsCategorySetTraderWeight);
                    }

                    if(CollectionUtils.isEmpty(collect.get("1"))&&StringUtils.isNotBlank(newBean.getPreTraderId())&&StringUtils.isNotBlank(newBean.getPreTrader())){
                        RcsCategorySetTraderWeight newRcsCategorySetTraderWeight1 = new RcsCategorySetTraderWeight();
                        newRcsCategorySetTraderWeight1.setSportId(newBean.getSportId());
                        newRcsCategorySetTraderWeight1.setMarketType(1);
                        newRcsCategorySetTraderWeight1.setTraderId(Long.valueOf(newBean.getPreTraderId()));
                        newRcsCategorySetTraderWeight1.setTraderCode(newBean.getPreTrader());
                        newRcsCategorySetTraderWeight1.setMatchId(newBean.getMatchInfoId());
                        if(null==performanceSetPlay.getPlayId()){
                            log.info("::{}::玩法集玩法为空 :{}","RDMADSG_"+linkId+"_"+newBean.getMatchInfoId(), performanceSetPlay.getSetNo());
                            continue;
                        }
                        newRcsCategorySetTraderWeight1.setTypeId(performanceSetPlay.getPlayId());
                        newRcsCategorySetTraderWeight1.setSetNo(performanceSetPlay.getSetNo());
                        newRcsCategorySetTraderWeight1.setVersion(1);
                        newRcsCategorySetTraderWeight1.setWeight(100);
                        addList.add(newRcsCategorySetTraderWeight1);
                    }
                }
            }
            //如果操盘手为空清除操盘手
            if (StringUtils.isBlank(newBean.getLiveTraderId())){
                QueryWrapper<RcsCategorySetTraderWeight> rcsCategorySetTraderWeightRemoveWrapper = new QueryWrapper<>();
                rcsCategorySetTraderWeightRemoveWrapper.lambda().eq(RcsCategorySetTraderWeight::getMatchId,matchInfoId);
                rcsCategorySetTraderWeightRemoveWrapper.lambda().eq(RcsCategorySetTraderWeight::getMarketType,0);
                rcsCatregorySetTraderWeightService.remove(rcsCategorySetTraderWeightRemoveWrapper);
            }
            if (StringUtils.isBlank(newBean.getPreTraderId())){
                QueryWrapper<RcsCategorySetTraderWeight> rcsCategorySetTraderWeightRemoveWrapper = new QueryWrapper<>();
                rcsCategorySetTraderWeightRemoveWrapper.lambda().eq(RcsCategorySetTraderWeight::getMatchId,matchInfoId);
                rcsCategorySetTraderWeightRemoveWrapper.lambda().eq(RcsCategorySetTraderWeight::getMarketType,1);
                rcsCatregorySetTraderWeightService.remove(rcsCategorySetTraderWeightRemoveWrapper);
            }
            //如果权重不为空  并且主trader变更
            HashMap<String, Integer> duplicateFlagMap = new HashMap<>();  //进行清理标记
            for (RcsCategorySetTraderWeight rcsCategorySetTraderWeight : list) {
                if(!CollectionUtils.isEmpty(collect.get("0"))&&StringUtils.isNotBlank(newBean.getLiveTraderId())&&!newBean.getLiveTraderId().equals(oldBean.getLiveTraderId())){
                    //如果新主操盘手在副操手里面做标记 进行清理标记
                    if(0==rcsCategorySetTraderWeight.getMarketType()&& StringUtils.isNotBlank(newBean.getLiveTraderId())&&rcsCategorySetTraderWeight.getTraderId().equals(Long.valueOf(newBean.getLiveTraderId()))){
                        duplicateFlagMap.put("0_"+rcsCategorySetTraderWeight.getTypeId(),1);
                    }
                    if(0==rcsCategorySetTraderWeight.getMarketType()&&!StringUtils.isBlank(newBean.getLiveTraderId())&&rcsCategorySetTraderWeight.getTraderId().equals(Long.valueOf(oldBean.getLiveTraderId()))){
                        rcsCategorySetTraderWeight.setTraderId(StringUtils.isBlank(newBean.getLiveTraderId())?null:Long.valueOf(newBean.getLiveTraderId()));
                        rcsCategorySetTraderWeight.setTraderCode(newBean.getLiveTrader());
                        updateList2.add(rcsCategorySetTraderWeight);
                    }
                }
                if(!CollectionUtils.isEmpty(collect.get("1"))&&StringUtils.isNotBlank(newBean.getPreTraderId())&&!newBean.getPreTraderId().equals(oldBean.getPreTraderId())){
                    //如果新主操盘手在副操手里面做标记 进行清理标记
                    if(1==rcsCategorySetTraderWeight.getMarketType()&& StringUtils.isNotBlank(newBean.getPreTraderId())&&rcsCategorySetTraderWeight.getTraderId().equals(Long.valueOf(newBean.getPreTraderId()))){
                        duplicateFlagMap.put("1_"+rcsCategorySetTraderWeight.getTypeId(),1);
                    }
                    if(1==rcsCategorySetTraderWeight.getMarketType()&&!StringUtils.isBlank(newBean.getPreTraderId())&&rcsCategorySetTraderWeight.getTraderId().equals(Long.valueOf(oldBean.getPreTraderId()))){
                        rcsCategorySetTraderWeight.setTraderId(StringUtils.isBlank(newBean.getPreTraderId())?null:Long.valueOf(newBean.getPreTraderId()));
                        rcsCategorySetTraderWeight.setTraderCode(newBean.getPreTrader());
                        updateList2.add(rcsCategorySetTraderWeight);
                    }
                }
            }
            //设置操盘人数
            Update update = new Update();
            if(CollectionUtils.isEmpty(collect.get("0"))&&StringUtils.isNotBlank(newBean.getLiveTraderId())&&StringUtils.isNotBlank(newBean.getLiveTrader())){
                update.set("traderNum", 1);
            }
            update.set("preTraderName", newBean.getPreTrader());
            update.set("preTraderId", newBean.getPreTraderId());
            update.set("liveTraderName", newBean.getLiveTrader());
            update.set("liveTraderId", newBean.getLiveTraderId());
            mongotemplate.updateFirst(new Query().addCriteria(Criteria.where("matchId").is(matchInfoId)), update, MatchMarketLiveBean.class);
            //设置新增权重
            rcsCatregorySetTraderWeightService.batchInsertOrUpdate(addList);
            //设置新增变更操盘手权重
            setMasterWeight100(duplicateFlagMap,updateList2,newBean);
            for (RcsCategorySetTraderWeight rcsCategorySetTraderWeight : updateList2) {
                rcsCatregorySetTraderWeightService.updateById(rcsCategorySetTraderWeight);
            }
        } catch (Exception e) {
            log.error("::{}::{}{}","RDMADSG_"+linkId,e.getMessage(),e);
        }
    }

    /**
     * 如果副操盘手与主操盘手相同 清理原来设置 设定主操盘手100%
     * @param updateList2
     * @param newBean
     * @param duplicateFlagMap  key 盘口类型_玩法集id
     */
    private void setMasterWeight100(Map<String, Integer>  duplicateFlagMap, List<RcsCategorySetTraderWeight> updateList2, RcsStandardSportMarketSell newBean) {
        if(CollectionUtils.isEmpty(duplicateFlagMap)){return;}
        for (String key : duplicateFlagMap.keySet()) {
            String[] split = key.split("_");
            QueryWrapper<RcsCategorySetTraderWeight> rcsCategorySetTraderWeightRemoveWrapper = new QueryWrapper<>();
            rcsCategorySetTraderWeightRemoveWrapper.lambda().eq(RcsCategorySetTraderWeight::getMatchId,newBean.getMatchInfoId());
            rcsCategorySetTraderWeightRemoveWrapper.lambda().eq(RcsCategorySetTraderWeight::getMarketType,Integer.valueOf(split[0]));
            rcsCategorySetTraderWeightRemoveWrapper.lambda().eq(RcsCategorySetTraderWeight::getTypeId,Long.valueOf(split[1]));
            rcsCatregorySetTraderWeightService.remove(rcsCategorySetTraderWeightRemoveWrapper);
        }

        if(CollectionUtils.isEmpty(updateList2)){return;}
        ArrayList<RcsCategorySetTraderWeight> objects1 = new ArrayList<>();
        ArrayList<RcsCategorySetTraderWeight> objects2 = new ArrayList<>();

        for (RcsCategorySetTraderWeight rcsCategorySetTraderWeight : updateList2) {
            for (String key : duplicateFlagMap.keySet()) {
                String[] split = key.split("_");
                if(split[0].equals(String.valueOf(rcsCategorySetTraderWeight.getMarketType()))&&split[1].equals(String.valueOf(rcsCategorySetTraderWeight.getTypeId()))){
                    String tid = 1 == rcsCategorySetTraderWeight.getMarketType().intValue() ? newBean.getPreTraderId(): newBean.getLiveTraderId() ;
                    String tname = 1 == rcsCategorySetTraderWeight.getMarketType().intValue() ? newBean.getPreTrader(): newBean.getLiveTrader() ;
                    Long traderId=StringUtils.isBlank(tid)?null:Long.valueOf(tid);
                    String traderCode=tname;
                    rcsCategorySetTraderWeight.setTraderId(traderId);
                    rcsCategorySetTraderWeight.setTraderCode(traderCode);
                    rcsCategorySetTraderWeight.setWeight(100);
                    objects1.add(rcsCategorySetTraderWeight);
                }else{
                    objects2.add(rcsCategorySetTraderWeight);
                }
            }
        }
        rcsCatregorySetTraderWeightService.batchInsertOrUpdate(objects1);
        updateList2.clear();
        updateList2.addAll(objects2);
    }


    private void buildTemplateDataSource(RcsStandardSportMarketSell data, String linkId){
        RcsTournamentTemplate tem = new RcsTournamentTemplate();
        tem.setType(3);        tem.setTypeVal(data.getMatchInfoId());
        tem.setMatchType(0);
        tem.setSportId(data.getSportId().intValue());
        RcsTournamentTemplate template = rcsTournamentTemplateMapper.selectTemplate(tem);
        log.info("::{}::接拒单数据源切换-滚球赛事模板", "RDMADSG_"+linkId+data.getMatchInfoId());
        if (!ObjectUtils.isEmpty(template) && StringUtils.isNotEmpty(data.getBusinessEvent())) {
            RcsTournamentTemplateAcceptConfigVo vo = new RcsTournamentTemplateAcceptConfigVo();
            vo.setTemplateId(template.getId());
            List<RcsTournamentTemplateAcceptConfigVo> list = configVoMapper.selectAcceptConfig(vo);
            log.info("::{}::接拒单数据源切换-旧接拒单数据源", "RDMADSG_"+linkId+data.getMatchInfoId());
            if (!CollectionUtils.isEmpty(list)) {
                for (RcsTournamentTemplateAcceptConfigVo config : list) {
                    config.setDataSource(data.getBusinessEvent());
                    log.info("::{}::接拒单数据源切换-新接拒单数据源配置","RDMADSG_"+linkId+data.getMatchInfoId());
                    configMapper.updateMatchDataSourceAndTimeConfig(config);
                }
            }
            QueryWrapper<RcsTournamentTemplateAcceptConfigSettle> configSettleQueryWrapper = new QueryWrapper();
            configSettleQueryWrapper.lambda().eq(RcsTournamentTemplateAcceptConfigSettle::getTemplateId, template.getId());
            List<RcsTournamentTemplateAcceptConfigSettle> settleList = configSettleMapper.selectList(configSettleQueryWrapper);
            if (!CollectionUtils.isEmpty(list)) {
                for (RcsTournamentTemplateAcceptConfigSettle config : settleList) {
                    config.setDataSource(data.getBusinessEvent());
                    log.info("::{}::接拒单结算数据源切换-新接拒单结算数据源配置","RDMADSG_"+linkId+data.getMatchInfoId());
                    configSettleMapper.updateMatchDataSourceAndTimeConfigSettle(config);
                }
            }

        }
    }

    /**
     * 权重ve据转成
     * @param linkId
     * @param dto
     */
    @Transactional(rollbackFor = Exception.class)
    void oldDataToNewData(String linkId, RcsStandardSportMarketSell dto) {
        QueryWrapper<RcsCategorySetTraderWeight> weightQueryWrapper = new QueryWrapper<>();
        weightQueryWrapper.lambda().eq(RcsCategorySetTraderWeight::getMatchId,dto.getMatchInfoId());
        weightQueryWrapper.lambda().eq(RcsCategorySetTraderWeight::getVersion,0);
        List<RcsCategorySetTraderWeight> rcsCategorySetTraderWeights = rcsCatregorySetTraderWeightService.list(weightQueryWrapper);
        log.info("::{}::,oldDataToNewData1","RDMADSG_"+linkId+"_"+dto.getMatchInfoId());
        if(CollectionUtils.isEmpty(rcsCategorySetTraderWeights)){
            return;
        }
        Map<String, List<RcsCategorySetTraderWeight>> sets = rcsCategorySetTraderWeights.stream().collect(Collectors.groupingBy(e -> e.getMarketType()+"_"+e.getTypeId()));
        Map<String, List<RcsCategorySetTraderWeight>> marketTypes = rcsCategorySetTraderWeights.stream().collect(Collectors.groupingBy(e->String.valueOf(e.getMarketType())));
        ArrayList<RcsCategorySetTraderWeight> list = new ArrayList<>();
        for (String mt : marketTypes.keySet()) {
            List<RcsCategorySetTraderWeight> rcsCategorySetTraderWeights1 = marketTypes.get(mt);
            if(CollectionUtils.isEmpty(rcsCategorySetTraderWeights1)){
                continue;
            }
            List<Long> setIds = rcsCategorySetTraderWeights1.stream().map(e -> e.getTypeId()).collect(Collectors.toList());
            List<FindMarketCategoryListAndNamesBO> marketCategoryListAndNames = marketCategorySetService.findMarketCategoryListAndNames(dto.getSportId(),setIds);
            log.info("::{}::,{},oldDataToNewData2","RDMADSG_"+linkId+"_"+dto.getMatchInfoId(),mt);
            if(CollectionUtils.isEmpty(marketCategoryListAndNames)){
                continue;
            }
            for (FindMarketCategoryListAndNamesBO marketCategoryListAndName : marketCategoryListAndNames) {
                RcsCategorySetTraderWeight rcsCategorySetTraderWeight = new RcsCategorySetTraderWeight();
                rcsCategorySetTraderWeight.setSetNo(marketCategoryListAndName.getMarketCategorySetId());
                rcsCategorySetTraderWeight.setMatchId(dto.getMatchInfoId());
                List<RcsCategorySetTraderWeight> setWeight = sets.get(mt+"_"+marketCategoryListAndName.getMarketCategorySetId());
                rcsCategorySetTraderWeight.setTraderId(setWeight.get(0).getTraderId());
                rcsCategorySetTraderWeight.setWeight(setWeight.get(0).getWeight());
                rcsCategorySetTraderWeight.setTraderCode(setWeight.get(0).getTraderCode());
                rcsCategorySetTraderWeight.setMarketType(Integer.valueOf(mt));
                rcsCategorySetTraderWeight.setSportId(dto.getSportId());
                rcsCategorySetTraderWeight.setVersion(1);
                rcsCategorySetTraderWeight.setTypeId(marketCategoryListAndName.getId());
                list.add(rcsCategorySetTraderWeight);
            }
        }
        rcsCatregorySetTraderWeightService.remove(weightQueryWrapper);
        rcsCatregorySetTraderWeightService.batchInsertOrUpdate(list);
    }
}