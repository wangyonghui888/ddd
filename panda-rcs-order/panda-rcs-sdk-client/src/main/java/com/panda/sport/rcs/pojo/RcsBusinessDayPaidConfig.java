package com.panda.sport.rcs.pojo;



import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 商户单日最大赔付
 * </p>
 *
 * @author admin
 * @since 2019-10-04
 */
public class RcsBusinessDayPaidConfig   {

    private Long businessId;

    private String businessName;

    /**
     * 制动比例  %
     */
    private Integer stopRate;

    /**
     * 制动值
     */
    private BigDecimal stopVal;

    /**
     * 高危 单位 %
     */
    private Integer warnLevel1Rate;

    /**
     * 高危值
     */
    private BigDecimal warnLevel1Val;

    /**
     * 危险  单位 %
     */
    private Integer warnLevel2Rate;

    /**
     * 危险值
     */
    private BigDecimal warnLevel2Val;

    private Date crtTime;

    private Date updateTime;

    private long expireTime;

    /**
     * 状态
     */
    private Integer status;


    protected Serializable pkVal() {
        return this.businessId;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Integer getStopRate() {
        return stopRate;
    }

    public void setStopRate(Integer stopRate) {
        this.stopRate = stopRate;
    }

    public BigDecimal getStopVal() {
        return stopVal;
    }

    public void setStopVal(BigDecimal stopVal) {
        this.stopVal = stopVal;
    }

    public Integer getWarnLevel1Rate() {
        return warnLevel1Rate;
    }

    public void setWarnLevel1Rate(Integer warnLevel1Rate) {
        this.warnLevel1Rate = warnLevel1Rate;
    }

    public BigDecimal getWarnLevel1Val() {
        return warnLevel1Val;
    }

    public void setWarnLevel1Val(BigDecimal warnLevel1Val) {
        this.warnLevel1Val = warnLevel1Val;
    }

    public Integer getWarnLevel2Rate() {
        return warnLevel2Rate;
    }

    public void setWarnLevel2Rate(Integer warnLevel2Rate) {
        this.warnLevel2Rate = warnLevel2Rate;
    }

    public BigDecimal getWarnLevel2Val() {
        return warnLevel2Val;
    }

    public void setWarnLevel2Val(BigDecimal warnLevel2Val) {
        this.warnLevel2Val = warnLevel2Val;
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

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
