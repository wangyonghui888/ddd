package com.panda.sport.rcs.mgr.service.limit;

import com.alibaba.fastjson.JSON;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mgr.enums.RedisCmdEnum;
import com.panda.sport.rcs.mgr.utils.StringUtil;
import com.panda.sport.rcs.pojo.limit.RedisUpdateVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 限额回滚服务
 * @Author : Paca
 * @Date : 2021-11-26 10:54
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Service
public class LimitCallbackService {

    @Autowired
    private RedisClient redisClient;

    /**
     * 串关额度回滚
     *
     * @param orderNo
     */
    public void seriesLimitCallback(String orderNo) {
        String key = "rcs:limit:redisUpdateRecord:series:" + orderNo;
        redisCallback(key);
    }

    /**
     * 信用模式额度回滚
     *
     * @param orderNo
     */
    public void creditLimitCallback(String orderNo) {
        String key = "rcs:limit:redisUpdateRecord:credit:" + orderNo;
        redisCallback(key);
    }

    /**
     * 冠军玩法额度回滚
     *
     * @param orderNo
     */
    public void championLimitCallback(String orderNo) {
        String key = "rcs:limit:redisUpdateRecord:champion:" + orderNo;
        redisCallback(key);
    }

    private void redisCallback(String key) {
        String value = redisClient.get(key);
        log.info("额度回滚：key={},value={}", key, value);
        if (StringUtils.isNotBlank(value)) {
            List<RedisUpdateVo> redisUpdateList = JSON.parseArray(value, RedisUpdateVo.class);
            if (CollectionUtils.isNotEmpty(redisUpdateList)) {
                redisUpdateList.forEach(vo -> {
                    if (RedisCmdEnum.isIncrBy(vo.getCmd())) {
                        redisClient.incrBy(vo.getKey(), -1 * StringUtil.toLong(vo.getValue(), 0L));
                    } else if (RedisCmdEnum.isIncrByFloat(vo.getCmd())) {
                        redisClient.incrByFloat(vo.getKey(), -1 * StringUtil.toDouble(vo.getValue(), 0.0D));
                    } else if (RedisCmdEnum.isHincrBy(vo.getCmd())) {
                        redisClient.hincrBy(vo.getKey(), vo.getField(), -1 * StringUtil.toLong(vo.getValue(), 0L));
                    } else if (RedisCmdEnum.isHincrByFloat(vo.getCmd())) {
                        redisClient.hincrByFloat(vo.getKey(), vo.getField(), -1 * StringUtil.toDouble(vo.getValue(), 0.0D));
                    }
                });
            }
            // 回滚后删除并备份
            redisClient.delete(key);
            String bakKey = key + ":bak";
            redisClient.set(bakKey, value);
            redisClient.expireKey(bakKey, Long.valueOf(TimeUnit.DAYS.toSeconds(1L)).intValue());
        }
    }
}
