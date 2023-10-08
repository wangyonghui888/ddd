package com.panda.sport.rcs.pojo.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;

/*
 * 對應大數據端給出的 TyRiskOrderDTO
 */
@Data
public class RcsRiskOrderDTO implements  Serializable{
	private static final long serialVersionUID = 705624871085400981L;
	
	private String riskDesc;
    /**
     * 注单编号
     */
    private String riskType;

    /**
     * 订单单号
     */
    private String orderNo;


}
