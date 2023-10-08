package com.panda.sport.rcs.pojo.danger;

import lombok.Data;

@Data
public class RcsDangerUser {

    private Long id;

    private Long userId;

    private Integer riskLevel;

    private Long userGroupId;

    private Long createTime = System.currentTimeMillis();

}
