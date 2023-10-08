package com.panda.sport.rcs.mgr.wrapper.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.panda.sport.data.rcs.api.limit.LimitApiService;
import com.panda.sport.rcs.mapper.RcsMatchOrderAcceptEventConfigMapper;
import com.panda.sport.rcs.mapper.limit.LimitMapper;
import com.panda.sport.rcs.mgr.mq.impl.BasicConfigProvider;
import com.panda.sport.rcs.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.RcsBusinessConfigMapper;
import com.panda.sport.rcs.mgr.wrapper.RcsPaidConfigService;

/**
 * <p>
 * 赔付配置类
 * </p>
 *
 * @author admin
 * @since 2019-10-04
 */
@Component
@Slf4j
public class RcsPaidConfigServiceImp implements RcsPaidConfigService {

    @Autowired
    private RcsBusinessConfigMapper rcsBusinessConfigMapper;
    @Autowired
    private RcsMatchOrderAcceptEventConfigMapper rcsMatchOrderAcceptEventConfigMapper;

    @Autowired
    private RedisClient redisClient;
    @Autowired
    LimitMapper limitMapper;

    @Autowired
    private BasicConfigProvider basicConfigProvider;

    public static String BUS_DAY_CONFIG_KEY = "BusDay";
    public static String BUS_MATCH_CONFIG_KEY = "BusMatch";
    public static String BUS_PLAY_CONFIG_KEY = "BusPlay";
    public static String BUS_USER_CONFIG_KEY = "BusUser";
    public static String BUS_SINGLE_BET_CONFIG_KEY = "BusSingleBet";
    public static String BUS_CON_PLAY_CONFIG_KEY = "BusConPlay";
    public static String BUS_ORDER_ACCEPT_CONFIG_KEY = "orderAcceptConfigs";

    private Map<String, Object> cacheConfigMap = new HashMap<>();

    private Map<String, String> allPlayMap = new HashMap<String, String>();

    @Override
    public void initConfigCache() {
        try {
//			List<RcsBusinessDayPaidConfig> dayList = rcsBusinessConfigMapper.queryBusDayConifgList();
//			List<RcsBusinessMatchPaidConfig> matchList = rcsBusinessConfigMapper.queryBusMatchConifgList();
//			List<RcsBusinessPlayPaidConfig> playList = rcsBusinessConfigMapper.queryBusPlayConifgList();
//			List<RcsBusinessUserPaidConfig> userList = rcsBusinessConfigMapper.queryBusUserConifgList();
//			List<RcsBusinessSingleBetConfig> singleBetList = rcsBusinessConfigMapper.queryBusSingleBetConfigList();
//			List<RcsBusinessConPlayConfig> conList = rcsBusinessConfigMapper.queryBusConPlayConifgList();
//			List<RcsMatchOrderAcceptConfig> orderAcceptConfigs = rcsMatchOrderAcceptEventConfigMapper.queryOrderConfigList();

//			cacheConfigMap.put(RcsBusinessDayPaidConfig.class.getName(), dayList);
//			cacheConfigMap.put(RcsBusinessMatchPaidConfig.class.getName(), matchList);
//			cacheConfigMap.put(RcsBusinessPlayPaidConfig.class.getName(), playList);
//			cacheConfigMap.put(RcsBusinessUserPaidConfig.class.getName(), userList);
//			cacheConfigMap.put(RcsBusinessSingleBetConfig.class.getName(), singleBetList);
//			cacheConfigMap.put(RcsBusinessConPlayConfig.class.getName(), conList);
//			cacheConfigMap.put(RcsMatchOrderAcceptConfig.class.getName(), orderAcceptConfigs);

//			redisClient.setExpiry(String.format("%s%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, "BusDay"), dayList, 1000 * 60 * 10L);
//			redisClient.setExpiry(String.format("%s%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, "BusMatch"), matchList, 1000 * 60 * 10L);
//			redisClient.setExpiry(String.format("%s%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, "BusPlay"), playList, 1000 * 60 * 10L);
//			redisClient.setExpiry(String.format("%s%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, "BusUser"), userList, 1000 * 60 * 10L);
//			redisClient.setExpiry(String.format("%s%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, "BusSingleBet"), singleBetList, 1000 * 60 * 10L);
//			redisClient.setExpiry(String.format("%s%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, "BusConPlay"), conList, 1000 * 60 * 10L);
//			redisClient.setExpiry(String.format("%s%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, "orderAcceptConfigs"), orderAcceptConfigs, 1000 * 60 * 10L);

            //阶段需要修改，从standard_sport_market_category_ref修改
            List<StandardSportMarketCategory> allPlayList = rcsBusinessConfigMapper.queryAllPlayList();
            Optional.of(allPlayList).ifPresent(consumer -> {
                consumer.forEach(info -> {
                    allPlayMap.put(String.valueOf(info.getSportId() + "_" + info.getId()), String.valueOf(info.getTheirTime()));
                });
            });

            cacheConfigMap.put("AllPlay", allPlayList);

            log.info("risk 初始化基础数据成功!");
        } catch (Exception ex) {
            log.error("初始化基础数据失败", ex);
        }
    }

