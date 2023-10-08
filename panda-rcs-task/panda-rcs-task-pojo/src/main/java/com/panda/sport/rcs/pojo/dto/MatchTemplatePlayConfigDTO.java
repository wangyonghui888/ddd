package com.panda.sport.rcs.pojo.dto;

import lombok.Data;

/**
 * 玩法配置
 */
@Data
public class MatchTemplatePlayConfigDTO {


    /**
     * 自动关盘比分设置 1勾选 0未勾选 0关盘状态
     */
    private Integer isAutoCloseScoreConfig;

    /**
     * 当达到X分时关盘
     */
    private Integer achieveCloseScore;

    /**
     * 玩法id
     */
    private Integer playId;

    /**
     * 局数需要转换
     */
    private Integer timeVal;


}
