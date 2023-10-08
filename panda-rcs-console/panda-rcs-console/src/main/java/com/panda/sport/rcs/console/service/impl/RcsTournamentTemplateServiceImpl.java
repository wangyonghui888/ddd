package com.panda.sport.rcs.console.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.console.dao.RcsTournamentTemplateMapper;
import com.panda.sport.rcs.console.dto.*;
import com.panda.sport.rcs.console.pojo.RcsTournamentTemplatePlayMargain;
import com.panda.sport.rcs.console.pojo.TournamentTemplateExcelVO;
import com.panda.sport.rcs.console.service.RcsTournamentTemplateService;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Service
@Slf4j
public class RcsTournamentTemplateServiceImpl implements RcsTournamentTemplateService {

    @Resource
    private RcsTournamentTemplateMapper templateMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSpecialPumping(RcsSpecialPumpingDTO dto) {
        try {
            List<RcsTournamentTemplatePlayMargain> resVo = templateMapper.selectTournamentSpecialOddsIntervalInitData(dto.getPlayId());
            for (RcsTournamentTemplatePlayMargain margain : resVo) {
                String preStr = "{\"1.01-1.25\":%s,\"1.26-1.39\":%s,\"1.40-1.59\":%s,\"1.60-1.79\":%s,\"1.80-1.85\":%s,\"1.86-2.00\":%s}";
                String liveStr = "{\"1.01-1.05\":%s,\"1.06-1.25\":%s,\"1.26-1.39\":%s,\"1.40-1.60\":%s,\"1.61-1.85\":%s,\"1.86-1.88\":%s,\"1.89-2.00\":%s}";
                //下方判断中的数据都是以产品给的计算规则计算出来的，根据的是分时节点中的数据（早盘取开售节点的数据；滚球取开赛节点的数据）
                if (margain.getMatchType().equals(1)) {
                    margain.setSpecialOddsIntervalHigh(String.format(preStr, margain.getOrderSinglePayVal().multiply(new BigDecimal(1)), margain.getOrderSinglePayVal().multiply(new BigDecimal(1)), margain.getOrderSinglePayVal().multiply(new BigDecimal(1)), margain.getOrderSinglePayVal().multiply(new BigDecimal(1)), margain.getOrderSinglePayVal().multiply(new BigDecimal(1)), margain.getOrderSinglePayVal().multiply(new BigDecimal(1))));
                    margain.setSpecialOddsIntervalLow(String.format(preStr, margain.getOrderSinglePayVal().multiply(new BigDecimal("0.5")), margain.getOrderSinglePayVal().multiply(new BigDecimal("0.6")), margain.getOrderSinglePayVal().multiply(new BigDecimal("0.7")), margain.getOrderSinglePayVal().multiply(new BigDecimal("0.8")), margain.getOrderSinglePayVal().multiply(new BigDecimal("0.9")), margain.getOrderSinglePayVal().multiply(new BigDecimal(1))));
                    margain.setSpecialOddsIntervalStatus(String.format(preStr, 0, 0, 0, 0, 0, 0));
                    margain.setSpecialBettingIntervalHigh(String.format(preStr, margain.getOrderSinglePayVal().multiply(new BigDecimal("0.25")), margain.getOrderSinglePayVal().multiply(new BigDecimal("0.25")), margain.getOrderSinglePayVal().multiply(new BigDecimal("0.25")), margain.getOrderSinglePayVal().multiply(new BigDecimal("0.25")), margain.getOrderSinglePayVal().multiply(new BigDecimal("0.25")), margain.getOrderSinglePayVal().multiply(new BigDecimal("0.25"))));
                    margain.setSpecialOddsInterval(dto.getPreStr());
                } else {
                    margain.setSpecialOddsIntervalHigh(String.format(liveStr, margain.getOrderSinglePayVal().multiply(new BigDecimal(1)), margain.getOrderSinglePayVal().multiply(new BigDecimal(1)), margain.getOrderSinglePayVal().multiply(new BigDecimal(1)), margain.getOrderSinglePayVal().multiply(new BigDecimal(1)), margain.getOrderSinglePayVal().multiply(new BigDecimal(1)), margain.getOrderSinglePayVal().multiply(new BigDecimal(1)), margain.getOrderSinglePayVal().multiply(new BigDecimal(1))));
                    margain.setSpecialOddsIntervalLow(String.format(liveStr, margain.getOrderSinglePayVal().multiply(new BigDecimal("0.5")), margain.getOrderSinglePayVal().multiply(new BigDecimal("0.5")), margain.getOrderSinglePayVal().multiply(new BigDecimal("0.6")), margain.getOrderSinglePayVal().multiply(new BigDecimal("0.7")), margain.getOrderSinglePayVal().multiply(new BigDecimal("0.8")), margain.getOrderSinglePayVal().multiply(new BigDecimal("0.9")), margain.getOrderSinglePayVal().multiply(new BigDecimal(1))));
                    margain.setSpecialOddsIntervalStatus(String.format(liveStr, 0, 0, 0, 0, 0, 0, 0));
                    margain.setSpecialBettingIntervalHigh(String.format(liveStr, margain.getOrderSinglePayVal().multiply(new BigDecimal("0.25")), margain.getOrderSinglePayVal().multiply(new BigDecimal("0.25")), margain.getOrderSinglePayVal().multiply(new BigDecimal("0.25")), margain.getOrderSinglePayVal().multiply(new BigDecimal("0.25")), margain.getOrderSinglePayVal().multiply(new BigDecimal("0.25")), margain.getOrderSinglePayVal().multiply(new BigDecimal("0.25")), margain.getOrderSinglePayVal().multiply(new BigDecimal("0.25"))));
                    margain.setSpecialOddsInterval(dto.getLiveStr());
                }
                //默认关闭
                margain.setIsSpecialPumping(0);
            }
            templateMapper.batchUpdateSpecialOddsInterval(resVo);
        } catch (Exception e) {
            log.error("初始化对应玩法的特殊限额数据入库异常,回滚数据",e);
        }
    }

