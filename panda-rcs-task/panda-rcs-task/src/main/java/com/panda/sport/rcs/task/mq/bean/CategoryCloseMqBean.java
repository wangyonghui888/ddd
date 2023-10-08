package com.panda.sport.rcs.task.mq.bean;

import com.panda.sport.rcs.vo.MatchMarketLiveOddsVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CategoryCloseMqBean implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 标准比赛ID   standard_match_info.id
     */
    private Long matchId;

    /**
     * 关盘玩法的集合
     */
    private List<Long> categoryIds;

    private Long sportId;

    /**
     * 比赛阶段
     */
    private Integer period;

    /**
     * 当前比分信息
     */
    private String score;

    /**
     * 小节比分
     */
    private String setScore;
    /**
     * 阶段比分
     */
    private String periodScore;
    /**
     * 距离比赛开始多少秒
     */
    private Long secondsFromStart;
    /**
     * 1 进球带X玩法  2 进球时间15分钟玩法
     */
    private Integer categoryType;

}
