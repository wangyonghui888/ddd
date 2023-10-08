package com.panda.sport.rcs.console.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.sport.rcs.console.dao.RcsMonitorDataMapper;
import com.panda.sport.rcs.console.dto.MatchFlowingDTO;
import com.panda.sport.rcs.console.pojo.RcsMonitorData;
import com.panda.sport.rcs.console.pojo.RcsMonitorDataVo;
import com.panda.sport.rcs.console.service.RcsMonitorDataService;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RcsMonitorDataServiceImpl implements RcsMonitorDataService {

    @Resource
    private RcsMonitorDataMapper rcsMonitorDataMapper;

    @Override
    public int updateBatch(List<RcsMonitorData> list) {
        return rcsMonitorDataMapper.updateBatch(list);
    }

    @Override
    public int updateBatchSelective(List<RcsMonitorData> list) {
        return rcsMonitorDataMapper.updateBatchSelective(list);
    }

    @Override
    public int batchInsert(List<RcsMonitorData> list) {
        return rcsMonitorDataMapper.batchInsert(list);
    }

    @Override
    public int insertOrUpdate(RcsMonitorData record) {
        return rcsMonitorDataMapper.insertOrUpdate(record);
    }

    @Override
    public int insertOrUpdateSelective(RcsMonitorData record) {
        return rcsMonitorDataMapper.insertOrUpdateSelective(record);
    }

    @Override
    public List<RcsMonitorDataVo> queryRate(MatchFlowingDTO bean) {
        return rcsMonitorDataMapper.queryRate(bean);
    }

    @Override
    public List group() {
        return rcsMonitorDataMapper.group();
    }

    @Override
    public HashMap<String, Map> graphaData(MatchFlowingDTO bean) {
        List<RcsMonitorDataVo> list = rcsMonitorDataMapper.queryAllCount(bean);
        log.info("1111111111111111111111{}", JsonFormatUtils.toJson(list));
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<>();
        }
        TreeMap<String, List<RcsMonitorDataVo>> maps = list.stream().collect(Collectors.groupingBy(RcsMonitorDataVo::getCreateTimeHours,TreeMap::new,Collectors.toList()));
        HashMap<String, Map> voMaps = new HashMap<>();
        for (Map.Entry<String, List<RcsMonitorDataVo>> stringListEntry : maps.entrySet()) {
            String key = stringListEntry.getKey();
            List<RcsMonitorDataVo> value = stringListEntry.getValue();
            List<String> allHaves = new ArrayList<>(Arrays.asList("RPC_CATEGORY_SET", "RPC_CATEGORY_SET_PLAY", "RPC_QUERY_MAXBET", "RPC_SAVE_ORDER", "MQ_ORDER_PREDICT_CALC", "MQ_ORDER_INFO_WS"));
            ArrayList<String> haves = new ArrayList<>();
            for (RcsMonitorDataVo bean1 : value) {
                haves.add(bean1.getMonitorCode());
                Map<String, List> map = voMaps.get(bean1.getMonitorCode());
                ArrayList<Object> hours = new ArrayList<>();
                ArrayList<Object> counts = new ArrayList<>();
                if (!CollectionUtils.isEmpty(map)) {
                    hours = (ArrayList<Object>) map.get("hours");
                    counts = (ArrayList<Object>) map.get("counts");
                }
                hours.add(bean1.getCreateTimeHours());
                counts.add(bean1.getAllCount());
                if (CollectionUtils.isEmpty(map)) {
                    map = new HashMap();
                    map.put("hours", hours);
                    map.put("counts", counts);
                    voMaps.put(bean1.getMonitorCode(), map);
                }
            }
            Iterator<String> iterator = allHaves.iterator();

            while (iterator.hasNext()) {
                String next = iterator.next();
                for (String hava : haves) {
                    if (hava.equals(next)) {
                        iterator.remove();
                    }
                }
            }
            if (!CollectionUtils.isEmpty(allHaves)) {
                for (String monitorCode : allHaves) {
                    Map<String, List> map = voMaps.get(monitorCode);
                    ArrayList<Object> hours = new ArrayList<>();
                    ArrayList<Object> counts = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(map)) {
                        hours = (ArrayList<Object>) map.get("hours");
                        counts = (ArrayList<Object>) map.get("counts");
                    }
                    hours.add(key);
                    counts.add(0);
                    if (CollectionUtils.isEmpty(map)) {
                        map = new HashMap();
                        map.put("hours", hours);
                        map.put("counts", counts);
                        voMaps.put(monitorCode, map);
                    }
                }
            }
        }
        return voMaps;
    }

    public static void main(String[] args) {
        String a = "[{\"monitorCode\":\"RPC_QUERY_MAXBET\",\"createTimeHours\":2021043003, \"allCount\":30.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_CATEGORY_SET\",\"createTimeHours\":2021043003, \"allCount\":24.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_CATEGORY_SET_PLAY\",\"createTimeHours\":2021043003, \"allCount\":75.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_CATEGORY_SET_PLAY\",\"createTimeHours\":2021043004, \"allCount\":450.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_CATEGORY_SET\",\"createTimeHours\":2021043004, \"allCount\":144.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_QUERY_MAXBET\",\"createTimeHours\":2021043004, \"allCount\":120.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_CATEGORY_SET_PLAY\",\"createTimeHours\":2021043005, \"allCount\":450.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_CATEGORY_SET\",\"createTimeHours\":2021043005, \"allCount\":144.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_QUERY_MAXBET\",\"createTimeHours\":2021043005, \"allCount\":120.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_CATEGORY_SET_PLAY\",\"createTimeHours\":2021043006, \"allCount\":450.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_CATEGORY_SET\",\"createTimeHours\":2021043006, \"allCount\":144.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_QUERY_MAXBET\",\"createTimeHours\":2021043006, \"allCount\":120.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_CATEGORY_SET_PLAY\",\"createTimeHours\":2021043007, \"allCount\":450.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_CATEGORY_SET\",\"createTimeHours\":2021043007, \"allCount\":144.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_QUERY_MAXBET\",\"createTimeHours\":2021043007, \"allCount\":120.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_CATEGORY_SET_PLAY\",\"createTimeHours\":2021043008, \"allCount\":450.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_CATEGORY_SET\",\"createTimeHours\":2021043008, \"allCount\":144.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_QUERY_MAXBET\",\"createTimeHours\":2021043008, \"allCount\":120.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_CATEGORY_SET\",\"createTimeHours\":2021043009, \"allCount\":144.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_QUERY_MAXBET\",\"createTimeHours\":2021043009, \"allCount\":127.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"MQ_ORDER_PREDICT_CALC\",\"createTimeHours\":2021043009, \"allCount\":6.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_SAVE_ORDER\",\"createTimeHours\":2021043009, \"allCount\":6.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_CATEGORY_SET_PLAY\",\"createTimeHours\":2021043009, \"allCount\":450.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"MQ_ORDER_INFO_WS\",\"createTimeHours\":2021043009, \"allCount\":6.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"MQ_ORDER_INFO_WS\",\"createTimeHours\":2021043010, \"allCount\":32.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_QUERY_MAXBET\",\"createTimeHours\":2021043010, \"allCount\":191.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_SAVE_ORDER\",\"createTimeHours\":2021043010, \"allCount\":32.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_CATEGORY_SET_PLAY\",\"createTimeHours\":2021043010, \"allCount\":525.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_CATEGORY_SET\",\"createTimeHours\":2021043010, \"allCount\":168.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"MQ_ORDER_PREDICT_CALC\",\"createTimeHours\":2021043010, \"allCount\":32.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_CATEGORY_SET_PLAY\",\"createTimeHours\":2021043011, \"allCount\":450.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_SAVE_ORDER\",\"createTimeHours\":2021043011, \"allCount\":34.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"MQ_ORDER_INFO_WS\",\"createTimeHours\":2021043011, \"allCount\":34.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null},\n" +
                " {\"monitorCode\":\"RPC_CATEGORY_SET\",\"createTimeHours\":2021043011, \"allCount\":144.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"MQ_ORDER_PREDICT_CALC\",\"createTimeHours\":2021043011, \"allCount\":34.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_QUERY_MAXBET\",\"createTimeHours\":2021043011, \"allCount\":228.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_SAVE_ORDER\",\"createTimeHours\":2021043012, \"allCount\":25.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"MQ_ORDER_INFO_WS\",\"createTimeHours\":2021043012, \"allCount\":25.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null},\n" +
                " {\"monitorCode\":\"RPC_QUERY_MAXBET\",\"createTimeHours\":2021043012, \"allCount\":253.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"MQ_ORDER_PREDICT_CALC\",\"createTimeHours\":2021043012, \"allCount\":25.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_CATEGORY_SET\",\"createTimeHours\":2021043012, \"allCount\":144.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_CATEGORY_SET_PLAY\",\"createTimeHours\":2021043012, \"allCount\":450.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_QUERY_MAXBET\",\"createTimeHours\":2021043013, \"allCount\":138.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_CATEGORY_SET\",\"createTimeHours\":2021043013, \"allCount\":96.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}, \n" +
                " {\"monitorCode\":\"RPC_CATEGORY_SET_PLAY\",\"createTimeHours\":2021043013, \"allCount\":300.0,\"value100\":null,\"value200\":null,\"value500\":null,\"value1000\":null,\"value2000\":null}]";

        List<RcsMonitorDataVo> list = JSONObject.parseObject(a, new TypeReference<List<RcsMonitorDataVo>>() {
        });

        TreeMap<String, List<RcsMonitorDataVo>> maps = list.stream().collect(Collectors.groupingBy(RcsMonitorDataVo::getCreateTimeHours,TreeMap::new,Collectors.toList()));
        HashMap<String, Map<String, List>> voMaps = new HashMap<>();

        for (Map.Entry<String, List<RcsMonitorDataVo>> stringListEntry : maps.entrySet()) {

            String key = stringListEntry.getKey();
            List<RcsMonitorDataVo> value = stringListEntry.getValue();
            List<String> allHaves = new ArrayList<>(Arrays.asList("RPC_CATEGORY_SET", "RPC_CATEGORY_SET_PLAY", "RPC_QUERY_MAXBET", "RPC_SAVE_ORDER", "MQ_ORDER_PREDICT_CALC", "MQ_ORDER_INFO_WS"));
            ArrayList<String> haves = new ArrayList<>();
            for (RcsMonitorDataVo bean1 : value) {
                haves.add(bean1.getMonitorCode());
                Map<String, List> map = voMaps.get(bean1.getMonitorCode());
                ArrayList<Object> hours = new ArrayList<>();
                ArrayList<Object> counts = new ArrayList<>();
                if (!CollectionUtils.isEmpty(map)) {
                    hours = (ArrayList<Object>) map.get("hours");
                    counts = (ArrayList<Object>) map.get("counts");
                }
                hours.add(bean1.getCreateTimeHours());
                counts.add(bean1.getAllCount());
                if (CollectionUtils.isEmpty(map)) {
                    map = new HashMap();
                    map.put("hours", hours);
                    map.put("counts", counts);
                    voMaps.put(bean1.getMonitorCode(), map);
                }
            }
            Iterator<String> iterator = allHaves.iterator();

            while (iterator.hasNext()) {
                String next = iterator.next();
                for (String hava : haves) {
                    if (hava.equals(next)) {
                        iterator.remove();
                    }
                }
            }
            if (!CollectionUtils.isEmpty(allHaves)) {
                for (String monitorCode : allHaves) {
                    Map<String, List> map = voMaps.get(monitorCode);
                    ArrayList<Object> hours = new ArrayList<>();
                    ArrayList<Object> counts = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(map)) {
                        hours = (ArrayList<Object>) map.get("hours");
                        counts = (ArrayList<Object>) map.get("counts");
                    }
                    hours.add(key);
                    counts.add(0);
                    if (CollectionUtils.isEmpty(map)) {
                        map = new HashMap();
                        map.put("hours", hours);
                        map.put("counts", counts);
                        voMaps.put(monitorCode, map);
                    }
                }
            }

        }
        System.out.println(1);
    }
}
