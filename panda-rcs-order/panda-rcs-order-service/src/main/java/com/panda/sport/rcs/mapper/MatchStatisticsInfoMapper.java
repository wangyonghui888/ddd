package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.LanguageInternation;
import com.panda.sport.rcs.pojo.MatchStatisticsInfo;
import java.util.List;

import com.panda.sport.rcs.pojo.SystemItemDict;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @ClassName MatchStatisticsInfoMapper
 * @Description: TODO 
 * @Author Vector
 * @Date 2019/10/11 
**/
public interface MatchStatisticsInfoMapper extends BaseMapper<MatchStatisticsInfo> {
    int deleteByPrimaryKey(Long id);

    MatchStatisticsInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MatchStatisticsInfo record);

    int updateByPrimaryKey(MatchStatisticsInfo record);

    int updateBatch(List<MatchStatisticsInfo> list);

    int batchInsert(@Param("list") List<MatchStatisticsInfo> list);

    @Select("select b.value,b.code from match_statistics_info a left join system_item_dict b on a.period=b.value where b.parent_type_id=8 and b.addition1=#{sportId} and a.standard_match_id=#{matchId}")
    SystemItemDict getMatchPeriod(@Param("sportId")Long sportId,@Param("matchId")Long matchId);
    
    @Select("SELECT i.match_period_id period, i.seconds_match_start secondsMatchStart, CONCAT(d.t1,':',d.t2) score FROM standard_match_info i LEFT JOIN match_statistics_info_detail d ON d.standard_match_id = i.id AND d.code = 'match_score'  AND d.first_num = 0 AND d.second_num = 0 WHERE i.id = #{matchId} limit 1 ")
    MatchStatisticsInfo getMatchInfoByMatchId(@Param("matchId")Long matchId);
}