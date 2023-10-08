package com.panda.sport.rcs.task.config;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.*;

/**
 * @author :  holly
 * @Project Name :panda-rcs-new
 * @Package Name :com.panda.sport.rcs.task.config
 * @Description :
 * @Date: 2020-09-16 15:10
 */
@Configuration
public class ScheduleExecutorConfig implements SchedulingConfigurer  {
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        ScheduledExecutorService scheduledExecutorService =
                new ScheduledThreadPoolExecutor(100,new BasicThreadFactory.Builder().namingPattern("rcs-task-pool-%d").daemon(true).build());
        scheduledTaskRegistrar.setScheduler(scheduledExecutorService);
    }
}
