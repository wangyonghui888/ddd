package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.dto.MatchFlowingDTO;
import com.panda.sport.rcs.console.pojo.RcsMonitorData;
import com.panda.sport.rcs.console.pojo.RcsMonitorDataVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;
import java.util.Map;

@Repository

public interface RcsMonitorDataMapper extends BaseMapper<RcsMonitorData> {
    int updateBatch(List<RcsMonitorData> list);

    int updateBatchSelective(List<RcsMonitorData> list);

    int batchInsert(@Param("list") List<RcsMonitorData> list);

    int insertOrUpdate(RcsMonitorData record);

    int insertOrUpdateSelective(RcsMonitorData record);

    List<RcsMonitorDataVo> queryRate(@Param("bean") MatchFlowingDTO bean);

    List<RcsMonitorDataVo> queryAllCount(@Param("bean") MatchFlowingDTO bean);


    List<String> group();

    int insertBean(RcsMonitorData bean);
}