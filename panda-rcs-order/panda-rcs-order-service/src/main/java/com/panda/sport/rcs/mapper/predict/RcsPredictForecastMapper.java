package com.panda.sport.rcs.mapper.predict;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.vo.api.request.QueryBetForMarketReqVo;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictForecast;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 预测forecast表 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2021-02-19
 */
public interface RcsPredictForecastMapper extends BaseMapper<RcsPredictForecast> {
    /**
     * 保存或者更新
     * @param list
     */
    void saveOrUpdate(@Param("list") List<RcsPredictForecast> list);

    List<RcsPredictForecast> selectList(@Param("vo") QueryBetForMarketReqVo vo);
}
