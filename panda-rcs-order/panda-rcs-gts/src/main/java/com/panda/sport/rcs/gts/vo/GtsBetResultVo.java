package com.panda.sport.rcs.gts.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 第三方返回转换对象
 */
@Data
public class GtsBetResultVo implements Serializable {
	private static final long serialVersionUID = 1L;
    /**
     * 第三方订单号
     */
    private String tickeId;

    /**
     * 第三方返回原始数据
     */
    private String thridValue;


    /**
     * 第三方成功/失败  描述
     */
    private String message;

    /**
     * 第三方状态  ACCEPTED/REJECTED
     */
    private String status;

    /**
     * 需要延迟接单的秒数
     */
    private Integer delayTime;


}
