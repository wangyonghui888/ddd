package com.panda.sport.rcs.mapper.predict.pending;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.vo.api.request.QueryBetForMarketReqVo;
import com.panda.sport.rcs.pojo.vo.predict.pending.RcsPredictPendingForecast;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RcsPredictPendingForecastMapper extends BaseMapper<RcsPredictPendingForecast> {
    List<RcsPredictPendingForecast> selectRcsPredictPendingForecastList(@Param("vo") QueryBetForMarketReqVo vo);
}
