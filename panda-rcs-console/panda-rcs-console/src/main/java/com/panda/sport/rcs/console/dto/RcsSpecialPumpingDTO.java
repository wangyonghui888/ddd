package com.panda.sport.rcs.console.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RcsSpecialPumpingDTO {
    /**
     * 玩法ID
     */
    private Long playId;

    /**
     * 特殊抽水Spread值json(早盘)
     */
    private String preStr;

    /**
     * 特殊抽水Spread值json(滾球)
     */
    private String liveStr;
}
