package com.panda.sport.data.rcs.dto.virtual;

import lombok.Data;

/**
 * 获取虚拟赛事  投注  返回VO
 *
 * @description:
 * @author: lithan
 * @date: 2020-12-22 14:41:29
 */
@Data
public class BetResVo implements java.io.Serializable {

    public BetResVo(String orderNo,int orderStatus){
        this.orderNo = orderNo;
        this.orderStatus = orderStatus;
    }
    /**
     * 订单号
     */
    String orderNo;

    /**
     * 订单状态: (0 失败  1：成功  2：处理中)  
     */
    int orderStatus;
    
}