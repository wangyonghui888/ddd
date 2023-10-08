package com.panda.sport.rcs.third.entity.beter.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Beulah
 * @date 2023/3/21 12:58
 * @description 投注项基本字段
 */
@Data
public class BeterThirdParamBaseDto implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 投注项id
     */
    private String outcomeId;
    /**
     * 赔率
     */
    private Double coefficient;
}
