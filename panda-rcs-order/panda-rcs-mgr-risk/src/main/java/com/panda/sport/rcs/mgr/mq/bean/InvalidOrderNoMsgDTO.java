package com.panda.sport.rcs.mgr.mq.bean;

import lombok.Data;
import lombok.ToString;

/**
 * @author Regan
 * @create 2023/9/15 15:24
 */
@Data
@ToString
public class InvalidOrderNoMsgDTO {
        //订单号
        private String orderNo;

        //0.首次;1.再次
        private Integer status = 0;
}
