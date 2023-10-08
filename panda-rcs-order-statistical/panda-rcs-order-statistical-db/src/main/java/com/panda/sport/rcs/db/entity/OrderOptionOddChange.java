package com.panda.sport.rcs.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author dorich
 * @since 2020-07-19
 */
@ApiModel(value="OrderOptionOddChange对象", description="")
public class OrderOptionOddChange implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "表ID，自增")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "注单编号")
    private String betNo;

    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    @ApiModelProperty(value = "投注类型ID.取自订单")
    private Long playOptionsId;

    @ApiModelProperty(value = "订单赔率. 单位: 0.0001")
    private Integer oddsValue;

    @ApiModelProperty(value = "0:赛前盘;1:滚球")
    private Integer orderType;

    @ApiModelProperty(value = "是否已经处理;0:未处理;1:处理")
    private Integer mark;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "订单下注时间")
    private Long betTime;

    @ApiModelProperty(value = "盘口ID")
    private Long marketId;

    public Long getMarketId() {
        return marketId;
    }

    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getBetNo() {
        return betNo;
    }

    public void setBetNo(String betNo) {
        this.betNo = betNo;
    }
    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
    public Long getPlayOptionsId() {
        return playOptionsId;
    }

    public void setPlayOptionsId(Long playOptionsId) {
        this.playOptionsId = playOptionsId;
    }
    public Integer getOddsValue() {
        return oddsValue;
    }

    public void setOddsValue(Integer oddsValue) {
        this.oddsValue = oddsValue;
    }
    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }
    public Integer getMark() {
        return mark;
    }

    public void setMark(Integer mark) {
        this.mark = mark;
    }

    public Long getBetTime() {
        return betTime;
    }

    public void setBetTime(Long betTime) {
        this.betTime = betTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
