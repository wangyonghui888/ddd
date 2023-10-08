package com.panda.rcs.logService.dto;

import com.panda.rcs.logService.vo.RcsQuotaBusinessLimitLog;
import lombok.Data;

import java.io.Serializable;

/**
 * 玩法集DTO
 */
@Data
public class RcsBusinessLogReqVo extends RcsQuotaBusinessLimitLog implements Serializable {

    /**
     * 当前是第几页
     */
    private Integer current;

    /**
     * 每一页大小
     */
    private Integer size;

    private String startTime;

    private String endTime;

    private Long totalCount;

    private Integer pageNum;

    private Integer pageSize;

    public Integer getCurrent() {
        if (this.current == null) {
            return 1;
        }
        return this.current;
    }

    public Integer getSize() {
        if (this.size == null) {
            return 10;
        }
        return this.size;
    }
    public Integer getPageNum() {
        if (this.pageNum == null) {
            return 1;
        }
        return this.pageNum;
    }

    public Integer getPageSize() {
        if (this.pageSize == null) {
            return 10;
        }
        return this.pageSize;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

}
