package com.panda.sport.rcs.entity;

import lombok.Data;

import java.util.List;

/**
 * 红猫盘口
 */
@Data
public class RedCatMarks {
    public Integer marketId;
    public List<RedCatOdds> odds;
    public String isSuspended;
    public String isClosed;
}
