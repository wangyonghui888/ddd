package com.panda.sport.rcs.customdb.service;

import com.panda.sport.rcs.customdb.entity.OddConversionEntity;

import java.util.List;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.customdb.service
 * @description :  赔率转换服务
 * @date: 2020-07-21 10:25
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface IOddConversionServiceExt {

    List<OddConversionEntity> getOddConversion();


    String getEuOddByMaOdd(String maOdd);
}
