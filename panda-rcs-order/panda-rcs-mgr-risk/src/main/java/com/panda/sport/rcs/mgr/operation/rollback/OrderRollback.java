package com.panda.sport.rcs.mgr.operation.rollback;

import com.panda.sport.data.rcs.dto.OrderBean;
import org.apache.dubbo.rpc.RpcException;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.operation.rollback
 * @Description :  订单回滚
 * @Date: 2020-02-28 10:32
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface OrderRollback {
    /**
     * @Description  回滚操作
     * @Param [orderBean]
     * @Author  toney
     * @Date  10:34 2020/2/28
     * @return void
     **/
    void handle(OrderBean orderBean) throws RpcException;
}
