package com.panda.sport.rcs.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.ProducerSendMessageUtils;
import com.panda.sport.rcs.common.constants.RocketMQConstants;
import com.panda.sport.rcs.common.exception.SysException;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.common.utils.ThreadUtil;
import com.panda.sport.rcs.common.vo.UserProfileTagsExtVo;
import com.panda.sport.rcs.common.vo.api.request.TUserGroupBetRateReqVo;
import com.panda.sport.rcs.common.vo.api.request.UserGroupBetRateReqVo;
import com.panda.sport.rcs.customdb.entity.BasketBallWinEntity;
import com.panda.sport.rcs.customdb.mapper.StaticsItemExtMapper;
import com.panda.sport.rcs.customdb.mapper.TagExtMapper;
import com.panda.sport.rcs.customdb.service.impl.StaticsItemServiceImpl;
import com.panda.sport.rcs.db.service.*;
import com.panda.sport.rcs.service.IBastetBallService;
import com.panda.sport.rcs.service.IRuleService;
import com.panda.sport.rcs.service.ITagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 标签扫描 Service
 *
 * @author :  lithan
 * @date: 2020-07-02
 */
@Service
public class BasketBallServiceImpl implements IBastetBallService {


    Logger log = LoggerFactory.getLogger(BasketBallServiceImpl.class);

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
    StaticsItemExtMapper staticsItemExtMapper;

    @Autowired
    ITagService tagService;

    @PostConstruct
    public void test() {
        Long time = LocalDateTimeUtil.getDayStartTime(System.currentTimeMillis());
//        executeBasetToOther(time);
    }

    /**
     * @param time 统计时间  (规则中 多少天内)会依据此时间作为依据  此参数方便手工调用 统计指定时间数据
     */
    @Override
    public void execute(Long time) {
        Long finalTime = time;
        //获取昨天有下注篮球的用户
        List<Long> userIdList = staticsItemService.fetchBasketBallUserId(time - 24 * 60 * 60 * 1000L, finalTime);
        for (int i = 0; i < userIdList.size(); i++) {
            log.info("篮球标签第{}/{}个用户处理中", i + 1, userIdList.size());
            Long userId = userIdList.get(i);
            ThreadUtil.submit(() -> {
                try {
                    Thread.currentThread().setName("Thread-" + userId);
                    ckeck(userId);
                } catch (Exception e) {
                    log.info("用戶{}篮球标签计算异常:{}:{}", userId, e.getMessage(), e);
                }
            });
        }
    }

