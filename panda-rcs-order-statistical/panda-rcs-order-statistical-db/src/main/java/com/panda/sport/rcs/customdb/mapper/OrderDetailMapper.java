package com.panda.sport.rcs.customdb.mapper;

import com.panda.sport.rcs.db.entity.OrderOptionOddChange;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.customdb.mapper
 * @description :
 * @date: 2020-07-18 13:52
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface OrderDetailMapper {

    /***
     * 查询已经接收的赛前盘订单
     * @param timeBegin
     * @param timeEnd
     * @return java.util.List<com.panda.sport.rcs.db.entity.TOrderDetail>
     * @Description
     * @Author dorich
     * @Date 14:04 2020/7/18
     **/
    List<OrderOptionOddChange> queryPreMatchOrder(@Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd,  @Param("betAmount") long betAmount);

    /***
     * 查询已经接收的滚球盘订单
     * @param timeBegin
     * @param timeEnd
     * @return java.util.List<com.panda.sport.rcs.db.entity.TOrderDetail>
     * @Description
     * @Author dorich
     * @Date 14:05 2020/7/18
     **/
    List<OrderOptionOddChange> queryLiveMatchOrder(@Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd, @Param("orderOddsValue") int orderOddsValue, @Param("betAmount") int betAmount);
    
    
}
