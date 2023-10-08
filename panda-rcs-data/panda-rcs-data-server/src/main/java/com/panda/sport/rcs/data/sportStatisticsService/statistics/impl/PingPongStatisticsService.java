package com.panda.sport.rcs.data.sportStatisticsService.statistics.impl;

import com.panda.sport.rcs.data.sportStatisticsService.StatisticsServiceContext;
import com.panda.sport.rcs.data.sportStatisticsService.statistics.AbstractStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PingPongStatisticsService extends AbstractStatisticsService {

    @Override
    public void initial() {
        sportId = 8L;
        StatisticsServiceContext.addStaticsService(this);
    }



}
