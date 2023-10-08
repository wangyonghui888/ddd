package com.panda.sport.data.rcs.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * <p>
 *  标准赛事数据DTO
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
@Data
public class StandardMatchInfoDTO implements Serializable {

    /**
     * 运动种类id。 对应sport.id
     */
    private Long sportId;

    /**
     * 联赛 id。  对应 sport_tournament.id
     */
    private Long tournamentId;

    /**
     * 标准赛事id
     */
    private Long Id;

    /**
     * 赛前盘下注状态。赛前盘：1 可下注；0不可下注；用于数据源控制下注状态
     */
    private Integer preMatchBetStatus;

    /**
     * 滚球下注状态。滚球中使用：1 可下注；0不可下注；用于数据源控制下注状态
     */
    private Integer liveOddsBetStatus;

    /**
     * 比赛进行时间。单位：秒
     */
    private Integer secondsMatchStart;

    /**
     * 主客队是否相反。与标准球队相比，主客队是否相反。0:否；1：是
     */
    private Integer homeAwayOpposite;

    /**
     * 比赛开始时间。 UTC时间
     */
    private Long beginTime;

    /**
     * 是否为中立场。取值为 0  和1。1:是中立场，0:非中立场。操盘人员可手动处理
     */
    private Integer neutralGround;

    /**
     * 当前比赛是否被预定。是否预定 0 否，1 是
     */
    private Integer booked;

    /**
     * 数据来源编码。取值： SR BC分别代表：SportRadar、BetConstruct。详情见data_source
     */
    private String dataSourceCode;

    /**
     * 先开球的球队。 取值： home away。home:主场队；away:客场队。
     */
    private String whoKickOff;

    /**
     * 比赛阶段。参考字典表定义：system_type_dict、system_item_dict
     */
    private String matchPeriod;

    /**
     * 比赛状态。参考字典表定义：system_type_dict、system_item_dict
     */
    private String matchStatus;

}
