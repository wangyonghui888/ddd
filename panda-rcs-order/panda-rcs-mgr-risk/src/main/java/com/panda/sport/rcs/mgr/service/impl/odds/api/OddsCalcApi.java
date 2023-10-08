package com.panda.sport.rcs.mgr.service.impl.odds.api;

import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.StandardMarketOddsDTO;
import com.panda.sport.data.rcs.dto.ThreewayOverLoadTriggerItem;
import com.panda.sport.rcs.enums.DataSourceTypeEnum;
import com.panda.sport.rcs.mgr.service.impl.odds.AutomaticOddsCalcService;
import com.panda.sport.rcs.mgr.service.impl.odds.ManualOddsCalcService;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.utils.SpringContextUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.ObjectUtils;

public interface OddsCalcApi {
	
	public static List<Integer> SINGLE_WIN_PLAYS = Arrays.asList(37,41,43,48,54,60,66,142);

	// 篮球主要欧赔玩法
	public static List<Integer> BASKETBALL_MAIN_EU_PLAYS = Arrays.asList(5,37,41,43,48,54,60,66,142);
	// 足球球主要欧赔玩法
	public static List<Integer> FOOTBALL_MAIN_EU_PLAYS = Arrays.asList(1,17,111,119,126,129);

	public Boolean maginCalc(RcsMatchMarketConfig config,ThreewayOverLoadTriggerItem overLoadTriggerItem);
	
	public Boolean waterCalc(RcsMatchMarketConfig config,ThreewayOverLoadTriggerItem overLoadTriggerItem);

	public static String ODD_TYPE_FIRSTHALF = "FirstHalf";
	public static String ODD_TYPE_SECONDHALF = "SecondHalf";
	public static String ODD_TYPE_EQUALS = "Equals";
	public static String ODD_TYPE_NONE = "None";
	public static String ODD_TYPE_X2 = "X2";
	public static String ODD_TYPE_1X = "1X";
	public static String ODD_TYPE_12 = "12";
	
	
	public static OddsCalcApi getInstall(Integer dataSource) {
		if(dataSource == DataSourceTypeEnum.AUTOMATIC.getValue().intValue()) {//自动
			AutomaticOddsCalcService automaticOddsCalcService = SpringContextUtils.getBean("automaticOddsCalcService");
			return automaticOddsCalcService;
		} else {
			ManualOddsCalcService manualOddsCalcService = SpringContextUtils.getBean("manualOddsCalcService");
			return manualOddsCalcService;
		}
	}

}
