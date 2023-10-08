package com.panda.sport.rcs.mgr.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsTradeConfig;

import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2020-03-07
 */
public interface IRcsTradeConfigService extends IService<RcsTradeConfig> {

    /**
     * 获取赛事状态配置
     *
     * @param matchId
     * @return
     * @author Paca
     */
    RcsTradeConfig getMatchStatusConfig(Long matchId);

    /**
     * 获取赛事状态
     *
     * @param matchId
     * @return
     * @author Paca
     */
    Integer getMatchStatus(Long matchId);

    /**
     * 获取玩法状态配置
     *
     * @param matchId
     * @param playId
     * @return
     * @author Paca
     */
    RcsTradeConfig getPlayStatusConfig(Long matchId, Long playId);

    /**
     * 通过玩法ID获取玩法集状态配置
     *
     * @param matchId 赛事ID
     * @param playId  玩法ID
     * @return
     * @author Paca
     */
    RcsTradeConfig getPlaySetStatusByPlayId(Long matchId, Long playId);

    /**
     * 获取玩法下所有盘口位置状态配置
     *
     * @param matchId
     * @param playId
     * @return
     * @author Paca
     */
    Map<Integer, RcsTradeConfig> getMarketPlaceStatus(Long matchId, Long playId);

    /**
     * 获取操盘类型
     *
     * @param matchId
     * @param playId
     * @return
     */
    Integer getDataSource(Long matchId, Long playId);
}
