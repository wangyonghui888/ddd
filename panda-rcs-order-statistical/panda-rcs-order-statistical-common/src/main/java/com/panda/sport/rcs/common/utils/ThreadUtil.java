package com.panda.sport.rcs.common.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * 线程池配置
 * @author  lithan
 */
public final class ThreadUtil {
    //线程数量
    private static final int THREAD_NUM = 100;
    private static ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("threadName-%d").build();
    private static ThreadPoolExecutor threadPool  = new ThreadPoolExecutor(THREAD_NUM, THREAD_NUM, 0L, TimeUnit.MILLISECONDS,
                                                 new LinkedBlockingQueue<Runnable>(), threadFactory);

    public static void submit(Runnable task) {
        threadPool.execute(task);
    }

    public static void destory() {
        threadPool.shutdown();
    }

    public static Integer size(){
        return threadPool.getQueue().size();
    }
}
