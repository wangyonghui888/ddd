package com.panda.sport.rcs.mgr.operation.order.impl;

import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.mgr.operation.order.CalcOrder;
import com.panda.sport.rcs.mgr.paid.annotion.Order;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.rpc.operation.order.impl
 * @Description :  用于通知websocket
 * @Date: 2019-11-04 17:29
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
@Order(4)
public class OrderNoteRecordNotifyImpl extends CalcOrderBase implements CalcOrder {
    @Autowired
    private ProducerSendMessageUtils sendMessage;

    /**
     * @return void
     * @Description 处理
     * @Param [orderBean]
     * @Author toney
     * @Date 14:38 2019/10/25
     **/
    @Override
    public void orderHandle(OrderBean orderBean, Integer type) {
//        log.info("推送OrderNoteRecordNotifyImpl,开始调用websocket推送:{}",JSONObject.toJSONString(orderBean));
//        sendMessage.sendMessage(MqConstants.WS_ORDER_BET_RECORD_TOPIC, MqConstants.WS_ORDER_BET_RECORD_TAG, "", orderBean);
    }
}
