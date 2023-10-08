package com.panda.rcs.cleanup.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.rcs.cleanup.entity.RcsUserConfig;
import com.panda.rcs.cleanup.entity.RcsUserConfigNew;
import com.panda.rcs.cleanup.entity.RcsUserConfigNewConfig;
import com.panda.rcs.cleanup.mapper.RcsUserConfigMapper;
import com.panda.rcs.cleanup.mapper.RcsUserConfigNewMapper;
import com.panda.rcs.cleanup.service.IRcsUserConfigNewService;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * user-config 优化配置
 *
 * @description:
 * @author: magic
 * @create: 2022-07-18 15:15
 **/
@Slf4j
@Service
public class RcsUserConfigNewServiceImpl extends ServiceImpl<RcsUserConfigNewMapper, RcsUserConfigNew> implements IRcsUserConfigNewService {
    @Autowired
    private RcsUserConfigMapper userConfigMapper;
    @Autowired
    private RedisClient redisUtils;
    private static final String TASK_KEY = "rcs:userconfig:task-id";

    private static final String TASK_COUNT_KEY = "rcs:userconfig:task-count";


    @Override
    public void convertOldDataTask() {
        new Thread(() -> {
            //监控一分钟，如果没有数据变化，那说明没有任务在跑，执行任务
            String idStr = redisUtils.get(TASK_KEY);
            boolean execute = true;
            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                String changeIdStr = redisUtils.get(TASK_KEY);
                if (!StringUtils.equals(idStr, changeIdStr)) {
                    //执行任务id记录在变更 说明有任务在执行，终止该次任务
                    execute = false;
                    break;
                }
            }
            if (!execute) {
                log.info("当前不执行，任务正在处理中");
            } else {
                log.info("开始执行任务：开始位置：{}", idStr);
            }
            //一份之后如果 id没有变化 execute == true  就在该程序执行任务
            while (execute) {
                idStr = redisUtils.get(TASK_KEY);
                LambdaQueryWrapper<RcsUserConfig> qw = new LambdaQueryWrapper<>();
                if (StringUtils.isNotBlank(idStr)) {
                    qw.gt(RcsUserConfig::getId, Long.parseLong(idStr));
                }
                qw.last("limit 5000");
                List<RcsUserConfig> list = userConfigMapper.selectList(qw);
                if (list.isEmpty()) {
                    log.info("userConfig-convertOldDataTask-任务执行完成");
                    execute = false;
                    break;
                }


                Map<Long, List<RcsUserConfig>> oldDataGroup = list.stream().filter(e -> e.getUserId() != null).collect(Collectors.groupingBy(RcsUserConfig::getUserId));

                //过程种有数据产生，对比更新时间 使用最新的数据
                Map<Long, RcsUserConfigNew> newDataGroup = baseMapper.selectList(new LambdaQueryWrapper<RcsUserConfigNew>()
                        .select(RcsUserConfigNew::getId, RcsUserConfigNew::getUserId, RcsUserConfigNew::getConfig, RcsUserConfigNew::getUpdateTime)
                        .in(RcsUserConfigNew::getUserId, oldDataGroup.keySet())).stream().collect(Collectors.toMap(RcsUserConfigNew::getUserId, e -> e));

                List<RcsUserConfigNew> insertList = new ArrayList<>();
                List<Long> updateIdsList = new ArrayList<>();
                oldDataGroup.forEach((k, v) -> {
                    RcsUserConfigNew rcsUserConfigNew = newDataGroup.get(k);
                    if (rcsUserConfigNew == null) {
                        rcsUserConfigNew = new RcsUserConfigNew();
                        BeanUtils.copyProperties(v.get(v.size() - 1), rcsUserConfigNew);
                        rcsUserConfigNew.setId(null);
                        rcsUserConfigNew.setConfig(JSONObject.toJSONString(v.stream().filter(Objects::nonNull).sorted(Comparator.comparing(RcsUserConfig::getSportId)).map(e -> {
                            RcsUserConfigNewConfig rcsUserConfigNewConfig = new RcsUserConfigNewConfig();
                            rcsUserConfigNewConfig.setSportId(e.getSportId());
                            return rcsUserConfigNewConfig;
                        }).collect(Collectors.toList())));
                        insertList.add(rcsUserConfigNew);
                    } else {
                        updateIdsList.add(k);
                    }
                });

                if (!insertList.isEmpty()) {
                    saveBatch(insertList);
                }
                if (!updateIdsList.isEmpty()) {
                    //为了防止有id不连续 错开的用户数据 对已存在新表的用户数据 根据userId查询旧数据经行修正
                    List<RcsUserConfigNew> updateList = new ArrayList<>();
                    Map<Long, List<RcsUserConfig>> oldFixDataGroup = userConfigMapper.selectList(new LambdaQueryWrapper<RcsUserConfig>().in(RcsUserConfig::getUserId, updateIdsList)).stream().collect(Collectors.groupingBy(RcsUserConfig::getUserId));
                    oldFixDataGroup.forEach((k, v) -> {
                        LocalDateTime oldLastUpdate = v.stream().map(e -> LocalDateTime.parse(e.getUpdateTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).max(Comparator.comparing(e -> e)).get();
                        RcsUserConfigNew rcsUserConfigNew = newDataGroup.get(k);
                        LocalDateTime newLastUpdate = LocalDateTime.parse(rcsUserConfigNew.getUpdateTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        String oldConfig = JSONObject.toJSONString(v.stream().filter(Objects::nonNull).sorted(Comparator.comparing(RcsUserConfig::getSportId)).map(e -> {
                            RcsUserConfigNewConfig rcsUserConfigNewConfig = new RcsUserConfigNewConfig();
                            rcsUserConfigNewConfig.setSportId(e.getSportId());
                            return rcsUserConfigNewConfig;
                        }).collect(Collectors.toList()));

                        String oldSportIds = v.stream().filter(Objects::nonNull).sorted(Comparator.comparing(RcsUserConfig::getSportId)).map(e -> e.getSportId().toString()).collect(Collectors.joining(","));

                        String newSportIds = null;
                        if (StringUtils.isNotBlank(rcsUserConfigNew.getConfig())) {
                            newSportIds = JSONArray.parseArray(rcsUserConfigNew.getConfig(), RcsUserConfigNewConfig.class).stream().map(RcsUserConfigNewConfig::getSportId).sorted().map(Object::toString).collect(Collectors.joining(","));
                        }
                        //旧数据更新时间大于新数据 或者 更新时间相同 但是 sportIds不同 对新数据进行更新 主要针对id 不连续的用户数据 修正处理
                        if (
                                (oldLastUpdate.compareTo(newLastUpdate) == 0 && !StringUtils.equals(oldSportIds, newSportIds)) ||
                                        oldLastUpdate.isAfter(newLastUpdate)) {
                            RcsUserConfigNew rcsUserConfigNewUpdate = new RcsUserConfigNew();
                            BeanUtils.copyProperties(v.get(v.size() - 1), rcsUserConfigNewUpdate);
                            rcsUserConfigNewUpdate.setId(rcsUserConfigNew.getId());
                            rcsUserConfigNewUpdate.setConfig(oldConfig);
                            updateList.add(rcsUserConfigNewUpdate);
                        }

                    });
                    if (!updateList.isEmpty()) {
                        updateBatchById(updateList);
                    }
                }
                String maxId = list.get(list.size() - 1).getId().toString();
                int taskCount = list.size();
                if (StringUtils.isNotBlank(idStr) && idStr.equals(maxId)) {
                    log.info("userConfig-convertOldDataTask-重复执行");
                } else {
                    redisUtils.set(TASK_KEY, maxId);
                    redisUtils.incrBy(TASK_COUNT_KEY, taskCount);
                }
            }
        }).start();
    }
//
//    @Override
//    public void verifySportIds() {
//        new Thread(() -> {
//            Long id = null;
//            while (true) {
//                List<RcsUserConfigNew> list = baseMapper.selectList(new LambdaQueryWrapper<RcsUserConfigNew>()
//                        .gt(id != null, RcsUserConfigNew::getId, id).last("limit 100"));
//                if (list.isEmpty()) {
//                    log.info("verifySportIds 数据验证完成");
//                    break;
//                }
//                Map<Long, List<Long>> oldList = userConfigMapper.selectList(new LambdaQueryWrapper<RcsUserConfig>().in(RcsUserConfig::getUserId, list.stream().map(RcsUserConfigNew::getUserId).collect(Collectors.toList())))
//                        .stream().filter(e -> e.getSportId() != null).collect(Collectors.groupingBy(RcsUserConfig::getUserId,
//                                Collectors.collectingAndThen(Collectors.toList(), e -> e.stream().map(RcsUserConfig::getSportId).collect(Collectors.toList()))
//                        ));
//                list.forEach(e -> {
//                    List<Long> newSportIds = JSONArray.parseArray(e.getConfig(), RcsUserConfigNewConfig.class).stream().map(RcsUserConfigNewConfig::getSportId).collect(Collectors.toList());
//                    List<Long> oldSportIds = oldList.get(e.getUserId());
//                    if (oldSportIds.stream().anyMatch(o -> !newSportIds.contains(o))) {
//                        log.info("verifySportIds 数据验证错误:{}", e.getUserId());
//                    }
//                });
//                id = list.get(list.size() - 1).getId();
//            }
//        }).start();
//    }

    public static void main(String[] args) {
        List<String> a = new ArrayList<>();
        a.add(null);
        a.add(null);
        System.out.println(a.stream().collect(Collectors.joining(",")));
    }
}

