package com.panda.sport.rcs.core.utils;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JsonFormatUtils {

    public final static Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

    /**
     * Object转成JSON数据
     */
    public static String toJson(Object object) {
        if (object instanceof Integer || object instanceof Long || object instanceof Float ||
                object instanceof Double || object instanceof Boolean || object instanceof String) {
            return String.valueOf(object);
        }
        return gson.toJson(object);
    }

    /**
     * JSON数据，转成Object
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    /**
     * JSON数据转Object
     * @param json
     * @param type
     * @return
     */
    public static <T> T fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }


    /**
     * 转换泛型的集合
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> fromJsonArray(String json, Class<T> clazz) {
        // 生成List<T> 中的 List<T>
        Type listType = new ParameterizedTypeImpl(List.class, new Class[]{clazz});
        return gson.fromJson(json, listType);
    }

    /**
     * 转换泛型的MAP
     *
     * @param json
     * @param keyClazz
     * @param valueClazz
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, V> fromJsonMap(String json, Class<K> keyClazz, Class<V> valueClazz) {
        if (Strings.isNullOrEmpty(json)) {
            return Collections.emptyMap();
        }
        // 生成Map<K,V>
        Type mapType = new ParameterizedTypeImpl(Map.class, new Class[]{keyClazz, valueClazz});
        return gson.fromJson(json, mapType);
    }

    public static <K, V> Map<K, List<V>> fromJsonListMap(String json, Class<K> keyClazz, Class<V> valueClazz) {
        // 生成List
        Type listType = new ParameterizedTypeImpl(List.class, new Class[]{valueClazz});
        // 生成Map<K,V>
        Type mapType = new ParameterizedTypeImpl(Map.class, new Type[]{keyClazz, listType});
        return gson.fromJson(json, mapType);
    }

    public static void main(String[] args) {
        String s = "{\"1\":\"abc\",\"2\":\"def\",\"3\":\"fdf\"}\n";
        //Map<Integer, String> integerStringMap = fromJsonMap(s, Integer.class, String.class);
        String ss = "{\"1\":[{\"10\":\"abc\"},{\"11\":\"bc\"}],\"2\":[{\"20\":\"a2bc\"},{\"21\":\"dbc\"}],\"3\":[{\"30\":\"a2bc\"},{\"31\":\"ffc\"}]}";
        //System.out.println(integerStringMap);
        //Map<Integer, String> integerStringMap = fromJsonListMap(ss, Integer.class, String.class);
        //System.out.println(integerStringMap);
    }


}
