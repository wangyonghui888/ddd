package com.panda.sport.rcs.console.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 第三方 虚拟赛事 订单表
 * </p>
 *
 * @author lithan
 * @since 2020-12-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RcsOrderVirtual implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long uid;

    /**
     * 订单状态(0 待处理  1：成功  2：拒绝 3:取消)
     */
    private Integer orderStatus;

    /**
     * 注单项数量
     */
    private Long productCount;

    /**
     * 串关类型(1:单注(默认) M000N:M串N  )
     */
    private Integer seriesType;

    /**
     * 注单总价
     */
    private Long productAmountTotal;

    /**
     * 实际付款金额
     */
    private Long orderAmountTotal;

    /**
     * 1:手机，2：PC
     */
    private Integer deviceType;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 商户id
     */
    private Long tenantId;

    /**
     * 币种编码
     */
    private String currencyCode;

    /**
     * 地区
     */
    private String ipArea;

    /**
     * 第三方返回状态
     */
    private String thirdStatus;

    /**
     * 拒单原因
     */
    private String reason;

    /**
     * 是否vip用户
     */
    private Integer vipLevel;

    /**
     * 投注时间(业务传)
     */
    private Long betTime;

    /**
     * 创建时间(风控处理时间)
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long modifyTime;


}
