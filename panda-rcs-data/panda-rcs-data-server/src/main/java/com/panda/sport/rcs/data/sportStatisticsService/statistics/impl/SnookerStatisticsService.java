package com.panda.sport.rcs.data.sportStatisticsService.statistics.impl;

import com.panda.sport.rcs.data.sportStatisticsService.StatisticsServiceContext;
import com.panda.sport.rcs.data.sportStatisticsService.statistics.AbstractStatisticsService;
import com.panda.sport.rcs.enums.SportIdEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SnookerStatisticsService extends AbstractStatisticsService {

    @Override
    public void initial() {
        sportId = SportIdEnum.SNOOKER.getId();
        StatisticsServiceContext.addStaticsService(this);
    }



}
