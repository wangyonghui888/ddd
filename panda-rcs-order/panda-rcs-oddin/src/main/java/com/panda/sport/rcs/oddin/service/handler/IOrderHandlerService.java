package com.panda.sport.rcs.oddin.service.handler;


import com.panda.sport.data.rcs.vo.oddin.TicketVo;
import com.panda.sport.rcs.oddin.entity.common.ThirdOrderExt;

/**
 * @author Beulah
 * @date 2023/3/21 19:09
 * @description 对接数据商的时候   订单消费模式
 */
public interface IOrderHandlerService {

    /**
     * 内部接单
     */
    void orderByPa(TicketVo ext);

    /**
     * 缓存接单
     */
    void orderByCache(ThirdOrderExt ext);

    /**
     * 三方接单
     */
    void orderByThird(TicketVo vo);

    /**
     * 判断是否走缓存接单
     *
     * @param ext       sdk订单传参
     * @return 是否走缓存接单
     */
    boolean orderIsCache(ThirdOrderExt ext);

    /**
     * 订单入库 - 第三方订单表
     */
//    void saveThirdOrder(ThirdOrderExt ext);

    /**
     * 订单更新 - 第三方订单表
     */
//    void updateThirdOrder(ThirdOrderExt ext);

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

    int getMtsIsCache(String third, int num);

}
