package com.panda.sport.rcs.pojo.danger;

import lombok.Data;

/**
 * 危险ip
 */
@Data
public class RcsDangerIp {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 危险Ip
     */
    private String dangerIp;

    /**
     * 创建时间
     */
    private Long createTime = System.currentTimeMillis();

}
