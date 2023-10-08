package com.panda.sport.sdk.listeners;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.sdk.constant.RedisKeys;
import com.panda.sport.sdk.core.JedisClusterServer;
import com.panda.sport.sdk.service.impl.RcsPaidConfigServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.panda.sport.sdk.service.impl.RcsPaidConfigServiceImp.*;

/**
 * @author :  max
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.sdk.mq
 * @Description :  额度管理各维度配置MQ消息同步
 * @Date: 2020-01-13 12:23
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Singleton
public class BusConfigHandler {
    private static final Logger log = LoggerFactory.getLogger(BusConfigHandler.class);

    @Inject
    JedisClusterServer jedisClusterServer ;
    
    @Inject
    RcsPaidConfigServiceImp rcsPaidConfigServiceImp;

    /**
     * 设置基础配置到redis
     **/
    public Boolean handleBusConfigMsg(String topic,String tag,String key,String body) {
        //log.info("handleBusConfigMsg 收到bean topic:{}, tag:{}, body ：{}",topic,tag,body);
        try {
            handleCache(topic,tag,key,body);
            if(rcsPaidConfigServiceImp.getKeyTimeMapping().containsKey(tag)) {
            	//更新缓存更新时间,内存监控变更更新数据
                jedisClusterServer.hset(RedisKeys.MONITOR_PAID_CONFIG_REDIS_CACHE, tag, System.currentTimeMillis() + "");
                rcsPaidConfigServiceImp.initConfigByKey(tag);
            }
        }catch (Exception e) {
            log.error("设置基础配置redis异常",e);
        }
        return true;
    }


    private void handleCache(String topic,String tag,String key,String body){
        /*if(BUS_CON_PLAY_CONFIG_KEY.equals(tag)|| BUS_PLAY_CONFIG_KEY.equals(tag)||BUS_PLAY_CONFIG_KEY.equals(tag)||
                BUS_MATCH_CONFIG_KEY.equals(tag)||BUS_MATCH_CONFIG_KEY.equals(tag)||BUS_DAY_CONFIG_KEY.equals(tag)) {
            String redisKey = String.format("%s%s:%s", RedisKeys.PAID_CONFIG_REDIS_CACHE, tag, key);
            jedisClusterServer.set(redisKey, JSONObject.toJSONString(body));
        }*/
        if(BUS_CON_PLAY_CONFIG_KEY.equals(tag)){
            //BusConPlay
//            List<RcsBusinessConPlayConfig>  configs = JSONObject.parseObject(body,new TypeReference<List<RcsBusinessConPlayConfig>>(){});
//            rcsPaidConfigServiceImp.setConPlayConfig(key,configs);
        }else if(BUS_PLAY_CONFIG_KEY.equals(tag)){
            //BusPlay
//            List<RcsBusinessPlayPaidConfig> configs = JSONObject.parseObject(body,new TypeReference<List<RcsBusinessPlayPaidConfig>>(){});
//            rcsPaidConfigServiceImp.setPlayPaidConfig(key,configs);
        }else if(BUS_USER_CONFIG_KEY.equals(tag)){
            //BusUser
//            List<RcsBusinessUserPaidConfig> configs = JSONObject.parseObject(body,new TypeReference<List<RcsBusinessUserPaidConfig>>(){});
//            rcsPaidConfigServiceImp.setUserPaidConfig(key,configs);
        }else if(BUS_MATCH_CONFIG_KEY.equals(tag)){
            //BusMatch
//            List<RcsBusinessMatchPaidConfig> configs = JSONObject.parseObject(body,new TypeReference<List<RcsBusinessMatchPaidConfig>>(){});
//            rcsPaidConfigServiceImp.setMatchPaidConfig(key,configs);
        }else if(BUS_DAY_CONFIG_KEY.equals(tag)){
            //BusDay
//            RcsBusinessDayPaidConfig config = JSONObject.parseObject(body,RcsBusinessDayPaidConfig.class);
//            rcsPaidConfigServiceImp.setDayPaidConfig(key,config);
        }else if(BUS_SINGLE_BET_CONFIG_KEY.equals(tag)){
            //BusSingleBet
//            List<RcsBusinessSingleBetConfig> configs = JSONObject.parseObject(body,new TypeReference<List<RcsBusinessSingleBetConfig>>(){});
//            rcsPaidConfigServiceImp.setSingleBetConfig(key,configs);
        }
        else{
            String redisKey  = String.format("%s%s", RedisKeys.PAID_CONFIG_REDIS_CACHE,tag);
            jedisClusterServer.set(redisKey,body);
        }
    }
}
