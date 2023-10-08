package com.panda.sport.rcs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.predict.RcsPredictBetOddsMapper;
import com.panda.sport.rcs.pojo.vo.api.response.BetForPlaceResVo;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictBetOdds;
import com.panda.sport.rcs.service.IRcsPredictBetOddsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 投注项/坑位-期望值/货量 服务实现类
 * </p>
 *
 * @author author
 * @since 2021-02-19
 */
@Service
public class RcsPredictBetOddsServiceImpl extends ServiceImpl<RcsPredictBetOddsMapper, RcsPredictBetOdds> implements IRcsPredictBetOddsService {

    @Autowired
    private RcsPredictBetOddsMapper mapper;

    @Override
    public List<BetForPlaceResVo> selectBetForPlace(Long matchId, Integer playId, Integer sportId, Integer matchType, String oddsTypeForHome, String oddsTypeForAway) {
        return mapper.selectBetForPlace(matchId,playId,sportId,matchType,oddsTypeForHome,oddsTypeForAway);
    }
}
