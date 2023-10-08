package com.panda.sport.rcs.limit.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 可用限额查询入参
 * @Author : Paca
 * @Date : 2021-12-05 15:58
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class AvailableLimitQueryReqVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商户编码
     */
    private String merchantCode;

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
    private Long playId;
}
