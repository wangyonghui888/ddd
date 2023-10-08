package com.panda.sport.rcs.service;

import com.panda.sport.rcs.common.vo.RcsQuotaBusinessLimitLog;
import com.panda.sport.rcs.common.vo.api.request.TagsGroupRuleSaveReqVo;
import com.panda.sport.rcs.common.vo.api.request.TagsRuleSaveReqVo;
import com.panda.sport.rcs.db.entity.UserProfileRule;
import com.panda.sport.rcs.db.entity.UserProfileTags;
import com.panda.sport.rcs.db.entity.UserProfileTagsGroupRuleRelation;
import com.panda.sport.rcs.db.entity.UserProfileTagsRuleRelation;
import com.panda.sport.rcs.db.service.IUserProfileRuleService;
import com.panda.sport.rcs.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * 标签管理日志清洗
 * */
@Slf4j
public class BusinessLogService  implements Callable<List<RcsQuotaBusinessLimitLog>> {
    private final String logTitle="标签管理";
    private  final  String isYes="是";
    private  final  String isNo="否";
    private BusinessLogVo businessLogVo;
    public BusinessLogService(BusinessLogVo businessLogVo){
        this.businessLogVo =businessLogVo;
    }
    /**
     * 多线程风控处理
     * */
    @Override
    public List<RcsQuotaBusinessLimitLog> call()  {
        List<RcsQuotaBusinessLimitLog> finalList=new ArrayList<>();
        try{
        addUserProfileTagsLog(businessLogVo.getNewUserProfileTags(),businessLogVo.getOldUserProfileTags()).forEach(it->{
            finalList.add(it);
        });
        log.info("标签管理->标签更改记录{}",finalList.size());
        businessLogVo.getNewTagsRuleRelations().forEach(item->{
            UserProfileTagsRuleRelation oldTagsRuleRelation=businessLogVo.getOldTagsRuleRelations().stream().filter(t->t.getTagId().equals(item.getTagId()) && t.getRuleId().equals(item.getRuleId())).findFirst().orElse(null);
            if(Objects.nonNull(oldTagsRuleRelation)){
                setTagsRuleRelationLog(item,oldTagsRuleRelation).forEach(e->{
                    finalList.add(e);
                });
            }else{
                setTagsRuleRelationLog(item).forEach(e->{
                     finalList.add(e);
                });
            }
        });
        log.info("标签管理->独立标签更改记录{}",finalList.size());
        businessLogVo.getTagsGroupRuleSaveReqVos().forEach(item->{
            UserProfileTagsGroupRuleRelation oldTagsGroupRuleRelation=businessLogVo.getOldUserProfileTagsGroupRuleRelations().stream().filter(t->t.getTagId().equals(item.getTagId()) && t.getRuleId().equals(item.getRuleId())).findFirst().orElse(null);
            if(Objects.nonNull(oldTagsGroupRuleRelation)){
                setTagsGroupRelationLog(item,oldTagsGroupRuleRelation).forEach(e->{
                    finalList.add(e);
                });
            }else{
               setTagsGroupRelationLog(item).forEach(e->{
                    finalList.add(e);
               });;
            }
        });
        log.info("标签管理->组合标签更改记录{}",finalList.size());
        businessLogVo.getOldUserProfileTagsGroupRuleRelations().forEach(it->{
           TagsGroupRuleSaveReqVo tagsGroupRuleSaveReqVo = businessLogVo.getTagsGroupRuleSaveReqVos().stream().filter(t->t.getTagId().equals(it.getTagId()) && t.getRuleId().equals(it.getRuleId())).findFirst().orElse(null);
           if(Objects.isNull(tagsGroupRuleSaveReqVo)){
               delTagsGroupRelationLog(it).forEach(l->{
                    finalList.add(l);
               });
           }
        });
        log.info("标签管理->组合标签删除记录{}",finalList.size());
        businessLogVo.getOldTagsRuleRelations().forEach(it->{
            TagsRuleSaveReqVo tagsRuleSaveReqVo = businessLogVo.getNewTagsRuleRelations().stream().filter(t->t.getTagId().equals(it.getTagId()) && t.getRuleId().equals(it.getRuleId())).findFirst().orElse(null);
                if(Objects.isNull(tagsRuleSaveReqVo)){
                    delTagsRelationLog(it).forEach(l->{
                            finalList.add(l);
                    });
                }
            });
            log.info("标签管理->独立标签删除记录{}",finalList.size());
        }catch (Exception e){
            log.error("标签管理->风控措施管理处理异常",e);
        }
        return finalList;
    }
    /**
     * 标签操作
     * */
    private List<RcsQuotaBusinessLimitLog> addUserProfileTagsLog(UserProfileTags news,UserProfileTags old)  {
        List<RcsQuotaBusinessLimitLog> list;
        if (Objects.nonNull(old)){
            list=setUserProfileTagsLog(news,old);
        } else {
            list=setUserProfileTagsLog(news);
        }
        return list;
    }
    /**
     * 修改标签
     * */
    private List<RcsQuotaBusinessLimitLog> setUserProfileTagsLog(UserProfileTags news,UserProfileTags old)  {
        List<RcsQuotaBusinessLimitLog> list=new ArrayList<>();
        if(Objects.nonNull(news.getTagType()) && news.getTagType() != old.getTagType()){
            String beforeVal=Constants.getTagTypeByValue(old.getTagType());
            String afterVal=Constants.getTagTypeByValue(news.getTagType());
            list.add(setTagRuleRelationLog(news, Constants.TagType,beforeVal,afterVal));
        }
        if(Objects.nonNull(news.getEnglishTagName()) && !news.getEnglishTagName().equals(old.getEnglishTagName())){
            list.add(setTagRuleRelationLog(news,Constants.EnglishTagName,old.getEnglishTagName(),news.getEnglishTagName()));
        }
        if(!news.getTagName().equals(old.getTagName())){
            list.add(setTagRuleRelationLog(news,Constants.TagName,old.getTagName(),news.getTagName()));
        }
        if(news.getTagRecheckDays()!= old.getTagRecheckDays()){
            String afterVal="";
            if(Objects.nonNull(news.getTagRecheckDays())){
                afterVal=news.getTagRecheckDays()+"";
            }
            String beforeVal="";
            if(Objects.nonNull(old.getTagRecheckDays())){
                beforeVal=old.getTagRecheckDays()+"";
            }
            list.add(setTagRuleRelationLog(news,Constants.TagRecheckDays,beforeVal,afterVal));
        }
        if (news.getIsRecheck() != old.getIsRecheck()){
            String beforeVal = old.getIsRecheck() ==null ? isNo: old.getIsRecheck() == 0 ? isNo : isYes;
            String afterVal = news.getIsRecheck() ==null ? isNo: news.getIsRecheck() == 0 ?isNo: isYes;
            list.add(setTagRuleRelationLog(news,Constants.IsRecheck,beforeVal,afterVal));
        }
        if (news.getIsCalculate() != old.getIsCalculate()){
            String beforeVal = old.getIsCalculate() ==null ? isNo: old.getIsCalculate() == 0 ?isNo:isYes;
            String afterVal = news.getIsCalculate() ==null ? isNo: news.getIsCalculate() == 0 ?isNo:isYes;
            list.add(setTagRuleRelationLog(news,Constants.IsCalculate,beforeVal,afterVal));
        }
        if (!news.getTagDetail().equals(old.getTagDetail())){

            list.add(setTagRuleRelationLog(news,Constants.TagDetail,old.getTagDetail(),news.getTagDetail()));
        }
        if(Objects.nonNull(news.getTagColor())){
            if(Objects.isNull(old.getTagColor())){
                list.add(setTagRuleRelationLog(news,Constants.TagColor,"",news.getTagColor()));
            }else{
                if (!news.getTagColor().equals(old.getTagColor())){
                    list.add(setTagRuleRelationLog(news,Constants.TagColor,old.getTagColor(),news.getTagColor()));
                }
            }
        }
        if(StringUtils.isNotEmpty(news.getTagImgUrl())){
            if(Objects.isNull(old.getTagImgUrl())){
                list.add(setTagRuleRelationLog(news,Constants.TagImgUrl,"",news.getTagImgUrl()));
            }else{
                if (!news.getTagImgUrl().equals(old.getTagImgUrl())){
                    list.add(setTagRuleRelationLog(news,Constants.TagImgUrl,old.getTagImgUrl(),news.getTagImgUrl()));
                }
            }
        }
        if (news.getIsAuto() != old.getIsAuto()){
            String beforeVal = old.getIsAuto() ==null ? isNo: old.getIsAuto() == 0 ?isNo:isYes;
            String afterVal = news.getIsAuto() ==null ? isNo: news.getIsAuto() == 0 ?isNo:isYes;
            list.add(setTagRuleRelationLog(news,Constants.IsAuto,beforeVal,afterVal));
        }
        if(!news.getIsDefault().equals(old.getIsDefault()) && Objects.nonNull(news.getIsDefault()) &&  1 == news.getIsDefault()){
            String beforeVal = isNo;
            String afterVal  =isYes;
            list.add(setTagRuleRelationLog(news,Constants.IsDefault,beforeVal,afterVal));
            list.add(setTagRuleRelationLog(businessLogVo.getDefaultTagEntity(),Constants.IsDefault,isYes,isNo));
        }
        if (news.getIsRollback() != old.getIsRollback()){
            String beforeVal = old.getIsRollback() ==null ? isNo: old.getIsRollback() == 0 ?isNo:isYes;
            String afterVal = news.getIsRollback() ==null ? isNo: news.getIsRollback() == 0 ?isNo:isYes;
            list.add(setTagRuleRelationLog(news,Constants.IsRollback,beforeVal,afterVal));
        }
        if (!news.getFatherId().equals(old.getFatherId())){
            String beforeVal="";
            String afterVal="";
            UserProfileTags oldUserTags=businessLogVo.getUserProfileTags().stream().filter(item->item.getId().equals(old.getFatherId())).findFirst().orElse(null);
            if(Objects.nonNull(oldUserTags)){
                beforeVal=oldUserTags.getTagName();
            }
            UserProfileTags newUserTags=businessLogVo.getUserProfileTags().stream().filter(item->item.getId().equals(news.getFatherId())).findFirst().orElse(null);
            if(Objects.nonNull(newUserTags)){
                afterVal=newUserTags.getTagName();
            }
            if(!beforeVal.equals(afterVal)) {
                list.add(setTagRuleRelationLog(news, Constants.FatherId, beforeVal, afterVal));
            }
        }
        if (news.getRiskStatus() != old.getRiskStatus()){
            String beforeVal = old.getRiskStatus() ==null ? isNo: old.getRiskStatus() == 0 ?isNo:isYes;
            String afterVal = news.getRiskStatus() ==null ? isNo: news.getRiskStatus() == 0 ?isNo:isYes;
            list.add(setTagRuleRelationLog(news,Constants.RiskStatus,beforeVal,afterVal));
        }
        return  list;
    }
    /**
     * 新增标签
     * */
    private List<RcsQuotaBusinessLimitLog> setUserProfileTagsLog(UserProfileTags news)  {
        List<RcsQuotaBusinessLimitLog> list=new ArrayList<>();
        if(Objects.nonNull(news.getIsDefault()) &&  1 == news.getIsDefault()){
            String beforeVal = isNo;
            String afterVal  =isYes;
            list.add(setTagRuleRelationLog(news,Constants.IsDefault,beforeVal,afterVal));
            list.add(setTagRuleRelationLog(businessLogVo.getDefaultTagEntity(),Constants.IsDefault,isYes,isNo));
        }
        list.add(setTagRuleRelationLog(news,"新增标签","",news.getTagName()));
        return  list;
    }
    /**
     * 修改独立标签
     * */
    private List<RcsQuotaBusinessLimitLog> setTagsRuleRelationLog(TagsRuleSaveReqVo news, UserProfileTagsRuleRelation old) {

        List<RcsQuotaBusinessLimitLog> list=new ArrayList<>();
        if(Objects.nonNull(news.getParameter1())){
            if(Objects.isNull(old.getParameter1())){
                list.add(setTagRuleRelationLog(news,Constants.SingeRule_1,"",news.getParameter1()));
            }else{
                if(!news.getParameter1().equals(old.getParameter1())){
                    list.add(setTagRuleRelationLog(news,Constants.SingeRule_1,old.getParameter1(),news.getParameter1()));
                }
            }
        }
        if(Objects.nonNull(news.getParameter2())){
            if(Objects.isNull(old.getParameter2())){
                list.add(setTagRuleRelationLog(news,Constants.SingeRule_2,"",news.getParameter2()));
            }else{
                if(!news.getParameter2().equals(old.getParameter2())){
                    list.add(setTagRuleRelationLog(news,Constants.SingeRule_2,old.getParameter2(),news.getParameter2()));
                }
            }
        }
        if(Objects.nonNull(news.getParameter3())){
            if(Objects.isNull(old.getParameter3())){
                list.add(setTagRuleRelationLog(news,Constants.SingeRule_3,"",news.getParameter3()));
            }else{
                if(!news.getParameter3().equals(old.getParameter3())){
                    list.add(setTagRuleRelationLog(news,Constants.SingeRule_3,old.getParameter3(),news.getParameter3()));
                }
            }
        }
        if(Objects.nonNull(news.getParameter4())){
            if(Objects.isNull(old.getParameter4())){
                list.add(setTagRuleRelationLog(news,Constants.SingeRule_4,"",news.getParameter4()));
            }else{
                if(!news.getParameter4().equals(old.getParameter4())){
                    list.add(setTagRuleRelationLog(news,Constants.SingeRule_4,old.getParameter4(),news.getParameter4()));
                }
            }
        }
        if(Objects.nonNull(news.getParameter5())){
            if(Objects.isNull(old.getParameter5())){
                list.add(setTagRuleRelationLog(news,Constants.SingeRule_5,"",news.getParameter5()));
            }else{
                if(!news.getParameter5().equals(old.getParameter5())){
                    list.add(setTagRuleRelationLog(news,Constants.SingeRule_5,old.getParameter5(),news.getParameter5()));
                }
            }
        }
        if(Objects.nonNull(news.getParameter6())){
            if(Objects.isNull(old.getParameter6())){
                list.add(setTagRuleRelationLog(news,Constants.SingeRule_6,"",news.getParameter6()));
            }else{
                if(!news.getParameter6().equals(old.getParameter6())){
                    list.add(setTagRuleRelationLog(news,Constants.SingeRule_6,old.getParameter6(),news.getParameter6()));
                }
            }
        }
        return  list;
    }
    /**
     * 添加独立标签规则
     * */
    private List<RcsQuotaBusinessLimitLog> setTagsRuleRelationLog(TagsRuleSaveReqVo news)  {
        List<RcsQuotaBusinessLimitLog> list=new ArrayList<>();
        list.add(setTagRuleRelationLog(news,Constants.SingeRule_Rule,"",news.getRuleName()));
        return  list;
    }
    /**
     * 修改组合标签设置
     * */
    private List<RcsQuotaBusinessLimitLog> setTagsGroupRelationLog(TagsGroupRuleSaveReqVo news,UserProfileTagsGroupRuleRelation old)  {
        List<RcsQuotaBusinessLimitLog> list=new ArrayList<>();
        if(Objects.nonNull(news.getParameter1())){
            if(Objects.isNull(old.getParameter1())){
                list.add(setTagRuleRelationLog(news,Constants.GroupRule_1,"",news.getParameter1()));
            }else{
                if(!news.getParameter1().equals(old.getParameter1())){
                    list.add(setTagRuleRelationLog(news,Constants.GroupRule_1,old.getParameter1(),news.getParameter1()));
                }
            }
        }
        if(Objects.nonNull(news.getParameter2())){
            if(Objects.isNull(old.getParameter2())){
                list.add(setTagRuleRelationLog(news,Constants.GroupRule_2,"",news.getParameter2()));
            }else{
                if(!news.getParameter2().equals(old.getParameter2())){
                    list.add(setTagRuleRelationLog(news,Constants.GroupRule_2,old.getParameter2(),news.getParameter2()));
                }
            }
        }
        if(Objects.nonNull(news.getParameter3())){
            if(Objects.isNull(old.getParameter3())){
                list.add(setTagRuleRelationLog(news,Constants.GroupRule_3,"",news.getParameter3()));
            }else{
                if(!news.getParameter3().equals(old.getParameter3())){
                    list.add(setTagRuleRelationLog(news,Constants.GroupRule_3,old.getParameter3(),news.getParameter3()));
                }
            }
        }
        if(Objects.nonNull(news.getParameter4())){
            if(Objects.isNull(old.getParameter4())){
                list.add(setTagRuleRelationLog(news,Constants.GroupRule_4,"",news.getParameter4()));
            }else{
                if(!news.getParameter4().equals(old.getParameter4())){
                    list.add(setTagRuleRelationLog(news,Constants.GroupRule_4,old.getParameter4(),news.getParameter4()));
                }
            }
        }
        if(Objects.nonNull(news.getParameter5())){
            if(Objects.isNull(old.getParameter5())){
                list.add(setTagRuleRelationLog(news,Constants.GroupRule_5,"",news.getParameter5()));
            }else{
                if(!news.getParameter5().equals(old.getParameter5())){
                    list.add(setTagRuleRelationLog(news,Constants.GroupRule_5,old.getParameter5(),news.getParameter5()));
                }
            }
        }
        if(Objects.nonNull(news.getParameter6())){
            if(Objects.isNull(old.getParameter6())){
                list.add(setTagRuleRelationLog(news,Constants.GroupRule_6,"",news.getParameter6()));
            }else{
                if(!news.getParameter6().equals(old.getParameter6())){
                    list.add(setTagRuleRelationLog(news,Constants.GroupRule_6,old.getParameter6(),news.getParameter6()));
                }
            }
        }
        return  list;
    }
    /**
     * 删除组合标签
     * */
    private List<RcsQuotaBusinessLimitLog> delTagsGroupRelationLog(UserProfileTagsGroupRuleRelation old) {
        List<RcsQuotaBusinessLimitLog> groupList=new ArrayList<>();
        UserProfileRule userProfileRule = businessLogVo.getUserProfileRules().stream().filter(t->t.getId().equals(old.getRuleId())).findFirst().orElse(null);
        groupList.add(delTagGroupRuleLog(old,Constants.SingeRule_Group,userProfileRule.getRuleName(),""));
        return  groupList;
    }
    /**
     * 删除独立标签
     * */
    private List<RcsQuotaBusinessLimitLog> delTagsRelationLog(UserProfileTagsRuleRelation old) {
        List<RcsQuotaBusinessLimitLog> ruleList=new ArrayList<>();
        UserProfileRule userProfileRule = businessLogVo.getUserProfileRules().stream().filter(t->t.getId().equals(old.getRuleId())).findFirst().orElse(null);
        ruleList.add(delTagRuleLog(old,Constants.SingeRule_Rule,userProfileRule.getRuleName(),""));
        return  ruleList;
    }
    /**
     * 设置组合规则日志
     * */
    private List<RcsQuotaBusinessLimitLog> setTagsGroupRelationLog(TagsGroupRuleSaveReqVo news) {
        List<RcsQuotaBusinessLimitLog> list=new ArrayList<>();
        list.add(setInsertGroupRuleLog(news,Constants.SingeRule_Group,"",news.getRuleName()));
        return  list;
    }
    /**
     * 设置独立规则日志
     * */
    private RcsQuotaBusinessLimitLog setTagRuleRelationLog(TagsRuleSaveReqVo tagsRuleRelation, String paramName,
                                                           String beforeVal, String afterVal)  {

        UserProfileTags userProfileTags=businessLogVo.getUserProfileTags().stream().filter(item->item.getId().equals(tagsRuleRelation.getTagId())).findFirst().orElse(null);

        RcsQuotaBusinessLimitLog limitLogRule = new RcsQuotaBusinessLimitLog();
        limitLogRule.setOperateCategory(logTitle);
        limitLogRule.setObjectId(userProfileTags.getId().toString());
        limitLogRule.setObjectName(userProfileTags.getTagName());
        limitLogRule.setExtObjectId("-");
        limitLogRule.setExtObjectName("-");
        limitLogRule.setOperateType(Constants.logCode);
        limitLogRule.setParamName(String.format(paramName,tagsRuleRelation.getRuleName()));
        limitLogRule.setBeforeVal(beforeVal);
        limitLogRule.setAfterVal(afterVal);
        limitLogRule.setUserId(businessLogVo.getUserId());
        //limitLoglog.setUserName(linkedHashMap.get("userName").toString());
        return limitLogRule;
    }
    /**
     * 删除组合规则日志操作
     * */
    private RcsQuotaBusinessLimitLog delTagGroupRuleLog(UserProfileTagsGroupRuleRelation tagsGroupRuleRelation, String paramName,
                                                        String beforeVal, String afterVal) {

        UserProfileTags userProfileTags=businessLogVo.getUserProfileTags().stream().filter(item->item.getId().equals(tagsGroupRuleRelation.getTagId())).findFirst().orElse(null);
        RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
        limitLoglog.setOperateCategory(logTitle);
        limitLoglog.setObjectId(tagsGroupRuleRelation.getTagId().toString());
        limitLoglog.setObjectName(userProfileTags.getTagName());
        limitLoglog.setExtObjectId("-");
        limitLoglog.setExtObjectName("-");
        limitLoglog.setOperateType(Constants.logCode);
        limitLoglog.setParamName(paramName);
        limitLoglog.setBeforeVal(beforeVal);
        limitLoglog.setAfterVal(afterVal);
        limitLoglog.setUserId(businessLogVo.getUserId());
        //limitLoglog.setUserName(linkedHashMap.get("userName").toString());
        return limitLoglog;
    }
    /**
     * 独立标签设置
     * */
    private RcsQuotaBusinessLimitLog delTagRuleLog(UserProfileTagsRuleRelation tagsRuleRelation, String paramName,
                                                        String beforeVal, String afterVal) {

        UserProfileTags userProfileTags=businessLogVo.getUserProfileTags().stream().filter(item->item.getId().equals(tagsRuleRelation.getTagId())).findFirst().orElse(null);
        RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
        limitLoglog.setOperateCategory(logTitle);
        limitLoglog.setObjectId(tagsRuleRelation.getTagId().toString());
        limitLoglog.setObjectName(userProfileTags.getTagName());
        limitLoglog.setExtObjectId("-");
        limitLoglog.setExtObjectName("-");
        limitLoglog.setOperateType(Constants.logCode);
        limitLoglog.setParamName(paramName);
        limitLoglog.setBeforeVal(beforeVal);
        limitLoglog.setAfterVal(afterVal);
        limitLoglog.setUserId(businessLogVo.getUserId());
        //limitLoglog.setUserName(linkedHashMap.get("userName").toString());
        return limitLoglog;
    }
    /**
     * 新增标签日志
     * */
    private RcsQuotaBusinessLimitLog setInsertGroupRuleLog(TagsGroupRuleSaveReqVo tagsGroupRuleRelation, String paramName,
                                                         String beforeVal, String afterVal) {

        UserProfileTags userProfileTags=businessLogVo.getUserProfileTags().stream().filter(item->item.getId().equals(tagsGroupRuleRelation.getTagId())).findFirst().orElse(null);
        RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
        limitLoglog.setOperateCategory(logTitle);
        limitLoglog.setObjectId(tagsGroupRuleRelation.getTagId().toString());
        limitLoglog.setObjectName(userProfileTags.getTagName());
        limitLoglog.setExtObjectId("-");
        limitLoglog.setExtObjectName("-");
        limitLoglog.setOperateType(Constants.logCode);
        limitLoglog.setParamName(paramName);
        limitLoglog.setBeforeVal(beforeVal);
        limitLoglog.setAfterVal(afterVal);
        limitLoglog.setUserId(businessLogVo.getUserId());
        //limitLoglog.setUserName(linkedHashMap.get("userName").toString());
        return limitLoglog;
    }
    /**
     * 构造设置组合标签日志
     * */
    private RcsQuotaBusinessLimitLog setTagRuleRelationLog(TagsGroupRuleSaveReqVo tagsGroupRuleRelation, String paramName,
                                                           String beforeVal, String afterVal) {

        UserProfileTags userProfileTags=businessLogVo.getUserProfileTags().stream().filter(item->item.getId().equals(tagsGroupRuleRelation.getTagId())).findFirst().orElse(null);
        RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
        limitLoglog.setOperateCategory(logTitle);
        limitLoglog.setObjectId(tagsGroupRuleRelation.getTagId().toString());
        limitLoglog.setObjectName(userProfileTags.getTagName());
        limitLoglog.setExtObjectId("-");
        limitLoglog.setExtObjectName("-");
        limitLoglog.setOperateType(Constants.logCode);
        limitLoglog.setParamName(String.format(paramName,tagsGroupRuleRelation.getRuleName()));
        limitLoglog.setBeforeVal(beforeVal);
        limitLoglog.setAfterVal(afterVal);
        limitLoglog.setUserId(businessLogVo.getUserId());
        //limitLoglog.setUserName(linkedHashMap.get("userName").toString());
        return limitLoglog;
    }
    /**
     * 标签操作
     * */
    private RcsQuotaBusinessLimitLog setTagRuleRelationLog(UserProfileTags userProfileTags, String paramName,
                                                           String beforeVal, String afterVal) {
        RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
        limitLoglog.setOperateCategory(logTitle);
        limitLoglog.setObjectId(userProfileTags.getId().toString());
        limitLoglog.setObjectName(userProfileTags.getTagName());
        limitLoglog.setExtObjectId("-");
        limitLoglog.setExtObjectName("-");
        limitLoglog.setOperateType(Constants.logCode);
        limitLoglog.setParamName(paramName);
        limitLoglog.setBeforeVal(beforeVal);
        limitLoglog.setAfterVal(afterVal);
        limitLoglog.setUserId(businessLogVo.getUserId());
        //limitLoglog.setUserName(linkedHashMap.get("userName").toString());
        return limitLoglog;
    }
}
