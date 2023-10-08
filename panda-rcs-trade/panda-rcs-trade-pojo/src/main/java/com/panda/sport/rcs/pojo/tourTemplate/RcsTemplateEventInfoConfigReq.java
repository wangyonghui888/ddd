package com.panda.sport.rcs.pojo.tourTemplate;

import lombok.Data;

import java.util.List;

/**
 * @author :  myname
 * @Project Name :  panda-rcs-trade-group
 * @Package Name :  com.panda.sport.rcs.pojo.tourTemplate
 * @Description :  TODO
 * @Date: 2023-03-07 13:34
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class RcsTemplateEventInfoConfigReq {
    /**
     * 玩法集id
     */
    private Integer categorySetId;
    /**
     * 玩法集名称
     */
    private String  categorySetName;
    /**
     * 操作页面编码
     */
    private Integer  operatePageCode;
    /**
     *     修改前参数
     */
    private List<RcsTemplateEventInfoConfig>  beforeParams;
    /**
     * 1.常规接距 2.提前结算接距
     */
    private Integer rejectType;

    /**
     * 操作人ID
     */
    private String userId;

    /**
     * 需要修改的数据
     */
    List<RcsTemplateEventInfoConfig>  events;
}
