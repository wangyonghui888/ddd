package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description   //特殊用户配置
 * @Param
 * @Author  sean
 * @Date   2020/12/18
 * @return
 **/
@Data
public class RcsUserConfig extends RcsBaseEntity<RcsUserConfig> {
    /**
     * 表ID，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 用户Id
     */
    private Long userId;
    /**
     * 体育种类Id
     */
    private Long sportId;
    /**
     * 操盘Id
     */
    private Long tradeId;
    /**
     * 投注额外延时
     */
    private Integer betExtraDelay;
    /**
     *  是否有投注特殊限额
     */
    private Integer specialBettingLimit;
    /**
     * 备注
     */
    private String remarks;

    /**
     * 时间
     */
    private String updateTime;
    /**
     * 时间
     */
    private String createTime;
    /**
     * 特殊货量
     */
    private Integer  specialVolume;
    /**
     * 是否提前结算  1是  其他否
     */
    private Integer settlementInAdvance;

    /**
     * 标签行情等级ID（赔率分组）
     */
    private String tagMarketLevelId;

    /**
     * 冠军玩法限额比例
     */
    private BigDecimal championLimitRate;
}