package com.panda.sport.rcs.mgr.operation.settlement;

import com.panda.sport.data.rcs.dto.SettleItem;
import com.panda.sport.rcs.mgr.operation.order.CalcOrder;
import com.panda.sport.rcs.pojo.TOrderDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.rpc.operation
 * @Description :  订单统计
 * @Date: 2019-10-26 16:39
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
public class CalcSettledAdapter {
    /*
     * @Description   结算处理-相关业务实现类
     * @Param
     * @Author  toney
     * @Date  10:43 2019/11/4
     * @return
     **/
    private List<CalcSettled> calcSettledList;

    /**
     * @return
     * @Description 初始化
     * @Param [calcOrderList]
     * @Author toney
     * @Date 10:43 2019/11/4
     **/
    public CalcSettledAdapter(List<CalcSettled> calcSettleds) {
        if (calcSettleds == null) {
            return;
        }
        this.calcSettledList = calcSettleds;
        Collections.sort(this.calcSettledList);
        for (CalcSettled calcSettled : calcSettledList) {
            log.info(calcSettled.getClass().toString());
        }
    }

    /**
     * @return void
     * @Description 统计数据
     * @Param [SettleItem]
     * @Author toney
     * @Date 10:43 2019/11/4
     **/
    public void handle(SettleItem settleItem, List<TOrderDetail> orderDetailList) throws RpcException {

        //判断数据是否存在，不存在抛出异常
        if (orderDetailList == null) {
            log.error("::{}::没有找到相关信息", settleItem.getOrderNo());
            throw new RpcException(100001, "结算mq出错:注单:" + settleItem.getBetNo() + "没有找到相关信息");
        }
        //执行业务逻辑
        for (CalcSettled calcSettled : calcSettledList) {
            try {
                calcSettled.settleHandle(settleItem, orderDetailList);
            } catch (Exception ex) {
                log.error("::{}::结算mq出错:{}", settleItem.getOrderNo(), ex.getMessage());
            }
        }
    }

}
