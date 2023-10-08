package com.panda.sport.rcs.third.service.handler;

import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.third.entity.common.ThirdOrderExt;
import com.panda.sport.rcs.third.entity.common.ThirdResultVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Beulah
 * @date 2023/3/21 19:09
 * @description 对接数据商的时候   订单消费模式
 */
public interface IOrderHandlerService {

    /**
     * 内部接单
     */
    void orderByPa(ThirdOrderExt ext);

    /**
     * 缓存接单
     */
    void orderByCache(ThirdOrderExt ext);

    /**
     * 三方接单
     */
    void orderByThird(ThirdOrderExt ext);

    /**
     * 判断是否走缓存接单
     *
     * @param ext       sdk订单传参
     * @return 是否走缓存接单
     */
    boolean orderIsCache(ThirdOrderExt ext);


    /**
     * 更新订单 - 内部
     *
     */
    void updateOrder(ThirdOrderExt ext, Integer infoStatus, String infoMsg, Integer mtsIsCache);

    /**
     * 订单确认
     *
     * @param ext 订单
     */
    void notifyThirdUpdateOrder(ThirdOrderExt ext);

    /**
     * 延迟队列参数设置
     *
     */
    void addDelayOrder(ThirdOrderExt ext);

    /**
     * MtsIsCache定义
     * @param third 第三方标识
     * @param num 策略标识
     */
    int getMtsIsCache(String third, int num);

    void checkOrderAfter(ThirdResultVo resultVo,ThirdOrderExt ext);



}
