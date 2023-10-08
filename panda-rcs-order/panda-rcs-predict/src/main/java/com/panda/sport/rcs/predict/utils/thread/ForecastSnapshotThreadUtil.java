package com.panda.sport.rcs.predict.utils.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置 forecast快照专用
 *
 * @author joey
 */
public final class ForecastSnapshotThreadUtil {
    //线程数量
    private static final int THREAD_NUM = 50;
    private final static ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("threadName-%d").build();
    private final static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(THREAD_NUM, THREAD_NUM, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), threadFactory);

    public static void submit(Runnable task) {
        threadPool.execute(task);
    }

    public static void destory() {
        threadPool.shutdown();
    }

    public static Integer size() {
        return threadPool.getQueue().size();
    }
}