package com.panda.rcs.stray.limit.entity.vo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.panda.rcs.stray.limit.entity.enums.SeriesTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 高风险单注赔付限额
 * </p>
 *
 * @author jeoy
 * @since 2022-03-27
 */
@Data
public class RcsMerchantHighRiskLimit implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long id;

    @ApiModelProperty("联赛等级  1 - 20 区间 -1  表示未评级")
    private Integer tournamentLevel;


    @ApiModelProperty("赛种")
    private Long sportId;

    /**
     * 串关赔付类型 2、2串1   3、3串N 4、4串N 5、5串N 6、6串N 7、7串N 8、8串N 及以上
     * {@link SeriesTypeEnum }
     */
    @ApiModelProperty("串关赔付类型 2、2串1   3、3串N 4、4串N 5、5串N 6、6串N 7、7串N 8、8串N 及以上")
    private Integer seriesType;


    @ApiModelProperty("串关赔付金额")
    private BigDecimal seriesAmount;

    /**
     * 是否启用 0、启用 1、未启用
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
