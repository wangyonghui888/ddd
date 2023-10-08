package com.panda.sport.rcs.pojo.tourTemplate;

import lombok.Data;
import lombok.NonNull;

/**
 * @author :  myname
 * @Project Name :  panda-rcs-trade-group
 * @Package Name :  com.panda.sport.rcs.pojo.tourTemplate
 * @Description :  TODO
 * @Date: 2023-03-07 15:06
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class QueryEventConfigReq {

    /**
     * 玩法集id
     */
    private Integer categorySetId;
    /**
     * 1.常规接距 2.提前结算接距
     */
    private Integer rejectType;

    /**
     * 赛事id,如果不传只返回接拒配置，反之返回接拒配置和等待时间
     */
    private Integer matchId;
}
