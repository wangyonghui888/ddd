package com.panda.sport.rcs.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 根据IP分组当日内的总投注额与总输赢额
 * </p>
 *
 * @author author
 * @since 2021-02-03
 */
@TableName("risk_order_statistics_day_by_ip")
@ApiModel(value="RiskOrderStatisticsDayByIp对象", description="根据IP分组当日内的总投注额与总输赢额")
public class RiskOrderStatisticsDayByIp implements Serializable {

    private static final long serialVersionUID = 1L;

    public RiskOrderStatisticsDayByIp() {

    }

    @ApiModelProperty(value = "自增ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "IP")
    private String ip;

    @ApiModelProperty(value = "统计时间(yyyyMMdd)")
    private Long staticTime;

    @ApiModelProperty(value = "有效投注")
    private BigDecimal betAmount;

    @ApiModelProperty(value = "输赢金额")
    private BigDecimal profitAmount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getStaticTime() {
        return staticTime;
    }

    public void setStaticTime(Long staticTime) {
        this.staticTime = staticTime;
    }

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }

    public BigDecimal getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(BigDecimal profitAmount) {
        this.profitAmount = profitAmount;
    }
}
