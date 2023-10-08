package com.panda.sport.rcs.mts.sportradar.builder;


import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.sportradar.mts.sdk.api.builders.BuilderFactory;
import com.sportradar.mts.sdk.api.exceptions.MtsPropertiesException;
import com.sportradar.mts.sdk.api.interfaces.MtsClientApi;
import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;
import com.sportradar.mts.sdk.api.interfaces.customBet.CustomBetManager;
import com.sportradar.mts.sdk.app.MtsSdk;
import com.sportradar.mts.sdk.impl.di.SdkInjectionModule;
import com.sportradar.mts.sdk.impl.libs.root.SdkRoot;

public class RcsMtsSdkApi extends MtsSdk{
	
	private static final Logger logger = LoggerFactory.getLogger(MtsSdk.class); 
	
	private static Injector injector;
	private SdkRoot sdkRoot; 
	private final Object stateLock; 
	private boolean opened; 
	private boolean closed;
	
	private final SdkConfiguration config; 
	  private BuilderFactory builderFactory; 
	  private MtsClientApi mtsClientApi; 
	  private CustomBetManager customBetManager;

	public RcsMtsSdkApi(SdkConfiguration config) {
		super(config);
		this.config = config;
	
		this.stateLock = getSubClassFieldVal(this, "stateLock");
	}
	
	
	private <T> T getSubClassFieldVal(Object obj , String fieldName) {
		try {
			Field field = MtsSdk.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			
			return (T) field.get(obj);
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
	
	private void setSubClassFieldVal(Object obj , String fieldName,Object val) {
		try {
			Field field = MtsSdk.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			
			field.set(obj,val);
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	  public void open() {
		  synchronized (this.stateLock) {
		      Preconditions.checkState(!this.opened, "MTS SDK is already open");
		      Preconditions.checkState(!this.closed, "MTS SDK cannot be reopened");
		      Preconditions.checkNotNull(this.config, "SdkConfiguration cannot be null");
		      logger.info("Starting the MTS SDK using provided configuration");
		      try {
		        openWithConfig();
		      } catch (Exception e) {
		        throw new MtsPropertiesException(e.getMessage(), e);
		      } 
		  } 
	}
	
	  
	private void openWithConfig() {
		Preconditions.checkNotNull(this.config, "configuration cannot be null");
	    logger.info("Opening the MTS SDK");
	    this.opened = true;
	    Injector injector = Guice.createInjector(new Module[] { new SdkInjectionModule(this.config) });
	    this.injector = injector;
	    
	    //MtsConfigReset.reConfigMts();
	    
	    this.builderFactory = (BuilderFactory)injector.getInstance(BuilderFactory.class);
	    this.mtsClientApi = (MtsClientApi)injector.getInstance(MtsClientApi.class);
	    this.customBetManager = (CustomBetManager)injector.getInstance(CustomBetManager.class);
	    this.sdkRoot = (SdkRoot)injector.getInstance(SdkRoot.class);
	    
	    logger.info("开始重置sdk bean对象 ,opened：{},builderFactory：{},mtsClientApi：{},customBetManager：{},sdkRoot：{}",
	    		this.opened,this.builderFactory,this.mtsClientApi,this.customBetManager,this.sdkRoot);
	    setSubClassFieldVal(this, "opened", this.opened);
	    setSubClassFieldVal(this, "builderFactory", this.builderFactory);
	    setSubClassFieldVal(this, "mtsClientApi", this.mtsClientApi);
	    setSubClassFieldVal(this, "customBetManager", this.customBetManager);
	    setSubClassFieldVal(this, "sdkRoot", this.sdkRoot);
	    
	    logger.info("重置完成sdk bean对象");
	    
	    this.sdkRoot.open();
	    logger.info("MTS SDK opened");
	  }
	
	public static <T> T getInstance(Class<T> clazz) {
		return (T)injector.getInstance(clazz);
	}

}
