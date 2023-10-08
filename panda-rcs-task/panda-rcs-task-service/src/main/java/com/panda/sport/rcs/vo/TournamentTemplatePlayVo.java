package com.panda.sport.rcs.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author :  Enzo
 * @Description :  玩法设置同步
 * @Date: 2020-09-18 15:10
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class TournamentTemplatePlayVo implements Serializable {
    /**
     * 赛事id
     */
    private Long standardMatchId;
    /**
     * 盘口类型1：早盘；0：滚球
     */
    private Integer matchType;
    /**
     * SR权重
     */
    private Integer srWeight;
    /**
     * BC权重
     */
    private Integer bcWeight;
    /**
     * BG权重
     */
    private Integer bgWeight;
    /**
     * tx权重
     */
    private Integer txWeight;
    /**
     * rb权重
     */
    private Integer rbWeight;
    /**
     * pd权重
     */
    private Integer pdWeight;

    /**
     * ao权重
     */
    private Integer aoWeight;
    /**
     * pi权重
     */
    private Integer piWeight;
    /**
     * ls权重
     */
    private Integer lsWeight;
    /**
     * 操盘平台
     */
    private String riskManagerCode;
    /**
     * 玩法集合
     */
    private List<TournamentTemplateCategoryVo> categoryList;
}
