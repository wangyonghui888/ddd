package com.panda.sport.rcs.mgr.operation.order;

import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.mgr.operation.settlement.CalcSettled;
import org.apache.dubbo.rpc.RpcException;


/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.rpc.operation.order
 * @Description :  订单处理-统计订单
 * @Date: 2019-10-26 16:38
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 * 测试数据
 * {"deviceType":1,"orderNo":"d415cef457ed43d4a170322290acb82f","orderStatus":0,"productCount":1,"productAmountTotal":1,"modifyUser":"系统","uid":100001,"modifyTime":1571044722,"createTime":1571044722,"seriesType":0,"tenantId":1,"createUser":"系统","orderAmountTotal":1,"items":[{"matchType":1,"marketType":"1","playId":1,"playOptionsId":101,"uid":100101,"oddsValue":2.2000000000000003E-5,"matchId":2019100801,"scoreBenchmark":"00","orderNo":"order_00001","betNo":"10000000001","isValid":1,"marketValue":"1","matchProcessId":1,"maxWinAmount":1000.0,"betAmount":200,"sportId":1,"marketId":10}]}
 */
public interface CalcOrder extends Comparable<CalcOrder> {
    Integer order=0;
    /**
     * @Description   计算
     * @Param [orderBean]
     * @Author toney
     * @Date  14:38 2019/10/25
     * @return void
     **/
    void orderHandle(OrderBean orderBean, Integer type) throws RpcException;


}
