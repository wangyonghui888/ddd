package com.panda.rcs.stray.limit.service;

import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.OrderBean;

/**
 * @author :  koala
 * @Project Name :  panda-rcs-api
 * @Description :  订单新版限额查询接口
 * @Date: 2022-03-25 15:30
 */
public interface OrderLimitNewVersionApi {


    Response queryMaxBetAmountByOrder(Request<OrderBean> request);

//    Response saveOrderCheckAmount(OrderBean requestParam);
//
//    Response queryBusinessSwitch(String businessId);
}
