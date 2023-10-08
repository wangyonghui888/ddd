package com.panda.rcs.order.reject.entity;
import lombok.Data;

import java.io.Serializable;

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
