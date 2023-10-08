package com.panda.sport.rcs.third.util.stray;

import com.google.common.collect.Maps;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.constants.SeriesEnum;
import com.panda.sport.rcs.third.common.NumberConstant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础方法封装
 */
public class StrayUtils {

    /**
     * 多字符串参数拼接单字符串
     *
     * @param params
     * @return
     */
    public static String toStringForParams(String... params) {
        StringBuffer sb = new StringBuffer();
        if (params.length > NumberConstant.NUM_ZERO) {
            for (String param : params) {
                sb.append(param);
            }
        }
        return sb != null ? sb.toString() : null;
    }

    /**
     * 拼装数组为字符串
     *
     * @param playIds
     * @return
     */
    public static String toStringForParams(Integer[] playIds) {
        StringBuffer sb = new StringBuffer();
        if (playIds.length > NumberConstant.NUM_ZERO) {
            for (int i = NumberConstant.NUM_ZERO; i < playIds.length; i++) {
                sb.append(playIds[i]);
                if (i < playIds.length - NumberConstant.NUM_ONE) {
                    sb.append("_");
                }
            }
        }
        return sb != null ? sb.toString() : null;
    }

    /**
     * 排列组合计算，C(m,n)
     *
     * @param m 大的数
     * @param n 小的数
     * @return
     */
    public static int combination(int m, int n) {
        if (n > m / 2) {
            n = m - n;
        }
        int c = 1;
        for (int i = 0; i < n; i++) {
            c = c * (m - i) / (i + 1);
        }
        return c;
    }

    /**
     * 组合求和，C(m,0) + C(m,1) + C(m,2) + ... + C(m,m) = 2^m
     *
     * @param m
     * @return
     */
    public static int combinationSum(int m) {
        return Double.valueOf(Math.pow(2, m)).intValue();
    }

    /**
     * 组合求和，C(m,1) + C(m,2) + ... + C(m,m) = 2^m - 1
     *
     * @param m
     * @return
     */
    public static int combinationSum2(int m) {
        return combinationSum(m) - 1;
    }

    /**
     * 多场赛事串关组合
     *
     * @param matchCount 赛事数量
     * @return 返回赛事下标组合，并根据场次分组
     */
    public static Map<Integer, List<List<Integer>>> combination(int matchCount) {
        Map<Integer, List<List<Integer>>> map = Maps.newHashMap();
        int limit = NumberConstant.NUM_ONE << matchCount;
        // list长度，避免扩容
        int listSize = NumberConstant.NUM_ONE << (matchCount - NumberConstant.NUM_TWO);
        for (int mask = NumberConstant.NUM_ONE; mask < limit; mask++) {
            List<Integer> subList = new ArrayList<>(matchCount);
            for (int subMask = NumberConstant.NUM_ONE, i = NumberConstant.NUM_ZERO; i < matchCount; subMask <<= NumberConstant.NUM_ONE, i++) {
                if ((mask & subMask) != NumberConstant.NUM_ZERO) {
                    subList.add(i);
                }
            }
            int size = subList.size();
            // 单场比赛的组合不返回
            if (size <= NumberConstant.NUM_ONE) {
                continue;
            }
            if (map.containsKey(size)) {
                map.get(size).add(subList);
            } else {
                List<List<Integer>> value = new ArrayList<>(listSize);
                value.add(subList);
                map.put(size, value);
            }
        }
        return map;
    }

    /**
     * 计算串关总赔率 <br/>
     * 推导公式，例如3串4，欧赔分别为odds1、odds2、odds3，A为拆分后单个注单投注额，M为剩余赔付额度，已知M求A <br/>
     * 2串1：A*(odds1*odds2-1) + A*(odds1*odds3-1) + A*(odds2*odds3-1) = M <br/>
     * 3串1：A*(odds1*odds2*odds3-1) = M <br/>
     * 3串4：A*(odds1*odds2-1) + A*(odds1*odds3-1) + A*(odds2*odds3-1) + A*(odds1*odds2*odds3-1) = M <br/>
     * M = (totalEuOdds - 1) * (n * A)
     * (odds1*odds2-1) + (odds1*odds3-1) + (odds2*odds3-1) = (totalEuOdds - 1) * n
     *
     * @param orderItemList
     * @return
     */
    public static Map<Integer, BigDecimal> calTotalEuOdds(List<OrderItem> orderItemList) {
        Map<Integer, BigDecimal> resultMap = Maps.newHashMap();
        Map<Integer, List<List<Integer>>> combinationMap = combination(orderItemList.size());
        // 所有注单港赔之和，用来计算M串N
        BigDecimal allHkOddsSum = BigDecimal.ZERO;
        int sizeSum = NumberConstant.NUM_ZERO;
        for (Map.Entry<Integer, List<List<Integer>>> entry : combinationMap.entrySet()) {
            // key=M串1中的M，value=M串1组成的集合
            Integer key = entry.getKey();
            List<List<Integer>> value = entry.getValue();
            int size = value.size();
            sizeSum += size;
            // 每组注单港赔之和
            BigDecimal groupHkOddsSum = BigDecimal.ZERO;
            // 遍历M串1注单
            for (List<Integer> oddsIndexList : value) {
                // 遍历注单中所有投注项，投注项欧赔相乘
                BigDecimal betEuOdds = BigDecimal.ONE;
                for (Integer oddsIndex : oddsIndexList) {
                    betEuOdds = betEuOdds.multiply(getOdds(orderItemList.get(oddsIndex)));
                }
                // 单注注单港赔 = 投注项欧赔相乘 - 1
                BigDecimal betHkOdds = betEuOdds.subtract(BigDecimal.ONE);
                groupHkOddsSum = groupHkOddsSum.add(betHkOdds);
            }
            // 计算M串1总赔率，M = (totalEuOdds - 1) * (n * A)
            BigDecimal euOdds = groupHkOddsSum.divide(new BigDecimal(size), NumberConstant.NUM_TWO, RoundingMode.HALF_UP).add(BigDecimal.ONE);
            resultMap.put(key * NumberConstant.NUM_ONE_THOUSAND + NumberConstant.NUM_ONE, euOdds);
            allHkOddsSum = allHkOddsSum.add(groupHkOddsSum);
        }
        if (orderItemList.size() > NumberConstant.NUM_TWO) {
            // 计算M串N总赔率(N>1)
            BigDecimal euOdds = allHkOddsSum.divide(new BigDecimal(sizeSum), NumberConstant.NUM_TWO, RoundingMode.HALF_UP).add(BigDecimal.ONE);
            SeriesEnum seriesEnum = SeriesEnum.getSeriesEnumBySeriesNum(orderItemList.size());
            resultMap.put(seriesEnum.getSeriesJoin(), euOdds);
        }
        return resultMap;
    }

