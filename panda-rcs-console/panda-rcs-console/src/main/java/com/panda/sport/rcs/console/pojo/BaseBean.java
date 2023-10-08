package com.panda.sport.rcs.console.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author :  Sean
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.console.pojo
 * @Description :  TODO
 * @Date: 2020-03-12 16:30
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class BaseBean implements Serializable {

    private static final long serialVersionUID = 1L;
    /**ip地址*/
    private String ip;
    /**进程id*/
    private String pid;
    /**服务名称*/
    private String serverName;
    /**创建时间*/
    private String createTime;
    /**开始时间*/
    private String startTime;
    /**结束时间*/
    private String endTime;
}
