package com.panda.sport.rcs.mapper.predict;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.vo.api.request.QueryBetForMarketReqVo;
import com.panda.sport.rcs.pojo.vo.api.request.QueryForecastPlayReqVo;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictForecastPlay;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 足球玩法forecast Mapper 接口
 * </p>
 *
 * @author author
 * @since 2021-02-19
 */
public interface RcsPredictForecastPlayMapper extends BaseMapper<RcsPredictForecastPlay> {

    Integer saveOrUpdate(@Param("list") List<RcsPredictForecastPlay> list);

    List<RcsPredictForecastPlay> selectList(@Param("vo") QueryForecastPlayReqVo vo);
}
