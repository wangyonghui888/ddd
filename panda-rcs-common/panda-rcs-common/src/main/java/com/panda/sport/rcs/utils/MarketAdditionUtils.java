package com.panda.sport.rcs.utils;

import java.math.BigDecimal;

public class MarketAdditionUtils {
	
	/**
	 * 格式化数据
	 * @param val
	 * @return
	 */
	public static String formatAddition(Double val) {
		if(val == val.intValue()) {
			return String.valueOf(val.intValue());
		}
		
		return val.toString();
	}
	
	/**
	 * add2 - add1 = 比分差 （客 减 主）
	 * add2 = add1 + 比分差 （客 减 主）
	 * add2 = add1 - （主 减 客）
	 * 通过add1 推算add2 
	 * add  add1值
	 * diff 比分差值   主队 - 客队
	 * @return
	 */
	public static String add1ToAdd2(Double add , Integer diff) {
		if(diff == null ) diff = 0 ;
		Double val = new BigDecimal(add.toString()).subtract(new BigDecimal(String.valueOf(diff))).doubleValue();
		
		return formatAddition(val);
	}
	
	/**
	 * add2 - add1 = 比分差 （客 减 主）
	 * add1 = add2 - 比分差 （客 减 主）
	 * 通过add2 推算add1 
	 * add  add2值
	 * diff 比分差值   主队 - 客队
	 * @return
	 */
	public static String add2ToAdd1(Double add , Integer diff) {
		if(diff == null ) diff = 0 ;
		Double val = new BigDecimal(add.toString()).subtract(new BigDecimal(String.valueOf(diff)).multiply(new BigDecimal("-1"))).doubleValue();
		
		return formatAddition(val);
	}
	
	public static String add1ToAdd2(Double add , Double diff) {
		return add1ToAdd2(add, diff.intValue());
	}
	
	public static void main(String[] args) {
		System.out.println(add2ToAdd1(-1.75d,0));
		System.out.println(add2ToAdd1(-1.75d,1));
	}

}
