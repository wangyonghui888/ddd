package com.panda.sport.data.rcs.dto.limit.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Project Name: panda-rcs-order-group
 * @Package Name: com.panda.sport.rcs.enums.limit
 * @Description : 限额数据类型
 * @Author : Paca
 * @Date : 2020-09-25 16:34
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Getter
@AllArgsConstructor
public enum LimitDataTypeEnum {
    /**
     * 位置限额
     */
    PLACE_LIMIT(-1),
    /**
     * 商户限额
     */
    MERCHANT_LIMIT(0),
    /**
     * 商户单场限额
     */
    MERCHANT_SINGLE_LIMIT(1),
    /**
     * 用户单日限额
     */
    USER_DAILY_LIMIT(2),
    /**
     * 用户单场限额
     */
    USER_SINGLE_LIMIT(3),
    /**
     * 用户单注单关限额
     */
    USER_SINGLE_BET_LIMIT(4),
    /**
     * 串关单注赔付限额
     */
    SERIES_PAYMENT_LIMIT(5),
    /**
     * 各投注项计入单关限额的投注比例
     */
    SERIES_RATIO(6),
    /**
     * 最低/最高投注额限制
     */
    BET_AMOUNT_LIMIT(7),
    /**
     * 计入串关已用额度的比例
     */
    SERIES_USED_RATIO(8),
    /**
     * 用户特殊限额
     */
    USER_SPECIAL_LIMIT(9),
    /**
     * 计入串关已用额度的比例
     */
    TAG_LIMIT(10),
    /**
     * 综合球种联赛模板限额
     */
    OTHER_SPORT_LIMIT(11);


    private Integer type;
}
