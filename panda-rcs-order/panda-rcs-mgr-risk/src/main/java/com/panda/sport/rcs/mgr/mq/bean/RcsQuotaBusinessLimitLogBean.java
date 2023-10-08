package com.panda.sport.rcs.mgr.mq.bean;

import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimitLog;
import lombok.Data;

/**
 * @author Z9-jing
 */
@Data
public class RcsQuotaBusinessLimitLogBean extends RcsQuotaBusinessLimitLog {

    private String method;

    private String beforeString;

    private Object[] afterString;
    /**
     * 藏单状态开关  0开 1关
     */
    private Integer status;
    /**
     * 最大藏单金额
     */
    private Long hideMoney;

}
