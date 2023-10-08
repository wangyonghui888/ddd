package com.panda.sport.rcs.third.entity.beter.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Beulah
 * @date 2023/3/20 16:32
 * @description 限额接口 vo
 */
@Data
public class BeterMaxBetResultVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long amount;
    private String currency;
}

