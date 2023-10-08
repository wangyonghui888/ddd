package com.panda.sport.rcs.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.sport.rcs.common.bean.RuleResult;
import com.panda.sport.rcs.common.constants.RedisConstants;
import com.panda.sport.rcs.common.utils.CopyUtils;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.common.utils.ThreadUtil;
import com.panda.sport.rcs.common.vo.UserProfileTagsExtVo;
import com.panda.sport.rcs.common.vo.api.response.UserProfileTagsRuleRelationResVo;
import com.panda.sport.rcs.common.vo.rule.DangerousRuleParameterVo;
import com.panda.sport.rcs.common.vo.rule.OrderDetailVo;
import com.panda.sport.rcs.common.vo.rule.RuleParameterVo;
import com.panda.sport.rcs.customdb.entity.MarketOptionEntity;
import com.panda.sport.rcs.customdb.mapper.DangerousRuleExtMapper;
import com.panda.sport.rcs.customdb.mapper.TagExtMapper;
import com.panda.sport.rcs.db.entity.*;
import com.panda.sport.rcs.db.service.*;
import com.panda.sport.rcs.redis.service.RedisService;
import com.panda.sport.rcs.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 危险注单扫描 Service
 *
 * @author :  lithan
 * @date: 2020-07-11 11:46:10
 */
@Service
public class DangerousOrderImpl implements IDangerousOrderService {

    Logger log = LoggerFactory.getLogger(DangerousOrderImpl.class);

    /**
     * 每次扫描的时间范围
     */
    private static final Long TIME_SCOPE = 10 * 60 * 1000L;

    @Autowired
    DangerousRuleExtMapper dangerousRuleExtMapper;

    @Autowired
    IDangerousService dangerousService;

    @Autowired
    IUserProfileDangerousBetRuleService userProfileDangerousBetRuleService;

    @Autowired
    RedisService redisService;

