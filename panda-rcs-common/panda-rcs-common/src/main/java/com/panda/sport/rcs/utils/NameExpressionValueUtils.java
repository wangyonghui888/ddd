package com.panda.sport.rcs.utils;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class NameExpressionValueUtils {
	
	public static String getNameExpressionValue(Integer playId,String oddsType,String addition1) {
		if(addition1 == null ) return null;
		String nameExpressionValue = addition1;
		
		if(!NumberUtils.isNumber(addition1)) return addition1;
		
		try {
			if("X".equalsIgnoreCase(oddsType) ) {
				nameExpressionValue = new BigDecimal(addition1).multiply(new BigDecimal("-1")).toPlainString();
			}
			
			if("Odd".equals(oddsType)) {//奇数
				nameExpressionValue = addition1;
			}else if("Even".equals(oddsType)) {//偶数
				nameExpressionValue = addition1;
			}else if("1".equals(oddsType)) {//让球
				nameExpressionValue = addition1;
			}else if("2".equals(oddsType)) {//让球
				nameExpressionValue = new BigDecimal(addition1).multiply(new BigDecimal("-1")).toPlainString();
			}else if("Over".equals(oddsType)) {//大小
				nameExpressionValue = addition1;
			}else if("Under".equals(oddsType)) {//大小
				nameExpressionValue = addition1;
			}
			
			return nameExpressionValue;
		}finally {
			if(StringUtils.isBlank(nameExpressionValue)) return nameExpressionValue;
			if(new Double(nameExpressionValue) % 1 / 1 == 0) nameExpressionValue = nameExpressionValue + ".0";
		}
	}
	
	public static void main(String[] args) {
		System.out.println(getNameExpressionValue(null, "X", "-3.25"));
	}

}
