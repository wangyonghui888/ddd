package com.panda.sport.rcs.customdb.mapper;

import com.panda.sport.rcs.customdb.entity.MarketEntity;
import com.panda.sport.rcs.customdb.entity.MarketOptionEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.customdb.mapper
 * @description :
 * @date: 2020-07-18 13:52
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface MarketOptionMapper {

    List<MarketOptionEntity> getMarketOptionByIds(@Param("ids") Set<Long> ids);

    List<MarketEntity> getMarketByIds(@Param("ids") Set<Long> ids);

}
