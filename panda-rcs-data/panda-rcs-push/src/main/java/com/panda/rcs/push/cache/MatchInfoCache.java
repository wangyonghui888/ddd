package com.panda.rcs.push.cache;

import com.panda.rcs.push.entity.vo.MatchInfo;
import com.panda.rcs.push.entity.vo.MatchPlayCacheVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class MatchInfoCache {

    /**
     * 数据存储过期时间
     */
    private final static Long EXPIRED_TIME = 3 * 60 * 60 * 1000L;

    /**
     * 赛事信息缓存
     */
    public static Map<String, MatchInfo> matchInfoMap = new ConcurrentHashMap<>(1024);

    /**
     * 赛事玩法数据缓存
     */
    public static Map<String, MatchPlayCacheVo> matchPlayMap = new ConcurrentHashMap<>(1024);

    /**
     * 创建时间超过3个小时，自动清理
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void checkMatchInfoData(){
        Long currTime = System.currentTimeMillis();

        if(matchInfoMap.size() > 0){
            log.info("::赛事信息缓存清理::当前集合总数据={}", matchInfoMap.size());
            matchInfoMap.forEach((k, v) -> {
                if(currTime - v.getCreateTime() > EXPIRED_TIME){
                    matchInfoMap.remove(k);
                }
            });
        }
    }


    /**
     * 创建时间超过3个小时，自动清理
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void checkMatchPlayData(){
        Long currTime = System.currentTimeMillis();

        if(matchPlayMap.size() > 0){
            log.info("::赛事玩法缓存清理::当前集合总数据={}", matchPlayMap.size());
            matchPlayMap.forEach((k, v) -> {
                if(currTime - v.getCreateTime() > EXPIRED_TIME){
                    matchPlayMap.remove(k);
                }
            });
        }
    }
}
