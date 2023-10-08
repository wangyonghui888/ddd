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
 * 单日串关类型赔付总限额
 * </p>
 *
 * @author joey
 * @since 2022-03-27
 */
@Data
public class RcsMerchantLimitCompensation implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 串关赔付类型 2、2串1 3、3串N 4、4串N 5、5串N 6、6串N 7、7串N 8、8串N 9、9串N 10、10串N
     * {@link SeriesTypeEnum}
     */
    @ApiModelProperty("串关赔付类型 2、2串1 3、3串N 4、4串N 5、5串N 6、6串N 7、7串N 8、8串N 9、9串N 10、10串N")
    private Integer seriesType;


    @ApiModelProperty("串关类型赔付总限额")
    private BigDecimal seriesLimitAmount;


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
