package com.panda.sport.rcs.common.vo.rule;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

import java.util.UUID;

/**
 * 规则计算  参数 Vo
 *
 * @author lithan
 * @date 2020-06-28 13:24:45
 */
public class RuleParameterVo {

    public RuleParameterVo(){
        setRequestId(UUID.randomUUID().toString().replace("-", ""));
    }

    public String requestId;

    @ApiModelProperty(value = "用户id")
    public Long userId;

    @ApiModelProperty(value = "标签id")
    public Long tagId;

    @ApiModelProperty(value = "统计时间依据")
    public Long time;

    @ApiModelProperty(value = "参数1")
    private String parameter1;

    @ApiModelProperty(value = "参数2")
    private String parameter2;

    @ApiModelProperty(value = "参数3")
    private String parameter3;

    @ApiModelProperty(value = "参数4")
    private String parameter4;

    @ApiModelProperty(value = "参数5")
    private String parameter5;

    @ApiModelProperty(value = "参数6")
    private String parameter6;

    @ApiModelProperty(value = "规则code")
    private String ruleCode;

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

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

    public String getParameter5() {
        return parameter5;
    }

    public void setParameter5(String parameter5) {
        this.parameter5 = parameter5;
    }

    public String getParameter6() {
        return parameter6;
    }

    public void setParameter6(String parameter6) {
        this.parameter6 = parameter6;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }
}
