package com.panda.sport.data.rcs.dto.dj;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName DjCancelOrderReqVo
 * @Description TODO
 * @Author Administrator
 * @Date 2021/10/20 10:32
 * @Version 1.0
 **/
@Data
public class DjCancelOrderReqVo implements Serializable {

    /**
     * OB订单id
     */
    private String orderNo;

    /**
     * DJ对应的注单号，复试串关，多个子单号用“,”分隔
     * 例如 123434354,243435454,544324223
     */
    private String orderIds;

    /**
     * 注单类型 (1-普通
     * 注单 2-串关注单
     * 3-局内串关注单
     * 4-复合玩法))
     */
    private int orderType;


    /**
     * 取消比赛的原因编码
     * 201-
     * 赔率错误，
     * 202-比赛提前开始
     */
    private int reasonCode;

    /**
     * 当前时间戳(精确
     * 到秒)
     */
    private int time;
}
