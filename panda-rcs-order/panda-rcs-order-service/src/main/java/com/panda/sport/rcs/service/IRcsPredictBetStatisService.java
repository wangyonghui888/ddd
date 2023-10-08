package com.panda.sport.rcs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.vo.api.response.BetForMarketResVo;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictBetStatis;

import java.util.List;

/**
 * <p>
 * 预测货量表 服务类
 * </p>
 *
 * @author author
 * @since 2021-02-19
 */
public interface IRcsPredictBetStatisService extends IService<RcsPredictBetStatis> {

    List<BetForMarketResVo> selectBetForMarket(Long matchId, Integer playId, Integer sportId, Integer matchType, String oddsItemForHome, String oddsItemForAway);

    List<BetForMarketResVo> selectPendingBetForMarket(Long matchId, Integer playId, Integer sportId, Integer matchType, String oddsItemForHome, String oddsItemForAway);
}
