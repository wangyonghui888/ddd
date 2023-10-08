package com.panda.sport.sdk.vo;


public class RcsMatchOrderAcceptConfig  {
    private Long matchId;

    /**
    * 1 SR 2 BC 3 BG
    */
    private String dataSource;

    /**
    * 最短等待时间
    */
    private Integer minWait;

    /**
    * 最长等待时间
    */
    private Integer maxWait;

    /**
    * 接单模式 0 自动 1 手动
    */
    private Integer mode;

    /**
    * 中场休息 0 关闭 1 开启
    */
    private Integer halfTime;

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public Integer getMinWait() {
        return minWait;
    }

    public void setMinWait(Integer minWait) {
        this.minWait = minWait;
    }

    public Integer getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(Integer maxWait) {
        this.maxWait = maxWait;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public Integer getHalfTime() {
        return halfTime;
    }

    public void setHalfTime(Integer halfTime) {
        this.halfTime = halfTime;
    }
}