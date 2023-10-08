package com.panda.sport.data.rcs.dto.virtual;

import lombok.Data;

/**
 * 获取虚拟赛事  投注  请求VO
 *
 * @description:
 * @author: lithan
 * @date: 2020-12-22 14:41:29
 */
@Data
public class BetItemReqVo implements java.io.Serializable {

    /**
     * 注单号
     */
    String betNo;

    /**
     * 赛种
     */
    Integer sportId;

    /**
     * 类似于 联赛ID
     */
    Long playListId;

    /**
     * 赛事ID
     */
    Long eventId;

    /**
     * 盘口
     */
    String marketId;
    /**
     * 投注项
     */
    String oddId;
    /**
     * 赔率
     */
    String oddValue;
    /**
     * 金额
     */
    Long stake;

    /**
     * 运动种类名称
     */
    private String sportName;

    /**
     * 对阵信息
     */
    private String matchInfo;

    /**
     * 投注项名称，直接保存业务系统传递的值
     */
    private String playOptionsName;

    /**
     * 最高可赢金额
     */
    private Long maxWinAmount;


}