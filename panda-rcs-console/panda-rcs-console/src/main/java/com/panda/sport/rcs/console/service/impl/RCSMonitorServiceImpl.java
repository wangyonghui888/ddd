package com.panda.sport.rcs.console.service.impl;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.panda.sport.rcs.console.dao.GarbageCollectorMapper;
import com.panda.sport.rcs.console.dao.MemoryMapper;
import com.panda.sport.rcs.console.dao.SystemInfoMapper;
import com.panda.sport.rcs.console.dao.SystemServiceInfoMapper;
import com.panda.sport.rcs.console.dto.JVMQueryDTO;
import com.panda.sport.rcs.console.pojo.GarbageCollector;
import com.panda.sport.rcs.console.pojo.HeartMqBean;
import com.panda.sport.rcs.console.pojo.Memory;
import com.panda.sport.rcs.console.pojo.SystemInfo;
import com.panda.sport.rcs.console.pojo.SystemServiceInfo;
import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.console.service.RCSMonitorService;

import tk.mybatis.mapper.entity.Example;

@Service
public class RCSMonitorServiceImpl implements RCSMonitorService {

    @Autowired
    SystemInfoMapper systemInfoMapper;
    @Autowired
    SystemServiceInfoMapper systemServiceInfoMapper;
    @Autowired
    GarbageCollectorMapper garbageCollectorMapper;
    @Autowired
    MemoryMapper memoryMapper;


    @Override
    public Object getMemory(JVMQueryDTO jVMQueryDTO) {
        if(StringUtils.isBlank(jVMQueryDTO.getServerName())) return null;
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.HOUR_OF_DAY, -12);
        String sTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(instance.getTime());
        jVMQueryDTO.setCreateTime(sTime);
        List<String> serverNames = Arrays.asList(jVMQueryDTO.getServerName().split("/"));

        Example example = new Example(Memory.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("severName", serverNames.get(0));
        criteria.andEqualTo("ip", serverNames.get(1));
        criteria.andEqualTo("pid", serverNames.get(2));
        criteria.andGreaterThan("createTime", jVMQueryDTO.getCreateTime());
        example.setOrderByClause("create_time desc");
        if (StringUtils.isBlank(jVMQueryDTO.getOne())) {
            return null;
        } else {
            if (jVMQueryDTO.getOne().equals("1")) {
            	example.setOrderByClause(example.getOrderByClause() + " limit 1");
                Memory memory = memoryMapper.selectOneByExample(example);
                return memory;
            }
            if (jVMQueryDTO.getOne().equals("2")) {
            	example.setOrderByClause(example.getOrderByClause() + " limit 1000");
                List<Memory> memories = memoryMapper.selectByExample(example);
                return memories;
            }
            return null;
        }
    }

    @Override
    public Object getServiceInfo(JVMQueryDTO jVMQueryDTO) {
        if(StringUtils.isBlank(jVMQueryDTO.getServerName())) return null;
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.HOUR_OF_DAY, -12);
        String sTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(instance.getTime());
        jVMQueryDTO.setCreateTime(sTime);
        List<String> serverNames = Arrays.asList(jVMQueryDTO.getServerName().split("/"));

        Example example = new Example(SystemServiceInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("severName", serverNames.get(0));
        criteria.andEqualTo("ip", serverNames.get(1));
        criteria.andEqualTo("pid", serverNames.get(2));
        criteria.andGreaterThan("createTime", jVMQueryDTO.getCreateTime());
        example.setOrderByClause("create_time desc");
        if (StringUtils.isBlank(jVMQueryDTO.getOne())) {
            return null;
        } else {
            if (jVMQueryDTO.getOne().equals("1")) {
            	example.setOrderByClause(example.getOrderByClause() + " limit 1");
                SystemServiceInfo systemServiceInfo = systemServiceInfoMapper.selectOneByExample(example);
                return systemServiceInfo;
            }
            if (jVMQueryDTO.getOne().equals("2")) {
            	example.setOrderByClause(example.getOrderByClause() + " limit 1000");
                List<SystemServiceInfo> systemServiceInfos = systemServiceInfoMapper.selectByExample(example);
                return systemServiceInfos;
            }
            return null;
        }
    }

