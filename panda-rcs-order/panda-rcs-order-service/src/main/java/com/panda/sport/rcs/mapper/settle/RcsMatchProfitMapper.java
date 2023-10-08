package com.panda.sport.rcs.mapper.settle;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsMatchProfit;
import com.panda.sport.rcs.pojo.vo.RcsMatchProfitVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mapper
 * @Description :  统计盈利金额
 * @Date: 2020-12-02 下午 2:07
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Repository
public interface RcsMatchProfitMapper extends BaseMapper<RcsMatchProfit> {
  /**
   * 添加或者更新
   * @param rcsMatchProfit
   * @return
   */
  int inserOrUpdate(RcsMatchProfit rcsMatchProfit);

  /**
   * @Author toney
   * @Date 2020/12/10 下午 4:43
   * @Description 更新盈利金额
   * @param profitAmount
   * @param matchType
   * @param marketId
   * @Return int
   * @Exception
   */
  int updateProfitAmount(@Param("profitAmount") BigDecimal profitAmount, @Param("matchType") Integer matchType, @Param("marketId") Long marketId);

  List<RcsMatchProfitVo> selectRcsMatchProfitByPlayId(@Param("matchId") Integer matchId, @Param("playIds") List<Integer> playIds, @Param("matchType") Integer matchType);

  BigDecimal selectRcsMatchProfitByMatchId(@Param("matchId") Integer matchId);
}
