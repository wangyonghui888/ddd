package com.panda.sport.rcs.console.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.panda.sport.rcs.console.dao.RcsOrderVirtualMapper;
import com.panda.sport.rcs.console.dto.OrderDTO;
import com.panda.sport.rcs.console.pojo.Order;
import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.console.service.IRcsOrderVirtualService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 第三方 虚拟赛事 订单表 服务实现类
 * </p>
 *
 * @author lithan
 * @since 2020-12-26
 */
@Service
public class RcsOrderVirtualServiceImpl implements IRcsOrderVirtualService {

    @Resource
    private RcsOrderVirtualMapper rcsOrderVirtualMapper;

    @Override
    public PageDataResult getOrderList(OrderDTO orderDTO, Integer pageNum, Integer pageSize) {
        PageDataResult pageDataResult = new PageDataResult();
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = rcsOrderVirtualMapper.getVirtualOrderList(orderDTO);
        if (orderList.size() > 0) {
            PageInfo<Order> pageInfo = new PageInfo<>(orderList);
            pageDataResult.setList(orderList);
            pageDataResult.setTotals((int) pageInfo.getTotal());
        }
        return pageDataResult;
    }
}
