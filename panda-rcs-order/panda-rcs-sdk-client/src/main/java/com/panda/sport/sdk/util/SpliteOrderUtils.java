package com.panda.sport.sdk.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpliteOrderUtils {

    public interface ApiCall<T> {

        public void execute(List<T> list);

    }

    public static <T> void spliteOrder(List<T> arr, int length, int start, int index,
                                       List<T> list, SpliteOrderUtils.ApiCall<T> apiCall) {
        for (; index < arr.size(); ) {
            List<T> tempList = new ArrayList<T>();
            tempList.addAll(list);
            tempList.add(arr.get(index));
            if (tempList.size() == length) {
                apiCall.execute(tempList);
                ++index;
                continue;
            }
            ++index;
            spliteOrder(arr, length, start, index, tempList, apiCall);
        }
    }

    /**
     * 求组合C(m,n)的值，C(4,3) = (4*3*2)/(1*2*3) = 4
     *
     * @param m
     * @param n
     * @return
     */
    public static int combination(int m, int n) {
        if (m < n || n <= 0 || m == n) {
            return 1;
        }
        // 分子
        int numerator = 1;
        // 分母
        int denominator = 1;
        if (n > m / 2) {
            n = m - n;
        }
        for (int i = 0; i < n; i++) {
            numerator = numerator * (m - i);
            denominator = denominator * (i + 1);
        }
        return numerator / denominator;
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

    private static void test(int count) {
        char[] chars = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',};
        if (count > chars.length || count < 2) {
            return;
        }
        int limit = 1 << count;
        Map<Integer, List<String>> map = Maps.newHashMap();
        for (int mask = 1; mask < limit; mask++) {
            StringBuilder sb = new StringBuilder();
            for (int subMask = 1, i = 0; i < count; subMask <<= 1, i++) {
                if ((mask & subMask) != 0) {
                    sb.append(chars[i]);
                }
            }
            int length = sb.length();
            if (length > 1) {
                if (map.containsKey(length)) {
                    map.get(length).add(sb.toString());
                } else {
                    List<String> list = Lists.newArrayList();
                    list.add(sb.toString());
                    map.put(length, list);
                }
            }
        }
        System.out.println(map);
//        map.forEach((k, v) -> System.out.println(k + "\t" + v.size()));
    }

    public static void main(String[] args) {
//        test(10);
        test(4);
        System.out.println(combination(4));
        test(3);
        System.out.println(combination(3));
        test(2);
        System.out.println(combination(2));
//        System.out.println(1 << 10 - 2);
//        System.out.println(1 << 2 - 2);
    }

}
