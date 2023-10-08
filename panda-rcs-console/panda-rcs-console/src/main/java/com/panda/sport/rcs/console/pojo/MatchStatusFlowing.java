package com.panda.sport.rcs.console.pojo;

import lombok.Data;

@Data
public class MatchStatusFlowing {
    /**
     * 表ID, 自增
     */
    private Long id;

    /**
     * 链路id
     */
    private String linkId;

    /**
     * 原本id
     */
    private Long oId;

    /**
     * 标准赛事id
     */
    private Long standardMatchId;

    /**
     * 标准运动种类id. 对应 standard_sport_type.id
     */
    private String sportId;

    /**
     * 比赛开始时间. 比赛开始时间 UTC时间
     */
    private Long beginTime;

    /**
     * 赛事状态.  比如:未开赛, 滚球, 取消, 延迟等.
     */
    private Integer matchStatus;

    /**
     * 是否为中立场. 取值为 0  和1  .   1:是中立场, 0:非中立场. 操盘人员可手动处理
     */
    private Integer neutralGround;

    /**
     * 第三方赛事原始id. 该厂比赛在第三方数据供应商中的id. 比如:  SportRadar 发送数据时, 这场比赛的ID.
     */
    private String thirdMatchSourceId;

    /**
     * 赛事可下注状态. 0: betstart; 1: betstop
     */
    private Integer betStatus;

    /**
     * 比赛阶段id. 取自基础表 : match_status.id
     */
    private Long matchPeriodId;

    private String insertTime;
}