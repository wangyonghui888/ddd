package com.panda.sport.rcs.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.panda.sport.rcs.ProducerSendMessageUtils;
import com.panda.sport.rcs.common.bean.RuleResult;
import com.panda.sport.rcs.common.constants.RedisConstants;
import com.panda.sport.rcs.common.constants.RocketMQConstants;
import com.panda.sport.rcs.common.data.CommonData;
import com.panda.sport.rcs.common.exception.SysException;
import com.panda.sport.rcs.common.utils.CopyUtils;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.common.utils.ThreadUtil;
import com.panda.sport.rcs.common.vo.AddUserTagVo;
import com.panda.sport.rcs.common.vo.UserProfileTagsExtVo;
import com.panda.sport.rcs.common.vo.api.response.UserProfileTagsGroupRuleRelationResVo;
import com.panda.sport.rcs.common.vo.api.response.UserProfileTagsRuleRelationResVo;
import com.panda.sport.rcs.common.vo.api.response.UserProfileUserTagChangeRecordResVo;
import com.panda.sport.rcs.common.vo.rule.RuleParameterVo;
import com.panda.sport.rcs.common.vo.rule.RuleResultDataVo;
import com.panda.sport.rcs.customdb.entity.StaticsUserDateEntity;
import com.panda.sport.rcs.customdb.mapper.StaticsItemExtMapper;
import com.panda.sport.rcs.customdb.mapper.TagExtMapper;
import com.panda.sport.rcs.customdb.service.impl.StaticsItemServiceImpl;
import com.panda.sport.rcs.db.entity.*;
import com.panda.sport.rcs.db.mapper.UserTagLastTimeMapper;
import com.panda.sport.rcs.db.service.*;
import com.panda.sport.rcs.redis.service.RedisService;
import com.panda.sport.rcs.service.IRuleService;
import com.panda.sport.rcs.service.ITagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.common.constants.RedisConstants.DAY_TAST_USER_RULE_RESULT;

/**
 * 标签扫描 Service
 *
 * @author :  lithan
 * @date: 2020-07-02
 */
@Service
public class TagServiceImpl implements ITagService {

    Logger log = LoggerFactory.getLogger(TagServiceImpl.class);

    @Autowired
    IRuleService ruleService;
    @Autowired
    IUserProfileRuleService userProfileRuleService;
    @Autowired
    IUserProfileTagsRuleRelationService userProfileTagsRuleRelationService;
    @Autowired
    IUserProfileTagsGroupRuleRelationService tagsGroupRuleRelationService;
    @Autowired
    IUserProfileTagsService userProfileTagsService;
    @Autowired
    TagExtMapper tagExtMapper;
    @Autowired
    IUserProfileTagUserRelationService userProfileTagUserRelationService;
    @Autowired
    IUserProfileUserTagChangeRecordService userProfileUserTagChangeRecordService;

    @Autowired
    StaticsItemServiceImpl staticsItemService;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    private StaticsItemExtMapper staticsItemExtMapper;

    @Autowired
    private UserTagLastTimeMapper userTagLastTimeMapper;

    @Autowired
    private IUserProfileTagsService tagsService;


    @Autowired
    RedisService redisService;

