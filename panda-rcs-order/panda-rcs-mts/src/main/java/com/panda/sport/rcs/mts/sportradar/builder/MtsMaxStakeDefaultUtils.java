package com.panda.sport.rcs.mts.sportradar.builder;

import org.apache.commons.lang3.StringUtils;

import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.utils.SpringContextUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Mts获取最大最小值失败的时候，默认值的获取
* @ClassName: MtsMaxStackDefaultUtils 
* @Description: TODO
* @author black  
* @date 2020年11月5日 下午5:27:05 
*
 */
@Slf4j
public class MtsMaxStakeDefaultUtils {
	
	private static String MTS_MAX_STAKE_DEFAULT = "rcs:mts:max:stake:default";
	
	public static Long getMaxStakeDefault() {
		try {
			RedisClient redisClient = SpringContextUtils.getBean("redisClient");
			
			String val = redisClient.get(MTS_MAX_STAKE_DEFAULT);
			if(StringUtils.isBlank(val)) {
				return 2000L;
			}
			
			return Long.parseLong(val);
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return 2000L;
	}

}
