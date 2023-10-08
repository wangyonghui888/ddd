package com.panda.sport.rcs.utils;

public class DataFieldConvert {
	
	private static String CONVERT_DATA_SOURCE = "0:1,1:0";
	
	/**
	 * 转换自动和手动  与融合对应
	 * db 是否使用数据源0：手动；1：使用数据源
	 * 融合  0:自动操盘 1:手动操盘 null 表示不修改当前操盘类型
	 * @return
	 */
	public static Integer convertDataSource(Integer val) {
		return getKetMapValue(CONVERT_DATA_SOURCE,val);
	}
	
	private static Integer getKetMapValue(String keys ,Integer val) {
		if(val == null) return null; 
		
		for(String key : keys.split(",")) {
			String[] vals = key.split(":");
			if(vals[0].equals(String.valueOf(val))) {
				return Integer.parseInt(vals[1]);
			}
		}
		
		return val;
	}
	
	
	private static String CONVERT_MARKET_STATUS = "0:0,1:2,2:11,3:1";
	/**
	 * 转换盘口状态  与融合对应
	 * db 状态：0-开盘、1-关盘、2-锁盘、3-封盘
	 * 融合 0:active 开, 1:suspended 封, 2:deactivated 关, 11:锁 null 表示不修改当前盘口状态
	 * @return
	 */
	public static Integer convertMarketStatus(Integer val) {
		return getKetMapValue(CONVERT_MARKET_STATUS,val);
	}

}
