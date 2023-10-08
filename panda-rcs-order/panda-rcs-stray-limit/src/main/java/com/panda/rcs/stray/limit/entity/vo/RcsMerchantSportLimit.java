package com.panda.rcs.stray.limit.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 单日串关赛种赔付限额及派彩限额
 * </p>
 *
 * @author joey
 * @since 2022-03-27
 */
@Data
public class RcsMerchantSportLimit {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 赛种ID
     */

    @ApiModelProperty("赛种ID")
    private Long sportId;


    @ApiModelProperty("赛种赔付限额")
    private BigDecimal strayLimitAmount;


    @ApiModelProperty("是否启用 0、启用 1、未启用")
    private Integer status;

    private Date createTime;

    private Date updateTime;

    /**
     * 操作人IP
     */
    @TableField(exist = false)
    private String ip;
}
