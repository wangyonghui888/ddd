package com.panda.sport.sdk.core;


import com.google.inject.Guice;
import com.google.inject.Injector;
import com.panda.sport.data.rcs.api.MtsApiService;
import com.panda.sport.data.rcs.api.limit.LimitApiService;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.sdk.listeners.OrderStatusServer;
//import com.panda.sport.sdk.log.LoggerInterceptor;
import com.panda.sport.sdk.module.SdkInjectModule;
import com.panda.sport.sdk.mq.Producer;
import com.panda.sport.sdk.scan.IocHandle;
import com.panda.sport.sdk.service.impl.OrderPaidApiImpl;
import com.panda.sport.sdk.util.GuiceContext;
import com.panda.sport.sdk.util.PropertiesUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;

import java.util.Map;

public class Sdk {
    private static final Logger logger = LoggerFactory.getLogger(Sdk.class);

    private static Sdk sdk = null;
    
    private OrderPaidApiImpl api;

    public synchronized static Sdk init() {
    	return initProperties(null);
    }
    
    public synchronized static Sdk initProperties(Map<String, Map<String, String>> map) {
//    	return initProperties(map, new LoggerInterceptor());
        return initProperties(map, null);
    }
    
    
    public synchronized static Sdk initProperties(Map<String, Map<String, String>> map , MethodInterceptor interceptor) {
    	if(sdk != null ) {
            return sdk;
        }
    	sdk = new Sdk();
    	Injector injector = Guice.createInjector(new SdkInjectModule(interceptor));
		GuiceContext.setInjector(injector);
		if( map != null) {
			PropertiesUtil propertiesUtil = injector.getInstance(PropertiesUtil.class);
			for(String fileKey : map.keySet()) {
				for(String key : map.get(fileKey).keySet()) {
                    propertiesUtil.put(key, map.get(fileKey).get(key));
				}
			}
		}
		
		injector.getInstance(IocHandle.class);
        injector.getInstance(MtsApiService.class);
        injector.getInstance(LimitApiService.class);
		loadSdkConfig();
        logger.info("SDK start succexiss!");
        return sdk;
    }

    public OrderPaidApiImpl getOrderPaidApi(OrderStatusServer listener){
    	if(api != null ) return api;
    	if(listener == null ) {
    		throw new RuntimeException("SKD获取API，监听不能为空");
    	}
        OrderPaidApiImpl api =  GuiceContext.getInstance(OrderPaidApiImpl.class);
        /*Consumer consumer = GuiceContext.getInstance(Consumer.class);
        consumer.setListener(listener);*/
        this.api = api;
        return api;
    }

    /**
     * 通知加载SDK配置
     */
    public static void loadSdkConfig() {
        try {
            Producer producer =  GuiceContext.getInstance(Producer.class);
            producer.sendMsg(MqConstants.RCS_ORDER_SDK_CACHE, "{}");
        } catch (Exception e) {
            logger.error("加载SDK配置异常",e);
        }
    }

}
