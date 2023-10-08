package com.panda.rcs.cleanup.mapper;

import com.panda.rcs.cleanup.entity.MatchTournamentVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchTournamentMapper {

    /**
     * 查询无效赛事模板数据
     * @return
     */
    List<MatchTournamentVo> queryInvalidTournamentId();

    Integer deletePlayMargain(@Param("matchTournamentLists") List<MatchTournamentVo> matchTournamentLists);

    Integer deleteTemplateNotMatch();

    List<MatchTournamentVo> queryTournamentIdByMatchIds(@Param("matchIds") List<Long> matchIds);

    Integer deleteTemplateByMatchIds(@Param("matchIds") List<Long> matchIds);

    Integer deleteTimeSharingNode(@Param("matchTournamentLists") List<MatchTournamentVo> matchTournamentLists);

    Integer deleteAcceptEvent(@Param("matchTournamentLists") List<MatchTournamentVo> matchTournamentLists);

    Integer deleteAcceptConfig(@Param("matchTournamentLists") List<MatchTournamentVo> matchTournamentLists);

    Integer deleteAcceptEventSettle(@Param("matchTournamentLists") List<MatchTournamentVo> matchTournamentLists);

    Integer deleteAcceptConfigSettle(@Param("matchTournamentLists") List<MatchTournamentVo> matchTournamentLists);

    Integer deleteTemplateEvent(@Param("matchTournamentLists") List<MatchTournamentVo> matchTournamentLists);

}
