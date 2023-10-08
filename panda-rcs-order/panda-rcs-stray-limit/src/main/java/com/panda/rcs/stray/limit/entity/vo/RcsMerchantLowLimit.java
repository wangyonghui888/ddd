package com.panda.rcs.stray.limit.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.panda.rcs.stray.limit.entity.enums.SeriesEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 单日额度用完最低可投注金额配置
 * </p>
 *
 * @author joey
 * @since 2022-04-02
 */
@Data
public class RcsMerchantLowLimit implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    /**
     * 单注赔付限额类型 M串N = M*1000+N
     * 2001 2串1
     * 3001 3串1
     * 3004 3串4
     * 4001 4串1
     * 40011 4串11
     * 5001 5串1
     * 50026 5串26
     * 6001 6串1
     * 60057 6串57
     * 7001 7串1
     * 700120 7串120
     * 8001  8串1
     * 800247 8串247
     * 9001  9串1
     * 900502 9串502
     * 10001 10串1
     * 10001013 10串1013
     * {@link SeriesEnum}
     */
    @ApiModelProperty("单注赔付限额类型 M串N = M*1000+N")
    private Integer strayType;


    @ApiModelProperty("单日额度用完最低可投注金额")
    private BigDecimal minAmount;

    /**
     * 是否启用 0 、启用 1、未启用
     */
    private Integer status;

    private Date createTime;

    private Date updateTime;

    /**
     * 操作人IP
     */
    @TableField(exist = false)
    private String ip;
}
