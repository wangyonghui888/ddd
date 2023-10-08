package com.panda.sport.rcs.pojo.dao;

import com.panda.sport.rcs.pojo.RcsMarketChampionExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author minho
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RcsMarketChampionExtVO extends RcsMarketChampionExt {

    /**
     * 操盘操作配置状态
     * 0:active 开, 1:suspended 封, 2:deactivated 关, 11:锁  空表示没操作',
     */
    private Integer status;
}