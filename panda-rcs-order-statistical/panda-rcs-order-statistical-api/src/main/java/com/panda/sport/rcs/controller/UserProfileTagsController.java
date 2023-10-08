package com.panda.sport.rcs.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.panda.sport.rcs.ProducerSendMessageUtils;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.constants.Constants;
import com.panda.sport.rcs.common.constants.*;
import com.panda.sport.rcs.common.utils.CopyUtils;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.common.utils.ThreadUtil;
import com.panda.sport.rcs.common.vo.RcsQuotaBusinessLimitLog;
import com.panda.sport.rcs.common.vo.TTagMarketReqVo;
import com.panda.sport.rcs.common.vo.api.request.*;
import com.panda.sport.rcs.common.vo.api.response.*;
import com.panda.sport.rcs.customdb.entity.DataEntity;
import com.panda.sport.rcs.db.entity.*;
import com.panda.sport.rcs.db.service.IUserProfileRuleService;
import com.panda.sport.rcs.db.service.IUserProfileTagsGroupRuleRelationService;
import com.panda.sport.rcs.db.service.IUserProfileTagsRuleRelationService;
import com.panda.sport.rcs.db.service.IUserProfileTagsService;
import com.panda.sport.rcs.redis.service.RedisService;
import com.panda.sport.rcs.service.BusinessLogService;
import com.panda.sport.rcs.service.BusinessLogVo;
import com.panda.sport.rcs.service.ITagService;
import com.panda.sport.rcs.utils.IPUtil;
import com.panda.sport.rcs.utils.TradeUserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户画像标签管理表 前端控制器
 * </p>
 *
 * @author lithan auto
 * @since 2020-06-25
 */
@Api(tags = "数据维护-标签维护")
@RestController
@RequestMapping("/userProfileTags")
public class UserProfileTagsController {

    Logger log = LoggerFactory.getLogger(UserProfileTagsController.class);


    @Autowired
    IUserProfileTagsService userProfileTagsService;
    @Autowired
    IUserProfileTagsRuleRelationService tagsRuleRelationService;
    @Autowired
    IUserProfileTagsGroupRuleRelationService tagsGroupRuleRelationService;

    @Autowired
    IUserProfileRuleService ruleService;

    @Autowired
    ITagService tagService;

    @Autowired
    IUserProfileTagsRuleRelationService ruleRelationService;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;


    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    //及时注单标签过滤
    private final static String currentTimeOrderTagListKey = RedisConstants.PREFIX + "currentTimeOrder:tagList";

    private final String logTitle="标签管理";

    private final String logCode="10020";

    @Autowired
    RedisService redisService;

    @ApiOperation(value = "根据标签类型查询标签列表 1基本属性类 2投注特征类 3访问特征类 4财务特征类")
    @RequestMapping(value = "/listByType", method = {RequestMethod.POST})
    public Result<List<UserProfileTags>> listByType(@RequestBody @Valid UserProfileTags vo, @RequestHeader("lang") String lang) {
        LambdaQueryWrapper<UserProfileTags> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectUtils.isNotEmpty(vo.getTagType()), UserProfileTags::getTagType, vo.getTagType());
        if(ObjectUtils.isNotEmpty(vo.getIsDefault())){
            wrapper.eq(UserProfileTags::getIsDefault, vo.getIsDefault());
        }
        List<UserProfileTags> pageData = userProfileTagsService.list(wrapper);

        //国际化
        if(lang.equals("en")){
            for (UserProfileTags pageDatum : pageData) {
                pageDatum.setTagName(pageDatum.getEnglishTagName());
            }
        }

