package com.panda.rcs.logService.utils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panda.rcs.logService.dto.CategorySetDTO;
import com.panda.rcs.logService.dto.SportDTO;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.sport.rcs.enums.FootBallCategorySetEnum;
import com.panda.sport.rcs.enums.SportTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.*;
@Slf4j
@Component
public class BaseUtils {

    //球種 玩法集 集合
    public static Map<Integer, SportDTO> sportPlayMap = new HashMap<>();

    //早盤對應頁面代碼
    public static List<Integer> earlyMarket = Arrays.asList(13, 14, 15, 100, 101, 110);

    //滾球對應頁面代碼
    public static List<Integer> runningMarket = Arrays.asList(16, 17, 18, 102, 103, 111);

    public static final String RCS_BUSINESS_LOG_SAVE = "rcs_business_log_save";
    /**
     * 导出设置
     * */
    public  static  Integer getPageCount(int total,int pageSize){
        if(total <= 0){
            total = 1;
        }
        if (total > 30000){
           total = 300000;
        }
        int pageCount = total / pageSize + (total % pageSize > 0 ? 1 : 0);
        return pageCount == 0 ? 1 : pageCount;
    }
  public static Boolean isTrue(Map<String, Object> map){
        if(Objects.isNull(map)){
            log.warn("::传入参数格式不正确,不生成日志");
            return true;
        }
      return false;
    }

    public  static Map<String,String> jsonStringMap(String str){
        if(StringUtils.isEmpty(str)){
            return null;
        }
        return JSONObject.parseObject(JSONObject.parseObject(str).toJSONString(), new TypeReference<HashMap<String, String>>() {
        });
    }

    public static LogAllBean mapObject(Map<String, Object> map, Class<LogAllBean> t){
        if(Objects.isNull(map)){
            return null;
        }
        return JSONObject.parseObject(JSONObject.toJSONString(map),t);
    }

    public static  LogAllBean setObject(Object obj){
        return  new ObjectMapper().convertValue(obj,LogAllBean.class);
    }

    public static  List<LogAllBean> mapList(Map<String, Object> map){
       return JSONObject.parseArray(JSONObject.toJSONString(map),LogAllBean.class);
    }

    public static Map<String, Object> getObjectToMap(Object obj) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<String, Object>();
        Class<?> cla = obj.getClass();
        Field[] fields = cla.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String keyName = field.getName();
            Object value = field.get(obj);
            if (value == null)
                value = "";
            map.put(keyName, value);
        }
        return map;
    }


    @PostConstruct
    public void prepareCategorySetMap() {
        for (SportTypeEnum sport : SportTypeEnum.values()) {
            SportDTO sportDTO = new SportDTO(sport);
            Map categorySetMap = new HashMap<>();

            switch (sport.getCode()) {
                case 1:
                    //組球
                    for (FootBallCategorySetEnum setEnum : FootBallCategorySetEnum.values()) {
                        categorySetMap.put(setEnum.getCategorySetId(), new CategorySetDTO(setEnum));
                    }
                    break;
            }

            sportDTO.setSportId(sport.getCode());
            sportDTO.setCategorySetMap(categorySetMap);
            sportPlayMap.put(sport.getCode(), sportDTO);
        }
    }
}
