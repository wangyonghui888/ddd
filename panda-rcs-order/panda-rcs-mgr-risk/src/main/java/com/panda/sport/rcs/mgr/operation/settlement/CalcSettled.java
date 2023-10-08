package com.panda.sport.rcs.mgr.operation.settlement;

import com.panda.sport.data.rcs.dto.SettleItem;
import com.panda.sport.rcs.pojo.TOrderDetail;

import java.util.List;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.rpc.operation
 * @Description :  结算处理-统计订单
 * @Date: 2019-10-26 16:38
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 * 测试数据
 * {"deviceType":1,"orderNo":"d415cef457ed43d4a170322290acb82f","orderStatus":0,"productCount":1,"productAmountTotal":1,"modifyUser":"系统","uid":100001,"modifyTime":1571044722,"createTime":1571044722,"seriesType":0,"tenantId":1,"createUser":"系统","orderAmountTotal":1,"items":[{"matchType":1,"marketType":"1","playId":1,"playOptionsId":101,"uid":100101,"oddsValue":2.2000000000000003E-5,"matchId":2019100801,"scoreBenchmark":"00","orderNo":"order_00001","betNo":"10000000001","isValid":1,"marketValue":"1","matchProcessId":1,"maxWinAmount":1000.0,"betAmount":200,"sportId":1}]}
 */
public interface CalcSettled extends Comparable<CalcSettled>{
    /**
     * @Description   处理相关流程
     * @Param [settleItem, orderDetail]
     * @Author  toney
     * @Date  11:30 2019/11/4
     * @return void
     **/
    void settleHandle(SettleItem settleItem, List<TOrderDetail> orderDetail);
}
