package com.panda.sport.rcs.third.entity.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Beulah
 * @date 2023/3/24 16:48
 * @description todo
 */

@Data
public class RcsTournamentTemplateAcceptConfigDto implements Serializable {
    /**
     * T常规
     */
    private Integer normal;


    /**
     * 接单等待时间
     */
    private Integer waitSeconds;

}
