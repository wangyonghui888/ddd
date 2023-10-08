package com.panda.sport.rcs.task.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.panda.sport.rcs.cache.RcsCacheUtils;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import javax.validation.constraints.NotNull;
import java.util.function.Function;

/**
 * @Describtion redis工具类
 * @Auther v
 * @Date 2022-02-2022/2/4 17:22
 */
@Component
@Slf4j
public class RedisUtil {


    public static final Long TWELVE_MINUTES_SECOND=12*60L;
    public static final Long TWO_HOURS=2*60*60L;
    public static final Long FIVE_MINUTES_SECOND=5*60L;
    public static final String REDIS_PRE_KEY= "rcs:task:redisUtilsCache:%s";

    public static Cache<String, String> pandaRDSCache = RcsCacheUtils.newSyncSimpleCache(1000,200000,5*60);

    public static Cache<String, String> pandaRDSLongTimeCache = RcsCacheUtils.newSyncSimpleCache(1000, 200000, 2*60*60);
    //防止null or null 对库暴读 计数器
    public static Cache<String, Integer> nullJudgeCache = RcsCacheUtils.newSyncSimpleCache(100, 20000, 10);


    @Autowired
    private RedisClient redisClient;

    public RedisClient getRedisClient() {
        return redisClient;
    }

    /**
     *
     * @param key 不同类型功能 key 请 mark+":"+key
     * @param function
     * @return
     */
    public String get(@NotNull String key, @NotNull Function<String,String> function){
        String redisKey=String.format(REDIS_PRE_KEY,key.toString());
        log.info(redisKey+":查缓存");
        String value=pandaRDSCache.get(redisKey, new Function<String, String>() {
            @Override
            public String apply(String s) {
                log.info(redisKey+":查redis缓存");
                //============防止null or null 对库暴读
                Integer nullMarkCount = nullJudgeCache.getIfPresent(redisKey);
                if(null!=nullMarkCount&&nullMarkCount>3){
                    log.info(redisKey+":上次10秒内多次redis缓存为null");
                    return "";
                }
                //============防止null or null 对库暴读====end
                return redisClient.get(redisKey);
            }
        });
        //--------------------------------testCode
        String ifPresent = pandaRDSCache.getIfPresent(redisKey);
        log.info(redisKey+"再验本地缓存:"+(CommonUtil.isBlankOrNull(ifPresent)?"空":"有值"));
        //--------------------------------testCode
        if(CommonUtil.isBlankOrNull(value)){
            //============防止null or null 对库暴读
            log.info(redisKey+":redis缓存为null");
            Integer nullMarkCount = nullJudgeCache.getIfPresent(redisKey);
            if(null!=nullMarkCount){
                nullJudgeCache.put(redisKey,1+nullMarkCount);
                log.info(redisKey+":上次10秒内缓存为null");
                if(nullMarkCount>6){
                    log.info(redisKey+":上次10秒内缓多次存为null,缓存大于6次为空,暂时停止查源");
                    return "";
                }
            }else {
                nullJudgeCache.put(redisKey,1);
            }
            //============防止null or null 对库暴读====end
            value=function.apply(key);
            if (CommonUtil.isBlankOrNull(value)){
                log.info(redisKey+":缓存源为null");
                return "";
            }
            pandaRDSCache.put(redisKey,value);
            redisClient.setExpiry(redisKey,value,TWELVE_MINUTES_SECOND);
            if(CommonUtil.isBlankOrNull(value)){
                return "";
            }
            return value;
        }else {
            return value;
        }
    }




    /**
     *
     * @param key 不同类型功能 key 请 mark+":"+key
     * @param function
     * @return
     */
    public String getLongTimeCache(@NotNull String key, @NotNull Function<String,String> function){
        String redisKey=String.format(REDIS_PRE_KEY,key.toString());
        log.info(redisKey+":查缓存");
        String value=pandaRDSLongTimeCache.get(redisKey, new Function<String, String>() {
            @Override
            public String apply(String s) {
                log.info(redisKey+":查redis缓存");
                //============防止null or null 对库暴读
                Integer nullMarkCount = nullJudgeCache.getIfPresent(redisKey);
                if(null!=nullMarkCount&&nullMarkCount>3){
                    log.info(redisKey+":上次10秒内多次redis缓存为null");
                    return "";
                }
                //============防止null or null 对库暴读====end
                return redisClient.get(redisKey);
            }
        });
        //--------------------------------testCode
        String ifPresent = pandaRDSLongTimeCache.getIfPresent(redisKey);
        log.info(redisKey+"再验本地缓存:"+(CommonUtil.isBlankOrNull(ifPresent)?"空":"有值"));
        //--------------------------------testCode
        if(CommonUtil.isBlankOrNull(value)){
            //============防止null or null 对库暴读
            log.info(redisKey+":redis缓存为null");
            Integer nullMarkCount = nullJudgeCache.getIfPresent(redisKey);
            if(null!=nullMarkCount){
                nullJudgeCache.put(redisKey,1+nullMarkCount);
                log.info(redisKey+":上次10秒内缓存为null");
                if(nullMarkCount>6){
                    log.info(redisKey+":上次10秒内缓多次存为null,缓存大于6次为空,暂时停止查源");
                    return "";
                }
            }else {
                nullJudgeCache.put(redisKey,1);
            }
            //============防止null or null 对库暴读====end
            value=function.apply(key);
            if (CommonUtil.isBlankOrNull(value)){
                log.info(redisKey+":缓存源为null");
            }
            pandaRDSLongTimeCache.put(redisKey,value);
            redisClient.setExpiry(redisKey,value,TWO_HOURS);
            if(CommonUtil.isBlankOrNull(value)){
                return "";
            }
            return value;
        }else {
            return value;
        }
    }


}
