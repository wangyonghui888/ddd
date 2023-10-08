package com.panda.sport.rcs.monitor.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SysInfoBean implements Serializable {

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
    
    private ServiceInfoBean serviceBean;
    
    private List<ServiceInfoBean> serviceBeanList;
    
    private String uuid;
}