    @PostConstruct
    public void test() {
        Long time = LocalDateTimeUtil.getDayStartTime(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
        //execute(time, "", 0L);

//        producerSendMessageUtils.sendMessage("user_rule_check_send", "[1]");
//        System.out.println(1);
    }


    /**
     * @param time 统计时间  (规则中 多少天内)会依据此时间作为依据  此参数方便手工调用 统计指定时间数据
     */
    @Override
    public void execute(Long time, String changeManner, Long tagId) {
        Long finalTime = time;
        //获取30天有下注的用户
//        List<Long> userIdList = new ArrayList<>();
//        List<StaticsUserDateEntity> uidList = staticsItemService.fetchUserId(time - 24 * 60 * 60 * 1000L, finalTime);
//        Map<Long, List<StaticsUserDateEntity>> map = uidList.stream().collect(Collectors.groupingBy(StaticsUserDateEntity::getUid));
//        map.forEach((k, v) -> {
//            userIdList.add(k);
//        });
        List<Long> userIdList = tagExtMapper.getUserByTime(time - 30 * 24 * 60 * 60 * 1000L);
        log.info("30天下注的用户数量{}", userIdList.size());
        //新用户
        long userTime = System.currentTimeMillis() - 60 * 24 * 60 * 60 * 1000L;
        List<Long> someTagUserList = tagExtMapper.getUserByTag(userTime);
        userIdList.addAll(someTagUserList);
        log.info("其他标签的用户数量{}", userIdList.size());
        List<Long> finalUserIdList = userIdList.stream().distinct().collect(Collectors.toList());

//        //获取所有标签
        List<UserProfileTagsExtVo> tagList = getTags(null);

        //投注特征标签处理
        doBetTag(finalTime, finalUserIdList, tagList);
    }


    /**
     * 投注特征标签用户 任务处理
     *
     * @param time
     */
    private void doBetTag(Long time, List<Long> betTagUserList, List<UserProfileTagsExtVo> tagList) {
        //记录日志用 真正需要检测的用户
        int chekuserNum = 0;
        log.info("投注特征标签用户时间:{},用户数量:{}:任务处理", time, betTagUserList.size());
        log.info("投注特征标签处理的用户:{}", JSONObject.toJSONString(betTagUserList));
        Long finalTime = time;
        for (int i = 0; i < betTagUserList.size(); i++) {
            Long userId = betTagUserList.get(i);
//            if (userId.compareTo(408876178800250880L) != 0) {
//                continue;
//            }
            //如果是沉默用户 跳过
            Long unactive = staticsItemExtMapper.getUserUnactive(userId);
            if (unactive != null && unactive == 1) {
                log.info("::{}::沉默用户跳过", userId);
                continue;
            }

            //只检测当前
            Long userLevel = staticsItemExtMapper.getUserBetTag(userId);
            if (userLevel == null) {
                log.info("::{}::等级未查到跳过跳过", userId);
                continue;
            }

            UserTagLastTime lastTimeEntity = getLastTime(userId, userLevel);

            int n = i;
            for (UserProfileTagsExtVo tag : tagList) {
                if (userLevel.compareTo(tag.getId()) != 0) {
                    //log.info("{}投注特征检测,非当前标签跳过,当前{}:检测{}", userId, userLevel, tag.getId());
                    continue;
                }
                //非投注特征标签跳过
                if (tag.getTagType() != 2) {
                    continue;
                }
                //是否循环复核（0.否 1.是）默认为0否
                Integer isRecheck = tag.getIsRecheck();
                //标签复核天数
                Integer tagRecheckDays = tag.getTagRecheckDays();
                if (tagRecheckDays == null) {
                    log.info("用户::{}::,{}标签复核天数{}为空", userId, tag.getTagName(), tag.getId());
                    continue;
                }
                if (isRecheck == 0) {
                    log.info("用户::{}::,0标签:{}复核天数:{}:任务时间:{}:上次时间:{}", userId, tag.getTagName(), tagRecheckDays, time, lastTimeEntity == null ? "无" : lastTimeEntity.getLastTime());
                    //如果没记录过 或者时间刚好匹配的上  检查
                    if (lastTimeEntity == null || ((time - lastTimeEntity.getLastTime()) / 86400000 + 1) == tagRecheckDays) {//
                        log.info("用户::{}::,0标签:{}复核天数通过", userId, tag.getTagName());
                    } else {
                        continue;
                    }
                } else if (isRecheck == 1) {
                    log.info("用户::{}::,1标签:{}复核天数:{}:任务时间:{}:上次时间:{}", userId, tag.getTagName(), tagRecheckDays, time, lastTimeEntity == null ? "无" : lastTimeEntity.getLastTime());
                    //如果没记录过 或者时间刚好匹配的上  检查
                    if (lastTimeEntity == null || ((time - lastTimeEntity.getLastTime()) / 86400000 + 1) % tagRecheckDays == 0) {
                        log.info("用户::{}::,0标签:{}复核天数通过", userId, tag.getTagName());
                    } else {
                        continue;
                    }
                }
                log.info("用户::{}::,真正检测标签的用户第{}个", userId, chekuserNum++);
                //周期内是否有投注过
                List<Long> isBetList = staticsItemExtMapper.fetchBetTagUserId(time - (tagRecheckDays + 1) * 24 * 60 * 60 * 1000L, time, userId);
                if (ObjectUtils.isNotEmpty(isBetList) || userLevel == 230L) {
                    ThreadUtil.submit(() -> {
                        try {
                            checkUserTag(tag, userId, finalTime, null);
                        } catch (SysException e) {
                            log.error("用户::{}::,投注特征标签特殊处理:{}", userId, e.getMessage());
                        } catch (Exception e) {
                            log.error("第{}个用户::{}::投注特征标签扫描异常:{}", n, userId, e);
                        }
                        log.info("第{}个用户::{}::投注特征标签扫描完成", n, userId);
                    });
                } else {
                    log.info("第{}个用户::{}::周期无数据", n, userId);
                }
            }
        }
        log.info("一共检测标签用户{}个", chekuserNum);
    }

    private UserTagLastTime getLastTime(Long userId, Long userLevel) {
        LambdaQueryWrapper<UserTagLastTime> userTagLastTimeQueryWrapper = new LambdaQueryWrapper<>();
        userTagLastTimeQueryWrapper.eq(UserTagLastTime::getUserId, userId);
        UserTagLastTime lastTimeEntity = userTagLastTimeMapper.selectOne(userTagLastTimeQueryWrapper);
        if (lastTimeEntity == null) {
            log.info("用户::{}::未查到最后时间", userId);
        }
        Long lastTime = staticsItemExtMapper.getUserTagLastTime(userId);
        if (lastTime != null) {
            log.info("用户::{}::从业务库查到最新", userId);
            lastTimeEntity = new UserTagLastTime();
            lastTimeEntity.setUserId(userId);
            lastTimeEntity.setLastTime(lastTime);
        } else {
            log.info("用户::{}::业务库未查到最后时间", userId);
            if (userLevel == 230L) {
                lastTime = staticsItemExtMapper.getUserCreateTime(userId);
                lastTimeEntity = new UserTagLastTime();
                lastTimeEntity.setUserId(userId);
                lastTimeEntity.setLastTime(lastTime);
                log.info("新用户标签的::{}::创建时间", userId);
            }
        }
        return lastTimeEntity;
    }

    //记录最后时间
    public void doLastTime(Long userId, Integer tagId) {
        //记录最后跑任务的时间
        UserTagLastTime userTagLastTime = new UserTagLastTime();
        userTagLastTime.setUserId(userId);
        userTagLastTime.setLastTime(System.currentTimeMillis());
        userTagLastTime.setRamark("");

        LambdaQueryWrapper<UserTagLastTime> userTagLastTimeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userTagLastTimeLambdaQueryWrapper.eq(UserTagLastTime::getUserId, userId);
        UserTagLastTime queryEntity = userTagLastTimeMapper.selectOne(userTagLastTimeLambdaQueryWrapper);
        if (queryEntity == null) {
            try {
                userTagLastTimeMapper.insert(userTagLastTime);
            } catch (DuplicateKeyException e) {
                //并发情况下会发生重复查询userid 的情况 重新根据userid update
                userTagLastTimeMapper.update(userTagLastTime, new LambdaQueryWrapper<UserTagLastTime>().eq(UserTagLastTime::getUserId, userId));
            }
        } else {
            queryEntity.setRamark("");
            queryEntity.setLastTime(System.currentTimeMillis());
            userTagLastTimeMapper.updateById(queryEntity);
        }
        log.info("用户::{}::用戶标签时间记录 tagId:{}", userId, tagId);
    }

    /**
     * 检查用户是否满足标签
     *
     * @param tag
     * @param userId
     * @
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void checkUserTag(UserProfileTagsExtVo tag, Long userId, Long time, String changeManner) throws Exception {
//        if (userId.compareTo(403428587434283008L) == 0) {
//            System.out.println(1);
//        }
        //自动类型标签
        if (tag.getIsAuto() == 1) {
            changeManner = "System";
            log.info("用户::{}::自动标签开始处理:{}名称:{}", userId, tag.getId(), tag.getTagName());
            List<UserProfileTagsExtVo> tagsExtVoList = getTags(tag.getId());
            log.info("用户::{}::自动标签 循环遍历下级标签:{}名称:{}", userId, tag.getId(), JSONObject.toJSON(tagsExtVoList));
            //循环遍历下级标签是否符合
            for (UserProfileTagsExtVo tagsExtVo : tagsExtVoList) {
                boolean flag = false;
                try {
                    //有任何一个满足 都结束
                    flag = checkUserTagDetail(tagsExtVo, userId, time, changeManner, 1);
                } catch (Exception e) {
                    log.info("用户::{}::自动标签等待计算跳过,名称:{}:{}", userId, tagsExtVo.getTagName(), e.getMessage());
                }
                //如果未计算
                if (!getDayStatus()) {
                    log.info("用户::{}::自动标签 大数据先计算,跳过,名称:{}", userId, tagsExtVo.getTagName());
                    continue;
                }

                if (flag) {
                    log.info("用户::{}::自动标签 循环遍历下级标签满足一个条件:{}", userId, JSONObject.toJSON(tagsExtVo));
                    if (tagsExtVo.getRiskStatus() == 1) {
                        log.info("用户::{}::提交审核跳过:{}", userId, tagsExtVo.getId());
                        return;
                    }
                    userProfileUserTagChangeRecordService.doLastTime(userId, tagsExtVo.getId());
                    doLastTime(userId, tagsExtVo.getId().intValue());
                    return;
                }
            }
            //如果不是默认标签 检查是否符合当前标签规则
            if (tag.getIsDefault() == 0 && tag.getIsCalculate() == 0) {

                log.info("用户::{}::自动标签无下级满足后,不是默认标签:{}检查自己:{}", userId, tag.getId(), tag.getTagName());
                //如果是新用户 设置为默认标签
                if (tag.getId().compareTo(230L) == 0) {
                    UserProfileTags defaultTag = getDefaultTag();
                    boolean isAddLog = true;
                    if (defaultTag.getId() == 208L) {
                        //  http://lan-zentao.sportxxxr1pub.com/bug-view-33347.html
                        //  由于每天都有10万级的新用户7天后自动转成正常用户，在用户标签变更日志表中产生了大量“垃圾数据”，
                        //  为此：用户标签自动化逻辑需要调整，在自动化标签变更逻辑中，当用户标签从新用户变更为正常用户时，不需要记录日志到用户标签变更日志表
                        isAddLog = false;
                    }
                    UserProfileTagsExtVo defaultTagExt = CopyUtils.clone(defaultTag, UserProfileTagsExtVo.class);
                    addUserTag(userId, defaultTagExt, new ArrayList<>(), changeManner, "[{\"result\":\"新用户设置为默认标签@;@\",\"rule\":{ \"ruleDetail\":\"\" }}]", 1,isAddLog);
                    if (tag.getRiskStatus() == 0) {
                        log.info("用户::{}::提交审核跳过:{}", userId, tag.getId());
                        return;
                    }
                    log.info("用户::{}::标签:{}名称:{}新用户初次设置成默认标签", userId, tag.getId(), tag.getTagName());
                    userProfileUserTagChangeRecordService.doLastTime(userId, defaultTagExt.getId());
                    log.info("用户::{}::标签:{}名称:{}新用户初次设置成默认标签", userId, tag.getId(), tag.getTagName());
                    doLastTime(userId, defaultTagExt.getId().intValue());
                    return;
                }


                log.info("用户::{}::自动标签开始处理:{}检查自己:{}", userId, tag.getId(), tag.getTagName());
                boolean flag = false;
                try {
                    flag = checkUserTagDetail(tag, userId, time, changeManner, 1);
                } catch (Exception e) {
                    log.info("用户::{}::自动标签开始处理:{}检查自己跳过:{}:异常:{}", userId, tag.getId(), tag.getTagName(), e.getMessage());
                }
                if (!getDayStatus()) {
                    log.info("用户::{}::自动标签-检查自己 交互跳过{}名称:{}", userId, tag.getId(), tag.getTagName());
                    return;
                }
                //如果不符合当前标签  设置为默认 或者 上级标签
                if (!flag) {
                    //是否退回上级
                    if (tag.getIsRollback() == 1) {
                        //设置为上级标签
                        UserProfileTags parentTag = tagsService.getById(tag.getFatherId());
                        if (ObjectUtils.isNotEmpty(parentTag)) {
                            UserProfileTagsExtVo parentTagExt = CopyUtils.clone(parentTag, UserProfileTagsExtVo.class);
                            addUserTag(userId, parentTagExt, new ArrayList<>(), changeManner, "[{\"result\":\"退回上级,设置为上级标签@;@\",\"rule\":{ \"ruleDetail\":\"\" }}]", 1);
                            log.info("用户::{}::设置为上级标签{}:{}", userId, parentTagExt.getId(), parentTagExt.getTagName());
                            userProfileUserTagChangeRecordService.doLastTime(userId, parentTagExt.getId());
                            log.info("用户::{}::设置为上级标签{}:{}", userId, parentTagExt.getId(), parentTagExt.getTagName());
                            doLastTime(userId, parentTagExt.getId().intValue());
                        } else {
                            //设置为默认标签
                            UserProfileTags defaultTag = getDefaultTag();
                            UserProfileTagsExtVo defaultTagExt = CopyUtils.clone(defaultTag, UserProfileTagsExtVo.class);
                            addUserTag(userId, defaultTagExt, new ArrayList<>(), changeManner, "[{\"result\":\"退回上级,无上级标签,设置为默认标签@;@\",\"rule\":{ \"ruleDetail\":\"\" }}]", 1);
                            log.info("用户::{}::标签:{}名称:{}无上级标签,设置成默认标签", userId, tag.getId(), tag.getTagName());
                            userProfileUserTagChangeRecordService.doLastTime(userId, defaultTagExt.getId());
                            log.info("用户::{}::标签:{}名称:{}无上级标签,设置成默认标签", userId, tag.getId(), tag.getTagName());
                            doLastTime(userId, defaultTagExt.getId().intValue());
                        }

                    } else {
                        //设置为默认标签
                        UserProfileTags defaultTag = getDefaultTag();
                        UserProfileTagsExtVo defaultTagExt = CopyUtils.clone(defaultTag, UserProfileTagsExtVo.class);
                        addUserTag(userId, defaultTagExt, new ArrayList<>(), changeManner, "[{\"result\":\"不退回上级,设置为默认标签@;@\",\"rule\":{ \"ruleDetail\":\"\" }}]", 1);
                        log.info("用户::{}::无需返回上级设置为默认标签{}:{}", userId, defaultTagExt.getId(), defaultTag.getTagName());
                        userProfileUserTagChangeRecordService.doLastTime(userId, defaultTagExt.getId());
                        log.info("用户::{}::无需返回上级设置为默认标签{}:{}", userId, defaultTagExt.getId(), defaultTag.getTagName());
                        doLastTime(userId, defaultTagExt.getId().intValue());
                    }
                }
            }

        } else {//非自动类型标签
            checkUserTagDetail(tag, userId, time, changeManner, 2);
        }

    }

    //获取默认标签
    private UserProfileTags getDefaultTag() {
        String key = "rcs:risk:userprofile:default:tag";
        Object data = redisService.get(key);
        if (data != null) {
            UserProfileTags tag = (UserProfileTags) redisService.get(key);
            return tag;
        }
        LambdaQueryWrapper<UserProfileTags> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserProfileTags::getIsDefault, 1);
        List<UserProfileTags> list = tagsService.list(lambdaQueryWrapper);
        if (ObjectUtils.isEmpty(list)) {
            log.info("获取默认标签失败");
        }
        redisService.set(key, list.get(0), 30);
        return list.get(0);
    }

    public Boolean checkUserTagDetail(UserProfileTagsExtVo tag, Long userId, Long time, String changeManner, Integer changeType) throws Exception {
        log.info("用户::{}::标签扫描标签:{}开始", userId, tag.getTagName());
        //一个标签有多个规则
        List<UserProfileTagsRuleRelationResVo> relationVoList = tag.getRelationVoList();
        //组合规则
        List<List<UserProfileTagsGroupRuleRelationResVo>> groupRuleRelationResVosList = tag.getGroupRuleRelationResVos();
        if (ObjectUtils.isEmpty(groupRuleRelationResVosList)) {
            groupRuleRelationResVosList = new ArrayList<>();
        }

        //判断用户是否满足 单个规则
        List<RuleResult<List<RuleResultDataVo>>> ruleResultList = new ArrayList<>();
        for (UserProfileTagsRuleRelationResVo ruleRelationVo : relationVoList) {
            RuleResult<List<RuleResultDataVo>> result = checkRule(userId, ruleRelationVo, time);
            //如果未计算
            if (!getDayStatus()) {
                continue;
            }
            result.setRuleCode(ruleRelationVo.getRuleCode());
            result.setUserId(userId);
            ruleResultList.add(result);
        }

        //判断用户是否满足组合规则
        for (List<UserProfileTagsGroupRuleRelationResVo> groupList : groupRuleRelationResVosList) {
            RuleResult result = checkGroupRule(userId, groupList, time);
            String groupCodes = "";
            for (UserProfileTagsGroupRuleRelationResVo userProfileTagsGroupRuleRelationResVo : groupList) {
                groupCodes += userProfileTagsGroupRuleRelationResVo.getRuleCode() + ",";
            }
            result.setRuleCode(groupCodes);
            result.setUserId(userId);
            ruleResultList.add(result);
            log.info("用户::{}::标签扫描 标签组合:{}结束", userId, tag.getTagName());
        }
        //如果未计算
        if (!getDayStatus()) {
            throw new SysException("向大数据发送mq数据:标签整体跳过" + userId);
        }
        log.info("用户::{}::标签扫描结果用户标签:{},综合结果:{}", userId, tag.getTagName(), JSONObject.toJSONString(ruleResultList));
        //判断是否全部满足规则
        for (RuleResult result : ruleResultList) {
            //如果不满足规则 只需判断是否有记录变更
            if (!result.getFlag()) {
                removeTagChange(userId, tag, ruleResultList, changeManner, "", changeType);
                //记录最后跑任务的时间
                //doLastTime(userId, tag);
                return false;
            }
        }

        //如果满足
        addUserTag(userId, tag, ruleResultList, changeManner, "", changeType);
        //记录最后跑任务的时间
        //doLastTime(userId, tag);

        log.info("用户::{}::标签扫描 标签:{}结束", userId, tag.getTagName());
        return true;
    }

    /**
     * 有的标签变更不需要记录日志
     * http://lan-zentao.sportxxxr1pub.com/bug-view-33347.html
     * 由于每天都有10万级的新用户7天后自动转成正常用户，在用户标签变更日志表中产生了大量“垃圾数据”，为此：用户标签自动化逻辑需要调整，在自动化标签变更逻辑中，当用户标签从新用户变更为正常用户时，不需要记录日志到用户标签变更日志表
     *
     * @param userId
     * @param tag
     * @param ruleResultList
     * @param changeManner
     * @param remark
     * @param changeType
     */
    @Override
    public void addUserTag(Long userId, UserProfileTagsExtVo tag, List<RuleResult<List<RuleResultDataVo>>> ruleResultList, String changeManner, String remark, Integer changeType, boolean isAddLog) {
        log.info("用户::{}::用户标签进入新增逻辑:{}:{}", userId, tag.getId(), changeManner);
        //特殊兼容处理
        Long userLevel = staticsItemExtMapper.getUserBetTag(userId);
        if (userLevel.compareTo(tag.getId()) == 0) {
            log.info("用户::{}::新增,业务库原标签为{},预警跳过", userId, userLevel);
            return;
        }

        String[] userInfo = this.getUserNameAndMerchant(userId);
        String merchantsData = redisService.getString("rcs:riskstatus:merchants:data");
        String merchantsRiskStatus = "0";
        Map<String, String> merchantsDataMap = JSONObject.parseObject(merchantsData, Map.class);
        if (merchantsDataMap != null && merchantsDataMap.get(userInfo[1]) != null) {
            merchantsRiskStatus = merchantsDataMap.get(userInfo[1]);
        }
        if (tag.getRiskStatus() == 1 && changeType == 1 && merchantsRiskStatus.equals("1")) {
            AddUserTagVo addUserTagVo = new AddUserTagVo(userId, tag, ruleResultList, changeManner, remark, changeType);
            producerSendMessageUtils.sendMessage("rcs_risk_merchant_manager_task_tag", JSONObject.toJSONString(addUserTagVo));
            log.info("用户::{}::需要商户确认的标签便变更 addUserTag跳过:{}", userId, tag.getTagName());
            return;
        }

        //如果是新增的 则记录  标签变更记录表
        UserProfileUserTagChangeRecord userProfileUserTagChangeRecord = new UserProfileUserTagChangeRecord();
        //记录到  用户标签关系表
        UserProfileTagUserRelation userProfileTagUserRelation = new UserProfileTagUserRelation();
        userProfileTagUserRelation.setTagId(tag.getId());
        userProfileTagUserRelation.setUserId(String.valueOf(userId));
        //规则类型 1基本属性类 2投注特征类 3访问特征类 4财务特征类  ,访问特征类、财务特征类标签，若计算结果符合标签判断条件，则自动变更用户的这两类标签
        if (changeType == 2) {
            userProfileTagUserRelation.setStatus(0);
        } else {
            userProfileTagUserRelation.setStatus(1);
        }

        boolean flag = userProfileTagUserRelationService.save(userProfileTagUserRelation);
        if (flag) {
            //往业务推消息（用户画像V1.6这期需求 如果触发的为财务特征类的标签则发消息，其他类型则不发）
            if (tag.getTagType().equals(4) || tag.getIsAuto() == 1 || changeType == 1) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", 1);
                map.put("remark", "变更用户对应标签");
                map.put("userId", userId);
                map.put("tagId", tag.getId());
                map.put("tagType", tag.getTagType());
                producerSendMessageUtils.sendMessage(RocketMQConstants.USER_TAG_CHANGE_TOPIC, RocketMQConstants.USER_TAG_CHANGE_TAG, userId.toString(), JSONObject.toJSONString(map));
                //向业务发送后 线程休眠20毫秒
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Map<String, Object> changeMap = new HashMap<>();
                changeMap.put("userId", userId);
                changeMap.put("tagId", tag.getId());
                producerSendMessageUtils.sendMessage("RCS_LIMIT_USER_TAG_CHANGE","",userId.toString(), JSONObject.toJSONString(changeMap));
                log.info("用户::{}::变更用户对应标签’消息发送成功",userId);
            }
        }

        if (!isAddLog) {
            return;
        }
        userProfileUserTagChangeRecord.setUserId(userId);
        userProfileUserTagChangeRecord.setChangeBefore(userLevel);
        userProfileUserTagChangeRecord.setChangeAfter(tag.getId());
        userProfileUserTagChangeRecord.setChangeTag(tag.getId());
        userProfileUserTagChangeRecord.setChangeDetail(userId + "用户新增了标签:" + tag.getTagName());
        userProfileUserTagChangeRecord.setChangeTime(System.currentTimeMillis());

        //新字段
        List<RuleResultDataVo> realityList = new ArrayList<>();
        for (RuleResult<List<RuleResultDataVo>> ruleResult : ruleResultList) {
            List<RuleResultDataVo> dataList = ruleResult.getData();
            realityList.addAll(dataList);
        }
        StringBuffer str = new StringBuffer(JSONObject.toJSONString(realityList));
        if (str.indexOf("|") != -1) {
            str.setCharAt(str.lastIndexOf("|"), ' ');//替换特定字符
        }
        userProfileUserTagChangeRecord.setRealityValue(str.toString());
        if (StringUtils.isNotEmpty(remark)) {
            userProfileUserTagChangeRecord.setRealityValue(remark);
        }
        userProfileUserTagChangeRecord.setChangeSuggest("1");
        if (changeType == 2) {
            userProfileUserTagChangeRecord.setStatus(0);
        } else {
            userProfileUserTagChangeRecord.setStatus(1);
        }
//            userProfileUserTagChangeRecord.setOperateTime(System.currentTimeMillis());

        if (ObjectUtils.isEmpty(changeManner)) {
            userProfileUserTagChangeRecord.setChangeManner("System");
            userProfileUserTagChangeRecord.setChangeReason("");
        } else {
            userProfileUserTagChangeRecord.setChangeManner(changeManner);
            userProfileUserTagChangeRecord.setChangeReason("标签规则调整");
        }
        userProfileUserTagChangeRecord.setChangeType(changeType);


        userProfileUserTagChangeRecord.setTagType(tag.getTagType());
        userProfileUserTagChangeRecord.setChangeValue(JSONObject.toJSONString(ruleResultList));

        userProfileUserTagChangeRecord.setUserName(userInfo[0]);
        userProfileUserTagChangeRecord.setMerchantCode(userInfo[1]);
        userProfileUserTagChangeRecord.setOperateTime(System.currentTimeMillis());
        userProfileUserTagChangeRecordService.save(userProfileUserTagChangeRecord);
        log.info("用户::{}::标签扫描 新增标签 标签:{} ", userId, tag.getTagName());
    }

