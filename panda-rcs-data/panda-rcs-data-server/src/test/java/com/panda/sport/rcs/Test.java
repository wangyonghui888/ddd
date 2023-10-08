package com.panda.sport.rcs;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;

public class Test {
    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("matchId","1");
        jsonObject.put("linkId","2");

        System.out.println(jsonObject.toString());
        System.out.println(JSONObject.toJSONString(jsonObject));



    }
}
