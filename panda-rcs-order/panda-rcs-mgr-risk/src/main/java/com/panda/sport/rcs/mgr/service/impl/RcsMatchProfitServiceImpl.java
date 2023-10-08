package com.panda.sport.rcs.mgr.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.settle.RcsMatchProfitMapper;
import com.panda.sport.rcs.mgr.service.IMatchProfitService;
import com.panda.sport.rcs.pojo.RcsMatchProfit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.service.impl
 * @Description :  盈利金额
 * @Date: 2020-12-02 下午 2:09
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class RcsMatchProfitServiceImpl extends ServiceImpl<RcsMatchProfitMapper, RcsMatchProfit> implements IMatchProfitService {
  @Autowired
  private RcsMatchProfitMapper matchProfitMapper;
  /**
   * 添加或者更新
   * @param rcsMatchProfit
   * @return
   */
  @Override
  public int inserOrUpdate(RcsMatchProfit rcsMatchProfit){
    return matchProfitMapper.inserOrUpdate(rcsMatchProfit);
  }

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
  public int updateProfitAmount(BigDecimal profitAmount, Integer matchType, Long marketId){
    return matchProfitMapper.updateProfitAmount(profitAmount,matchType,marketId);
  }
}
