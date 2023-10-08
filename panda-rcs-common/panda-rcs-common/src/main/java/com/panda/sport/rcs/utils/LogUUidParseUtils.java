package com.panda.sport.rcs.utils;

import java.util.UUID;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogUUidParseUtils {

    public static String getUuid(Object obj) {
        return getUuid(obj, UUID.randomUUID().toString().replace("-", ""));
    }

    public static String getUuid(Object obj, String defaultStr) {
        try {
            JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(obj));
            if (json.containsKey("globalId")) {
                return json.getString("globalId");
            }
            if (json.containsKey("linkId")) {
                return json.getString("linkId");
            }
            if (json.containsKey("requestId")) {
                return json.getString("requestId");
            }
        } catch (Exception e) {
            return defaultStr;
        }
        return defaultStr;
    }
    public static String getUuids(Object[] objs) {
        return getUuids(objs, UUID.randomUUID().toString().replace("-", ""));
    }

    public static String getUuids(Object[] objs, String defaultStr) {
        if (objs == null || objs.length <= 0) {
            return defaultStr;
        }
        return getUuid(objs[0], defaultStr);
    }

}
