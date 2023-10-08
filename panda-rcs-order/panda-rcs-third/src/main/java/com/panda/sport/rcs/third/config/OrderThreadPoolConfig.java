package com.panda.sport.rcs.third.config;

import jodd.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;


/**
 * 线程池管理
 */
@Configuration
@Slf4j
public class OrderThreadPoolConfig {


    /**
     * 订单取消 请求池
     */
    @Bean("cancelPoolExecutor")
    public ThreadPoolExecutor cancelPoolExecutor() {
        //int cpuNum = Runtime.getRuntime().availableProcessors()==1 ? 16 : Runtime.getRuntime().availableProcessors();
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("取消池" + "-%d")
                .setDaemon(true).get();
        //1、核心线程 2、最大线程 3、空闲等待时间 4、等待时间单位 5、等待队列 6、线程工厂 7、拒绝策略
        return new ThreadPoolExecutor(
                100, 500, 20, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100), threadFactory,
                new ThreadPoolExecutor.AbortPolicy());

    }

    /**
     * 投注请求池
     */
    @Bean("betPoolExecutor")
    public ThreadPoolExecutor betPoolExecutor() {
        //int cpuNum = Runtime.getRuntime().availableProcessors()==1 ? 16 : Runtime.getRuntime().availableProcessors();
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("投注池" + "-%d")
                .setDaemon(true).get();
        //1、核心线程 2、最大线程 3、空闲等待时间 4、等待时间单位 5、等待队列 6、线程工厂 7、拒绝策略
        return new ThreadPoolExecutor(
                500, 3000, 60, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100), threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 订单确认请求池
     */
    @Bean("confirmPoolExecutor")
    public ThreadPoolExecutor confirmPoolExecutor() {
        //int cpuNum = Runtime.getRuntime().availableProcessors() == 1 ? 16 : Runtime.getRuntime().availableProcessors();
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("确认池" + "-%d")
                .setDaemon(true).get();
        //1、核心线程 2、最大线程 3、空闲等待时间 4、等待时间单位 5、等待队列 6、线程工厂 7、拒绝策略
        return new ThreadPoolExecutor(
                300, 1500, 30, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100), threadFactory,
                new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean("redCatNotifyPoolExecutor")
    public ThreadPoolExecutor redCatNotifyPoolExecutor() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("redCat确认池" + "-%d")
                .setDaemon(true).get();
        //1、核心线程 2、最大线程 3、空闲等待时间 4、等待时间单位 5、等待队列 6、线程工厂 7、拒绝策略
        return new ThreadPoolExecutor(
                64, 256, 30, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1024), threadFactory,
                new ThreadPoolExecutor.AbortPolicy());
    }

}