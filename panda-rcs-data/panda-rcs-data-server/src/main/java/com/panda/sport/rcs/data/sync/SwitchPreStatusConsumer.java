package com.panda.sport.rcs.data.sync;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.mapper.MerchantsSinglePercentageMapper;
import com.panda.sport.rcs.data.mapper.RcsOperateMerchantsSetMapper;
import com.panda.sport.rcs.data.mq.RcsConsumer;
import com.panda.sport.rcs.data.service.IStandardMatchInfoService;
import com.panda.sport.rcs.data.service.MarketCategorySetService;
import com.panda.sport.rcs.data.service.RcsCategorySetTraderWeightService;
import com.panda.sport.rcs.data.utils.RDSProducerSendMessageUtils;
import com.panda.sport.rcs.data.utils.RcsDataRedis;
import com.panda.sport.rcs.data.utils.RealTimeControlUtils;
import com.panda.sport.rcs.pojo.MatchMarketLiveBean;
import com.panda.sport.rcs.pojo.MerchantsSinglePercentage;
import com.panda.sport.rcs.pojo.RcsCategorySetTraderWeight;
import com.panda.sport.rcs.pojo.RcsOperateMerchantsSet;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.StandardMatchSwitchStatusMessage;
import com.panda.sport.rcs.pojo.dto.ClearDTO;
import com.panda.sport.rcs.pojo.dto.ClearSubDTO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "STANDARD_MATCH_SWITCH_STATUS",
        consumerGroup = "RCS_DATA_STANDARD_MATCH_SWITCH_STATUS_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class SwitchPreStatusConsumer extends RcsConsumer<Request<StandardMatchSwitchStatusMessage>> {

    @Autowired
    IStandardMatchInfoService iStandardMatchInfoService;

    @Autowired
    protected RDSProducerSendMessageUtils sendMessage;

    @Autowired
    private RcsOperateMerchantsSetMapper rcsOperateMerchantsSetMapper;

    @Autowired
    private MerchantsSinglePercentageMapper merchantsSinglePercentageMapper;

    @Autowired
    private StandardTxThirdMarketOddsConsumer standardTxThirdMarketOddsConsumer;

    @Autowired
    private RcsCategorySetTraderWeightService rcsCatregorySetTraderWeightService;

    @Autowired
    MarketCategorySetService marketCategorySetService;

    @Autowired
    private MongoTemplate mongotemplate;


    @Autowired
    private RcsDataRedis redisClient;

    @Autowired
    private RealTimeControlUtils realTimeControlUtils;

    @Override
    protected String getTopic() {
        return "STANDARD_MATCH_SWITCH_STATUS";
    }

    @Override
    public Boolean handleMs(Request<StandardMatchSwitchStatusMessage> rRequests) {
        try {
            log.info("::{}::赛前状态切换","RDSMSSG_"+rRequests.getLinkId()+rRequests.getData().getStandardMatchId()+"_"+rRequests.getData().getOddsLive());
            StandardMatchSwitchStatusMessage data = rRequests.getData();
            Integer oddsLive = data.getOddsLive();
            StandardMatchInfo standardMatchInfo = iStandardMatchInfoService.getById(data.getStandardMatchId());
            if (null == standardMatchInfo) {
                log.warn("::{}::赛事为空:{}" ,"RDSMSSG_"+rRequests.getLinkId()+"_"+rRequests.getData().getStandardMatchId(), JsonFormatUtils.toJson(rRequests));
                return true;
            }
            log.info("::{}::赛前状态切换2","RDSMSSG_"+rRequests.getLinkId()+"_"+rRequests.getData().getStandardMatchId());
            if (oddsLive.intValue() == 1 || oddsLive.intValue() == 0) {
                StandardMatchInfo newStandardMatchInfo = new StandardMatchInfo();
                newStandardMatchInfo.setId(data.getStandardMatchId());
                newStandardMatchInfo.setOddsLive(oddsLive);
                if (oddsLive.intValue() == 1) {
                    newStandardMatchInfo.setMatchStatus(1);
                } else if (oddsLive.intValue() == 0) {
                    newStandardMatchInfo.setMatchStatus(0);
                }
                UpdateWrapper<StandardMatchInfo> standardMatchInfoUpdateWrapper = new UpdateWrapper<>();
                standardMatchInfoUpdateWrapper.lambda().eq(StandardMatchInfo::getId, data.getStandardMatchId());
                log.info("::{}::赛前状态切换3","RDSMSSG_"+rRequests.getLinkId()+"_"+rRequests.getData().getStandardMatchId());
                iStandardMatchInfoService.update(newStandardMatchInfo, standardMatchInfoUpdateWrapper);
                if (1 == oddsLive.intValue()) {
                    clear(data,rRequests.getLinkId(),standardMatchInfo);
                }
            }
            //清理赛事单场限额
            if(oddsLive.intValue() == 1){
                clear(standardMatchInfo,rRequests.getLinkId());
            }

            //早盘滚球切换清理百家赔数据
            standardTxThirdMarketOddsConsumer.clearMultiOddsDataByoddsLive(rRequests.getData().getStandardMatchId(),"RDSMSSG_"+rRequests.getLinkId());

            //设置滚球操盘人数
            if(oddsLive.intValue() == 1){
                setTradeNum(standardMatchInfo,rRequests.getLinkId());
            }
        } catch (Exception e) {
            log.error("::{}::{},{},{}","RDSMSSG_"+rRequests.getLinkId(),JsonFormatUtils.toJson(rRequests),e.getMessage(), e);
        }
        return true;
    }

    private void clear(StandardMatchInfo standardMatchInfo, String linkId) {
        MerchantsSinglePercentage entity = new MerchantsSinglePercentage();
        entity.setStatus(0);

        LambdaUpdateWrapper<MerchantsSinglePercentage> merchantsSingleWrapper = new LambdaUpdateWrapper();
        merchantsSingleWrapper.eq(MerchantsSinglePercentage::getMatchId, standardMatchInfo.getId());
        merchantsSingleWrapper.eq(MerchantsSinglePercentage::getMatchType, 1);
        merchantsSinglePercentageMapper.update(entity, merchantsSingleWrapper);
        log.info("::{}::更新赛事单场限额状态完成:{}","RDSMSSG_"+linkId+"_"+standardMatchInfo.getId(), standardMatchInfo.getId());

    }

    /**
     * 发送清理概率差
     * @param data
     * @param linkId
     * @param standardMatchInfo
     */
    private void clear(StandardMatchSwitchStatusMessage data, String linkId, StandardMatchInfo standardMatchInfo) {
        ClearDTO clearDTO = new ClearDTO();
        clearDTO.setType(1);
        clearDTO.setSportId(standardMatchInfo.getSportId());
        clearDTO.setClearType(4);
        clearDTO.setMatchId(standardMatchInfo.getId());
        clearDTO.setBeginTime(standardMatchInfo.getBeginTime());
        ArrayList<ClearSubDTO> objects = new ArrayList<>();
        ClearSubDTO clearSubDTO = new ClearSubDTO();
        clearSubDTO.setMatchId(standardMatchInfo.getId());
        objects.add(clearSubDTO);
        clearDTO.setList(objects);
        sendMessage.sendMessage("RCS_CLEAR_MATCH_MARKET_TAG", null, linkId, clearDTO, null);
    }


    /**
     * 设置操盘手数量
     * @param data
     * @param linkId
     */
    private void setTradeNum(StandardMatchInfo data, String linkId) {
        try {
            QueryWrapper<RcsCategorySetTraderWeight> rcsCategorySetTraderWeightWrapper = new QueryWrapper<>();
            rcsCategorySetTraderWeightWrapper.lambda().eq(RcsCategorySetTraderWeight::getMatchId, data.getId());
            rcsCategorySetTraderWeightWrapper.lambda().eq(RcsCategorySetTraderWeight::getMarketType, 0);
            List<RcsCategorySetTraderWeight> list = rcsCatregorySetTraderWeightService.list(rcsCategorySetTraderWeightWrapper);
            Map<Long, List<RcsCategorySetTraderWeight>> collect = list.stream().collect(Collectors.groupingBy(RcsCategorySetTraderWeight::getTraderId));
            //设置操盘人数
            Update update = new Update();
            update.set("traderNum", collect.size()<1?1:collect.size());
            mongotemplate.updateFirst(new Query().addCriteria(Criteria.where("matchId").is(data.getId())), update, MatchMarketLiveBean.class);
        } catch (Exception e) {
            log.error("::{}::{},{}","RDSMSSG_"+linkId+"_"+data.getId(),e.getMessage(), e);
        }
    }
}
