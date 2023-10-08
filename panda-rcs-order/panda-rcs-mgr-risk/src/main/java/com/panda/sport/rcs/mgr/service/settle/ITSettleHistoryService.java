package com.panda.sport.rcs.mgr.service.settle;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.data.rcs.dto.SettleItem;
import com.panda.sport.rcs.pojo.settle.TSettleHistory;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.service
 * @Description :  结算明细
 * @Date: 2020-11-28 下午 3:00
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ITSettleHistoryService extends IService<TSettleHistory> {
    /**
     * 保存
     * @param msg
     */
    void insert(SettleItem msg);

}
