package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 操盘商户设置
 * </p>
 *
 * @author lithan
 * @since 2020-12-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RcsOperateMerchantsSet implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商户id
     */
    private String merchantsId;

    /**
     * 商户编码
     */
    private String merchantsCode;

    /**
     * '操盘设置的商户是否进行计算   0无效 1有效',
     */
    private Integer status;

    /**
     * '商户是否有效   0无效  1有效',
     */
    private Integer validStatus;

    /**
     * 限额类型  1标准限额模式 2信用限额模式,
     */
    private Integer limitType;

    /**
     * 信用模式上级商户ID，为0表示普通商户无上级
     */
    private Long creditParentId;

    /**
     * 信用父代理ID，0表示无父代理或普通商户
     */
    private String creditParentAgentId;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date crteaTime;

    /**
     * 商户对应虚拟赛事的用户id(顶级代理id)
     */
    private Integer virtualParentId;

    /**
     * 代理名称
     */
    private String creditName;
}
