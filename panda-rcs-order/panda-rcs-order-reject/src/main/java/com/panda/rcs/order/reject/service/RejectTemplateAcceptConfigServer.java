package com.panda.rcs.order.reject.service;

import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.PreOrderDetailRequest;
import com.panda.sport.data.rcs.vo.MatchEventInfo;
import com.panda.sport.data.rcs.vo.RcsTournamentTemplateAcceptConfig;
import com.panda.sport.rcs.pojo.reject.RcsGoalWarnSet;

import java.util.List;

/**
 * 本地消费实现类
 */
public interface RejectTemplateAcceptConfigServer {
    RcsTournamentTemplateAcceptConfig queryAcceptConfig(OrderItem orderItem);
    RcsTournamentTemplateAcceptConfig queryAcceptConfig(PreOrderDetailRequest orderItem);

    void setEventId(OrderItem orderItem);
    void setPreSettleEventId(PreOrderDetailRequest orderItem);

    MatchEventInfo getMatchEventInfo(String key, String playSet, OrderItem orderItem);

    String getPlayCollect(OrderItem orderItem);

    String getDataSourceCode(String playSet, Long matchId);

    /**
     * 根据玩法集id获取玩法集code
     * @param logKey
     * @param rcsTournamentTemplateAcceptConfig
     * @return
     */
    String getCategoryPlaySetCodeById(String logKey, RcsTournamentTemplateAcceptConfig rcsTournamentTemplateAcceptConfig);

    /**
     * 根据联赛id,赛事id,球队id查询设置缓存,缓存没有从数据库取
     * @param standardTournamentId
     * @param standardMatchId
     * @param teamId
     * @return
     */
    RcsGoalWarnSet getGoalWarnSet(Long standardTournamentId, Long standardMatchId, String teamId);
//    MatchEventInfo getMatchEventInfo(String key, String playSet, OrderItem orderItem);
//    RcsTournamentTemplateAcceptConfig getConfig(List<RcsTournamentTemplateAcceptConfig> configs, String eventCode);
//     String getPlayCollect(OrderItem orderItem);
}