    @Override
    public void addTournamentTemplatePlay(TournamentTemplateUpdateParam param) {
        RcsTournamentTemplate rcsTournamentTemplate = BeanCopyUtils.copyProperties(param, RcsTournamentTemplate.class);
        List<RcsTournamentTemplate> list = templateMapper.selectListByInfo(rcsTournamentTemplate);
        log.info("本次需要增加玩法的模板：{}", JsonFormatUtils.toJson(list));
        if (CollectionUtils.isEmpty(list)) {
            throw new IllegalArgumentException("未找到联赛模板数据！");
        }
        for (RcsTournamentTemplate template : list) {
            for (TournamentTemplatePlayMargainParam playMarginParam : param.getPlayMargainList()) {
                playMarginParam.setTemplateId(template.getId());
                TournamentTemplatePlayMargainParam playMargin = templateMapper.selectPlayMarginByInfo(playMarginParam);
                if (ObjectUtils.isEmpty(playMargin)) {
                    playMarginParam.setId(null);
                    playMarginParam.setTemplateId(template.getId());
                    playMarginParam.setMatchType(template.getMatchType());
                    templateMapper.batchInsertMarginPlay(playMarginParam);
                    log.info("新增玩法-玩法数据为:{}", JSONObject.toJSONString(playMarginParam));
                    Long newMarginId = null;
                    if (!ObjectUtils.isEmpty(playMarginParam)) {
                        newMarginId = playMarginParam.getId();
                    }
                    if(newMarginId==null){
                        throw new IllegalArgumentException("新增玩法出错,newMarginId为空");
                    }
                    //封装ref表数据(滚球和早盘的第一个节点timeVal值不一样)
                    Long timeVal = template.getMatchType() == 1 ? 2592000L : 0L;
                    RcsTournamentTemplatePlayMargainRef playMarginRef = templateMapper.selectPlayMarginRefByInfo(newMarginId, timeVal);
                    if (ObjectUtils.isEmpty(playMarginRef) && !CollectionUtils.isEmpty(playMarginParam.getPlayMargainRefParamList())) {
                        TournamentTemplatePlayMargainRefParam playMarginRefParam = playMarginParam.getPlayMargainRefParamList().get(0);
                        playMarginRefParam.setId(null);
                        playMarginRefParam.setMargainId(newMarginId);
                        playMarginRefParam.setTimeVal(timeVal);
                        playMarginRefParam.setStatus(1);
                        log.info("新增玩法-分时节点数据为:{}", JSONObject.toJSONString(playMarginRefParam));
                        templateMapper.batchInsertMarginRefPlay(playMarginRefParam);
                    }
                }else{
                    log.error("模板id={},已有{}玩法", template.getId(), playMarginParam.getPlayId());
                    throw new IllegalArgumentException("模板id="+template.getId()+",已有"+playMarginParam.getPlayId()+"玩法");
                }
            }
        }
    }

