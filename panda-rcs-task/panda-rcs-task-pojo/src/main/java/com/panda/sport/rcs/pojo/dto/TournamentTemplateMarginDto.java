package com.panda.sport.rcs.pojo.dto;

import lombok.Data;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.pojo.dto
 * @Description :  TODO
 * @Date: 2020-09-15 15:26
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class TournamentTemplateMarginDto {
    /**
     * 具体的值
     */
    private Long orderSinglePayVal;
    /**
     * 倍数
     */
    private Integer marketWarn;
    /**
     * 具体的值
     */
    private Integer maxSingleBetAmount;

}
