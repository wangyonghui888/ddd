package com.panda.sport.rcs.mgr.wrapper.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.core.db.annotation.Master;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.mgr.wrapper.StandardMatchInfoService;
import com.panda.sport.rcs.mgr.wrapper.StandardSportTeamService;
import com.panda.sport.rcs.mgr.wrapper.statistics.RcsMatchDimensionStatisticsService;
import com.panda.sport.rcs.vo.*;
import com.panda.sport.rcs.vo.statistics.RcsMatchDimensionStatisticsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
@Service
@Slf4j
public class StandardMatchInfoServiceImpl extends ServiceImpl<StandardMatchInfoMapper, StandardMatchInfo> implements StandardMatchInfoService {

    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;

    @Autowired
    StandardSportTeamService standardSportTeamService;

    /**
     * @MethodName: getOtherEarlyTime
     * @Description: 得到其它早盘开始时间和结束时间
     * @Param:
     * @Return:
     * @Author: Vector
     * @Date: 2019/11/5
     **/
    public static Long[] getOtherEarlyTime() {
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        if (instance.get(Calendar.HOUR_OF_DAY) < 12) {
            instance.add(Calendar.DATE, 6);
        } else {
            instance.add(Calendar.DATE, 7);
        }
        instance.set(Calendar.HOUR_OF_DAY, 12);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        long time1 = instance.getTime().getTime();
        instance.add(Calendar.DATE, 7);
        instance.add(Calendar.MILLISECOND, -1);
        long time2 = instance.getTime().getTime();
        return new Long[]{time1, time2};
    }


    /**
     * 组合条件查询数据库赛事数据
     *
     * @param marketLiveOddsQueryVo
     * @return
     */
    @Override
    @Master
    public List<StandardMatchInfo> queryMatches(MarketLiveOddsQueryVo marketLiveOddsQueryVo) {
        if (marketLiveOddsQueryVo.getBeginTime() != null) {
            long endTimeLong = DateUtils.addNDay(marketLiveOddsQueryVo.getBeginTime(), 1).getTime();
            marketLiveOddsQueryVo.setBeginTimeMillis(marketLiveOddsQueryVo.getBeginTime().getTime());
            marketLiveOddsQueryVo.setEndTimeMillis(endTimeLong - 1L);
        }
        marketLiveOddsQueryVo.setCurrentTimeMillis(System.currentTimeMillis());
        //其它早盘
        if (marketLiveOddsQueryVo.getIsOtherEarly() != null && marketLiveOddsQueryVo.getIsOtherEarly().longValue() == 1) {
            Long[] otherEarlyTime = getOtherEarlyTime();
            marketLiveOddsQueryVo.setBeginTimeMillis(otherEarlyTime[0]);
            marketLiveOddsQueryVo.setEndTimeMillis(otherEarlyTime[1]);
        }

        List<StandardMatchInfo> list = standardMatchInfoMapper.selectPageByCondition( marketLiveOddsQueryVo);
        return list;
    }

    @Override
    public List<Long> selectByMap(Map<String, Object> map) {
        MarketLiveOddsQueryVo marketLiveOddsQueryVo = new MarketLiveOddsQueryVo();
        if (!Strings.isNullOrEmpty(marketLiveOddsQueryVo.getMatchDate())) {
            Date matchDate = DateUtils.dateStrToDate(marketLiveOddsQueryVo.getMatchDate() + " 12:00:00");
            marketLiveOddsQueryVo.setBeginTime(matchDate);
        }
        marketLiveOddsQueryVo.setOperateMatchStatus(1);
        if (map.get("liveOddBusiness") != null) {
            marketLiveOddsQueryVo.setLiveOddBusiness(Integer.parseInt(map.get("liveOddBusiness").toString()));
        }
        if (map.get("matchManageId") != null) {
            marketLiveOddsQueryVo.setMatchId(Long.parseLong(map.get("matchManageId").toString()));
        }
        if (map.get("tournamentLevel") != null) {
            marketLiveOddsQueryVo.setTournamentLevel(Integer.parseInt(map.get("tournamentLevel").toString()));
        }
        if (map.get("sportId") != null) {
            marketLiveOddsQueryVo.setMatchId(Long.parseLong(map.get("sportId").toString()));
        }
        List<StandardMatchInfo> standardMatchInfos = queryMatches(marketLiveOddsQueryVo);
        ArrayList<Long> longs = new ArrayList<>();
        for (StandardMatchInfo standardMatchInfo : standardMatchInfos) {
            longs.add(standardMatchInfo.getId());
        }
        return longs;
    }

    @Override
    public StandardMatchInfo getMacthInfoById(Long matchId){
        return standardMatchInfoMapper.getMacthInfoById(matchId);
    }
}
