package com.panda.sport.rcs.mgr.wrapper.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.constants.RedisKey;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.RcsMerchantsHideRangeConfigMapper;
import com.panda.sport.rcs.mgr.mq.bean.RcsMerchantsHideRangeConfigDto;
import com.panda.sport.rcs.mgr.wrapper.RcsMerchantsHideRangeConfigService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsMerchantsHideRangeConfig;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * @author bobi
 * 2023/7/29
 */
@Service
@Slf4j
public class RcsMerchantsHideRangeConfigServiceImpl extends ServiceImpl<RcsMerchantsHideRangeConfigMapper, RcsMerchantsHideRangeConfig> implements RcsMerchantsHideRangeConfigService {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private ProducerSendMessageUtils sendMessage;
    /**
     * 编辑金额
     * @param rcsMerchantsHideRangeConfig 金额字段传参
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editHideMoneyList(RcsMerchantsHideRangeConfigDto rcsMerchantsHideRangeConfig) {
        this.update(Wrappers.<RcsMerchantsHideRangeConfig>lambdaUpdate().set(RcsMerchantsHideRangeConfig::getHideMoney,rcsMerchantsHideRangeConfig.getHideMoney())
                .set(RcsMerchantsHideRangeConfig::getUpdateUsername,rcsMerchantsHideRangeConfig.getUpdateUsername())
                .in(RcsMerchantsHideRangeConfig::getId,rcsMerchantsHideRangeConfig.getIds()));

        cover(rcsMerchantsHideRangeConfig);
    }

    /**
     * 更新缓存
     * @param rcsMerchantsHideRangeConfig
     */
    private void cover(RcsMerchantsHideRangeConfigDto rcsMerchantsHideRangeConfig) {
        List<RcsMerchantsHideRangeConfig> list = this.listByIds(rcsMerchantsHideRangeConfig.getIds());
        List<String> keys= new ArrayList<>(list.size());
        for (RcsMerchantsHideRangeConfig merchantsHideRangeConfig : list) {
            redisClient.hSet(RedisKey.REDIS_HIDE_RANGE_CONFIG, RedisKey.getCacheKey(merchantsHideRangeConfig.getMerchantsId()+"",merchantsHideRangeConfig.getSportId()), JSON.toJSONString(merchantsHideRangeConfig));
            keys.add(RedisKey.getCacheKey(merchantsHideRangeConfig.getMerchantsId()+"",merchantsHideRangeConfig.getSportId()));
        }
        //发送topic存入本地缓存
        sendMessage.sendMessage("RCS_HIDE_RANGE_CONFIG","",rcsMerchantsHideRangeConfig.getUpdateUsername(), JSON.toJSONString(keys));
        //存2小时
        redisClient.expireKey(RedisKey.REDIS_HIDE_RANGE_CONFIG,  15 * 24 * 60 * 60);
    }

    /**
     * 编辑开关
     * @param rcsMerchantsHideRangeConfig 开关字段传参
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editHideStatusList(RcsMerchantsHideRangeConfigDto rcsMerchantsHideRangeConfig) {
        this.update(Wrappers.<RcsMerchantsHideRangeConfig>lambdaUpdate().set(RcsMerchantsHideRangeConfig::getStatus,rcsMerchantsHideRangeConfig.getStatus())
                .set(RcsMerchantsHideRangeConfig::getUpdateUsername,rcsMerchantsHideRangeConfig.getUpdateUsername())
                .in(RcsMerchantsHideRangeConfig::getId,rcsMerchantsHideRangeConfig.getIds()));
        cover(rcsMerchantsHideRangeConfig);
    }
}
