package com.panda.sport.rcs.monitor.service.impl;

import com.panda.sport.rcs.monitor.entity.RuntimeBean;
import com.panda.sport.rcs.monitor.service.RuntimeInfoService;
import com.panda.sport.rcs.monitor.utils.JSONTemplate;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tycoding
 * @date 2019-05-10
 */
@Service
public class RuntimeInfoServiceImpl implements RuntimeInfoService {

    @Override
    public RuntimeBean get() {
        RuntimeBean bean = new RuntimeBean();
        RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();
        bean.setHost(mxBean.getName() + " " + mxBean.getSystemProperties().get("os.name") + " " + mxBean.getSystemProperties().get("os.version"));
        bean.setJvm(mxBean.getVmName() + " " + mxBean.getVmVendor());
        bean.setArgs(mxBean.getInputArguments());
        bean.setVersion(mxBean.getSystemProperties().get("java.runtime.version") + " " + mxBean.getSystemProperties().get("java.runtime.name"));
        bean.setHome(mxBean.getSystemProperties().get("java.home"));
        bean.setStartTime(mxBean.getStartTime());

        List<JSONTemplate> jsonTemplates = new ArrayList<>();
        mxBean.getSystemProperties().forEach((key, value) -> {
            JSONTemplate format = new JSONTemplate();
            format.setKey(key);
            format.setValue(value);
            jsonTemplates.add(format);
        });
        bean.setProperties(jsonTemplates);
        return bean;
    }
}
