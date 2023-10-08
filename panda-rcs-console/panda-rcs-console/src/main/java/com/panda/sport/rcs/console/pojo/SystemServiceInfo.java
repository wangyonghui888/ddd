package com.panda.sport.rcs.console.pojo;

import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

@Data
@Table(name = "rcs_monitor_service_info")
public class SystemServiceInfo implements Serializable {
    private static final long serialVersionUID = 1L;
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
    private String uuid;
    /**
     * 1主 2 次
     */
    private Integer systemType;

    private String severName;

    private String ip ;

    private String stackInfo;

    private String createTime;

}
