package com.panda.sport.rcs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.common.utils.ThreadUtil;
import com.panda.sport.rcs.customdb.entity.StaticsItemEntity;
import com.panda.sport.rcs.customdb.entity.StaticsUserDateEntity;
import com.panda.sport.rcs.customdb.service.impl.StaticsItemServiceImpl;
import com.panda.sport.rcs.db.entity.UserSpecialStatis;
import com.panda.sport.rcs.db.service.impl.StandardMatchTeamRelationServiceImpl;
import com.panda.sport.rcs.db.service.impl.UserSpecialStatisServiceImpl;
import com.panda.sport.rcs.service.IOrderStaticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.service.impl
 * @description :   订单统计服务. 统计指定用户的订单
 * @date: 2020-06-23 13:18
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class OrderStaticsServiceImpl implements IOrderStaticsService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    StandardMatchTeamRelationServiceImpl standardMatchTeamRelationService;

    @Autowired
    StaticsItemServiceImpl staticsItemService;

    @Autowired
    UserSpecialStatisServiceImpl userSpecialStatisService;

    private final long millSecondDay = 24 * 3600 * 1000;

    /***
     * 统计某个用户昨天的订单信息
     * @param uid
     * @return void
     * @Description 统计
     * @Author dorich
     * @Date 13:55 2020/6/23
     **/
    @Override
    public void staticsUserOrder(long uid) {
        try {
            long timeBegin = LocalDateTimeUtil.getLastHourTime();
            long timeEnd = LocalDateTimeUtil.getCurrentHourTime();
            staticsUserOrder(uid, timeBegin, timeEnd);
        } catch (Exception e) {
            String tip = String.format("用户(uid:%s)统计订单数据出错", uid);
            log.error(tip, e);
        }
    }

    /***
     * 统计某个用户指定时间戳范围内的订单信息(时间范围: 当前时间戳对应日期的前一天)
     * @param uid
     * @param timeStamp
     * @return void
     * @Description
     * @Author dorich
     * @Date 10:19 2020/7/10
     **/
    @Override
    public void staticsUserOrder(long uid, Long timeStampBegin, Long timeStampEnd) {
        log.info("用户画像统计-用户{}开始", uid);
        List<StaticsItemEntity> staticsItemEntities = new ArrayList<>();
        /*** 构造指定时间前一天的起始值作为统计时间范围的起始值   (timeStamp / millSecondDay - 1) * millSecondDay; ***/
        //Long timeStampBegin = LocalDateTimeUtil.getDayStartTime(timeStamp) - LocalDateTimeUtil.dayMill;

        //long timeStampEnd = timeStampBegin + LocalDateTimeUtil.dayMill;
        long timeStart = System.currentTimeMillis();
        /*** 按照运动种类进行统计 ***/
        List<StaticsItemEntity> itemEntitiesSportId = staticsItemService.staticsBySportId(uid, timeStampBegin, timeStampEnd);
        staticsItemEntities.addAll(itemEntitiesSportId);

        /*** 按照联赛进行统计 ***/
        List<StaticsItemEntity> itemEntitiesTournament = staticsItemService.staticsByTournamentId(uid, timeStampBegin, timeStampEnd);
        staticsItemEntities.addAll(itemEntitiesTournament);

        /*** 按照玩法进行统计 ***/
        List<StaticsItemEntity> itemEntitiesPlayId = staticsItemService.staticsByPlayId(uid, timeStampBegin, timeStampEnd);
        staticsItemEntities.addAll(itemEntitiesPlayId);

        /*** 按照球队进行统计 ***/
        List<StaticsItemEntity> itemEntitiesTeamId = staticsItemService.staticsByTeamId(uid, timeStampBegin, timeStampEnd);
        staticsItemEntities.addAll(itemEntitiesTeamId);

        /*** 按照盘口类型统计 ***/
        List<StaticsItemEntity> itemEntitiesMarketType = staticsItemService.staticsByMarketType(uid, timeStampBegin, timeStampEnd);
        staticsItemEntities.addAll(itemEntitiesMarketType);

        /*** 按照赔率统计  ***/
        List<StaticsItemEntity> itemEntitiesOdds = staticsItemService.staticsByOddsValue(uid, timeStampBegin, timeStampEnd);
        staticsItemEntities.addAll(itemEntitiesOdds);

        /*** 按照投注额进行统计 ***/
        List<StaticsItemEntity> itemEntitiesBetAmount = staticsItemService.staticsByBetAmount(uid, timeStampBegin, timeStampEnd);
        staticsItemEntities.addAll(itemEntitiesBetAmount);

        /*** 按照主副统计 ***/
        List<StaticsItemEntity> itemEntitiesMainMarket = staticsItemService.staticsByMainMarket(uid, timeStampBegin, timeStampEnd);
        staticsItemEntities.addAll(itemEntitiesMainMarket);

        /*** 按照是否对冲统计 ***/
        List<StaticsItemEntity> itemEntitiesHedge = staticsItemService.staticsByHedge(uid, timeStampBegin, timeStampEnd);
        staticsItemEntities.addAll(itemEntitiesHedge);


        /*** 统计投注类型 （单关、串关、冠军玩法）***/
        List<StaticsItemEntity> itemEntitiesSeriesType = staticsItemService.staticsByBetType(uid, timeStampBegin, timeStampEnd);
        staticsItemEntities.addAll(itemEntitiesSeriesType);


        /*** 投注阶段 （早盘、滚球）***/
        List<StaticsItemEntity> itemEntitiesMatchType = staticsItemService.staticsByBetStage(uid, timeStampBegin, timeStampEnd);
        staticsItemEntities.addAll(itemEntitiesMatchType);


        saveStaticsDataToDatabase(timeStampBegin, staticsItemEntities);


        long cost = System.currentTimeMillis() - timeStart;
        log.info("用户画像统计用户{}完成,耗时:{} 毫秒", uid, cost);
    }

    /***
     * 将统计数据写入数据库中
     * @param timeStamp                         时间戳
     * @param datas      待写入数据库的统计数据列表
     * @return void
     * @Description
     * @Author dorich
     * @Date 9:40 2020/6/24
     **/
    //@Transactional(rollbackFor = Exception.class)
    public void saveStaticsDataToDatabase(long timeStamp, List<StaticsItemEntity> datas) {
        if (CollectionUtils.isEmpty(datas)) {
            return;
        }
        String msg = String.format("添加用户(uid:%s)的统计信息", datas.get(0).getUid());
        long timeStart = System.currentTimeMillis();

        List<UserSpecialStatis> specialStatisList = new ArrayList<>();
        for (StaticsItemEntity data : datas) {
            UserSpecialStatis userSpecialStatis = new UserSpecialStatis();
//            BeanUtils.copyProperties(data, userSpecialStatis);
            userSpecialStatis.setParentType(data.getParentType());
            userSpecialStatis.setChildType(data.getChildType());
            userSpecialStatis.setValue(data.getValue());
            userSpecialStatis.setFinanceValue(data.getFinanceValue());
            userSpecialStatis.setProfit(data.getProfit());
            userSpecialStatis.setBetNum(data.getBetNum());
            userSpecialStatis.setBetProfitNum(data.getBetProfitNum());
            userSpecialStatis.setWinBetNum(data.getWinBetNum());
            userSpecialStatis.setUserId(data.getUid());
            userSpecialStatis.setStatisDay(timeStamp);

            specialStatisList.add(userSpecialStatis);
        }
        userSpecialStatisService.saveBatch(specialStatisList);
        long cost = System.currentTimeMillis() - timeStart;
        msg = msg + ";保存数据耗时:" + cost + "; 毫秒";
        log.info(msg);
    }

    @Override
    public void staticsOrderForUsers(long startStamp, long endTime) {
        /*** 查找需要统计的订单 ***/
        long timeBegin = startStamp;
        long timeEnd = endTime;
        List<StaticsUserDateEntity> uidList = staticsItemService.fetchUserId(timeBegin, timeEnd);
        long size = uidList.size();
        log.info("用户画像统计-开始:{}-{}用户:{}个", LocalDateTimeUtil.milliToLocalDateTime(timeBegin), LocalDateTimeUtil.milliToLocalDateTime(timeEnd), size);

        Map<String, List<StaticsUserDateEntity>> map = uidList.stream().collect(Collectors.groupingBy(StaticsUserDateEntity::getDate));
        map.forEach((k,v)->{
            try {
                QueryWrapper<UserSpecialStatis> queryWrapper = new QueryWrapper();
                queryWrapper.lambda().eq(UserSpecialStatis::getStatisDay, LocalDateTimeUtil.string2Date(v.get(0).getDate()));
                userSpecialStatisService.remove(queryWrapper);
                log.info("提前清空要统计的当日数据完成" + v.get(0).getDate());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        for (int i = 0; i < uidList.size(); i++) {
            StaticsUserDateEntity entity = uidList.get(i);
            int n = i;
            ThreadUtil.submit(() -> {
                try {
                    long timeStartForUser = System.currentTimeMillis();
                    Long staticsUserBegin = LocalDateTimeUtil.string2Date(entity.getDate());
                    Long staticUserEnd = staticsUserBegin + LocalDateTimeUtil.dayMill;
                    staticsUserOrder(entity.getUid(), staticsUserBegin, staticUserEnd);
                    long timeCost = System.currentTimeMillis() - timeStartForUser;
                    log.info("用户画像统计第{}/{}个用户(id:{}),统计当前用户订单信息耗时:{} 毫秒", n, size, entity.getUid(), timeCost);
                } catch (Exception e) {
                    String tip = String.format("用户(uid:%s)订单分析失败.", entity.getUid());
                    log.error(tip, e);
                }
            });

        }
    }

    /***
     * 根据给定的时间戳,得到当前时间戳所在日期的起始时间
     * @param timestamp
     * @Description
     * @Author dorich
     * @Date 11:42 2020/7/1
     **/
    public Long getDateTimeOfTimestamp(long timestamp) {
        long timeBeginUsed = (timestamp / millSecondDay) * millSecondDay;
        return timeBeginUsed;
    }
}
