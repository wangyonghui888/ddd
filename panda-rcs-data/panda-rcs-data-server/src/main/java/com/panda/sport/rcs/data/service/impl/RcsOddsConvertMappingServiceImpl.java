package com.panda.sport.rcs.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.data.mapper.RcsOddsConvertMappingMapper;
import com.panda.sport.rcs.data.service.RcsOddsConvertMappingService;
import com.panda.sport.rcs.data.utils.BigDecimalUtils;
import com.panda.sport.rcs.pojo.RcsOddsConvertMapping;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private Map<String,String> myToEuOddsMap = Maps.newHashMap();
    private static String  MIN_ODDS_VALUE;
    private static String  MAX_ODDS_VALUE;

    @Autowired
    private RcsOddsConvertMappingMapper mapper;

    @PostConstruct
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

    /**
     * @Description   //根据马来赔获取欧赔
     * @Param [myOdds]
     * @Author  Sean
     * @Date  16:50 2020/10/3
     * @return java.lang.String
     **/
    @Override
    public String getEUOdds(String myOdds){
        myOdds = formatOdds(myOdds);
        String odds = myToEuOddsMap.get(myOdds);
        if (StringUtils.isEmpty(odds)){
            odds = NumberUtils.INTEGER_ZERO.toString();
        }
        return odds;
    }

    @Override
    public int myOddsToOddsValue(BigDecimal myOdds) {
        BigDecimal euOdds = BigDecimalUtils.toBigDecimal(getEUOdds(myOdds.toPlainString()), BigDecimal.ZERO);
        return BigDecimalUtils.ROUND_DOWN_2.multiply(euOdds, new BigDecimal(BaseConstants.MULTIPLE_VALUE)).intValue();
    }

    /**
     * @Description   //赔率保存两位小数
     * @Param [myOdds]
     * @Author  Sean
     * @Date  14:58 2020/10/23
     * @return java.lang.String
     **/
    private String formatOdds(String myOdds) {
        return new BigDecimal(myOdds).divide(new BigDecimal(NumberUtils.DOUBLE_ONE),NumberUtils.INTEGER_TWO,BigDecimal.ROUND_DOWN).toPlainString();
    }
}
