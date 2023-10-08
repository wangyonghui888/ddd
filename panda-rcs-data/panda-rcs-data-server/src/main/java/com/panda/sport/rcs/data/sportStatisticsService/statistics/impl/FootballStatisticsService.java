package com.panda.sport.rcs.data.sportStatisticsService.statistics.impl;

import com.panda.sport.rcs.data.sportStatisticsService.StatisticsServiceContext;
import com.panda.sport.rcs.data.sportStatisticsService.statistics.AbstractStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.stereotype.Service;


/**
 * 事件比分统计
 * @author V
 */
@Service
@Slf4j
@TraceCrossThread
public class FootballStatisticsService extends AbstractStatisticsService {


    @Override
    protected void initial() {
        sportId = 1L;
        StatisticsServiceContext.addStaticsService(this);
    }



}
