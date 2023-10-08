package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.bean.RcsMatchEventTypeInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface RcsMatchEventTypeInfoMapper extends BaseMapper<RcsMatchEventTypeInfo> {
    int updateBatch(List<RcsMatchEventTypeInfo> list);

    int batchInsert(@Param("list") List<RcsMatchEventTypeInfo> list);

    int insertOrUpdate(RcsMatchEventTypeInfo record);

    int insertOrUpdateSelective(RcsMatchEventTypeInfo record);

}