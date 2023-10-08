package com.panda.rcs.cleanup.entity;

import lombok.Data;

@Data
public class STempRedisKey {

    private int id;

    private String redisKey;

    private String saveDate;

    private String createTime;
}
