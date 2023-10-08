package com.panda.rcs.stray.limit.entity.constant;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : MQ
 * @Author : Paca
 * @Date : 2022-03-29 11:01
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface MqConstant {

    String PREFIX = "RCS_STRAY_";

    String SUFFIX = "_GROUP";

    interface Topic {
        /**
         * 结算派彩
         */
        String OSMC_SETTLE_RESULT_SDK = "OSMC_SETTLE_RESULT_SDK";

        /**
         * 拒单、订单取消，来源业务
         */
        String QUEUE_ORDER_REFUSAL = "queue_order_refusal";
    }

    static String getGroup(String topic) {
        return PREFIX + topic + SUFFIX;
    }
}
