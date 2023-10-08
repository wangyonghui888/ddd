package com.panda.sport.rcs.credit.matrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  矩阵常量
 * @Date: 2019-10-04 13:18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class MatrixConstant {

    /**
     * 行矩阵数组最大值
     **/
    public static final Integer MATRIX_LINE_LENGTH = 13;
    /**
     * 列矩阵数组最大值
     **/
    public static final Integer MATRIX_COLUMN_LENGTH =13;

    /**
     * @Description  盘口值分隔符
     **/
    public static final String MARKET_ODDS_VALUE_SPLIT = "/";

    /**
     * @Description   总进球数分隔符
     * @Param
     * @Author  max
     * @Date  10:04 2019/10/11
     * @return
     **/
    public static final String  TOTAL_GOALS_MORE_SPLIT = "+";

    /**
     * @Description  上半场准确比分计算,其它比分标识
     **/
    public static final String HALFTIME_SCORE_OTHER = "other";

    public enum MatrixCategoryType {
        /**
         * 矩阵
         **/
        MATRIX,
        /**
         * 穷举
         **/
        EXHAUSTIVE,
        /**
         * 未知
         **/
        UNKNOWN
    }

    public enum MarketTypeOptions{
        /**
         * 欧洲盘
         **/
        MARKET_TYPE_EUROPE("EU"),

        /**
         * 马来盘
         **/
        MARKET_TYPE_MALAY("MY");


        private String  marketType;
        private MarketTypeOptions(String value)
        {
            marketType=value;
        }

        /**
         * @return 枚举变量实际返回值
         */
        public String getMarketType()
        {
            return marketType;
        }

    }

    public static List<Map<String,Integer>>  categoryList = new ArrayList<Map<String,Integer>>();

    /**
     * @Description   //玩法矩阵类型初始化
     * @Param []
     * @Author  max
     * @Date  14:34 2019/10/4
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Integer>>
     **/

    public static List<Map<String,Integer>> queryCategoryList(){
        Map<String,Integer> categoryMap = new HashMap<String,Integer>(40);
        categoryMap.put("全场独赢",0);
        categoryMap.put("全场进球大小",0);
        categoryMap.put("让球胜平负",0);
        categoryMap.put("亚盘让球",0);
        categoryMap.put("平局返还",0);
        categoryMap.put("剩余时间胜平负",0);
        categoryMap.put("双重机会",0);
        categoryMap.put("全场比分/波胆",0);
        categoryMap.put("主队精确进球数",0);
        categoryMap.put("客队精确进球数",0);
        categoryMap.put("主队进球数大小",0);
        categoryMap.put("客队进球数大小",0);
        categoryMap.put("双方是否都进球",0);
        categoryMap.put("独赢&进球数大小",0);
        categoryMap.put("总进球数",0);
        categoryMap.put("进球数单双",0);

        categoryList.add(categoryMap);
        return categoryList;
    }
}