    /**
     * 危险投注(非蛇单)扫描
     *
     * @param time 当前时间 毫秒
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(Long time) {
        //定时扫描 10分钟范围内的新订单
        String key = String.format("%s.%s", RedisConstants.PREFIX, "dangerous.order.lasttime");
        Long beginTime = 0L;
        if (ObjectUtils.isEmpty(redisService.get(key))) {
            beginTime = time;
        } else {
            beginTime = (Long) redisService.get(key);
        }
        //beginTime = 1598949490970L;
        //结束时间
        Long endTime = beginTime + TIME_SCOPE;
        log.info("危险注单扫描开始时间:{}结束时间:{}", LocalDateTimeUtil.milliToLocalDateTime(beginTime), LocalDateTimeUtil.milliToLocalDateTime(endTime));
        if ((System.currentTimeMillis() - endTime) < 1 * 60 * 1000L) {
            log.info("危险注单扫描 非1分中之前:{}", LocalDateTimeUtil.milliToLocalDateTime(endTime));
            return;
        }

        //需要处理的注单
        List<OrderDetailVo> list = dangerousRuleExtMapper.getOrderByBetTime(beginTime, endTime);
        log.info("危险注单扫描 注单:数量{}", list.size());
        //所有危险规则(不包含蛇单)
        LambdaQueryWrapper<UserProfileDangerousBetRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(UserProfileDangerousBetRule::getDangerousCode, "d1");
        wrapper.ne(UserProfileDangerousBetRule::getDangerousCode, "d2");
        List<UserProfileDangerousBetRule> dangerousBetRuleList = userProfileDangerousBetRuleService.list(wrapper);
        //处理所有
        checkAll(list, dangerousBetRuleList);
        redisService.set(key, endTime);
        log.info("危险注单扫描完成--开始时间:{}结束时间:{}", LocalDateTimeUtil.milliToLocalDateTime(beginTime), LocalDateTimeUtil.milliToLocalDateTime(endTime));
    }

    public void checkAll(List<OrderDetailVo> orderDetailVoList, List<UserProfileDangerousBetRule> dangerousBetRuleList){
        //处理每个注单  的危险判断
        for (OrderDetailVo orderDetailVo : orderDetailVoList) {
            ThreadUtil.submit(()->{
                log.info("危险注单扫描 注单:{}开始", orderDetailVo.getOrderNo());
                for (UserProfileDangerousBetRule userProfileDangerousBetRule : dangerousBetRuleList) {
                    if (userProfileDangerousBetRule.getEnable() == 1) {
                        checkOne(orderDetailVo, userProfileDangerousBetRule);
                    }
                }
                log.info("危险注单扫描 注单:{}完成", orderDetailVo.getOrderNo());
            });
        }
    }


    private void checkOne(OrderDetailVo orderDetailVo, UserProfileDangerousBetRule dangerousBetRule) {
        try {
            log.info("危险注单扫描 注单:{},Id:{},Code:{},开始", orderDetailVo.getBetNo(), dangerousBetRule.getId(), dangerousBetRule.getDangerousCode());
            DangerousRuleParameterVo vo = new DangerousRuleParameterVo();
            vo.setParameter1(dangerousBetRule.getParameter1());
            vo.setParameter2(dangerousBetRule.getParameter2());
            vo.setParameter3(dangerousBetRule.getParameter3());
            vo.setParameter4(dangerousBetRule.getParameter4());
            vo.setUserId(orderDetailVo.getUid());
            vo.setOrderDetailVo(orderDetailVo);
            Method method = dangerousService.getClass().getMethod(dangerousBetRule.getDangerousCode().toLowerCase(), DangerousRuleParameterVo.class);
            method.invoke(dangerousService, vo);
            log.info("危险注单扫描 注单:{},Id:{},Code:{},结束", orderDetailVo.getBetNo(), dangerousBetRule.getId(), dangerousBetRule.getDangerousCode());
        } catch (Exception e) {
            log.info("危险注单扫描 注单:{},Id:{},Code:{},异常{}", orderDetailVo.getBetNo(), dangerousBetRule.getId(), dangerousBetRule.getDangerousCode(),e);
        }
    }

    /**
     * 危险投注(蛇单)扫描
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeSnake() {
        //缓存
        String key = String.format("%s.%s", RedisConstants.PREFIX, "dangerous.snake.rule");
        Object cache = redisService.get(key);
        List<UserProfileDangerousBetRule> dangerousBetRuleList = new ArrayList<>();
        if(cache==null){
            //只查看蛇单规则
            LambdaQueryWrapper<UserProfileDangerousBetRule> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(UserProfileDangerousBetRule::getDangerousCode, new Object[]{"d1", "d2"});
            dangerousBetRuleList = userProfileDangerousBetRuleService.list(wrapper);
            redisService.set(key, dangerousBetRuleList, 2 * 60);
        }else {
            dangerousBetRuleList = (List<UserProfileDangerousBetRule>) redisService.get(key);
        }

        Map<String, UserProfileDangerousBetRule> map = dangerousBetRuleList.stream().collect(Collectors.toMap(UserProfileDangerousBetRule::getDangerousCode, Function.identity()));

        DangerousRuleParameterVo vo = new DangerousRuleParameterVo();

        UserProfileDangerousBetRule d1Rule = map.get("d1");
        if(d1Rule.getEnable()==1){
//            BeanUtils.copyProperties(d1Rule, vo);
            vo.setParameter1(d1Rule.getParameter1());
            vo.setParameter2(d1Rule.getParameter2());
            vo.setParameter3(d1Rule.getParameter3());
            vo.setParameter4(d1Rule.getParameter4());
            dangerousService.d1(vo);
        }

        UserProfileDangerousBetRule d2Rule = map.get("d2");
        if(d2Rule.getEnable()==1){
            vo = new DangerousRuleParameterVo();
//            BeanUtils.copyProperties(d2Rule, vo);
            vo.setParameter1(d1Rule.getParameter1());
            vo.setParameter2(d1Rule.getParameter2());
            vo.setParameter3(d1Rule.getParameter3());
            vo.setParameter4(d1Rule.getParameter4());
            dangerousService.d2(vo);
        }
    }
}
