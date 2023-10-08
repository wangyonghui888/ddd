package com.panda.rcs.push.entity.vo;

import com.panda.rcs.push.entity.vo.BetForMarketResVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
public class BetForMarketResWsVo extends BetForMarketResVo {

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

}

