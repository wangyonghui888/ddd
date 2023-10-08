package com.panda.sport.rcs.mgr.operation.order;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mgr.mq.impl.trigger.TriggerChangeImpl;
import com.panda.sport.rcs.mgr.operation.order.impl.ProfitMarketServiceImpl;

import lombok.extern.slf4j.Slf4j;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.rpc.operation.order
 * @Description :  订单处理-订单统计
 * @Date: 2019-10-26 16:39
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
public class CalcOrderAdapter {
    /**
     * @Description 相关业务实现类
     * @Param
     * @Author toney
     * @Date 16:45 2019/10/26
     * @return
     **/
    private List<CalcOrder> calcOrderList;


    @Autowired
    private ProfitMarketServiceImpl profitMarketService;

    @Autowired
    private RedisClient redisClient;

    /**
     * @return
     * @Description 初始化
     * @Param [calcOrderList]
     * @Author toney
     * @Date 16:46 2019/10/26
     **/
    public CalcOrderAdapter(List<CalcOrder> calcOrderList) {
        if (calcOrderList == null) {
            return;
        }

        this.calcOrderList = calcOrderList;

        Collections.sort(this.calcOrderList);
    }

    /**
     * @return void
     * @Description 统计数据
     * @Param [orderBean]
     * @Author toney
     * @Date 16:45 2019/10/26
     **/
    public void calc(OrderBean orderBean, Integer type) throws Exception {
    	String isCacle = redisClient.get("rcs:risk:order:calc");

    	if("0".equals(isCacle)) {//不做计算
    		return ;
    	}

        for (CalcOrder calcOrder : calcOrderList) {
            try {
                //增加过滤--------
                if(!(calcOrder instanceof ProfitMarketServiceImpl) &&
                        !(calcOrder instanceof TriggerChangeImpl)){
                    continue;
                }
                //增加过滤--------
                calcOrder.orderHandle(orderBean, type);
            } catch (Exception ex) {
                log.error("::{}::实货量错误：{},{}",orderBean.getOrderNo(),ex.getMessage(), ex);
            }
        }

    }
}
