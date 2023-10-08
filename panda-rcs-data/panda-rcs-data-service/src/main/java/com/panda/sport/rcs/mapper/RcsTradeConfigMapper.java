package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsTradeConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

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

    RcsTradeConfig getRcsTradeConfig(@Param("config")RcsTradeConfig rcsTradeConfig);
}
