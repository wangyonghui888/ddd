package com.panda.sport.rcs.pojo.virtual;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 虚拟赛事-第三方用户信息表
 * </p>
 *
 * @author lithan
 * @since 2020-12-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RcsVirtualUser implements Serializable {

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
     * 第三方用户名(对应panda的userid)
     */
    private String virtualUserName;

    /**
     * 第三方ext_id
     */
    private String virtualExtId;

    /**
     * 第三方用户状态
     */
    private String virtualUserStatus;

    /**
     * 第三方用户上级ID
     */
    private Integer virtualParentId;

    /**
     * 下注需要使用到的ID
     */
    private Integer calculationId;

    /**
     * 钱包ID
     */
    private Long walletId;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * pandan备用字段 用户状态(0 禁用 1启用)
     */
    private Integer pandaStatus;

    /**
     * 备注
     */
    private String remark;


}
