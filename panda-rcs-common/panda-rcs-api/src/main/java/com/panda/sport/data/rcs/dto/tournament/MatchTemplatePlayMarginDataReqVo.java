package com.panda.sport.data.rcs.dto.tournament;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 查询赛事模板中指定玩法相关字段 请求参数VO
 *
 * @description:
 * @author: Waldkir
 * @date: 2022-01-02 14:14
 */
@Data
public class MatchTemplatePlayMarginDataReqVo implements java.io.Serializable{
    /**
     * 赛种
     */
    Integer sportId;
    /**
     * 赛事ID
     */
    Long matchId;
    /**
     * 1：早盘；0：滚球
     */
    Integer matchType;
    /**
     * 玩法id
     */
    Integer playId;
}