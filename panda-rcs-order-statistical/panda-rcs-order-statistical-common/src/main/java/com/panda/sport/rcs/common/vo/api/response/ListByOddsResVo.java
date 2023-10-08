package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ApiModel(value = "投注偏好详情/财务特征详情-赔率区间  返回vo", description = "")
public class ListByOddsResVo {

    @ApiModelProperty(value = "赔率区间 1对应[1-1.3 )、2对应[1.3-1.5 )、3对应[1.5-2 )、4对应[2,3 )、5对应[3,5 )、6对应[5,10 )、7对应>10")
    private int oddsType;

    @ApiModelProperty(value = "投注金额")
    private BigDecimal betAmount;

    @ApiModelProperty(value = "前端需要的名称")
    private String name;


    private static Map<Integer, String> nameMap = new HashMap<>();

    static {
        ListByOddsResVo.nameMap.put(new Integer(1), "[1,1.3)赔率)");
        ListByOddsResVo.nameMap.put(new Integer(2), "[1.3,1.5)赔率)");
        ListByOddsResVo.nameMap.put(new Integer(3), "[1.5,2)赔率)");
        ListByOddsResVo.nameMap.put(new Integer(4), "[2,3)赔率)");
        ListByOddsResVo.nameMap.put(new Integer(5), "[3,5)赔率)");
        ListByOddsResVo.nameMap.put(new Integer(6), "[5,10)赔率)");
        ListByOddsResVo.nameMap.put(new Integer(7), "赔率>=10");
    }


    public static Set<Integer> getOddTypeSet() {
        return new HashSet<>(nameMap.keySet());
    }


    public int getOddsType() {
        return oddsType;
    }

    public void setOddsType(int oddsType) {
        this.oddsType = oddsType;
    }

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) { 
        this.name = ListByOddsResVo.nameMap.get(new Integer(oddsType));
    }
}
