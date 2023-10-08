package com.panda.sport.rcs.predict.service.impl.football.snapshot;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mapper.predict.RcsPredictForecastSnapshotMapper;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.vo.api.request.QueryForecastPlayReqVo;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictForecastPlay;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictForecastSnapshot;
import com.panda.sport.rcs.service.IRcsPredictForecastPlayService;
import com.panda.sport.rcs.service.IRcsPredictForecastSnapshotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * forecast 快照逻辑处理
 *
 * @author joey
 * @since 2022-07-26 18:31:19
 */
@Component
@Slf4j
public class ForecastSnapshotServiceBo {

    /**
     * 过期时间 1天
     */
    public static final Long MATCH_EXPIRY = 24 * 60 * 60L;
    /**
     * forecast快照只存储 开赛前15分钟后的数据
     */
    private static final Long BEGIN_TIME_15 = 900000L;

    /**
     * 快照间隔时间
     */
    private static final Long SNAPSHOT_DIFF = 300000L;
    /**
     * forecast快照 赛事开始时间
     */
    private static final String STANDARD_MATCH_INFO_BEGIN_TIME = "rcs.risk.predict.snapshot.standard.beginTime.match_id.%s";


    /**
     * forecast 快照赛事玩法初始化状态 0、未初始化 1、初始化
     */
    private static final String STANDARD_FORECAST_IS_INIT_STATUS = "rcs.risk.predict.snapshot.standard.init.status.match_id.%s.playId.%s";


    /**
     * forecast 赛事快照集合
     */

    private static final String STANDARD_FORECAST_SNAPSHOT_KEY = "rcs.risk.predict.snapshot.standard.init.list.match_id.%s.playId.%s";

    /**
     * 上一次快照时间
     */
    private static final String LAST_FORECAST_SNAPSHOT_TIME_KEY = "rcs.risk.predict.snapshot.last.time.match_id.%s.playId.%s.matchType.%s";


    @Autowired
    protected RedisClient redisClient;
    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    private RcsPredictForecastSnapshotMapper rcsPredictForecastSnapshotMapper;
    @Autowired
    private IRcsPredictForecastSnapshotService rcsPredictForecastSnapshotService;

    @Autowired
    private IRcsPredictForecastPlayService rcsPredictForecastPlayService;

    /**
     * 下一个快照时间
     *
     * @param betTime 下单时间
     * @param matchId 赛事ID
     * @return 快照时间
     */
    private Long nextSnapshotTime(Long betTime, Long matchId, Integer playId) {
        LocalDateTime betTimeLocalDateTime = LocalDateTime.ofInstant(new Date(betTime).toInstant(), ZoneId.systemDefault()).withSecond(0).withNano(0);
        long time = Date.from(betTimeLocalDateTime.atZone(ZoneId.systemDefault()).toInstant()).getTime();
        String json = redisClient.get(String.format(STANDARD_FORECAST_SNAPSHOT_KEY, matchId, playId));
        List<Long> longs = JSONArray.parseArray(json, Long.class);
        if (longs.contains(time)) {
            return time;
        } else {
            for (int i = 0; i < 6; i++) {
                betTimeLocalDateTime = betTimeLocalDateTime.plusMinutes(1);
                time = Date.from(betTimeLocalDateTime.atZone(ZoneId.systemDefault()).toInstant()).getTime();
                if (longs.contains(time)) {
                    return time;
                }
            }
        }
        return longs.stream().max(Comparator.comparing(Long::longValue)).get();
    }


