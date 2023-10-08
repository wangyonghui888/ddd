package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.StandardSportTournament;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * SR中对应tournament
BC中对应Competition
 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
@Repository
public interface StandardSportTournamentMapper extends BaseMapper<StandardSportTournament> {
    /**
     * @Description   查询
     * @Param [tournamentIds, beginDate, endDate, matchType, otherMorningMarke]
     * @Author  toney
     * @Date  13:11 2020/3/19
     * @return java.util.List<com.panda.sport.rcs.pojo.StandardSportTournament>
     **/
    List<StandardSportTournament> queryByIdsAndBeginDateAndEndDateAndMatchType(@Param("tournamentIds") List<Long> tournamentIds,@Param("beginDate") Long beginDate,@Param("endDate") Long endDate,@Param("matchType")Integer matchType,@Param("otherMorningMarke")Integer otherMorningMarke);
}
