package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsTradeConfig;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mapper
 * @Description :  TODO
 * @Date: 2020-03-05 16:38
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
public interface RcsTradeConfigMapper extends BaseMapper<RcsTradeConfig> {

    /**
     * @return com.panda.sport.rcs.pojo.RcsTradeConfig
     * @Description //TODO
     * @Param [matchId, playId, marketId]
     * @Author kimi
     * @Date 2020/3/6
     **/
    RcsTradeConfig selectRcsTradeConfig(@Param("matchId") String matchId, @Param("playId") String playId, @Param("marketId") String marketId);

    RcsTradeConfig getRcsTradeConfig(@Param("config") RcsTradeConfig rcsTradeConfig);

    /**
     * 通过玩法ID获取赛事下玩法集的状态
     *
     * @param matchId 赛事ID
     * @param playId  玩法ID
     * @return
     * @author Paca
     */
    RcsTradeConfig getPlaySetStatusByPlayId(@Param("matchId") String matchId, @Param("playId") Long playId);

    /**
     * 获取赛事下盘口位置的状态
     *
     * @param matchId
     * @param playIds
     * @return
     * @author Paca
     */
    List<RcsTradeConfig> getMarketPlaceStatus(@Param("matchId") String matchId, @Param("playIds") Collection<String> playIds);

    /**
     * 获取玩法操盘方式
     *
     * @param matchId
     * @param playId
     * @return
     */
    @Select("SELECT * FROM rcs_trade_config t" +
            " WHERE" +
            " t.match_id = #{matchId}" +
            " AND t.data_source IS NOT NULL" +
            " AND (" +
            " ( t.trader_level = 1 AND t.targer_data = #{matchId} )" +
            " OR ( t.trader_level = 2 AND t.targer_data = #{playId} )" +
            " )" +
            " ORDER BY id DESC LIMIT 1")
    RcsTradeConfig getDataSource(@Param("matchId") String matchId, @Param("playId") String playId);

}
