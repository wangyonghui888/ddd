package com.panda.sport.rcs.mgr.operation.settlement.impl;

import com.panda.sport.data.rcs.api.OrderPaidApiService;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.dto.SettleItem;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mgr.aspect.RcsLockable;
import com.panda.sport.rcs.mgr.operation.settlement.CalcSettled;
import com.panda.sport.rcs.mgr.paid.annotion.Order;
import com.panda.sport.rcs.pojo.TOrderDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.rpc.operation.settlement.impl
 * @Description :  订单结算派奖
 * @Date: 2019-11-05 21:25
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Order(300)
@Component
public class SettledPrizeServiceImpl extends CalcSettledBase  implements CalcSettled {
    @Autowired
    @Qualifier("orderPaidApiImpl")
    OrderPaidApiService orderPaidApiService;

    /**
     * @Description   处理相关流程
     * @Param [settleItem, orderDetail]
     * @Author  toney
     * @Date  11:35 2019/11/5
     * @return void
     **/
    @Override
    @RcsLockable(key = "SettledPrizeServiceImpl")
    public void settleHandle(SettleItem src, List<TOrderDetail> orderDetailList) throws RpcException {
        if(src==null){
            log.warn("结算对象输入值为空");
            return;
        }
        if(src.getDelFlag() == null) {
            log.warn("::{}::settle del_flag 不可以为 null,data:{}",src.getOrderNo(),src);
            return;
        }
        //设置为派奖
        src.setIsSettled(1);
        Request<SettleItem> requestParam = new Request<SettleItem>();
        //兼容spring更改了是否派彩字段
        src.setSettleStatus(src.getPayoutStatus());
        requestParam.setData(src);
        try {
            orderPaidApiService.updateOrderAfterRefund(requestParam);
        } catch (RcsServiceException e) {
            log.error("::{}::结算消息同步出错code:{},msg:{}",src.getOrderNo(),e.getCode(),e.getErrorMassage(),e);
        } catch (Exception e) {
            log.error("::{}::结算消息同步出错{}",src.getOrderNo(),e.getMessage());
        }
    }
}
