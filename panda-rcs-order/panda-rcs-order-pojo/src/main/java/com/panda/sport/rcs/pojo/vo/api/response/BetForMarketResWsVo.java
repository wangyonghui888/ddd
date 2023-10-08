package com.panda.sport.rcs.pojo.vo.api.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 盘口级别货量出参 resVo
 * </p>
 *
 * @author Kir
 * @since 2021-02-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BetForMarketResWsVo extends BetForMarketResVo{

    /**
     * 唯一表示
     */
    String linkId;
    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 玩法ID
     */
    private Integer playId;


    /**
     * 子玩法ID
     */
    private String subPlayId;

}
