package com.panda.sport.rcs.mapper.predict;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsPredictBetStatis;
import com.panda.sport.rcs.pojo.RcsPredictForecast;

/**
 * <p>
 * 预测forecast表 Mapper 接口
 * </p>
 *
 * @author lithan auto
 * @since 2020-07-18
 */
public interface RcsPredictForecastMapper extends BaseMapper<RcsPredictForecast> {
    /**
     * 保存或者更新
     * @param entity
     */
    void saveOrUpdate(RcsPredictForecast entity);
}
