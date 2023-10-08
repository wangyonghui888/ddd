package com.panda.sport.rcs.limit.vo;

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
     */
    private Integer seriesType;


    private BigDecimal seriesLimitAmount;


    private Integer status;

    private Date createTime;

    private Date updateTime;
}
