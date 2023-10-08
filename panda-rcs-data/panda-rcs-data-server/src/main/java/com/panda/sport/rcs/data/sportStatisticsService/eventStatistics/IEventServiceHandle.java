package com.panda.sport.rcs.data.sportStatisticsService.eventStatistics;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchEventInfoMessage;
import com.panda.sport.rcs.data.sportStatisticsService.ISuperStatisticsServiceHandle;

import java.util.Map;

/**
 * @author Administrator
 * @project Name :  panda_data_service
 * @package Name :  com.panda.sports.manager.realtime.eventhandle
 * @description :   运动种类事件统计功能共用的接口
 * --------  ---------  --------------------------
 */
public interface IEventServiceHandle extends ISuperStatisticsServiceHandle<MatchEventInfoMessage> {


    Map standardScore(Request<MatchEventInfoMessage> request, int size);
}
