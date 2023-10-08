package com.panda.sport.rcs.mapper.settle;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.settle.TSettleDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mapper
 * @Description :  结算明细
 * @Date: 2020-11-28 下午 2:59
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Mapper
public interface TSettleDetailMapper  extends BaseMapper<TSettleDetail> {
  /**
   * @Author toney
   * @Date 2020/11/28 下午 5:16
   * @Description 批量添加
   * @param list
   * @Return int
   * @Exception
   */
  int bathInsertOrUpdate(List<TSettleDetail> list);

}
