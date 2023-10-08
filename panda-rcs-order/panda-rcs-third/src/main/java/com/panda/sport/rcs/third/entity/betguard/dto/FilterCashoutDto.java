package com.panda.sport.rcs.third.entity.betguard.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.apache.poi.hpsf.Decimal;

import java.io.Serializable;

/**
 * @author Beulah
 * @date 2023/3/21 11:43
 * @description 外部请求传参
 */

@ToString
@Builder
@Data
public class FilterCashoutDto implements Serializable {

    private static final long serialVersionUID = 1L;

    //Bet Id
    private Long BetId;

    //Indicates Cashout Amount
    private Decimal Price;

}
