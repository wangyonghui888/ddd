package com.panda.sport.rcs.mgr.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsMatchProfit;

import java.math.BigDecimal;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.service
 * @Description :  盈利金额
 * @Date: 2020-12-02 下午 2:09
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface IMatchProfitService extends IService<RcsMatchProfit> {
  /**
   * 添加或者更新
   * @param tbRcsMatchProfit
   * @return
   */
  int inserOrUpdate(RcsMatchProfit tbRcsMatchProfit);


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
  int updateProfitAmount(BigDecimal profitAmount, Integer matchType, Long marketId);
}
