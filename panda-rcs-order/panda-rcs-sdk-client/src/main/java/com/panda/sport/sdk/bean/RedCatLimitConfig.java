package com.panda.sport.sdk.bean;

import lombok.Data;

import java.util.List;

/**
 * C01限额配置
 * @author vere
 * @date 2023-06-30
 * @version 1.0.0
 */
@Data
public class RedCatLimitConfig {
    /**
     * 是否开启
     */
    private boolean open=false;
    /**
     * 单关单注限额
     */
    private List<RedCatSingleLimitConfig> single;
    /**
     * 单关单注默认配置
     */
    private Long defaultSingle;

    /**
     * 商户单场单注
     */
    private Long merchant;
    /**
     * 用户单场单注
     */
    private Long user;
}
