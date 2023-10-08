package com.panda.sport.rcs.gts.vo;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

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

}
