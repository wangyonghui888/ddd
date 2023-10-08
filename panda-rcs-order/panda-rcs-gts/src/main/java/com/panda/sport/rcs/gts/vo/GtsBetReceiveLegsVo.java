package com.panda.sport.rcs.gts.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * gts Bet Receive api 入参
 *
 * @author z9-lithan
 * @date 2023-01-07 16:31:22
 */
@Data
public class GtsBetReceiveLegsVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 投注边的当前状态，支持的值：["Open"/"Settled"/"Cancelled"]。
     */
    private String status;
    /**
     * 投注项金额
     */
    private Long price;
    /**
     * 早盘 PreMatch 滚球 InPlay
     */
    private String gameState;
    /**
     * betgeniusContent参数对象
     */
    private GtsBetGeniusContentVo betgeniusContent;
    /**
     * bookmakerContent 参数对象
     */
    private BookmakerContentContentVo bookmakerContent;

}
