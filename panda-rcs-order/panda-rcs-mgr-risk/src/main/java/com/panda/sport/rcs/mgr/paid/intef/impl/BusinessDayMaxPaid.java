package com.panda.sport.rcs.mgr.paid.intef.impl;

import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.log.LogContext;
import com.panda.sport.rcs.pojo.RcsBusinessDayPaidConfig;
import com.panda.sport.rcs.mgr.paid.annotion.Order;
import com.panda.sport.rcs.mgr.paid.intef.AmountValidateAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.rpc.paid.intef.impl
 * @Description :  商户每日维度规则计算与结果
 * @Date: 2019-10-04 11:15
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Order(value = 1)
@Slf4j
public class BusinessDayMaxPaid extends AmountValidateAdapter {

    @Autowired
    private RedisClient redisClient;

    /**
     * 获取缓存键值
     * @param order
     * @return
     */
    private String getCacheKey(ExtendBean order){
        return String.format(RedisKeys.PAID_DATE_BUS_REDIS_CACHE,order.getDateExpect(), order.getBusId());
    }
    /**
     * 获取用户选项在当前维度最大下注金额
     * 1、取缓存数据，判断是否存在数据
     * 2、如果不存在从数据库取出默认值
     * 3、用现在累加值和刚取出的值做相减
     * 4、返回结果值
     * @param order
     * @param rec
     * @return
     */
    @Override
    public Long getSurplusAmount(ExtendBean order, Long[][] rec) {
    	String stopKey = String.format(RedisKeys.PAID_DATE_BUS_STOP_REDIS_CACHE, order.getDateExpect(), order.getBusId());
            if("1".equals(String.valueOf(redisClient.get(stopKey)))) {
                //log.warn(String.format("requestId:%s,当前商户已经维护，不在收单:%s",LogContext.getContext().getRequestId(),stopKey));
                log.warn("::{}::当前商户已经维护，不在收单{}",order.getOrderId(),stopKey);
                return -1L;
            }
            //获取配置数据
            RcsBusinessDayPaidConfig config = configService.getDayPaidConfig(order.getBusId());
            //数据库没有配置数据，返回0
            if (config == null ) {
                //log.warn(String.format("requestId:%s,没有找到商户id:%s单日最大赔付的配置项",LogContext.getContext().getRequestId(),order.getBusId()));
                log.warn("::{}::没有找到商户id:{}单日最大赔付的配置项",order.getOrderId(),order.getBusId());
                return -1L;
            }
            //状态（1-高危 2-危险 3-正常 4-健康 5-满负荷制动 6-手动制动 7-已停用）
            if(config.getStatus() == 7) {
                //log.warn(String.format("requestId:%s,商户id:%s已经停止接单",LogContext.getContext().getRequestId(),order.getBusId()));
                log.warn("::{}::,商户id:{}已经停止接单",order.getOrderId(),order.getBusId());
                return -1L;
            }else if(config.getStatus() == 6 && config.getExpireTime() > System.currentTimeMillis() ) {
                //手动制动需要判断过期时间
                //log.warn(String.format("requestId:%s,商户id:%s当前是手动制动状态，已经停止接单",LogContext.getContext().getRequestId(),order.getBusId()));
                log.warn("::{}::,商户id:{}当前是手动制动状态，已经停止接单",order.getOrderId(),order.getBusId());
                return -1L;
        }
        	
        //计算配置中最大数据
        BigDecimal maxMoneySet = BigDecimal.ZERO;
        //获取缓存值
        String cacheValue = redisClient.get(getCacheKey(order));
        if(StringUtils.isBlank(cacheValue)){
            cacheValue = "0";
        }

        //商户维度不计算预期赔付值
        maxMoneySet = config.getStopVal().subtract(new BigDecimal(cacheValue));
        if(maxMoneySet.longValue() < 0 ) {
        	log.warn("::{}::requestId:{},商户维度配置小于0，ExtendBean:{},maxMoneySet:{}",order.getOrderId(),LogContext.getContext().getRequestId(),order,maxMoneySet.longValue());
        }

        log.info("::{}::requestId:{},BusinessDayMaxPaid, getStopVal:{},cacheValue:{},getCacheKey:{}",order.getOrderId(),LogContext.getContext().getRequestId(),config.getStopVal(),getCacheKey(order));
        return maxMoneySet.longValue();
    }

    /**
     * 订单是否可以通过
     * 获取用户订单在当前金额下是否超出最大赔付
     * 原子累加
     * 需求修改 ：商户维度不计算预期赔付值
     * @param order
     * @param rec
     * @param data
     * @return
     */
    @Override
    public Boolean saveOrder(ExtendBean order, Long[][] rec, Map<String, Object> data) {
        Long amount= getSurplusAmount(order, rec);
        data.put("addVal", amount);
        data.put("type", "商户每日维度");
    	if(amount < 0 ) {
    	    return false;
        }
		return true; 
//    	RcsBusinessDayPaidConfig config = configService.getDayPaidConfig(order.getBusId());
//    	if(config == null ) {
//    	    return false;
//    	}
//    	
//    	String key = getCacheKey(order);
//        Long result = redisClient.incrBy( key ,order.getCurrentMaxPaid());
//        data.put("addVal", order.getCurrentMaxPaid());
//        
//        if(result > config.getStopVal().longValue()) {
//        	rollBack(order, rec, data);
//        	return false;
//        }
//        return true;
    }

    /**
     * 订单做入库计算
     * 失败 缓存操作需要回滚之前的操作，数据库操作不用处理,加回原来增加的值
     * 原子相减
     * 需求修改 ：商户维度不计算预期赔付值
     * @param order
     * @param rec
     * @param data
     */
    @Override
    public void rollBack(ExtendBean order, Long[][] rec, Map<String, Object> data) {
//    	if(!data.containsKey("addVal")) {
//    	    return;
//        }
//    	
//    	Long addVal = Long.valueOf(String.valueOf(data.get("addVal")));
//        redisClient.incrBy(getCacheKey(order),-addVal);
    }
    
    /**
     * 需求修改 ：商户维度只计算当前时间期号的累计派奖金额
     */
	@Override
	public void prizeHandle(ExtendBean order) {
		if(order.getProfit().longValue() <= 0 ){
		    return;
        }
		String dateExpect = DateUtils.getTimeExpect(order.getSettleTime());
		String key = String.format(RedisKeys.PAID_DATE_BUS_REDIS_CACHE,dateExpect, order.getBusId());
		//Long currentPaidAmount = redisClient.incrBy(key,order.getSettleAmount() - order.getOrderMoney());
        Long currentPaidAmount = redisClient.incrBy(key,order.getProfit());
		
		RcsBusinessDayPaidConfig config = configService.getDayPaidConfig(order.getBusId());
		
		if(currentPaidAmount >= config.getStopVal().longValue()) {
			String stopKey = String.format(RedisKeys.PAID_DATE_BUS_STOP_REDIS_CACHE, dateExpect, order.getBusId());
			redisClient.set(stopKey, 1);
		}
	}
}
