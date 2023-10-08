package com.panda.sport.sdk.strategy;

import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.sdk.vo.MatrixForecastVo;
import com.panda.sport.sdk.vo.RcsBusinessPlayPaidConfigVo;

import java.util.List;
import java.util.Map;

/**
 * 策略接口 不同渠道走不同策略处理
 */
public interface IOrderStrategy {
    /**
     * 订单验证接口
     *
     * @param orderBean
     * @param matrixForecastVo
     * @return
     */
    public Map<String, Object> checkOrder(OrderBean orderBean, MatrixForecastVo matrixForecastVo);

    public List<RcsBusinessPlayPaidConfigVo> getMaxBetAmount(List<ExtendBean> list, OrderBean orderBean);
    
    /**
     * 1:风控  2：MTS
     * @return
     */
    public int orderType();
}