package com.panda.sport.rcs.mts.sportradar.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.MtsgGetMaxStakeDTO;
import com.panda.sport.rcs.pojo.TOrderDetail;
//import com.sportradar.mts.sdk.api.AutoAcceptedOdds;

import java.util.List;

/**
 * mts参数转换设置
 */
public interface MtsCommonService {

    public void convertAllParam(List<ExtendBean> extendBeanList);

    public void convertSingleParam(ExtendBean extendBean);

//    public void updateMtsOrder(String ticketId, String status, String orderNo, List<AutoAcceptedOdds> autoAcceptedOddsList, String jsonValue,
//                               Integer reasonCode, String reasonMsg, Integer isCache);
}
