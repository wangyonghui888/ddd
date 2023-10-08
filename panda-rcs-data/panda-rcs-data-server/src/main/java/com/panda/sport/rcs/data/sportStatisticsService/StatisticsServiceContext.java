package com.panda.sport.rcs.data.sportStatisticsService;

import com.panda.sport.rcs.data.sportStatisticsService.eventStatistics.IEventServiceHandle;
import com.panda.sport.rcs.data.sportStatisticsService.standarMatchScore.IScoreServiceHandle;
import com.panda.sport.rcs.data.sportStatisticsService.statistics.IStatisticsServiceHandel;
import com.panda.sport.rcs.data.sportStatisticsService.thirdMatchScore.IScoreThirdServiceHandle;

import java.util.HashMap;
import java.util.Map;

/**
 * service factory
 *
 * @author Administrator
 */
public class StatisticsServiceContext {


    /*** 保存事件统计服务 ***/
    private static Map<String, IEventServiceHandle> eventStaticsServiceMap = new HashMap<>();
    /*** 保存统计服务 ***/
    private static Map<String, IStatisticsServiceHandel> statisticsServiceMap = new HashMap<>();
    /*** 标准比分服务 ***/
    private static Map<String, IScoreServiceHandle> scoreServiceMap = new HashMap<>();
    /*** 三方比分服务 ***/
    private static Map<String, IScoreThirdServiceHandle> thirdScoreServiceMap = new HashMap<>();

    public static void addEventStaticsService(IEventServiceHandle serviceHandle) {
        eventStaticsServiceMap.put(String.valueOf(serviceHandle.getSportId()), serviceHandle);
    }

    public static void addStaticsService(IStatisticsServiceHandel serviceHandle) {
        statisticsServiceMap.put(String.valueOf(serviceHandle.getSportId()), serviceHandle);
    }

    public static void addScoreService(IScoreServiceHandle serviceHandle) {
        scoreServiceMap.put(String.valueOf(serviceHandle.getSportId()), serviceHandle);
    }

    public static void addThirdScoreService(IScoreThirdServiceHandle serviceHandle) {
        thirdScoreServiceMap.put(String.valueOf(serviceHandle.getSportId()), serviceHandle);
    }

    public static IStatisticsServiceHandel getStaticsService(Long sportId) {
        return statisticsServiceMap.get(String.valueOf(sportId));
    }

    public static IEventServiceHandle getEventStaticsService(Long sportId) {
        return eventStaticsServiceMap.get(String.valueOf(sportId));
    }

    public static IScoreServiceHandle getScoreService(Long sportId) {
        return scoreServiceMap.get(String.valueOf(sportId));
    }

    public static IScoreThirdServiceHandle getThirdScoreService(Long sportId) {
        return thirdScoreServiceMap.get(String.valueOf(sportId));
    }


}
