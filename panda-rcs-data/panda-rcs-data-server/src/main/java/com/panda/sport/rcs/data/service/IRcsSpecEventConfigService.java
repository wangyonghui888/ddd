package com.panda.sport.rcs.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsSpecEventConfig;

import java.util.List;

public interface IRcsSpecEventConfigService extends IService<RcsSpecEventConfig> {

    /**
     * 根据赛事ID获取配置
     * @param matchId
     * @param eventCode 事件code
     * @return
     */
    RcsSpecEventConfig getByMatchId(Long matchId,String eventCode);

    /**
     * 根据赛事ID获取配置
     * @param matchId
     * @param eventCode 事件code
     * @return
     */
    List<RcsSpecEventConfig> getByMatchId(Long matchId);

}
