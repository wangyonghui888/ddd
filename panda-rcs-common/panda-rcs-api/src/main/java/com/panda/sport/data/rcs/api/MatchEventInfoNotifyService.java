package com.panda.sport.data.rcs.api;

import com.panda.sport.data.rcs.dto.MatchEventInfoDTO;

/**
 * @ClassName MatchEventInfoNotifyService
 * @Description: 事件通知接口
 * @Author Vector
 * @Date 2019/10/30
 **/
public interface MatchEventInfoNotifyService {
    /**
     * 赛事状态变化通知
     *
     * @param matchEventInfoDTO
     * @return
     */
    Response eventNotify(MatchEventInfoDTO matchEventInfoDTO);
}
