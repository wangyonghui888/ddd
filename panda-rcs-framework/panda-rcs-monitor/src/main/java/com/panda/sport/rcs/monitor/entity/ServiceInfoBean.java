package com.panda.sport.rcs.monitor.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ServiceInfoBean implements Serializable {

    /**
     * 主键
     */
    private Long id;
    
    private String pid;
    private String user;
    private String pr;
    private String ni;
    private String virt;
    private String res;
    private String shr;
    private String s;
    private String cpu;
    private String mem;
    private String time;
    
    private String stackInfo;
}
