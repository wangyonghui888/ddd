package com.panda.sport.rcs.pojo.virtual;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 虚拟赛事-第三方订单记录
 * </p>
 *
 * @author lithan
 * @since 2020-12-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RcsVirtualOrderExt implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * panda系统用户ID
     */
    private Long userId;

    /**
     * 第三方用户ID
     */
    private Integer virtualUserId;

    /**
     * panda订单号
     */
    private String orderNo;

    /**
     * 第三方订单号(订单成功了才会返回)
     */
    private Long ticketId;

    /**
     * 第三方transactionId(订单成功了才会返回)
     */
    private Long transactionId;

    /**
     * 请求第三方参数
     */
    private String requestParam;

    /**
     * 第三方返回结果
     */
    private String responseParam;

    /**
     * 响应状态
     */
    private String responseStatus;

    /**
     * 订单状态: (0 待处理  1：成功  2：拒绝)
     */
    private Integer orderStatus;

    /**
     * 备注(主要记录失败原因)
     */
    private String remark;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 系统内部金额
     */
    private Long paAmount;

    /**
     * 给第三方接口的金额
     */
    private Long virtualAmount;
}
