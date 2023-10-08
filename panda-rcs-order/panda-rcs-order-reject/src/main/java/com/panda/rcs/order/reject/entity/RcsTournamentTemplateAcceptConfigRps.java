package com.panda.rcs.order.reject.entity;

import lombok.Data;

/**
 * @author :  myname
 * @Project Name :  panda-rcs-order-group
 * @Package Name :  com.panda.rcs.order.reject.entity
 * @Description :  TODO
 * @Date: 2023-03-08 14:41
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class RcsTournamentTemplateAcceptConfigRps {
    /**
     * SR  BC  BG
     */
    private String dataSource;

    private  Integer  normal;
    /**
     * 最大等待时间
     */
    private Integer maxWait;
    /**
     * 最小等待时间
     */
    private Integer minWait;


}
