package com.panda.rcs.stray.limit.service;

import com.panda.rcs.stray.limit.entity.vo.RcsMerchantSeriesRespVo;

public interface RcsMerchantSeriesService {

    RcsMerchantSeriesRespVo queryData();

    void updateData(RcsMerchantSeriesRespVo rcsMerchantSeriesRespVo);
}
