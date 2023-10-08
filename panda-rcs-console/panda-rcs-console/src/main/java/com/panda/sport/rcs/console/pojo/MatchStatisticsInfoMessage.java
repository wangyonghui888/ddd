package com.panda.sport.rcs.console.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author :  Jimmy
 * @Project Name :  data-realtime
 * @Package Name :  com.panda.sport.data.realtime.api.message
 * @Description :  TODO
 * @Date: 2019-10-07 17:10
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MatchStatisticsInfoMessage implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
     * id
     */
    private Long id;

    /**
     * 第三方原始事件id
     */
    private Long thirdSourceEventId;

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
     * 事件发生时间，为utc时间戳
     */
    private Long eventTime;

    /**
     * 第三方事件类型id
     */
    private Integer thirdEventTypeId;

    /**
     * 第三方事件类型
     */
    private String thirdEventType;

    /**
     * 主客队信息. home: 主场队;away:客场队
     */
    private String homeAway;

    /**
     * 当前比赛进行时间.单位:秒
     */
    private Integer secondsMatchStart;

    /**
     * 预计比赛时长.  单位:秒
     */
    private Integer matchLength;

    /**
     * 当前比分信息
     */
    private String score;

    /**
     * 角球比分
     */
    private String cornerScore;

    /**
     * 黄牌比分
     */
    private String yellowCardScore;

    /**
     * 红牌比分
     */
    private String redCardScore;

    /**
     * 射正次数
     */
    private String shotOnTargetScore;

    /**
     * 射偏次数
     */
    private String shotOffTargetScore;

    /**
     * 危险进攻次数比分
     */
    private String dangerousAttackScore;

    /**
     * 发球得分
     */
    private String acesScore;

    /**
     * 两次发球失误比分
     */
    private String doubleFaultScore;

    /**
     * 阶段比分
     */
    private String periodScore;

    /**
     * 四分之一节比分
     */
    private String quarterScore;

    /**
     * 汇总比分
     */
    private String setScore;

    /**
     * 汇总比分1
     */
    private String set1Score;

    /**
     * 汇总比分2
     */
    private String set2Score;

    /**
     * 汇总比分3
     */
    private String set3Score;

    /**
     * 汇总比分4
     */
    private String set4Score;

    /**
     * 汇总比分5
     */
    private String set5Score;

    /**
     * 汇总比分6
     */
    private String set6Score;

    /**
     * 汇总比分7
     */
    private String set7Score;

    /**
     * 汇总比分8
     */
    private String set8Score;

    /**
     * 汇总比分9
     */
    private String set9Score;

    /**
     * 汇总比分10
     */
    private String set10Score;

    /**
     * 一局比分(网球)
     */
    private String gameScore;

    /**
     * 发球人
     */
    private Integer server;

    /**
     * Game short info
     */
    private String info;

    /**
     * 比赛剩余时间. 单位:秒
     */
    private Integer remainingTime;

    /**
     * 比赛阶段
     */
    private Integer period;

    /**
     * 比赛阶段个数
     */
    private Integer periodLength;

    /**
     * Total set count
     */
    private Integer setCount;

    /**
     * 点球比分
     */
    private String penaltyScore;

    /**
     * 任意球比分
     */
    private String freeKickScore;

    /**
     * 加时赛比分
     */
    private String extraTimeScore;

    /**
     * set1的黄牌比分
     */
    private String set1YellowCardScore;

    /**
     * set1的红牌比分
     */
    private String set1RedCardScore;

    /**
     * set1的角球比分
     */
    private String set1CornerScore;

    /**
     * set2的黄牌比分
     */
    private String set2YellowCardScore;

    /**
     * set2的红牌比分
     */
    private String set2RedCardScore;

    /**
     * set2的角球比分
     */
    private String set2CornerScore;

    /**
     * 备注
     */
    private String remark;

    /**
     * 更新时间. UTC时间,精确到毫秒
     */
    private Long modifyTime;

}
