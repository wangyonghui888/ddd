package com.panda.sport.rcs.mgr.service.orderhide.impl;

import com.panda.sport.rcs.mgr.service.orderhide.ITOrderHideService;
import com.panda.sport.rcs.mgr.utils.CopyUtils;
import com.panda.sport.rcs.pojo.TOrderHidePO;
import com.panda.sport.rcs.service.dal.TOrderHideDal;
import com.panda.sport.rcs.vo.TOrderHide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 藏单 服务实现类
 * </p>
 *
 * @author skyKong
 * @since 2022-9-12
 */
@Service
public class TOrderHideServiceImpl  implements ITOrderHideService {

  @Autowired
  private TOrderHideDal orderHideDal;

  @Override
  public int insertOrUpdate(TOrderHide orderHide) {
    return orderHideDal.insertOrUpdate(orderHide);
  }

  @Override
  public int insertOrUpdates(List<TOrderHide> list) {
    List<TOrderHidePO> poList= CopyUtils.clone(list,TOrderHidePO.class);
   return  orderHideDal.insertOrUpdates(poList);
  }
}
