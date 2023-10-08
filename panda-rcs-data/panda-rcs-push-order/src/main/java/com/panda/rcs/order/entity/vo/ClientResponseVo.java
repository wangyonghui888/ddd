package com.panda.rcs.order.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ClientResponseVo implements Serializable {

    /**
     * 消息版本号，每次有新查询条件versionNo置为0
     */
    private Long time = System.currentTimeMillis();

    /**
     * 推送数据的命令
     */
    private Integer command;

    /**
     * 推送数据的msgId  用于ack机制
     */
    private String  msgId;

    /**
     * 唯一链路Id
     */
    private String globalId;

    private Integer ack = 0;

    /**
     * 上行请求的数据（如：查询条件）
     */
    private Object requestData;

    /**
     * 响应的业务数据
     * <p>
     * responseData数据：
     * 1.盘口赔率：
     * List<MatchMarketLiveOddsVo.MatchMarketCategoryVo>
     * 2.赛事状态：
     * StandardMatchStatusVo
     *
     **/
    private Object responseData;

}
