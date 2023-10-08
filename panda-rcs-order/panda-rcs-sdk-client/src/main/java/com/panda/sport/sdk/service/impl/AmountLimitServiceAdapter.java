//package com.panda.sport.sdk.service.impl;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.panda.sport.data.rcs.dto.OrderItem;
//import com.panda.sport.data.rcs.dto.ThreewayOverLoadTriggerItem;
//import com.panda.sport.data.rcs.pojo.RcsMatchMarketConfig;
//import com.panda.sport.rcs.common.DateUtils;
//import com.panda.sport.rcs.common.FileReadUtils;
//import com.panda.sport.rcs.constants.PlayIdEnum;
//import com.panda.sport.rcs.constants.RedisKeys;
//import com.panda.sport.rcs.core.cache.client.RedisClient;
//import com.panda.sport.rcs.core.utils.BeanCopyUtils;
//import com.panda.sport.rcs.exeception.LogicException;
//import com.panda.sport.rcs.exeception.RcsServiceException;
//import com.panda.sport.rcs.mgr.calculator.service.impl.ThreeWayAmountLimitServiceImpl;
//import com.panda.sport.rcs.mgr.calculator.service.impl.TwoWaySingleAmountLimitServiceImpl;
//import com.panda.sport.rcs.mgr.wrapper.AmountLimitService;
//import com.panda.sport.rcs.mgr.wrapper.BalanceService;
//import com.panda.sport.rcs.mgr.wrapper.IRcsMatchMarketConfigService;
//import com.panda.sport.rcs.mgr.wrapper.MarketOddsChangeCalculationService;
//import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
//import com.panda.sport.sdk.constant.RedisKeys;
//import com.panda.sport.sdk.core.JedisClusterServer;
//import com.panda.sport.sdk.exception.RcsServiceException;
//import com.panda.sport.sdk.util.DateUtils;
//import com.panda.sport.sdk.util.FileUtil;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.*;
//
///**
// * @author :  holly
// * @Project Name :rcs-parent
// * @Package Name :com.panda.sport.rcs.rpc.calculator.service.impl
// * @Description :操盘限额计算
// * @Date: 2019-10-22 21:36
// */
//
//public abstract class AmountLimitServiceAdapter implements AmountLimitService {
//    /**
//     * 加载lua脚本和计算
//     * @param fileName
//     * @return
//     */
//
//
//    public String shakey = null;
//
//
//    public AmountLimitServiceAdapter( ) {
//        String text = new FileUtil().getFileTxt("lua/oddsCalc.lua");
//
//		//log.info("oddsCalc lua 脚本内容text:{}",text);
//		shakey = JedisClusterServer.scriptLoad(text);
//		if(shakey == null) {
//            throw new RcsServiceException("oddsCalc lua脚本加载失败");
//        }
//
//    }
//
//    public void executeCalcLua(OrderItem item, RcsMatchMarketConfig result, Object...  params ) {
//    	String key = String.format(RedisKeys.CALC_AMOUNT_ODDS_CHANGE, Optional.ofNullable(item.getDateExpect()).orElse(DateUtils.transferDateToString(new Date())),item.getMarketId());
//    	Integer isLock = 0 ;
//        String suffixKey = "{" + item.getMarketId() + "}" ;
//        JSONArray jsonArr = null;
//        try {
//        	List<String> keys = new ArrayList<>();
//
//        	keys.add(key + suffixKey);
//        	keys.add(key + ":count" + suffixKey);
//        	keys.add(key + ":lock" + suffixKey);
//        	List<String> args = new ArrayList<>();
//        	args.add(String.valueOf(params[0]));//是否是二项盘
//        	args.add(String.valueOf(params[1]));//金额
//        	args.add(String.valueOf(params[2]));//赔率
//        	args.add(String.valueOf(Optional.ofNullable(params[3]).orElse(0)));//主一级金额
//        	args.add(String.valueOf(Optional.ofNullable(params[4]).orElse(0)));//主二级金额
//        	args.add(String.valueOf(params[5]));//投注项id
//        	args.add(String.valueOf(Optional.ofNullable(params[6]).orElse(0)));//客一级金额
//        	args.add(String.valueOf(Optional.ofNullable(params[7]).orElse(0)));//客二级金额
//        	args.add(String.valueOf(params[8]));//二项盘投注类型 1：主  3：客
//        	Object ret = JedisClusterServer.evalsha(shakey, keys, args);
//        	//lockVal,isChangeOdds,changeLevel,currentOptionAmount,allAmount
//        	//当前赔率是否锁定   ，是否需要变更赔率  ，变更级别  ，当前投注项计算金额"主客赔率均不可以为空"，当前盘口总金额
//            log.info("{} executeCalcLua item:{}",this.getClass(),item);
////        	log.info("shakey:{},keys：{}，args:{},result:{}",shakey,keys,args, JSONObject.toJSONString(ret));
//    		jsonArr = JSONObject.parseArray(JSONObject.toJSONString(ret));
//    		isLock = jsonArr.getInteger(0);
//    		if(isLock == 2) {
//    		    throw new RcsServiceException(7001, "赔率正在变化中,下单失败");
//            }
//    		if(isLock == 1) {
//    			triggerChange(result, item,jsonArr);
//    		}
//
//        }catch (RcsServiceException e) {
//        	if(e.getCode() == 7001) {
//        		throw e;
//        	}
//        }catch (Exception e) {
//        	log.error(e.getMessage(),e);
//        	throw new RcsServiceException(7002, "赔率计算失败，请重试!");
//        }finally {
//			if(isLock == 1) {//释放锁
//                JedisClusterServer.delete(key + suffixKey);
//                JedisClusterServer.delete(key + ":count" + suffixKey);
//                JedisClusterServer.delete(key + ":lock" + suffixKey);
//                //清0平衡值
//                if(String.valueOf(params[0]).equalsIgnoreCase("1")){
//                    balanceService.updateBalance(item.getMatchId(), item.getMarketId(), (long)0);
//                }
//			}else{
//                //触发平衡值到前端
//                if(isLock != 2 && jsonArr != null && String.valueOf(params[0]).equals("1")){
//                    Long currentOptionAmount = jsonArr.getLong(3);
//                    Long allAmount = jsonArr.getLong(4);
//                    balanceService.updateBalance(item.getMatchId(), item.getMarketId(), currentOptionAmount-(allAmount - currentOptionAmount));
//                }
//            }
//		}
//    }
//
//    public abstract void triggerChange(RcsMatchMarketConfig result, OrderItem item, JSONArray exeResultArray);
//
//
//
//
//    /**
//     * 限额计算||平衡值 赛事和联赛
//     * 两项盘返回负数 代表客方投注超过主方投注
//     * 三项盘返回负数 代表预期盈利为亏损
//     * @param item
//     */
//    public RcsMatchMarketConfig getConfiguredParams(OrderItem item) {
//        log.info("{} getConfiguredParams , item:{}",this.getClass(),item);
//        if(item == null) return null;
//        String amoutLimitKey = "rcs:amount:limit:betno:"+item.getBetNo();
//        if(redisClient.exist(amoutLimitKey)) {
//            log.error("{} checkItemValid 不能重复计算同一个注单！betno:{}", this.getClass(),item.getBetNo());
//            return null;
//        }else{
//            redisClient.setExpiry(amoutLimitKey,item.getBetNo(),RedisKeys.getExpireTime());
//        }
//        checkValidPlay(item);
//        RcsMatchMarketConfig result = null;
//        try {
//            checkOrderItemArguments(item);
//            //计算当前限额 || 平衡值  拆分成两个
//            result = getMarketConfig(rcsMatchMarketConfigService,item);
//            if(result == null || result.getHomeLevelFirstMaxAmount() == null){
//                log.warn("赛事或联赛未配置任何参数，使用自动数据源数据，不做计算！item:{}", item);
//                return null;
//            }
//            //是否使用数据源0：手动；1：使用数据源
//            if(result.getDataSource() == null || result.getDataSource() == 1) {
//                log.warn("赛事或联赛使用自动数据源数据，不做计算！item:{}", item);
//                return null;
//            }
//        } catch (LogicException e) {
//            log.error("{} checkItemValid code:{},error :{}",this.getClass(),e.getCode(),e.getMessage());
//        }catch (Exception e) {
//            log.error("{} checkItemValid error :{}",this.getClass(),e.getMessage());
//        }
//        return result;
//    }
//
//    private void checkValidPlay(OrderItem item){
//        if(this.getClass().equals(ThreeWayAmountLimitServiceImpl.class)){
//            if(!Arrays.asList(PlayIdEnum.ThreeWay.getThreeWayPlay()).contains(item.getPlayId())) {
//                log.error("{} checkValidPlay  玩法不匹配 ThreeWay , playId:{}",this.getClass(),item.getPlayId());
//                throw new LogicException("60001", "玩法和所调用类型不匹配");
//            }
//        }else if(this.getClass().equals(TwoWaySingleAmountLimitServiceImpl.class)){
//            if(!Arrays.asList(PlayIdEnum.EvenOddTotal.getTwoWaySinglePlay()).contains(item.getPlayId())) {
//                log.error("{} checkValidPlay  玩法不匹配 TwoWay single , playId:{}",this.getClass(),item.getPlayId());
//                throw new LogicException("60002", "玩法和所调用类型不匹配");
//            }
//        }else{
//            if(!Arrays.asList(PlayIdEnum.HandicapAnd1X2.getTwoWayDoublePlay()).contains(item.getPlayId())) {
//                log.error("{} checkValidPlay  玩法不匹配 TwoWay double , playId:{}",this.getClass(),item.getPlayId());
//                throw new LogicException("60003", "玩法和所调用类型不匹配");
//            }
//        }
//    }
//
//
//
//
//    /*
//	校验注单单参数
//	 */
//    private void checkOrderItemArguments(OrderItem bean) throws LogicException {
//        if(bean.getPlayId() == null) throw new LogicException("605", "玩法ID不能为空！");
//        if(bean.getMatchId() == null) throw new LogicException("606", "比赛ID不能为空！");
//        if(bean.getBetAmount() == null || bean.getBetAmount() == 0) throw new LogicException("609", "BetAmout不能为空！");
//        if(bean.getOddsValue() == null || bean.getOddsValue() == 0) throw new LogicException("610", "赔率OddsValue不能为空！");
//        if(bean.getPlayOptionsId() == null || bean.getPlayOptionsId() == 0) throw new LogicException("613", "投注项ID不能为空！");
//    }
//
//    /**
//     * 获取盘口预期赔付最大值
//     * @param item
//     * @return
//     */
//    public abstract void sumCurrentLoadValue(OrderItem item);
//
//
//    public <T> T getTriggerItem(OrderItem item, Class<T> targetClass)  {
//        T target = BeanCopyUtils.copyProperties(item, targetClass);
//        return target;
//    }
//    /**
//     * 触发调价和平衡值清零
//     * @param target
//     * @return
//     */
//    public void triggerForOverLoad(ThreewayOverLoadTriggerItem target) {
//    	//赔率变更改成同步
//        log.warn("{} triggerForOverLoad 限额触发赔率调整，item：{}", this.getClass(),target);
//    	marketOddsChangeCalculationService.calculationOddsByOverLoadTrigger(target);
//
//    }
//
//    public RcsMatchMarketConfig getMarketConfig(IRcsMatchMarketConfigService rcsMatchMarketConfigService, OrderItem item){
//        String key = String.format(RedisKeys.RCS_MATCH_MARKET_CONFIG,item.getMatchId(),item.getMarketId(),item.getPlayId(),item.getTournamentId());
//        RcsMatchMarketConfig rcsMatchMarketConfig = (RcsMatchMarketConfig) redisClient.getObj(key,RcsMatchMarketConfig.class);
//        if(rcsMatchMarketConfig != null){
//            return rcsMatchMarketConfig;
//        }
//        RcsMatchMarketConfig config = new RcsMatchMarketConfig();
//        config.setHomeMarketValue(null);
//        config.setAwayMarketValue(null);
//        config.setMatchId(item.getMatchId());
//        config.setMarketId(item.getMarketId());
//        config.setPlayId((long)item.getPlayId());
//        config.setTournamentId(item.getTournamentId());
//        RcsMatchMarketConfig conf = rcsMatchMarketConfigService.queryMaxBetAmount(config);
//        redisClient.set(key,conf);
//        return conf;
//    }
//}
