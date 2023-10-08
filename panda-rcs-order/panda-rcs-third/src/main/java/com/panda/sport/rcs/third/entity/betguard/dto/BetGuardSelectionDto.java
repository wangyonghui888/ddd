package com.panda.sport.rcs.third.entity.betguard.dto;

import cn.hutool.core.date.DateTime;
import lombok.Data;
import lombok.ToString;
import org.apache.poi.hpsf.Decimal;

import java.io.Serializable;

/**
 * @author Beulah
 * @date 2023/3/29 21:19
 * @description betGuard 投注项扩展参数
 */

@ToString
public class BetGuardSelectionDto extends BetGuardSelectionBase implements Serializable {

    private static final long serialVersionUID = -3593230581314104269L;

    private String SportFullName;	//赛种全名
    private String MatchInfo;		//赛事信息
    private String HomeTeamName;		//主队名称
    private String AwayTeamName;		//客队名称
    private Integer HomeTeamId;		//主队ID
    private Integer AwayTeamId;		//客队ID


    public String getSportFullName() {
        return SportFullName;
    }

    public void setSportFullName(String sportFullName) {
        SportFullName = sportFullName;
    }

    public String getMatchInfo() {
        return MatchInfo;
    }

    public void setMatchInfo(String matchInfo) {
        MatchInfo = matchInfo;
    }

    public String getHomeTeamName() {
        return HomeTeamName;
    }

    public void setHomeTeamName(String homeTeamName) {
        HomeTeamName = homeTeamName;
    }

    public String getAwayTeamName() {
        return AwayTeamName;
    }

    public void setAwayTeamName(String awayTeamName) {
        AwayTeamName = awayTeamName;
    }

    public Integer getHomeTeamId() {
        return HomeTeamId;
    }

    public void setHomeTeamId(Integer homeTeamId) {
        HomeTeamId = homeTeamId;
    }

    public Integer getAwayTeamId() {
        return AwayTeamId;
    }

    public void setAwayTeamId(Integer awayTeamId) {
        AwayTeamId = awayTeamId;
    }
}
