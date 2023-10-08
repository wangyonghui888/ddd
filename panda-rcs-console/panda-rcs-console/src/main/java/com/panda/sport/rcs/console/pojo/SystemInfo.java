package com.panda.sport.rcs.console.pojo;

import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

@Data
@Table(name = "rcs_monitor_system_info")
public class SystemInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Long id;

    /**
     * 平均负载
     */
    private String loadAvg1;
    private String loadAvg2;
    private String loadAvg3;

    /**
     * cpu信息
     */
    private String usCpu;
    private String syCpu;
    private String niCpu;
    private String idCpu;
    private String waCpu;
    private String hiCpu;
    private String siCpu;
    private String stCpu;

    /**
     * 物理内存
     */
    private String totalMem;
    private String freeMem;
    private String usedMem;
    private String cacheMem;

    /**
     * 虚拟内存
     */
    private String totalSwap;
    private String freeSwap;
    private String usedSwap;
    private String cacheSwap;

    private String memoryRate;

    private String uuid;

    private String severName;

    private String ip ;

    private String pid;

    private String createTime;

}
