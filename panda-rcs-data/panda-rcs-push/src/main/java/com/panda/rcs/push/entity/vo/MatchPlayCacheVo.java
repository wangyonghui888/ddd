package com.panda.rcs.push.entity.vo;

import lombok.Data;

import java.util.Map;

@Data
public class MatchPlayCacheVo {

    private String matchId;

    private Long createTime;

    private Map<Long, Long> playUpdateTime;
}
