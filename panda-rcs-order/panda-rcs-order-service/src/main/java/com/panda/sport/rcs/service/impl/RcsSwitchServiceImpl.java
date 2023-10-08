package com.panda.sport.rcs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.RcsSwitchMapper;
import com.panda.sport.rcs.pojo.RcsSwitch;
import com.panda.sport.rcs.service.RcsSwitchService;
import com.panda.sport.rcs.utils.SpringContextUtils;
import com.panda.sport.rcs.service.RcsSwitchService;
import com.panda.sport.rcs.utils.RcsLocalTimedCacheUtils;
import com.panda.sport.rcs.vo.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import static com.panda.sport.rcs.constants.CommonConstants.SWITCH_CODE;

import static com.panda.sport.rcs.constants.CommonConstants.SWITCH_CODE;
import static com.panda.sport.rcs.constants.RedisKey.REDIS_DYNAMIC_MISSED_ORDER_CONFIG_KEY;
import static com.panda.sport.rcs.constants.RedisKey.REDIS_RCS_SWITCH_CONFIG_KEY;

/**
 * @author :  tim
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.impl
 * @Description :  TODO
 * @Date: 2020-09-04 15:08
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Service
public class RcsSwitchServiceImpl extends ServiceImpl<RcsSwitchMapper, RcsSwitch> implements RcsSwitchService {

    @Autowired
    private RcsSwitchMapper rcsSwitchMapper;

    @Autowired
    private RedisClient redisClient;


    @Override
    public HttpResponse<?> editSwitch(Integer status) {
        try{
            String switchCode = "LOUDAN";
            int i = rcsSwitchMapper.updateStatus(status, switchCode);
            if(i <= 0){
                return HttpResponse.failToMsg("未修改成功");
            }

            //按商户id缓存这个漏单设置
            String key = String.format(REDIS_RCS_SWITCH_CONFIG_KEY, switchCode);
            redisClient.set(key, String.valueOf(status));//TTL设置10天
            redisClient.expireKey(key, 60 * 60 * 24 * 10);//TTL设置10天

            return HttpResponse.success(true);
        }catch(Exception e){
            log.error("::RcsSwitchServiceImpl::editSwitch 发生异常 ::{}", e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }

    @Override
    public String getMissOrderSwitchStatus() {
        String discountSwitchKey = String.format(REDIS_RCS_SWITCH_CONFIG_KEY, SWITCH_CODE);
        String switchStatus = redisClient.get(discountSwitchKey);
        RcsSwitch rcsSwitch = null;
        if (StringUtils.isEmpty(switchStatus)) {
            RcsSwitchMapper rcsSwitchMapper = SpringContextUtils.getBeanByClass(RcsSwitchMapper.class);
            rcsSwitch = rcsSwitchMapper.selectOne(new LambdaQueryWrapper<RcsSwitch>()
                    .eq(RcsSwitch::getSwitchCode, SWITCH_CODE));
            if (ObjectUtils.isNotEmpty(rcsSwitch)) {
                switchStatus = String.valueOf(rcsSwitch.getSwitchStatus());
                redisClient.setNX(discountSwitchKey, switchStatus,10 * 60 * 1000);
            } else {
                //提示获取总开关数据缓存和db中都没有数据
                throw new RcsServiceException("折扣利率(动态漏单总开关)全局开关缓存和db中都没有数据");
            }
        }
        log.info("获取商户总开关的状态:{}",switchStatus);
        return switchStatus;
    }
}

