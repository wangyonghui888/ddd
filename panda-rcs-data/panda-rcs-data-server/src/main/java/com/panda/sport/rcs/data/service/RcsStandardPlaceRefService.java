package com.panda.sport.rcs.data.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsStandardPlaceRef;

import java.util.List;
import java.util.Map;

public interface RcsStandardPlaceRefService extends IService<RcsStandardPlaceRef> {


    int deleteByPrimaryKey(Long id);

    int insertOrUpdate(RcsStandardPlaceRef record);

    int insertOrUpdateSelective(RcsStandardPlaceRef record);

    int insertSelective(RcsStandardPlaceRef record);

    RcsStandardPlaceRef selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RcsStandardPlaceRef record);

    int updateByPrimaryKey(RcsStandardPlaceRef record);

    int updateBatch(List<RcsStandardPlaceRef> list);

    int updateBatchSelective(List<RcsStandardPlaceRef> list);

    int batchInsert(List<RcsStandardPlaceRef> list);

    int batchInsertOrUpdate(List<RcsStandardPlaceRef> rcsStandardPlaceRefs);

	List<Map<String, Object>> queryOddsByPlaceNumAndPlayId(Map<String, Object> params);
}

