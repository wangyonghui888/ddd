package com.panda.sport.rcs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.vo.api.response.BetForPlaceResVo;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictBetOdds;

import java.util.List;

/**
 * <p>
 * 投注项/坑位-期望值/货量 服务类
 * </p>
 *
 * @author author
 * @since 2021-02-19
 */
public interface IRcsPredictBetOddsService extends IService<RcsPredictBetOdds> {

    List<BetForPlaceResVo> selectBetForPlace(Long matchId, Integer playId, Integer sportId, Integer matchType, String oddsTypeForHome, String oddsTypeForAway);
}
