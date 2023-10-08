package com.panda.sport.rcs.mgr.service.settle;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.settle.TSettleDetail;

import java.util.List;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.service
 * @Description :  结算明细
 * @Date: 2020-11-28 下午 3:00
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ITSettleDetailService extends IService<TSettleDetail> {
  /**
   * @Author toney
   * @Date 2020/11/28 下午 5:18
   * @Description 批量更新
   * @param list
   * @Return int
   * @Exception
   */
  int bathInsertOrUpdate(List<TSettleDetail> list);
}
