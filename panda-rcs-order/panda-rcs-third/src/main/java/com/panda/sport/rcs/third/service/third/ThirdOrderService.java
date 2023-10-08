package com.panda.sport.rcs.third.service.third;

import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.bts.ThirdBetParamDto;
import com.panda.sport.rcs.third.entity.common.ThirdOrderExt;
import com.panda.sport.rcs.third.entity.common.ThirdResultVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Beulah
 * @date 2023/3/20 16:23
 * @description 第三方投注接口，包含：
 * 1.请求第三方投注
 * 2.订单确认
 * 3.订单取消
 * 4.商户打折
 * 5.内部接单商户获取
 * 6.获取限额
 * 7.订单结果通知
 */
public interface ThirdOrderService {

    /**
     * 获取最大限额
     */
    Long getMaxBetAmount(ThirdBetParamDto dto);

    /**
     * 投注
     */
    ThirdResultVo placeBet(ThirdOrderExt ext);

    /**
     * 订单确认
     */
    Boolean orderConfirm(ThirdOrderExt ext);

    /**
     * 组装第三方入参
     */
    Object convertThirdParam(ThirdOrderExt ext);

    String updateThirdOrderStatus(OrderBean orderBean,String reason);

    /**
     * 检查接单之前，是否注单被取消
     *
     * @param orderNo 注单号
     * @return 是否被取消
     */
    boolean orderIsCanceled(String orderNo);

    /**
     * 订单入库 - 第三方订单表
     */
    void saveOrder(ThirdOrderExt ext);

    /**
     * 订单更新 - 第三方订单表
     */
    void updateOrder(ThirdOrderExt ext);

    /**
     * 取消注单
     */
    void orderCancel(ThirdOrderExt ext);

    /**
     * 更新取消失败原因
     * @param ext 註單
     * @param reason 錯誤原因
     */
    void updateOrderCancelFailedReason(ThirdOrderExt ext,String reason);

    /**
     * 商户金额打折
     */
    BigDecimal discountAmount(ThirdOrderExt ext);



}
