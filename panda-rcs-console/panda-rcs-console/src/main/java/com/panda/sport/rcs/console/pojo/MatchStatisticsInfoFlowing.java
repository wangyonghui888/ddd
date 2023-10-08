package com.panda.sport.rcs.console.pojo;

import lombok.Data;

import javax.persistence.Column;
import java.util.List;

@Data
public class MatchStatisticsInfoFlowing {

    /**
     * 表ID, 自增
     */
    private Long id;

    /**
     * 链路id
     */
    @Column(name = "link_id")
    private String globalId;

    /**
     * 原本id
     */
    private Long oId;

    private static final long serialVersionUID = 1L;

    private List<MatchStatisticsInfoDetailFlowing> list;

    /**
     * 运动类型
     */
    private Long sportId;
    /**
     * 第三方赛事原始id
     */
    private Long thirdSourceMatchId;

    /**
     * 第三方赛事id
     */
    private Long thirdMatchId;

    /**
     * 标准赛事id
     */
    private Long standardMatchId;

    /**
     * 预计比赛时长.  单位:秒
     */
    private Integer matchLength;

    /**
     * Game short info
     */
    private String info;

    /**
     * 比赛阶段
     */
    private Integer period;

    /**
     * Total set count
     */
    private Integer setCount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 更新时间. UTC时间,精确到毫秒
     */
    private Long modifyTime;

    /**
     * 当前比分信息
     */
    private String score;

    private String insertTime;

    private String remainingTime;

    private String secondsMatchStart;

}

