package com.panda.sport.rcs.customdb.service.impl;

import com.panda.sport.rcs.customdb.mapper.SSportMapper;
import com.panda.sport.rcs.customdb.service.ISportTypeService;
import com.panda.sport.rcs.db.entity.SSport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.customdb.service.impl
 * @description :  运动种类查询服务
 * @date: 2020-07-17 13:25
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service("sportTypeServiceImpl")
public class SportTypeServiceImpl implements ISportTypeService {

    @Autowired
    SSportMapper sportMapper;

    @Override
    public List<SSport> query() {
        return sportMapper.query();
    }
}
