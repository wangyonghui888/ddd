package com.panda.sport.rcs.console.pojo;

import lombok.Data;

/**
 * @author :  Sean
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.console.pojo
 * @Description :  TODO
 * @Date: 2020-03-12 16:31
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class HeartMqBean extends BaseBean {

    private static final long serialVersionUID = 1L;

    private String currentTime;

    private Integer instanceCount;

    private String serverStatus;
    
    private String startNode;
    
    private String healthStatus;
    
    private String sumDiff;
    
    private String tps;
}
