package com.panda.sport.rcs.predict.common;

import com.panda.sport.rcs.core.cache.client.RedisClient;

/**
 * 过期时间
 */
public class Expiry {
    //早盘赛事key 默认过期时间 3个月
    public static final Integer MATCH_EXPIRY = 7 * 24 * 60 * 60;

    //早盘赛事key 货量总表单独设置 过期时间 30天
    public static final Integer MATCH_EXPIRY_STATIS = 30 * 24 * 60 * 60;

    //滚球 赛事key 默认过期时间 1天
    public static final Integer MATCH_ONE_DAY_EXPIRY = 24 * 60 * 60;


    public static void redisKeyExpiry(RedisClient redisClient, Integer matchType, String redisKey) {
        if (matchType == 1) {
            redisClient.expireKey(redisKey, MATCH_EXPIRY);
        } else if (matchType == 3) {
            redisClient.expireKey(redisKey, MATCH_EXPIRY_STATIS);
        } else {
            redisClient.expireKey(redisKey, MATCH_ONE_DAY_EXPIRY);
        }
    }

    public static void redisKeyExpiryStatis(RedisClient redisClient, Integer matchType, String redisKey) {
        if (matchType == 1) {
            redisClient.expireKey(redisKey, MATCH_EXPIRY_STATIS);
        } else {
            redisClient.expireKey(redisKey, MATCH_ONE_DAY_EXPIRY);
        }
    }
}
