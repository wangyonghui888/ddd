package com.panda.sport.rcs.data.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.core.db.annotation.Master;
import com.panda.sport.rcs.data.mapper.StandardSportTypeMapper;
import com.panda.sport.rcs.data.service.IStandardSportTypeService;
import com.panda.sport.rcs.pojo.StandardSportType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 标准体育种类表.  服务实现类
 * </p>
 *
 * @author Vector
 * @since 2019-09-27
 */
@Service
public class StandardSportTypeServiceImpl extends ServiceImpl<StandardSportTypeMapper, StandardSportType> implements IStandardSportTypeService {

    @Autowired
    StandardSportTypeMapper standardSportTypeMapper;

    @Override
    public Long getLastCrtTime() {
        return standardSportTypeMapper.getLastCrtTime();
    }

    @Override
    public int insertOrUpdate(StandardSportType standardSportType) {
        if (standardSportType == null) return 0;
        return standardSportTypeMapper.insertOrUpdate(standardSportType);
    }

}
