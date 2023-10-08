package com.panda.sport.rcs.mgr.paid.intef.impl;

import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.log.LogContext;
import com.panda.sport.rcs.mgr.paid.annotion.Order;
import com.panda.sport.rcs.mgr.paid.intef.AmountValidateAdapter;
import com.panda.sport.rcs.pojo.RcsBusinessMatchPaidConfig;
import com.panda.sport.rcs.mgr.wrapper.impl.RcsRectanglePlayServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
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
@Order(value = 5)
@Slf4j
public class SingleMacthMaxPaid extends AmountValidateAdapter {
    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RcsRectanglePlayServiceImpl playService;

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
        return 0L;
    }


    /**
     * 获取赛事选项在当前维度最大下注金额
     * @param order
     * @param rec
     * @return
     */
    @Override
    public Long getSurplusAmount(ExtendBean order, Long[][] rec) {
        //1、取缓存数据，判断是否存在数据
        //2、如果不存在从数据库取出默认值
        //3、用现在累加值和刚取出的值做相减
        //4、返回结果值

        RcsBusinessMatchPaidConfig config = getMatchPaidConfig(order);
        if(config == null ) {
            log.warn("::{}:: requestId:{},单日赛事维度配置获取失败，ExtendBean:{}",order.getOrderId(),LogContext.getContext().getRequestId(),order);
            return -1L;
        }

        //计算配置中最大数据
        BigDecimal maxMoneySet = BigDecimal.ZERO;

        //获取用户维度赛事不能用比分推算的最大赔付
        Long noScorePlayAmount = playService.queryAllUserMatchNoScorePlay(order);

        //获取矩阵最大和缓存累加的最大赔付
        Long maxPaidScore = getRecMaxPaid(order, rec);

        maxMoneySet = config.getMatchMaxPayVal().subtract(new BigDecimal(maxPaidScore)).subtract(new BigDecimal(noScorePlayAmount));
        if(order.getCurrentMaxPaid() > 0) {
            maxMoneySet = maxMoneySet.subtract(new BigDecimal(order.getCurrentMaxPaid()));
        }

        if(maxMoneySet.longValue() < 0){
            log.warn("::{}:: requestId:{},单日赛事维度配置小于0，ExtendBean:{},maxMoneySet:{}",order.getOrderId(),LogContext.getContext().getRequestId(),order,maxMoneySet.longValue());
        }

        log.info("::{}::requestId:{},SingleMatchMaxPaid, getMatchMaxPayVal:{},noScorePlayAmount:{},maxPaidScore:{}",order.getOrderId(),LogContext.getContext().getRequestId(),config.getMatchMaxPayVal(),noScorePlayAmount,maxPaidScore);

        return maxMoneySet.longValue();
    }

    public RcsBusinessMatchPaidConfig getMatchPaidConfig(ExtendBean order) {
        //获取配置数据
        RcsBusinessMatchPaidConfig config = configService.getMatchPaidConfig(order.getBusId(),
                order.getSportId(),
                order.getTournamentLevel().toString());
        //数据库没有配置数据，返回0
        if (config == null ) {
            log.warn("::{}:: 没有找到商户id:{};sportId:{};联赛级别:{}的赛事的配置项",
                    order.getOrderId(),
                    order.getBusId(),
                    order.getSportId(),
                    order.getTournamentLevel().toString());
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

        Long result = redisClient.incrBy(key, order.getCurrentMaxPaid());
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
        redisClient.incrBy(getCacheKey(order), -val);
    }
}
