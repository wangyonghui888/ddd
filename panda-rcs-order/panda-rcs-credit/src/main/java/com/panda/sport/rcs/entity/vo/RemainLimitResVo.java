package com.panda.sport.rcs.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 剩余额度返回出参
 * @Author : Paca
 * @Date : 2021-07-08 1:29
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class RemainLimitResVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 串关-用户可用额度
     */
    private Map<Integer, Long> seriesUserLimit;

    /**
     * 串关-代理可用额度
     */
    private Map<Integer, Long> seriesAgentLimit;

    /**
     * 赛事操盘方
     */
    private String riskManagerCode;

    /**
     * 单关-用户玩法可用限额
     */
    private Long singleUserPlayLimit;

    /**
     * 单关-代理玩法可用限额
     */
    private Long singleAgentPlayLimit;

    /**
     * 单关-代理单场可用限额
     */
    private Long singleAgentLimit;
}