    /**
     * @param userId
     * @param tag
     * @param ruleResultList
     * @param changeManner
     * @param remark
     * @param changeType     1自动 2手动
     */
    @Override
    public void addUserTag(Long userId, UserProfileTagsExtVo tag, List<RuleResult<List<RuleResultDataVo>>> ruleResultList, String changeManner, String remark, Integer changeType) {
        addUserTag(userId, tag, ruleResultList, changeManner, remark, changeType, true);
    }

    /**
     * 扫描出用户不满足该标签后   检查用户标签 之前是否存在  如果存在 需要记录变更记录
     *
     * @param userId
     * @param tag
     * @param ruleResultList
     * @
     */
    private void removeTagChange(Long userId, UserProfileTagsExtVo tag, List<RuleResult<List<RuleResultDataVo>>> ruleResultList, String changeManner, String remark, Integer changeType) {
        //特殊兼容处理
        Long userLevel = staticsItemExtMapper.getUserBetTag(userId);
        if (userLevel == null) {
            userLevel = 208L;
        }
        if ((userLevel.compareTo(208L) == 0) && tag.getTagType() == 2) {
            log.info("{},取消,业务库原标签为{},预警跳过", userId, userLevel);
            return;
        }

        //如果是新增的 则记录  标签变更记录表
        UserProfileUserTagChangeRecord userProfileUserTagChangeRecord = new UserProfileUserTagChangeRecord();
        //标签是否已经存在
        LambdaQueryWrapper<UserProfileTagUserRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserProfileTagUserRelation::getUserId, userId);
        wrapper.eq(UserProfileTagUserRelation::getTagId, tag.getId());
        List<UserProfileTagUserRelation> list = userProfileTagUserRelationService.list(wrapper);
        //如果存在
        if (ObjectUtils.isNotEmpty(list)) {
            //删除标签关系表
            LambdaQueryWrapper<UserProfileTagUserRelation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(UserProfileTagUserRelation::getTagId, tag.getId());
            lambdaQueryWrapper.eq(UserProfileTagUserRelation::getUserId, userId);

            boolean flag = userProfileTagUserRelationService.remove(lambdaQueryWrapper);
            //往业务推消息（用户画像V1.6这期需求 如果触发的为财务特征类的标签则发消息，其他类型则不发）
            if (tag.getTagType().equals(4)) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", 2);
                map.put("remark", "删除用户对应标签");
                map.put("userId", userId);
                map.put("tagId", tag.getId());
                map.put("tagType", tag.getTagType());
                producerSendMessageUtils.sendMessage(RocketMQConstants.USER_TAG_CHANGE_TOPIC, RocketMQConstants.USER_TAG_CHANGE_TAG, JSONObject.toJSONString(map));
                //向业务发送后 线程休眠20毫秒
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Map<String, Object> changeMap = new HashMap<>();
                changeMap.put("userId", userId);
                producerSendMessageUtils.sendMessage("RCS_LIMIT_USER_TAG_CHANGE","",userId.toString(), JSONObject.toJSONString(changeMap));
                log.info("{}删除用户对应标签’消息发送成功", RocketMQConstants.USER_TAG_CHANGE_TAG);
            }


