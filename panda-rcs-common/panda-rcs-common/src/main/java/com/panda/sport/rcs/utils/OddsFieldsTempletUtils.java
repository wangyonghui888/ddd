package com.panda.sport.rcs.utils;

import java.util.HashMap;
import java.util.Map;

import com.panda.sport.rcs.exeception.RcsServiceException;

public class OddsFieldsTempletUtils {
	
	private static Map<String, Map<String, Long>> oddsData = new HashMap<String, Map<String,Long>>(){
		{
			put("1", new HashMap<String,Long>(){
				{
					put("1", 47l);
					put("X", 48l);
					put("2", 49l);
				}
			});
			put("17", new HashMap<String,Long>(){
				{
					put("1", 101l);
					put("X", 102l);
					put("2", 103l);
				}
			});
			
			
			put("2", new HashMap<String,Long>(){
				{
					put("Under", 1l);
					put("Over", 2l);
				}
			});
			put("18", new HashMap<String,Long>(){
				{
					put("Under", 95l);
					put("Over", 96l);
				}
			});
			
			
			put("4", new HashMap<String,Long>(){
				{
					put("1", 3l);
					put("2", 4l);
				}
			});
			put("19", new HashMap<String,Long>(){
				{
					put("1", 146l);
					put("2", 147l);
				}
			});
			
//			[1,4,2,17,19,18]
			
			
			
			
			put("111", new HashMap<String,Long>(){
				{
					put("1", 388l);
					put("X", 389l);
					put("2", 390l);
				}
			});
			put("119", new HashMap<String,Long>(){
				{
					put("1", 404l);
					put("X", 405l);
					put("2", 406l);
				}
			});
			
			
			put("114", new HashMap<String,Long>(){
				{
					put("Under", 396l);
					put("Over", 397l);
				}
			});
			put("122", new HashMap<String,Long>(){
				{
					put("Under", 412l);
					put("Over", 413l);
				}
			});
			
			
			put("113", new HashMap<String,Long>(){
				{
					put("1", 394l);
					put("2", 395l);
				}
			});
			put("121", new HashMap<String,Long>(){
				{
					put("1", 410l);
					put("2", 411l);
				}
			});
			

//			[111,113,114,119,121,122]
		}
	};
	
	public static Long getTempletId(String playId , String oddType) {
		if(!oddsData.containsKey(playId)) {
			throw new RcsServiceException("不支持的玩法新增");
		}
		
		Map<String, Long> playMap = oddsData.get(playId);
		if(!playMap.containsKey(oddType)) {
			throw new RcsServiceException("不支持的投注项新增");
		}
		
		return playMap.get(oddType);
	}
	
	public static void main(String[] args) {
		System.out.println(getTempletId("121", "2"));
	}

}
