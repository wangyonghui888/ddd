package com.panda.sport.rcs.pojo;


import java.io.Serializable;
import java.math.BigDecimal;


/**
 * <p>
 * 用户最大赔付设置
 * </p>
 *
 * @author admin
 * @since 2019-10-04
 */
public class RcsBusinessUserPaidConfig extends RcsBaseEntity {


    private Long id;

    /**
     * 商户id
     */
    private Long businessId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户单日最大赔付比例 %
     */
    private Integer userDayPayRate;

    /**
     * 用户单日最大赔付
     */
    private BigDecimal userDayPayVal;

    /**
     * 用户单场最大赔付比例 %
     */
    private Integer userMatchPayRate;

    /**
     * 用户单场最大赔付
     */
    private BigDecimal userMatchPayVal;

  /*  private LocalDateTime crtTime;

    private LocalDateTime updateTime;*/

    private Integer status;


    public Serializable pkVal() {
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getUserDayPayRate() {
        return userDayPayRate;
    }

    public void setUserDayPayRate(Integer userDayPayRate) {
        this.userDayPayRate = userDayPayRate;
    }

    public BigDecimal getUserDayPayVal() {
        return userDayPayVal;
    }

    public void setUserDayPayVal(BigDecimal userDayPayVal) {
        this.userDayPayVal = userDayPayVal;
    }

    public Integer getUserMatchPayRate() {
        return userMatchPayRate;
    }

    public void setUserMatchPayRate(Integer userMatchPayRate) {
        this.userMatchPayRate = userMatchPayRate;
    }

    public BigDecimal getUserMatchPayVal() {
        return userMatchPayVal;
    }

    public void setUserMatchPayVal(BigDecimal userMatchPayVal) {
        this.userMatchPayVal = userMatchPayVal;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
