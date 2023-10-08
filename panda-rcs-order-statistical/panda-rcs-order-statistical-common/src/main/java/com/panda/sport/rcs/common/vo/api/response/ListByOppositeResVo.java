package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.*;

@ApiModel(value = "投注偏好详情/财务特征详情-对冲投注   返回vo", description = "")
public class ListByOppositeResVo {

    @ApiModelProperty(value = "对冲投注 1是 0否")
    private int isOpposite;

    @ApiModelProperty(value = "投注金额")
    private BigDecimal betAmount;

    private String name;

    private static Map<Integer, String> nameMap = new HashMap<>();

    static {
        ListByOppositeResVo.nameMap.put(new Integer(0), "否");
        ListByOppositeResVo.nameMap.put(new Integer(1), "是");
    }


    public static Set<Integer> getOppositeTypes() {
        return new HashSet<Integer>(Arrays.asList(0,1));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if( 0 == isOpposite) {
            this.name = "否";
        }
        if( 1 == isOpposite) {
            this.name = "是";
        }
    }



    public int getIsOpposite() {
        return isOpposite;
    }

    public void setIsOpposite(int isOpposite) {
        this.isOpposite = isOpposite;
    }

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }
}
