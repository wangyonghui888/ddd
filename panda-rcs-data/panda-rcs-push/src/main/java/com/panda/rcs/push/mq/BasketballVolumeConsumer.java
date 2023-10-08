package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.push.cache.MatchInfoCache;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.constant.BaseConstant;
import com.panda.rcs.push.entity.enums.MatchStatusEnums;
import com.panda.rcs.push.entity.enums.PlayIdsEnum;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.entity.vo.MatchInfo;
import com.panda.rcs.push.utils.ClientResponseUtils;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.pojo.dto.RcsPredictBetStatisDTO;
import com.panda.sport.rcs.pojo.vo.ActualVolumeVO;
import com.panda.sport.rcs.utils.ListUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @Description: 篮球货量推送消费
 * Topic=RCS_PREDICT_BET_STATIS_SAVE_WS
 * Group=RCS_PUSH_PREDICT_BET_STATIS_SAVE_WS_GROUP
 * 对应指令-> 30042
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "RCS_PREDICT_BET_STATIS_SAVE_WS",
        consumerGroup = "RCS_PUSH_PREDICT_BET_STATIS_SAVE_WS_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class BasketballVolumeConsumer implements RocketMQListener<RcsPredictBetStatisDTO>, RocketMQPushConsumerLifecycleListener {

    private final static SubscriptionEnums subscriptionEnums = SubscriptionEnums.NO_FOOTBALL_AMOUNT;

    private static HashMap<Integer, Integer> playHashMap = new HashMap<>();

    @Autowired
    private ClientManageService clientManageService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(96);
        defaultMQPushConsumer.setConsumeThreadMax(196);
    }

    @PostConstruct
    private void initPlayHashMap() {
        for (PlayIdsEnum playIdsEnum : PlayIdsEnum.values()) {
            playHashMap.put(playIdsEnum.getPlayId(), playIdsEnum.getPlayTimeStage());
        }
    }

    @Override
    public void onMessage(RcsPredictBetStatisDTO predictBetStatis) {
        if(predictBetStatis == null){
            return;
        }
        log.info("::{}::,::{}::,::{}::消费数据:{}", predictBetStatis.getGlobalId(), predictBetStatis.getMatchId(), predictBetStatis.getPlayId(), JSONObject.toJSON(predictBetStatis));
        try {
            if(!BaseConstant.AMOUNT_SPORT_LISTS.contains(predictBetStatis.getSportId())){
                return;
            }

            MatchInfo matchInfo = MatchInfoCache.matchInfoMap.get(Long.toString(predictBetStatis.getMatchId()));
            Integer matchType = predictBetStatis.getMatchType() == 2 ? 1 : 0;
            if(matchInfo != null && matchInfo.getMatchStatus().equals(MatchStatusEnums.MATCH_STATUS_LIVE.getKey()) && !matchInfo.getMatchStatus().equals(matchType)){
                log.info("::{}::,::{}::,::{}::赛事状态不对应,不推送", predictBetStatis.getGlobalId(), predictBetStatis.getMatchId(), predictBetStatis.getPlayId());
                return;
            }

            HashMap<String, Object> list = getList(predictBetStatis);
            list.put("playTimeStage", playHashMap.get(predictBetStatis.getPlayId()));
            list.put("matchType", predictBetStatis.getMatchType());
            String msgid = UUID.randomUUID().toString();
            //推送数据
            clientManageService.sendMessage(subscriptionEnums, Long.toString(predictBetStatis.getMatchId()), predictBetStatis.getMatchType(), predictBetStatis.getSportId(), playHashMap.get(predictBetStatis.getPlayId()), ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), list, 0, predictBetStatis.getGlobalId(), msgid, null));
        } catch (Exception e){
            log.error("::{}::,::{}::,::{}::篮球货量消费异常消费数据:{}异常信息：", predictBetStatis.getGlobalId(), predictBetStatis.getMatchId(), predictBetStatis.getPlayId(), predictBetStatis, e);
        }
    }


    private HashMap<String, Object> getList(RcsPredictBetStatisDTO msg) {
        HashMap<String, Object> hashMap = new HashMap<>();
        List<ActualVolumeVO> rcsPredictBetStatisVoList =msg.getActualVolumeVOList();
        BigDecimal betAmountTotal = BigDecimal.ZERO;
        BigDecimal betAmountPayTotal = BigDecimal.ZERO;
        BigDecimal betAmountComplexTotal = BigDecimal.ZERO;
        if (rcsPredictBetStatisVoList!=null &&rcsPredictBetStatisVoList.size()>0) {
            hashMap.put("matchLength",rcsPredictBetStatisVoList.get(0).getMatchLength());
            hashMap.put("matchStatus",rcsPredictBetStatisVoList.get(0).getMatchStatus());
            //当前盘口的总投注笔数
            Long betNumSum = 0L;
            for (ActualVolumeVO actualVolumeVO : rcsPredictBetStatisVoList) {
                betNumSum = actualVolumeVO.getBetNum() + betNumSum;
                if (actualVolumeVO.getOddsItem().equals("Over") || actualVolumeVO.getOddsItem().equals("1")) {
                    betAmountTotal = betAmountTotal.add(actualVolumeVO.getBetAmount());
                    betAmountPayTotal = betAmountPayTotal.add(actualVolumeVO.getBetAmountPay());
                    betAmountComplexTotal = betAmountComplexTotal.add(actualVolumeVO.getBetAmountComplex());
                    actualVolumeVO.setOddsItem("1");
                } else if (actualVolumeVO.getOddsItem().equals("Under") || actualVolumeVO.getOddsItem().equals("2")) {
                    actualVolumeVO.setOddsItem("2");
                    betAmountTotal = betAmountTotal.subtract(actualVolumeVO.getBetAmount());
                    betAmountPayTotal = betAmountPayTotal.subtract(actualVolumeVO.getBetAmountPay());
                    betAmountComplexTotal = betAmountComplexTotal.subtract(actualVolumeVO.getBetAmountComplex());
                }
            }

            //如果当前盘口总投注笔数等于1的话，就表示为新开盘口，则告诉前端重新拉去数据
            if (betNumSum == 1L) {
                hashMap.put("pullAgain", 1);
            }

            if (rcsPredictBetStatisVoList.size() == 1) {
                ActualVolumeVO actualVolumeVO = rcsPredictBetStatisVoList.get(0);
                ActualVolumeVO copyActualVolumeVO = new ActualVolumeVO();
                BeanCopyUtils.copyProperties(actualVolumeVO, copyActualVolumeVO);
                try {
                    copyActualVolumeVO.setOddsItem(String.valueOf(3 - Integer.parseInt(actualVolumeVO.getOddsItem())));
                } catch (NumberFormatException e) {
                    copyActualVolumeVO.setOddsItem(actualVolumeVO.getOddsItem());
                }
                copyActualVolumeVO.setBetNum(0L);
                copyActualVolumeVO.setBetAmount(BigDecimal.ZERO);
                copyActualVolumeVO.setBetAmountPay(BigDecimal.ZERO);
                copyActualVolumeVO.setBetAmountComplex(BigDecimal.ZERO);
                rcsPredictBetStatisVoList.add(copyActualVolumeVO);
            }
            for (ActualVolumeVO actualVolumeVO : rcsPredictBetStatisVoList) {
                if (actualVolumeVO.getOddsItem().equals("1") && betAmountTotal.doubleValue() > 0) {
                    actualVolumeVO.setMarketBetAmount(betAmountTotal);
                } else if (actualVolumeVO.getOddsItem().equals("2") && betAmountTotal.doubleValue() < 0) {
                    actualVolumeVO.setMarketBetAmount(betAmountTotal.abs());
                }
                if (actualVolumeVO.getOddsItem().equals("1") && betAmountPayTotal.doubleValue() > 0) {
                    actualVolumeVO.setMarketBetAmountPay(betAmountPayTotal);
                } else if (actualVolumeVO.getOddsItem().equals("2") && betAmountPayTotal.doubleValue() < 0) {
                    actualVolumeVO.setMarketBetAmountPay(betAmountPayTotal.abs());
                }
                if (actualVolumeVO.getOddsItem().equals("1") && betAmountComplexTotal.doubleValue() > 0) {
                    actualVolumeVO.setMarketBetAmountComplex(betAmountComplexTotal);
                } else if (actualVolumeVO.getOddsItem().equals("2") && betAmountComplexTotal.doubleValue() < 0) {
                    actualVolumeVO.setMarketBetAmountComplex(betAmountComplexTotal.abs());
                }
            }
        } else {
            hashMap.put("pullAgain", 1);
            hashMap.put("matchLength",null);
            hashMap.put("matchStatus",null);
        }
        if (rcsPredictBetStatisVoList!=null &&rcsPredictBetStatisVoList.size()>0){
            ActualVolumeVO actualVolumeVO = rcsPredictBetStatisVoList.get(0);
            //如果是乒乓球第X局总分玩法则进行降序排序
            if(actualVolumeVO.getPlayId().equals(177)){
                ListUtils.sort(rcsPredictBetStatisVoList, false, "sort");
            }
        }
        hashMap.put("betStatisVoList", rcsPredictBetStatisVoList);
        hashMap.put("playId", msg.getPlayId());
        hashMap.put("subPlayId", msg.getSubPlayId());
        hashMap.put("marketId", msg.getMarketId());
        return hashMap;
    }
}
