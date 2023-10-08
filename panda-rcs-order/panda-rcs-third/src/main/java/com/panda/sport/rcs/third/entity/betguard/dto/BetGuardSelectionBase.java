package com.panda.sport.rcs.third.entity.betguard.dto;

import cn.hutool.core.date.DateTime;
import lombok.Data;
import lombok.ToString;
import org.apache.poi.hpsf.Decimal;

import java.io.Serializable;

/**
 * @author Beulah
 * @date 2023/3/31 14:10
 * @description bc投注项基本参数
 */
@ToString
@Data
public class BetGuardSelectionBase implements Serializable {
    private static final long serialVersionUID = 1262353502003166747L;

    // BC后端中用于识别选择的投注选择/事件的唯一 ID。（必填）
    private Long SelectionId;
    private String SelectionName;   //投注像/事件的名称。 例如，P1。
    private Long MarketTypeId;		//BC后端中玩法的唯一 ID，用于识别盘口类型。（必填）
    private String MarketName;		//玩法名称
    private Integer MatchId;		//BC后端中赛事的唯一 ID。（必填）
    private String MatchName;	//赛事名称
    private DateTime MatchStartDate;		//赛事开始时间(UTC)
    private DateTime EventEndDate;		//赛事结束时间(UTC)
    private Integer RegionId;		//BC后端中进行比赛的地区的唯一 ID。（必填）
    private String RegionName;	//地区名称
    private Integer CompetitionId;		//BC后端中联赛的唯一 ID。（必填）
    private String CompetitionName;	//联赛名称
    private Integer SportId;	//	BC后端中赛种的唯一 ID。（必填）
    private String SportName;	//赛种名称
    private Decimal Price;		//投注项赔率。（必填）
    private Boolean IsLive;  //True – 滚球, False – 赛前.
    private Decimal Basis;		//让分类型市场的让分值。 例如，-1、2.5 等。(盘口值)
    private Boolean IsOutright;		//True - when the selection is an outright match/market,False - otherwise


}
