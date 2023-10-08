package com.panda.sport.rcs.mgr.service.orderhide;

import com.panda.sport.rcs.vo.TOrderHide;

import java.util.List;

/**
 * <p>
 * 藏单 服务类
 * </p>
 *
 * @author skyKong
 * @since 2019-9-12
 */
public interface ITOrderHideService {
  int insertOrUpdate(TOrderHide orderHide);

  int insertOrUpdates(List<TOrderHide> orderHides);
}
