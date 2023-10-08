package com.panda.sport.rcs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.vo.api.request.QueryForecastPlayReqVo;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictForecastPlay;

import java.util.List;

/**
 * <p>
 * 足球玩法forecast 服务类
 * </p>
 *
 * @author author
 * @since 2021-02-19
 */
public interface IRcsPredictForecastPlayService extends IService<RcsPredictForecastPlay> {

    List<RcsPredictForecastPlay> selectList(QueryForecastPlayReqVo vo);

}
