package com.panda.sport.rcs.wrapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.panda.sport.rcs.pojo.TOrder;
import com.panda.sport.rcs.pojo.TOrderDetail;
import com.panda.sport.rcs.pojo.TOrderDetailExt;
import com.panda.sport.rcs.pojo.TOrderDetailExtDO;


/**
 * 接拒单服务
 *
 * @author carver
 */
public interface OrderAcceptRejectService {

    /**
     * 滚球接拒单，发送MQ
     *
     * @param ids
     * @param state
     */
    void sendMessage(List<String> ids, int state, Map<String, String> map);

    /**
     * 滚球接拒单，发送MQ
     *
     * @param ids
     * @param state
     */
    @Deprecated
    void sendMessage(List<TOrder> list, List<TOrderDetailExt> tOrderDetailExtList);

    /**
     * 新逻辑
     * @param tOrderDetailExts ext订单详情信息
     */
    void sendMessageNew(List<TOrderDetailExt>  tOrderDetailExts,List<String> acceptOrderList,List<String> rejectOrderList);

    /**
     * 滚球接拒单，修改ext扩展表信息
     *
     * @param rejectOrders
     * @param acceptOrders
     */
    Integer updateOrderDetailExtStatus(Set<String> rejectOrders, Set<Long> acceptOrders);

    /**
     * @return java.lang.Integer
     * @Description //更新订单状态
     * @Param [tOrderDetailExt]
     * @Author sean
     * @Date 2020/11/7
     **/
    @Deprecated
    Integer updateOrderDetailExtStatusByOrderNo(TOrderDetailExt tOrderDetailExt);

    Integer updateOrderDetailExtStatusByOrderNoList(Integer state,List<String> orderList);

    Integer updateOrderDetailExtStatusByOrderNoMongo(TOrderDetailExtDO tOrderDetailExt);

}
