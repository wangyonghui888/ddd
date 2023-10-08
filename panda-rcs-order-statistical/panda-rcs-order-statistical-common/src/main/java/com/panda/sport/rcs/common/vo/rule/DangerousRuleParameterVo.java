package com.panda.sport.rcs.common.vo.rule;

import io.swagger.annotations.ApiModelProperty;

/**
 * 危险投注 规则计算  参数 Vo
 *
 * @author lithan
 * @date 2020-07-10 09:08:38
 */
public class DangerousRuleParameterVo {
    @ApiModelProperty(value = "用户id")
    public Long userId;

    @ApiModelProperty(value = "注单")
    public OrderDetailVo orderDetailVo;

    @ApiModelProperty(value = "参数1")
    private String parameter1;

    @ApiModelProperty(value = "参数2")
    private String parameter2;

    @ApiModelProperty(value = "参数3")
    private String parameter3;

    @ApiModelProperty(value = "参数4")
    private String parameter4;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    public String getParameter3() {
        return parameter3;
    }

    public void setParameter3(String parameter3) {
        this.parameter3 = parameter3;
    }

    public String getParameter4() {
        return parameter4;
    }

    public void setParameter4(String parameter4) {
        this.parameter4 = parameter4;
    }

    public OrderDetailVo getOrderDetailVo() {
        return orderDetailVo;
    }

    public void setOrderDetailVo(OrderDetailVo orderDetailVo) {
        this.orderDetailVo = orderDetailVo;
    }
}
