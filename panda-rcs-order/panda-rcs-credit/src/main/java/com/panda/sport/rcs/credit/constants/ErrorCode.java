package com.panda.sport.rcs.credit.constants;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 错误码
 * @Author : Paca
 * @Date : 2021-05-04 20:01
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ErrorCode {

    int CONFIG_QUERY_EXCEPTION = -10001;

    int CONFIG_UPDATE_EXCEPTION = -10002;

    int PARAM_CHECK_EXCEPTION = -10003;

    /**
     * 成功
     */
    int LIMIT_SUCCESS = 0;

    /**
     * 失败
     */
    int LIMIT_FAILURE = -1;

    /**
     * 代理单场剩余额度不足
     */
    int LIMIT_20001 = -20001;

    /**
     * 代理玩法剩余额度不足
     */
    int LIMIT_20002 = -20002;

    /**
     * 用户玩法剩余额度不足
     */
    int LIMIT_20101 = -20101;

    /**
     * MTS剩余额度不足
     */
    int LIMIT_20201 = -20201;

    /**
     * 商户单注额度不足
     */
    int LIMIT_20301 = -20301;

    /**
     * 冠军玩法，商户玩法额度不足
     */
    int LIMIT_21001 = -21001;

    /**
     * 冠军玩法，用户玩法额度不足
     */
    int LIMIT_21101 = -21101;

    /**
     * 冠军玩法，用户单注额度不足
     */
    int LIMIT_21102 = -21102;

    /**
     * 冠军玩法，用户单项额度不足
     */
    int LIMIT_21103 = -21103;
}
