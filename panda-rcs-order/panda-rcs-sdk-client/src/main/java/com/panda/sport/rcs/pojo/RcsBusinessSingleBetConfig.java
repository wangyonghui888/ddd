package com.panda.sport.rcs.pojo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.pojo
 * @Description  :  TODO
 * @author       :  Administrator
 * @Date:  2019-11-22 15:04
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
*/
public class RcsBusinessSingleBetConfig {
    private String id;

    /**
     * 商户ID
     */
    private Long businessId;

    /**
     * 体育类型 standard_sport_type .id
     */
    private Long sportId;

    /**
     * 投注阶段  未开赛 和 滚球  0:赛前 1:滚球
     */
    private Integer matchType;

    /**
     * 所属时段  system_item_dict.value
     */
    private Integer timePeriod;

    /**
     * 玩法名称编码 id standard_sport_market_category
     */
    private Integer playId;

    /**
     * 联赛级别
     */
    private Integer tournamentLevel;


    /**
     * 单注最大下注额
     */
    private BigDecimal orderMaxValue;

    /**
     * 百分比
     */
    private BigDecimal orderMaxRate;

    private Date crtTime;

    private Date updateTime;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 排序設置
     */
    private Integer orderNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Integer getMatchType() {
        return matchType;
    }

    public void setMatchType(Integer matchType) {
        this.matchType = matchType;
    }

    public Integer getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(Integer timePeriod) {
        this.timePeriod = timePeriod;
    }

    public Integer getPlayId() {
        return playId;
    }

    public void setPlayId(Integer playId) {
        this.playId = playId;
    }

    public Integer getTournamentLevel() {
        return tournamentLevel;
    }

    public void setTournamentLevel(Integer tournamentLevel) {
        this.tournamentLevel = tournamentLevel;
    }

    public BigDecimal getOrderMaxValue() {
        return orderMaxValue;
    }

    public void setOrderMaxValue(BigDecimal orderMaxValue) {
        this.orderMaxValue = orderMaxValue;
    }

    public BigDecimal getOrderMaxRate() {
        return orderMaxRate;
    }

    public void setOrderMaxRate(BigDecimal orderMaxRate) {
        this.orderMaxRate = orderMaxRate;
    }

    public Date getCrtTime() {
        return crtTime;
    }

    public void setCrtTime(Date crtTime) {
        this.crtTime = crtTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }
}