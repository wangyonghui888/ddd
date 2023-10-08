package com.panda.sport.rcs.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 用户行为详情-投注偏好/财务特征-日统计表
 * </p>
 *
 * @author lithan auto
 * @since 2020-06-21
 */
@ApiModel(value="UserSpecialStatis对象", description="用户行为详情-投注偏好/财务特征-日统计表")
@TableName("user_special_statis")
public class UserSpecialStatis implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "统计日期")
    private Long statisDay;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "父统计类型:1球类,2联赛,3玩法,4球队,5盘口类型,6赔率,7投注金额,8正副盘,9对冲投注")
    private Integer parentType;

    @ApiModelProperty(value = "子统计类型(分组字段): 父1 :存sportId  值1表示 球类下面的足球 , 父2 :存联赛id    父3 :存sportid_playid(此字段待定),  父4 :存球队id,  父5 :存盘口类型,  父6 :1对应[1-1.3 )、2对应[1.3-1.5 )、3对应[1.5-2 )、4对应[2,3 )、5对应[3,5 )、6对应[5,10 )、7对应>10,  父7 :1对应<1000、2对应1000,2000 )、3对应[2000,5000 )、4对应[5000,10000 )、5对应>10000,  父8 :0正盘,1副盘,  父9 :1是 0否,  ")
    private String childType;

    @ApiModelProperty(value = "值:投注金额(仅在parent_type为4时,表示球队次数)")
    private Long value;

    @ApiModelProperty(value = "投注金额(结算成功)")
    @TableField(value="finance_value")
    private BigDecimal financeValue;

    @ApiModelProperty(value = "盈利金额")
    private Long profit;

    @ApiModelProperty(value = "投注总笔数")
    private Long betNum;

    @ApiModelProperty(value = "盈利投注笔数")
    private Long betProfitNum;

    /**
     * 赢的盘数
     */
    private Long winBetNum;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStatisDay() {
        return statisDay;
    }

    public void setStatisDay(Long statisDay) {
        this.statisDay = statisDay;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getParentType() {
        return parentType;
    }

    public void setParentType(Integer parentType) {
        this.parentType = parentType;
    }

    public String getChildType() {
        return childType;
    }

    public void setChildType(String childType) {
        this.childType = childType;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public BigDecimal getFinanceValue() {
        return financeValue;
    }

    public void setFinanceValue(BigDecimal financeValue) {
        this.financeValue = financeValue;
    }

    public Long getProfit() {
        return profit;
    }

    public void setProfit(Long profit) {
        this.profit = profit;
    }

    public Long getBetNum() {
        return betNum;
    }

    public void setBetNum(Long betNum) {
        this.betNum = betNum;
    }

    public Long getBetProfitNum() {
        return betProfitNum;
    }

    public void setBetProfitNum(Long betProfitNum) {
        this.betProfitNum = betProfitNum;
    }

    public Long getWinBetNum() {
        return winBetNum;
    }

    public void setWinBetNum(Long winBetNum) {
        this.winBetNum = winBetNum;
    }
}
