package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.MatchEventInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName MatchEventInfoMapper
 * @Description: TODO 
 * @Author Vector
 * @Date 2019/10/10 
**/
@Mapper
public interface MatchEventInfoMapper extends BaseMapper<MatchEventInfo> {

    int updateBatch(List<MatchEventInfo> list);

    int batchInsert(@Param("list") List<MatchEventInfo> list);

    int insertOrUpdate(MatchEventInfo record);
}