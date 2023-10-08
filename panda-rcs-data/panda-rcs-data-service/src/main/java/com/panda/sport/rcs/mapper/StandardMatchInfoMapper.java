package com.panda.sport.rcs.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.vo.BaseMatchInfoVo;
import com.panda.sport.rcs.vo.MarketLiveOddsQueryVo;
import com.panda.sport.rcs.vo.StandardMatchAllSellVo;
import com.panda.sport.rcs.vo.TeamVo;
import com.panda.sport.rcs.vo.TournamentMatchInfoVo;
import com.panda.sport.rcs.vo.TournamentNameVo;
import com.panda.sport.rcs.vo.TournamentVoBySport;
import com.panda.sport.rcs.vo.TraderConditionVo;
import org.springframework.stereotype.Component;

/**
 * @ClassName StandardMatchInfoMapper
 * @Description: TODO
 * @Author Vector
 * @Date 2019/10/4
 **/
@Component
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

	Integer selectMatchLengthById(@Param("matchId") Integer matchId);
}
