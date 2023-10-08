package com.panda.sport.rcs.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 玩家组货量百分比表
 * </p>
 *
 * @author Kir
 * @since 2021-08-20
 */
@ApiModel(value="TUserGroupBetRate对象", description="玩家组货量百分比表")
public class TUserGroupBetRate implements Serializable  {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "玩家组id")
    private Long groupId;

    @ApiModelProperty(value = "赛种id")
    private Integer sportId;

    @ApiModelProperty(value = "货量百分比（3位整数2位小数）")
    private BigDecimal betRate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Integer getSportId() {
        return sportId;
    }

    public void setSportId(Integer sportId) {
        this.sportId = sportId;
    }

    public BigDecimal getBetRate() {
        return betRate;
    }

    public void setBetRate(BigDecimal betRate) {
        this.betRate = betRate;
    }
}
