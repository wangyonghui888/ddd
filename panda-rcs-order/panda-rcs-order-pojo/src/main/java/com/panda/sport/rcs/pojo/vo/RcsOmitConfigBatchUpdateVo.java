package com.panda.sport.rcs.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class RcsOmitConfigBatchUpdateVo {

    /**
     * 商户
     */
    private List<String> merchantIds;

    /**
     * 设置类型 1:批量設置、2:默認設置、2:例外設置
     */
    private Integer type;

    /**
     * 漏单比例
     */
    private BigDecimal volumePercentage;

    /**
     * 金额区间始
     */
    private Integer minMoney;

    /**
     * 金额区间终
     */
    private Integer maxMoney;

    /**
     * 标签开关：1：开 2：关
     */
    private Integer bqStatus = 2;

    /**
     * 全局开关：1：开 2：关
     */
    private Integer qjStatus = 2;

    /**
     * 用户标签，可以多选,用,号分割
     */
    private List<String> levelId = new ArrayList<>();

    private Long id;
}
