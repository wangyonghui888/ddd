package com.panda.sport.sdk.util;

import com.google.common.collect.Maps;
import com.panda.sport.rcs.constants.SeriesEnum;
import com.panda.sport.sdk.exception.RcsServiceException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeriesTypeUtils {

    public static Integer getSeriesType(Integer seriesType) {
        if (seriesType == null || seriesType < 1000) throw new RcsServiceException("seriesType参数错误");

        int type = 0;
        Integer prifix = Integer.parseInt(String.valueOf(seriesType).substring(0, 4));
        if (prifix % 10 == 0) {
            type = prifix / 100;
        } else {
            type = prifix / 1000;
        }
        return type;
    }

    public static Integer getCount(Integer seriesType, Integer type) {
        Integer temp = type * 100;

        return Integer.parseInt(String.valueOf(seriesType).substring(String.valueOf(temp).length()));
    }

    /**
     * 获取M串N中的N
     *
     * @param seriesType
     * @return
     */
    public static int getCount(Integer seriesType) {
        int temp = getSeriesType(seriesType) * 100;
        return Integer.parseInt(String.valueOf(seriesType).substring(String.valueOf(temp).length()));
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
    public static void main(String[] args) {
        System.out.println(getSeriesType(7001));
    }

}
