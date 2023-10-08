package com.panda.sport.rcs.customdb.service;

import com.panda.sport.rcs.db.entity.SSport;

import java.util.List;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.customdb.service
 * @description :  运动种类查询接口
 * @date: 2020-07-17 13:24
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ISportTypeService {

    List<SSport> query();
}
