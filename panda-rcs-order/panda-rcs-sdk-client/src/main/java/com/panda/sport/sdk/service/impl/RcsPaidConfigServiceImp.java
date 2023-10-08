package com.panda.sport.sdk.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.data.rcs.api.limit.LimitApiService;
import com.panda.sport.rcs.pojo.*;
import com.panda.sport.sdk.annotation.AutoInitMethod;
import com.panda.sport.sdk.constant.RedisKeys;
import com.panda.sport.sdk.constant.SdkConstants;
import com.panda.sport.sdk.core.JedisClusterServer;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.service.RcsPaidConfigService;
import com.panda.sport.sdk.vo.RcsBusinessConPlayConfig;
import com.panda.sport.sdk.vo.RcsMatchOrderAcceptConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * <p>
 * 赔付配置类
 * </p>
 *
 * @author admin
 * @since 2019-10-04
 */
@Singleton
@AutoInitMethod(init = "initConfigCache")
public class RcsPaidConfigServiceImp implements RcsPaidConfigService {
    @Inject
    JedisClusterServer jedisClusterServer;
    @Inject
    LimitApiService limitApiService;

    private static final Logger log = LoggerFactory.getLogger(RcsPaidConfigServiceImp.class);

    public static String BUS_DAY_CONFIG_KEY = "BusDay";
    public static String BUS_MATCH_CONFIG_KEY = "BusMatch";
    public static String BUS_PLAY_CONFIG_KEY = "BusPlay";
    public static String BUS_USER_CONFIG_KEY = "BusUser";
    public static String BUS_SINGLE_BET_CONFIG_KEY = "BusSingleBet";
    public static String BUS_CON_PLAY_CONFIG_KEY = "BusConPlay";

    private static Map<String, Object> cacheConfigMap = new HashMap<String, Object>();

    private static Map<String, String> allPlayMap = new HashMap<String, String>();

    private static Map<String, Class> keyClassMapping = new HashMap<String, Class>();

    private static Map<String, Class> busMap = new HashMap<>();

    static {
//    	keyClassMapping.put("BusDay", RcsBusinessDayPaidConfig.class);
//    	keyClassMapping.put("BusMatch", RcsBusinessMatchPaidConfig.class);
//    	keyClassMapping.put("BusPlay", RcsBusinessPlayPaidConfig.class);
//    	keyClassMapping.put("BusUser", RcsBusinessUserPaidConfig.class);
//    	keyClassMapping.put("BusSingleBet", RcsBusinessSingleBetConfig.class);
//    	keyClassMapping.put("BusConPlay", RcsBusinessConPlayConfig.class);
//    	keyClassMapping.put("orderAcceptConfigs", RcsMatchOrderAcceptConfig.class);
        keyClassMapping.put("AllPlay", StandardSportMarketCategory.class);

//		busMap.put("BusDay", RcsBusinessDayPaidConfig.class);
//		busMap.put("BusMatch", RcsBusinessMatchPaidConfig.class);
//		busMap.put("BusPlay", RcsBusinessPlayPaidConfig.class);
//		busMap.put("BusUser", RcsBusinessUserPaidConfig.class);
//		busMap.put("BusSingleBet", RcsBusinessSingleBetConfig.class);
//		busMap.put("BusConPlay", RcsBusinessConPlayConfig.class);
    }

    private static Map<String, Long> keyTimeMapping = new HashMap<String, Long>();
    
    /*public void initConfig(){
		initConfigCache();
		Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
			for(String key : keyTimeMapping.keySet()) {
				String val = jedisClusterServer.hgetNoLog(RedisKeys.MONITOR_PAID_CONFIG_REDIS_CACHE, key);
				Long redisValue = StringUtils.isBlank(val) ? -1l : Long.parseLong(val);

				if(redisValue > keyTimeMapping.get(key)) {//需要更新缓存
					log.info("监控到缓存数据更新，key：{}",key);
					initConfigByKey(key);
				}

			}
		}, 1, 1, TimeUnit.SECONDS);
	}*/

