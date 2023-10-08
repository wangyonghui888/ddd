package com.panda.sport.rcs.common.vo.api.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.panda.sport.rcs.common.constants.Constants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(value = "规则  返回vo", description = "")
public class RuleListResVo {


    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "规则代码")
    private String ruleCode;

    @ApiModelProperty(value = "规则类型 1基本属性类 2投注特征类 3访问特征类 4财务特征类")
    private Integer ruleType;

    @ApiModelProperty(value = "规则名")
    private String ruleName;

    @ApiModelProperty(value = "规则说明")
    private String ruleDetail;

    @ApiModelProperty(value = "参数1")
    private String parameter1;

    @ApiModelProperty(value = "参数2")
    private String parameter2;

    @ApiModelProperty(value = "参数3")
    private String parameter3;

    @ApiModelProperty(value = "参数4")
    private String parameter4;

    @ApiModelProperty(value = "规则参数5")
    private String parameter5;

    @ApiModelProperty(value = "规则参数6")
    private String parameter6;

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

    public void setRuleTypeName(String ruleTypeName) {
        this.ruleTypeName = ruleTypeName;
    }

    //**********add*********//
    @ApiModelProperty(value = "规则类型名")
    private String ruleTypeName;

    public String getRuleTypeName() {
        return Constants.ruleTypeMap.get(ruleType);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

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

    public String getRuleDetail() {
        return ruleDetail;
    }

    public void setRuleDetail(String ruleDetail) {
        this.ruleDetail = ruleDetail;
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
}

