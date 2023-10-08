package com.panda.sport.rcs.mgr.service.impl;

import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.mgr.service.impl
 * @Description :  线程池发送订单MQ消息
 * @Date: 2019-12-11 15:44
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@TraceCrossThread
public class TaskExecutorOrder<T> {
    @Autowired
    ProducerSendMessageUtils sendMessage;

    private class MessageOrderSendTask<T> implements Runnable {
        private T orderMsgBean;
        private String    topic;

        public MessageOrderSendTask(T orderMsgBean,String topic) {
            this.orderMsgBean = orderMsgBean;
            this.topic = topic;
        }

        @Override
        public void run() {
            log.info("发送订单mq消息:{}",orderMsgBean);
            sendMessage.sendMessage(topic, orderMsgBean);
        }
    }

    private TaskExecutor taskExecutor;

    public TaskExecutorOrder(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void sendOrderMessage(T orderMsgBean,String topic) {
        taskExecutor.execute(new MessageOrderSendTask(orderMsgBean,topic));
    }
}
