package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 标签-规则关系表
 * </p>
 *
 * @author lithan auto
 * @since 2020-06-25
 */
@ApiModel(value="UserProfileTagsRuleRelation Vo对象", description="标签-规则关系表VO")
public class GetRuleByTagIdResVo implements Serializable {

    @ApiModelProperty(value = "独立规则")
    List<UserProfileTagsRuleRelationResVo> ruleRelationResVos;

    @ApiModelProperty(value = "组合规则")
    List<List<UserProfileTagsGroupRuleRelationResVo>> groupRuleRelationResVos;

    public List<UserProfileTagsRuleRelationResVo> getRuleRelationResVos() {
        return ruleRelationResVos;
    }

    public void setRuleRelationResVos(List<UserProfileTagsRuleRelationResVo> ruleRelationResVos) {
        this.ruleRelationResVos = ruleRelationResVos;
    }

    public List<List<UserProfileTagsGroupRuleRelationResVo>> getGroupRuleRelationResVos() {
        return groupRuleRelationResVos;
    }

    public void setGroupRuleRelationResVos(List<List<UserProfileTagsGroupRuleRelationResVo>> groupRuleRelationResVos) {
        this.groupRuleRelationResVos = groupRuleRelationResVos;
    }
}
