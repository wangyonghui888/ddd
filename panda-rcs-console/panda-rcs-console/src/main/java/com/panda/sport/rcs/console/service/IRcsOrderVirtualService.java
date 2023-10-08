package com.panda.sport.rcs.console.service;

import com.panda.sport.rcs.console.dto.OrderDTO;
import com.panda.sport.rcs.console.response.PageDataResult;

/**
 * <p>
 * 第三方 虚拟赛事 订单表 服务类
 * </p>
 *
 * @author lithan
 * @since 2020-12-26
 */
public interface IRcsOrderVirtualService {

    PageDataResult getOrderList(OrderDTO orderDTO, Integer pageNum, Integer pageSize);
}
