package com.panda.sport.rcs.pojo.odd;

import com.panda.merge.dto.I18nItemDTO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 
* @ClassName: StandardMarketOddsMessage 
* @Description: TODO
* @author black  
* @date 2020年8月11日 上午10:10:52 
*
 */
@Data
public class CashoutOddsMessage implements Serializable{
	private static final long serialVersionUID = 1L;

    /**
     * 标准投注项ID
     */
    private Long id;
    /**
     * 该字段用于做风控时, 需要替换成风控服务商提供的投注项id.  如果数据源发生切换, 当前字段需要更新.
     */
    private String thirdOddsFieldSourceId;

    /**
     * 投注项类型
     */
    private String oddsType;

    /**
     *
     */
    private BigDecimal probabilities;

}

