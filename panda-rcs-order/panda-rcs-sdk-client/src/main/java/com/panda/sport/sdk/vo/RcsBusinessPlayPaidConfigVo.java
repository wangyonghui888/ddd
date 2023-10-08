package com.panda.sport.sdk.vo;



import java.io.Serializable;


/**
 * <p>
 * 玩法维度配置
 * </p>
 *
 * @author holly
 * @since 2019-10-04
 */
public class RcsBusinessPlayPaidConfigVo implements  Serializable{

    /**
     * 最高单注赔付
     */
    private Long playId;
    /**
     * 最高单注赔付
     */
    private Long orderMaxPay;

    /**
     * 用户最高玩法赔付
     */
    private Long playMaxPay;

    /**
     * 玩法类型 全场 3，上半场 1，下半场 2，0-15分钟 4
     */
    private Long playType;

    /**
     * 用户最低玩法赔付
     */
    private Long minBet;
    
    /**
     * 复式串关，拆分类型
     */
    private String type;
    
    /**
     * 串关   验证是否通过
     */
    private Boolean isPass;
    
    /**
     * 串关   不通过原因
     */
    private String errorMsg;

    public Long getPlayId() {
        return playId;
    }

    public void setPlayId(Long playId) {
        this.playId = playId;
    }

    public Long getOrderMaxPay() {
        return orderMaxPay;
    }

    public void setOrderMaxPay(Long orderMaxPay) {
        this.orderMaxPay = orderMaxPay;
    }

    public Long getPlayMaxPay() {
        return playMaxPay;
    }

    public void setPlayMaxPay(Long playMaxPay) {
        this.playMaxPay = playMaxPay;
    }

    public Long getPlayType() {
        return playType;
    }

    public void setPlayType(Long playType) {
        this.playType = playType;
    }

    public Long getMinBet() {
        return minBet;
    }

    public void setMinBet(Long minBet) {
        this.minBet = minBet;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getPass() {
        return isPass;
    }

    public void setPass(Boolean pass) {
        isPass = pass;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
