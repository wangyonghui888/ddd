package com.panda.sport.rcs.oddin.entity.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 电竞对oddin注单实体类
 * @author Z9-conway
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RcsOddinOrder {

    /**
     * 表ID，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

//    private Integer sourceId;
    /**
     * 订单编码
     */
    private String orderNo;

    /**
     * 订单状态
     */
    private String status;

    private String selectionId;

    /**
     * 最大限额
     */
    private Integer exchangeRate;

    /**
     * 请求赔率
     */
    private Integer requesteOdds;

    /**
     * 使用的赔率
     */
    private Integer usedOdds;

    /**
     * 下单拒绝原因
     */
    private String rejectReason;

    /**
     * 撤单拒接原因
     */
    private String cancelRejectionReason;

    /**
     * 撤单拒接信息
     */
    private String cancelRejectionMessage;

    /**
     * 商户id
     */
    private Long tenantId;

    /**
     * 商家折扣率
     */
    private String discount;

    /**
     * 注单请求金额
     */
    private Integer amount;

    /**
     * 注单优惠后实际金额
     */
    private Integer realAmount;

    /**
     * 第三方标志
     */
    private String thirdName;

    /**
     * 股权类型
     */
    private String betStakeType;

    /**
     * 用户id
     */
    private Long uid;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
