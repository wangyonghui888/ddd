package com.panda.rcs.logService.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Z9-jing
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessLogAllVo  extends  RcsQuotaBusinessLimitLog{

    private Integer id;

    /**
     * 运动种类id
     */
    private Integer sportId;

    /**
     * 藏单状态开关 0开 1关
     */
    private Integer status;

    /**
     * 商户id
     */
    private Long merchantsId;
    /**
     * 商户编码
     */
    private String merchantsCode;
    /**
     * 最大藏单金额
     */
    private Long hideMoney;

    /**
     * id主键集合
     */
    private List<Integer> ids;

    private String method;

    private String beforeString;

    private Object[] afterString;
    /**
     * '漏单比例'
     */
    private BigDecimal volumePercentage;
    /**
     * '金额区间始'
     */
    private Integer minMoney;
    /**
     * 金额区间终
     */
    private Integer maxMoney;
    /**
     * 标签开关
     */
    private Integer bqStatus;
    /**
     * 全局开关
     */
    private Integer qjStatus;

    private Integer type;

    private List<Long> levelId;


}
