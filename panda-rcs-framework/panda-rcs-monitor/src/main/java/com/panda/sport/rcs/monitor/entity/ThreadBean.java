package com.panda.sport.rcs.monitor.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 线程信息
 *
 * @author tycoding
 * @date 2019-05-10
 */
@Data
public class ThreadBean implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 当前线程执行时间（纳秒）
     */
    private Long currentTime;

    /**
     * 当前守护线程数量
     */
    private Integer daemonCount;

    /**
     * 当前线程总数量（包括守护线程和非守护线程）
     */
    private Integer count;
    
    /**
     * 返回自从 Java 虚拟机启动以来创建和启动的线程总数目。
     */
    private Long totalStartedThreadCount;
    
    private String ip;
    
    private String pid;
    
    private String severName;
    
    private String createTime;
    
}
