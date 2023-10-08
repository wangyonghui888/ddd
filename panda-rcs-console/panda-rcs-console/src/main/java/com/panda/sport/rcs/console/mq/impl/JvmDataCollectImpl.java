package com.panda.sport.rcs.console.mq.impl;


import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.console.dao.ErrorMapper;
import com.panda.sport.rcs.console.dao.GarbageCollectorMapper;
import com.panda.sport.rcs.console.dao.HeartMapper;
import com.panda.sport.rcs.console.dao.MemoryMapper;
import com.panda.sport.rcs.console.dao.SystemInfoMapper;
import com.panda.sport.rcs.console.dao.SystemServiceInfoMapper;
import com.panda.sport.rcs.console.pojo.ErrorMqBean;
import com.panda.sport.rcs.console.pojo.GarbageCollector;
import com.panda.sport.rcs.console.pojo.HeartMqBean;
import com.panda.sport.rcs.console.pojo.Memory;
import com.panda.sport.rcs.console.pojo.SystemInfo;
import com.panda.sport.rcs.console.pojo.SystemServiceInfo;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.monitor.entity.GarbageCollectorBean;
import com.panda.sport.rcs.monitor.entity.MqConsumerStatsBean;
import com.panda.sport.rcs.monitor.entity.ServiceInfoBean;
import com.panda.sport.rcs.monitor.entity.SysInfoBean;
import com.panda.sport.rcs.monitor.entity.ThreadBean;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class JvmDataCollectImpl extends ConsumerAdapter<JSONObject> {
    @Autowired
    private MemoryMapper memoryMapper;
    @Autowired
    private HeartMapper heartMapper;
    @Autowired
    private ErrorMapper errorMapper;

    @Autowired
    private GarbageCollectorMapper garbageCollectorMapper;

    @Autowired
    private SystemServiceInfoMapper systemServiceInfoMapper;

    @Autowired
    private SystemInfoMapper systemInfoMapper;

    public JvmDataCollectImpl() {
        super("RCS_COLLECT_DATA", "");
    }

    @Override
    public Boolean handleMs(JSONObject jsonData, Map<String, String> paramsMap) {
      /*      log.info("收到风控后台管理消息jsonData：{}, paramsMap：{}",JSONObject.toJSONString(jsonData),JSONObject.toJSONString(paramsMap));
        try {
            String key = paramsMap.get("KEYS");
            String ip = paramsMap.get("IP");
            String pid = paramsMap.get("PID");
            String serverName = paramsMap.get("SEND_SERVER_NAME");


            if (key.startsWith("MEMORY")) {
                //保存内存使用信息
                saveMemory(jsonData, ip, pid, serverName);
            } else if (key.startsWith("GC")) {
                // 保存GC使用情况
                saveGC(jsonData, ip, pid, serverName);
            } else if (key.startsWith("CPU")) {
                // 保存CPU使用情况
                saveCPU(jsonData, ip, pid, serverName);
            }else if (key.startsWith("ERROR_LOG")) {
                // 保存错误日志信息
                saveError(jsonData, ip, pid, serverName);
            }else if (key.startsWith("HEART_")) {
                // 保存心跳信息
                saveHeart(jsonData, ip, pid, serverName);
            }else if (key.startsWith("THREAD")) {
                // 保存线程信息
                saveThread(jsonData, ip, pid, serverName);
            }else if (key.startsWith("MQ_")) {
                // 保存队列信息
                saveMqInfo(jsonData, ip, pid, serverName);
            }
            
            log.info("数据收集收到消息：{},properties:{}", jsonData.toJSONString(), JSONObject.toJSONString(paramsMap));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }*/
        return true;
    }

    private void saveMqInfo(JSONObject jsonData, String ip, String pid, String serverName) {
    	MqConsumerStatsBean bean = JsonFormatUtils.fromJson(jsonData.toString(), MqConsumerStatsBean.class);
    	if(bean.getMergeTopicData() == null || bean.getMergeTopicData().size() <= 0 ) return;
    	
    	Map<String, Object> updateMap = new HashMap<String, Object>();
    	updateMap.put("ip", ip);
    	updateMap.put("pid", pid);
    	updateMap.put("server_name", serverName);
    	updateMap.put("group_name", bean.getGroupName());
    	updateMap.put("version_id", bean.getVersionId());
    	for(String topic : bean.getMergeTopicData().keySet()) {
    		updateMap.put("topic", topic);
    		updateMap.put("diff", bean.getMergeTopicData().get(topic));
    		
    		errorMapper.saveMqInfo(updateMap);
    	}
    	
    	updateMap.put("topic", "MQ_CONSUMER_RADIO");
		updateMap.put("diff", bean.getConsumeTps());
		errorMapper.saveMqInfo(updateMap);
	}

	private void saveThread(JSONObject jsonData, String ip, String pid, String serverName) {
    	ThreadBean bean = JsonFormatUtils.fromJson(jsonData.toString(), ThreadBean.class);
    	bean.setIp(ip);
    	bean.setPid(pid);
    	bean.setSeverName(serverName);
    	bean.setCreateTime(com.panda.sport.rcs.common.DateUtils.parseDate(System.currentTimeMillis(), DateUtils.DATE_YYYY_MM_DD_HH_MM_SS));
    	errorMapper.saveThreadInfo(bean);
	}

	private void saveCPU(JSONObject jsonData, String ip, String pid, String serverName) {
        SysInfoBean sysInfoBean = JsonFormatUtils.fromJson(jsonData.toString(), SysInfoBean.class);
        SystemInfo systemInfo = new SystemInfo();
        String uuid = String.valueOf(System.currentTimeMillis());
        BeanUtils.copyProperties(sysInfoBean, systemInfo);
        systemInfo.setIp(ip);
        systemInfo.setPid(pid);
        systemInfo.setSeverName(serverName);
        systemInfo.setUuid(uuid);
        systemInfo.setCreateTime(com.panda.sport.rcs.common.DateUtils.parseDate(System.currentTimeMillis(), DateUtils.DATE_YYYY_MM_DD_HH_MM_SS));
        systemInfoMapper.insert(systemInfo);


        List<SystemServiceInfo> systemServiceInfos = new ArrayList<>();
        if (sysInfoBean.getServiceBean() != null) {
            SystemServiceInfo systemServiceInfo = new SystemServiceInfo();
            BeanUtils.copyProperties(sysInfoBean.getServiceBean(), systemServiceInfo);
            systemServiceInfo.setIp(ip);
            systemServiceInfo.setPid(pid);
            systemServiceInfo.setSeverName(serverName);
            systemServiceInfo.setUuid(uuid);
            systemServiceInfo.setSystemType(1);
            systemServiceInfo.setCreateTime(com.panda.sport.rcs.common.DateUtils.parseDate(System.currentTimeMillis(), DateUtils.DATE_YYYY_MM_DD_HH_MM_SS));
            systemServiceInfos.add(systemServiceInfo);
            //systemServiceInfoMapper.insert(systemServiceInfo);
        }
        List<ServiceInfoBean> serviceBeanList = sysInfoBean.getServiceBeanList();
        if (serviceBeanList != null && serviceBeanList.size() > 0) {
            serviceBeanList.stream().forEach(model -> {

                SystemServiceInfo systemServiceInfo = new SystemServiceInfo();
                BeanCopyUtils.copyProperties(model, systemServiceInfo);
                systemServiceInfo.setIp(ip);
                systemServiceInfo.setPid(pid);
                systemServiceInfo.setSeverName(serverName);
                systemServiceInfo.setUuid(uuid);
                systemServiceInfo.setSystemType(2);
                systemServiceInfo.setCreateTime(com.panda.sport.rcs.common.DateUtils.parseDate(System.currentTimeMillis(), DateUtils.DATE_YYYY_MM_DD_HH_MM_SS));
                systemServiceInfos.add(systemServiceInfo);
            });

        }
        if (systemServiceInfos.size() > 0) systemServiceInfoMapper.batchInsert(systemServiceInfos);
    }

    private void saveGC(JSONObject jsonData, String ip, String pid, String serverName) {
        String uuid = String.valueOf(System.currentTimeMillis());
        GarbageCollectorBean garbageCollector = JsonFormatUtils.fromJson(jsonData.toString(), GarbageCollectorBean.class);
        GarbageCollector mainCollector = new GarbageCollector();
        mainCollector.setCount(garbageCollector.getCount());
        mainCollector.setTime(garbageCollector.getTime());
//        BeanCopyUtils.copyProperties(garbageCollector, mainCollector);
        mainCollector.setIp(ip);
        mainCollector.setPid(pid);
        mainCollector.setSeverName(serverName);
        mainCollector.setUuid(uuid);
        mainCollector.setGcName("ALL");
        mainCollector.setCreateTime(com.panda.sport.rcs.common.DateUtils.parseDate(System.currentTimeMillis(), DateUtils.DATE_YYYY_MM_DD_HH_MM_SS));
        garbageCollectorMapper.insert(mainCollector);
        List<GarbageCollectorBean> list = garbageCollector.getList();
        List<GarbageCollector> collectors = new ArrayList<>();
        if (list.size() > 0) {
            list.stream().forEach(model -> {
                GarbageCollector collector = new GarbageCollector();
                collector.setCount(model.getCount());
                collector.setTime(model.getTime());
                collector.setGcName(model.getGcName());
                collector.setIp(ip);
                collector.setPid(pid);
                collector.setSeverName(serverName);
                collector.setUuid(uuid);
                collector.setCreateTime(com.panda.sport.rcs.common.DateUtils.parseDate(System.currentTimeMillis(), DateUtils.DATE_YYYY_MM_DD_HH_MM_SS));
                
                collectors.add(collector);
                garbageCollectorMapper.insert(collector);
            });
        }
    }

    private void saveMemory(JSONObject jsonData, String ip, String pid, String serverName) {
        Memory memory = JsonFormatUtils.fromJson(jsonData.toString(), Memory.class);
        memory.setIp(ip);
        memory.setPid(pid);
        memory.setSeverName(serverName);
        memory.setCreateTime(com.panda.sport.rcs.common.DateUtils.parseDate(System.currentTimeMillis(), DateUtils.DATE_YYYY_MM_DD_HH_MM_SS));
        memoryMapper.insert(memory);
    }
    private void saveError(JSONObject jsonData, String ip, String pid, String serverName) {
        try {
            ErrorMqBean error = new ErrorMqBean();
            error.setCurrentDate(jsonData.getString("currentDate"));
            error.setLogContent(jsonData.getString("logContent"));
            error.setIp(ip);
            error.setPid(pid);
            error.setServerName(serverName);
            error.setCreateTime(com.panda.sport.rcs.common.DateUtils.parseDate(System.currentTimeMillis(), DateUtils.DATE_YYYY_MM_DD_HH_MM_SS));
            errorMapper.saveError(error);
        }catch (Exception e){
            log.info("保存错误日志失败,jsonData:{}",JSONObject.toJSONString(jsonData));
            log.error("保存错误日志失败"+ e);
        }
    }
    private void saveHeart(JSONObject jsonData, String ip, String pid, String serverName) {
        try {
            HeartMqBean heart = new HeartMqBean();
            heart.setIp(ip);
            heart.setPid(pid);
            heart.setCurrentTime(jsonData.getString("currentTime"));
            heart.setServerName(serverName);
            heartMapper.saveHeart(heart);
        }catch (Exception e){
            log.info("保存心跳信息报错,jsonData:{}",JSONObject.toJSONString(jsonData));
            log.error("保存心跳信息报错" + e);
        }
    }
}
