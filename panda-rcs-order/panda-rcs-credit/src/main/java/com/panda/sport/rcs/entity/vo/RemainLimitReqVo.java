package com.panda.sport.rcs.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 剩余额度请求入参
 * @Author : Paca
 * @Date : 2021-07-08 1:22
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class RemainLimitReqVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类型，1-单关，2-串关
     */
    private Integer type;

    /**
     * 信用代理ID
     */
    private String creditAgentId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 赛事管理ID
     */
    private String matchManageId;

    /**
     * 玩法ID
     */
    private Integer playId;
}
