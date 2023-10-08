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

	List<StandardMatchInfo> selectPageByConditionV2(@Param("obj") MarketLiveOddsQueryVo marketLiveOddsQueryVo);

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

    /**
     * @return java.util.List<java.lang.Long>
     * @Description //根据玩法阶段查找有哪些盘口
     * @Param [state, matchId]
     * @Author kimi
     * @Date 2020/2/10
     **/
    List<Long> selectMarketIdByState(@Param("state") Integer state, @Param("matchId") Long matchId);


	List<String> queryMatchEndIds(Map<String, Object> params);

	IPage<StandardMatchAllSellVo> getAllSellMatchList(IPage<StandardMatchAllSellVo> iPage
			,@Param("marketLiveOddsQueryVo") MarketLiveOddsQueryVo marketLiveOddsQueryVo);
	/**
	 * @Description   //查询赛事是否处于暂停事件
	 * @Param [standardMatchId]
	 * @Author  Sean
	 * @Date  16:57 2020/10/17
	 * @return com.panda.sport.rcs.pojo.StandardMatchInfo
	 **/
    StandardMatchInfo queryTimeOutMatch(@Param("id") Long standardMatchId);

    /**
     * 获取早盘赛事列表
     * @return
     */
    List<StandardMatchInfo> getMatchList();

    /**
     * 根据赛事ID查询赛事信息
     * @param matchId
     * @return
     */
    StandardMatchInfo getMacthInfoById (@Param("matchId") Long matchId);
}
