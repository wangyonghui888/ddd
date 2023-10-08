package com.panda.sport.sdk.service.impl;

import com.google.inject.Inject;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.sdk.constant.RedisKeys;
import com.panda.sport.sdk.core.JedisClusterServer;
import com.panda.sport.rcs.pojo.RcsBusinessMatchPaidConfig;
import com.panda.sport.sdk.service.AmountValidateAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.rpc.paid.intef.impl
 * @Description :  单场赛事最大赔付
 * @Date: 2019-10-04 11:05
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

public class SingleMacthMaxPaid extends AmountValidateAdapter {
    private static final Logger log = LoggerFactory.getLogger(SingleMacthMaxPaid.class);

    @Inject
    JedisClusterServer jedisClusterServer;
    /**
     * 获取缓存键值
     * @param order
     * @return
     */
    private String getCacheKey(ExtendBean order){
        String cacheKey = String.format(RedisKeys.PAID_DATE_MATCH_REDIS_CACHE,
                order.getDateExpect(),order.getTournamentLevel(),order.getMatchId());   //日期期数
        return cacheKey;
    }

    /**
     * 获取所有矩阵和当前订单矩阵相加的最大赔付值
     */
    private Long getRecMaxPaid(ExtendBean order, Long[][] rec) {
        return 0L ;
    }


    /**
     * 获取赛事选项在当前维度最大下注金额
     * @param order
     * @param rec
     * @return
     */
    @Override
    public Long getSurplusAmount(ExtendBean order, Long[][] rec) {
        return 0L;
    }

    public RcsBusinessMatchPaidConfig getMatchPaidConfig(ExtendBean order) {
        //获取配置数据
        RcsBusinessMatchPaidConfig config = configService.getMatchPaidConfig(order.getBusId(),
                order.getSportId(),
                order.getTournamentLevel().toString());
        //数据库没有配置数据，返回0
        if (config == null ) {
            log.warn(String.format("没有找到商户id:%s;sportId:%s;联赛级别:%s的赛事的配置项",
                    order.getBusId(),
                    order.getSportId(),
                    order.getTournamentLevel().toString()));
            return null;
        }
        return  config;
    }

    /**
     * 订单是否可以通过
     * 获取用户订单在当前金额下是否超出最大赔付
     * 用redis原子相加
     * @param order
     * @param rec
     * @param data
     * @return
     */
    @Override
    public Boolean saveOrder(ExtendBean order, Long[][] rec, Map<String, Object> data) {
        RcsBusinessMatchPaidConfig config = getMatchPaidConfig(order);

        if(config == null)  {
            return false;
        }

        String key = getCacheKey(order);

        Long result = jedisClusterServer.incrBy(key, order.getCurrentMaxPaid());
        data.put("SingleMacthMaxPaid-addVal", order.getCurrentMaxPaid());

        data.put("addVal", result);
        data.put("type", "单场赛事最大赔付");

        if(result > config.getMatchMaxPayVal().longValue()) {
            rollBack(order, rec, data);
            return false;
        }
        return true;
    }

    /**
     * 订单做入库计算
     * 失败 缓存操作需要回滚之前的操作，数据库操作不用处理
     * 把之前加的缓存进行减少
     * @param order
     * @param rec
     * @param data
     */
    @Override
    public void rollBack(ExtendBean order, Long[][] rec, Map<String, Object> data) {
        if(!data.containsKey("SingleMacthMaxPaid-addVal")) {
            data.put("SingleMacthMaxPaid-addVal", order.getCurrentMaxPaid());
        }
        Long val = Long.parseLong(String.valueOf(data.get("SingleMacthMaxPaid-addVal")));
        jedisClusterServer.incrBy(getCacheKey(order), -val);
    }
}
