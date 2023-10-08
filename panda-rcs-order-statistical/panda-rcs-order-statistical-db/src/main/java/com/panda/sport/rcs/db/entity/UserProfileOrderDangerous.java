package com.panda.sport.rcs.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 注单-危险投注关系表
 * </p>
 *
 * @author lithan auto
 * @since 2020-07-10
 */
@ApiModel(value="UserProfileOrderDangerous对象", description="注单-危险投注关系表")
public class UserProfileOrderDangerous implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "注单编号")
    private String betNo;

    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    @ApiModelProperty(value = "对应的危险规则id ")
    private Long dangerousId;

    @ApiModelProperty(value = "盘口id ")
    private Long marketId;

    @ApiModelProperty(value = "投注项id")
    private Long playOptionsId;

    @ApiModelProperty(value = "创建时间 ")
    private Long createTime;


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
    public Long getDangerousId() {
        return dangerousId;
    }

    public void setDangerousId(Long dangerousId) {
        this.dangerousId = dangerousId;
    }

    public Long getMarketId() {
        return marketId;
    }

    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }

    public Long getPlayOptionsId() {
        return playOptionsId;
    }

    public void setPlayOptionsId(Long playOptionsId) {
        this.playOptionsId = playOptionsId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "UserProfileOrderDangerous{" +
            "id=" + id +
            ", betNo=" + betNo +
            ", orderNo=" + orderNo +
            ", dangerousId=" + dangerousId +
        "}";
    }
}
