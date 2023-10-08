package com.panda.sport.rcs.vo;

import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author :  enzo
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.vo
 * @Description :  TODO
 * @Date: 2020-01-15 11:56
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MatchMarketTradeTypeVo implements Serializable {

    private Long matchId;

    /**
     * 标准赛事的id. 对应 standard_match_info.id
     */
    private Integer tradeType;

    private Integer level;

    private List<RcsMatchMarketConfig> configs;

}