            userProfileUserTagChangeRecord.setUserId(userId);
            userProfileUserTagChangeRecord.setChangeBefore(list.get(0).getTagId());
            userProfileUserTagChangeRecord.setChangeAfter(208L);
            userProfileUserTagChangeRecord.setChangeTag(208L);
            userProfileUserTagChangeRecord.setChangeDetail(userId + "用户删除了标签:" + tag.getTagName());
            userProfileUserTagChangeRecord.setChangeTime(System.currentTimeMillis());

            //新字段
            List<RuleResultDataVo> realityList = new ArrayList<>();
            for (RuleResult<List<RuleResultDataVo>> ruleResult : ruleResultList) {
                List<RuleResultDataVo> dataList = ruleResult.getData();
                realityList.addAll(dataList);
            }
            StringBuffer str = new StringBuffer(JSONObject.toJSONString(realityList));
            if (str.indexOf("|") != -1) {
                str.setCharAt(str.lastIndexOf("|"), ' ');//替换特定字符
            }
            userProfileUserTagChangeRecord.setRealityValue(str.toString());
            if (StringUtils.isNotEmpty(remark)) {
                userProfileUserTagChangeRecord.setRealityValue(remark);
            }
            userProfileUserTagChangeRecord.setChangeSuggest("2");
            if (changeType == 2) {
                userProfileUserTagChangeRecord.setStatus(0);
            } else {
                userProfileUserTagChangeRecord.setStatus(1);
            }
//            userProfileUserTagChangeRecord.setOperateTime(System.currentTimeMillis());

