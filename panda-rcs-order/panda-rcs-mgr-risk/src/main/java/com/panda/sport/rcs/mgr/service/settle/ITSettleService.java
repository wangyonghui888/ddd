package com.panda.sport.rcs.mgr.service.settle;


import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.data.rcs.dto.SettleItem;
import com.panda.sport.rcs.pojo.settle.TSettle;

/**
 * <p>
 * 结算表 服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-07
 */
public interface ITSettleService extends IService<TSettle> {
  /**
   * @Description   保存更新数据
   * @Param [settleItem]
   * @Author  myname
   * @Date   2020/12/26
   * @return void
   **/
  
  void saveOrUpdate(SettleItem settleItem);
  /**
   * @Author toney
   * @Date 2020/12/10 上午 11:06
   * @Description 添加或者更新
   * @param settle
   * @Return int
   * @Exception
   */
  int insertOrUpdate(TSettle settle);

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
  int updateOperateStatus(String orderNo, Integer operateStatus, Long operateTime);

}
