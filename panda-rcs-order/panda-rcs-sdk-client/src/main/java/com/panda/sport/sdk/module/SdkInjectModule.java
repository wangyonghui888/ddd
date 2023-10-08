package com.panda.sport.sdk.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Named;
import com.panda.sport.data.rcs.api.MtsApiService;
import com.panda.sport.data.rcs.api.OrderLimitNewVersionApi;
import com.panda.sport.data.rcs.api.PIMtsApiService;
import com.panda.sport.data.rcs.api.TemplateAcceptConfigServer;
import com.panda.sport.data.rcs.api.config.ConfigApiService;
import com.panda.sport.data.rcs.api.credit.CreditLimitApiService;
import com.panda.sport.data.rcs.api.limit.LimitApiService;
import com.panda.sport.data.rcs.api.third.ThirdApiService;
import com.panda.sport.data.rcs.api.third.OddinApiService;
import com.panda.sport.data.rcs.api.tournament.TournamentTemplateByMatchService;
import com.panda.sport.data.rcs.api.virtual.VirtualApiService;
import com.panda.sport.sdk.annotation.handle.AutoInsertHandle;
import com.panda.sport.sdk.bean.*;
import com.panda.sport.sdk.scan.ClasspathPackageScanner;
import com.panda.sport.sdk.scan.IocHandle;
import com.panda.sport.sdk.service.impl.CategoryService;
import com.panda.sport.sdk.util.GuiceContext;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.context.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SdkInjectModule extends AbstractModule {

    private static final Logger logger = LoggerFactory.getLogger(SdkInjectModule.class);
    
    private org.aopalliance.intercept.MethodInterceptor interceptor = null;
    
    public SdkInjectModule() {}
    
    public SdkInjectModule(org.aopalliance.intercept.MethodInterceptor interceptor) {
    	this.interceptor = interceptor;
    }

    @Singleton
    @Provides
    public IocHandle provideIocHandle() {
        return new IocHandle();
    }

    @Singleton
    @Provides
    public ClasspathPackageScanner provideClasspathPackageScanner() {
        return new ClasspathPackageScanner("com.panda.sport.sdk");
    }
    
    @Override
	protected void configure() {
		super.configure();
		if(interceptor != null) {
			this.binder().bindInterceptor(Matchers.any(), Matchers.any(), this.interceptor);
		}
	}

	@Singleton
    @Provides
    public ApplicationConfig provideDubboApplicationConfig() {
    	if(ConfigManager.getInstance().getApplication().isPresent()) {
            ApplicationConfig applicationConfig =  ConfigManager.getInstance().getApplication().get();
    		return applicationConfig;
    	}
    	DubboApplicationConfig app = new DubboApplicationConfig();
        app.init();
    	return app;
    }

    @Singleton
    @Provides
    public DubboRegistryConfig provideDubboRegistryConfig() {
        return new DubboRegistryConfig();
    }


    @Singleton
    @Provides
    public DubboProtocolConfig provideDubboProtocolConfig() {
        return new DubboProtocolConfig();
    }
    
    @Singleton
    @Provides
    public DubboRestProtocolConfig provideRestProtocolConfig() {
        return new DubboRestProtocolConfig();
    }


    @Named("reference")
    @Provides
    public DubboReferenceConfig provideDubboReferenceConfig() {
        DubboReferenceConfig config = new DubboReferenceConfig();
        config.setApplication(GuiceContext.getInstance(ApplicationConfig.class));
        config.setRegistry(GuiceContext.getInstance(DubboRegistryConfig.class));
        return config;
    }

    @Named("iocReference")
    @Provides
    public DubboReferenceConfig provideIocDubboReferenceConfig(@Named("reference") DubboReferenceConfig config) {
        IocHandle handle = GuiceContext.getInstance(IocHandle.class);
        handle.ioc(DubboReferenceConfig.class, GuiceContext.getInstance(AutoInsertHandle.class), config);
        return config;
    }

    @Singleton
    @Provides
    public MtsApiService provideMtsApiService() {
        return getBasicReference(MtsApiService.class,2500).get();
    }

    @Singleton
    @Provides
    public PIMtsApiService providePiMtsApiService() {
        return getBasicReference(PIMtsApiService.class,2500).get();
    }

    @Singleton
    @Provides
    public LimitApiService provideLimitApiService() {
        return getBasicReference(LimitApiService.class, 3000).get();
    }

    @Singleton
    @Provides
    public ConfigApiService provideConfigApiServiceervice() {
        return getBasicReference(ConfigApiService.class, 3000).get();
    }

    @Singleton
    @Provides
    public CreditLimitApiService provideCreditLimitApiService() {
        return getBasicReference(CreditLimitApiService.class, 3000).get();
    }

    @Singleton
    @Provides
    public CategoryService provideCategoryList() {
        CategoryService categoryService = new CategoryService();
        return categoryService;
    }

    private <T> ReferenceConfig<T> getBasicReference(Class<T> t, int timeout) {
        return getBasicReference(t, timeout, 0);
    }

    private <T> ReferenceConfig<T> getBasicReference(Class<T> t, int timeout, int retries) {
        ReferenceConfig<T> reference = new ReferenceConfig<>();
        reference.setApplication(GuiceContext.getInstance(ApplicationConfig.class));
        reference.setRegistry(GuiceContext.getInstance(DubboRegistryConfig.class));
        reference.setProtocol("dubbo");
        reference.setInterface(t);
        reference.setCheck(false);
        reference.setTimeout(timeout);
        reference.setRetries(retries);
        return reference;
    }

    @Singleton
    @Provides
    public OrderLimitNewVersionApi orderLimitNewVersionApiImpl() {
        ReferenceConfig<OrderLimitNewVersionApi>  reference = new ReferenceConfig<>();
        reference.setApplication(GuiceContext.getInstance(ApplicationConfig.class));
        reference.setRegistry(GuiceContext.getInstance(DubboRegistryConfig.class));
        reference.setProtocol("dubbo");
        reference.setInterface(OrderLimitNewVersionApi.class);
        reference.setCheck(false);
        reference.setTimeout(3000);
        reference.setRetries(0);
        return (OrderLimitNewVersionApi) reference.get();
    }
    @Singleton
    @Provides
    public TemplateAcceptConfigServer provideTemplateAcceptConfigServer() {
        ReferenceConfig<TemplateAcceptConfigServer>  reference = new ReferenceConfig<>();
        reference.setApplication(GuiceContext.getInstance(ApplicationConfig.class));
        reference.setRegistry(GuiceContext.getInstance(DubboRegistryConfig.class));
        reference.setProtocol("dubbo");
        reference.setInterface(TemplateAcceptConfigServer.class);
        reference.setCheck(false);
        reference.setTimeout(3000);
        reference.setRetries(0);
        return (TemplateAcceptConfigServer) reference.get();
    }

    @Singleton
    @Provides
    public TournamentTemplateByMatchService provideTournamentTemplateByMatchService() {
        return getBasicReference(TournamentTemplateByMatchService.class, 3000).get();
    }

    @Singleton
    @Provides
    public OddinApiService provideOddinApiService() {
        return getBasicReference(OddinApiService.class, 30000).get();
    }

    @Singleton
    @Provides
    public ThirdApiService provideThirdApiService() {
        return getBasicReference(ThirdApiService.class, 3000).get();
    }

    @Singleton
    @Provides
    public VirtualApiService virtualApiService() {
        return getBasicReference(VirtualApiService.class, 3000).get();
    }

}

