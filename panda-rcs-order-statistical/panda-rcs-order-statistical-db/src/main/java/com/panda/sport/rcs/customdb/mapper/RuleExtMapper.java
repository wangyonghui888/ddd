package com.panda.sport.rcs.customdb.mapper;

import com.panda.sport.rcs.common.vo.rule.*;
import com.panda.sport.rcs.customdb.entity.StaticsItemEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 * 统计规则相关数据 Mapper 接口
 * </p>
 *
 * @author
 * @since 2020-06-23
 */

public interface RuleExtMapper {

    FinancialRuleVo getProfitAndRate(@Param("userId") long userId, @Param("beginDate") long beginDate, @Param("endDate") long endDate);

    SportRateVo getSportRate(@Param("userId") long userId, @Param("sportId") long sportId, @Param("beginDate") long beginDate, @Param("endDate") long endDate);

    List<TournamentBetNumVo> getTournamentBetNum(@Param("userId") long userId, @Param("beginDate") long beginDate, @Param("endDate") long endDate);

    List<SportBetNumVo> getSportBetNum(@Param("userId") long userId, @Param("beginDate") long beginDate, @Param("endDate") long endDate);

    Long getUserBetNum(@Param("userId") long userId, @Param("beginDate") long beginDate, @Param("endDate") long endDate);

    Long getUserSuccessBetNum(@Param("userId") long userId, @Param("beginDate") long beginDate, @Param("endDate") long endDate);

    UserBetAmountVo getUserBetAmount(@Param("userId") long userId, @Param("beginDate") long beginDate, @Param("endDate") long endDate);

    TeamTimesVo getTeamTimes(@Param("userId") long userId, @Param("beginDate") long beginDate, @Param("endDate") long endDate);

    CityNumVo getCityNum(@Param("userId") long userId, @Param("beginDate") Long beginDate, @Param("endDate") Long endDate);

    List<IpNumVo> getIpNum(@Param("num") long num, @Param("beginDate") Long beginDate, @Param("endDate") Long endDate);

    Long getUserDangerousBetNum(@Param("userId") long userId, @Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("dangerousId") Long dangerousId);

    BetAmountOrderVo getLargeAmountBetOrders(@Param("uid") long uid, @Param("limitAmount") Long  limitAmount, @Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    Long getFullBetNum(@Param("userId") long userId, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    List<PlayBetNumVo> getPlayBetNum(@Param("userId") long userId, @Param("beginDate") long beginDate, @Param("endDate") long endDate);

    Long getMatchBetNumVo(@Param("userId") long userId, @Param("beginDate") long beginDate, @Param("endDate") long endDate);

    List<MatchProfitNumVo> getMatchProfitNum(@Param("userId") long userId, @Param("beginDate") long beginDate, @Param("endDate") long endDate, @Param("minLevel") long minLevel, @Param("maxLevel") long maxLevel);

}
