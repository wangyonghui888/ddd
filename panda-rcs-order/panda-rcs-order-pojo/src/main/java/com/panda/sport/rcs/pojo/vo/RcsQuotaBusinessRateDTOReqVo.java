package com.panda.sport.rcs.pojo.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class RcsQuotaBusinessRateDTOReqVo extends RcsQuotaBusinessRateDTO implements Serializable {

    /**
     * 当前是第几页
     */
    private Integer current;

    /**
     * 每一页大小
     */
    private Integer size;



    public Integer getCurrent() {
        if (this.current == null) {
            return 1;
        }
        return this.current;
    }

    public Integer getSize() {
        if (this.size == null) {
            return 15;
        }
        return this.size;
    }
}
