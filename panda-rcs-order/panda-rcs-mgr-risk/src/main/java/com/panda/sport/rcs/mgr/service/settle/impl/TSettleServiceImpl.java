package com.panda.sport.rcs.mgr.service.settle.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.data.rcs.dto.OrderDetailPO;
import com.panda.sport.data.rcs.dto.SettleItem;
import com.panda.sport.rcs.mapper.settle.TSettleDetailMapper;
import com.panda.sport.rcs.mapper.settle.TSettleMapper;
import com.panda.sport.rcs.mgr.service.settle.ITSettleService;
import com.panda.sport.rcs.pojo.settle.TSettle;
import com.panda.sport.rcs.pojo.settle.TSettleDetail;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 结算表 服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-07
 */
@Service
public class TSettleServiceImpl extends ServiceImpl<TSettleMapper, TSettle> implements ITSettleService {
  @Autowired
  private TSettleMapper mapper;

  @Autowired
  private TSettleDetailMapper settleDetailMapper;

  /**
   * @Description   保存或者更新
   * @Param [settleItem]
   * @Author  myname
   * @Date   2020/12/26
   * @return void
   **/
  
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void saveOrUpdate(SettleItem settleItem) {
    OrderDetailPO orderDetail = settleItem.getOrderDetailRisk().get(0);
    TSettle bean = new TSettle();
    BeanUtils.copyProperties(settleItem, bean);
    bean.setSettleStatus(settleItem.getPayoutStatus());
    bean.setBetAmount(orderDetail.getBetAmount());
    bean.setOddFinally(Double.parseDouble(orderDetail.getOddFinally()));
    bean.setCalcStatus(0);
    bean.setOddsValue(settleItem.getOddsValue());
    bean.setProfitAmount((settleItem.getSettleAmount() - settleItem.getBetAmount()) * -1);
    bean.setOperateStatus(1);
    bean.setOperateTime(settleItem.getSettleTime());
    insertOrUpdate(bean);


    List<TSettleDetail> settleDetailList = new ArrayList<>();
    for (OrderDetailPO po : settleItem.getOrderDetailRisk()) {
      TSettleDetail settleDetail = new TSettleDetail();
      BeanUtils.copyProperties(po, settleDetail);
      settleDetail.setMarketValue(po.getMarketValue());
      settleDetail.setPlayId(po.getPlayId());

      settleDetail.setSettleScore(po.getSettleScore());
      settleDetail.setMarketValue(po.getMarketValueNew());
      settleDetail.setCreateTime(System.currentTimeMillis());
      settleDetail.setModifyTime(System.currentTimeMillis());
      settleDetailList.add(settleDetail);
    }
    settleDetailMapper.bathInsertOrUpdate(settleDetailList);
  }

  @Override
  public int insertOrUpdate(TSettle settle) {
    return mapper.insertOrUpdate(settle);
  }


  /**
   * @Author toney
   * @Date 2020/12/10 上午 10:34
   * @Description 更新状态
   * @param orderNo
   * @param operateStatus
   * @param operateTime
   * @Return int
   * @Exception
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public int updateOperateStatus(String orderNo, Integer operateStatus, Long operateTime){
    return mapper.updateOperateStatus(orderNo,operateStatus,operateTime);
  }
}
