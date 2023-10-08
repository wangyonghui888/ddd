package com.panda.sport.rcs.task.utils;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.enums.DataSourceTypeEnum;
import com.panda.sport.rcs.enums.MarketKindEnum;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author :  holly
 * @Project Name :  panda-rcs-trade
 * @Package Name :  com.panda.sport.rcs.trade.util
 * @Description :  TODO
 * @Date: 2020-08-08 15:13
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class MarginUtils {

    public static boolean convert(List<Map<String, Object>> oddsList,
                                  Long dataSource, String marketKindEnumString, BigDecimal margin) {
        BigDecimal calcedMargin = new BigDecimal(0);
        BigDecimal sumValue = new BigDecimal(0);
        MarketKindEnum marketKindEnum = MarketKindEnum.getMarketKindByValue(marketKindEnumString);
        switch (marketKindEnum) {
            //欧赔 独赢盘
            case Europe:
                //自动操盘

                if(dataSource == DataSourceTypeEnum.AUTOMATIC.getValue().longValue()){
//                    return Boolean.TRUE;
                    for (Map<String, Object> map : oddsList) {
                        BigDecimal fieldOddsValue = new BigDecimal(map.get("margin").toString());
                        sumValue = sumValue.add(fieldOddsValue);
                    }
                    return sumValue.compareTo(margin.multiply(new BigDecimal(3))) == 0 ? Boolean.TRUE:Boolean.FALSE;
                }else{
                    calcedMargin = convert(oddsList, marketKindEnumString);
                    BigDecimal upMargin = calcedMargin.setScale(0,BigDecimal.ROUND_HALF_UP);
                    BigDecimal downMargin = calcedMargin.setScale(0,BigDecimal.ROUND_HALF_DOWN);
                    return downMargin.compareTo(margin) == 0 || upMargin.compareTo(margin) == 0? Boolean.TRUE:Boolean.FALSE;
                }
                //马培 两项盘 如果odds1+odds2>=1 margin=2-(odds1+odds2)  如果odds1+odds2<1 margin= -(odds1+odds2)
            case Malaysia:
                //自动操盘 不需要校验
                if(dataSource == DataSourceTypeEnum.AUTOMATIC.getValue().longValue()){
                    return Boolean.TRUE;
                }
                for (Map<String, Object> map : oddsList) {
                    BigDecimal fieldOddsValue = new BigDecimal(map.get("fieldOddsValue").toString());
                    fieldOddsValue = fieldOddsValue.
                            divide(new BigDecimal(BaseConstants.MULTIPLE_VALUE), 2, BigDecimal.ROUND_HALF_UP);
                    sumValue = sumValue.add(fieldOddsValue);
                }
                if(sumValue.compareTo(new BigDecimal(0))>0){
                    sumValue = (new BigDecimal(2)).subtract(sumValue);
                }else if (sumValue.compareTo(new BigDecimal(0)) < 0){
                    sumValue = sumValue.multiply(new BigDecimal(-1));
                }
                return margin.compareTo(sumValue) == 0;
            default:
                return false;
        }
    }


    public static BigDecimal convert(List<Map<String, Object>> oddsList, String marketKindEnumString) {
        BigDecimal margin1 = new BigDecimal(0);
        BigDecimal bigDecimal = new BigDecimal(BaseConstants.MULTIPLE_VALUE);
        MarketKindEnum marketKindEnum = MarketKindEnum.getMarketKindByValue(marketKindEnumString);
        switch (marketKindEnum) {
            case Europe:
                for (Map<String, Object> map : oddsList) {
                    BigDecimal fieldOddsValue = new BigDecimal(map.get("fieldOddsValue").toString());
                    fieldOddsValue = fieldOddsValue.divide(new BigDecimal(BaseConstants.MULTIPLE_VALUE),2,BigDecimal.ROUND_DOWN).multiply(new BigDecimal(BaseConstants.MULTIPLE_VALUE));
                    if (fieldOddsValue.compareTo(BigDecimal.ZERO) != 0) {
                        BigDecimal divide = bigDecimal.divide(fieldOddsValue, 4, BigDecimal.ROUND_HALF_DOWN);
                        margin1 = margin1.add(divide);
                    }else {
                        margin1 = margin1.add(BigDecimal.ONE);
                    }
                }
                return margin1.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_DOWN);
            case Malaysia:
                BigDecimal fieldOddsValueNum = new BigDecimal(0);
                for (Map<String, Object> map : oddsList) {
                    BigDecimal fieldOddsValue = new BigDecimal(map.get("fieldOddsValue").toString());
                    fieldOddsValueNum = fieldOddsValueNum.add(fieldOddsValue);
                }
                //小于0
                if (fieldOddsValueNum.compareTo(new BigDecimal(0)) > 0) {
                    fieldOddsValueNum = new BigDecimal("2").subtract(fieldOddsValueNum).setScale(2, BigDecimal.ROUND_DOWN);
                } else if (fieldOddsValueNum.compareTo(new BigDecimal(0)) < 0){
                    fieldOddsValueNum = fieldOddsValueNum.multiply(new BigDecimal(-1)).setScale(2, BigDecimal.ROUND_DOWN);
                }
                return fieldOddsValueNum;
            default:
                return margin1;
        }
    }


    /**
     * @Description 如果负数超过1了，就+2
     *             如果整数超过1 了，就-2
     * @Param [originalOdds]
     * @Author  Sean
     * @Date  14:34 2020/8/15
     * @return java.math.BigDecimal
     **/
    public static BigDecimal checkMyOdds(BigDecimal originalOdds){
        BigDecimal odds = BigDecimal.ZERO;
        if (ObjectUtils.isNotEmpty(originalOdds)){
            if (originalOdds.compareTo(new BigDecimal("-1")) <= 0){
                odds = originalOdds.add(new BigDecimal("2"));
            }else if (originalOdds.compareTo(new BigDecimal("1")) > 0){
                odds = originalOdds.subtract(new BigDecimal("2"));
            }else {
                odds = originalOdds;
            }
        }
        return odds;
    }
}
