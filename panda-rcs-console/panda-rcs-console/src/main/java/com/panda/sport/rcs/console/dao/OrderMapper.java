package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.dto.OrderDTO;
import com.panda.sport.rcs.console.pojo.Order;
import org.springframework.stereotype.Repository;
import tk.mapper.MyMapper;

import java.util.List;

@Repository
public interface OrderMapper extends MyMapper<Order> {

    List<Order> getOrderList(OrderDTO orderDTO);
}