        return Result.succes(pageData);
    }

    @ApiOperation(value = "根据标签类型查询标签列表-及时注单 1基本属性类 2投注特征类 3访问特征类 4财务特征类")
    @RequestMapping(value = "/listByTypeForCurrentTimeOrder", method = {RequestMethod.POST})
    public Result<List<UserProfileTags>> listByTypeForCurrentTimeOrder(@RequestBody @Valid UserProfileTags vo, @RequestHeader("lang") String lang) {
        LambdaQueryWrapper<UserProfileTags> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectUtils.isNotEmpty(vo.getTagType()), UserProfileTags::getTagType, vo.getTagType());
        List<UserProfileTags> pageData = userProfileTagsService.list(wrapper);

        if (ObjectUtils.isNotEmpty(redisService.get(currentTimeOrderTagListKey))) {
            String str = redisService.get(currentTimeOrderTagListKey).toString();
            List<String> tagIds = Arrays.stream(str.split(",")).collect(Collectors.toList());
            pageData = pageData.stream().filter(e -> !tagIds.contains(e.getId().toString())).collect(Collectors.toList());
        }

        //国际化
        if(lang.equals("en")){
            for (UserProfileTags pageDatum : pageData) {
                pageDatum.setTagName(pageDatum.getEnglishTagName());
            }
        }

        return Result.succes(pageData);
    }

    @ApiOperation(value = "列表 分页")
    @RequestMapping(value = "/list", method = {RequestMethod.POST})
    public Result<IPage<TagListResVo>> list(@RequestBody @Valid TagListReqVo vo, @RequestHeader("lang") String lang) {
        LambdaQueryWrapper<UserProfileTags> wrapper = new LambdaQueryWrapper<>();
        //分页数据
        Page<UserProfileTags> pageParam = new Page<>(vo.getPageNum(), vo.getPageSize());
        IPage<UserProfileTags> pageData = userProfileTagsService.page(pageParam, wrapper);
        //vo转换
        List<TagListResVo> list = CopyUtils.clone(pageData.getRecords(), TagListResVo.class);

        IPage<TagListResVo> resList = CopyUtils.copyPage(pageData, list);

        //国际化
//        if(lang.equals("en")){
//            for (TagListResVo tagListResVo : resList.getRecords()) {
//                tagListResVo.setTagName(tagListResVo.getEnglishTagName());
//            }
//        }

        return Result.succes(resList);
    }

    @ApiOperation(value = "查询")
    @RequestMapping(value = "/getById", method = {RequestMethod.POST})
    public Result<UserProfileTags> getById(@RequestBody @Valid IdReqVo vo, @RequestHeader("lang") String lang) {
        UserProfileTags entity = userProfileTagsService.getById(vo.getId());

        //国际化
        if(lang.equals("en")){
            entity.setTagName(entity.getEnglishTagName());
        }

        return Result.succes(entity);
    }

    @ApiOperation(value = "新增", notes = "")
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    public Result<Boolean> add(@RequestBody @Valid UserProfileTagsAddReqVo vo, HttpServletRequest request) {
        try{
            //只能有一个默认标签
            UserProfileTags defaultTagEntity=new UserProfileTags();
            if(vo.getIsDefault().equals(1)){
                QueryWrapper<UserProfileTags> queryUserTags = new QueryWrapper<>();
                queryUserTags.lambda().eq(UserProfileTags::getIsDefault, 1);
                UserProfileTags tagEntity = userProfileTagsService.getOne(queryUserTags);
                if(!ObjectUtils.isEmpty(tagEntity)){
                    return Result.fail("系统必须有且只有一个默认标签");
                }
                defaultTagEntity=tagEntity;
            }

            UserProfileTags tagEntity = CopyUtils.clone(vo, UserProfileTags.class);
            UserProfileTags newEntity=new UserProfileTags();
            boolean flag = userProfileTagsService.saveOrUpdate(tagEntity);
            if(flag){
                QueryWrapper<UserProfileTags> queryUserTags = new QueryWrapper<>();
                queryUserTags.lambda().orderByDesc(UserProfileTags::getId).last("limit 1");
                newEntity=userProfileTagsService.getOne(queryUserTags);
            }
            //如果操作的标签类型是 投注特征类 的时候则发送MQ数据同步数据到操盘库
            if(tagEntity.getTagType().equals(2)){
                Map<String, Object> map = new HashMap<>();
                map.put("type", 1);
                map.put("entity", tagEntity);
                producerSendMessageUtils.sendMessage(RocketMQConstants.USER_PROFILE_TAGS_TOPIC, RocketMQConstants.USER_PROFILE_TAGS_TAG, JSONObject.toJSONString(map));
                log.info("{}消息发送成功", RocketMQConstants.USER_PROFILE_TAGS_TAG);
            }
            if(tagEntity.getTagType().equals(4)){
                Map<String, Object> map = new HashMap<>();
                map.put("type", 1);
                map.put("entity", tagEntity);
                producerSendMessageUtils.sendMessage(RocketMQConstants.USER_FINANCE_TAGS_TOPIC, RocketMQConstants.USER_FINANCE_TAGS_TAG, JSONObject.toJSONString(map));
                log.info("{}消息发送成功", RocketMQConstants.USER_FINANCE_TAGS_TAG);

                //通知业务新增一个赔率增减默认为0的财务特征标签
                TTagMarketReqVo reqVo = new TTagMarketReqVo();
                reqVo.setTagId(Integer.parseInt(tagEntity.getId().toString()));
                reqVo.setLevelId(0);
                reqVo.setOddsValue(BigDecimal.ZERO);
                reqVo.setTagName(tagEntity.getTagName());
                reqVo.setTagName(tagEntity.getEnglishTagName());

                Map<String, Object> map1 = new HashMap<>();
                map1.put("type", 1);
                map1.put("entity", reqVo);
                producerSendMessageUtils.sendMessage(RocketMQConstants.USER_FINANCE_TAG_CHANGE_TOPIC, RocketMQConstants.USER_FINANCE_TAG_CHANGE_TAG, JSONObject.toJSONString(map1));
                log.info("{}消息发送成功", RocketMQConstants.USER_FINANCE_TAG_CHANGE_TAG);
            }

            vo.getRule().forEach(e -> {
                e.setTagId(tagEntity.getId());
                e.setId(null);
            });
            List<UserProfileTagsRuleRelation> tagsRuleRelations = CopyUtils.clone(vo.getRule(), UserProfileTagsRuleRelation.class);
            tagsRuleRelationService.saveBatch(tagsRuleRelations);
            List<TagsGroupRuleSaveReqVo> newTagsGroupRuleSaveReqVos= new ArrayList<>();
            //组合规则
            List<List<TagsGroupRuleSaveReqVo>> groupRule = vo.getGroupRule();
            if(CollectionUtils.isNotEmpty(groupRule)){
                for (int i = groupRule.size() - 1; i >= 0; i--) {
                    for (TagsGroupRuleSaveReqVo tagsGroupRuleSaveReqVo : groupRule.get(i)) {
                        tagsGroupRuleSaveReqVo.setGroupId(Long.valueOf(i));
                    }
                }
                vo.getGroupRule().forEach(e -> {
                    for (TagsGroupRuleSaveReqVo tagsGroupRuleSaveReqVo : e) {
                        tagsGroupRuleSaveReqVo.setTagId(tagEntity.getId());
                    }
                });
                QueryWrapper<UserProfileTagsGroupRuleRelation> groupQueryWrapper = new QueryWrapper<>();
                groupQueryWrapper.lambda().eq(UserProfileTagsGroupRuleRelation::getTagId, vo.getId());
                tagsGroupRuleRelationService.remove(groupQueryWrapper);

                for (List<TagsGroupRuleSaveReqVo> tagsGroupRuleSaveReqVos : vo.getGroupRule()) {
                    List<UserProfileTagsGroupRuleRelation> groupRuleRelations = CopyUtils.clone(tagsGroupRuleSaveReqVos, UserProfileTagsGroupRuleRelation.class);
                    if (CollectionUtils.isNotEmpty(groupRuleRelations)) {
                        newTagsGroupRuleSaveReqVos.addAll(tagsGroupRuleSaveReqVos);
                        tagsGroupRuleRelationService.saveBatch(groupRuleRelations);
                    }
                }
            }
            String userId= TradeUserUtils.getUserId().toString();
            LambdaQueryWrapper<UserProfileTags> wrapper = new LambdaQueryWrapper<>();
            List<UserProfileTags> userProfileTags = userProfileTagsService.list(wrapper);
            BusinessLogVo businessLogVo =new BusinessLogVo(null,newEntity,new ArrayList<>(),vo.getRule(),new ArrayList<>(),newTagsGroupRuleSaveReqVos,userId,userProfileTags,defaultTagEntity,null);
            List<RcsQuotaBusinessLimitLog> listFuture = taskExecutor.submit(new BusinessLogService(businessLogVo)).get();
            if(CollectionUtils.isNotEmpty(listFuture)){
                //添加操作IP
                String ip = IPUtil.getRequestIp(request);
                for(RcsQuotaBusinessLimitLog log:listFuture){
                    log.setIp(ip);
                }
                String arrString = JSONArray.toJSONString(listFuture);
                log.info("标签管理->标签添加管理处理条数{}",listFuture.size());
                producerSendMessageUtils.sendMessage(com.panda.sport.rcs.utils.Constants.RCS_BUSINESS_LOG_SAVE,null, com.panda.sport.rcs.utils.Constants.logCode,arrString);
            }
        }catch (Exception e){
            log.error("操作异常{}" + e);
            return Result.fail("操作异常");
        }

        return Result.succes();
    }
    /**
     * 设置风控日志
     * */
    private void  setBusinessLog(UserProfileTags userProfileTags) {
        List<RcsQuotaBusinessLimitLog> limitLogList=new ArrayList<>();
        limitLogList.add(setBusinessLimitLog(userProfileTags,"新增标签","",userProfileTags.getTagName()));
        log.info("标签管理->风控措施管理处理条数{}",limitLogList.size());
        String arrString = JSONArray.toJSONString(limitLogList);
        producerSendMessageUtils.sendMessage(com.panda.sport.rcs.utils.Constants.RCS_BUSINESS_LOG_SAVE,null,com.panda.sport.rcs.utils.Constants.logCode,arrString);
    }
    @ApiOperation(value = "新增/编辑前验证接口", notes = "")
    @RequestMapping(value = "/validate", method = {RequestMethod.POST})
    public Result validate(@RequestBody @Valid UserProfileTagsAddReqVo vo) {
        //如果勾选了默认标签即isDefault=1 就走验证接口，否则直接走修改接口
        if(vo.getIsDefault().equals(1)){
            QueryWrapper<UserProfileTags> queryUserTags = new QueryWrapper<>();
            queryUserTags.lambda().eq(UserProfileTags::getIsDefault, 1);
            UserProfileTags tagEntity = userProfileTagsService.getOne(queryUserTags);
            if(ObjectUtils.isEmpty(tagEntity)){
                return Result.succes(true);
            }else{
                if(!ObjectUtils.isEmpty(vo.getId())){
                    if(tagEntity.getId().equals(vo.getId())){
                        return Result.succes(true);
                    }else{
                        return Result.succes(JSONObject.toJSONString(tagEntity));
                    }
                }else{
                    return Result.succes(JSONObject.toJSONString(tagEntity));
                }
            }
        }
        return Result.succes(true);
    }
    @ApiOperation(value = "编辑", notes = "")
    @RequestMapping(value = "/edit", method = {RequestMethod.POST})
    public Result<Boolean> edit(@RequestBody @Valid UserProfileTagsAddReqVo vo, HttpServletRequest request) {
        //检查code是否重复
        if (null == vo.getId() || 0 == vo.getId()) {
            return Result.fail("id为空,编辑失败");
        }
        if (ObjectUtils.isEmpty(vo.getChangeManner())) {
            return Result.fail("操作人为空,编辑失败");
        }
        try{
            //默认给0
            if(ObjectUtils.isEmpty(vo.getFatherId())){
                vo.setFatherId(0L);
            }

            UserProfileTags newEntity = CopyUtils.clone(vo, UserProfileTags.class);

            //只能有一个默认标签
            QueryWrapper<UserProfileTags> queryUserTags = new QueryWrapper<>();
            queryUserTags.lambda().eq(UserProfileTags::getIsDefault, 1);
            UserProfileTags defaultTagEntity = userProfileTagsService.getOne(queryUserTags);

            if(ObjectUtils.isEmpty(defaultTagEntity)){
                if(vo.getIsDefault().equals(0)){
                    return Result.fail("系统必须有且只有一个默认标签");
                }
            }else{
                if(vo.getIsDefault().equals(0) && vo.getId().equals(defaultTagEntity.getId())){
                    return Result.fail("系统必须有且只有一个默认标签");
                }
                if(vo.getIsDefault().equals(1) && !vo.getId().equals(defaultTagEntity.getId())){
                    defaultTagEntity.setIsDefault(0);
                    userProfileTagsService.updateById(defaultTagEntity);
                }
            }
            QueryWrapper<UserProfileTags> queryOldUserTags = new QueryWrapper<>();
            queryOldUserTags.lambda().eq(UserProfileTags::getId, newEntity.getId());
            UserProfileTags oldTagEntity = userProfileTagsService.getOne(queryOldUserTags);

            userProfileTagsService.updateById(newEntity);
            //如果操作的标签类型是 投注特征类 的时候则发送MQ数据同步数据到操盘库
            if(newEntity.getTagType().equals(2)){
                Map<String, Object> map = new HashMap<>();
                map.put("type", 2);
                map.put("entity", newEntity);
                producerSendMessageUtils.sendMessage(RocketMQConstants.USER_PROFILE_TAGS_TOPIC, RocketMQConstants.USER_PROFILE_TAGS_TAG,JSONObject.toJSONString(map));
                log.info("{}消息发送成功", RocketMQConstants.USER_PROFILE_TAGS_TAG);
            }
            if(newEntity.getTagType().equals(4)){
                Map<String, Object> map = new HashMap<>();
                map.put("type", 2);
                map.put("entity", newEntity);
                producerSendMessageUtils.sendMessage(RocketMQConstants.USER_FINANCE_TAGS_TOPIC, RocketMQConstants.USER_FINANCE_TAGS_TAG,JSONObject.toJSONString(map));
                log.info("{}消息发送成功", RocketMQConstants.USER_FINANCE_TAGS_TAG);
            }

            //独立规则
            vo.getRule().forEach(e -> {
                e.setTagId(newEntity.getId());
            });
            List<Long> ruleIds=new ArrayList<>();
            List<UserProfileTagsRuleRelation> oldRelations=new ArrayList<>();
            QueryWrapper<UserProfileTagsRuleRelation> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(UserProfileTagsRuleRelation::getTagId, vo.getId());
            tagsRuleRelationService.list(queryWrapper).forEach(u->{
                oldRelations.add(u);
                ruleIds.add(u.getRuleId());
            });

            tagsRuleRelationService.remove(queryWrapper);
            List<UserProfileTagsRuleRelation> ruleRelations = CopyUtils.clone(vo.getRule(), UserProfileTagsRuleRelation.class);
            if (!CollectionUtils.isEmpty(ruleRelations)) {
                tagsRuleRelationService.saveBatch(ruleRelations);
            }
            List<TagsRuleSaveReqVo> newRelations=vo.getRule();
            //组合规则
            QueryWrapper<UserProfileTagsGroupRuleRelation> groupQueryWrapper = new QueryWrapper<>();
            groupQueryWrapper.lambda().eq(UserProfileTagsGroupRuleRelation::getTagId, vo.getId());

            List<UserProfileTagsGroupRuleRelation> oldTagsGroupRuleRelations=new ArrayList<>();
            tagsGroupRuleRelationService.list(groupQueryWrapper).forEach(t->{
                oldTagsGroupRuleRelations.add(t);
                ruleIds.add(t.getRuleId());
            });

            tagsGroupRuleRelationService.remove(groupQueryWrapper);
            List<TagsGroupRuleSaveReqVo> newTagsGroupRuleSaveReqVos =new ArrayList<>();
            List<List<TagsGroupRuleSaveReqVo>> groupRule = vo.getGroupRule();
            if(CollectionUtils.isNotEmpty(groupRule)){
                for (int i = groupRule.size() - 1; i >= 0; i--) {
                    for (TagsGroupRuleSaveReqVo tagsGroupRuleSaveReqVo : groupRule.get(i)) {
                        tagsGroupRuleSaveReqVo.setGroupId(Long.valueOf(i));
                    }
                }
                vo.getGroupRule().forEach(e -> {
                    for (TagsGroupRuleSaveReqVo tagsGroupRuleSaveReqVo : e) {
                        tagsGroupRuleSaveReqVo.setTagId(newEntity.getId());
                    }
                });

                for (List<TagsGroupRuleSaveReqVo> tagsGroupRuleSaveReqVos : vo.getGroupRule()) {
                    if(CollectionUtils.isNotEmpty(tagsGroupRuleSaveReqVos)) {
                        newTagsGroupRuleSaveReqVos.addAll(tagsGroupRuleSaveReqVos);
                        List<UserProfileTagsGroupRuleRelation> groupRuleRelations = CopyUtils.clone(tagsGroupRuleSaveReqVos, UserProfileTagsGroupRuleRelation.class);
                        tagsGroupRuleRelationService.saveBatch(groupRuleRelations);
                    }
                }
            }
            producerSendMessageUtils.sendMessage(RocketMQConstants.USER_TAG_FLUSH_TOPIC, RocketMQConstants.USER_TAG_FLUSH_TAG, "1");
            LambdaQueryWrapper<UserProfileRule> userProfileRuleLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userProfileRuleLambdaQueryWrapper.in(!ruleIds.isEmpty(), UserProfileRule::getId, ruleIds);
            List<UserProfileRule> userProfileRules=ruleService.list(userProfileRuleLambdaQueryWrapper);
            //修改日志收集
            String userId= TradeUserUtils.getUserId().toString();
            LambdaQueryWrapper<UserProfileTags> wrapper = new LambdaQueryWrapper<>();
            List<UserProfileTags> userProfileTags = userProfileTagsService.list(wrapper);
            BusinessLogVo businessLogVo =new BusinessLogVo(oldTagEntity,newEntity,oldRelations,newRelations,oldTagsGroupRuleRelations,newTagsGroupRuleSaveReqVos,userId,userProfileTags,defaultTagEntity,userProfileRules);
            List<RcsQuotaBusinessLimitLog> listFuture = taskExecutor.submit(new BusinessLogService(businessLogVo)).get();
            if(CollectionUtils.isNotEmpty(listFuture)){
                //添加操作IP
                String ip = IPUtil.getRequestIp(request);
                for(RcsQuotaBusinessLimitLog ipLog :listFuture){
                    ipLog.setIp(ip);
                }
                String arrString = JSONArray.toJSONString(listFuture);
                log.info("标签管理->修改标签管理处理条数{}",listFuture.size());
                producerSendMessageUtils.sendMessage(com.panda.sport.rcs.utils.Constants.RCS_BUSINESS_LOG_SAVE,null, com.panda.sport.rcs.utils.Constants.logCode,arrString);
            }
        }catch (Exception e){
            log.error("操作异常{}",e);
            return Result.fail("操作异常");
        }

        return Result.succes();
    }


    @ApiOperation(value = "删除", notes = "")
    @RequestMapping(value = "/del", method = {RequestMethod.POST})
    public Result<Boolean> del(@RequestBody @Valid IdReqVo vo) {
        try{
            QueryWrapper<UserProfileTags> queryUserTags = new QueryWrapper<>();
            queryUserTags.lambda().eq(UserProfileTags::getIsDefault, 1);
            UserProfileTags tagEntity = userProfileTagsService.getOne(queryUserTags);
            if(ObjectUtils.isNotEmpty(tagEntity) && vo.getId().equals(tagEntity.getId())){
                return Result.fail("系统必须有且只有1个默认标签");
            }

            UserProfileTags byId = userProfileTagsService.getById(vo.getId());
            boolean flag = userProfileTagsService.removeById(vo.getId());

            if (!flag) {
                return Result.fail("数据不存在");
            }

            QueryWrapper<UserProfileTagsRuleRelation> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(UserProfileTagsRuleRelation::getTagId, vo.getId());
            tagsRuleRelationService.remove(queryWrapper);
            QueryWrapper<UserProfileTagsGroupRuleRelation> groupQueryWrapper = new QueryWrapper<>();
            groupQueryWrapper.lambda().eq(UserProfileTagsGroupRuleRelation::getTagId, vo.getId());
            tagsGroupRuleRelationService.remove(groupQueryWrapper);

            //如果操作的标签类型是 投注特征类 的时候则发送MQ数据同步数据到操盘库
            if(byId.getTagType().equals(2)){
                Map<String, Object> map = new HashMap<>();
                map.put("type", 3);
                map.put("entity", vo);
                producerSendMessageUtils.sendMessage(RocketMQConstants.USER_PROFILE_TAGS_TOPIC, RocketMQConstants.USER_PROFILE_TAGS_TAG, JSONObject.toJSONString(map));
                log.info("{}消息发送成功", RocketMQConstants.USER_PROFILE_TAGS_TAG);
            }
            if(byId.getTagType().equals(4)){
                Map<String, Object> map = new HashMap<>();
                map.put("type", 3);
                map.put("entity", vo);
                producerSendMessageUtils.sendMessage(RocketMQConstants.USER_FINANCE_TAGS_TOPIC, RocketMQConstants.USER_FINANCE_TAGS_TAG, JSONObject.toJSONString(map));
                log.info("{}消息发送成功", RocketMQConstants.USER_FINANCE_TAGS_TAG);

                //通知业务删除一个赔率增减默认为0的财务特征标签
                TTagMarketReqVo reqVo = new TTagMarketReqVo();
                reqVo.setTagId(Integer.parseInt(vo.getId().toString()));

                Map<String, Object> map1 = new HashMap<>();
                map1.put("type", 3);
                map1.put("entity", reqVo);
                producerSendMessageUtils.sendMessage(RocketMQConstants.USER_FINANCE_TAG_CHANGE_TOPIC, RocketMQConstants.USER_FINANCE_TAG_CHANGE_TAG, JSONObject.toJSONString(map1));
                log.info("{}消息发送成功", RocketMQConstants.USER_FINANCE_TAG_CHANGE_TAG);
            }
            //操作标签时，WS推送给前端刷新界面
            producerSendMessageUtils.sendMessage(RocketMQConstants.USER_TAG_FLUSH_TOPIC, RocketMQConstants.USER_TAG_FLUSH_TAG, "1");
            delBusinessLog(byId);
        }catch (Exception e){
            log.error("操作异常",e);
            return Result.fail("操作异常"+e);
        }

        return Result.succes();
    }
    /**
     * 删除日志
     * */
    private void delBusinessLog(UserProfileTags userProfileTags) throws Exception {
        List<RcsQuotaBusinessLimitLog> limitLogList=new ArrayList<>();
        limitLogList.add(setBusinessLimitLog(userProfileTags,"删除标签",userProfileTags.getTagName(),""));
        log.info("标签管理->风控措施管理处理条数{}",limitLogList.size());
        String arrString = JSONArray.toJSONString(limitLogList);
        producerSendMessageUtils.sendMessage(com.panda.sport.rcs.utils.Constants.RCS_BUSINESS_LOG_SAVE,null,com.panda.sport.rcs.utils.Constants.logCode,arrString);
    }
    private RcsQuotaBusinessLimitLog setBusinessLimitLog(UserProfileTags userProfileTags,String paramName,
                                                         String beforeVal,String afterVal)  {
        RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
        limitLoglog.setOperateCategory(logTitle);
        limitLoglog.setObjectId(userProfileTags.getId().toString());
        limitLoglog.setObjectName(userProfileTags.getTagName());
        limitLoglog.setExtObjectId("-");
        limitLoglog.setExtObjectName("-");
        limitLoglog.setOperateType(logCode);
        limitLoglog.setParamName(paramName);
        limitLoglog.setBeforeVal(beforeVal);
        limitLoglog.setAfterVal(afterVal);
        String userId=null;
        try {
            userId=TradeUserUtils.getUserId().toString();
        }catch (Exception ex){
            log.info("转换错误",ex);
        }
        limitLoglog.setUserId(userId);
        //limitLoglog.setUserName(linkedHashMap.get("userName").toString());
        return limitLoglog;
    }
    @ApiOperation(value = "标签-规则 关系绑定", notes = "")
    @RequestMapping(value = "/saveTagRule", method = {RequestMethod.POST})
    public Result<Boolean> saveOrUpdate(@RequestBody @Valid List<TagsRuleSaveReqVo> voList) {
        List<UserProfileTagsRuleRelation> list = CopyUtils.clone(voList, UserProfileTagsRuleRelation.class);
        try {
            tagsRuleRelationService.saveBatch(list);
        } catch (Exception e) {
            log.info("标签-规则 关系绑定异常:{}", e);
            return Result.fail("操作失败,请确认是否重复设置");
        }

        return Result.succes();
    }

    @ApiOperation(value = "标签-规则 关系删除", notes = "")
    @RequestMapping(value = "/delTagRule", method = {RequestMethod.POST})
    public Result<Boolean> delTagRule(@RequestBody @Valid TagsRuleDelReqVo vo) {
        boolean flag;
        try{
            LambdaQueryWrapper<UserProfileTagsRuleRelation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(UserProfileTagsRuleRelation::getTagId, vo.getTagId());
            lambdaQueryWrapper.eq(UserProfileTagsRuleRelation::getRuleId, vo.getRuleId());
            flag = tagsRuleRelationService.remove(lambdaQueryWrapper);
        }catch (Exception e){
            log.error("操作异常{}" + e);
            return Result.fail("操作异常");
        }

        return Result.succes(flag);
    }

    @ApiOperation(value = "标签-规则 关系查询", notes = "")
    @RequestMapping(value = "/getRuleListByTagId", method = {RequestMethod.POST})
    public Result<GetRuleByTagIdResVo> getRuleListByTagId(@RequestBody @Valid IdReqVo vo) {
        GetRuleByTagIdResVo resVo = new GetRuleByTagIdResVo();
        try{
            //独立规则
            LambdaQueryWrapper<UserProfileTagsRuleRelation> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserProfileTagsRuleRelation::getTagId, vo.getId());
            List<UserProfileTagsRuleRelation> list = tagsRuleRelationService.list(wrapper);
            //返回规则名   此处为后台操作 频率低 数据量少 循环处理不影响性能
            List<UserProfileTagsRuleRelationResVo> voList = CopyUtils.clone(list, UserProfileTagsRuleRelationResVo.class);
            for (UserProfileTagsRuleRelationResVo relationVo : voList) {
                UserProfileRule userProfileRule = ruleService.getById(relationVo.getRuleId());
                relationVo.setRuleName(userProfileRule.getRuleName());
                relationVo.setRuleDetail(userProfileRule.getRuleDetail());
            }

            //组合规则
            List<List<UserProfileTagsGroupRuleRelationResVo>> groupRuleRelationResVos = new ArrayList<>();
            LambdaQueryWrapper<UserProfileTagsGroupRuleRelation> groupWrapper = new LambdaQueryWrapper<>();
            groupWrapper.eq(UserProfileTagsGroupRuleRelation::getTagId, vo.getId());
            List<UserProfileTagsGroupRuleRelation> groupRuleRelationList = tagsGroupRuleRelationService.list(groupWrapper);
            if(CollectionUtils.isNotEmpty(groupRuleRelationList)){
                Map<Long, List<UserProfileTagsGroupRuleRelation>> collect = groupRuleRelationList.stream().collect(Collectors.groupingBy(UserProfileTagsGroupRuleRelation::getGroupId));
                collect.forEach((k, v) -> {
                    //返回规则名   此处为后台操作 频率低 数据量少 循环处理不影响性能
                    List<UserProfileTagsGroupRuleRelationResVo> vos = CopyUtils.clone(v, UserProfileTagsGroupRuleRelationResVo.class);
                    for (UserProfileTagsGroupRuleRelationResVo userProfileTagsGroupRuleRelationResVo : vos) {
                        UserProfileRule userProfileRule = ruleService.getById(userProfileTagsGroupRuleRelationResVo.getRuleId());
                        userProfileTagsGroupRuleRelationResVo.setRuleName(userProfileRule.getRuleName());
                        userProfileTagsGroupRuleRelationResVo.setRuleDetail(userProfileRule.getRuleDetail());
                    }
                    groupRuleRelationResVos.add(vos);
                });
            }

            resVo.setRuleRelationResVos(voList);
            resVo.setGroupRuleRelationResVos(groupRuleRelationResVos);
        }catch (Exception e){
            log.error("操作异常{}" + e);
            return Result.fail("操作异常");
        }

        return Result.succes(resVo);
    }

    @ApiOperation(value = "标签类型获取", notes = "")
    @RequestMapping(value = "/getTagTypeList", method = {RequestMethod.POST})
    public Result<List<TagTypeResVo>> getTagTypeList(@RequestHeader("lang") String lang) {
        List<TagTypeResVo> list = new ArrayList<>();
        try{
            Constants.tagTypeMap.forEach((key, value) -> {
                TagTypeResVo tagTypeResVo = new TagTypeResVo();
                tagTypeResVo.setTypeId(key);
                tagTypeResVo.setTypeName(value);
                if(key==1){
                    tagTypeResVo.setEnglishTypeName("BasicFeatureType");
                }else if(key==2){
                    tagTypeResVo.setEnglishTypeName("BetFeatureType");
                }else if(key==3){
                    tagTypeResVo.setEnglishTypeName("IPFeatureType");
                }else if(key==4){
                    tagTypeResVo.setEnglishTypeName("FinanceFeatureType");
                }
                //国际化
                if(lang.equals("en")){
                    tagTypeResVo.setTypeName(tagTypeResVo.getEnglishTypeName());
                }
                list.add(tagTypeResVo);
            });
        }catch (Exception e){
            log.error("操作异常{}" + e);
            return Result.fail("操作异常");
        }

        return Result.succes(list);
    }

}
