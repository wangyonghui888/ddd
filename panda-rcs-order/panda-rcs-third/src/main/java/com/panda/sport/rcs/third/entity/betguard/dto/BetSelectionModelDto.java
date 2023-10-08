package com.panda.sport.rcs.third.entity.betguard.dto;

import cn.hutool.core.date.DateTime;
import lombok.Data;
import org.apache.poi.hpsf.Decimal;

import java.io.Serializable;

/**
 * @author Beulah
 * @date 2023/4/1 22:11
 * @description todo
 */
@Data
public class BetSelectionModelDto implements Serializable {
    private static final long serialVersionUID = 1L;

    //BC后端中用于识别选择的投注选择/事件的唯一 ID。（必填）
    private Long SelectionId;

    /**
     * Selection name
     * */
    private String SelectionName;

    //投注像/事件的名称。 例如，P1。
    //private String SelectionName;

    //BC后端中玩法的唯一 ID，用于识别盘口类型。（必填）
    private Long MarketTypeId;

    //玩法名称
    private String MarketName;

    //BC后端中赛事的唯一 ID。（必填）
    private int MatchId;

    //赛事名称
    private String MatchName;

    //赛事开始时间(UTC)
    private DateTime MatchStartDate;


    //BC后端中进行比赛的地区的唯一 ID。（必填）
    private int RegionId;

    //The name of the region. For example, Spain.
    private String RegionName;

    //Unique ID of the competition of the match in
    //BetConstructBE. (Mandatory)
    private int CompetitionId;

    //The name of the competition. For example, La Liga.
    private String CompetitionName;

    //Unique ID of the competition of the match in
    //BetConstructBE. (Mandatory)
    private int SportId;

    //The name of the sport. For example, Football.
    private String SportName;

    //The price/odd of the selection. (Mandatory)
    private Decimal Price;

    //True – when the selection is on live match, False –
    //Pre-Match.
    private boolean IsLive;

    //Handicap value for handicap type markets. For
    //example, -1, 2.5, etc
    private Decimal Basis;


    //True - when the selection is an outright match/market,
    //otherwise - False.
    private boolean IsOutright;


}
