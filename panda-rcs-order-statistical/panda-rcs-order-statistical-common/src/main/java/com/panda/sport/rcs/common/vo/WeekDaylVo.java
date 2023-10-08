package com.panda.sport.rcs.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 * 周区间
 * </p>
 *
 * @author lithan auto
 * @since 2020-06-26
 */
@ApiModel(value = "周区间 Vo对象", description = "周区间")
public class WeekDaylVo implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "开始天")
    private LocalDate startDay;

    @ApiModelProperty(value = "结束天")
    private LocalDate endDay;

    public LocalDate getStartDay() {
        return startDay;
    }

    public void setStartDay(LocalDate startDay) {
        this.startDay = startDay;
    }

    public LocalDate getEndDay() {
        return endDay;
    }

    public void setEndDay(LocalDate endDay) {
        this.endDay = endDay;
    }
}
