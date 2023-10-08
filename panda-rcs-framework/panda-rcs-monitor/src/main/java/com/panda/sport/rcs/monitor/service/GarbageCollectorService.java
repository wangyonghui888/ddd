package com.panda.sport.rcs.monitor.service;

import com.panda.sport.rcs.monitor.entity.GarbageCollectorBean;
import com.panda.sport.rcs.monitor.entity.MemoryPoolBean;

import java.util.List;

/**
 * @author tycoding
 * @date 2019-05-11
 */
public interface GarbageCollectorService {

    GarbageCollectorBean get();

    List<MemoryPoolBean> getPools();
}
