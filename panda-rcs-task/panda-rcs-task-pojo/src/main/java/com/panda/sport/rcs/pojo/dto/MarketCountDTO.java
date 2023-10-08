package com.panda.sport.rcs.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author :  Enzo
 * @Project Name :  panda-rcs-task-group
 * @Package Name :  com.panda.sport.rcs.pojo.dto
 * @Description :  盘口数实体类
 * @Date: 2020-09-13 13:45
 * @ModificationHistory
 * --------  ---------  --------------------------
 */
@Data
public class MarketCountDTO implements Serializable {
    /**
     * 赛事ID
     */
    private Long matchId;
    /**
     * 玩法ID
     */
    private Integer playId;
    /**
     * 盘口数量
     */
    private Integer marketCount;
    /**
     * 盘口类型
     */
    private String marketType;
    /**
     * margain值
     */
    private String margain;
    /**
     * 赔率（水差）变动幅度
     */
    private BigDecimal oddsAdjustRange;
    /**
     * 盘口调整幅度
     */
    private BigDecimal marketAdjustRange;
    /**
     * 数据源
     */
    private String dataSource;

}
