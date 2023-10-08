package com.panda.sport.rcs.third.entity.beter.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Beulah
 * @date 2023/3/21 13:20
 * @description beter投注接口 vo
 */
@Data
public class BeterPlaceBetResultVo implements Serializable {

    private static final long serialVersionUID = 1L;

    //注单id
    private String id;

    //投注项状态
    private List<Map<String,String>> bets;

    //订单状态
    private Integer status;

    //错误码
    private String errorCode;

    //滚球延迟
    private Integer delay;



}
