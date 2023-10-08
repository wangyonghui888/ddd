package com.panda.sport.rcs.monitor.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * JVM垃圾回收信息
 *
 * @author tycoding
 * @date 2019-05-10
 */
@Data
public class GarbageCollectorBean implements Serializable {

    /**
     * GC回收次数
     */
    private Long count = 0l;

    /**
     * GC回收耗时
     */
    private Long time = 0l;
    
    private String gcName;
    
    List<GarbageCollectorBean> list;
}
