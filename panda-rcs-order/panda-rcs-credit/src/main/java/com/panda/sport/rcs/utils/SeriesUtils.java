package com.panda.sport.rcs.utils;

import com.google.common.collect.Maps;
import com.panda.sport.rcs.credit.constants.ErrorCode;
import com.panda.sport.rcs.exeception.LogicException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 串关工具类
 * @Author : Paca
 * @Date : 2021-05-06 0:07
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class SeriesUtils {

    /**
     * 获取M串N中的M
     *
     * @param seriesType
     * @return
     */
    public static int getSeriesNum(Integer seriesType) {
        if (seriesType == null || seriesType < 2000) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "seriesType参数错误！");
        }
        int prefix = Integer.parseInt(String.valueOf(seriesType).substring(0, 4));
        return (prefix % 10 == 0) ? (prefix / 100) : (prefix / 1000);
    }

    /**
     * 获取M串N中的N
     *
     * @param seriesType
     * @param seriesNum
     * @return
     */
    public static int getCount(Integer seriesType, int seriesNum) {
        int temp = seriesNum * 100;
        return Integer.parseInt(String.valueOf(seriesType).substring(String.valueOf(temp).length()));
    }

    /**
     * 获取M串N中的N
     *
     * @param seriesType
     * @return
     */
    public static int getCount(Integer seriesType) {
        int temp = getSeriesNum(seriesType) * 100;
        return Integer.parseInt(String.valueOf(seriesType).substring(String.valueOf(temp).length()));
    }

    /**
     * 多场赛事串关组合
     *
     * @param matchCount 赛事数量
     * @return 返回赛事下标组合，并根据场次分组
     */
    public static Map<Integer, List<List<Integer>>> combination(int matchCount) {
        Map<Integer, List<List<Integer>>> map = Maps.newHashMap();
//        int[] indexArray = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
        int limit = 1 << matchCount;
        // list长度，避免扩容
        int listSize = 1 << (matchCount - 2);
        for (int mask = 1; mask < limit; mask++) {
            List<Integer> subList = new ArrayList<>(matchCount);
            for (int subMask = 1, i = 0; i < matchCount; subMask <<= 1, i++) {
                if ((mask & subMask) != 0) {
//                    subList.add(indexArray[i]);
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
        return map;
    }

    public static void checknk(int n, int k){
        if(k < 0 || k > n){ // N must be a positive integer.
            throw new IllegalArgumentException("K must be an integer between 0 and N.");
        }
    }
    public static int nchoosek(int n, int k){
        if(n > 31){
            throw new IllegalArgumentException("N must be less than or equal to 31");
        }
        checknk(n, k);
        k = (k > (n - k)) ? n - k : k;
        if(k <= 1){
            // C(n, 0) = 1, C(n, 1) = n
            return k == 0 ? 1 : n;
        }
        int limit = Integer.MAX_VALUE >> (31 - n);
        int cnk = 0;
        for(int i = 3; i < limit; i++){
            if(Integer.bitCount(i) == k){
                cnk++;
            }
        }
        return cnk;
    }

    public static int count(int n) {
        int sum = 0;
        for (int i=0;i<=n;i++) {
            if (i >=2) {
                sum+=nchoosek(n, i);
            }
        }
        return sum;
    }

    public static void main(String[] args) {
//        System.out.println(nchoosek(10,0));
//        System.out.println(nchoosek(10,1));
//        System.out.println(nchoosek(10,2));
//        System.out.println(nchoosek(10,3));
//        System.out.println(nchoosek(10,4));
//        System.out.println(nchoosek(10,5));
//        System.out.println(nchoosek(10,6));
//        System.out.println(nchoosek(10,7));
//        System.out.println(nchoosek(10,8));
//        System.out.println(nchoosek(10,9));
//        System.out.println(nchoosek(10,10));
        for (int i=2;i<=10;i++) {
            int count = count(i);
            System.out.println(count+"\t"+((1 << i)-1-i)+"\t"+(1 << (i - 2)));
        }

//        int n = 10;
//        int sum = 0;
//        for (int i=0;i<=n;i++) {
//            int nchoosek = nchoosek(n, i);
//            System.out.println(nchoosek+"\t"+(1 << i));
//            sum+=nchoosek;
//        }
//        System.out.println(sum);
//        System.out.println(1 << 2);
//        System.out.println(1 << 3);
//        System.out.println(1 << 4);
//        System.out.println(1 << 5);
//        System.out.println(1 << 6);
//        System.out.println(1 << 7);
//        System.out.println(1 << 8);
//        System.out.println(1 << 9);
//        System.out.println(1 << 10);
//        System.out.println(combination(10));
    }
}
