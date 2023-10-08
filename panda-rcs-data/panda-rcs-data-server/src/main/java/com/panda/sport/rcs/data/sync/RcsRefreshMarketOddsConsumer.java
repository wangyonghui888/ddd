package com.panda.sport.rcs.data.sync;

import com.panda.merge.dto.Request;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.mapper.StandardSportMarketMapper;
import com.panda.sport.rcs.data.mq.RcsConsumer;
import com.panda.sport.rcs.data.mqSerializaBean.StandardMarketMessageDTO;
import com.panda.sport.rcs.data.mqSerializaBean.StandardMatchMarketMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = MqConstants.RCS_REALTIME_SYNC_MARKET_ODDS_TOPIC,
        consumerGroup = "RCS_DATA_RCS_REALTIME_SYNC_MARKET_ODDS_TOPIC_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class RcsRefreshMarketOddsConsumer extends RcsConsumer<Request<StandardMatchMarketMessageDTO>> {

    @Autowired
    StandardSportMarketMapper standardSportMarketMapper;

    @Override
    protected String getTopic() {
        return MqConstants.RCS_REALTIME_SYNC_MARKET_ODDS_TOPIC;
    }

    /**
     * @Description: 实时盘口赔率变化通知
     * @Author: Vector
     * @Date: 2019/12/12
     **/
    @Override
    public Boolean handleMs(Request<StandardMatchMarketMessageDTO> msg) {
        try {
            StandardMatchMarketMessageDTO data = msg.getData();
            if (data == null || data.getStandardMatchInfoId() == null) {return true;}

            List<StandardMarketMessageDTO> marketList = data.getMarketList();
            if (CollectionUtils.isEmpty(marketList)) {return true;}
            for(StandardMarketMessageDTO bean : marketList) {
            	Map<String, Object> map = new HashMap<String, Object>();
            	map.put("marketId", String.valueOf(bean.getId()));
            	map.put("addition1", bean.getAddition1());
            	standardSportMarketMapper.updateAdditionOne(map);
            }
        } catch (Exception e) {
            log.error("::{}::,{},{},{}", "RDRRSMOTG_"+msg.getLinkId(),JsonFormatUtils.toJson(msg),e.getMessage(),e);
        }
        return true;
    }


}