package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
     * @Description   通过订单明细扩展表
     * @Param []
     * @Author  toney
     * @Date  15:56 2020/1/31
     * @return java.util.List<com.panda.sport.rcs.pojo.TOrder>
     **/
    List<TOrder> queryByOrderDetailExt(Long date);

    /**
     * @Description   按OrderDetailExt里ids查询
     * @Param [ids]
     * @Author  toney
     * @Date  10:43 2020/2/1
     * @return java.util.List<com.panda.sport.rcs.pojo.TOrder>
     **/
    List<TOrder> queryByOrderDetailExtAndIds(@Param("ids") List<String> ids);


    List<TOrder> queryByOrderDetailAndIds(@Param("ids") List<String> ids);
    /**
     * @Description   分页查询注单列表
     * @Param [map]
     * @Author  Sean
     * @Date  18:09 2020/2/20
     * @return java.util.List<com.panda.sport.rcs.pojo.TOrder>
     **/
    List<TOrder> queryOrderDetailByPage(Map<String, Object> map);
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
     * 查讯待处理滚球订单，更新状态
     * @param orderNo
     * @return
     */
    List<TOrder> getLiveWaitedOrders(@Param("orderNo") String orderNo);

    /**
     * 查找数量
     * @param orderNo
     * @param ip
     * @return
     */
    Integer getOrderByNotInOrderNoAndInIp(@Param("orderNo") String orderNo, @Param("ip") String ip);
}
