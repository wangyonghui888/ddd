package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.pojo
 * @Description :  TODO
 * @Date: 2020-09-12 9:42
 * @ModificationHistory Who    When    What
 * 用户单注单关限额
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RcsQuotaUserSingleNote extends RcsBaseEntity<RcsQuotaUserSingleNote> {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 体育种类
     */
    private Integer sportId;
    /**
     * 0 早盘 1滚球
     */
    private Integer betState;
    /**
     * 玩法类型
     */
//    private Integer playType;
    /**
     * 玩法id
     */
    private Integer playId;
    /**
     * 限额基础值
     */
    private Long quotaBase;
    /**
     * 单注投注限额比例 0.0001-10
     */
    private BigDecimal singleBetLimitRatio;
    /**
     * 单注赔付限额
     */
    @TableField(exist = false)
    private BigDecimal singlePayLimit;
    /**
     * 单注投注限额
     */
    private BigDecimal singleBetLimit;
    /**
     * 玩法累计赔付比例 0.0001-10
     */
    private BigDecimal cumulativeCompensationPlayingRatio;
    /**
     * 玩法累计赔付
     */
    private BigDecimal cumulativeCompensationPlaying;
    /**
     * 0未生效 1生效
     */
    private Integer status;

    /**
     * 操作人IP
     */
    @TableField(exist = false)
    private String ip;
}
