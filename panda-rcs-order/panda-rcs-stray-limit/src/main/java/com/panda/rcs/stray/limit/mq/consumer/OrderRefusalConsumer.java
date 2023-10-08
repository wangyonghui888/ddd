//package com.panda.rcs.stray.limit.mq.consumer;
//
//import com.alibaba.fastjson.JSONObject;
//import com.panda.rcs.stray.limit.entity.constant.MqConstant;
//import com.panda.rcs.stray.limit.mq.RcsConsumer;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.rocketmq.spring.annotation.ConsumeMode;
//import org.apache.rocketmq.spring.annotation.MessageModel;
//import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
//import org.springframework.stereotype.Component;
//
///**
// * @Project Name : panda-rcs-order-group
// * @Package Name : panda-rcs-order-group
// * @Description : 拒单、订单取消
// * @Author : Paca
// * @Date : 2022-03-30 11:41
// * @ModificationHistory Who    When    What
// * --------  ---------  --------------------------
// */
//@Slf4j
//@Component
//@RocketMQMessageListener(
//        topic = MqConstant.Topic.QUEUE_ORDER_REFUSAL,
//        consumerGroup = MqConstant.PREFIX + MqConstant.Topic.QUEUE_ORDER_REFUSAL + MqConstant.SUFFIX,
//        messageModel = MessageModel.CLUSTERING,
//        consumeMode = ConsumeMode.CONCURRENTLY)
//public class OrderRefusalConsumer extends RcsConsumer<JSONObject> {
//
//    @Override
//    protected String getTopic() {
//        return MqConstant.Topic.QUEUE_ORDER_REFUSAL;
//    }
//
//    @Override
//    protected Boolean handleMs(JSONObject message) {
//        return null;
//    }
//}
