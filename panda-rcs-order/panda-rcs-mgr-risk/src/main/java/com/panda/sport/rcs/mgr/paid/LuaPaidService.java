package com.panda.sport.rcs.mgr.paid;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.panda.sport.data.rcs.dto.SettleItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.common.FileReadUtils;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mgr.paid.intef.impl.SingleMacthMaxPaid;
import com.panda.sport.rcs.mgr.paid.intef.impl.UserMatchMaxPaid;
import com.panda.sport.rcs.mgr.paid.intef.impl.UserPlayMaxPaid;
import com.panda.sport.rcs.mgr.wrapper.impl.RcsPaidConfigServiceImp;
import com.panda.sport.rcs.pojo.RcsBusinessDayPaidConfig;
import com.panda.sport.rcs.pojo.RcsBusinessMatchPaidConfig;
import com.panda.sport.rcs.pojo.RcsBusinessPlayPaidConfig;
import com.panda.sport.rcs.pojo.RcsBusinessUserPaidConfig;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LuaPaidService {
	
    private RedisClient redisClient;
	
	private String shakey;
	
	private String prizeShaKey;
	
    @Autowired
    private UserPlayMaxPaid userPlayMaxPaid;
    
    @Autowired
    private UserMatchMaxPaid userMatchMaxPaid;
    
    @Autowired
    private SingleMacthMaxPaid singleMacthMaxPaid;
    
	@Autowired
	public RcsPaidConfigServiceImp configService;

	@Autowired
	public LuaPaidService(RedisClient redisClient){
		this.redisClient = redisClient;
		String text = FileReadUtils.readFileContent("lua/orderSave_new.lua");

		log.info("lua 脚本内容text:{}",text);
		shakey = redisClient.scriptLoad(text);
		if(shakey == null) {
			throw new RcsServiceException("lua脚本加载失败");
		}
		
		text = FileReadUtils.readFileContent("lua/orderPrize_new.lua");

		log.info("lua 脚本内容text:{}",text);
		prizeShaKey = redisClient.scriptLoad(text);
		if(prizeShaKey == null) {
			throw new RcsServiceException("lua脚本加载失败");
		}
	}


	public Map<String, Object> saveOrder(ExtendBean extendBean, String rec){
		Map<String, Object> result = new HashMap<>(1);

		String busId = extendBean.getBusId();
		String matchId = extendBean.getMatchId();
		String suffix = "_{" + busId + "_" + matchId + "}";

		//传入的参数
		List<String> params = new ArrayList<>();
		params.add(extendBean.getDateExpect());
		params.add(extendBean.getBusId());
		params.add(extendBean.getSportId());
		params.add(extendBean.getUserId());
		params.add(extendBean.getMatchId());
		params.add(extendBean.getPlayId());
		params.add(extendBean.getMarketId());
		params.add(extendBean.getItemBean().getMatchType().toString());
		params.add(extendBean.getPlayType());
		params.add(extendBean.getOrderId());
		params.add(extendBean.getOrderMoney().toString());
		params.add(extendBean.getRecType().toString());
		params.add(extendBean.getSelectId());
		params.add(extendBean.getItemBean().getMaxWinAmount().toString());
		params.add(rec);
		params.add("13");

		RcsBusinessPlayPaidConfig userPlayConfig = userPlayMaxPaid.getPlayConfig(extendBean);
		if (userPlayConfig == null) {
			throw new RcsServiceException(-1, "用户玩法配置为空");
		}

		RcsBusinessUserPaidConfig userConfig = userMatchMaxPaid.getUserConfig(extendBean);
		if (userConfig == null) {
			throw new RcsServiceException(-1, "用户单场或者单日配置为空");
		}

		RcsBusinessMatchPaidConfig matchConfig = singleMacthMaxPaid.getMatchPaidConfig(extendBean);
		if (matchConfig == null) {
			throw new RcsServiceException(-1, "单场赛事配置为空");
		}

		params.add(userPlayConfig.getPlayMaxPay().toString());
		params.add(userConfig.getUserMatchPayVal().toPlainString());
		params.add(userConfig.getUserDayPayVal().toPlainString());
		params.add(matchConfig.getMatchMaxPayVal().toPlainString());


		params.add(extendBean.getItemBean().getHandleAfterOddsValue().toString());

		List<String> keys = new ArrayList<>();
		keys.add("A" + suffix);

		ArrayList<String> ret = (ArrayList<String>) redisClient.evalsha(shakey, keys, params);


		result.put("keys", keys);
		result.put("values", params);
		result.put("code", ret.get(0));
		result.put("msg", ret.get(1));
		return result;
	}


	private List<String> buildRedisValus(ExtendBean order, Long[][] rec) {
		String busId = order.getBusId();
    	String orderNo = order.getOrderId();
    	String matchId = order.getMarketId();
    	String dateExpect = order.getDateExpect();
    	String matchType = order.getIsScroll();
    	String playId = order.getPlayId();
    	String playType = order.getPlayType();
    	String sportId = order.getSportId();
    	String userId = order.getUserId();
    	String marketId = order.getMarketId();
		
    	List<String> args = new ArrayList<>();
    	args.add(orderNo);//订单号
    	args.add(dateExpect);//时间期号
    	args.add(busId);//商户
    	args.add(matchId);//赛事id
    	args.add(matchType);//赛事类型  0:赛前 1：滚球
    	args.add(playId);//玩法Id
    	args.add(playType);//赛事阶段id  1：全场  2：上半场  3：下半场 
    	args.add(String.valueOf(order.getOrderMoney()));//下注金额，乘以100  单位分
    	args.add(order.getOdds());//赔率
    	args.add(String.valueOf(order.getRecType()));//是否可用矩阵推算的  0:矩阵   1：不是矩阵
    	args.add(userId);//用户id
    	args.add(order.getSelectId());//投注项id
    	args.add(marketId);//盘口id
    	args.add(new BigDecimal(order.getOrderMoney()).multiply(new BigDecimal(order.getOdds())).toPlainString());//当前赔付金额
    	args.add(sportId);//sportId
    	
    	if(rec != null ) {
    		StringBuilder sb = new StringBuilder();
        	for(int i = 0 ; i < rec.length ; i ++) {
        		for(int j = 0 ; j < rec[i].length ; j ++) {
        			sb.append(",").append("[\"" + String.format("%03d", i) + String.format("%03d", j) + "\"]").append("=").append(rec[i][j]);
        		}
        	}
        	args.add("{" + sb.toString().substring(1) + "}");
        	args.add(rec.length + "");
    	}else {
    		args.add("{}");
        	args.add("13");
    	}

    	RcsBusinessPlayPaidConfig userPlayConfig = userPlayMaxPaid.getPlayConfig(order);
    	if (userPlayConfig == null) {
    		throw new RcsServiceException(-1, "用户玩法配置为空");
		}
    	
    	RcsBusinessUserPaidConfig userConfig = userMatchMaxPaid.getUserConfig(order);
    	if (userConfig == null) {
    		throw new RcsServiceException(-1, "用户单场或者单日配置为空");
		}
    	
    	RcsBusinessMatchPaidConfig matchConfig = singleMacthMaxPaid.getMatchPaidConfig(order);
    	if (matchConfig == null) {
    		throw new RcsServiceException(-1, "单场赛事配置为空");
		}
    	
    	args.add(userPlayConfig.getPlayMaxPay() + "");
    	args.add(userConfig.getUserMatchPayVal().toPlainString());
    	args.add(userConfig.getUserDayPayVal().toPlainString());
    	args.add(matchConfig.getMatchMaxPayVal().toPlainString());
    	
		return args;
	}


	private List<String> buildRedisKeys(ExtendBean order) {
		String busId = order.getBusId();
    	String orderNo = order.getOrderId();
    	String matchId = order.getMatchId();

    	String dateExpect = order.getDateExpect();
    	String matchType = order.getIsScroll();
    	String playId = order.getPlayId();
    	String playType = order.getPlayType();
    	String sportId = order.getSportId();
    	String userId = order.getUserId();
    	String marketId = order.getMarketId();
    	String suffix = "_{" + busId + "_" + matchId + "}";
		String userMatchPlayMarketKey = "RCS:RISK:" + dateExpect + ":" + busId  + ":" + sportId + ":" + userId + ":" + matchId + ":" + playId + ":" + marketId;
    	userMatchPlayMarketKey = userMatchPlayMarketKey + ":" + matchType + ":" + playType;
    	
    	String userMatchPlayKey = "RCS:RISK:" + dateExpect + ":" + busId  + ":" + sportId + ":" + userId + ":" + matchId + ":" + playId;
    	userMatchPlayKey = userMatchPlayKey + ":" + matchType + ":" + playType;
    	
    	String userMatchKey = "RCS:RISK:" + dateExpect + ":" + busId  + ":" + sportId + ":" + userId + ":" + matchId ;
    	String userKey = "RCS:RISK:" + dateExpect + ":" + busId  + ":" + sportId + ":" + userId ;
    	String singleMatchKey = "RCS:RISK:" + dateExpect + ":" + busId  + ":" + sportId + ":" + matchId ;

    	List<String> keys = new ArrayList<>();
    	keys.add("RCS:ORDER:" + orderNo + suffix);
    	keys.add(userMatchPlayMarketKey + suffix);
    	keys.add(userMatchPlayKey + suffix);
    	keys.add(userMatchKey + suffix);
    	keys.add(userKey + suffix);
    	keys.add(singleMatchKey + suffix);
    	
		return keys;
	}
	
	
	public Long getUserSelectsMaxBetAmount(ExtendBean order, Long[][] rec) {
		String busId = order.getBusId();
    	String matchId = order.getMatchId();
    	String dateExpect = order.getDateExpect();
    	String matchType = order.getIsScroll();
    	String playId = order.getPlayId();
    	String playType = order.getPlayType();
    	String sportId = order.getSportId();
    	String userId = order.getUserId();
    	String marketId = order.getMarketId();
    	String suffix = "_{" + busId + "_" + matchId + "}";
		String userMatchPlayMarketKey = "RCS:RISK:" + dateExpect + ":" + busId  + ":" + sportId + ":" + userId + ":" + matchId + ":" + playId + ":" + marketId;
    	userMatchPlayMarketKey = userMatchPlayMarketKey + ":" + matchType + ":" + playType;
    	
    	String userMatchPlayKey = "RCS:RISK:" + dateExpect + ":" + busId  + ":" + sportId + ":" + userId + ":" + matchId + ":" + playId;
    	userMatchPlayKey = userMatchPlayKey + ":" + matchType + ":" + playType;
    	
    	String userMatchKey = "RCS:RISK:" + dateExpect + ":" + busId  + ":" + sportId + ":" + userId + ":" + matchId ;
    	String userKey = "RCS:RISK:" + dateExpect + ":" + busId  + ":" + sportId + ":" + userId ;
    	String singleMatchKey = "RCS:RISK:" + dateExpect + ":" + busId  + ":" + sportId + ":" + matchId ;
    	
    	RcsBusinessPlayPaidConfig userPlayConfig = userPlayMaxPaid.getPlayConfig(order);
    	if (userPlayConfig == null) {
    		throw new RcsServiceException(-1, "用户玩法配置为空");
		}
    	
    	RcsBusinessUserPaidConfig userConfig = userMatchMaxPaid.getUserConfig(order);
    	if (userConfig == null) {
    		throw new RcsServiceException(-1, "用户单场或者单日配置为空");
		}
    	
    	RcsBusinessMatchPaidConfig matchConfig = singleMacthMaxPaid.getMatchPaidConfig(order);
    	if (matchConfig == null) {
    		throw new RcsServiceException(-1, "单场赛事配置为空");
		}
    	
    	String secondKey = playId + '_' + matchType + '_' + playType;
		String redisDbPaid = redisClient.hGet(userMatchKey + suffix, secondKey);
		if(redisDbPaid == null){
			redisDbPaid = "0";
		}
		
		Long minPaid = new BigDecimal(String.valueOf(userPlayConfig.getPlayMaxPay())).subtract(new BigDecimal(redisDbPaid)).longValue();
		if(minPaid <= 0 ) {
			log.warn("::{}::玩法维度限额超出：{}",order.getOrderId(),redisDbPaid);
			return 0L;
		}
		log.info("::{}::查询最大限额玩法维度：{},玩法配置值：{},玩法redis值:{}",order.getOrderId(),minPaid,userPlayConfig.getPlayMaxPay(),redisDbPaid);
		
		redisDbPaid = redisClient.hGet(userMatchKey  + suffix, "USER_MATCH_ALL_PAID");
		if(redisDbPaid == null){
			redisDbPaid = "0";
		}
		minPaid = Math.min(minPaid, userConfig.getUserMatchPayVal().subtract(new BigDecimal(redisDbPaid)).longValue());
		if(minPaid <= 0 ) {
			log.warn("::{}::用户赛事限额超出：{}",order.getOrderId(),redisDbPaid);
			return 0L;
		}

		log.info("::{}::查询用户赛事维度：{},赛事配置值：{},赛事redis值:{}",order.getOrderId(),userConfig.getUserMatchPayVal().subtract(new BigDecimal(redisDbPaid)).longValue(),userConfig.getUserMatchPayVal(),redisDbPaid);
		
		redisDbPaid = redisClient.get(userKey  + suffix);
		if(redisDbPaid == null){
			redisDbPaid = "0";
		}
		minPaid = Math.min(minPaid, userConfig.getUserDayPayVal().subtract(new BigDecimal(redisDbPaid)).longValue());
		if(minPaid <= 0 ) {
			log.warn("::{}::用户单日限额超出：{}",order.getOrderId(),redisDbPaid);
			return 0L;
		}

		log.info("::{}::查询用用户单日限额维度：{},用户单日限额配置值：{},用户单日限额redis值:{}",order.getOrderId(),userConfig.getUserDayPayVal().subtract(new BigDecimal(redisDbPaid)).longValue(),userConfig.getUserDayPayVal(),redisDbPaid);

		redisDbPaid = redisClient.get(singleMatchKey  + suffix);
		if(redisDbPaid == null){
			redisDbPaid = "0";
		}
		minPaid = Math.min(minPaid, matchConfig.getMatchMaxPayVal().subtract(new BigDecimal(redisDbPaid)).longValue());
		if(minPaid <= 0 ) {
			log.warn("::{}::单场赛事限额超出：{}",order.getOrderId(),redisDbPaid);
			return 0L;
		}

		log.info("::{}::查询单场赛事维度：{},单场赛事配置值：{},单场赛事redis值:{}",order.getOrderId(),matchConfig.getMatchMaxPayVal().subtract(new BigDecimal(redisDbPaid)).longValue(),matchConfig.getMatchMaxPayVal(),redisDbPaid);

		return minPaid;
	}
	
	public void prizeHandle(ExtendBean order, SettleItem settleItem) {
		if(order.getProfit().longValue() > 0 ){
			String dateExpect = DateUtils.getTimeExpect(settleItem.getSettleTime());
			String key = String.format(RedisKeys.PAID_DATE_BUS_REDIS_CACHE,dateExpect, order.getBusId());
	        Long currentPaidAmount = redisClient.incrBy(key,order.getProfit());
			
			RcsBusinessDayPaidConfig config = configService.getDayPaidConfig(order.getBusId());
			
			if(currentPaidAmount >= config.getStopVal().longValue()) {
				String stopKey = String.format(RedisKeys.PAID_DATE_BUS_STOP_REDIS_CACHE, dateExpect, order.getBusId());
				redisClient.set(stopKey, 1);
			}
        }
		
		List<String> keysList = buildRedisKeys(order);
		
		Long[][] recVal = null;
		if(order.getRecVal() != null  && order.getRecType() == 0) {
			JSONArray arrays = JSONArray.parseArray(order.getRecVal());
			recVal = new Long[arrays.size()][];
			for(int i = 0 ; i < arrays.size() ; i ++) {
				JSONArray keys = arrays.getJSONArray(i);
				recVal[i] = new Long[keys.size()];
				for(int j = 0 ; j < keys.size() ; j ++) {
					recVal[i][j] = keys.getLong(j);
				}
			}
		}
		List<String> valuesList = buildRedisValus(order, recVal);
		//profitVal  盈利金额（作为用户计算）
		valuesList.add(order.getProfit() + "");
		log.info("::{}::返奖计算 orderPrize.lua keys:{},valueList:{}",settleItem.getOrderNo(),JSONObject.toJSONString(keysList),JSONObject.toJSONString(valuesList));
		Object ret = redisClient.evalsha(prizeShaKey, keysList, valuesList);
    	JSONArray jsonArr = JSONObject.parseArray(JSONObject.toJSONString(ret));
    	log.info("::{}::返奖计算成功：order:{},result:{}",order.getOrderId(),order,jsonArr.toJSONString());
	}
	
}
