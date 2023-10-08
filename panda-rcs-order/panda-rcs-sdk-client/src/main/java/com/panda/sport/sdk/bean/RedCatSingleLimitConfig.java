package com.panda.sport.sdk.bean;

import lombok.Data;

/**
 * 单关单注明细配置
 * @author vere
 * @date 2023-06-30
 * @version 1.0.0
 */
@Data
public class RedCatSingleLimitConfig {
    /**
     * 赛制长度
     */
    private Integer matchLength;
    /**
     * 限额
     */
    private Long limit;
}
