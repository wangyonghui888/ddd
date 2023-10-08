package com.panda.sport.rcs.pojo.bo;

import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;

/**
 * 玩法集表
 */
@Data
public class GetPerformanceSetPlaysBO extends RcsBaseEntity<GetPerformanceSetPlaysBO> {

    /**
     * 玩法id
     */
    private Long playId;
    /**
     * 玩法集id
     */
    private Long setNo;

}