    @Override
    public Object getSystem(JVMQueryDTO jVMQueryDTO) {
        if(StringUtils.isBlank(jVMQueryDTO.getServerName())) return null;
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.HOUR_OF_DAY, -12);
        String sTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(instance.getTime());
        jVMQueryDTO.setCreateTime(sTime);
        List<String> serverNames = Arrays.asList(jVMQueryDTO.getServerName().split("/"));

        Example example = new Example(SystemInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("severName", serverNames.get(0));
        criteria.andEqualTo("ip", serverNames.get(1));
        criteria.andEqualTo("pid", serverNames.get(2));
        criteria.andGreaterThan("createTime", jVMQueryDTO.getCreateTime());
        example.setOrderByClause("create_time desc");
        if (StringUtils.isBlank(jVMQueryDTO.getOne())) {
            return null;
        } else {
            if (jVMQueryDTO.getOne().equals("1")) {
            	example.setOrderByClause(example.getOrderByClause() + " limit 1");
                SystemInfo systemInfo = systemInfoMapper.selectOneByExample(example);
                return systemInfo;
            }
            if (jVMQueryDTO.getOne().equals("2")) {
            	example.setOrderByClause(example.getOrderByClause() + " limit 1000");
                List<SystemInfo> systemInfos = systemInfoMapper.selectByExample(example);
                return systemInfos;
            }
            return null;
        }
    }

    @Override
    public Object getGC(JVMQueryDTO jVMQueryDTO) {
        if(StringUtils.isBlank(jVMQueryDTO.getServerName())) return null;
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.HOUR_OF_DAY, -12);
        String sTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(instance.getTime());
        jVMQueryDTO.setCreateTime(sTime);
        List<String> serverNames = Arrays.asList(jVMQueryDTO.getServerName().split("/"));

        Example example = new Example(GarbageCollector.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("severName", serverNames.get(0));
        criteria.andEqualTo("ip", serverNames.get(1));
        criteria.andEqualTo("pid", serverNames.get(2));
        criteria.andEqualTo("gcName","ALL");
        criteria.andGreaterThan("createTime", jVMQueryDTO.getCreateTime());
        example.setOrderByClause("create_time desc ");
        if (StringUtils.isBlank(jVMQueryDTO.getOne())) {
            return null;
        } else {
            if (jVMQueryDTO.getOne().equals("1")) {
            	example.setOrderByClause(example.getOrderByClause() + " limit 1");
                GarbageCollector garbageCollector = garbageCollectorMapper.selectOneByExample(example);
                return garbageCollector;
            }
            if (jVMQueryDTO.getOne().equals("2")) {
            	example.setOrderByClause(example.getOrderByClause() + " limit 1000");
                List<GarbageCollector> garbageCollectors = garbageCollectorMapper.selectByExample(example);
                return garbageCollectors;
            }
            return null;
        }
    }

    @Override
    public List<HeartMqBean> getServerName() {
        return systemInfoMapper.getServerName();
    }

	@Override
	public Object getThread(JVMQueryDTO jVMQueryDTO) {
		if(StringUtils.isBlank(jVMQueryDTO.getServerName())) return null;
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.HOUR_OF_DAY, -12);
        String sTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(instance.getTime());
        jVMQueryDTO.setCreateTime(sTime);
        List<String> serverNames = Arrays.asList(jVMQueryDTO.getServerName().split("/"));

        Map<String,Object > queryMap = new HashMap<String, Object>();
        queryMap.put("severName", serverNames.get(0));
        queryMap.put("ip", serverNames.get(1));
        queryMap.put("pid", serverNames.get(2));
        queryMap.put("createTime", sTime);
        queryMap.put("type", "1");
        if("2".equals(jVMQueryDTO.getOne())) {
        	queryMap.put("type", "2");
        	List<Map<String, Object>> list = garbageCollectorMapper.queryThreadInfo(queryMap);
        	return list;
        }else {
        	List<Map<String, Object>> list = garbageCollectorMapper.queryThreadInfo(queryMap);
        	if(list == null || list.size() <= 0 ) return null;
        	return list.get(0);
        }
	}
}
