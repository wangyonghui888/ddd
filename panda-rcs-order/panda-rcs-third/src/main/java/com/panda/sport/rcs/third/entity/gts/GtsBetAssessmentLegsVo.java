package com.panda.sport.rcs.third.entity.gts;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * gts Bet Assessment api 入参
 * @author z9-lithan
 * @date 2023-01-06 15:21:20
 */
@Data
public class GtsBetAssessmentLegsVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 投注项id
     */
    private String selectionId;
    /**
     * 投注项金额
     */
    private BigDecimal price;

    /**
     * Open
     * Suspended
     * Closed
     */
    private String selectionStatus;

    /**
     * 原因
     */
    private GtsRejectReasonVo rejectReason;


}
