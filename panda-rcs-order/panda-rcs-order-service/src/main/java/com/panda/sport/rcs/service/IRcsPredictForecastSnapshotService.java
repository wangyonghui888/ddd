package com.panda.sport.rcs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.vo.api.request.QueryForecastPlayReqVo;
import com.panda.sport.rcs.pojo.vo.api.response.ForecastSnapshotResVo;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictForecastSnapshot;

import java.util.List;

/**
 * forecast 快照
 *
 * @author joey
 * @since 2022-07-26
 */
public interface IRcsPredictForecastSnapshotService extends IService<RcsPredictForecastSnapshot> {

    List<ForecastSnapshotResVo> querySnapshot(QueryForecastPlayReqVo vo);
}