    private static BigDecimal getOdds(OrderItem orderItem) {
        Double oddsValue = orderItem.getOddsValue();
        if (oddsValue == null) {
            return BigDecimal.ONE;
        }
        return new BigDecimal(oddsValue + "").divide(new BigDecimal(NumberConstant.NUM_ONE_HUNDRED_THOUSAND), NumberConstant.NUM_TWO, BigDecimal.ROUND_DOWN);
    }

    /**
     * 用于返回空的数字
     *
     * @param count
     * @return
     */
    public static Map<Integer, Integer> getEmptyArray(int count) {
        Map<Integer, Integer> map = new HashMap<>();
        Map<Integer, List<List<Integer>>> combinationMap = combination(count);
        for (Map.Entry<Integer, List<List<Integer>>> entry : combinationMap.entrySet()) {
            map.put(entry.getKey() * NumberConstant.NUM_ONE_THOUSAND + NumberConstant.NUM_ONE, NumberConstant.NUM_ONE);
        }
        if (count > NumberConstant.NUM_TWO) {
            SeriesEnum seriesEnum = SeriesEnum.getSeriesEnumBySeriesNum(count);
            map.put(seriesEnum.getSeriesJoin(), NumberConstant.NUM_ONE);
        }
        return map;
    }

    public static int statisticsSeriesNum(int num) {
        return combinationSum2(num);
    }

    public static int num(int matchAmount, int m, int n) {
        if (n > 1) {
            return statisticsSeriesNum(matchAmount - 1);
        }
        if (matchAmount == m) {
            return 1;
        }
        return matchAmount - 1;
    }

    public static void main(String[] args) {
        /*for (int i = 1; i <= 9; i++) {
            System.out.println(statisticsSeriesNum(i) + "\t" + (combinationSum(i) - 1) + "\t" + combinationSum2(i));
        }*/

        System.out.println(combination(3));


        /*Map<Integer, List<List<Integer>>> map = Maps.newHashMap();
        int limit = 1 << 3;
        // list长度，避免扩容
        int listSize = 1 << (3 - 2);
        for (int mask = 1; mask < limit; mask++) {
            List<Integer> subList = new ArrayList<>(3);
            for (int subMask = 1, i = 0; i < 3; subMask <<= 1, i++) {
                if ((mask & subMask) != 0) {
                    subList.add(i);
                }
            }
            int size = subList.size();
            // 单场比赛的组合不返回
            if (size <= 1) {
                continue;
            }
            if (map.containsKey(size)) {
                map.get(size).add(subList);
            } else {
                List<List<Integer>> value = new ArrayList<>(listSize);
                value.add(subList);
                map.put(size, value);
            }
        }


        System.out.println(map);*/


//        System.out.println(statisticsSeriesNum(1));
//        System.out.println(statisticsSeriesNum(2));
//        System.out.println(statisticsSeriesNum(3));
//        System.out.println(statisticsSeriesNum(4));
//        System.out.println(getEmptyArray(5));
//        OrderItem orderItem1 = new OrderItem();
//        orderItem1.setOddsValue(2.49 * 100000);
//        OrderItem orderItem2 = new OrderItem();
//        orderItem2.setOddsValue(1.81 * 100000);
//        OrderItem orderItem3 = new OrderItem();
//        orderItem3.setOddsValue(2.08 * 100000);
//        OrderItem orderItem4 = new OrderItem();
//        orderItem4.setOddsValue(1.77 * 100000);
////        OrderItem orderItem5 = new OrderItem();
////        orderItem5.setOddsValue(1.87 * 100000);
//        List<OrderItem> orderItemList = Lists.newArrayList(orderItem1, orderItem2, orderItem3, orderItem4);
//        Map<Integer, Integer> countMap = Maps.newHashMap();
//        countMap.put(2001, 10);
//        countMap.put(3001, 10);
//        countMap.put(4001, 5);
//        countMap.put(5001, 1);
//        countMap.put(50026, 26);
//        Map<Integer, BigDecimal> map = calTotalEuOdds(orderItemList);
//        map.forEach((type, odds) -> {
//            BigDecimal pay = odds.subtract(BigDecimal.ONE).multiply(new BigDecimal(countMap.get(type))).setScale(2, RoundingMode.HALF_UP);
//            System.out.println(type + "\t" + odds.setScale(2, RoundingMode.HALF_UP).toPlainString() + "\t" + pay.toPlainString());
//        });

    }
}