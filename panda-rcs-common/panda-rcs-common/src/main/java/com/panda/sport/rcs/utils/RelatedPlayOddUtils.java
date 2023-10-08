package com.panda.sport.rcs.utils;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.constants.BaseConstants;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelatedPlayOddUtils {
	
	private static Integer MAX_SCORE = 16;
	
	private static Double MIN_DIFF = 0.000001d;
	
	private List<Map<String, Double>> PLAY_CHANCE_RESULT = null;

	//new String[]{"0.00","1.9325","2.3653"};  盘口，主队赔率，客队赔率
	private String[] hdcInfoArray = null;

	//new String[]{"0.00","1.9325","2.3653"};  盘口，主队赔率，客队赔率
	private String[] goalsInfoArray = null;
	
	public RelatedPlayOddUtils() {
		
	}
	
	public RelatedPlayOddUtils(String[] hdc , String[] goals) {
		if(hdc == null || hdc.length != 3) throw new RuntimeException("联动算法初始化数据异常:" + JSONObject.toJSONString(hdc));
		if(goals == null || goals.length != 3) throw new RuntimeException("联动算法初始化数据异常:" + JSONObject.toJSONString(goals));
		
		this.hdcInfoArray = hdc;
		this.goalsInfoArray = goals;
	}
	
	public static void main(String[] args) {
		String[] hdc = new String[]{"2","1.959990","2.041660"};
		String[] goals = new String[]{"3.75","1.950000","1.910000"};
		RelatedPlayOddUtils utils = new RelatedPlayOddUtils(hdc,goals);
		System.out.println(utils.getGolasOddsByHdc("3.75"));
	}
	
	private void validate() {
		if(hdcInfoArray == null ) {
			throw new RuntimeException("联动算法初始化数据异常，初始化数据不能为空:" + JSONObject.toJSONString(hdcInfoArray));
		}
		if(goalsInfoArray == null ) {
			throw new RuntimeException("联动算法初始化数据异常，初始化数据不能为空:" + JSONObject.toJSONString(goalsInfoArray));
		}
	}

	/**
	 * 获取让球指定盘口的赔率
	 *
	 * @param hdcVal
	 * @return
	 */
	public List<Double> getLetOddsByHdc(String hdcVal) {
		validate();
		if(PLAY_CHANCE_RESULT == null ) {
			calcMain(hdcInfoArray, goalsInfoArray);
		}
		return getOddsByHdc(PLAY_CHANCE_RESULT.get(0), hdcVal, true);
	}

	private List<Double> getOddsByHdc(Map<String, Double> playChance, String hdcVal, boolean isLet) {
		List<Double> letChanceList = getHomeAndAwayOdds(playChance, hdcVal,isLet);
		Double letChance = letChanceList.get(0);
		Double letChance2 = letChanceList.get(1);
		List<Double> oddsList = new ArrayList<Double>();
		Double homeOdds = getHomeOdds(letChance, letChance2);
		Double awayOdds = getAwayOdds(letChance, letChance2);
		oddsList.add(homeOdds);
		oddsList.add(awayOdds);
		return oddsList;
	}

	/**
	 * 获取大小球指定盘口的赔率
	 * @param hdcVal
	 * @return
	 */
	public List<Double> getGolasOddsByHdc(String hdcVal) {
		validate();
		if(PLAY_CHANCE_RESULT == null ) {
			calcMain(hdcInfoArray, goalsInfoArray);
		}
		return getOddsByHdc(PLAY_CHANCE_RESULT.get(1), hdcVal,false);
	}
	
	private List<Map<String, Double>> calcMain(String[] hdc,String[] goals) {
		if(PLAY_CHANCE_RESULT != null ) return PLAY_CHANCE_RESULT;
		
		List<String> list = null;
		String sup_hdc = hdc[0];
		String sup_golads = goals[0];
		do {
			PLAY_CHANCE_RESULT = new ArrayList<Map<String,Double>>();
			list = getChangeVal(sup_hdc, sup_golads, hdc, goals,PLAY_CHANCE_RESULT);
			sup_hdc = add(Double.parseDouble(sup_hdc), Double.parseDouble(list.get(1))).toString();
			sup_golads = add(Double.parseDouble(sup_golads), Double.parseDouble(list.get(3))).toString();
		}while(list.get(0).equals("1") || list.get(2).equals("1"));
		
		System.out.println(sup_hdc  + " , " + sup_golads);
		System.out.println(JSONObject.toJSONString(PLAY_CHANCE_RESULT));
		return PLAY_CHANCE_RESULT;
	}
	
	private List<Map<String, Double>> getPlayChance(Map<String, Double> allMap){
		Map<String, Double> goalsChance = new HashMap<String, Double>();
		Map<String, Double> letScoreChance = new HashMap<String, Double>();
		for(int i = 0 ; i <= MAX_SCORE ; i ++  ) {
			for(int j = 0 ; j <= MAX_SCORE ; j ++  ) {
				String key = String.valueOf(i + j) ;
				String tempKey = String.format("%03d", i) + String.format("%03d", j);
				if(goalsChance.containsKey(key)) {
					goalsChance.put(key , add(goalsChance.get(key), allMap.get(tempKey)));
				}else {
					goalsChance.put(key , allMap.get(tempKey));
				}
				
				String letKey = String.valueOf(i - j);
				if(letScoreChance.containsKey(letKey)) {
					letScoreChance.put(letKey , add(letScoreChance.get(letKey), allMap.get(tempKey)));
				}else {
					letScoreChance.put(letKey , allMap.get(tempKey));
				}
			}
		}
		List<Map<String, Double>> list = new ArrayList<Map<String,Double>>();
		list.add(letScoreChance);
		list.add(goalsChance);
		return list;
	}
	
	/**
	 * 获取需要变化的优越值  index 1   0：表示让球不需要变化，index  3   0：表示大小不需要变化
	 * @param sup  让球优越值
	 * @param ttg  大小球优越值
	 * @param hdc  让球盘数据  1：盘口 2：主队赔率，   3：客队赔率
	 * @param goals  大小球数据  1：盘口 2：主队赔率，   3：客队赔率
	 * @return
	 */
	private List<String> getChangeVal(String sup , String ttg,String[] hdc,String[] goals,List<Map<String, Double>> result){
		List<String> list = new ArrayList<String>();
		
		Double hdcVal = Double.parseDouble(sup);
		Double goalsVal = Double.parseDouble(ttg);
		
		Double homePro = divide(add(hdcVal, goalsVal), 2d);//1.25205
		Double awayPro = divide(subtract(goalsVal , hdcVal), 2d);//1.05245
		
		Map<String, Double> homeMap = getProList(MAX_SCORE, homePro);
		Map<String, Double> awayMap = getProList(MAX_SCORE, awayPro);
		Map<String, Double> allMap = getAllMap(homeMap,awayMap);
		
		result.addAll(getPlayChance(allMap));
		Map<String, Double> goalsChance = result.get(1);
		Map<String, Double> letScoreChance = result.get(0);


		List<Double> letChanceList = getHomeAndAwayOdds(letScoreChance, hdc[0],true);
		Double letChance = letChanceList.get(0);
		Double letChance2 = letChanceList.get(1);
		
		Double diffLetChance = subtract(divide(1d, divide(letChance, add(letChance , letChance2))), Double.parseDouble(hdc[1]));
		if(Math.abs(diffLetChance) > MIN_DIFF) {
			list.add("1");
			
			int beishu = divide(Math.abs(diffLetChance), multiply(MIN_DIFF, 5d)).intValue() + 1;
			if(diffLetChance > 0) {
				list.add(multiply(Double.parseDouble(String.valueOf(beishu)), MIN_DIFF).toString());
			}else {
				list.add(multiply(Double.parseDouble(String.valueOf(beishu)), -MIN_DIFF).toString());
			}
		}else {
			list.add("0");
			list.add("0");
		}

		List<Double> golasChanceList = getHomeAndAwayOdds(goalsChance, goals[0],false);
		Double goalsScoreChance = golasChanceList.get(0);
		Double goalsScoreChance2 = golasChanceList.get(1);
	 	
		diffLetChance = subtract(divide(1d, divide(goalsScoreChance, add(goalsScoreChance, goalsScoreChance2))), Double.parseDouble(goals[1]));
		if(Math.abs(diffLetChance) > MIN_DIFF) {
			list.add("1");
			
			int beishu = divide(Math.abs(diffLetChance), multiply(MIN_DIFF, 5d)).intValue() + 1;
			if(diffLetChance > 0) {
				list.add(multiply(Double.parseDouble(String.valueOf(beishu)), MIN_DIFF).toString());
			}else {
				list.add(multiply(Double.parseDouble(String.valueOf(beishu)), -MIN_DIFF).toString());
			}
		}else {
			list.add("0");
			list.add("0");
		}
		
		return list;
	}
	
	private Double getHomeOdds(Double home,Double away) {
		return divide(1d, divide(home, add(home , away)));
	}
	
	private Double getAwayOdds(Double home,Double away) {
		return divide(1d, divide(away, add(home , away)));
	}
	
	public String[] getHdcInfoArray() {
		return hdcInfoArray;
	}

	public void setHdcInfoArray(String[] hdcInfoArray) {
		this.hdcInfoArray = hdcInfoArray;
	}

	public String[] getGoalsInfoArray() {
		return goalsInfoArray;
	}

	public void setGoalsInfoArray(String[] goalsInfoArray) {
		this.goalsInfoArray = goalsInfoArray;
	}

	/**
	 * 获取主队机会值
	 * @param playChance
	 * @param hdcVal
	 * @param isLet
	 * @return
	 */
	private List<Double> getHomeAndAwayOdds(Map<String, Double> playChance, String hdcVal, boolean isLet  ){
		Double chance1 = 0d;
		Double chance2 = 0d;
		for(String key : playChance.keySet()) {
			if (isLet && Double.parseDouble(hdcVal) > 0 && "0".equals(key)) {
				chance1 = add(chance1, playChance.get(key));
				continue;
			}
			if (isLet && Double.parseDouble(hdcVal) < 0 && "0".equals(key)) {
				chance2 = add(chance2, playChance.get(key));
				continue;
			}

			if(Double.parseDouble(key) > Double.parseDouble(hdcVal) ) {
				chance1 = add(chance1 , playChance.get(key));
			}else if(Double.parseDouble(key) < Double.parseDouble(hdcVal) ) {
				chance2 = add(chance2 , playChance.get(key));
			}
		}
		
		int num = divide(Double.parseDouble(hdcVal), Double.parseDouble("0.25")).intValue() % 4;
		if (Math.abs(num) == 1 || Math.abs(num) == 3) {
			Double goalsHdcVal = 0d;
			if (num > 0) {
				goalsHdcVal = Math.abs(num) == 1 ? add(Double.parseDouble(hdcVal), Double.parseDouble("-0.25")) :
						add(Double.parseDouble(hdcVal), Double.parseDouble("0.25"));
			} else {
				goalsHdcVal = Math.abs(num) == 1 ? add(Double.parseDouble(hdcVal), Double.parseDouble("0.25")) :
						add(Double.parseDouble(hdcVal), Double.parseDouble("-0.25"));
			}

			List<Double> golasChanceListTemp = getHomeAndAwayOdds(playChance, String.valueOf(goalsHdcVal),isLet);
	 		Double chance3 = divide(golasChanceListTemp.get(0), add(golasChanceListTemp.get(0), golasChanceListTemp.get(1)));
	 		Double chance4 = subtract(1d, chance3);
	 		chance1 = divide(chance1, add(chance1, chance2));
	 		chance2 = subtract(1d, chance1);
	 		chance2 = chance2 * chance4 == 0 ? 0d : divide(1d, divide(add(divide(1d, chance2), divide(1d, chance4)), 2d));
	 		chance1 = subtract(1d, chance2);
		}
		
		List<Double> result = new ArrayList<Double>();
		result.add(chance1);
		result.add(chance2);
		return result;
	}

	
	/**
	 * 通过泊松分布计算出所有集合概率值
	 * @param home
	 * @param away
	 * @return
	 */
	private Map<String, Double> getAllMap(Map<String, Double> home,Map<String, Double> away){
		Map<String, Double> result = new HashMap<String, Double>();
		for(String homeScore : home.keySet()) {
			for(String awayScore : away.keySet()) {
				String key = String.format("%03d", Integer.parseInt(homeScore)) + String.format("%03d", Integer.parseInt(awayScore));
				result.put(key, multiply(home.get(homeScore),away.get(awayScore)));
			}
		}
		
		return result;
	}
	
	/**
	 * 获取泊松分布集合值
	 * @param num
	 * @param pro
	 * @return
	 */
	private Map<String, Double> getProList(Integer num,Double pro){
		Map<String, Double> result = new HashMap<String, Double>();
		for(int i = 0 ; i <= num ; i++) {
			result.put(String.valueOf(i), getPossion(i, pro));
		}
		
		return result;
	}

	private double getPossion(int k,double y) {

        double result = 0;
        if(k ==0){
            result = Math.exp(-y);
        }else{
            result = Math.pow(y, k)/getFactorial(k)*Math.exp(-y);
        }
        DecimalFormat df = new DecimalFormat("#.00000000000000");
        result = Double.parseDouble(df.format(result));
        return result;
    }

    private int getFactorial(int k) {
        int  result = 1;
        for(int i=1;i<=k;i++){
            result = result*i;
        }
        return result;
    }
    
	private Double add(Double a ,Double b) {
		return new BigDecimal(String.valueOf(a)).add(new BigDecimal(String.valueOf(b))).doubleValue();
	}
	
	private Double divide(Double a ,Double b) {
		return new BigDecimal(String.valueOf(a)).divide(new BigDecimal(String.valueOf(b)),8,BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	private Double subtract(Double a ,Double b) {
		return new BigDecimal(String.valueOf(a)).subtract(new BigDecimal(String.valueOf(b))).doubleValue();
	}
	
	private Double multiply(Double a ,Double b) {
		return new BigDecimal(String.valueOf(a)).multiply(new BigDecimal(String.valueOf(b))).doubleValue();
	}
}
