package com.panda.sport.rcs.common.vo.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 危险投注表
 * </p>
 *
 * @author lithan auto
 * @since 2020-06-24
 */
@ApiModel(value = "UserProfileDangerousBetRule新增VO", description = "UserProfileDangerousBetRule新增VO")
public class UserProfileDangerousBetRuleAddReqVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "危险代码 唯一")
//    @NotBlank
    private String dangerousCode;

    @ApiModelProperty(value = "所属球类 0全部  非0表示对应的体育id")
    private Integer sportId;

    @ApiModelProperty(value = "名称")
    @NotNull
    private String ruleName;


    @ApiModelProperty(value = "英文名称")
    @NotNull
    private String englishRuleName;

    public String getEnglishRuleName() {
        return englishRuleName;
    }

    public void setEnglishRuleName(String englishRuleName) {
        this.englishRuleName = englishRuleName;
    }

    @ApiModelProperty(value = "定义")
    @NotNull
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
