package com.panda.sport.rcs.utils;

import com.panda.sport.rcs.exeception.RcsServiceException;

public class SeriesTypeUtils {
	
	public static Integer getSeriesType(Integer seriesType) {
		if(seriesType == null || seriesType < 1000 ) throw new RcsServiceException("seriesType参数错误");
		
		int type = 0;
		Integer prifix = Integer.parseInt(String.valueOf(seriesType).substring(0, 4));
		if(prifix % 10 == 0) {
			type = prifix / 100;
		}else {
			type = prifix / 1000;
		}
		return type;
	}
	
	public static Integer getCount(Integer seriesType,Integer type) {
		Integer temp = type * 100;
		
		return Integer.parseInt(String.valueOf(seriesType).substring(String.valueOf(temp).length()));
	}
	
	public static void main(String[] args) {
		System.out.println(getSeriesType(7001));
	}

}
