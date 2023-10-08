package com.panda.sport.rcs.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsTradeConfig;

/**
 * @author :  wealth
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.data.wrapper
 * @Description :  TODO
 * @Date: 2022-06-07 16:55
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsTradeConfigService extends IService<RcsTradeConfig> {

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
}
