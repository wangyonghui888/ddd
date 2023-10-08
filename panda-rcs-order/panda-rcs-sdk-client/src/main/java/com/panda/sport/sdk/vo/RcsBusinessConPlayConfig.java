package com.panda.sport.sdk.vo;



import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.pojo
 * @Description :  TODO
 * @Date: 2019-11-22 16:29
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */


public class RcsBusinessConPlayConfig {
    private Integer id;

    /**
     * 商户Id
     */
    private Long businessId;

    /**
     * 1-单注串关最大赔付值
     * 2-单注串关最低投注额
     * 3-单注串关限额占单关限额比例
     */
    private Integer playType;
    /**
     * 设置值
     */
    private BigDecimal playValue;
    /**
     * 百分比
     */
    private BigDecimal playRate;

//    private Timestamp crtTime;
//
//    private Timestamp updateTime;

    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public Integer getPlayType() {
        return playType;
    }

    public void setPlayType(Integer playType) {
        this.playType = playType;
    }

    public BigDecimal getPlayValue() {
        return playValue;
    }

    public void setPlayValue(BigDecimal playValue) {
        this.playValue = playValue;
    }

    public BigDecimal getPlayRate() {
        return playRate;
    }

    public void setPlayRate(BigDecimal playRate) {
        this.playRate = playRate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}