package com.panda.sport.rcs.common.vo.api.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@ApiModel(value = "   危险投注  返回vo", description = "")
public class DangerousListResVo {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    @ApiModelProperty(value = "代码")
    @NotNull
    private String dangerousCode;

    @ApiModelProperty(value = "所属球类 0全部  非0表示对应的体育id")
    private Integer sportId;


    @ApiModelProperty(value = "运动种类名称")
    private String sportName;


    @ApiModelProperty(value = "名称")
    private String ruleName;

    @ApiModelProperty(value = "英文名称")
    private String englishRuleName;

    public String getEnglishRuleName() {
        return englishRuleName;
    }

    public void setEnglishRuleName(String englishRuleName) {
        this.englishRuleName = englishRuleName;
    }

    @ApiModelProperty(value = "定义")
    private String ruleDetail;

    @ApiModelProperty(value = "参数1")
    private String parameter1;

    @ApiModelProperty(value = "参数2")
    private String parameter2;

    @ApiModelProperty(value = "参数3")
    private String parameter3;

    @ApiModelProperty(value = "参数4")
    private String parameter4;

    @ApiModelProperty(value = "是否启用 0否 1是")
    private Integer enable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDangerousCode() {
        return dangerousCode;
    }

    public void setDangerousCode(String dangerousCode) {
        this.dangerousCode = dangerousCode;
    }

    public Integer getSportId() {
        return sportId;
    }

    public void setSportId(Integer sportId) {
        this.sportId = sportId;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
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

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }
}
