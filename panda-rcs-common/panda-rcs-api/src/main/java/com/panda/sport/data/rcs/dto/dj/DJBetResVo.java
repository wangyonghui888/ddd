package com.panda.sport.data.rcs.dto.dj;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @ClassName DJBetResVo
 * @Description TODO
 * @Author Administrator
 * @Date 2021/9/21 22:10
 * @Version 1.0
 **/
@Data
public class DJBetResVo implements Serializable {

    public DJBetResVo(String orderNo, int orderStatus, Map<String, List<Long>> id_mapping, Map<String, Double> oddsMap){
        this.orderNo = orderNo;
        this.orderStatus = orderStatus;
        this.id_mapping = id_mapping;
        this.oddsMap = oddsMap;
    }
    /**
     * 订单号
     */
    String orderNo;

    /**
     * 订单状态: (0 失败  1：成功  2：处理中)
     */
    int orderStatus;

    /**
     * 注单映射关系
     */
    private Map<String, List<Long>> id_mapping;

    /**
     * 注单映射关系
     */
    private Map<String, Double> oddsMap;
}
