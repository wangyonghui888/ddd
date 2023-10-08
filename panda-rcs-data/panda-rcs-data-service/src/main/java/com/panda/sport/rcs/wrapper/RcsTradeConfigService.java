package com.panda.sport.rcs.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsTradeConfig;

/**
 * @author: jstyChandler
 * @Package: com.panda.sport.rcs.wrapper
 * @ClassName: RcsTradeConfigService
 * @Description: 操盘配置表
 * @Date: 2022/10/10 18:48
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsTradeConfigService extends IService<RcsTradeConfig> {

    /**
     * 冠军赛事获取操盘类型(WS推送)
     *
     * @param matchId
     * @param marketId
     * @return
     */
    String[] getDataSource(String linkId, Long matchId, Long marketId, String dataSource);
}
