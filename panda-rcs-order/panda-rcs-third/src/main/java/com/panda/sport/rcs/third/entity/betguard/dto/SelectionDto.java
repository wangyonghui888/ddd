package com.panda.sport.rcs.third.entity.betguard.dto;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.poi.hpsf.Decimal;

import java.io.Serializable;

/**
 * @author Beulah
 * @date 2023/4/1 22:11
 * @description todo
 */
@Data
public class SelectionDto implements Serializable {
    private static final long serialVersionUID = 1L;

    //BC后端中用于识别选择的投注选择/事件的唯一 ID。（必填）
    @JsonProperty("SelectionId")
    private String selectionId;

    //投注像/事件的名称。 例如，P1。
    //private String SelectionName;

    //BC后端中玩法的唯一 ID，用于识别盘口类型。（必填）
    @JsonProperty("MarketTypeId")
    private Long marketTypeId;

    //玩法名称
    //private String MarketName;

    //BC后端中赛事的唯一 ID。（必填）
    @JsonProperty("MatchId")
    private int matchId;

    //赛事名称
    /*private String MatchName;

    //赛事开始时间(UTC)
    private DateTime MatchStartDate;

    //赛事结束时间(UTC)
    private DateTime EventEndDate;*/

    //BC后端中进行比赛的地区的唯一 ID。（必填）
    @JsonProperty("RegionId")
    private int regionId;

    //The name of the region. For example, Spain.
    //private String RegionName;

    //Unique ID of the competition of the match in
    //BetConstructBE. (Mandatory)
    @JsonProperty("CompetitionId")
    private int competitionId;

    //The name of the competition. For example, La Liga.
    //private String CompetitionName;

    //Unique ID of the competition of the match in
    //BetConstructBE. (Mandatory)
    @JsonProperty("SportId")
    private int sportId;

    //The name of the sport. For example, Football.
   // private String SportName;

    //The full name of the sport, It’s also translatable.
    //private String SportFullName;

    //The price/odd of the selection. (Mandatory)
    @JsonProperty("Price")
    private Double price;

    //True – when the selection is on live match, False –
    //Pre-Match.
    /*private boolean IsLive;

    //Handicap value for handicap type markets. For
    //example, -1, 2.5, etc
    private Decimal Basis;

    //A text describing the score and some live info when the
    //bet is placed on this Live event.
    private String MatchInfo;

    //True - when the selection is an outright match/market,
    //otherwise - False.
    private boolean IsOutright;

    //The name of the team/player.
    private String HomeTeamName;

    //The name of the team/player.
    private String AwayTeamName;

    //Unique ID of the team/player.
    private int HomeTeamId;

    //Unique ID of the team/player.
    private int AwayTeamId;*/




}
