package com.panda.sport.rcs.common.constants;

public class RocketMQConstants {
    /**
     * 投注特征类 类型的标签变更推送
     **/
    public static final String USER_PROFILE_TAGS_TOPIC = "USER_PROFILE_TAGS_TOPIC";
    public static final String USER_PROFILE_TAGS_TAG = "USER_PROFILE_TAGS_TAG";

    /**
     * 财务特征类 类型的标签变更推送
     **/
    public static final String USER_FINANCE_TAGS_TOPIC = "USER_FINANCE_TAGS_TOPIC";
    public static final String USER_FINANCE_TAGS_TAG = "USER_FINANCE_TAGS_TAG";

    /**
     * 编辑/新增标签新增规则时，变更标签时推送给业务
     **/
    public static final String USER_TAG_CHANGE_TOPIC = "USER_TAG_CHANGE_TOPIC";
    public static final String USER_TAG_CHANGE_TAG = "USER_TAG_CHANGE_TAG";

    /**
     * 1912  自动化标签 商户决策
     */
    public static final String RCS_RISK_MERCHANT_MANAGER_TASK_AUTO_TAG = "rcs_risk_merchant_manager_task_auto_tag";


    /**
     * 编辑/新增标签新增规则时，变更标签时推送给业务
     **/
    public static final String USER_FINANCE_TAG_CHANGE_TOPIC = "USER_FINANCE_TAG_CHANGE_TOPIC";
    public static final String USER_FINANCE_TAG_CHANGE_TAG = "USER_FINANCE_TAG_CHANGE_TAG";

    /**
     * 操作标签时，WS推送给前端刷新界面
     **/
    public static final String USER_TAG_FLUSH_TOPIC = "USER_TAG_FLUSH_TOPIC";
    public static final String USER_TAG_FLUSH_TAG = "USER_TAG_FLUSH_TAG";

    /**
     * 用户-规则 发送topic
     */
    public static final String USER_RULE_CHECK_SEND = "rcs_user_rule_check_send";
    /**
     * 用户-规则 发送时间
     */
    public static final String USER_RULE_CHECK_LAST_TIME = "user_rule_check_last_time";
}
