package com.panda.sport.rcs.pojo;
import lombok.Data;

import java.io.Serializable;

@Data
public class RcsTournamentTemplateAcceptConfig implements Serializable {
    /**
     * T常规
     */
    private Integer normal;


    /**
     * 接单等待时间
     */
    private Integer waitSeconds;
}
