package com.panda.sport.data.rcs.dto.virtual;

import lombok.Data;

import java.util.List;

/**
 * 获取虚拟赛事  投注  请求VO
 *
 * @description:
 * @author: lithan
 * @date: 2020-12-22 14:41:29
 */
@Data
public class BetReqVo implements java.io.Serializable {

    /**
     * 订单号
     */
    String orderNo;

    /**
     * 商户ID 预留扩展使用
     */
    Long tenantId;

    /**
     * 用户ID
     */
    Long userId;

    /**
     * 总金额(分)
     */
    Long totalStake;

    /**
     * 串关类型(1:单注(默认) M000N:M串N  )
     */
    Integer seriesType;

    /**
     * 1:手机，2：PC
     */
    Integer deviceType;

    /**
     * 1:手机，2：PC
     */
    String ip;

    /**
     * 币种编码
     */
    private String currencyCode;

    /**
     * ip地区
     */
    private String ipArea;

    /**
     * 是否vip用户
     */
    private Integer vipLevel;

    /**
     * 投注时间
     */
    private Long betTime;

    /**
     * 注单详情
     */
    List<BetItemReqVo> orderItemList;

    /**
     * 指纹字符串
     */
    private String fpId;

}