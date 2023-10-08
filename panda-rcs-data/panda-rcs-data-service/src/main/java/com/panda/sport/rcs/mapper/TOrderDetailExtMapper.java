package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.TOrderDetailExt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface TOrderDetailExtMapper extends BaseMapper<TOrderDetailExt> {
    int updateBatch(List<TOrderDetailExt> list);

    int updateOrderDetailExtStatus(@Param("orderNo") String orderNo,@Param("orderStatus") String orderStatus);

    int batchInsert(@Param("list") List<TOrderDetailExt> list);

    int insertOrUpdate(TOrderDetailExt record);

    int insertOrUpdateSelective(TOrderDetailExt record);
    /**
     * @Description   批量处理订单状态
     * @Param [orderStatus, ids]
     * @Author  toney
     * @Date  10:17 2020/2/1
     * @return int
     **/
    int orderTakingBatch(@Param("orderStatus") String orderStatus, @Param("ids") List<String> ids);


    /**
     * @Description   修改handleStatus状态
     * @Param [handleStatus, ids]
     * @Author  toney
     * @Date  14:22 2020/4/7
     * @return int
     **/
    int updateHandleStatusByList(@Param("handleStatus") Integer handleStatus, @Param("ids") List<Long> ids);

    int checkOrderStatus(@Param("orderNo") String orderNo);

    List<TOrderDetailExt> selectWaitedOrderList(@Param("maxId") Long maxId);


    List<TOrderDetailExt> selectHandleOrderList();

    void saveOrUpdateTOrderDetailExt(@Param("list") List<TOrderDetailExt> exts);
    /**
     * @Description   查询订单号根据主单号
     * @Param [berNos]
     * @Author  Sean
     * @Date  16:25 2020/2/21
     **/
    List<String> queryOrderNoByBetNo(@Param("ids") List<String> betNos);
    /**
     * @Description   拒单详情表全部拒单
     * @Param [betNos]
     * @Author  Sean
     * @Date  17:27 2020/2/21
     * @return void
     **/
    void updateOrderDetailExtList(@Param("ids") List<String> ids);
    /**
     * @Description   查询为完全处理的订单
     * @Param [betNos]
     * @Author  Sean
     * @Date  17:10 2020/2/21
     * @return java.util.List<java.lang.String>
     **/
    List<String> queryOrderNosByBetNo(@Param("ids") List<String> betNos);

}