package com.panda.sport.rcs.console.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.panda.sport.rcs.console.dao.OrderMapper;
import com.panda.sport.rcs.console.dto.OrderDTO;
import com.panda.sport.rcs.console.pojo.Order;
import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.console.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Override
    public PageDataResult getOrderList(OrderDTO orderDTO, Integer pageNum, Integer pageSize) {
        PageDataResult pageDataResult = new PageDataResult();
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.getOrderList(orderDTO);
        if (orderList.size() > 0) {
            PageInfo<Order> pageInfo = new PageInfo<>(orderList);
            pageDataResult.setList(orderList);
            pageDataResult.setTotals((int) pageInfo.getTotal());
        }
        return pageDataResult;
    }
}
