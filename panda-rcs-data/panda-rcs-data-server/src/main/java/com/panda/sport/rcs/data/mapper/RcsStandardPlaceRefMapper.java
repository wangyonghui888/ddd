package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsStandardPlaceRef;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RcsStandardPlaceRefMapper  extends BaseMapper<RcsStandardPlaceRef> {
    int deleteByPrimaryKey(Long id);

    int insertOrUpdate(RcsStandardPlaceRef record);

    int insertOrUpdateSelective(RcsStandardPlaceRef record);

    int insertSelective(RcsStandardPlaceRef record);

    RcsStandardPlaceRef selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RcsStandardPlaceRef record);

    int updateByPrimaryKey(RcsStandardPlaceRef record);

    int updateBatch(List<RcsStandardPlaceRef> list);

    int updateBatchSelective(List<RcsStandardPlaceRef> list);

    int batchInsert(@Param("list") List<RcsStandardPlaceRef> list);

    int batchInsertOrUpdate(@Param("list") List<RcsStandardPlaceRef> list);

	List<Map<String, Object>> queryOddsByPlaceNumAndPlayId(Map<String, Object> params);
}