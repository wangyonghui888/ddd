package com.panda.sport.rcs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.vo.api.request.QueryBetForMarketReqVo;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictForecast;

import java.util.List;

/**
 * <p>
 * 预测forecast表 服务类
 * </p>
 *
 * @author author
 * @since 2021-02-19
 */
public interface IRcsPredictForecastService extends IService<RcsPredictForecast> {

    List<RcsPredictForecast> selectList(QueryBetForMarketReqVo vo);

    List<RcsPredictForecast> pendingSelectList(QueryBetForMarketReqVo vo);
}
