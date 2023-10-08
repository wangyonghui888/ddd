package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsBatchChangeDataSourceRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 批量切换数据源记录
 * magic
 * 2023.4.19
 */
@Repository
public interface RcsBatchChangeDataSourceRecordMapper extends BaseMapper<RcsBatchChangeDataSourceRecord> {

    int batchSave(@Param("list") List<RcsBatchChangeDataSourceRecord> list);
}
