package com.panda.rcs.cleanup.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderMapper {

    /**
     * 接拒单表数据，保留一天数据
     * @param expiredTime 过期时间戳
     * @return
     */
    int deleteOrderDetailExt(@Param("expiredTime") Long expiredTime);

    /**
     * 删除7天前的订单数据
     * @param expiredTime 过期时间戳
     * @return
     */
    int deleteOrder(@Param("expiredTime") Long expiredTime);

    /**
     * 删除7天前的订单详细数据
     * @param expiredTime 过期时间戳
     * @return
     */
    int deleteOrderDetail(@Param("expiredTime") Long expiredTime);

    /**
     * 根据赛事Id查询订单编号
     * @param matchIds
     * @return
     */
    List<String> getOrderNoByMatchIds(@Param("matchIds") List<Long> matchIds);

    /**
     * 删除订单表
     * @param orderNos
     * @return
     */
    int deleteOrderByOrderNo(@Param("orderNos") List<String> orderNos);

    /**
     * 删除订单表
     * @param matchIds
     * @return
     */
    int deleteOrderDetailByMatchIds(@Param("matchIds") List<Long> matchIds);

    /**
     * 删除订单表-按条数删除
     * @param matchIds
     * @return
     */
    int deleteOrderDetailByMatchIdsByLimit(@Param("matchIds") List<Long> matchIds, @Param("rows") Integer rows);

    /**
     * 删除三个月前数据
     * @param cleanupTime
     * @return
     */
    int deleteMtsOrderExt(@Param("cleanupTime") String cleanupTime);



    int deletePendingOrderByMatchIds(@Param("matchIds") List<Long> matchIds);

    int deletePendingBetPredictByMatchIds(@Param("matchIds") List<Long> matchIds);

    int deletePendingForecastByMatchIds(@Param("matchIds") List<Long> matchIds);

    int deletePendingForecastPlayByMatchIds(@Param("matchIds") List<Long> matchIds);


    int deleteOrderVolumeByTime(@Param("deleteTime") Long deleteTime);

    int deleteForecastSnapshotByMatchIds(@Param("matchIds") List<Long> matchIds);


    List<String> getOrderNoByMatchId(@Param("matchId") Long matchId);

    int deleteOrderDetailByMatchIdByLimit(@Param("matchId") Long matchId, @Param("rows") Integer rows);

    int deleteOrderByMatchIdByLimit(@Param("matchId") Long matchId, @Param("rows") Integer rows);

}