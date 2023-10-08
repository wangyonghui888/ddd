package com.panda.sport.rcs.pojo.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;

@Data
public class RcsSwitchUpdateVo {

    /**
     * 开关编码, 如LOUDAN
     */
    @NotBlank
    private String switchCode;
    /**
     * 状态 1：开 2：关
     */
    @NotNull
    private Integer status;



}
