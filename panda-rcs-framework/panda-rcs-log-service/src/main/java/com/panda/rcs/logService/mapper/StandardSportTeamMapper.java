package com.panda.rcs.logService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.rcs.logService.vo.MatchTeamInfo;
import com.panda.rcs.logService.vo.StandardSportTeam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface StandardSportTeamMapper extends BaseMapper<StandardSportTeam> {

    List<MatchTeamInfo> queryTeamListByMatchId(@Param("matchId") Long matchId);

    /**
     * 获取中文队伍
     * @param matchId
     * @return
     */
    List<MatchTeamInfo> queryTeamListByMatchIdZs(@Param("matchId") Long matchId);

    /**
     * 获取英文队伍
     * @param matchId
     * @return
     */
    List<MatchTeamInfo> queryTeamListByMatchIdEn(@Param("matchId") Long matchId);
}
