package com.panda.sport.data.rcs.api;

import com.panda.sport.data.rcs.dto.OrderBean;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.rpc
 * @Description :  订单推送
 * @Date: 2019-11-04 17:19
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface OrderBetRecordNotifyService {
    /**
     * 订单状态变化通知
     * @param orderBean
     * @return
     */
    Response statusChanged(OrderBean orderBean);
}
