package com.panda.sport.rcs.third.common;

/**
 * 获取第三方返回结果状态枚举类
 * @author vere
 * @date 2023-05-27
 * @version 1.0.0
 */
public final class ThirdReceivedConstants {

    /**
     * 红猫常量
     */
    public class RedCatMessage{

        /**
         * 已处理
         */
        public static final String ACCEPTED="ACCEPTED";
        /**
         * 拒绝
         */
        public static final String REJECTED="REJECTED";
        /**
         * 价格变更
         */
        public static final String RE_OFFER="REOFFER";
        /**
         * 取消
         */
        public static final String CANCELLED="CANCELLED";
        /**
         * 处理中
         */
        public static final String PENDING="PENDING";

        /**
         * 成功
         */
        public static final String SUCCESS="true";

    }
}
