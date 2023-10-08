package com.panda.sport.rcs.third.entity.gts;

import lombok.Data;

import java.io.Serializable;

/**
 * gts Bet Receiver api 返回
 * <p>
 *
 * @author z9-lithan
 * @date 2023-01-06 15:21:20
 */
@Data
public class GtsBetReceiverResVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 是否可以接单
     */
    private Integer code;
    /**
     * 返回数据
     */
    private String data;

}
