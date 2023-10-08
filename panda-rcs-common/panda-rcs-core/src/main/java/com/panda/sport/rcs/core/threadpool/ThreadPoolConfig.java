package com.panda.sport.rcs.core.threadpool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {

    /**
     * 异步处理方法使用的线程池
     *
     * 线程池对拒绝任务（无线程可用）的处理策略：
     * AbortPolicy:直接抛出java.util.concurrent.RejectedExecutionException异常
     * CallerRunsPolicy:若已达到待处理队列长度，将由主线程直接处理请求
     * DiscardOldestPolicy:抛弃旧的任务；会导致被丢弃的任务无法再次被执行
     *DiscardPolicy:抛弃当前任务；会导致被丢弃的任务无法再次被执行
     */
    @Bean(name = "asyncPoolTaskExecutor")
    public ThreadPoolTaskExecutor getAsyncThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(256);
        taskExecutor.setMaxPoolSize(256);
        taskExecutor.setQueueCapacity(20000);
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setThreadNamePrefix("rcs-asyncPool-");
        // CallerRunsPolicy:若已达到待处理队列长度，将由主线程直接处理请求
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }
}
