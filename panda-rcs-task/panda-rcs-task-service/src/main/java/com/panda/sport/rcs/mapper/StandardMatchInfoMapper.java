package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
@Repository
public interface StandardMatchInfoMapper extends BaseMapper<StandardMatchInfo> {


    //IPage<StandardMatchInfo> selectPageByCondition(IPage<StandardMatchInfo> page, @Param("obj") MarketLiveOdselectMarketIdByStatedsQueryVo marketLiveOddsQueryVo);

    TraderConditionVo selectConditionById(@Param("id") Long standardMatchId);

    String selectMatchPositionByOptionId(Long playOptionId);

    List<TournamentMatchInfoVo> selectMacthInfo(@Param("tournamentId") long tournamentId,@Param("dateTime") String dateTime);

    List<StandardMatchInfo> selectPageByCondition(@Param("obj") MarketLiveOddsQueryVo marketLiveOddsQueryVo);

    List<StandardMatchInfo> selectTournamentsByCondition(@Param("obj") MarketLiveOddsQueryVo marketLiveOddsQueryVo);

	List<StandardMatchInfoVo> selectPageByConditionV2(@Param("obj") MarketLiveOddsQueryVo marketLiveOddsQueryVo);

    List<StandardMatchInfo> selectMatchs(@Param("obj") MarketLiveOddsQueryVo marketLiveOddsQueryVo);

    //List<MacthTournamentNameVo> selectTournamentNameVo(@Param("beginTime") long beginTime, @Param("endTime") long endTime, @Param("isOtherEarly") Integer isOtherEarly, @Param("liveOddBusiness") Integer liveOddBusiness);

    List<StandardMatchInfo> selectTournamentCount(@Param("tournamentIds") List<Long> tournamentIds, @Param("matchIds") List<Long> matchIds, @Param("matchType")Integer matchType);

    /**
     * @return java.lang.Integer
     * @Description //查询滚球数量
     * @Param []
     * @Author kimi
     * @Date 2020/1/13
     **/
    Integer getGrounderNumber();

    @Select("SELECT id,sport_id FROM standard_match_info WHERE  begin_time >  UNIX_TIMESTAMP() *1000 - (4*60*60*1000)  and match_status in (0,1,2,10) AND standard_tournament_id = #{id}")
    List<StandardMatchInfo> selectOpenStatusMatchByTournament(@Param("id") Long tournamentId);
    /**
     * @Description   查询联赛
     * @Param [map]
     * @Author  Sean
     * @Date  12:21 2020/2/5
     * @return java.util.List<com.panda.sport.rcs.vo.BaseMatchInfoVo>
     **/
    /**
     * @return java.util.List<java.lang.Long>
     * @Description //根据玩法阶段查找有哪些盘口
     * @Param [state, matchId]
     * @Author kimi
     * @Date 2020/2/10
     **/
    List<Long> selectMarketIdByState(@Param("state") Integer state, @Param("matchId") Long matchId);

    /**
     * @Description   查询30s内没有处理的订单
     * @Param [matchId, beginTime]
     * @Author  Sean
     * @Date  17:31 2020/2/13
     * @return java.util.List<java.lang.String>
     **/
    List<String> queryOrderNoByMatchId(@Param("matchId") Integer matchId,@Param("beginTime") Long beginTime);

	List<String> queryMatchEndIds(Map<String, Object> params);

	IPage<StandardMatchAllSellVo> getAllSellMatchList(IPage<StandardMatchAllSellVo> iPage
			,@Param("marketLiveOddsQueryVo") MarketLiveOddsQueryVo marketLiveOddsQueryVo);

	List<Integer> getUserId(@Param("matchId") Integer matchId);

	List<Map<String, Object>> queryUpdateInfoByTime(Map<String, Object> queryParmas);

	List<Map<String, Object>> querySwitchLinkedList(Map<String, Object> queryParmas);

	void saveAutoSwitchLinked(Map<String, Object> bean);
}