            if (ObjectUtils.isEmpty(changeManner)) {
                userProfileUserTagChangeRecord.setChangeManner("System");
                userProfileUserTagChangeRecord.setChangeReason("");
            } else {
                userProfileUserTagChangeRecord.setChangeManner(changeManner);
                userProfileUserTagChangeRecord.setChangeReason("标签规则调整");
            }
            userProfileUserTagChangeRecord.setChangeType(changeType);

            userProfileUserTagChangeRecord.setChangeValue(JSONObject.toJSONString(ruleResultList));
            userProfileUserTagChangeRecord.setTagType(tag.getTagType());
            String[] userInfo = this.getUserNameAndMerchant(userId);
            userProfileUserTagChangeRecord.setUserName(userInfo[0]);
            userProfileUserTagChangeRecord.setMerchantCode(userInfo[1]);
            userProfileUserTagChangeRecord.setOperateTime(System.currentTimeMillis());
            userProfileUserTagChangeRecordService.save(userProfileUserTagChangeRecord);
            log.info("标签扫描 删除标签 用户:{}标签:{} ", userId, tag.getTagName());
        }
    }

    /**
     * 检查用户是否符合该条规则
     *
     * @param userId
     * @param ruleRelation
     * @return
     * @
     */
    private RuleResult<List<RuleResultDataVo>> checkRule(Long userId, UserProfileTagsRuleRelationResVo ruleRelation, Long time) throws Exception {
        RuleParameterVo vo = new RuleParameterVo();
//        BeanUtils.copyProperties(ruleRelation, vo);
        vo.setParameter1(ruleRelation.getParameter1());
        vo.setParameter2(ruleRelation.getParameter2());
        vo.setParameter3(ruleRelation.getParameter3());
        vo.setParameter4(ruleRelation.getParameter4());
        vo.setParameter5(ruleRelation.getParameter5());
        vo.setParameter6(ruleRelation.getParameter6());
        vo.setUserId(userId);
        vo.setTime(time);
        vo.setRuleCode(ruleRelation.getRuleCode());
        vo.setTagId(ruleRelation.getTagId().longValue());
        //如果未计算
        if (!getDayStatus()) {
            CommonData.userRuledataMap.put(vo.getTagId() + vo.getRuleCode() + vo.getUserId(), vo);
            log.info("标签扫描 用户:{}规则:{}向大数据发送mq数据", userId, vo.getRuleCode());
//            throw new SysException("向大数据发送mq数据" + userId + "-" + vo.getRuleCode());
            return null;
        }

        //通过反射 执行对应的方法
//        Method method = ruleService.getClass().getMethod(ruleRelation.getRuleCode().toLowerCase(), RuleParameterVo.class);
//        RuleResult ruleResult = (RuleResult) method.invoke(ruleService, vo);
        String resultKey = String.format(DAY_TAST_USER_RULE_RESULT, vo.getTagId(), vo.getUserId(), vo.getRuleCode());
        RuleResult ruleResult = (RuleResult) redisService.get(resultKey);
        if (ruleResult == null) {
            log.info("用户{}规则未获取到大数据的统计结果{}", userId, ruleRelation.getRuleCode());
            throw new SysException("用户" + userId + "规则未获取到大数据的统计结果" + ruleRelation.getRuleCode());
        }
        String data = String.format("【%s】%s", ruleRelation.getRuleName(), ruleResult.getData().toString());
        data += "@;@";
        log.info("标签扫描 用户:{},tagId:{},ruleCode:{},结果:{}", userId, ruleRelation.getTagId(), ruleRelation.getRuleCode(), JSONObject.toJSON(ruleResult));

        //组装统一格式
        List<RuleResultDataVo> list = new ArrayList<>();
        RuleResultDataVo dataVo = new RuleResultDataVo();
        dataVo.setRule(ruleRelation);
        dataVo.setResult(data);
        list.add(dataVo);
        ruleResult.setData(list);
        return ruleResult;
    }

    private RuleResult<List<RuleResultDataVo>> checkGroupRule(Long userId, List<UserProfileTagsGroupRuleRelationResVo> groupList, Long time) throws Exception {

        //组装统一格式
        List<RuleResultDataVo> list = new ArrayList<>();
        Boolean flag = false;
        for (int i = 0; i < groupList.size(); i++) {
            UserProfileTagsGroupRuleRelationResVo userProfileTagsGroupRuleRelationResVo = groupList.get(i);
            UserProfileTagsRuleRelationResVo ruleRelation = CopyUtils.clone(userProfileTagsGroupRuleRelationResVo, UserProfileTagsRuleRelationResVo.class);
            RuleParameterVo vo = new RuleParameterVo();
//            BeanUtils.copyProperties(ruleRelation, vo);
            vo.setParameter1(ruleRelation.getParameter1());
            vo.setParameter2(ruleRelation.getParameter2());
            vo.setParameter3(ruleRelation.getParameter3());
            vo.setParameter4(ruleRelation.getParameter4());
            vo.setParameter5(ruleRelation.getParameter5());
            vo.setParameter6(ruleRelation.getParameter6());
            vo.setTagId(ruleRelation.getTagId().longValue());
            vo.setUserId(userId);
            vo.setTime(time);
            vo.setRuleCode(ruleRelation.getRuleCode());
            //如果未计算
            if (!getDayStatus()) {
                CommonData.userRuledataMap.put(vo.getTagId() + vo.getRuleCode() + vo.getUserId(), vo);
                log.info("用户::{}::标签扫描规则:{}向大数据发送mq数据", userId, vo.getRuleCode());
                //如果未计算
                if (!getDayStatus()) {
                    continue;
                }
            }
            //通过反射 执行对应的方法
//            Method method = ruleService.getClass().getMethod(ruleRelation.getRuleCode().toLowerCase(), RuleParameterVo.class);
//            RuleResult ruleResult = (RuleResult) method.invoke(ruleService, vo);
            String resultKey = String.format(DAY_TAST_USER_RULE_RESULT, vo.getTagId(), vo.getUserId(), vo.getRuleCode());
            RuleResult ruleResult = (RuleResult) redisService.get(resultKey);
            if (ruleResult == null) {
                log.info("用户::{}::规则未获取到大数据的统计结果{}", userId, ruleRelation.getRuleCode());
                throw new SysException("用户" + userId + "规则未获取到大数据的统计结果" + ruleRelation.getRuleCode());
            }
            //这里用code字段 存规则名称
            ruleResult.setRuleCode(ruleRelation.getRuleName());
            log.info("标签扫描 用户::{}::,tagId:{},ruleCode:{},组合结果:{}", userId, ruleRelation.getTagId(), ruleRelation.getRuleName(), JSONObject.toJSON(ruleResult));
            if (!flag && ruleResult.getFlag()) {
                flag = true;
            }
            String data = String.format("【%s】%s", ruleRelation.getRuleName(), ruleResult.getData().toString());
            //前段换行
            if (i == groupList.size() - 1) {
                data += "@;@";
            }
            RuleResultDataVo dataVo = new RuleResultDataVo();
            dataVo.setRule(ruleRelation);
            dataVo.setResult(data);
            list.add(dataVo);
        }
        return RuleResult.init(flag, list);
    }

    /**
     * 获取昨天有下注的用户
     */
    private List<Long> getUserIdList(Long time) {
        //获取时间段
        Long startTime = time - 24 * 60 * 60 * 1000L;
        Long endTime = time;
        List<Long> userIdList = tagExtMapper.getUserId(startTime, endTime);
        return userIdList;
    }

    /**
     * 获取所有tag
     *
     * @return
     * @
     */
    public List<UserProfileTagsExtVo> getTags(Long parentId) {
        //获取所有标签 排除停止的
        LambdaQueryWrapper<UserProfileTags> tagsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tagsLambdaQueryWrapper.eq(UserProfileTags::getIsCalculate, 0);
        tagsLambdaQueryWrapper.eq(UserProfileTags::getTagType, 2);
//        tagsLambdaQueryWrapper.ne(UserProfileTags::getId, 201L);
//        tagsLambdaQueryWrapper.ne(UserProfileTags::getId, 200L);
        tagsLambdaQueryWrapper.eq(ObjectUtils.isNotEmpty(parentId), UserProfileTags::getFatherId, parentId);
        tagsLambdaQueryWrapper.orderByAsc(UserProfileTags::getFatherId);
        List<UserProfileTags> tagList = userProfileTagsService.list(tagsLambdaQueryWrapper);
        log.info("需要计算的标签:{}", JSONObject.toJSON(tagList));
        List<UserProfileTagsExtVo> tagsExtVosList = new ArrayList<>();

        //设置标签对应的规则
        for (UserProfileTags userProfileTags : tagList) {
            UserProfileTagsExtVo userProfileTagsExtVo = CopyUtils.clone(userProfileTags, UserProfileTagsExtVo.class);
            //获取标签对应的规则关系 一个标签可能有多个规则
            LambdaQueryWrapper<UserProfileTagsRuleRelation> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserProfileTagsRuleRelation::getTagId, userProfileTagsExtVo.getId());
            List<UserProfileTagsRuleRelation> relationList = userProfileTagsRuleRelationService.list(queryWrapper);

            List<UserProfileTagsRuleRelationResVo> relationVoList = CopyUtils.clone(relationList, UserProfileTagsRuleRelationResVo.class);
            for (UserProfileTagsRuleRelationResVo relationVo : relationVoList) {
                UserProfileRule userProfileRule = userProfileRuleService.getById(relationVo.getRuleId());
                relationVo.setRuleCode(userProfileRule.getRuleCode());
                relationVo.setRuleName(userProfileRule.getRuleName());
                relationVo.setRuleDetail(userProfileRule.getRuleDetail());
            }
            userProfileTagsExtVo.setRelationVoList(relationVoList);


            //组合标签 一个标签可能有多个规则
            LambdaQueryWrapper<UserProfileTagsGroupRuleRelation> groupQueryWrapper = new LambdaQueryWrapper<>();
            groupQueryWrapper.eq(UserProfileTagsGroupRuleRelation::getTagId, userProfileTagsExtVo.getId());
            List<UserProfileTagsGroupRuleRelation> groupRuleRelationList = tagsGroupRuleRelationService.list(groupQueryWrapper);

            if (ObjectUtils.isEmpty(relationList) && ObjectUtils.isEmpty(groupRuleRelationList)) {
                log.info("标签关系未配置任何规则:{}", JSONObject.toJSONString(userProfileTagsExtVo));
                //continue;
            }
            Map<Long, List<UserProfileTagsGroupRuleRelation>> groupMap = groupRuleRelationList.stream().collect(Collectors.groupingBy(UserProfileTagsGroupRuleRelation::getGroupId));
            List<List<UserProfileTagsGroupRuleRelationResVo>> groupRuleRelationResVos = new ArrayList<>();
            groupMap.forEach((k, v) -> {
                List<UserProfileTagsGroupRuleRelationResVo> groupVoList = CopyUtils.clone(v, UserProfileTagsGroupRuleRelationResVo.class);
                for (UserProfileTagsGroupRuleRelationResVo relationVo : groupVoList) {
                    UserProfileRule userProfileRule = userProfileRuleService.getById(relationVo.getRuleId());
                    relationVo.setRuleCode(userProfileRule.getRuleCode());
                    relationVo.setRuleName(userProfileRule.getRuleName());
                    relationVo.setRuleDetail(userProfileRule.getRuleDetail());
                }
                groupRuleRelationResVos.add(groupVoList);
            });
            userProfileTagsExtVo.setGroupRuleRelationResVos(groupRuleRelationResVos);
            tagsExtVosList.add(userProfileTagsExtVo);
        }
        return tagsExtVosList;
    }

    /**
     * 根据用户ID查询用户名称和商户编码
     *
     * @param userId
     * @return
     */
    private String[] getUserNameAndMerchant(Long userId) {
        UserProfileUserTagChangeRecordResVo vo = userProfileUserTagChangeRecordService.selectByUserId(userId);
        return new String[]{vo.getUserName(), vo.getMerchantCode()};
    }

    /**
     * 检测今天数据是否处理完毕
     *
     * @return
     */
    public boolean getDayStatus() {
        String key = String.format(RedisConstants.DAY_TAST_STATUS, LocalDateTimeUtil.now("yyyyMMdd"));
        Object data = redisService.get(key);
        if (ObjectUtils.isEmpty(data)) {
            return false;
        }
        return true;
    }
}
