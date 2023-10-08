package com.panda.sport.sdk.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.sdk.annotation.AutoInitMethod;
import com.panda.sport.sdk.constant.RedisKeys;
import com.panda.sport.sdk.core.JedisClusterServer;
import com.panda.sport.rcs.pojo.RcsBusinessUserPaidConfig;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.service.AmountValidateAdapter;
import com.panda.sport.sdk.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/*
 * 用户单场最高赔付
 */
@AutoInitMethod(init = "init")
public class UserMatchMaxPaid extends AmountValidateAdapter {
	private static final Logger log = LoggerFactory.getLogger(UserMatchMaxPaid.class);

	@Inject
	JedisClusterServer jedisClusterServer;

	private String shakey;


	public void init() {
		String text =  new FileUtil().getFileTxt("/lua/orderSave.lua");
		log.info("lua 脚本内容text:{}", text);
		shakey = jedisClusterServer.scriptLoad(text);
		if (shakey == null){
			throw new RcsServiceException("lua脚本加载失败");
		}
	}

	@Override
	public Long getSurplusAmount(ExtendBean order, Long[][] rec) {
		return 1L;
	}

	public RcsBusinessUserPaidConfig getUserConfig(ExtendBean order) {
		RcsBusinessUserPaidConfig userConfig = order.getUserConfig();
		if(userConfig == null ) {
			userConfig = configService.getUserPaidConfig(order.getBusId(), order.getUserId());
			if(userConfig == null ){
				return null;
			}
			order.setUserConfig(userConfig);
		}
		return userConfig;
	}

	/**
	 * 获取矩阵和当前订单矩阵相加的最大赔付值
	 */
	private Long getRecMaxPaid(ExtendBean order, Long[][] rec) {
		Long maxPaidScore = Long.MAX_VALUE;
		if( order.getRecType() == null|| rec == null || order.getRecType() == 1) {
			//不可用比分推算的
			Map<String, Long> infoMap = jedisClusterServer.hGetAll(String.format(RedisKeys.PAID_DATE_USER_REDIS_CACHE ,
					order.getDateExpect(),order.getUserId(),order.getMatchId()),Long.class);
			if(infoMap == null || infoMap.size() <= 0 ){
				return 0L;
			}


			maxPaidScore = Collections.min(infoMap.values());

			log.info("requestId:{},UserMatchMaxPaid, 不可用比分推算的:{},maxPaidScore:{}",infoMap,maxPaidScore);
		}else {//可用比分推算的需要做矩阵累加
			Map<String, Long> infoMap = jedisClusterServer.hGetAll(String.format(RedisKeys.PAID_DATE_USER_REDIS_CACHE ,
					order.getDateExpect(),order.getUserId(),order.getMatchId()),Long.class);
			for(int i = 0 ; i < rec.length ; i ++) {
				for(int j = 0 ; j < rec[i].length ; j ++) {
					String key = String.format("%03d", i) + String.format("%03d", j);
					Long oldAmount = infoMap.get(key) == null ? 0L : infoMap.get(key);
					maxPaidScore = Math.min(maxPaidScore, oldAmount + rec[i][j]);
				}
			}
			log.info("requestId:{},UserMatchMaxPaid, 可用比分推算的:{},maxPaidScore:{}", infoMap,maxPaidScore);
		}

		return maxPaidScore * -1;
	}

