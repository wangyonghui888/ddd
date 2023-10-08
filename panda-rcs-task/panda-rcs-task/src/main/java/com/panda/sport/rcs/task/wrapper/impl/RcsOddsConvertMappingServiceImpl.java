package com.panda.sport.rcs.task.wrapper.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.sport.rcs.enums.MarketKindEnum;
import com.panda.sport.rcs.mapper.RcsOddsConvertMappingMapper;
import com.panda.sport.rcs.pojo.RcsOddsConvertMapping;
import com.panda.sport.rcs.pojo.StandardSportMarketCategory;
import com.panda.sport.rcs.task.wrapper.RcsOddsConvertMappingService;
import com.panda.sport.rcs.task.wrapper.StandardSportMarketCategoryService;
import com.panda.sport.rcs.vo.SportMarketCategoryVo;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 赔率转换映射表 服务实现类
 * </p>
 *
 * @author admin
 * @since 2019-10-04
 */
@Service
@Slf4j
public class RcsOddsConvertMappingServiceImpl extends ServiceImpl<RcsOddsConvertMappingMapper, RcsOddsConvertMapping> implements RcsOddsConvertMappingService {

    //存放赔率枚举
    private static LinkedHashMap<String, String> map = new LinkedHashMap<>();
    private static String  MIN_ODDS_VALUE;
    private static String  MAX_ODDS_VALUE;

    @Autowired
    private RcsOddsConvertMappingMapper mapper;

    @Override
    public void init(){
        List<RcsOddsConvertMapping> listRcsOddsConvertMapping = mapper.queryOddsMappingList();
        if (!CollectionUtils.isEmpty(listRcsOddsConvertMapping)){
            MIN_ODDS_VALUE=new DecimalFormat("#.00").format(Double.parseDouble(listRcsOddsConvertMapping.get(0).getEurope()));
            MAX_ODDS_VALUE=new DecimalFormat("#.00").format(Double.parseDouble(listRcsOddsConvertMapping.get(listRcsOddsConvertMapping.size()-1).getEurope()));
            for (int x=0;x<listRcsOddsConvertMapping.size();x++) {
                String format = new DecimalFormat("#.00").format(Double.parseDouble(listRcsOddsConvertMapping.get(x).getEurope()));
                map.put(format,format );
            }
        }
    }

	@Override
	public String getNextLevelOdds(String displayOddsVal) {
        if(displayOddsVal == null) {
            return null;
        }
        double oddValue = Double.parseDouble(displayOddsVal);
        if (oddValue<Double.parseDouble(MIN_ODDS_VALUE)){
            return map.get(MIN_ODDS_VALUE);
        }
        if (oddValue>Double.parseDouble(MAX_ODDS_VALUE)){
            return map.get(MAX_ODDS_VALUE);
        }
        displayOddsVal = new DecimalFormat("#.00").format(oddValue);
        String s=null;
        if (!CollectionUtils.isEmpty(map)){
            s= map.get(displayOddsVal);
            if (s==null){
                BigDecimal multiply=new BigDecimal(displayOddsVal);
                while (true){
                    if (map.containsKey(multiply.toPlainString())){
                        return map.get(multiply);
                    }
                    multiply=multiply.subtract(new BigDecimal("0.01"));
                    //避免无效循环
                    if (multiply.doubleValue()<Double.parseDouble(MIN_ODDS_VALUE)  ){
                        return map.get(MIN_ODDS_VALUE);
                    }
                }
            }else {
                return s;
            }

        }
        return s;
	}
}
