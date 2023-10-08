package com.panda.sport.rcs.common.vo.api.request;

import com.panda.sport.rcs.common.bean.PageBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value = "规则 列表获取  vo对象", description = "规则 列表获取  vo对象")
public class RuleListReqVo extends PageBean implements Serializable {
    @ApiModelProperty(value = "规则类型 1基本属性类 2投注特征类 3访问特征类 4财务特征类")
    private Integer ruleType;
    @ApiModelProperty(value = "名称")
    private String ruleName;

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
}

