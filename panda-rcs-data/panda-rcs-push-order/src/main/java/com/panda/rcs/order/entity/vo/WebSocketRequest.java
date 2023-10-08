package com.panda.rcs.order.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class WebSocketRequest implements Serializable {
    
    /**
     * 页面标识
     */
    private String uuid;

    /**
     * 请求指令
     */
    private List<Integer> commands;

    private String sessionId;
    /**
     * 节点id
     */
    private String nodeId;

    /**
     * 30001实时赛事状态
     * 30002实时赛事结果
     * 30003实时盘中事件
     * 30013实时盘中事件
     * 30004实时盘口赔率-玩法ids
     * 30005实时盘口赔率-所有玩法 (赔率返参命令类型不返30004,只返30005)
     * 30006实时注单
     * 30007平衡值
     * 30008保存盈利值、实货量
     */
    private List<Integer> needCommands;


    private Integer needCommand;


    private Long versionNo;

    /**
     * 新版协议字段，协议版本
     */
    private Integer protocolVersion;

    /**
     * 新版协议字段，协议版本
     */
    private Map<String,List> subscribe;

    /**
     * ack机制
     */
    private List<Integer> acks;
    /**
     * ack机制
     */
    private Boolean isAck;

    /**
     * 赛事id
     */
    private Long matchId;


}