    public void initConfigByKey(String key) {
        String redisKey = String.format("%s%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, key);
        if (busMap.containsKey(key)) {
            //initConfigByClass2(key, redisKey, keyClassMapping.get(key));
        } else {
            initConfigByClass(key, redisKey, keyClassMapping.get(key));
        }
    }

    /**
     * 限额维度 加载缓存
     *
     * @param key
     * @param redisKey
     * @param clazz
     * @param <T>
     */
    private <T> void initConfigByClass2(String key, String redisKey, Class<T> clazz) {

    }


    private <T> List<T> initConfigByClass(String key, String redisKey, Class<T> clazz) {
        String json = jedisClusterServer.get(redisKey);
        JSONArray jsonArray = JSONObject.parseArray(json);
        List<T> dayList = null;
        if (jsonArray != null) {
            dayList = JSONArray.parseArray(jsonArray.toString(), clazz);
        }
        cacheConfigMap.put(clazz.getName(), json);

        String val = jedisClusterServer.hget(RedisKeys.MONITOR_PAID_CONFIG_REDIS_CACHE, key);
        keyTimeMapping.put(key, StringUtils.isBlank(val) ? -1L : Long.parseLong(val));

        if ("AllPlay".equals(key)) {//玩法需要特殊处理
            Optional.of(dayList).ifPresent(consumer -> {
                consumer.forEach(info -> {
                    JSONObject jsonObj = JSONObject.parseObject(JSONObject.toJSONString(info));
                    allPlayMap.put(jsonObj.getString("sportId") + "_" + jsonObj.getString("id"), jsonObj.getString("theirTime"));
                });
            });
        }

        return dayList;
    }

    public void initConfigCache() {
        try {
            for (String key : keyClassMapping.keySet()) {
                initConfigByKey(key);
            }
            log.info("sdk client 同步基础数据成功!");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

//	public void initConfigCache() {
//    	try {
//
//			String json = jedisClusterServer.get(String.format("%s%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, "BusDay"));
//			JSONArray jsonArray = JSONObject.parseArray(json);
//			List<RcsBusinessDayPaidConfig> dayList = null;
//			if (jsonArray != null) {
//				dayList = JSONArray.parseArray(jsonArray.toString(), RcsBusinessDayPaidConfig.class);
//			}
//
//			json = jedisClusterServer.get(String.format("%s%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, "BusMatch"));
//			jsonArray = JSONObject.parseArray(json);
//			List<RcsBusinessMatchPaidConfig> matchList = null;
//			if (jsonArray != null) {
//				matchList = JSONArray.parseArray(jsonArray.toString(), RcsBusinessMatchPaidConfig.class);
//			}
//
//			json = jedisClusterServer.get(String.format("%s%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, "BusPlay"));
//			jsonArray = JSONObject.parseArray(json);
//			List<RcsBusinessPlayPaidConfig> playList = null;
//			if (jsonArray != null) {
//				playList = JSONArray.parseArray(jsonArray.toString(), RcsBusinessPlayPaidConfig.class);
//			}
//
//			json = jedisClusterServer.get(String.format("%s%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, "BusUser"));
//			jsonArray = JSONObject.parseArray(json);
//			List<RcsBusinessUserPaidConfig> userList = null;
//			if (jsonArray != null) {
//				userList = JSONArray.parseArray(jsonArray.toString(), RcsBusinessUserPaidConfig.class);
//			}
//
//			json = jedisClusterServer.get(String.format("%s%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, "BusSingleBet"));
//			jsonArray = JSONObject.parseArray(json);
//			List<RcsBusinessSingleBetConfig> singleBetList = null;
//			if (jsonArray != null) {
//				singleBetList = JSONArray.parseArray(jsonArray.toString(), RcsBusinessSingleBetConfig.class);
//			}
//
//			json = jedisClusterServer.get(String.format("%s%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, "BusConPlay"));
//			jsonArray = JSONObject.parseArray(json);
//			List<RcsBusinessConPlayConfig> conList = null;
//			if (jsonArray != null) {
//				conList = JSONArray.parseArray(jsonArray.toString(), RcsBusinessConPlayConfig.class);
//			}
//
//			json = jedisClusterServer.get(String.format("%s%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, "orderAcceptConfigs"));
//			jsonArray = JSONObject.parseArray(json);
//			List<RcsMatchOrderAcceptConfig> orderAcceptConfigs = null;
//			if (jsonArray != null) {
//				orderAcceptConfigs = JSONArray.parseArray(jsonArray.toString(), RcsMatchOrderAcceptConfig.class);
//			}
//
//			cacheConfigMap.put(RcsBusinessDayPaidConfig.class.getName(), dayList);
//			cacheConfigMap.put(RcsBusinessMatchPaidConfig.class.getName(), matchList);
//			cacheConfigMap.put(RcsBusinessPlayPaidConfig.class.getName(), playList);
//			cacheConfigMap.put(RcsBusinessUserPaidConfig.class.getName(), userList);
//			cacheConfigMap.put(RcsBusinessSingleBetConfig.class.getName(), singleBetList);
//			cacheConfigMap.put(RcsBusinessConPlayConfig.class.getName(), conList);
//			cacheConfigMap.put(RcsMatchOrderAcceptConfig.class.getName(), orderAcceptConfigs);
//
//			//需要缓存
//			json = jedisClusterServer.get(String.format("%s%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, "AllPlay"));
//			jsonArray = JSONObject.parseArray(json);
//			List<StandardSportMarketCategory> allPlayList = null;
//			if (jsonArray != null) {
//				allPlayList = JSONArray.parseArray(jsonArray.toString(), StandardSportMarketCategory.class);
//			}
//			Optional.of(allPlayList).ifPresent(consumer -> {
//				consumer.forEach(info -> {
//					allPlayMap.put(String.valueOf(info.getId()), String.valueOf(info.getTheirTime()));
//				});
//			});
//
//			log.info("sdk client 同步基础数据成功!");
//		}
//    	catch (Exception ex){
//    		log.error("初始化基础数据失败!");
//		}
//	}
    /**
     * 获取玩法类型所属时段
     *
     * @param sportId 赛种ID
     * @param playId 玩法ID
     */
    public String getPlayProcess(String sportId, String playId) {
        String result = allPlayMap.get(sportId + "_" + playId);
        if (StringUtils.isBlank(result)) {
            String str = limitApiService.queryPlayInfoById(Integer.valueOf(sportId), Integer.valueOf(playId)).getData();
            if (StringUtils.isBlank(str)) {
                log.info("玩法不存在,allPlayMap的bean={},查询数据库返回:{}", JSONObject.toJSONString(allPlayMap),str);
                throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_CONFIG, "玩法不存在");
            }
            allPlayMap.put(sportId + "_" + playId, str);
            return str;
        }
        return result;
    }

    /**
     * key 为空则取clazz
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> List<T> getCacheConfig(String key, Class<T> clazz) {
        if (StringUtils.isNotEmpty(key)) {
            return (List<T>) cacheConfigMap.get(clazz.getName() + "_" + key);
        }
        return (List<T>) cacheConfigMap.get(clazz.getName());
    }


    /**
     * @param <T>
     * @param key
     * @param clazz
     * @return
     */
    public <T> List<T> getConfigCache(String key, String busId, Class<T> clazz) {
        if (key == null) {
            return null;
        }
        List<T> result = getCacheConfig(busId, clazz);

        if (result != null) {
            return result;
        }
        String data = jedisClusterServer.get(String.format("%s%s:%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, key, busId));
        List<T> infoList = new ArrayList<T>();
        if (data == null) {
            throw new RcsServiceException("getConfigCache缓存异常,key值=" + String.format("%s%s:%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, key, busId));
        } else {
            infoList = JSONObject.parseArray(data, clazz);
        }

        return infoList;
    }


    private void checkParamsNull(String... args) {
        for (String arg : args) {
            if (StringUtils.isBlank(arg)) {
                throw new RcsServiceException("参数错误，不能为空:" + arg);
            }
        }
    }

    public RcsBusinessDayPaidConfig getDayPaidConfig(String busId) {
        checkParamsNull(busId);
        List<RcsBusinessDayPaidConfig> list = getConfigCache(BUS_DAY_CONFIG_KEY, busId, RcsBusinessDayPaidConfig.class);
        for (RcsBusinessDayPaidConfig info : list) {
            if (busId.equals(String.valueOf(info.getBusinessId()))) {
                return info;
            }
        }
        return null;
    }

    public void setDayPaidConfig(String busId, RcsBusinessDayPaidConfig config) {
        String redisKey = String.format("%s%s:%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, BUS_DAY_CONFIG_KEY, busId);
        List<RcsBusinessDayPaidConfig> list = new ArrayList<>();
        list.add(config);
        jedisClusterServer.set(redisKey, JSONObject.toJSONString(list));
    }

    public RcsBusinessConPlayConfig getConPlayConfig(String busId, String type) {
        checkParamsNull(busId, type);
        List<RcsBusinessConPlayConfig> list = getConfigCache(BUS_CON_PLAY_CONFIG_KEY, busId, RcsBusinessConPlayConfig.class);
        for (RcsBusinessConPlayConfig info : list) {
            if (busId.equals(String.valueOf(info.getBusinessId()))
                    && type.equals(String.valueOf(info.getPlayType()))) {
                return info;
            }
        }
        return null;
    }

    public void setConPlayConfig(String busId, List<RcsBusinessConPlayConfig> configs) {
        String redisKey = String.format("%s%s:%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, BUS_CON_PLAY_CONFIG_KEY, busId);
        jedisClusterServer.set(redisKey, JSONObject.toJSONString(configs));
    }

    public RcsBusinessSingleBetConfig getSingleBetConfig(String busId, String sportId, String matchType, String timePeriod,
                                                         String playId, String tournamentLevel) {
        checkParamsNull(busId, sportId, matchType, timePeriod, playId, tournamentLevel);
        List<RcsBusinessSingleBetConfig> list = getConfigCache(BUS_SINGLE_BET_CONFIG_KEY, busId, RcsBusinessSingleBetConfig.class);
        RcsBusinessSingleBetConfig otherConfig = null;
        for (RcsBusinessSingleBetConfig info : list) {
            if (busId.equals(String.valueOf(info.getBusinessId())) &&
                    sportId.equals(String.valueOf(info.getSportId())) &&
                    matchType.equals(String.valueOf(info.getMatchType())) &&
                    timePeriod.equals(String.valueOf(info.getTimePeriod())) &&
                    tournamentLevel.equals(String.valueOf(info.getTournamentLevel())) &&
                    "-1".equals(String.valueOf(info.getPlayId()))) {
                otherConfig = info;
            }

            if (busId.equals(String.valueOf(info.getBusinessId())) &&
                    sportId.equals(String.valueOf(info.getSportId())) &&
                    matchType.equals(String.valueOf(info.getMatchType())) &&
                    timePeriod.equals(String.valueOf(info.getTimePeriod())) &&
                    tournamentLevel.equals(String.valueOf(info.getTournamentLevel())) &&
                    playId.equals(String.valueOf(info.getPlayId()))) {
                return info;
            }
        }
        return otherConfig;
    }

    public void setSingleBetConfig(String busId, List<RcsBusinessSingleBetConfig> configs) {
        String redisKey = String.format("%s%s:%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, BUS_SINGLE_BET_CONFIG_KEY, busId);
        jedisClusterServer.set(redisKey, JSONObject.toJSONString(configs));
    }

    public RcsBusinessMatchPaidConfig getMatchPaidConfig(String busId, String sportId, String tournamentLevel) {
        checkParamsNull(busId, sportId, tournamentLevel);
        List<RcsBusinessMatchPaidConfig> list = getConfigCache(BUS_MATCH_CONFIG_KEY, busId, RcsBusinessMatchPaidConfig.class);
        for (RcsBusinessMatchPaidConfig info : list) {
            if (busId.equals(String.valueOf(info.getBusinessId())) &&
                    sportId.equals(String.valueOf(info.getSportId())) &&
                    tournamentLevel.equals(String.valueOf(info.getTournamentLevel()))) {
                return info;
            }
        }
        return null;
    }

    public void setMatchPaidConfig(String busId, List<RcsBusinessMatchPaidConfig> configs) {
        String redisKey = String.format("%s%s:%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, BUS_MATCH_CONFIG_KEY, busId);
        jedisClusterServer.set(redisKey, JSONObject.toJSONString(configs));
    }

    /*
     * match_type 投注阶段  未开赛 和 滚球
     * playType 玩法类型   全场 上半场  下半场
     * play_id  玩法id  -1表示其他玩法
     */
    public RcsBusinessPlayPaidConfig getPlayPaidConfig(String busId, String sportId, String matchType, String playType, String playId) {
        checkParamsNull(busId, sportId, matchType, playType, playId);
        List<RcsBusinessPlayPaidConfig> list = getConfigCache(BUS_PLAY_CONFIG_KEY, busId, RcsBusinessPlayPaidConfig.class);
        RcsBusinessPlayPaidConfig otherConfig = null;
        for (RcsBusinessPlayPaidConfig info : list) {
            if (busId.equals(String.valueOf(info.getBusinessId())) &&
                    sportId.equals(String.valueOf(info.getSportId())) &&
                    matchType.equals(String.valueOf(info.getMatchType())) &&
                    playType.equals(String.valueOf(info.getPlayType())) &&
                    "-1".equals(String.valueOf(info.getPlayId()))) {
                otherConfig = info;
            }

            if (busId.equals(String.valueOf(info.getBusinessId())) &&
                    sportId.equals(String.valueOf(info.getSportId())) &&
                    matchType.equals(String.valueOf(info.getMatchType())) &&
                    playType.equals(String.valueOf(info.getPlayType())) &&
                    playId.equals(String.valueOf(info.getPlayId()))) {
                return info;
            }
        }
        return otherConfig;
    }

    public void setPlayPaidConfig(String busId, List<RcsBusinessPlayPaidConfig> configs) {
        String redisKey = String.format("%s%s:%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, BUS_PLAY_CONFIG_KEY, busId);
        jedisClusterServer.set(redisKey, JSONObject.toJSONString(configs));
    }


    public RcsBusinessUserPaidConfig getUserPaidConfig(String busId, String userId) {
        checkParamsNull(busId, userId);
        List<RcsBusinessUserPaidConfig> list = getConfigCache(BUS_USER_CONFIG_KEY, busId, RcsBusinessUserPaidConfig.class);
        RcsBusinessUserPaidConfig otherConfig = null;
        for (RcsBusinessUserPaidConfig info : list) {
            if (busId.equals(String.valueOf(info.getBusinessId())) &&
                    "-1".equals(String.valueOf(info.getUserId()))) {
                otherConfig = info;
            }

            if (busId.equals(String.valueOf(info.getBusinessId())) &&
                    userId.equals(String.valueOf(info.getUserId()))) {
                return info;
            }
        }
        return otherConfig;
    }

    public void setUserPaidConfig(String busId, List<RcsBusinessUserPaidConfig> configs) {
        String redisKey = String.format("%s%s:%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, BUS_USER_CONFIG_KEY, busId);
        jedisClusterServer.set(redisKey, JSONObject.toJSONString(configs));
    }

    public RcsMatchOrderAcceptConfig getOrderAcceptConfigs(Long matchId, long tournamentId) {
        List<RcsMatchOrderAcceptConfig> list = getConfigCache("orderAcceptConfigs", "", RcsMatchOrderAcceptConfig.class);
        RcsMatchOrderAcceptConfig otherConfig = null;
        for (RcsMatchOrderAcceptConfig info : list) {
            if (info.getMatchId().equals(matchId) || info.getMatchId().equals(tournamentId)) {
                return info;
            }
        }
        return otherConfig;
    }

    public Map<String, Long> getKeyTimeMapping() {
        return this.keyTimeMapping;
    }

}
