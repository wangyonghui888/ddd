package com.panda.sport.data.rcs.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author :  Felix
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.data.rcs.dto
 * @Description :  TODO
 * @Date: 2019-10-15 10:51
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class RcsMarketCategorySetMarginDTO implements Serializable {

    private Long id;

    /**
     * 时间节点(换算成按小时存)
     */
    private Integer timeFrame;

    /**
     * 抽水值
     */
    private Integer margin;

    /**
     * 玩法集ID
     */
    private Long marketCategorySetId;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long modifyTime;
}
