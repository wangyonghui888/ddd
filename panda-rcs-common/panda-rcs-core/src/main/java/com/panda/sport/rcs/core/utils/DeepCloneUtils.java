package com.panda.sport.rcs.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

/**
 * @author :  holly
 * @Project Name :rcs-parent
 * @Package Name :com.panda.sport.rcs.core.utils
 * @Description :
 * @Date: 2019-10-12 17:21
 */
public class DeepCloneUtils {
    private  static Gson gson = new Gson();
    public static <T> T copyProperties(Object src,Class<T> clazz){
        String srcString = gson.toJson(src);
        T dst = (T)gson.fromJson(srcString, clazz);
        return dst;
    }
}
