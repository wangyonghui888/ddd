package com.panda.sport.rcs.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.MatchEventInfo;
import com.panda.sport.rcs.pojo.TOrderDetailExt;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateAcceptConfig;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2020-01-31
 */
public interface ITOrderDetailExtService extends IService<TOrderDetailExt> {
    void batchUpdateOrderExt(MatchEventInfo event,String linkedId);

    /**
     * 特殊事件处理
     * @param event
     * @param linkedId
     */
    void specEventHandler(MatchEventInfo event, String linkedId);

    /**
     * 赛事切换特殊事件
     * @param currentEventCode
     * @param matchId
     * @param linkId
     * @param isHome 是否是主队事件
     */
    void changeSpecEvent(String currentEventCode, Long matchId, String linkId,boolean isHome);

    /**
     * 退出特殊事件
     * @param matchId 赛事ID
     * @param linkId
     */
    void exitSpecEvent(Long matchId, String linkId);
}
