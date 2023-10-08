package com.panda.sport.rcs.task.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 系统级别提前结算
 */
@Data
public class SystemPreSwitchVo {

    public static final Integer OPEN = 1;
    public static final Integer CLOSE = 0;

    public SystemPreSwitchVo(Integer AO , Integer SR, Long sportId) {
        this.AO = AO;
        this.SR = SR;
        this.sportId = sportId;
    }

    public SystemPreSwitchVo() {
    }

    @JsonProperty(value = "AO")
    private Integer AO;
    @JsonProperty(value = "SR")
    private Integer SR;
    @JsonProperty(value = "sportId")
    private Long sportId;
}