    @Override
    public void initTournamentTemplate(Integer sportId) {
        //滚球和早盘
        Integer[] matchTypes = {0, 1};
        for (Long i = 0L; i <= 20; i++) {
            for (Integer matchType : matchTypes) {
                TournamentTemplateParam param = new TournamentTemplateParam();
                param.setSportId(sportId);
                param.setMatchType(matchType);
                param.setType(1);
                param.setTypeVal(i);
                RcsTournamentTemplate rcsTournamentTemplate = BeanCopyUtils.copyProperties(param, RcsTournamentTemplate.class);
                List<RcsTournamentTemplate> list = templateMapper.selectListByInfo(rcsTournamentTemplate);
                if (list.size() == 0) {
                    initTournamentTemplate(param);
                }
            }
        }
    }

    /**
     * 初始化联赛模板
     *
     * @param param
     */
    @Transactional(rollbackFor = Exception.class)
    public Long initTournamentTemplate(TournamentTemplateParam param) {
        // 初始化模板基表
        RcsTournamentTemplate rcsTournamentTemplate = new RcsTournamentTemplate();
        rcsTournamentTemplate.setSportId(param.getSportId());
        rcsTournamentTemplate.setType(param.getType());
        rcsTournamentTemplate.setTypeVal(param.getTypeVal());
        rcsTournamentTemplate.setMatchType(param.getMatchType());
        rcsTournamentTemplate.setDataSourceCode("{\"SR\":1,\"BC\":2,\"BG\":3,\"TX\":4,\"RB\":5,\"AO\":6,\"PI\":7,\"PD\":8,\"LS\":9}");
        rcsTournamentTemplate.setBusinesMatchPayVal(1000000L);
        rcsTournamentTemplate.setUserMatchPayVal(200000L);
        rcsTournamentTemplate.setTemplateName(param.getTemplateName());
        rcsTournamentTemplate.setOddsChangeStatus(1);
        rcsTournamentTemplate.setIfWarnSuspended(1);
        rcsTournamentTemplate.setAoConfigValue("{\"perId\":45,\"oneInjTime\":2,\"twoInjTime\":4,\"htDrawAdj\":5,\"ftDrawAdj\":10,\"refresh\":30,\"zeroOneFive\":0.295,\"oneFiveThree\":0.325,\"threeHt\":0.38,\"htSix\":0.3,\"sixSevenFive\":0.305,\"sevenFiveFt\":0.395}");
        rcsTournamentTemplate.setMtsConfigValue("{\"mtsSwitch\":0,\"contactPercentage\":4}");
        rcsTournamentTemplate.setUserPendingOrderPayVal(200000L);
        rcsTournamentTemplate.setBusinesPendingOrderPayVal(1000000L);
        rcsTournamentTemplate.setPendingOrderRate(100);
        rcsTournamentTemplate.setUserPendingOrderCount(10);
        rcsTournamentTemplate.setPendingOrderStatus(0);
        templateMapper.batchInsertTemplate(rcsTournamentTemplate);
        log.info("初始化联赛模板-模板数据为:{}", JSONObject.toJSONString(rcsTournamentTemplate));
        // 获取玩法参数和margin盘口参数默认值
        RcsTournamentPlayMarginTemplate rcsTournamentPlayMarginTemplate = new RcsTournamentPlayMarginTemplate();
        rcsTournamentPlayMarginTemplate.setMatchType(param.getMatchType());
        rcsTournamentPlayMarginTemplate.setSportId(param.getSportId());
        rcsTournamentPlayMarginTemplate.setLevel(param.getTypeVal());
        List<RcsTournamentPlayMarginTemplate> rcsTournamentPlayMarginTemplates = templateMapper.queryPlayTemplateInitData(rcsTournamentPlayMarginTemplate);
        if (!CollectionUtils.isEmpty(rcsTournamentPlayMarginTemplates)) {
            // 构建玩法参数数据
            for (RcsTournamentPlayMarginTemplate margainTemplate : rcsTournamentPlayMarginTemplates) {
                com.panda.sport.rcs.console.dto.RcsTournamentTemplatePlayMargain margain = new com.panda.sport.rcs.console.dto.RcsTournamentTemplatePlayMargain();
                BeanCopyUtils.copyProperties(margainTemplate, margain);
                margain.setTemplateId(rcsTournamentTemplate.getId());
                margain.setMatchType(param.getMatchType());
                margain.setPlayId(margainTemplate.getPlayId());
                margain.setIfWarnSuspended(1);
                //初始化默认数据
                margain.setOddsChangeStatus(1);
                margain.setOddsChangeValue(new BigDecimal(4));
                //篮球模板玩法里最大盘口数移入分时节点，所以将模板里玩法最大盘口数设置为空
                if (param.getSportId() == 2) {
                    margain.setMarketCount(null);
                    margain.setViceMarketRatio(null);
                }
                TournamentTemplatePlayMargainParam margainParam = BeanCopyUtils.copyProperties(margain, TournamentTemplatePlayMargainParam.class);
                templateMapper.batchInsertMarginPlay(margainParam);
                log.info("初始化联赛模板-玩法数据为:{}", JSONObject.toJSONString(margainParam));
                RcsTournamentTemplatePlayMargainRef margainRef = new RcsTournamentTemplatePlayMargainRef();
                BeanCopyUtils.copyProperties(margainTemplate, margainRef);
                if (param.getMatchType() == 1) {
                    //早盘初始化第一个时间节点为30天（30*60*60*24）
                    margainRef.setTimeVal(2592000L);
                } else if (param.getMatchType().equals(0)) {
                    //滚球初始化第一个时间节点为0
                    if (param.getSportId().equals(7) || param.getSportId().equals(4)) {
                        margainRef.setTimeVal(1L);
                    } else {
                        margainRef.setTimeVal(0L);
                    }
                }
                //篮球模板玩法里最大盘口数移入分时节点，其他球种保持原样，所以设置分时节点数据为空
                if (param.getSportId() != 2) {
                    margainRef.setMarketCount(null);
                    margainRef.setViceMarketRatio(null);
                }
                margainRef.setStatus(1);
                margainRef.setMargainId(margainParam.getId());
                templateMapper.batchInsertMarginRefPlay(margainRef);
                log.info("初始化联赛模板-分时节点数据为:{}", JSONObject.toJSONString(margainRef));
            }
        }
        // 滚球数据单独处理
        if (param.getMatchType().equals(0)) {
            // 初始化事件结算/审核时间
            List<RcsTournamentTemplateEvent> templateEventList = new ArrayList<>();
            List<RcsTournamentEventTemplate> list = templateMapper.selectEventListBySportId(param.getSportId());
            for (RcsTournamentEventTemplate templateEvent : list) {
                RcsTournamentTemplateEvent event = new RcsTournamentTemplateEvent();
                event.setTemplateId(rcsTournamentTemplate.getId());
                event.setEventCode(templateEvent.getEventCode());
                event.setEventDesc(templateEvent.getTemplateText());
                event.setEventHandleTime(templateEvent.getAuditTime());
                event.setSettleHandleTime(templateEvent.getBillTime());
                event.setSortNo(templateEvent.getOrderNo());
                templateEventList.add(event);
            }
            if (templateEventList.size() > 0) {
                templateMapper.batchInsertEvent(templateEventList);
            }
            // 初始化滚球接拒单参数数据
            /*QueryWrapper<RcsMarketCategorySet> rcsMarketCategorySetWrapper = new QueryWrapper<>();
            rcsMarketCategorySetWrapper.lambda().eq(RcsMarketCategorySet::getSportId, param.getSportId())
                    .eq(RcsMarketCategorySet::getType, 1)
                    .eq(RcsMarketCategorySet::getStatus, 2);
            List<RcsMarketCategorySet> categorySetIds = marketCategorySetService.list(rcsMarketCategorySetWrapper);
            if (!CollectionUtils.isEmpty(categorySetIds)) {
                for (RcsMarketCategorySet set : categorySetIds) {
                    RcsTournamentTemplateAcceptConfig config = new RcsTournamentTemplateAcceptConfig();
                    config.setTemplateId(rcsTournamentTemplate.getId());
                    config.setCategorySetId(Integer.valueOf(set.getId() + ""));
                    config.setDataSource("SR");
                    config.setNormal(3);
                    config.setMinWait(10);
                    config.setMaxWait(120);
                    config.setCreateTime(new Date());
                    config.setUpdateTime(new Date());
                    rcsMatchEventTypeInfoService.insertEventList(config);
                }
            }
            //初始化其他玩法集id -1
            RcsTournamentTemplateAcceptConfig config = new RcsTournamentTemplateAcceptConfig();
            config.setTemplateId(rcsTournamentTemplate.getId());
            config.setCategorySetId(-1);
            config.setDataSource("SR");
            config.setNormal(3);
            config.setMinWait(10);
            config.setMaxWait(120);
            config.setCreateTime(new Date());
            config.setUpdateTime(new Date());
            rcsMatchEventTypeInfoService.insertEventList(config);*/
        }
        return rcsTournamentTemplate.getId();
    }

    @Override
    @Async("asyncServiceExecutor")
    public void importTemplate(List<TournamentTemplateExcelVO> collect, CountDownLatch countDownLatch) {
        try {
            if(!CollectionUtils.isEmpty(collect)){
                templateMapper.batchImportTemplate(collect);
            }
        } finally {
            countDownLatch.countDown();
        }
    }
}
