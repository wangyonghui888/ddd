package com.panda.sport.rcs.common;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Comparator，按map的value排序，value需要继承Comparable接口
 * @param <K>
 * @param <V>
 */
public class MapComparatorByValue<K, V extends Comparable> implements Comparator<K> {

    private Map<K, V> map;

    public MapComparatorByValue(Map<K, V> map) {
        this.map = new HashMap<>(map);
    }

    @Override
    public int compare(K s1, K s2) {
        return map.get(s1).compareTo(map.get(s2));
    }
}
