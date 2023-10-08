package com.panda.sport.rcs.trade.vo.tourTemplate;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author :  jayz
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.trade.vo.tourTemplate
 * @Description :
 * @Date: 2023-04-21 21:03
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class TournamentTemplatePlayMargainRefScoreVo {
    /**
     * 当局比分
     */
    private String setScore;
    /**
     * 是否进行中 0否 1是
     */
    private Integer isIng;
}
