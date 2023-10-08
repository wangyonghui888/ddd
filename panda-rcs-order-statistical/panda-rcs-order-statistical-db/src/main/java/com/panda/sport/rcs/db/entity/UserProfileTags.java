package com.panda.sport.rcs.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * <p>
 * 用户画像标签管理表
 * </p>
 *
 * @author lithan auto
 * @since 2020-06-25
 */
@ApiModel(value="UserProfileTags对象", description="用户画像标签管理表")
public class UserProfileTags implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "标签类型  1基本属性类 2投注特征类 3访问特征类 4财务特征类")
    private Integer tagType;

    @ApiModelProperty(value = "标签名称")
    private String tagName;

    @ApiModelProperty(value = "标签英文名称")
    private String englishTagName;

    @ApiModelProperty(value = "标签说明")
    private String tagDetail;

    @ApiModelProperty(value = "标签颜色")
    private String tagColor;

    @ApiModelProperty(value = "标签图标url")
    private String tagImgUrl;

    @ApiModelProperty(value = "标签复核天数")
    private Integer tagRecheckDays;

    @ApiModelProperty(value = "是否循环复核（0.否 1.是）默认为0否")
    private Integer isRecheck;

    @ApiModelProperty(value = "是否停止计算（0.否 1.是）默认为0否")
    private Integer isCalculate;

    @ApiModelProperty(value = "是否自动化（0.否 1.是）默认为0否")
    private Integer isAuto;

    @ApiModelProperty(value = "是否默认标签（0.否 1.是）默认为0否")
    private Integer isDefault;

    @ApiModelProperty(value = "是否不达标回退至上级标签（0.否 1.是）默认为0否")
    private Integer isRollback;

    @ApiModelProperty(value = "上级标签")
    private Long fatherId;

    @ApiModelProperty(value = "允许风控措施 0否 1是")
    private Integer riskStatus;

    public Integer getRiskStatus() {
        return riskStatus;
    }

    public void setRiskStatus(Integer riskStatus) {
        this.riskStatus = riskStatus;
    }

    public Integer getIsAuto() {
        return isAuto;
    }

    public void setIsAuto(Integer isAuto) {
        this.isAuto = isAuto;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public Integer getIsRollback() {
        return isRollback;
    }

    public void setIsRollback(Integer isRollback) {
        this.isRollback = isRollback;
    }

    public Long getFatherId() {
        return fatherId;
    }

    public void setFatherId(Long fatherId) {
        this.fatherId = fatherId;
    }

    public Integer getTagRecheckDays() {
        return tagRecheckDays;
    }

    public void setTagRecheckDays(Integer tagRecheckDays) {
        this.tagRecheckDays = tagRecheckDays;
    }

    public Integer getIsRecheck() {
        return isRecheck;
    }

    public void setIsRecheck(Integer isRecheck) {
        this.isRecheck = isRecheck;
    }

    public Integer getIsCalculate() {
        return isCalculate;
    }

    public void setIsCalculate(Integer isCalculate) {
        this.isCalculate = isCalculate;
    }

    public String getEnglishTagName() {
        return englishTagName;
    }

    public void setEnglishTagName(String englishTagName) {
        this.englishTagName = englishTagName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTagType() {
        return tagType;
    }

    public void setTagType(Integer tagType) {
        this.tagType = tagType;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagDetail() {
        return tagDetail;
    }

    public void setTagDetail(String tagDetail) {
        this.tagDetail = tagDetail;
    }

    public String getTagColor() {
        return tagColor;
    }

    public void setTagColor(String tagColor) {
        this.tagColor = tagColor;
    }

    public String getTagImgUrl() {
        return tagImgUrl;
    }

    public void setTagImgUrl(String tagImgUrl) {
        this.tagImgUrl = tagImgUrl;
    }
}
