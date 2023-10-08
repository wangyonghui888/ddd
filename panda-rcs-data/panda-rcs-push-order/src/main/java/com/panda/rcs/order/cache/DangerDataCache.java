package com.panda.rcs.order.cache;

import com.panda.rcs.order.entity.vo.DangerVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 危险要素相关本地缓存类处理
 */
@Slf4j
@Component
public class DangerDataCache {

    /**
     * 数据存储过期时间
     */
    private final static Long EXPIRED_TIME = 3 * 60 * 60 * 1000L;

    /**
     * 危险Ip
     */
    public static Map<String, DangerVo> dangerIpMap = new ConcurrentHashMap<>(4096);

    /**
     * 危险fp(指纹池)
     */
    public static Map<String, DangerVo> dangerFpMap = new ConcurrentHashMap<>(4096);

    /**
     * 危险玩家组
     */
    public static Map<String, DangerVo> dangerUserGroupMap = new ConcurrentHashMap<>(4096);

    /**
     * 获取危险Ip
     * @param ip
     * @return
     */
    public static String getDangerIp(String ip){
        if(!dangerIpMap.containsKey(ip)){
            return null;
        }

        return dangerIpMap.get(ip).getLevel();
    }

    /**
     * 获取危险fp
     * @param fp
     * @return
     */
    public static String getDangerFp(String fp){
        if(!dangerFpMap.containsKey(fp)){
            return null;
        }

        return dangerFpMap.get(fp).getLevel();
    }

    /**
     * 获取危险玩家组
     * @param userId
     * @return
     */
    public static String getDangerUserGroupMap(String userId){
        if(!dangerUserGroupMap.containsKey(userId)){
            return null;
        }

        return dangerUserGroupMap.get(userId).getLevel();
    }

    /**
     * 危险数据检查，创建时间超过3个小时，自动清理
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void checkDangerData(){
        Long currTime = System.currentTimeMillis();

        if(dangerIpMap.size() > 0){
            log.info("::危险信息-Ip::当前集合总数据={}", dangerIpMap.size());
            dangerIpMap.forEach((k, v) -> {
                if(currTime - v.getCreateTime() > EXPIRED_TIME){
                    dangerIpMap.remove(k);
                }
            });
        }

        if(dangerFpMap.size() > 0){
            log.info("::危险信息-Fp(指纹)::当前集合总数据={}", dangerFpMap.size());
            dangerFpMap.forEach((k, v) -> {
                if(currTime - v.getCreateTime() > EXPIRED_TIME){
                    dangerFpMap.remove(k);
                }
            });
        }

        if(dangerUserGroupMap.size() > 0){
            log.info("::危险信息-玩家组::当前集合总数据={}", dangerUserGroupMap.size());
            dangerUserGroupMap.forEach((k, v) -> {
                if(currTime - v.getCreateTime() > EXPIRED_TIME){
                    dangerUserGroupMap.remove(k);
                }
            });
        }

    }

}
