package com.panda.sport.rcs.pojo.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 赔接拒率参数对象
 *
 * @author gray
 * @version 1.0.0
 * @date 2023/5/7 15:12
 */
@Data
public class OddsChangeDTO {
    
    /**
     * 赔率变动接拒开关（0.关 1.开）
     */
    private Integer oddsChangeStatus;
    
    /**
     * 赔接拒率变动范围
     */
    private BigDecimal oddsChangeValue;
    
    /**
     * 赛事ID
     */
    private Long matchId;
    /**
     * 早盘/滚球
     */
    private Integer matchType;
    /**
     * 玩法ID
     */
    private Integer playId;
    
    /**
     * 赛种ID
     */
    private Integer sportId;
    
}
