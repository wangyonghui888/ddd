package com.panda.sport.rcs.pojo;


import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * <p>
 * 赛事最大赔付配置
 * </p>
 *
 * @author admin
 * @since 2019-10-04
 */
public class RcsBusinessMatchPaidConfig  {

    private Long id;

    /**
     * 商户id
     */
    private Long businessId;

    /**
     * 体育类型
     */
    private Long sportId;

    /**
     * 联赛级别
     */
    private Long tournamentLevelId;
    /**
     * 联赛级别
     */
    private Integer tournamentLevel;
    /**
     * 联赛别名
     */
    private String tournamentLevelCode;

    /**
     * 单场最大赔付比例  %
     */
    private BigDecimal matchMaxPayRate;

    /**
     * 单场最大赔付
     */
    private BigDecimal matchMaxPayVal;

    /**
     * 单场串关最大赔付值
     */
    private BigDecimal matchMaxConPayVal;

    /**
     * 单场串关最大赔付比例%
     */
    private BigDecimal matchMaxConPayRate;

    private Timestamp crtTime;

    private Timestamp updateTime;

    /**
     * 状态
     */
    private Integer status;


    protected Serializable pkVal() {
        return this.id;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getTournamentLevelId() {
        return tournamentLevelId;
    }

    public void setTournamentLevelId(Long tournamentLevelId) {
        this.tournamentLevelId = tournamentLevelId;
    }

    public Integer getTournamentLevel() {
        return tournamentLevel;
    }

    public void setTournamentLevel(Integer tournamentLevel) {
        this.tournamentLevel = tournamentLevel;
    }

    public String getTournamentLevelCode() {
        return tournamentLevelCode;
    }

    public void setTournamentLevelCode(String tournamentLevelCode) {
        this.tournamentLevelCode = tournamentLevelCode;
    }

    public BigDecimal getMatchMaxPayRate() {
        return matchMaxPayRate;
    }

    public void setMatchMaxPayRate(BigDecimal matchMaxPayRate) {
        this.matchMaxPayRate = matchMaxPayRate;
    }

    public BigDecimal getMatchMaxPayVal() {
        return matchMaxPayVal;
    }

    public void setMatchMaxPayVal(BigDecimal matchMaxPayVal) {
        this.matchMaxPayVal = matchMaxPayVal;
    }

    public BigDecimal getMatchMaxConPayVal() {
        return matchMaxConPayVal;
    }

    public void setMatchMaxConPayVal(BigDecimal matchMaxConPayVal) {
        this.matchMaxConPayVal = matchMaxConPayVal;
    }

    public BigDecimal getMatchMaxConPayRate() {
        return matchMaxConPayRate;
    }

    public void setMatchMaxConPayRate(BigDecimal matchMaxConPayRate) {
        this.matchMaxConPayRate = matchMaxConPayRate;
    }

    public Timestamp getCrtTime() {
        return crtTime;
    }

    public void setCrtTime(Timestamp crtTime) {
        this.crtTime = crtTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
