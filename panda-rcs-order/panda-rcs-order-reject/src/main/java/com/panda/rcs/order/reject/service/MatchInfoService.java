package com.panda.rcs.order.reject.service;

/**
 * @author admin
 */
public interface MatchInfoService {
    /**
     * 获取var开关状态
     */
    boolean getVarSwitchStatus(String matchId);

    boolean getVarSwitchStatus(String matchId, String orderNo);

    /**
     * VAR订单发送等待或拒单状态
     */
    void sendVarOrderStatus(String linkId, String matchId, String sportId, Integer varOrderStatus);

    /**
     * 修改缓存var收单状态
     */
    void updateVarAccept(String matchId, String varStatus);

    /**
     * 查询缓存var收单状态
     */
    String getVarAccept(String matchId);
}
