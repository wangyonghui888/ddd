package com.panda.rcs.pending.order.utils;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.panda.rcs.pending.order.constants.NumberConstant;
import com.panda.rcs.pending.order.constants.RedisKey;
import com.panda.sport.data.rcs.dto.limit.RedisUpdateVo;
import com.panda.sport.rcs.enums.RedisCmdEnum;
import com.panda.sport.rcs.redis.utils.RedisUtils;
import com.panda.sport.rcs.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author :  myname
 * @Project Name :  panda-rcs-order-group
 * @Package Name :  com.panda.rcs.pending.order.utils
 * @Description :  TODO
 * @Date: 2022-05-22 10:57
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
public class CommonServer {

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 订单取消，扣除的限额需要加回
     *
     * @param orderNo 订单号
     */
    public void revertLimitBetAccount(String orderNo) {
        List<RedisUpdateVo> list = this.getUpdateList(orderNo);
        log.info("预约投注取消获取缓存值:{}", list);
        this.redisCallback(list);
    }

    public List<RedisUpdateVo> getUpdateList(String orderNo) {
        String key = String.format(RedisKey.SERIES_REDIS_UPDATE_RECORD_KEY, orderNo);
        String redisVal = redisUtils.get(key);
        log.info("查询累计限额获取缓存值:{}", redisVal);
        List<RedisUpdateVo> list = JSON.parseArray(redisVal, RedisUpdateVo.class);
        return list;
    }


    public void redisCallback(List<RedisUpdateVo> redisUpdateList) {
        if (org.apache.dubbo.common.utils.CollectionUtils.isEmpty(redisUpdateList)) {
            return;
        }
        redisUpdateList.forEach(vo -> {
            BigDecimal value = CommonUtils.toBigDecimal(vo.getValue(), BigDecimal.ZERO).negate();
            exeIncrByCmd(vo.getCmd(), vo.getKey(), vo.getField(), value);
        });
    }

    private void exeIncrByCmd(String cmd, String key, String field, BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) == NumberConstant.NUM_ZERO) {
            return;
        }
        if (RedisCmdEnum.isIncrBy(cmd)) {
            redisUtils.incrBy(key, value.longValue());
        } else if (RedisCmdEnum.isIncrByFloat(cmd)) {
            redisUtils.incrByFloat(key, value.doubleValue());
        } else if (RedisCmdEnum.isHincrBy(cmd)) {
            redisUtils.hincrBy(key, field, value.longValue());
        } else if (RedisCmdEnum.isHincrByFloat(cmd)) {
            redisUtils.hincrByFloat(key, field, value.doubleValue());
        } else {
            return;
        }
        redisUtils.expire(key, NumberConstant.LONG_NINETY, TimeUnit.DAYS);
    }


    public void saveRedisUpdateRecord(String orderNo, List<RedisUpdateVo> redisUpdateList) {
        if (CollectionUtils.isEmpty(redisUpdateList)) {
            return;
        }
        String key = String.format(RedisKey.SERIES_REDIS_UPDATE_RECORD_KEY, orderNo);
        redisUtils.set(key, JSON.toJSONString(redisUpdateList));
        log.info("预约投注,订单入库校验-缓存Redis更新记录：key:{},value:{}", key, redisUpdateList);
        redisUtils.expire(key, NumberConstant.LONG_NINETY, TimeUnit.DAYS);
    }

}
