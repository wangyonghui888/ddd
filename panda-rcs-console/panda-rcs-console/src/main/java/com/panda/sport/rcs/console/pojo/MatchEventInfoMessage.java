package com.panda.sport.rcs.console.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Mirro
 * @Project Name :  panda_data_realtime
 * @Package Name :  com.panda.sport.data.realtime.dto
 * @Description:
 * @date 2019/10/4 19:50
 * @ModificationHistory Who    When    What
 */
@Data
public class MatchEventInfoMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 运动种类id。 对应sport.id
     * 如果玩法不区分体育类型，传0，否则传对应体育类型标识
     */
    private Long sportId;

    /**
     * 是否被取消.1 被取消; 0:没有被取消
     */
    private Integer canceled;

    /**
     * 对应data_source.code
     */
    private String dataSourceCode;
//
//    /**
//     * 数据来源类型：
//     * 0 : UOF
//     * 1:  Scoring Feed
//     * 參考枚举 类型映射
//     */
//    private String sourceType;

    /**
     * 事件编码. 对应 match_event_type.event_code
     */
    private String eventCode;

    /**
     * 事件发生时间. UTC时间
     */
    private Long eventTime;

    /**
     * 扩展信息
     */
    private String extraInfo;

    /**
     * 主客场. 主场队:home; 客场队:away
     */
    private String homeAway;

    /**
     * 比赛阶段id.  system_item_dict.value
     */
    private Long matchPeriodId;

    /**
     * 球员1的id
     */
    private Long player1Id;

    /**
     * 球员1的名称
     */
    private String player1Name;

    /**
     * 球员2的id
     */
    private Long player2Id;

    /**
     * 球员2的名称
     */
    private String player2Name;

    /**
     * 距离比赛开始多少秒
     */
    private Long secondsFromStart;

    /**
     * 标准赛事的id. 对应 standard_match_info.id
     */
    private Long standardMatchId;

    /**
     * 标准球队 ID. 对应 standard_sport_team.id
     */
    private Long standardTeamId;

    /**
     * 主队数量
     */
    private Integer t1;

    /**
     * 客队数量
     */
    private Integer t2;
    /**
     * 当前第几局
     */
    private Integer secondNum;

    /**
     * 盘主队比分
     */
    private Integer firstT1;

    /**
     * 盘客队比分
     */
    private Integer firstT2;

    /**
     * 局主队比分
     */
    private Integer secondT1;

    /**
     * 局客队比分
     */
    private Integer secondT2;

    /**
     * 当前盘数
     */
    private Integer firstNum;
    /**
     * 第三方数据源提供的该事件id.
     */
    private String thirdEventId;

    /**
     * 第三方赛事的id. 对应third_match_info.id
     */
    private String thirdMatchId;

    /**
     * 比赛在数据源中的ID
     */
    private String thirdMatchSourceId;

    /**
     * 第三方球队id. 对应 third_sport_team.id
     */
    private String thirdTeamId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 当前节\阶段剩余时间
     */
    private Long periodRemainingSeconds;
    /**
     * 创建时间. UTC时间
     */
    private Long createTime;

    /**
     * 修改时间. UTC时间
     */
    private Long modifyTime;
    /**
     * 扩展字段
     */
    private String addition6;

    /**
     * 扩展字段
     */
    private String addition7;

    /**
     * 扩展字段
     */
    private String addition8;

    /**
     * 扩展字段
     */
    private String addition9;

    /**
     * 扩展字段
     */
    private String addition10;

    private String addition1;

    private String addition2;

    /**
     * 扩展字段
     */
    private String addition3;

    /**
     * 扩展字段
     */
    private String addition4;

    /**
     * 扩展字段
     */
    private String addition5;
}
