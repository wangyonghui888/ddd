package com.panda.sport.data.rcs.dto.dj;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName DJAmountLimitResVo
 * @Description TODO
 * @Author Administrator
 * @Date 2021/9/17 16:16
 * @Version 1.0
 **/
@Data
public class DJAmountLimitResVo implements Serializable {

    private static final long serialVersionUID = -3111848821801902323L;
    /**
     * 最大投注额度
     */
    private Long maxStake;
    /**
     * 最小投注额度
     */
    private Long minStake;

    /**
     * 串关类型
     */
    Integer seriesType;
}
