package com.panda.sport.rcs.monitor.service.impl;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.panda.sport.rcs.monitor.entity.GarbageCollectorBean;
import com.panda.sport.rcs.monitor.entity.MemoryPoolBean;
import com.panda.sport.rcs.monitor.service.GarbageCollectorService;

/**
 * @author tycoding
 * @date 2019-05-11
 */
@Service
public class GarbageCollectorServiceImpl implements GarbageCollectorService {

    @Override
    public GarbageCollectorBean get() {
        GarbageCollectorBean gcBean = new GarbageCollectorBean();
        List<GarbageCollectorBean> list = new ArrayList<GarbageCollectorBean>();
        List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        garbageCollectorMXBeans.forEach(bean -> {
            gcBean.setCount(bean.getCollectionCount() + gcBean.getCount());
            gcBean.setTime(bean.getCollectionTime() + gcBean.getTime());
            
            GarbageCollectorBean tempBean = new GarbageCollectorBean();
            tempBean.setCount(bean.getCollectionCount());
            tempBean.setGcName(bean.getName());
            tempBean.setTime(bean.getCollectionTime());
            list.add(tempBean);
        });
        gcBean.setList(list);
        return gcBean;
    }

    @Override
    public List<MemoryPoolBean> getPools() {
        List<MemoryPoolBean> list = new ArrayList<>();
        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        memoryPoolMXBeans.forEach(bean -> {
            MemoryPoolBean poolBean = new MemoryPoolBean();
            poolBean.setName(bean.getName());
            poolBean.setManageNames(Arrays.toString(bean.getMemoryManagerNames()));
            poolBean.setUsed(bean.getUsage().getUsed());
            poolBean.setMax(bean.getUsage().getMax());
            poolBean.setCommitted(bean.getUsage().getCommitted());
            list.add(poolBean);
        });
        return list;
    }
}
