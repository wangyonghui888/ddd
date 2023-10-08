package com.panda.sport.rcs.customdb.mapper;

import com.panda.sport.rcs.db.entity.OrderOptionOddChange;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.customdb.mapper
 * @description :
 * @date: 2020-07-19 10:05
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

public interface OrderOptionOddChangeExtMapper {

    void batchSaveOrUpdate(@Param("list") List<OrderOptionOddChange> list);
}
