package com.panda.sport.rcs.mgr.wrapper;

/**
 * <p>
 * 商户单日最大赔付 服务类
 * </p>
 *
 * @author admin
 * @since 2019-10-04
 */
public interface RcsPaidConfigService{

	void initConfigCache();

	void sendCacheConfigMQ();
}
