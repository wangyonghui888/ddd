package com.panda.sport.rcs.console.pojo;

import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * JVM垃圾回收信息
 */
@Data
@Table(name = "rcs_monitor_garbage_collector")
public class GarbageCollector implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

    /**
     * GC回收次数
     */
    private Long count = 0l;

    /**
     * GC回收耗时
     */
    private Long time = 0l;

    private String gcName;

    private String ip;

    private String pid;

    private String uuid;

    private String severName;

    private String createTime;


}
