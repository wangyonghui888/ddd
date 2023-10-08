package com.panda.sport.rcs.vo;

import lombok.Data;

import java.util.Collection;

/**
 * @program: trade
 * @description:
 * @author: Deven
 * @create: 2023-07-03 11:18
 **/
@Data
public class RedCatLimitVo {
    private int matchLength;
    private int redcatLimitSingle;
    private int redcatLimitMerchant;
    private int redcatLimitUser;
}
