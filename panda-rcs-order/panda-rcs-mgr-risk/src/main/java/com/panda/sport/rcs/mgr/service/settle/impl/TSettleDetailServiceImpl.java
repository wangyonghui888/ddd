package com.panda.sport.rcs.mgr.service.settle.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.settle.TSettleDetailMapper;
import com.panda.sport.rcs.mgr.service.settle.ITSettleDetailService;
import com.panda.sport.rcs.pojo.settle.TSettleDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.service.impl
 * @Description :  结算明细
 * @Date: 2020-11-28 下午 3:01
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class TSettleDetailServiceImpl  extends ServiceImpl<TSettleDetailMapper, TSettleDetail> implements ITSettleDetailService {
  @Autowired
  private TSettleDetailMapper mapper;


  /**
   * @Author toney
   * @Date 2020/11/28 下午 5:19
   * @Description 批量修改
   * @param list
   * @Return int
   * @Exception
   */
  @Override
  public int bathInsertOrUpdate(List<TSettleDetail> list) {
    return mapper.bathInsertOrUpdate(list);
  }
}
