package com.panda.sport.rcs.common.vo.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * <p>
 * 标签-规则关系表
 * </p>
 *
 * @author lithan auto
 * @since 2020-06-25
 */
@ApiModel(value="UserProfileTagsRuleRelation 解除绑定关系 Vo对象", description="标签-规则关系表VO")
public class TagsRuleDelReqVo implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "标签id")
    private Integer tagId;

    @ApiModelProperty(value = "规则id")
    private Integer ruleId;

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }
}
