package com.panda.sport.rcs.third.monitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 投注线程池
 *
 * @author CL
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderThread {

    private int corePoolSize;
    private int maximumPoolSize;
    private int poolSize;
    private int queueSize;
    private long taskCount;
    private int activeCount;
    private long completedTaskCount;

    @Override
    public String toString() {
        return Layout.Table.of(
                Layout.Row.of("核心线程数", corePoolSize),
                Layout.Row.of("最大线程数", maximumPoolSize),
                Layout.Row.of("运行线程数", poolSize),
                Layout.Row.of("活跃线程数", activeCount),
                Layout.Row.of("任务数大小", taskCount),
                Layout.Row.of("任务完成数", completedTaskCount),
                Layout.Row.of("队列等待数", queueSize)
        ).toString();
    }

}