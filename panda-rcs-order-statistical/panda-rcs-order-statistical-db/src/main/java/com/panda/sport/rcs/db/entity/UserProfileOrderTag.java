package com.panda.sport.rcs.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * <p>
 * 订单标签表,标识订单部分特殊字段
 * </p>
 *
 * @author lithan auto
 * @since 2020-06-25
 */
@ApiModel(value="UserProfileOrderTag对象", description="订单标签表,标识订单部分特殊字段")
public class UserProfileOrderTag implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "注单编号")
    private String betNo;

    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    @ApiModelProperty(value = "是否对冲投注 0否 1是")
    private Integer isInverse;

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

    public Integer getIsInverse() {
        return isInverse;
    }

    public void setIsInverse(Integer isInverse) {
        this.isInverse = isInverse;
    }
}
