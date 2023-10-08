/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.panda.sport.rcs.mts.sportradar.constants;

//import com.sportradar.mts.sdk.api.interfaces.SdkConfiguration;

public class Constants {
//
//    public static int BOOKMAKER_ID = 28483;
//    public static int LIMIT_ID = 2726;

	public final static String MTS_ORDER_ODDSCHANGETYPE = "rcs:mts:order:oddsChangeType:orderNo.%s";
	public final static String MTS_ORDER_EXPIRE = "rcs:mts:order:expire";
	public final static String MTS_ORDER_RATE = "rcs:mts:order:rate";
	public final static String MTS_ORDER_CACHE = "rcs:mts:order:oddsChangeType:optionId.%s.odds.%s.oddsChangeType.%s";


	//按照商户级别控制是否某个商户的注单都不提交mts  1不走mts
	public final static String MTS_MERCHANT_SENDTICKET_STATUS = "rcs:mts:merchant:sendticket:status:%s";
	//按照商户级别控制是否某个商户的注单都不提交mts的订单
	public final static String MTS_MERCHANT_SENDTICKET_LIST = "rcs:mts:merchant:sendticket:list";
	//按照商户级别控制是否某个商户的注单都不提交mts的订单  是否在处理中
	public final static String MTS_MERCHANT_SENDTICKET_RUNIG = "rcs:mts:merchant:sendticket:runing";

//	private static SdkConfiguration config;
//
//	public static SdkConfiguration getConfig() {
//		return config;
//	}
//
//	public static void setConfig(SdkConfiguration config) {
//		Constants.config = config;
//	}


}
