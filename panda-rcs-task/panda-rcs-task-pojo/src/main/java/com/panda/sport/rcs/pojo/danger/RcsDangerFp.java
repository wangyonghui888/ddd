package com.panda.sport.rcs.pojo.danger;

import lombok.Data;

@Data
public class RcsDangerFp {

    private Long id;

    private String fpId;

    private Integer fpLevel;

    private Long createTime = System.currentTimeMillis();

}
