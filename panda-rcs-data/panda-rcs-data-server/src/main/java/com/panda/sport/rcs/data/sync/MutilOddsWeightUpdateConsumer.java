package com.panda.sport.rcs.data.sync;

import com.alibaba.nacos.common.util.UuidUtils;
import com.panda.sport.rcs.data.mq.RcsConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 百家赔入库转发
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "RCS_TOUR_MATCH_TEMPLATE_CONFIG_TOPIC",
        consumerGroup = "RCS_DATA_RCS_TOUR_MATCH_TEMPLATE_CONFIG_TOPIC_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MutilOddsWeightUpdateConsumer extends RcsConsumer<String> {

    @Autowired
    StandardTxThirdMarketOddsConsumer StandardTxThirdMarketOddsConsumer;

    @Override
    protected String getTopic() {
        return "RCS_TOUR_MATCH_TEMPLATE_CONFIG_TOPIC";
    }

    /**
     * 百家赔入库转发
     * @param text
     * @return
     */
    @Override
    public Boolean handleMs(String text) {
        //MatchId+"_"+MatchType+"_"+SportId
        String linkid = text;
        log.info("::{}::{}","RDTMTCTG_"+linkid,text);
        try {
            String[] str = text.split("_");
//            if(str[1].equals(1)){
//                log.info("::{}::早盘不根新:{}","RDTMTCTG_"+linkid,text);
//                return true;}
            StandardTxThirdMarketOddsConsumer.notifyModifyWeight(Long.valueOf(str[2]),str[0],str[1],linkid);
        } catch (Exception e) {
            log.error("::{}::{},{},{}","RDTMTCTG_"+linkid,e.getMessage(), e);
        }
        return true;
    }

}