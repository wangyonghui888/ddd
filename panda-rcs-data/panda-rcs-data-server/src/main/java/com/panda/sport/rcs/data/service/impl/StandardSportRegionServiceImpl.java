package com.panda.sport.rcs.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.panda.sport.rcs.core.db.annotation.Master;
import com.panda.sport.rcs.data.mapper.StandardSportMarketCategoryMapper;
import com.panda.sport.rcs.data.mapper.StandardSportRegionMapper;
import com.panda.sport.rcs.data.service.IStandardSportRegionService;
import com.panda.sport.rcs.pojo.StandardSportRegion;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 标准体育区域表 服务实现类
 * </p>
 *
 * @author Vector
 * @since 2019-09-26
 */
@Service
@Slf4j
public class StandardSportRegionServiceImpl extends ServiceImpl<StandardSportRegionMapper, StandardSportRegion> implements IStandardSportRegionService {


    @Autowired
    StandardSportRegionMapper standardSportRegionMapper;

    @Override
    @Master
    public Long getLastCrtTime() {
        return standardSportRegionMapper.getLastCrtTime();
    }

    @Override
    public int batchInsert(List<StandardSportRegion> standardSportRegions) {
        if(CollectionUtils.isEmpty(standardSportRegions)){return 0;}
        return standardSportRegionMapper.batchInsert(standardSportRegions);
    }

    @Override
    @Master
    public List<StandardSportRegion> listByListIds(ArrayList<Long> sportRegionLongs) {
        return standardSportRegionMapper.selectBatchIds(sportRegionLongs);
    }

    @Override
    public int batchInsertOrUpdate(List<StandardSportRegion> standardSportRegions) {
        if(CollectionUtils.isEmpty(standardSportRegions)){return 0;}
        return standardSportRegionMapper.batchInsertOrUpdate(standardSportRegions);
    }

}
