package com.panda.sport.data.rcs.dto.limit;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Project Name: panda-rcs-order-group
 * @Package Name: com.panda.sport.rcs.dto.limit
 * @Description : 表 rcs_quota_limit_other_data
 * @Author : Paca
 * @Date : 2020-09-25 11:36
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class RcsQuotaLimitOtherDataVo implements Serializable {

    private static final long serialVersionUID = -5437200337037762331L;

    /**
     * 1：用户单日限额（暂时没用存在用户限额表里的）
     * 2：串关 单注最低投注额
     * 3：串关单注最高投注额所占比例
     * 4：投注项计入单关限额投注比例 2串1
     * 5：投注项计入单关限额投注比例 3串1
     * 6：投注项计入单关限额投注比例 4串1
     * 7：投注项计入单关限额投注比例 5串1
     * 8：投注项计入单关限额投注比例 6串1
     * 9：投注项计入单关限额投注比例 7串1
     * 10：投注项计入单关限额投注比例 8串1
     * 11：投注项计入单关限额投注比例 9串1
     * 12：投注项计入单关限额投注比例 10串1
     */
    private Integer type;

    /**
     * 值
     */
    private BigDecimal baseValue;

}
