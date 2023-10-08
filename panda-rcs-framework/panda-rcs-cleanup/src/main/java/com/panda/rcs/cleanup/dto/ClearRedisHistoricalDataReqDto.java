package com.panda.rcs.cleanup.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Project Name : panda-rcs-framework
 * @Package Name : panda-rcs-framework
 * @Description : 清除Redis历史数据入参
 * @Author : Paca
 * @Date : 2022-03-08 21:47
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class ClearRedisHistoricalDataReqDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 0-清除所有，1-清除位置状态
     */
    private Integer clearType;

    private Long matchIdStart;

    private Long matchIdEnd;

    public Integer getClearType() {
        if (clearType == null) {
            return 0;
        }
        return clearType;
    }

    public Long getMatchIdStart() {
        if (matchIdStart == null) {
            return 1L;
        }
        return matchIdStart;
    }

    public Long getMatchIdEnd() {
        if (matchIdEnd == null) {
            return 0L;
        }
        return matchIdEnd;
    }
}
