package com.panda.sport.data.rcs.api;

import com.panda.sport.rcs.pojo.dto.StandardMatchStatusDTO;

/**
 * 赛事状态变化通知接口服务类
 */
public interface MatchStatusNotifyService {

    /**
     * 赛事状态变化通知
     * @param standardMatchStatusVo
     * @return
     */
    Response statusChanged(StandardMatchStatusDTO standardMatchStatusVo);
}
