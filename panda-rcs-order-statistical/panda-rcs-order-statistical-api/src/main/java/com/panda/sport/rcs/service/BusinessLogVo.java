package com.panda.sport.rcs.service;

import com.panda.sport.rcs.common.vo.api.request.TagsGroupRuleSaveReqVo;
import com.panda.sport.rcs.common.vo.api.request.TagsRuleSaveReqVo;
import com.panda.sport.rcs.db.entity.UserProfileRule;
import com.panda.sport.rcs.db.entity.UserProfileTags;
import com.panda.sport.rcs.db.entity.UserProfileTagsGroupRuleRelation;
import com.panda.sport.rcs.db.entity.UserProfileTagsRuleRelation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用日志对象
 * */
public class BusinessLogVo implements Serializable {

    public BusinessLogVo(UserProfileTags oldUserProfileTags, UserProfileTags newUserProfileTags
            , List<UserProfileTagsRuleRelation> oldTagsRuleRelations, List<TagsRuleSaveReqVo> newTagsRuleRelations
            , List<UserProfileTagsGroupRuleRelation> oldUserProfileTagsGroupRuleRelations, List<TagsGroupRuleSaveReqVo> tagsGroupRuleSaveReqVos
            , String userId, List<UserProfileTags> userProfileTags,UserProfileTags defaultTagEntity,List<UserProfileRule> userProfileRules
      ){
        this.newUserProfileTags = newUserProfileTags;
        this.oldUserProfileTags = oldUserProfileTags;
        this.oldTagsRuleRelations=oldTagsRuleRelations;
        this.newTagsRuleRelations =newTagsRuleRelations;
        this.oldUserProfileTagsGroupRuleRelations=oldUserProfileTagsGroupRuleRelations;
        this.tagsGroupRuleSaveReqVos =tagsGroupRuleSaveReqVos;
        this.userId=userId;
        this.userProfileTags =userProfileTags;
        this.defaultTagEntity=defaultTagEntity;
        this.userProfileRules = userProfileRules;
    }
    /**
     * 旧数据
     */
    private UserProfileTags oldUserProfileTags;
    /**
     * 新数据
     * */
    private UserProfileTags newUserProfileTags;

    private String userId;

    private List<UserProfileTags> userProfileTags;

    /**
     * 取消默认标签
     * */
    private UserProfileTags defaultTagEntity;

    /***
     * 原来数组oldTagsRuleRelations
     */

    private List<UserProfileTagsRuleRelation> oldTagsRuleRelations=new ArrayList();
    /**
     *新数据oldTagsRuleRelations
     */
    private List<TagsRuleSaveReqVo> newTagsRuleRelations=new ArrayList();


    /***
     * 原来数组oldUserProfileTagsGroupRuleRelations
     */

    private List<UserProfileTagsGroupRuleRelation> oldUserProfileTagsGroupRuleRelations=new ArrayList();
    /**
     *新数据newUserProfileTagsGroupRuleRelations
     */
    private List<TagsGroupRuleSaveReqVo> tagsGroupRuleSaveReqVos=new ArrayList();

    private List<UserProfileRule> userProfileRules;

    public List<UserProfileTagsRuleRelation> getOldTagsRuleRelations() {
        return this.oldTagsRuleRelations;
    }

    public List<TagsRuleSaveReqVo> getNewTagsRuleRelations() {
        return this.newTagsRuleRelations;
    }

    public List<TagsGroupRuleSaveReqVo> getTagsGroupRuleSaveReqVos() {
        return this.tagsGroupRuleSaveReqVos;
    }
    public List<UserProfileTagsGroupRuleRelation> getOldUserProfileTagsGroupRuleRelations() {
        return this.oldUserProfileTagsGroupRuleRelations;
    }
    public List<UserProfileRule> getUserProfileRules() {
        return this.userProfileRules;
    }
    public String getUserId() {
        return this.userId;
    }
    public UserProfileTags getOldUserProfileTags() {
       return this.oldUserProfileTags;
    }
    public UserProfileTags getNewUserProfileTags() {
        return this.newUserProfileTags;
    }

    public UserProfileTags getDefaultTagEntity() {
        return this.defaultTagEntity;
    }

    public List<UserProfileTags>  getUserProfileTags() {
        return this.userProfileTags;
    }
}
