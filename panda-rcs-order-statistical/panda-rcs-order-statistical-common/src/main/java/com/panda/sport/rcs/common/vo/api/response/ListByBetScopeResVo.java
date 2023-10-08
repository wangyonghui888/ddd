package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.*;

@ApiModel(value = "投注偏好详情/财务特征详情-投注金额区间   返回vo", description = "")
public class ListByBetScopeResVo {

    @ApiModelProperty(value = "投注金额区间 1对应<1000、2对应1000,2000 )、3对应[2000,5000 )、4对应[5000,10000 )、5对应>10000")
    private int betType;

    private String name;

    @ApiModelProperty(value = "投注金额")
    private BigDecimal betAmount;
 
    public static Set<Integer> getBetTypeSet() {
        return new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5));
    }


    public int getBetType() {
        return betType;
    }

    public void setBetType(int betType) {
        this.betType = betType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(1 == betType) {
            this.name =   "<1000元";
        }
        if(2 == betType) {
            this.name =   "[1000,2000)元";
        }
        if(3 == betType) {
            this.name =   "[2000,5000)元";
        }
        if(4 == betType) {
            this.name =   "[5000,10000)元";
        }
        if(5 == betType) {
            this.name =   ">=10000";
        }
    }

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }
}
