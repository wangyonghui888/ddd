package com.panda.sport.rcs.customdb.service;

import com.panda.sport.rcs.customdb.entity.MarketEntity;
import com.panda.sport.rcs.customdb.entity.MarketOptionEntity;

import java.util.List;
import java.util.Set;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.customdb.service
 * @description :  TODO
 * @date: 2020-07-19 14:17
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

public interface IMarketOptionService {

    List<MarketOptionEntity> getMarketOptionByIds(Set<Long> ids);

    List<MarketEntity> getMarketByIds(Set<Long> ids);
}
