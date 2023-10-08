package com.panda.rcs.stray.limit.service;


import com.panda.rcs.stray.limit.entity.vo.RcsMerchantHighRiskRespVo;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantLowLimit;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantSingleLimit;

import java.util.List;

public interface IRcsMerchantHighRiskLimitWebService {


    RcsMerchantHighRiskRespVo queryData(Integer sportId);

    void updateData(RcsMerchantHighRiskRespVo rcsMerchantHighRiskRespVo);

    List<RcsMerchantLowLimit> queryByLowData();

    void updateByLowData(List<RcsMerchantLowLimit> rcsMerchantLowLimitList);


    List<RcsMerchantSingleLimit> queryBySingleLimitData(Integer sportId);

    void updateBySingleLimitData(List<RcsMerchantSingleLimit> rcsMerchantSingleLimits);

    void insertBusinessLimitLog(String paramName, String operateType, String beforeVal, String afterVal);

    void insertBusinessLimitLogIP(String paramName, String operateType, String beforeVal, String afterVal,String ip);
}
