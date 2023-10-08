package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetail;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 足球赛事盘口表. 使用盘口关联的功能存在以下假设：同一个盘口的显示值不可变更，如果变更需要删除2个盘口之间的关联关系。。 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
@Repository
public interface StandardSportMarketMapper extends BaseMapper<StandardSportMarket> {

	Map<String, Object> queryMatchMarketInfo(Map<String, Object> map);

	String queryOddTemplateInfo(String templateCode);


	/**
	 * @return java.util.List<com.panda.sport.rcs.pojo.StandardSportMarket>
	 * @Description //查找盘口id
	 * @Param [matchId, playIds]
	 * @Author kimi
	 * @Date 2020/1/15
	 **/
	List<StandardSportMarket> selectStandardSportMarketByMatchIdAndPlayId(@Param("matchId") Long matchId, @Param("playId") Integer playId);

	/**
	 * @return java.lang.Integer
	 * @Description //根据盘口值查询id
	 * @Param [matchId, marketValue]
	 * @Author kimi
	 * @Date 2020/1/25
	 **/
	Long selectStandardSportMarketIdByMarketValue(@Param("matchId") Long matchId, @Param("playId") Long playId, @Param("marketValue") String marketValue);

	/**
	 * @return java.util.List<java.lang.Long>
	 * @Description //根据玩法阶段查找有哪些盘口
	 * @Param [state, matchId]
	 * @Author kimi
	 * @Date 2020/2/10
	 **/
	List<Long> selectMarketIdByState(@Param("state") Integer state, @Param("matchId") Long matchId);

	/**
	 * @return java.util.List<java.lang.Long>
	 * @Description //根据赛事id 查询所有盘口Id
	 * @Param [matchId]
	 * @Author kimi
	 * @Date 2020/2/10
	 **/
	List<Long> selectMarketIdByMatchId(@Param("matchId") Long matchId);

	/**
	 * @return java.util.List<java.lang.Integer>
	 * @Description //TODO
	 * @Param [matchId]
	 * @Author kimi
	 * @Date 2020/2/18
	 **/
	List<Long> selectPlayIdByMatchId(@Param("matchId") Long matchId);

	/**
	 * @return java.util.List<com.panda.sport.rcs.pojo.StandardSportMarket>
	 * @Description //TODO
	 * @Param [matchId, playId]
	 * @Author kimi
	 * @Date 2020/2/20
	 **/
	List<StandardSportMarketOdds> selectStandardSportMarketByGiveWay(@Param("matchId") Long matchId, @Param("playId") Long playId);

	Map<String, Object> getOtherMarketInfo(Map<String, Object> params);

	List<Map<String, Object>> queryRelatedDataList(Map<String, Object> queryParams);

	/**
	 * @return java.util.List<com.panda.sport.rcs.pojo.MatchStatisticsInfoDetail>
	 * @Description //TODO
	 * @Param [matchId]
	 * @Author kimi
	 * @Date 2020/2/29
	 **/
	List<MatchStatisticsInfoDetail> selectMatchStatisticsInfoDetail(@Param("matchId") Long matchId);

	/**
	 * @return java.util.List<com.panda.sport.rcs.pojo.StandardSportMarket>
	 * @Description //TODO
	 * @Param [matchId, playId, addition2]
	 * @Author kimi
	 * @Date 2020/2/29
	 **/
	List<StandardSportMarket> selectStandardSportMarket(@Param("matchId") Long matchId, @Param("playId") Long playId, @Param("addition2") String addition2);

	/**
	 * @return java.util.List<com.panda.sport.rcs.pojo.StandardSportMarket>
	 * @Description //TODO
	 * @Param [matchId, playId1, playId2]
	 * @Author kimi
	 * @Date 2020/3/2
	 **/
	List<StandardSportMarket> selectStandardSportMarketByMatchIdAndPlayIdAndPlayId(@Param("matchId") Long matchId);

}


