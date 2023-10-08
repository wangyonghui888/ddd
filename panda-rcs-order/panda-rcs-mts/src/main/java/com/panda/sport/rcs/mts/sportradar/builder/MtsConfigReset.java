package com.panda.sport.rcs.mts.sportradar.builder;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
* @ClassName: MtsConfigReset 
* @Description: TODO
* @author black  
* @date 2020年11月4日 下午3:27:06 
 */
public class MtsConfigReset {
	
	private static final Logger log = LoggerFactory.getLogger(MtsConfigReset.class);
	
    /**
     * 重置mts http配置
    * @Title: reConfigMts 
    * @Description: TODO 
    * @return void    返回类型
    * @throws
     */
    public static void reConfigMts() {
		try {
			CloseableHttpClient mtsCloseableHttpClient = RcsMtsSdkApi.getInstance(CloseableHttpClient.class);
			if(mtsCloseableHttpClient instanceof Configurable) {
				Configurable conf = (Configurable) mtsCloseableHttpClient;
				RequestConfig requestConfig = conf.getConfig();
				Field connectTimeout = requestConfig.getClass().getDeclaredField("connectTimeout");
				connectTimeout.setAccessible(true);
				connectTimeout.setInt(requestConfig, 2000);

				Field socketTimeout = requestConfig.getClass().getDeclaredField("socketTimeout");
				socketTimeout.setAccessible(true);
				socketTimeout.setInt(requestConfig, 3000);

				log.info("MTS请求配置使用：{}",JSONObject.toJSONString(conf.getConfig()));
			}
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
    }

}
