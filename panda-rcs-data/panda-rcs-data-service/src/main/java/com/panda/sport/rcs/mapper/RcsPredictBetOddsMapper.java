package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.BetForPlaceResVo;
import com.panda.sport.rcs.pojo.RcsPredictBetOdds;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: xindaima
 * @description:
 * @author: kimi
 * @create: 2021-02-27 18:10
 **/
@Component
public interface RcsPredictBetOddsMapper extends BaseMapper<RcsPredictBetOdds> {
    List<BetForPlaceResVo> selectBetForPlace(@Param("matchId")Long matchId, @Param("playId")Integer playId, @Param("sportId")Integer sportId,
                                             @Param("matchType")Integer matchType, @Param("oddsTypeForHome")String oddsItemForHome, @Param("oddsTypeForAway")String oddsItemForAway);
}