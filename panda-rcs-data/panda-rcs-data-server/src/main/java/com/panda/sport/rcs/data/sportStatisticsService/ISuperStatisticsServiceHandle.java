package com.panda.sport.rcs.data.sportStatisticsService;

import java.util.Map;

import com.panda.merge.dto.Request;

/**
 * @author V
 * @description :   运动种类统计功能共用的接口
 * --------  ---------  --------------------------
 */
public interface ISuperStatisticsServiceHandle<T> {

    /**
     * getSportId
     *
     * @return
     */
    Long getSportId();

    /**
     * 标准
     *
     * @param request
     * @return
     */
    Map standardScore(Request<T> request);


}
