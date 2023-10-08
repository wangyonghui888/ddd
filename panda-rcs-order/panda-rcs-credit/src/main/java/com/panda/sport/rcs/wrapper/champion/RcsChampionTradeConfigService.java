package com.panda.sport.rcs.wrapper.champion;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsChampionTradeConfig;

import java.util.List;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 冠军玩法限额配置
 * @Author : Paca
 * @Date : 2021-06-15 14:57
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsChampionTradeConfigService extends IService<RcsChampionTradeConfig> {

    /**
     * 获取冠军玩法限额配置
     *
     * @param marketId
     * @return
     */
    List<RcsChampionTradeConfig> getChampionLimit(Long marketId);
}
