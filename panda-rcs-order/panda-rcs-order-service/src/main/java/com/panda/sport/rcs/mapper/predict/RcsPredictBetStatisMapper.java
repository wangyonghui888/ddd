package com.panda.sport.rcs.mapper.predict;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.vo.ActualVolumeApiVO;
import com.panda.sport.rcs.pojo.vo.ActualVolumeVO;
import com.panda.sport.rcs.pojo.vo.api.response.BetForMarketResVo;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictBetStatis;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 预测货量表 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2021-02-19
 */
@Repository
public interface RcsPredictBetStatisMapper extends BaseMapper<RcsPredictBetStatis> {
    void saveOrUpdate(RcsPredictBetStatis entity);

    List<BetForMarketResVo> selectBetForMarket(@Param("matchId")Long matchId, @Param("playId")Integer playId, @Param("sportId")Integer sportId,
                                               @Param("matchType")Integer matchType, @Param("oddsItemForHome")String oddsItemForHome, @Param("oddsItemForAway")String oddsItemForAway);


    /**
     * @return java.util.List<com.panda.sport.rcs.vo.statistics.RcsPredictBetStatisVo>
     * @Description //根据赛事阶段查询
     * @Param [matchId, playTimeStages, matchType]
     * @Author kimi
     * @Date 2020/10/7
     **/
    List<ActualVolumeVO> getRcsPredictBetStatisVo(@Param("marketId") Long marketId, @Param("matchType") Integer matchType);


    /**
     * @return java.util.List<com.panda.sport.rcs.vo.statistics.RcsPredictBetStatisVo>
     * @Description //根据赛事阶段查询
     * @Param [matchId, playTimeStages, matchType]
     * @Author kimi
     * @Date 2020/10/7
     **/
    List<ActualVolumeApiVO> getRcsPredictBetStatisVo1(@Param("matchId") Integer matchId, @Param("playIds") List<Integer> playIds,
                                                      @Param("matchType") Integer matchType);


}
