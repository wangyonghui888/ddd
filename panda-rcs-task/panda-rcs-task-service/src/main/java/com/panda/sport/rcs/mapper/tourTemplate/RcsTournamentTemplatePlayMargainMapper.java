package com.panda.sport.rcs.mapper.tourTemplate;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.dto.MarketCountDTO;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Description   联赛玩法margin配置表
 * @Param
 * @Author  toney
 * @Date  20:02 2020/5/10
 * @return
 **/
@Mapper
@Component
public interface RcsTournamentTemplatePlayMargainMapper extends BaseMapper<RcsTournamentTemplatePlayMargain> {
    int deleteByPrimaryKey(Integer id);


    int insertSelective(RcsTournamentTemplatePlayMargain record);


    int insertOrUpdateBatch(@Param("list")List<RcsTournamentTemplatePlayMargain> list);

    RcsTournamentTemplatePlayMargain selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RcsTournamentTemplatePlayMargain record);

    int updateByPrimaryKey(RcsTournamentTemplatePlayMargain record);


    MarketCountDTO marketCountByPlayId(@Param("matchId")Long matchId, @Param("playId")Integer playId);


    List<MarketCountDTO> marketCountByMatchId(@Param("matchId")Long matchId);


	List<Map<String, Object>> queryMarketConfigByMatchId(Map<String, Object> params);

    /**
     * 查询位置配置
     *
     * @param matchId   赛事ID
     * @param matchType 0-滚球，1-赛前
     * @return
     */
    List<RcsMatchMarketConfig> queryPlaceConfigByMatchId(@Param("matchId") Long matchId, @Param("matchType") Integer matchType);

    /**
     * 查询位置配置
     *
     * @param matchId   赛事ID
     * @param matchType 0-滚球，1-赛前
     * @return
     */
    List<RcsMatchMarketConfig> queryPlaceConfigByMatchIdSnooker(@Param("matchId") Long matchId, @Param("matchType") Integer matchType);

    List<RcsTournamentTemplatePlayMargain> searchHistoryMatch();
}