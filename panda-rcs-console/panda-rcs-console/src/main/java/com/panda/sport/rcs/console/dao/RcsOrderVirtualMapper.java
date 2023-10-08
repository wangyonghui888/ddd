package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.dto.OrderDTO;
import com.panda.sport.rcs.console.pojo.Order;
import com.panda.sport.rcs.console.pojo.RcsOrderVirtual;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

/**
 * <p>
 * 第三方 虚拟赛事 订单表 Mapper 接口
 * </p>
 *
 * @author lithan
 * @since 2020-12-26
 */
@Component
public interface RcsOrderVirtualMapper extends BaseMapper<RcsOrderVirtual> {

    List<Order> getVirtualOrderList(OrderDTO orderDTO);
}
