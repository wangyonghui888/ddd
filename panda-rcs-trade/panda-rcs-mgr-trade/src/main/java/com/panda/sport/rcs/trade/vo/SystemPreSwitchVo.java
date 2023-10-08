package com.panda.sport.rcs.trade.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统级别提前结算
 */
@Data
public class SystemPreSwitchVo {

    public static final Integer OPEN = 1;
    public static final Integer CLOSE = 0;

    public SystemPreSwitchVo(Integer AO, Integer SR) {
        this.AO = AO;
        this.SR = SR;
    }

    public SystemPreSwitchVo() {
    }

    @JsonProperty(value = "AO")
    private Integer AO;
    @JsonProperty(value = "SR")
    private Integer SR;
}