	/**
	 * 获取矩阵和当前订单矩阵相加的最大赔付值,做矩阵累加
	 */
	private Long getRecMaxPaidBySave(ExtendBean order, Long[][] rec, Map<String, Object> data) {
		Long maxPaidScore = Long.MAX_VALUE;
		//不可用比分推算的
		if(order.getRecType() == null|| rec == null || order.getRecType() == 1) {
			Map<String, Long> infoMap = jedisClusterServer.hGetAll(String.format(RedisKeys.PAID_DATE_USER_REDIS_CACHE ,
					order.getDateExpect(),order.getUserId(),order.getMatchId()),Long.class);
			if(infoMap == null || infoMap.size() <= 0 ){
				return 0L;
			}

			maxPaidScore = Collections.min(infoMap.values());
		}else {//可用比分推算的需要做矩阵累加
			Map<String, Long> updateMap = new HashMap<String, Long>();
			data.put("UserMatchMaxPaid-updateMap", updateMap);
			for(int i = 0 ; i < rec.length ; i ++) {
				for(int j = 0 ; j < rec[i].length ; j ++) {
					String field = String.format("%03d", i) + String.format("%03d", j);
					long result = jedisClusterServer.hincrBy(String.format(RedisKeys.PAID_DATE_USER_REDIS_CACHE ,
							order.getDateExpect(),order.getUserId(),order.getMatchId()), field, rec[i][j]);
					updateMap.put(field, rec[i][j]);

					if(maxPaidScore > result){
						maxPaidScore = result;
					}
				}
			}
		}
		data.put("addVal", maxPaidScore);
		data.put("type", "用户单场最高赔付");
		return maxPaidScore * -1;
	}

	public static void main(String[] args) {
		System.out.println(String.format("%03d", 11));
	}

	private Long executeUserMatchLua(String key ,Long[][] rec) {
		//要获取的key值
		List<String> keys = new ArrayList<>();
		keys.add(key);
		//传入的参数
		List<String> args = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		for(int i = 0 ; i < rec.length ; i ++) {
			for(int j = 0 ; j < rec[i].length ; j ++) {
				sb.append(",").append("[\"" + String.format("%03d", i) + String.format("%03d", j) + "\"]").append("=").append(rec[i][j]);
			}
		}
		args.add("{" + sb.toString().substring(1) + "}");
		args.add(rec.length + "");
		Object ret = jedisClusterServer.evalsha(shakey, keys, args);
		ret = ret == null ? Arrays.asList(0) : ret;
		return JSONObject.parseArray(JSONObject.toJSONString(ret)).getLongValue(0) * -1;
	}

	@Override
	public Boolean saveOrder(ExtendBean order, Long[][] rec, Map<String, Object> data) {
		return false;
	}

	@Override
	public void rollBack(ExtendBean order, Long[][] rec, Map<String, Object> data) {
		if(!data.containsKey("UserMatchMaxPaid-updateMap")) {
			if(order.getRecType() == 1 || rec == null){
				return;
			}

			Map<String, Long> dataMap = new HashMap<String, Long>();
			for(int i = 0 ; i < rec.length ; i ++) {
				for(int j = 0 ; j < rec[i].length ; j ++) {
					String field = String.format("%03d", i) + String.format("%03d", j);
					dataMap.put(field, rec[i][j]);
				}
			}
			data.put("UserMatchMaxPaid-updateMap", dataMap);
		}

		Map<String, Long> updateMap = (Map<String, Long>) data.get("UserMatchMaxPaid-updateMap");
		for(String key : updateMap.keySet()) {
			jedisClusterServer.hincrBy(String.format(RedisKeys.PAID_DATE_USER_REDIS_CACHE ,
					order.getDateExpect(),order.getUserId(),order.getMatchId()), key, -1 * updateMap.get(key));
		}
	}

	@Override
	public void prizeHandle(ExtendBean orderItem) {
		//Long prizeAmount = orderItem.getOrderMoney() - orderItem.getSettleAmount().longValue() ;
		if(orderItem.getRecType() == 0) {
			JSONArray arrays = JSONArray.parseArray(orderItem.getRecVal());
			for(int i = 0 ; i < arrays.size() ; i ++) {
				JSONArray keys = arrays.getJSONArray(i);
				for(int j = 0 ; j < keys.size() ; j ++) {
					String key = String.format("%03d", i) + String.format("%03d", j);
					//矩阵每个数据都加上当前订单的实际赔付
					jedisClusterServer.hincrBy(String.format(RedisKeys.PAID_DATE_USER_REDIS_CACHE ,
							orderItem.getDateExpect(),orderItem.getUserId(),orderItem.getMatchId()), key, -1 * keys.getLongValue(j) - orderItem.getProfit());
				}
			}
		}
	}

}
