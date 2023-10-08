package com.panda.sport.rcs.mgr.mq.bean;

/**
 * @author :  regan
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.sdk.vo
 * @Description :  TODO
 * @Date: 2023-09-23 20:22
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

public class TradingTypeVo {
    /**
     * @Description 赛事ID
     **/
    private Long matchId;
    /**
     * @Description 球种ID
     **/
    private Long sportId;
    /**
     * @Description 玩法ID
     **/
    private Long playId;
    /**
     * @Description 盘口ID
     **/
    private Long marketId;

    /**
     * @Description 类型：1 ：早盘 ，2： 滚球盘， 3： 冠军盘
     **/
    private Integer matchType;

    //状态 操盘类型
    private Integer tradeType;

    public Integer getMatchType() {
        return matchType;
    }

    public void setMatchType(Integer matchType) {
        this.matchType = matchType;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Long getPlayId() {
        return playId;
    }

    public void setPlayId(Long playId) {
        this.playId = playId;
    }

    public Long getMarketId() {
        return marketId;
    }

    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }

    public Integer getTradeType() {
        return tradeType;
    }

    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }
}