    private void initSnapshotTime(Long beginTime, OrderItem orderItem, List<RcsPredictForecastPlay> list) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(new Date(beginTime).toInstant(), ZoneId.systemDefault()).plusMinutes(5).withSecond(0).withNano(0);
        List<Long> resultList = new ArrayList<>();
        resultList.add(Date.from(localDateTime.minusMinutes(5).withSecond(0).withNano(0).atZone(ZoneId.systemDefault()).toInstant()).getTime());
        resultList.add(Date.from(localDateTime.minusMinutes(10).withSecond(0).withNano(0).atZone(ZoneId.systemDefault()).toInstant()).getTime());
        resultList.add(Date.from(localDateTime.minusMinutes(15).withSecond(0).withNano(0).atZone(ZoneId.systemDefault()).toInstant()).getTime());
        resultList.add(Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()).getTime());
        LocalDateTime localDateTime1 = localDateTime.plusMinutes(5);
        while (true) {
            long time = Date.from(localDateTime1.atZone(ZoneId.systemDefault()).toInstant()).getTime();
            resultList.add(time);
            if (Duration.between(localDateTime, localDateTime1).abs().toMinutes() >= 95) {
                break;
            }
            localDateTime1 = localDateTime1.plusMinutes(5).withSecond(0).withNano(0);
        }
        redisClient.setExpiry(String.format(STANDARD_FORECAST_SNAPSHOT_KEY, orderItem.getMatchId(), orderItem.getPlayId()), JSONObject.toJSONString(resultList), MATCH_EXPIRY);
        List<RcsPredictForecastSnapshot> saveData = new ArrayList<>();
        resultList.forEach(time -> list.forEach(item -> {
            RcsPredictForecastSnapshot rcsPredictForecastSnapshot = new RcsPredictForecastSnapshot();
            BeanUtils.copyProperties(item, rcsPredictForecastSnapshot);
            rcsPredictForecastSnapshot.setId(null);
            rcsPredictForecastSnapshot.setProfitValue(BigDecimal.ZERO);
            rcsPredictForecastSnapshot.setSnapshotTime(time);
            rcsPredictForecastSnapshot.setUpdateTime(new Date().getTime());
            rcsPredictForecastSnapshot.setCreateTime(new Date().getTime());
            saveData.add(rcsPredictForecastSnapshot);
        }));
        log.info(":::forecast快照订单号{}::: 初始化保存数据：{}", orderItem.getOrderNo(), JSONObject.toJSONString(saveData));
        rcsPredictForecastSnapshotService.saveBatch(saveData);
        redisClient.setExpiry(String.format(STANDARD_FORECAST_IS_INIT_STATUS, orderItem.getMatchId(), orderItem.getPlayId()), "1", MATCH_EXPIRY);

    }


    /**
     * forecast快照
     */
    public void forecastSnapshot(OrderItem orderItem) {
        try {
            log.info(":::forecast快照订单号{}::: 开始执行forecast快照", orderItem.getOrderNo());
            String standardMatchBeginTime = redisClient.get(String.format(STANDARD_MATCH_INFO_BEGIN_TIME, orderItem.getMatchId()));
            if (StringUtils.isEmpty(standardMatchBeginTime)) {
                StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectOne(new LambdaQueryWrapper<StandardMatchInfo>()
                        .eq(StandardMatchInfo::getId, orderItem.getMatchId()));
                standardMatchBeginTime = standardMatchInfo.getBeginTime().toString();
                redisClient.setExpiry(STANDARD_MATCH_INFO_BEGIN_TIME, standardMatchBeginTime, MATCH_EXPIRY);
            }
            long beginTime = Long.parseLong(standardMatchBeginTime);
            long nowLong = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            if (nowLong < (beginTime - BEGIN_TIME_15)) {
                String beginTimeStr = LocalDateTime.ofInstant(new Date(beginTime).toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                log.info("forecast快照 赛事id：{} 赛事开始时间:{}，未达到快照要求标准时间：15分钟 ", orderItem.getMatchId(), beginTimeStr);
                return;
            }
            // 防止 forecast 计算太慢导致查询不到的情况
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            QueryForecastPlayReqVo queryForecastPlayReqVo = new QueryForecastPlayReqVo();
            if (orderItem.getPlayId().equals(1)) orderItem.setPlayId(4);
            queryForecastPlayReqVo.setPlayId(Long.valueOf(orderItem.getPlayId()));
            queryForecastPlayReqVo.setMatchId(orderItem.getMatchId());
            queryForecastPlayReqVo.setDataType(1);
            queryForecastPlayReqVo.setMatchType(orderItem.getMatchType());
            List<RcsPredictForecastPlay> list = rcsPredictForecastPlayService.selectList(queryForecastPlayReqVo);
            if (CollectionUtils.isEmpty(list)) {
                log.info(":::forecast快照订单号{}::: 未查询到forecast数据 跳过本次快照执行！", orderItem.getOrderNo());
                return;
            }
            log.info(":::forecast快照订单号{}::: 查询forecast数据:{}", orderItem.getOrderNo(), JSONObject.toJSONString(list));
            String initStatus = redisClient.get(String.format(STANDARD_FORECAST_IS_INIT_STATUS, orderItem.getMatchId(), orderItem.getPlayId()));
            if (StringUtils.isEmpty(initStatus) || initStatus.equals("0")) {
                log.info(":::forecast快照订单号{}::: 赛事数据开始初始化", orderItem.getOrderNo());
                initSnapshotTime(beginTime, orderItem, list);
            }
            long date = nextSnapshotTime(orderItem.getBetTime(), orderItem.getMatchId(), orderItem.getPlayId());
            log.info(":::forecast快照订单号{}::: 初始化完成！获取当前快照时间", orderItem.getOrderNo());
            List<RcsPredictForecastSnapshot> rcsPredictForecastSnapshots = findByRcsPredictForecastSnapshotList(date, orderItem);
            log.info(":::forecast快照订单号{}::: 快照时间：{}, 数据结果：{}", orderItem.getOrderNo(), date, JSONObject.toJSONString(rcsPredictForecastSnapshots));
            Map<Integer, RcsPredictForecastPlay> collect = list.stream().collect(Collectors.groupingBy(RcsPredictForecastPlay::getScore, Collectors.collectingAndThen(Collectors.toMap(k -> k, v -> v)
                    , e -> e.values().stream().findFirst().get())));
            rcsPredictForecastSnapshots.forEach(item -> item.setProfitValue(collect.get(item.getScore()).getProfitValue()));
            log.info(":::forecast快照订单号{}::: 修改保存数据：{}", orderItem.getOrderNo(), JSONObject.toJSONString(rcsPredictForecastSnapshots));
            rcsPredictForecastSnapshotService.updateBatchById(rcsPredictForecastSnapshots);
            String format = String.format(LAST_FORECAST_SNAPSHOT_TIME_KEY, orderItem.getMatchId(), orderItem.getPlayId(), orderItem.getMatchType());
            String lastTime = redisClient.get(format);
            if (StringUtils.isEmpty(lastTime)) {
                redisClient.setExpiry(String.format(LAST_FORECAST_SNAPSHOT_TIME_KEY, orderItem.getMatchId(), orderItem.getPlayId(), orderItem.getMatchType()), date, MATCH_EXPIRY);
                log.info(":::forecast快照订单号{}::: 无需补录数据 处理完成！", orderItem.getOrderNo());
                return;
            }
            long time = Long.parseLong(lastTime);
            log.info(":::forecast快照订单号{}::: 补录数据 上一次快照时间{}", orderItem.getOrderNo(), time);
            long diff = date - time;
            if (diff >= SNAPSHOT_DIFF) {
                List<Long> tmpList = new ArrayList<>();
                while (true) {
                    time = time + SNAPSHOT_DIFF;
                    if (date - time > 0) {
                        tmpList.add(time);
                    } else {
                        break;
                    }
                }
                if (CollectionUtils.isEmpty(tmpList)) return;
                log.info(":::forecast快照订单号{}::: 需补录数据时间： {}", orderItem.getOrderNo(), JSONArray.toJSONString(tmpList));
                Map<Integer, RcsPredictForecastSnapshot> lastSnapshotList = findByRcsPredictForecastSnapshotList(Long.parseLong(lastTime), orderItem).stream().collect(Collectors.groupingBy(RcsPredictForecastSnapshot::getScore, Collectors.collectingAndThen(Collectors.toMap(k -> k, v -> v)
                        , e -> e.values().stream().findFirst().get())));
                List<RcsPredictForecastSnapshot> snapshotList = findByCompensateRcsPredictForecastSnapshotList(tmpList, orderItem);
                Map<Long, Map<Integer, RcsPredictForecastSnapshot>> collect1 = snapshotList.stream().
                        collect(Collectors.groupingBy(RcsPredictForecastSnapshot::getSnapshotTime, Collectors.groupingBy(RcsPredictForecastSnapshot::getScore, Collectors.collectingAndThen(Collectors.toMap(k -> k, v -> v), v -> v.values().stream().findFirst().get()))));

                collect1.forEach((key, map) -> lastSnapshotList.forEach((lastKey, lastValue) -> {
                    RcsPredictForecastSnapshot rcsPredictForecastSnapshot = map.get(lastKey);
                    rcsPredictForecastSnapshot.setProfitValue(lastValue.getProfitValue());
                }));
                rcsPredictForecastSnapshotService.updateBatchById(snapshotList);
            }
            redisClient.setExpiry(String.format(LAST_FORECAST_SNAPSHOT_TIME_KEY, orderItem.getMatchId(), orderItem.getPlayId(), orderItem.getMatchType()), date, MATCH_EXPIRY);
            log.info(":::forecast快照订单号{}::: 处理完成！快照时间：{}", orderItem.getOrderNo(), date);
        } catch (Exception e) {
            e.printStackTrace();
            log.info(":::forecast快照订单号{}::: 处理异常！", orderItem.getOrderNo());
        }
    }

    private List<RcsPredictForecastSnapshot> findByRcsPredictForecastSnapshotList(Long date, OrderItem orderItem) {
        return rcsPredictForecastSnapshotMapper.selectList(new LambdaQueryWrapper<RcsPredictForecastSnapshot>()
                .eq(RcsPredictForecastSnapshot::getMatchId, orderItem.getMatchId())
                .eq(RcsPredictForecastSnapshot::getMatchType, orderItem.getMatchType())
                .eq(RcsPredictForecastSnapshot::getPlayId, orderItem.getPlayId())
                .eq(RcsPredictForecastSnapshot::getSnapshotTime, date));
    }


    private List<RcsPredictForecastSnapshot> findByCompensateRcsPredictForecastSnapshotList(List<Long> dateList, OrderItem orderItem) {
        return rcsPredictForecastSnapshotMapper.selectList(new LambdaQueryWrapper<RcsPredictForecastSnapshot>()
                .eq(RcsPredictForecastSnapshot::getMatchId, orderItem.getMatchId())
                .eq(RcsPredictForecastSnapshot::getMatchType, orderItem.getMatchType())
                .eq(RcsPredictForecastSnapshot::getPlayId, orderItem.getPlayId())
                .in(RcsPredictForecastSnapshot::getSnapshotTime, dateList));
    }

}
