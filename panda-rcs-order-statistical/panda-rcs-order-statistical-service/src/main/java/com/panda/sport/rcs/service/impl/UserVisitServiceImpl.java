package com.panda.sport.rcs.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.sport.rcs.common.constants.RedisConstants;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.customdb.mapper.RiskUserVisitIpExtMapper;
import com.panda.sport.rcs.db.entity.RiskUserVisitIp;
import com.panda.sport.rcs.db.service.IRiskUserVisitIpService;
import com.panda.sport.rcs.db.service.IRiskUserVisitIpTagService;
import com.panda.sport.rcs.redis.service.RedisService;
import com.panda.sport.rcs.service.IUserVisitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户访问 Service
 */
@Service
public class UserVisitServiceImpl implements IUserVisitService {

    Logger log = LoggerFactory.getLogger(UserVisitServiceImpl.class);
    /**
     * 一天毫秒数量
     */
    private static final Long ONE_DAY = 24 * 60 * 60 * 1000L;
    /**
     * 每次扫描的时间范围
     */
    private static final Long TIME_SCOPE = 10 * 60 * 1000L;

    @Autowired
    RiskUserVisitIpExtMapper riskUserVisitIpMapper;
    @Autowired
    IRiskUserVisitIpService riskUserVisitIpService;
    @Autowired
    IRiskUserVisitIpTagService riskUserVisitIpTagService;
    @Autowired
    RedisService redisService;

    /**
     * 定时扫描 10分钟范围内的新订单 处理用户对应的IP
     */
    @Override
    public void userIpStatics(Long time) {
        log.info("用户IP数据统计开始");
        //开始时间 缓存没有则从今天0点0分开始扫描
        String key = String.format("%s.%s", RedisConstants.PREFIX, "statics.ip.lasttime");
        Long beginTime = 0L;
        if (ObjectUtils.isEmpty(redisService.get(key))) {
            beginTime = time;
        } else {
            beginTime = (Long) redisService.get(key);
        }
        //整10分钟记录 避免跨天记录误差
        beginTime = beginTime / TIME_SCOPE * TIME_SCOPE;
        //结束时间
        Long endTime = beginTime + TIME_SCOPE;
        log.info("用户IP数据统计开始时间:{}结束时间:{}", LocalDateTimeUtil.milliToLocalDateTime(beginTime), LocalDateTimeUtil.milliToLocalDateTime(endTime));
        if ((System.currentTimeMillis() - endTime) < 1 * 60 * 1000L) {
            log.info("用户IP数据统计 非1分中之前:{}", LocalDateTimeUtil.milliToLocalDateTime(endTime));
            return;
        }
        //查出最近timeScope分钟所有的订单  用户对应的ip
        List<RiskUserVisitIp> riskUserVisitIpList = riskUserVisitIpMapper.getUserOrderIp(beginTime, endTime);
        log.info("用户IP数据统计处理数量{}", riskUserVisitIpList.size());
        //循环处理
        for (RiskUserVisitIp riskUserVisitIp : riskUserVisitIpList) {
            if (riskUserVisitIp.getIp().length() > 20) {
                log.info("ip异常跳过:" + riskUserVisitIp.getIp());
                continue;
            }
            LambdaQueryWrapper<RiskUserVisitIp> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RiskUserVisitIp::getUserId, riskUserVisitIp.getUserId());
            wrapper.eq(RiskUserVisitIp::getIp, riskUserVisitIp.getIp());
            wrapper.eq(RiskUserVisitIp::getLoginDate, LocalDateTimeUtil.getDayStartTime(beginTime));
            List<RiskUserVisitIp> resultList = riskUserVisitIpService.list(wrapper);
            //如果是新ip则保存
            if (ObjectUtils.isEmpty(resultList)) {
                saveUserVisitIp(riskUserVisitIp, LocalDateTimeUtil.getDayStartTime(beginTime));
            }
        }
        redisService.set(key, endTime);
        log.info("用户IP数据统计完成--开始时间:{}结束时间:{}", LocalDateTimeUtil.milliToLocalDateTime(beginTime), LocalDateTimeUtil.milliToLocalDateTime(endTime));
    }

    /**
     * 保存用户 ip访问数据
     *
     * @param time 当天的00:00:00
     */
    private void saveUserVisitIp(RiskUserVisitIp riskUserVisitIp, long time) {
        riskUserVisitIp.setLoginDate(time);
        //tagId设置
        LambdaQueryWrapper<RiskUserVisitIp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RiskUserVisitIp::getIp, riskUserVisitIp.getIp());
        wrapper.gt(RiskUserVisitIp::getTagId, 0);
        wrapper.last("limit 1");
        RiskUserVisitIp entity = riskUserVisitIpService.getOne(wrapper);
        if (ObjectUtils.isNotEmpty(entity)) {
            riskUserVisitIp.setTagId(entity.getTagId());
        }
        //设置国家 省 市(备用功能 不一定会用到)
        setArea(riskUserVisitIp);
        riskUserVisitIpService.save(riskUserVisitIp);
        log.info("用户访问数据新增IP:{}", JSONObject.toJSONString(riskUserVisitIp));
    }

    /**
     * 根据区域 拆分成 国家 省 市
     *
     * @param riskUserVisitIp
     */
    private void setArea(RiskUserVisitIp riskUserVisitIp) {
        if (ObjectUtils.isNotEmpty(riskUserVisitIp.getArea())) {
            String area = riskUserVisitIp.getArea();
            String[] arr = area.split(",");
            if (arr.length > 0) {
                riskUserVisitIp.setCountry(arr[0]);
            }
            if (arr.length > 1) {
                riskUserVisitIp.setProvince(arr[1]);
            }
            if (arr.length > 2) {
                riskUserVisitIp.setCity(arr[2]);
            }
        }
    }
}
