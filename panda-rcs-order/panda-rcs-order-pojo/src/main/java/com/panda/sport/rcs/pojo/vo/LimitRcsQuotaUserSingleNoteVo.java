package com.panda.sport.rcs.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author :  lithan
 * 用户单注单关限额
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LimitRcsQuotaUserSingleNoteVo  implements Serializable {

    private static final long serialVersionUID = -4690304095761410853L;

    /**
     * 赛事id
     */
    private Long matchId;
    /**
     * 主键
     */
//    private Long id;
    /**
     * 体育种类
     */
//    private Integer sportId;
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
//    private Long quotaBase;
    /**
     * 单注投注限额比例 0.0001-10
     */
//    private BigDecimal singleBetLimitRatio;
    /**
     * 单注赔付限额
     */
    private BigDecimal singlePayLimit;
    /**
     * 单注投注限额
     */
    private BigDecimal singleBetLimit;
    /**
     * 玩法累计赔付比例 0.0001-10
     */
//    private BigDecimal cumulativeCompensationPlayingRatio;
    /**
     * 玩法累计赔付
     */
    private BigDecimal cumulativeCompensationPlaying;
    /**
     * 0未生效 1生效
     */
//    private Integer status;

}
