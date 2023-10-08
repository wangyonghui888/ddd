package com.panda.sport.rcs.third.entity.gts;

import lombok.Data;

import java.io.Serializable;

/**
 * gts 接拒原因
 * @author z9-lithan
 * @date 2023-01-06 15:21:20
 */
@Data
public class GtsRejectReasonVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * An extensible list of possible rejection reasons.
     * Possible values
     * PriceChanged
     * SelectionClosed
     * SelectionSuspended
     * LiabilityLimitBreached
     * UserLimitBreached
     * LimitNotSet
     * MaxStakeBreached
     */
    private String reasonCode;
    /**
     *  Description of the rejection reason.
     */
    private String reasonMessage;


}
