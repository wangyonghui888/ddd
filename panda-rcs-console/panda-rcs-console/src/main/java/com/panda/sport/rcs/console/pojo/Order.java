package com.panda.sport.rcs.console.pojo;

import lombok.Data;

@Data
public class Order {
	
	private String orderNo ;
	private String tenantId;
	private String uid;
	private String orderStatus;
	private String infoStatus;
	private String createTime;
	private String reason;
	private String seriesType;
	private String orderAmountTotal;
	private String ip;
	private String betNo;
	private String matchId;
	private String playId;
	private String marketId;
	private String marketValue;
	private String oddsValue;
	private String playOptionsId;
	private String recVal;
	private String isAcct;
	private String accOrderStatus;
	private String mode;
	private String handleStatus;
	private String accTime;
	private String virtualAmount;
	private String paAmount;
	private String remark;
	private String responseParam;
	private String betAmount;
}