    private void ckeck(Long userId) {
        Long userLevel = staticsItemExtMapper.getUserBetTag(userId);
        log.info("用戶{}当前标签:{}", userId, userLevel);
        if (userLevel != 208L && userLevel != 230L && userLevel != 200L) {
            log.info("用户非(普通用户/专注篮球赌客)..跳过");
            return;
        }

        //用户是否只投注篮球
        Boolean flag = false;
        List<Long> userSportList = staticsItemExtMapper.getAllSportIdByUser(userId);
        if (userSportList != null && userSportList.size() == 1 && userSportList.get(0) == 2) {
            flag = true;
        }
        log.info("用户{}是否只投注篮球:{}:{}", userId, flag, JSONObject.toJSONString(userSportList));

        //修改赛种货量百分比的对象
        UserGroupBetRateReqVo vo = new UserGroupBetRateReqVo();
        List<TUserGroupBetRateReqVo> userGroupBetRateList = new ArrayList<>();
        TUserGroupBetRateReqVo userGroupBetRateReqVo = new TUserGroupBetRateReqVo();
        userGroupBetRateReqVo.setSportId(2);

        //篮球胜率查询
        BasketBallWinEntity basketBallWinEntity = staticsItemExtMapper.getBasketBallWinNum(userId);
        log.info("用戶{}胜率对象:{}", userId, JSONObject.toJSONString(basketBallWinEntity));

        Map<String, Object> map = new HashMap<>();
        map.put("type", 1);
        map.put("remark", "变更用户对应标签");
        map.put("userId", userId);
        map.put("tagType", 2);
        //正常用户
        if (userLevel == 208L || userLevel == 230L) {
            if (flag) { //只投注篮球
                map.put("tagId", 200);
                //如果用户篮球胜率>50%，则设置用户特殊货量的篮球赛种货量百分比设置为80%；
                if (basketBallWinEntity.getPercentage().compareTo(new BigDecimal("0.5")) > 0) {
                    userGroupBetRateReqVo.setBetRate(new BigDecimal("80"));
                } else if (basketBallWinEntity.getPercentage().compareTo(new BigDecimal("0.5")) <= 0) {
                    userGroupBetRateReqVo.setBetRate(new BigDecimal("20"));
                }
            } else {//不止投注篮球
                map.put("tagId", 201);
//                userGroupBetRateReqVo.setBetRate(new BigDecimal("50"));
            }
        } else if (userLevel == 200) {//针对专注篮球赌客
            if (flag) { //只投注篮球
                map.put("tagId", 200);
                if (basketBallWinEntity.getPercentage().compareTo(new BigDecimal("0.5")) > 0) {
                    userGroupBetRateReqVo.setBetRate(new BigDecimal("80"));
                } else if (basketBallWinEntity.getPercentage().compareTo(new BigDecimal("0.5")) <= 0) {
                    userGroupBetRateReqVo.setBetRate(new BigDecimal("20"));
                }
            } else {
                map.put("tagId", 201);
//                userGroupBetRateReqVo.setBetRate(new BigDecimal("50"));
            }
        }

        userGroupBetRateList.add(userGroupBetRateReqVo);
        vo.setUserGroupBetRateList(userGroupBetRateList);

        if (!Long.valueOf(map.get("tagId").toString()).equals(userLevel)) {
            producerSendMessageUtils.sendMessage(RocketMQConstants.USER_TAG_CHANGE_TOPIC, RocketMQConstants.USER_TAG_CHANGE_TAG,userId.toString(), JSONObject.toJSONString(map));
            Map<String, Object> changeMap = new HashMap<>();
            changeMap.put("userId", userId);
            changeMap.put("tagId", map.get("tagId").toString());
            producerSendMessageUtils.sendMessage("RCS_LIMIT_USER_TAG_CHANGE","",userId.toString(), JSONObject.toJSONString(changeMap));
            log.info("变更用户对应标签发送:{}", JSONObject.toJSONString(map));
        } else {
            log.info("变更用户对应标签无需发送:{}", userId);
        }

        List rateUserList = new ArrayList();
        rateUserList.add(userId);
        //发消息到trade，修改该玩家组内所有用户按照当前玩家组的“风控措施”配置进行更新
        Map<String, Object> rateMap = new HashMap<>();
        rateMap.put("userList", rateUserList);
        rateMap.put("betRateConfig", vo);
        rateMap.put("modifyUser", "SystemAuto");
        producerSendMessageUtils.sendMessage("USER_GROUP_BET_RATE_TOPIC", "USER_GROUP_BET_RATE_TAG", JSONObject.toJSONString(rateMap));
        log.info("变更篮球货量百分比发送:{}", JSONObject.toJSONString(rateMap));
    }

    /**
     * 专注/部分篮球赌客转ufo-$或好脚
     *
     * @param time
     */
    @Override
    public void executeBasetToOther(Long time) {
        //所有篮球标签的用户
        List<Long> userIdList = tagExtMapper.getBasketBallUser();
        log.info("篮球赌客转ufo-$或好脚用户数量:{}", userIdList.size());
        log.info("篮球赌客转ufo-$或好脚的用户:{}", JSONObject.toJSONString(userIdList));
        //获取所有标签
        List<UserProfileTagsExtVo> tagList = tagService.getTags(null);
        List<UserProfileTagsExtVo> collect = tagList.stream().filter(o -> o.getId() == 3L || o.getId() == 139L).collect(Collectors.toList());
        //标签处理
        doTag(userIdList, collect, time);
    }

    private void doTag(List<Long> betTagUserList, List<UserProfileTagsExtVo> tagList, Long time) {
        for (int i = 0; i < betTagUserList.size(); i++) {
            int n = i;
            Long userId = betTagUserList.get(i);
            Long lastTime = staticsItemExtMapper.getUserCreateTime(userId);
            if ((time - lastTime) / 86400000 % 28 == 0) {
                log.info("篮球赌客转ufo-$或好脚用户用户:{}时间通过", userId, LocalDateTimeUtil.getDayStartTime(lastTime));
            } else {
                continue;
            }
            for (UserProfileTagsExtVo tag : tagList) {
                ThreadUtil.submit(() -> {
                    try {
                        tagService.checkUserTag(tag, userId, time, null);
                    } catch (SysException e) {
                        log.error("篮球赌客转ufo-$或好脚用户特殊处理:{}", e.getMessage());
                    } catch (Exception e) {
                        log.error("篮球赌客转ufo-$或好脚用户第{}个用户{}投注特征标签扫描异常:{}", n, userId, e);
                    }
                    log.info("篮球赌客转ufo-$或好脚用户第{}个用户{}投注特征标签扫描完成", n, userId);
                });
            }
        }
    }

}
