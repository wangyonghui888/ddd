package com.panda.sport.data.rcs.api;

/**
 * @author :  holly
 * @Project Name :rcs-parent
 * @Package Name :com.panda.sport.data.rcs.api
 * @Description :
 * @Date: 2019-11-30 18:54
 */
public interface BalanceValueService {
    /**
     *
     * @param matchId
     * @param marketId
     */
    void zeroBalanceValue(Long matchId, Long marketId);
}
