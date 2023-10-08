package com.panda.sport.rcs.monitor.service.impl;

import com.panda.sport.rcs.monitor.entity.ClassLoaderBean;
import com.panda.sport.rcs.monitor.service.ClassLoaderService;
import org.springframework.stereotype.Service;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;

/**
 * @author tycoding
 * @date 2019-05-10
 */
@Service
public class ClassLoaderServiceImpl implements ClassLoaderService {

    @Override
    public ClassLoaderBean get() {
        return init();
    }

    private ClassLoaderBean init() {
        ClassLoaderBean bean = new ClassLoaderBean();
        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        bean.setLoaded(classLoadingMXBean.getTotalLoadedClassCount());
        bean.setCount(classLoadingMXBean.getLoadedClassCount());
        bean.setUnLoaded(classLoadingMXBean.getUnloadedClassCount());
        bean.setIsVerbose(classLoadingMXBean.isVerbose());
        return bean;
    }
}
