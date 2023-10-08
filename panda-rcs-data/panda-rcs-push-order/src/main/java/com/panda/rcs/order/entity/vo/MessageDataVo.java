package com.panda.rcs.order.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageDataVo implements Serializable {

    private Integer [] msgType;

    private Integer sportId;

    private Integer matchType;
}
