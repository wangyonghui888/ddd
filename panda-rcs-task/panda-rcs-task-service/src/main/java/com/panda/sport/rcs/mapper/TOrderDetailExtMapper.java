package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.TOrderDetailExt;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface TOrderDetailExtMapper extends BaseMapper<TOrderDetailExt> {
    int updateBatch(List<TOrderDetailExt> list);

    int updateOrderDetailExtStatus(@Param("orderNo") String orderNo, @Param("orderStatus") String orderStatus);

    int batchInsert(@Param("list") List<TOrderDetailExt> list);

    int insertOrUpdate(TOrderDetailExt record);

    int insertOrUpdateSelective(TOrderDetailExt record);

    /**
     * @return int
     * @Description 批量处理订单状态
     * @Param [orderStatus, ids]
     * @Author toney
     * @Date 10:17 2020/2/1
     **/
    int orderTakingBatch(@Param("orderStatus") String orderStatus, @Param("ids") List<String> ids);


    /**
     * @return int
     * @Description 修改handleStatus状态
     * @Param [handleStatus, ids]
     * @Author toney
     * @Date 14:22 2020/4/7
     **/
    int updateHandleStatusByList(@Param("handleStatus") Integer handleStatus, @Param("ids") List<Long> ids);

    int checkOrderStatus(@Param("orderNo") String orderNo);

    List<TOrderDetailExt> selectWaitedOrderList(Map<String, Object> params);


    List<TOrderDetailExt> selectFootballWaitedOrderListNew(@Param("currentTime") Long currentTime);

    List<TOrderDetailExt> selectBasketballWaitedOrderListNew(@Param("currentTime") Long currentTime);

    List<TOrderDetailExt> selectWaitedOrderListComprehensive(@Param("currentTime") Long currentTime);

    List<TOrderDetailExt> selectWaitedOrderListStray(@Param("currentTime") Long currentTime);



    List<TOrderDetailExt> selectHandleOrderList();

    void saveOrUpdateTOrderDetailExt(@Param("list") List<TOrderDetailExt> exts);

    /**
     * @Description 查询订单号根据主单号
     * @Param [berNos]
     * @Author Sean
     * @Date 16:25 2020/2/21
     **/
    List<String> queryOrderNoByBetNo(@Param("ids") List<String> betNos);

    /**
     * @return void
     * @Description 拒单详情表全部拒单
     * @Param [betNos]
     * @Author Sean
     * @Date 17:27 2020/2/21
     **/
    void updateOrderDetailExtList(@Param("ids") List<String> ids);

    /**
     * @return java.util.List<java.lang.String>
     * @Description 查询为完全处理的订单
     * @Param [betNos]
     * @Author Sean
     * @Date 17:10 2020/2/21
     **/
    List<String> queryOrderNosByBetNo(@Param("ids") List<String> betNos);

    /**
     * 查询ext表的未处理的最小id
     *
     * @param @return 设定文件
     * @return Long    返回类型
     * @throws
     * @Title: queryMinExtCurrentId
     * @Description: TODO
     */
    Long queryMinExtCurrentId();

    /**
     * @return java.lang.Integer
     * @Description //更新注单状态
     * @Param [tOrderDetailExt]
     * @Author sean
     * @Date 2020/11/7
     **/
    Integer updateOrderDetailExtStatusByOrderNo(@Param("ext") TOrderDetailExt tOrderDetailExt);

    Integer updateOrderDetailExtStatusByOrderNoList(@Param("state") Integer state, @Param("list") List<String> orderNoList);


    Integer updateExtStatusBybetNo(@Param("ext") TOrderDetailExt tOrderDetailExt);

    List<TOrderDetailExt> queryUnHandleOrder(@Param("crtTime") Date crtTime);

}