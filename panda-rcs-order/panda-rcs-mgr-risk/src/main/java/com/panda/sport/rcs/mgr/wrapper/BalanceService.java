package com.panda.sport.rcs.mgr.wrapper;

import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.vo.statistics.BalanceVo;
import com.panda.sport.rcs.vo.statistics.MarketBalanceVo;

/**
 * @author :  kimi
 * @Project Name :  daima1
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.impl
 * @Description :  TODO
 * @Date: 2019-10-30 16:37
 */
public interface BalanceService {

    /**
     * @return void
     * @Description //更新平衡值
     * @Param [matchId 赛事id  marketId盘口Id , balance 变化后的平衡值]
     * @Author kimi
     * @Date 2019/10/30
     **/
    void updateBalance(Long matchId, Long marketId, MarketBalanceVo balance, RcsMatchMarketConfig result);

    /**
     * 查询平衡值
     *
     * @param balanceType   平衡值类型，1-跳赔平衡值，2-跳盘平衡值
     * @param dateExpect    赛事账务日
     * @param keySuffix     足球-盘口ID，篮球-位置ID
     * @param balanceOption 累计差额计算方式，0-投注额差值，1-投注/赔付混合差值
     * @param balanceVo     平衡值
     */
    void queryBalance(Integer balanceType, String dateExpect, String keySuffix, Integer balanceOption, BalanceVo balanceVo);

//    /**
//     * 清除玩法下所有平衡值，暂不支持足球
//     *
//     * @param balanceType 平衡值类型，1-跳赔平衡值，2-跳盘平衡值
//     * @param sportId     赛种ID
//     * @param matchId     赛事ID
//     * @param playId      玩法ID
//     * @param dateExpect  赛事账务日
//     */
//    void clearAllBalance(Integer balanceType, Long sportId, Long matchId, Long playId, String dateExpect);
    /**
     * 清除玩法下所有平衡值，暂不支持足球
     *
     * @param balanceType 平衡值类型，1-跳赔平衡值，2-跳盘平衡值
     * @param sportId     赛种ID
     * @param matchId     赛事ID
     * @param playId      玩法ID
     * @param dateExpect  赛事账务日
     */
    void clearAllBalance(Object ... args);
}
