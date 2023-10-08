package com.panda.sport.sdk.service.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.sdk.constant.LimitRedisKeys;
import com.panda.sport.sdk.core.JedisClusterServer;
import com.panda.sport.sdk.util.RcsLocalCacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 公共服务
 * @Author : Paca
 * @Date : 2022-04-02 11:23
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Singleton
public class CommonService {

    private static final Logger log = LoggerFactory.getLogger(CommonService.class);

    @Inject
    private JedisClusterServer jedisClusterServer;

    /**
     * 获取用户特殊限额类型，0-无，1-标签限额（已作废），2-特殊百分比限额，3-特殊单注单场限额，4-特殊VIP限额
     *
     * @param userId
     * @return
     */
    public String getUserSpecialLimitType(String userId) {
        String key = LimitRedisKeys.getUserSpecialLimitKey(userId);
        String hashValue = RcsLocalCacheUtils.getValue(key,LimitRedisKeys.USER_SPECIAL_LIMIT_TYPE_FIELD,jedisClusterServer::hget);
        log.info("额度查询-用户特殊限额类型：key={},hashKey={},hashValue={}", key, LimitRedisKeys.USER_SPECIAL_LIMIT_TYPE_FIELD, hashValue);
        return hashValue;
    }

    /**
     * 是否特殊VIP限额
     *
     * @param userId
     * @return
     */
    public boolean isSpecialVipLimit(String userId) {
        return "4".equals(getUserSpecialLimitType(userId));
    }

    /**
     * 是否新商户限额模式
     *
     * @param merchantId
     * @return
     */
    public boolean isNewMerchantLimitMode(String merchantId) {
        String key = LimitRedisKeys.getBussinessSwitchKey();
        String hashValue = jedisClusterServer.hget(key, merchantId);
        log.info("额度查询-商户限额模式：key={},hashKey={},hashValue={}", key, merchantId, hashValue);
        return "1".equals(hashValue);
    }
}
