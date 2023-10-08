package com.panda.sport.rcs.mapper.predict;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.vo.api.response.BetForPlaceResVo;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictBetOdds;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 投注项/坑位-期望值/货量 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2021-02-19
 */
@Repository
public interface RcsPredictBetOddsMapper extends BaseMapper<RcsPredictBetOdds> {

    void insertOrUpdate(@Param("list") List<RcsPredictBetOdds> list);

    List<BetForPlaceResVo> selectBetForPlace(@Param("matchId")Long matchId, @Param("playId")Integer playId, @Param("sportId")Integer sportId,
                                             @Param("matchType")Integer matchType, @Param("oddsTypeForHome")String oddsItemForHome, @Param("oddsTypeForAway")String oddsItemForAway);


    void updateData(RcsPredictBetOdds item);

}
