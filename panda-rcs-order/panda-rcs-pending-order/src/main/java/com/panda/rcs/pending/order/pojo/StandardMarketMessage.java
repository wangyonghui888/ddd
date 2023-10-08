package com.panda.rcs.pending.order.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author black
 * @ClassName: StandardMarketMessage
 * @Description: TODO
 * @date 2020年8月11日 上午10:10:00
 * @see com.panda.merge.dto.message.StandardMarketMessage
 */
@Data
public class StandardMarketMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 标准盘口id
     * 非空
     */
    private Long id;

    /**
     * 附加字段1
     */
    private String addition1;

    /**
     * 通过以上三种状态加上操盘赛事状态得出的最终状态
     * 盘口状态0-5. 0:active 开盘, 1:suspended 封盘, 2:deactivated 关盘, 3:settled 已结算, 4:cancelled 已取消, 5:handedOver  盘口的中间状态，该状态的盘口后续不会有赔率过来 11:锁盘状态
     */
    private Integer status;

    /**
     * 盘口投注项
     */
    private List<StandardMarketOddsMessage> marketOddsList;
}
