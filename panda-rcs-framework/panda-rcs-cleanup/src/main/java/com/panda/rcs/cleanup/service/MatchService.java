package com.panda.rcs.cleanup.service;

public interface MatchService {

    void cleanupMatchBusiData();

    void cleanupNotExistMatchLinkData();

    void clearRedisByMatchId(Long matchId,int clearType);

    /**
     * 删除已经开售的赛事接距缓存
     */
    void deleteAcceptConfig();
}