package com.panda.sport.rcs.pojo.dj;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName RcsDjOrder
 * @Description TODO
 * @Author Administrator
 * @Date 2021/9/22 12:01
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RcsDjOrder implements Serializable {
    private static final long serialVersionUID = 1730480276637607973L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户id
     */
    private Long uid;

    /**
     * 订单状态
     * 0:待处理
     * 1:成功
     * 2:拒绝
     */
    private Integer orderStatus;

    /**
     * 注单项数量
     */
    private Integer productCount;

    /**
     * 串关类型
     */
    private Integer seriesType;

    /**
     * 注单总价
     */
    private Long productAmountTotal;

    /**
     * 设备类型 1:手机，2：PC
     */
    private Integer deviceType;

    /**
     * ip
     */
    private String ip;

    /**
     * 三方注单状态
     */
    private String thirdStatus;

    /**
     * 拒单原因
     */
    private String reason;

    /**
     * 0 : 非VIP  1 :vip
     */
    private Integer vipLevel;

    /**
     * 投注时间
     */
    private Long betTime;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long modifyTime;

}
