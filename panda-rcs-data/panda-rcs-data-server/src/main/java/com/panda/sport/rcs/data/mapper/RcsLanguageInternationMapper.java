package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsLanguageInternation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface RcsLanguageInternationMapper extends BaseMapper<RcsLanguageInternation> {
    int updateBatch(List<RcsLanguageInternation> list);

    int batchInsert(@Param("list") List<RcsLanguageInternation> list);

    int insertOrUpdate(RcsLanguageInternation record);

    int insertOrUpdateSelective(RcsLanguageInternation record);

    int batchInsertOrUpdate(@Param("list") List<RcsLanguageInternation> list);

    int batchInsertOrUpdateMerge(@Param("list") List<RcsLanguageInternation> list);

    int insertOrUpdateMerge(RcsLanguageInternation record);
}