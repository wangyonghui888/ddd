package com.panda.sport.rcs.utils;

import com.panda.sport.rcs.enums.MarketMillionEnum;

public class OddsDiffOddsUtils {
	
	private static Integer ADD_DIFF_VAL  = 100000000;
	
	/**
	 * 先按照状态排序  ， 关盘排最后
	 * 盘口状态0-5. 0:active, 1:suspended, 2:deactivated, 3:settled, 4:cancelled, 5:handedOver  11 :锁
	 * @param
	 * @param diff
	 * @return
	 */
	public static Integer getChangeDiff(Integer newStatus ,Integer oldStatus , Integer diff ) {
		if(diff < MarketMillionEnum.getEnum(oldStatus).getValue()) {//兼容旧数据
			return diff + MarketMillionEnum.getEnum(newStatus).getValue();
		}
		return	diff - MarketMillionEnum.getEnum(oldStatus).getValue() + MarketMillionEnum.getEnum(newStatus).getValue();
	}
	
	public static Integer getBeforeChangeDiff(Integer status , Integer diff ) {
		return	diff - MarketMillionEnum.getEnum(status).getValue();
	}
	
	public static void main(String[] args) {
		System.out.println(getChangeDiff(2, 1, 100));
	}

}
