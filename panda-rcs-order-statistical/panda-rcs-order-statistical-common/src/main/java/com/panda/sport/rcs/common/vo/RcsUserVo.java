package com.panda.sport.rcs.common.vo;

import java.io.Serializable;

/**
 * <p>
 * 周区间
 * </p>
 *
 * @author lithan auto
 * @since 2020-06-26
 */
public class RcsUserVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer userId;

    private String userNme;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName(){
        return userNme;
    }

    public void setUserName(String userNme) {
        this.userNme = userNme;
    }
}
