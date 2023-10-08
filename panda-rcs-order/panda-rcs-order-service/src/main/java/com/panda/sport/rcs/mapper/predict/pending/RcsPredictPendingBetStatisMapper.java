package com.panda.sport.rcs.mapper.predict.pending;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.vo.api.response.BetForMarketResVo;
import com.panda.sport.rcs.pojo.vo.predict.pending.RcsPredictPendingBetStatis;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RcsPredictPendingBetStatisMapper extends BaseMapper<RcsPredictPendingBetStatis> {
    List<BetForMarketResVo> selectBetForMarket(@Param("matchId") Long matchId, @Param("playId") Integer playId, @Param("sportId") Integer sportId
            , @Param("matchType") Integer matchType, @Param("oddsItemForHome") String oddsItemForHome, @Param("oddsItemForAway") String oddsItemForAway);
}
