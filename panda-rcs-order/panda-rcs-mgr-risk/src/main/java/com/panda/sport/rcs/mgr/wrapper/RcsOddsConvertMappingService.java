package com.panda.sport.rcs.mgr.wrapper;

import com.panda.sport.rcs.pojo.RcsOddsConvertMapping;
import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.vo.SportMarketCategoryVo;

import java.util.List;
import java.util.Map;

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
	String getNextLevelOdds(String displayOddsVal);
}
