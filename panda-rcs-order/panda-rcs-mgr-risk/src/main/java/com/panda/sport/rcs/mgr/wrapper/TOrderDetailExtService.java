package com.panda.sport.rcs.mgr.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.TOrderDetailExt;

import java.util.List;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.task.wrapper
 * @Description :  订单明细扩展
 * @Date: 2020-01-31 11:46
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface TOrderDetailExtService extends IService<TOrderDetailExt> {

    void insertOrUpdateTOrderDetailExt(List<TOrderDetailExt> exts);
}
