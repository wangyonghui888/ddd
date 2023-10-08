package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsTradeConfig;
import org.apache.ibatis.annotations.Param;
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

    /**
     * 通过玩法相关的最后一次配置
     *
     * @param matchId 赛事ID
     * @param playId  玩法ID
     * @return
     * @author Paca
     */
    RcsTradeConfig getLastPlayConfig(@Param("matchId") String matchId, @Param("playId") String playId);

    /**
     * 获取操盘模式
     *
     * @param matchId 赛事ID
     * @param playIds 玩法ID集合，为空查询所有
     * @return
     */
    List<RcsTradeConfig> getTradeMode(@Param("matchId") String matchId, @Param("playIds") Collection<String> playIds);

}
