package com.panda.sport.rcs.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * @author :  toney
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.mgr.operation
 * @Description :  工具类
 * @Date: 2019-12-12 10:46
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class MarketValueUtils {
    /**
     * 判断字符串是不是double型
     */
    private static Pattern isNumericPattern = Pattern.compile("[0-9]+[.]{0,1}[0-9]*[dD]{0,1}");

    /**
     * 分开盘口
     * @param strMarketValue
     * @return
     */
    public static List<Double> splitMarketValue(String strMarketValue){
        List<Double> marketValueList = new ArrayList<>();
        if(strMarketValue.indexOf("/")>-1){
            String[] marketValues =strMarketValue.split("/");
            for(String str:marketValues){
                Double marketValue = Math.abs(Double.parseDouble(str));
                if(strMarketValue.contains("-")){
                    marketValue = marketValue*-1;
                }
                marketValueList.add(marketValue);
            }

        }else{
            marketValueList.add(Double.parseDouble(strMarketValue));
        }

        return marketValueList;
    }


    public static void main(String[] args){
      System.out.println(splitMarketValue("-0/0.5"));
        System.out.println(splitMarketValue("0/0.5"));

        System.out.println(splitMarketValue("-1/1.5"));
        System.out.println(splitMarketValue("1/1.5"));
    }
    /**
     * 拆分盘口值列表
     * @param strMarketValue
     * @return
     */
    public static List<Double> splitMarketList(String strMarketValue){
        if(StringUtils.isEmpty(strMarketValue)){
            return null;
        }

        List<Double> marketValueList = new ArrayList<>();
        Double marketValue = Double.parseDouble(strMarketValue);

        if (strMarketValue.contains(".25")) {
            if (marketValue > 0) {
                marketValueList.add(Math.floor(marketValue));
                marketValueList.add(Math.floor(marketValue) + 0.5);
            } else if (marketValue < 0) {
                marketValueList.add(Math.ceil(marketValue));
                marketValueList.add(Math.ceil(marketValue) - 0.5);
            }
        } else if (strMarketValue.contains(".75")) {
            if (marketValue > 0) {
                marketValueList.add(Math.floor(marketValue) + 0.5);
                marketValueList.add(Math.floor(marketValue) + 1);
            } else if (marketValue < 0) {
                marketValueList.add(Math.ceil(marketValue) - 0.5);
                marketValueList.add(Math.ceil(marketValue) - 1);
            }
        } else {
            marketValueList.add(marketValue);
        }
        return marketValueList;
    }
    /**
     * @Description   转化0.25到0/0.5
     * @Param [strMarketValue]
     * @Author  toney
     * @Date  21:50 2020/2/28
     * @return java.lang.String
     **/
    public static String mergeMarketString(String strMarketValue){
        if(StringUtils.isEmpty(strMarketValue)){
            return "";
        }
        if(strMarketValue.contains("/")){
            return strMarketValue;
        }
        if(strMarketValue.contains("+")){
            return strMarketValue;
        }
        if(!isNumeric(strMarketValue)){
            return strMarketValue;
        }
        Double marketValue = Double.valueOf(strMarketValue);
        String temp ="";
        if (strMarketValue.contains(".25")) {
            if (marketValue > 0) {
                temp +=Math.floor(marketValue);
                temp +="/"+(Math.floor(marketValue) + 0.5);
            } else if (marketValue < 0) {
                temp +=Math.ceil(marketValue);
                temp +="/"+ Math.abs(Math.ceil(marketValue) - 0.5);
            }
        } else if (strMarketValue.contains(".75")) {
            if (marketValue > 0) {
                temp += (Math.floor(marketValue) + 0.5);
                temp +="/" +  (Math.floor(marketValue) + 1);
            } else if (marketValue < 0) {
                temp +=(Math.ceil(marketValue) - 0.5);
                temp +="/"+ Math.abs(Math.ceil(marketValue) - 1);
            }
        } else {
            temp = strMarketValue;
        }
        return temp;
    }



    /**
     * @Description   让球对比 主队
     * @Param [marketValue：盘口值, rectangle：当前矩阵数]
     * @Author  toney
     * @Date  11:52 2019/12/20
     * @return java.lang.Integer 1:赢 2:输 31:赢 2:输 :输一半 4:赢一半 5:和
     **/
    public static Integer compareHomeMarketValue(Double marketValue,Double rectangle){
        if (rectangle + marketValue  > 0) {
            return 1;
        }else if(marketValue.compareTo(rectangle)==0)
        {
            return 5;
        }
        else{
            return 2;
        }
    }
    /**
     * @Description   让球对比 客队
     * @Param [marketValue, rectangle, x, y]
     * @Author  toney
     * @Date  16:47 2020/3/3
     * @return java.lang.Integer
     **/
    public static Integer compareAwayMarketValue(Double marketValue,Double rectangle){
        if (rectangle + marketValue  < 0) {
            return 1;
        }
        else if(marketValue.compareTo(rectangle)==0)
        {
            return 5;
        }
        else{
            return 2;
        }
    }

    /**
     * @Description   大小球盘口值
     * @Param [marketValue, rectangle]
     * @Author  toney
     * @Date  16:06 2019/12/14
     * @return java.lang.Integer 1为大 2为小
     **/
    public static Integer compareGoalLineMarketValue(Double marketValue,Double rectangle) {
        if (DoubleUtil.considerEqual(marketValue, rectangle)) {
            return 5;
        }

        if (rectangle > marketValue ) {
            return 1;
        }

        return 2;
    }

    /**
     * @Description   合并盘口值
     * @Param [orderItem]
     * @Author  toney
     * @Date  15:28 2019/12/13
     * @return java.lang.Double
     **/
    public static Double mergeMarket(String marketValueText){
        if(StringUtils.isEmpty(marketValueText)){
            return 0.0;
        }
        Integer flag = 1;
        if("-".equals(marketValueText.substring(0,1))){
            flag = -1;
        }
        marketValueText= marketValueText.replace("-","").replace("+","");
        String[] marketList  = marketValueText.split("/");

        Double marketValue = 0.0;
        if(marketList.length == 2){
            marketValue = (Double.valueOf(marketList[0]) + Double.valueOf(marketList[1]))/2;
        }else{
            marketValue = Double.valueOf(marketList[0]);
        }
        return marketValue * flag;
    }


    /**
     * 判断字符串是不是double型
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){
        Matcher isNum = isNumericPattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
}
