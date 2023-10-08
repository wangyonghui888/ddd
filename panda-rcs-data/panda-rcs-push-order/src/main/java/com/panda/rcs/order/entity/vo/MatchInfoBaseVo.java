package com.panda.rcs.order.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MatchInfoBaseVo implements Serializable {

    private String matchId;

    private Long modifyTime;
}