    @Override
    public void sendCacheConfigMQ() {
        initConfigCache();
        basicConfigProvider.sendBusConfig(cacheConfigMap);
    }

    public String getPlayProcess(String sportId, String playId) {
        String result = allPlayMap.get(sportId + "_" + playId);
        if (StringUtils.isBlank(result)) {
            String str = limitMapper.queryPlayInfoById(Integer.valueOf(sportId), Integer.valueOf(playId));
            if (StringUtils.isBlank(str)) {
                log.info("玩法不存在,allPlayMap的bean={},查询数据库返回:{},sport={},playId={}", JSONObject.toJSONString(allPlayMap), str, sportId, playId);
                throw new RcsServiceException(650, "玩法不存在");
            }
            allPlayMap.put(sportId + "_" + playId, str);
            return str;
        }
        return result;
    }


    private <T> List<T> getCacheConfig(String key, Class<T> clazz) {
        List<T> result = (List<T>) cacheConfigMap.get(clazz.getName());
        return result;
    }

    /**
     * @param <T>
     * @param key
     * @param clazz
     * @return
     */
    public <T> List<T> getConfigCache(String key, Class<T> clazz) {
        if (key == null) return null;
        List<T> result = getCacheConfig(key, clazz);
        if (result != null) return result;
        String data = redisClient.get(String.format("%s%s%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, key));
        List<T> infoList = new ArrayList<T>();
        if (data == null) {
            List<T> list = null;
            if (key.equals(BUS_DAY_CONFIG_KEY)) {
                list = (List<T>) rcsBusinessConfigMapper.queryBusDayConifgList();
            } else if (key.equals(BUS_MATCH_CONFIG_KEY)) {
                list = (List<T>) rcsBusinessConfigMapper.queryBusMatchConifgList();
            } else if (key.equals(BUS_PLAY_CONFIG_KEY)) {
                list = (List<T>) rcsBusinessConfigMapper.queryBusPlayConifgList();
            } else if (key.equals(BUS_USER_CONFIG_KEY)) {
                list = (List<T>) rcsBusinessConfigMapper.queryBusUserConifgList();
            } else if (key.equals(BUS_SINGLE_BET_CONFIG_KEY)) {
                list = (List<T>) rcsBusinessConfigMapper.queryBusSingleBetConfigList();
            } else if (key.equals(BUS_CON_PLAY_CONFIG_KEY)) {
                list = (List<T>) rcsBusinessConfigMapper.queryBusConPlayConifgList();
            } else if (key.equals(BUS_ORDER_ACCEPT_CONFIG_KEY)) {
                list = (List<T>) rcsMatchOrderAcceptEventConfigMapper.queryOrderConfigList();
            }

            redisClient.setExpiry(String.format("%s%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, key), list, 1000 * 60 * 10L);

            infoList = JSONObject.parseArray(data, clazz);
        } else {
            infoList = JSONObject.parseArray(data, clazz);
        }

        return infoList;
    }

    private void checkParamsNull(String... args) {
        for (String arg : args) {
            if (StringUtils.isBlank(arg)) throw new RcsServiceException("参数错误，不能为空:" + arg);
        }
    }

    public RcsBusinessDayPaidConfig getDayPaidConfig(String busId) {
        checkParamsNull(busId);
        List<RcsBusinessDayPaidConfig> list = getConfigCache(BUS_DAY_CONFIG_KEY, RcsBusinessDayPaidConfig.class);
        for (RcsBusinessDayPaidConfig info : list) {
            if (busId.equals(String.valueOf(info.getBusinessId()))) return info;
        }
        return null;
    }

    public RcsBusinessConPlayConfig getConPlayConfig(String busId, String type) {
        checkParamsNull(busId, type);
        List<RcsBusinessConPlayConfig> list = getConfigCache(BUS_CON_PLAY_CONFIG_KEY, RcsBusinessConPlayConfig.class);
        for (RcsBusinessConPlayConfig info : list) {
            if (busId.equals(String.valueOf(info.getBusinessId()))
                    && type.equals(String.valueOf(info.getPlayType()))) return info;
        }
        return null;
    }

    public RcsBusinessSingleBetConfig getSingleBetConfig(String busId, String sportId, String matchType, String timePeriod, String playId, String tournamentLevel) {
        checkParamsNull(busId, sportId, matchType, timePeriod, playId, tournamentLevel);
        List<RcsBusinessSingleBetConfig> list = getConfigCache(BUS_SINGLE_BET_CONFIG_KEY, RcsBusinessSingleBetConfig.class);
        RcsBusinessSingleBetConfig otherConfig = null;
        for (RcsBusinessSingleBetConfig info : list) {
            if (busId.equals(String.valueOf(info.getBusinessId())) &&
                    sportId.equals(String.valueOf(info.getSportId())) &&
                    matchType.equals(String.valueOf(info.getMatchType())) &&
                    timePeriod.equals(String.valueOf(info.getTimePeriod())) &&
                    tournamentLevel.equals(String.valueOf(info.getTournamentLevel())) &&
                    "-1".equals(String.valueOf(info.getPlayId())))
                otherConfig = info;

            if (busId.equals(String.valueOf(info.getBusinessId())) &&
                    sportId.equals(String.valueOf(info.getSportId())) &&
                    matchType.equals(String.valueOf(info.getMatchType())) &&
                    timePeriod.equals(String.valueOf(info.getTimePeriod())) &&
                    tournamentLevel.equals(String.valueOf(info.getTournamentLevel())) &&
                    playId.equals(String.valueOf(info.getPlayId()))) return info;
        }
        return otherConfig;
    }

    public RcsBusinessMatchPaidConfig getMatchPaidConfig(String busId, String sportId, String tournamentLevel) {
        checkParamsNull(busId, sportId, tournamentLevel);
        List<RcsBusinessMatchPaidConfig> list = getConfigCache(BUS_MATCH_CONFIG_KEY, RcsBusinessMatchPaidConfig.class);
        for (RcsBusinessMatchPaidConfig info : list) {
            if (busId.equals(String.valueOf(info.getBusinessId())) &&
                    sportId.equals(String.valueOf(info.getSportId())) &&
                    tournamentLevel.equals(String.valueOf(info.getTournamentLevel()))) return info;
        }
        return null;
    }

    /*
     * match_type 投注阶段  未开赛 和 滚球
     * playType 玩法类型   全场 上半场  下半场
     * play_id  玩法id  -1表示其他玩法
     */
    public RcsBusinessPlayPaidConfig getPlayPaidConfig(String busId, String sportId, String matchType, String playType, String playId) {
        checkParamsNull(busId, sportId, matchType, playType, playId);
        List<RcsBusinessPlayPaidConfig> list = getConfigCache(BUS_PLAY_CONFIG_KEY, RcsBusinessPlayPaidConfig.class);

        RcsBusinessPlayPaidConfig otherConfig = null;
        for (RcsBusinessPlayPaidConfig info : list) {
            if (busId.equals(String.valueOf(info.getBusinessId())) &&
                    sportId.equals(String.valueOf(info.getSportId())) &&
                    matchType.equals(String.valueOf(info.getMatchType())) &&
                    playType.equals(String.valueOf(info.getPlayType())) &&
                    "-1".equals(String.valueOf(info.getPlayId())))
                otherConfig = info;

            if (busId.equals(String.valueOf(info.getBusinessId())) &&
                    sportId.equals(String.valueOf(info.getSportId())) &&
                    matchType.equals(String.valueOf(info.getMatchType())) &&
                    playType.equals(String.valueOf(info.getPlayType())) &&
                    playId.equals(String.valueOf(info.getPlayId()))) return info;
        }
        return otherConfig;
    }

    public RcsBusinessUserPaidConfig getUserPaidConfig(String busId, String userId) {
        checkParamsNull(busId, userId);
        List<RcsBusinessUserPaidConfig> list = getConfigCache(BUS_USER_CONFIG_KEY, RcsBusinessUserPaidConfig.class);
        RcsBusinessUserPaidConfig otherConfig = null;
        for (RcsBusinessUserPaidConfig info : list) {
            if (busId.equals(String.valueOf(info.getBusinessId())) &&
                    "-1".equals(String.valueOf(info.getUserId())))
                otherConfig = info;

            if (busId.equals(String.valueOf(info.getBusinessId())) &&
                    userId.equals(String.valueOf(info.getUserId()))) return info;
        }
        return otherConfig;
    }
}
