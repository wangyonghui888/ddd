package com.panda.sport.rcs.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.log.annotion.NotWriteLog;
import com.panda.sport.rcs.pojo.RcsOddsConvertMapping;

import java.math.BigDecimal;

/**
 * <p>
 * 赔率转换映射表 服务类
 * </p>
 *
 * @author admin
 * @since 2019-10-04
 */
public interface RcsOddsConvertMappingService extends IService<RcsOddsConvertMapping> {


    /**
     * 获取降级后的赔率
     * @param displayOddsVal
     * @return
     */
	@NotWriteLog
	String getNextLevelOdds(String displayOddsVal);

	/**
	 * @Description   //根据马来赔获取欧赔
	 * @Param [myOdds]
	 * @Author  Sean
	 * @Date  11:10 2020/10/17
	 * @return java.lang.String
	 **/
	String getEUOdds(String myOdds);

	/**
	 * 马赔转100000倍欧赔
	 *
	 * @param myOdds
	 * @return
	 */
	int myOddsToOddsValue(BigDecimal myOdds);
}
