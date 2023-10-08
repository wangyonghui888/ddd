package com.panda.sport.rcs.third.entity.gts;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * gts Bet Assessment api 返回
 *
 * @author z9-lithan
 * @date 2023-01-06 15:21:20
 */
@Data
public class GtsBetAssessmentResVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 是否可以接单
     */
    private Boolean isBetAllowed;
    /**
     * 最大允许限额
     */
    private String maxAllowedStake;
    /**
     * 延迟接单时间
     */
    private String betDelay;
    /**
     * 投注编号
     */
    private String betId;
    /**
     * 投注结果
     */
    private String betAssessmentResult;
    /**
     * 接拒原因
     */
    private GtsRejectReasonVo rejectReason;
    /**
     * 投注项
     */
    List<GtsBetAssessmentLegsVo> legs;

}
