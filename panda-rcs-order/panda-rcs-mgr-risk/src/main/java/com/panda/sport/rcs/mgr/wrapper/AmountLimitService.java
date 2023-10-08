package com.panda.sport.rcs.mgr.wrapper;

import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;

/**
 * @author :  holly
 * @Project Name :rcs-parent
 * @Package Name :com.panda.sport.rcs.rpc.calculator.service
 * @Description : 操盘限额计算
 * @Date: 2019-10-22 20:17
 */
public interface AmountLimitService {
    /*
     * 下单计算当前盘口预期赔付最大值 || 平衡值
     * 判断玩法是三项盘还是两项盘，决定依赖哪个实现来调用
     */
    void sumCurrentLoadValue(OrderItem item, OrderBean orderBean);
}
