package com.panda.rcs.pending.order.pojo;

import com.panda.merge.dto.I18nItemDTO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author black
 * @ClassName: StandardMarketOddsMessage
 * @Description: TODO
 * @date 2020年8月11日 上午10:10:52
 * @see com.panda.merge.dto.message.StandardMarketOddsMessage
 */
@Data
public class StandardMarketOddsMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 标准投注项ID
     */
    private Long id;

    /**
     * 盘口ID  standard_sport_market.id
     */
    private Long marketId;


    /**
     * 投注项类型
     */
    private String oddsType;

    /**
     * 投注项赔率. 单位: 0.00001
     */
    private Integer paOddsValue;
}
