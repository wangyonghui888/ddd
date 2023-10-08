package com.panda.sport.rcs.common;

import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.enums.DataSourceTypeEnum;
import com.panda.sport.rcs.enums.MarketKindEnum;
import com.panda.sport.rcs.exeception.RcsServiceException;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description //TODO
 * @Param
 * @Author kimi
 * @Date 2019/11/15
 * @return
 **/
@Slf4j
public class MarginUtils {


    /**
     * @return boolean
     * @Description //magin值校验
     * @Param [oddsList, marketKindEnum, magin]
     * @Author kimi
     * @Date 2019/12/11
     **/
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
                if(sumValue.compareTo(new BigDecimal(1))>=0){
                    calcedMargin = (new BigDecimal(2)).subtract(sumValue);
                }else{
                    calcedMargin = sumValue.multiply(new BigDecimal(-1));
                }
                return margin.doubleValue() == calcedMargin.doubleValue();
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
                    if (fieldOddsValue.compareTo(BigDecimal.ZERO) != 0) {
                        BigDecimal divide = bigDecimal.divide(fieldOddsValue, BaseConstants.ODDS_CALCULATION_SCALE, BigDecimal.ROUND_HALF_UP);
                        margin1 = margin1.add(divide);
                    }
                }
                return margin1.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_DOWN);
            case Malaysia:
                BigDecimal fieldOddsValueNum = new BigDecimal(0);
                for (Map<String, Object> map : oddsList) {
                    BigDecimal fieldOddsValue = new BigDecimal(map.get("fieldOddsValue").toString());
                    fieldOddsValueNum = fieldOddsValueNum.add(fieldOddsValue);
                }
                //小于0
                if (fieldOddsValueNum.compareTo(new BigDecimal(0)) == -1) {
                    margin1 = fieldOddsValueNum.divide(new BigDecimal(-1)).setScale(2, BigDecimal.ROUND_DOWN);
                } else {
                    margin1 = new BigDecimal("2").subtract(fieldOddsValueNum).setScale(2, BigDecimal.ROUND_DOWN);
                }
                return margin1;
            default:
                return margin1;
        }
    }

    public static void main(String[] args) {
        System.out.println(BigDecimal.ZERO.compareTo(BigDecimal.ZERO));
    }
}
