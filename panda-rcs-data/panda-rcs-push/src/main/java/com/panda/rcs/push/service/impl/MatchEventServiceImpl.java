package com.panda.rcs.push.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.panda.rcs.push.client.ClientManageService;
import com.panda.rcs.push.entity.enums.SportEnum;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.entity.vo.ClientResponseVo;
import com.panda.rcs.push.service.MatchEventService;
import com.panda.rcs.push.utils.ClientResponseUtils;
import com.panda.sport.rcs.cache.RcsCacheUtils;
import com.panda.sport.rcs.pojo.dto.MatchEventInfoDTO;
import com.panda.sport.rcs.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class MatchEventServiceImpl implements MatchEventService {

    //可透传到前端事件
    private static final List<String> passEventList = Arrays.asList("match_status", "coverage_status", "time_start",
            "timeout", "timeout_over", "match_stop_suspension", "match_stop_suspension_over", "ace", "tennis_score_change", "uof", "error_end_event",
            "table_tennis_score_change","volleyball_score_change","ball_possession","snooker_score_change","time_startstop","who_throws_the_first_pitch","badminton_score_change",
            "safety","extra_point","score_change");
    //可直接透传阶段
    public static List<Integer> passPeriod = Arrays.asList(
            0,32,34,61,81,90,100,999,
            800,900,1000,1100,1200,
            301,302,303,304,305,306,445,
            421,422,423,424,425,426,427,428,429,430,431,432,433,434,435,435,436,437,438,
            43910,43810,43911,43811,43912,43812,43913,43813,43914,43814,43915,43815,43916,43816,43917,43817,43918,43818,43919,43819,43920);
    private static Cache<String, String> eventCache = RcsCacheUtils.newSyncSimpleCache(1000, 100000, 3);
    private static Cache<String, String> compareEventTimeCache = RcsCacheUtils.newSyncSimpleCache(1000, 100000, 4 * 60 * 60);

    private final static SubscriptionEnums subscriptionEnums = SubscriptionEnums.MATCH_EVENT;

    @Autowired
    private ClientManageService clientManageService;

    @Override
    public void handlerMatchEvent(MatchEventInfoDTO eventDto, String linkId) {
        //特殊处理
        eventSpecificHandling(eventDto);
        //推送时间
        //赛事时间，20秒推送一次
        //更新前端时间，已经更新过时间就不再重复推送
        String flagKey =eventDto.getStandardMatchId().toString();
        String flagValue = eventCache.getIfPresent(flagKey);
        if (CommonUtils.isBlankOrNull(flagValue)) {
            eventCache.put(flagKey, "1");
            send(eventDto, linkId);
            return;
        }
        boolean flag1 =StringUtils.isNotBlank(eventDto.getEventCode()) && passEventList.contains(eventDto.getEventCode());
        boolean flag2 =null!=eventDto.getMatchPeriodId() && passPeriod.contains(eventDto.getMatchPeriodId().intValue());
        if (flag1||flag2) {
            if(eventDto.getSportId().intValue() == SportEnum.SPORT_TENNIS.getKey()){
                if(!eventTimeFilter(eventDto)){
                    return;
                }else {
                    send(eventDto, linkId);
                    return;
                }
            }

            if (eventDto.getEventCode().equals("match_status")) {
                if (StringUtils.isNotBlank(eventDto.getExtraInfo())) {
                    send(eventDto, linkId);
                    return;
                }
            } else {
                send(eventDto, linkId);
                return;
            }
        }
    }

    /**
     * 发送方法
     * @param eventDto
     */
    private void send(MatchEventInfoDTO eventDto, String linkId){
        ClientResponseVo responseContext = ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), eventDto, 0, linkId, linkId, null);
        clientManageService.sendMessage(subscriptionEnums, Long.toString(eventDto.getStandardMatchId()), responseContext);
    }

    /**
     * 下发事件特殊处理
     *
     * @param data
     */
    private void eventSpecificHandling(MatchEventInfoDTO data) {
        //篮球特殊处理
        if (SportEnum.SPORT_BASKETBALL.getKey() == data.getSportId().intValue()) {
            if (("time_start".equals(data.getEventCode()) && "0".equals(data.getExtraInfo()))) {
                //time_start extraInfo 为 0 转成 timeout
                data.setEventCode("timeout");
                data.setExtraInfo("time_start");//标识time_start转换的
                data.setRemark("time_start转换的timeout");
            }
            if (("time_start".equals(data.getEventCode()) && "1".equals(data.getExtraInfo()))) {
                //time_start extraInfo 为 1 转成 timeout_over
                data.setEventCode("timeout_over");
            }
        }
    }

    /**
     * 事件时间比较
     *
     * @param data
     * @return
     */
    protected boolean eventTimeFilter(MatchEventInfoDTO data) {
        try {
            if (null == data || null == data.getEventTime()) {
                return true;
            }
            String compareEventTime = compareEventTimeCache.getIfPresent(String.valueOf(data.getStandardMatchId()));
            if (CommonUtils.isBlankOrNull(compareEventTime)) {
                compareEventTimeCache.put(String.valueOf(data.getStandardMatchId()),String.valueOf(data.getEventTime()));
                return true;
            }
            Long oldTime = Long.valueOf(compareEventTime);
            if (data.getEventTime() > oldTime) {
                compareEventTimeCache.put(String.valueOf(data.getStandardMatchId()),String.valueOf(data.getEventTime()));
                return true;
            } else {
                log.info("事件时对比过期{},{},{}", data.getStandardMatchId(), data.getEventTime(), oldTime);
                return false;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return true;
    }


}
