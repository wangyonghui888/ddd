package com.panda.rcs.order.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class ClientRequestVo implements Serializable {

    private Integer ack;

    private Integer [] acks;

    private Integer protocolVersion;

    private Integer command;

    private String uuid;

    private Long time = System.currentTimeMillis();

    private Map<Integer, List<SingleSubInfoVo>> subscribe;

    private Integer [] commands;

    private Integer [] needCommands;

    private Integer[] marketCategoryIds;

    private Integer[] currentMatchIds;

    private Long timestamp;

    private String msgId;

}
