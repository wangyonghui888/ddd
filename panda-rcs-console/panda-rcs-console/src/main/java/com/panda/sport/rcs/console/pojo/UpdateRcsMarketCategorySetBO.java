package com.panda.sport.rcs.console.pojo;

import lombok.Data;

/**
 * 玩法集表
 */
@Data
public class UpdateRcsMarketCategorySetBO  {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 多语言编码
     */
    private Long nameCode;
    private String oldNameCode;


}
