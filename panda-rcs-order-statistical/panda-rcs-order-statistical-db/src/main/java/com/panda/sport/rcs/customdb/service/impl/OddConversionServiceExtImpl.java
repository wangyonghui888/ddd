package com.panda.sport.rcs.customdb.service.impl;

import com.panda.sport.rcs.customdb.entity.OddConversionEntity;
import com.panda.sport.rcs.customdb.mapper.OddConversionMapper;
import com.panda.sport.rcs.customdb.service.IOddConversionServiceExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.customdb.service.impl
 * @description :   赔率转换 服务
 * @date: 2020-07-21 10:26
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service("oddConversionServiceExtImpl")
public class OddConversionServiceExtImpl implements IOddConversionServiceExt {

    @Autowired
    OddConversionMapper conversionMapper;

    /***欧赔转马赔的 map ***/
    Map<String, String> eu2MaOddMap = new HashMap<>();

    /***马赔转欧赔的 map ***/
    Map<String, String> ma2OuOddMap = new HashMap<>();

    @PostConstruct
    public void initial() {
        List<OddConversionEntity> mapper =  getOddConversion();
        mapper.forEach(e -> {
            eu2MaOddMap.put(e.getEuOdds(),e.getMyOdds());
            ma2OuOddMap.put(e.getMyOdds(),e.getEuOdds());
        });
    }
    
    @Override
    public List<OddConversionEntity> getOddConversion() {
        return conversionMapper.getOddConversion();
    }
 
    @Override
    public String getEuOddByMaOdd(String euOdd) {
        return eu2MaOddMap.get(euOdd);
    }
 
}
