package com.panda.sport.data.rcs.dto.limit;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author :  lithan
 * 用户单场限额
 */
@Data
public class RcsQuotaUserSingleSiteQuotaVo implements Serializable {

    private static final long serialVersionUID = -5874390613281877964L;

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
     * 联赛等级
     */
//    private Integer templateLevel;
    /**
     * 用户单场限额基础值
     */
//    private Long userSingleSiteQuotaBase;
    /**
     * 早盘用户单场限额比例  0.0001-10
     */
//    private BigDecimal earlyUserSingleSiteQuotaProportion;
    /**
     * 早盘用户单场限额
     */
    private BigDecimal earlyUserSingleSiteQuota;
    /**
     * 滚球用户单场限额比例  0.0001-10
     */
//    private BigDecimal liveUserSingleSiteQuotaProportion;
    /**
     * 滚球用户单场限额
     */
    private BigDecimal liveUserSingleSiteQuota;
    /**
     * 0 未生效 1生效
     */
//    private Integer status;

}
