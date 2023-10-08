package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.MatchEventInfo;
import com.panda.sport.rcs.vo.RedCardVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName MatchEventInfoMapper
 * @Description: TODO 
 * @Author Vector
 * @Date 2019/10/10 
**/
public interface MatchEventInfoMapper extends BaseMapper<MatchEventInfo> {

    int updateBatch(List<MatchEventInfo> list);

    int batchInsert(@Param("list") List<MatchEventInfo> list);

    int insertOrUpdate(MatchEventInfo record);

}