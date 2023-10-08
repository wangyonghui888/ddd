package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.pojo.TOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-03
 */
@Service
public interface TOrderMapper extends BaseMapper<TOrder> {
    /*
    批量修改订单状态
     */
    int updateOrderStatusBatch(TOrder orders);

    int insertAndUpdate(TOrder order);


    /**
     * @Description   查询注单条数
     * @Param [map]
     * @Author  Sean
     * @Date  17:20 2020/2/22
     * @return java.lang.Integer
     **/
    Integer queryOrderCountByPage(Map<String, Object> map);
    /**
     * @Description   添加拒单原因
     * @Param [ids]
     * @Author  Sean
     * @Date  22:44 2020/2/20
     * @return void
     **/
    void denialOrderDetailByIds(@Param("ids") List<String> ids,@Param("denialReason") String denialReason);

    /**
     * 查找数量
     * @param orderNo
     * @param ip
     * @return
     */
    Integer getOrderByNotInOrderNoAndInIp(@Param("orderNo") String orderNo, @Param("ip") String ip);

    /**
     * 查询订单
     * @param orderNo
     * @return
     */
    TOrder queryOrder(@Param("orderNo")String orderNo);

    OrderBean queryOrderAndDetailByOrderNo(@Param("orderNo") String orderNo);
    /**
     * 获取单个订单明细
     * */
     TOrder getOrderAndDetailByOrderNo(@Param("orderNo") String orderNo);
}
