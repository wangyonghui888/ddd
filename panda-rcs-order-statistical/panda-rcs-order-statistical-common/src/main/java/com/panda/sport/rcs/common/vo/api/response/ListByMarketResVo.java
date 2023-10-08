package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ApiModel(value = "投注偏好详情/财务特征详情-盘口类型返回vo", description = "")
public class ListByMarketResVo {

    @ApiModelProperty(value = "盘口类型(OU:欧盘 HK:香港盘 US:美式盘 ID:印尼盘 MY:马来盘 GB:英式盘）")
    private String market;

    @ApiModelProperty(value = "盘口类型(OU:欧盘 HK:香港盘 US:美式盘 ID:印尼盘 MY:马来盘 GB:英式盘）")
    private String name;
    
    @ApiModelProperty(value = "投注金额")
    private BigDecimal betAmount;

    private static Map<String, String> nameMap = new HashMap<>();

    static {
        ListByMarketResVo.nameMap.put("EU", "欧盘");
        ListByMarketResVo.nameMap.put("HK", "香港盘");
        ListByMarketResVo.nameMap.put("US", "美式盘");
        ListByMarketResVo.nameMap.put("ID", "印尼盘");
        ListByMarketResVo.nameMap.put("MY", "马来盘");
        ListByMarketResVo.nameMap.put("UK", "英式盘");
    }

    public static Set<String> getMarketTypes() {
        return new HashSet<>(nameMap.keySet());
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
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
        this.name = ListByMarketResVo.nameMap.get(market);
    }
}
