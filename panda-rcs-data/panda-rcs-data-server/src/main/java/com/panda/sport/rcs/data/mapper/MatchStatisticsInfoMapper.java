package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.MatchStatisticsInfo;
import com.panda.sport.rcs.pojo.dto.MatchStatisticsInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName MatchStatisticsInfoMapper
 * @Description: TODO 
 * @Author Vector
 * @Date 2019/10/11 
**/
@Mapper
public interface MatchStatisticsInfoMapper extends BaseMapper<MatchStatisticsInfo> {

    int updateBatch(List<MatchStatisticsInfo> list);

    int batchInsert(@Param("list") List<MatchStatisticsInfo> list);

    int insertOrUpdate(MatchStatisticsInfo record);

    int insertOrUpdateSelective(MatchStatisticsInfo record);

    int insertOrUpdateDto(MatchStatisticsInfoDTO data);
}