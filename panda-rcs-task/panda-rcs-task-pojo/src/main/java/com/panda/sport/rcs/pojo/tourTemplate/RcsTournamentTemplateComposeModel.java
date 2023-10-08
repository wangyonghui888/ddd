package com.panda.sport.rcs.pojo.tourTemplate;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.panda.sport.rcs.utils.DateFormatSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Data
public class RcsTournamentTemplateComposeModel implements Serializable {

    /**
     * 赛事ID
     */
    private Long matchId;
    /**
     * 1早盘 0 滚球
     */
    private Integer matchType;
    /**
     * 模板id
     */
    private Long templateId;
    /**
     * 玩法id
     */
    private Long playId;
    /**
     * 玩法名称
     */
    private Long marginId;

    private Long marginRefId;

    private Integer timeVal;

    private Long validMarginId;

    private Integer marketCount;

    private String viceMarketRatio;

    private Integer pauseWaitTime;

    private Integer normalWaitTime;

    private BigDecimal pauseMargain;
    /**
     * 是否有盘口
     */
    private Boolean haveHandicap;
}
