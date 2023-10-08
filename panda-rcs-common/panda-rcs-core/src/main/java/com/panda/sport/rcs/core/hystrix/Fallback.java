package com.panda.sport.rcs.core.hystrix;


import org.apache.dubbo.common.extension.SPI;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.core.hystrix
 * @Description :  业务失败返回处理函数
 * @Date: 2019-10-10 15:54
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@SPI
public interface Fallback {
	/**
	 * invoke
	 * @return
	 */
	Object invoke();
}
