package com.panda.sport.rcs.customdb.mapper;

import com.panda.sport.rcs.customdb.entity.OddConversionEntity;

import java.util.List;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.customdb.mapper
 * @description :  T
 * @date: 2020-07-21 10:13
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface OddConversionMapper {


    List<OddConversionEntity> getOddConversion();
}
