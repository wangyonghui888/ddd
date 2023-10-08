package com.panda.sport.rcs.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author :  skyKong
 * @Project Name :  daima1
 * @Package Name :  com.panda.sport.rcs.vo
 * @Description :  TODO
 * @Date: 2023-01-19
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class RcsUserVo implements Serializable {
    /**
     * @Description userId
     * @return
     **/
    private Integer userId;
    /**
     * @Description 用户名称
     * @return
     **/
    private String userNme;
}
