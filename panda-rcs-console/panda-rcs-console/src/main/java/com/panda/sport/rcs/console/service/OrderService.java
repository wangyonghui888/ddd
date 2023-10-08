package com.panda.sport.rcs.console.service;

import com.panda.sport.rcs.console.dto.OrderDTO;
import com.panda.sport.rcs.console.response.PageDataResult;

public interface OrderService {

    PageDataResult getOrderList(OrderDTO userSearch, Integer pageNum, Integer pageSize);
}
