package com.panda.sport.sdk.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * 线程池配置
 * @author  lithan
 */
public final class ThreadUtil {
    //线程数量
    private static final int THREAD_NUM = 60;
    private static ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("sdkThread-%d").build();
    private static ExecutorService threadPool  = new ThreadPoolExecutor(THREAD_NUM, THREAD_NUM, 0L, TimeUnit.MILLISECONDS,
                                                 new LinkedBlockingQueue<Runnable>(), threadFactory);

    public static void submit(Runnable task) {
        threadPool.execute(task);
    }

    public static void destory() {
        threadPool.shutdown();
    }
}
