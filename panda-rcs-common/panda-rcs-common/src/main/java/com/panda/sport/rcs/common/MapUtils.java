package com.panda.sport.rcs.common;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MapUtils {

    // 按MAP的值排序
    public static <K, V> Map<K, V> sortByValues(Map<K, V> sourceMap) {
        if(sourceMap!=null){
            Comparator<K> comparator = new MapComparatorByValue(sourceMap);

            Map<K, V> sortedMap = new TreeMap<>(comparator);
            sortedMap.putAll(sourceMap);
            return sortedMap;
        }
        return null;
    }

}
