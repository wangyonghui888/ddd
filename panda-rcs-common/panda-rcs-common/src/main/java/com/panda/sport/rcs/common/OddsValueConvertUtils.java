package com.panda.sport.rcs.common;

import com.github.kiprobinson.bigfraction.BigFraction;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.enums.MarketKindEnum;

import java.math.BigDecimal;

/**
 * 赔率转换工具类，原始值为欧洲赔率
 * 转换公式来源：http://47.75.151.110:8090/pages/viewpage.action?pageId=67345
 */
public class OddsValueConvertUtils {


    /**
     * 欧盘转换为其他盘口赔率
     *
     * @param kind          盘口类型，null或非系统支持的赔率返回原值
     * @param originalValue 原始值（欧赔）
     * @return
     */
    public static BigDecimal convertTo(MarketKindEnum kind, Integer originalValue) {
        if (originalValue == null) {
            return null;
        }
        if (kind == null) {
            return div(originalValue, BaseConstants.MULTIPLE_VALUE);
        }
        BigDecimal newValue = null;
        switch (kind) {
            case HongKong:
            case UnitedKingdom:
                //newValue = new BigDecimal(originalValue - 1 * BaseConstants.MULTIPLE_VALUE);
                newValue = div(originalValue - 1 * BaseConstants.MULTIPLE_VALUE, BaseConstants.MULTIPLE_VALUE);
                break;
            case Indonesia:
                if (originalValue <= 2 * BaseConstants.MULTIPLE_VALUE) {
                    newValue = div(-1 * BaseConstants.MULTIPLE_VALUE, originalValue - 1 * BaseConstants.MULTIPLE_VALUE);
                } else {
                    //newValue = new BigDecimal(originalValue - 1 * BaseConstants.MULTIPLE_VALUE);
                    newValue = div(originalValue - 1 * BaseConstants.MULTIPLE_VALUE, BaseConstants.MULTIPLE_VALUE);
                }
                break;
            case Malaysia:
                if (originalValue < 2 * BaseConstants.MULTIPLE_VALUE) {
                    //newValue = new BigDecimal(originalValue - 1 * BaseConstants.MULTIPLE_VALUE);
                    newValue = div(originalValue - 1 * BaseConstants.MULTIPLE_VALUE, BaseConstants.MULTIPLE_VALUE);
                } else {
                    newValue = div(1 * BaseConstants.MULTIPLE_VALUE, 1 * BaseConstants.MULTIPLE_VALUE - originalValue);
                }
                break;
            case UnitedStates:
                if (originalValue < 2 * BaseConstants.MULTIPLE_VALUE) {
                    newValue = div(100, 1 * BaseConstants.MULTIPLE_VALUE - originalValue);
                } else {
                    //newValue = new BigDecimal(100 * (originalValue - 1 * BaseConstants.MULTIPLE_VALUE));
                    newValue = div(100 * (originalValue - 1 * BaseConstants.MULTIPLE_VALUE), BaseConstants.MULTIPLE_VALUE);
                }
                break;
            default:
                newValue = div(originalValue, BaseConstants.MULTIPLE_VALUE);
                break;
        }
        return newValue;
    }
    /**
     * @Description   欧赔转马来赔精确到scale位小数
     * @Param [kind, originalValue]
     * @Author  Sean
     * @Date  16:29 2019/12/11
     * @return java.math.BigDecimal
     **/
    public static BigDecimal convertTo(MarketKindEnum kind, Integer originalValue,Integer scale) {
        if(null == scale){
            return convertTo(kind,originalValue);
        }
        if (originalValue == null) {
            return null;
        }
        if (kind == null) {
            return div(originalValue, BaseConstants.MULTIPLE_VALUE);
        }
        BigDecimal newValue = null;
        switch (kind) {
            case HongKong:
            case UnitedKingdom:
                newValue = div(originalValue - 1 * BaseConstants.MULTIPLE_VALUE, BaseConstants.MULTIPLE_VALUE,scale);
                break;
            case Indonesia:
                if (originalValue <= 2 * BaseConstants.MULTIPLE_VALUE) {
                    newValue = div(-1 * BaseConstants.MULTIPLE_VALUE, originalValue - 1 * BaseConstants.MULTIPLE_VALUE,scale);
                } else {
                    newValue = div(originalValue - 1 * BaseConstants.MULTIPLE_VALUE, BaseConstants.MULTIPLE_VALUE,scale);
                }
                break;
            case Malaysia:
                if (originalValue < 2 * BaseConstants.MULTIPLE_VALUE) {
                    newValue = div(originalValue - 1 * BaseConstants.MULTIPLE_VALUE, BaseConstants.MULTIPLE_VALUE,scale);
                } else {
                    newValue = div(1 * BaseConstants.MULTIPLE_VALUE, 1 * BaseConstants.MULTIPLE_VALUE - originalValue,scale);
                }
                break;
            case UnitedStates:
                if (originalValue < 2 * BaseConstants.MULTIPLE_VALUE) {
                    newValue = div(100, 1 * BaseConstants.MULTIPLE_VALUE - originalValue,scale);
                } else {
                    newValue = div(100 * (originalValue - 1 * BaseConstants.MULTIPLE_VALUE), BaseConstants.MULTIPLE_VALUE,scale);
                }
                break;
            default:
                newValue = div(originalValue, BaseConstants.MULTIPLE_VALUE,scale);
                break;
        }
        return newValue;
    }
    /**
     * @Description   其他赔率转欧赔，目前只支持马来赔
     * @Param [kind, originalValue, scale]
     * @Author  Sean
     * @Date  16:37 2019/12/11
     * @return java.math.BigDecimal
     **/
    public static BigDecimal convertToEU(MarketKindEnum kind, Integer originalValue,Integer scale) {
        if (originalValue == null) {
            return null;
        }
        if (kind == null) {
            return div(originalValue, BaseConstants.MULTIPLE_VALUE,scale);
        }
        BigDecimal newValue = null;
        switch (kind) {
            case Malaysia:
                if (originalValue < 0) {
                    newValue = div(-1 * BaseConstants.MULTIPLE_VALUE, originalValue,scale).add(new BigDecimal(1)).multiply(new BigDecimal(BaseConstants.MULTIPLE_VALUE));
                } else {
                    newValue = new BigDecimal(originalValue).add(new BigDecimal(BaseConstants.MULTIPLE_VALUE)) ;
                }
                break;
            default:
                newValue = div(originalValue, BaseConstants.MULTIPLE_VALUE,scale);
                break;
        }
        return newValue;
    }

//    public static void main(String[] args) {
//        BigDecimal b = convertToEU(MarketKindEnum.Malaysia,-80000,5);
//        System.out.println(b);
//    }
    /**
     * 赔率转换并已默认方式显示
     *
     * @param kind
     * @param originalValue
     * @return
     */
    public static String convertAndDefaultDisply(MarketKindEnum kind, Integer originalValue) {
        BigDecimal newValue = convertTo(kind, originalValue);
        if (newValue == null) {
            return null;
        }
        BigDecimal b2 = new BigDecimal(BaseConstants.MULTIPLE_VALUE);
        if (MarketKindEnum.UnitedKingdom == kind) {
            // 英式赔率以分数表示
            return BigFraction.valueOf(originalValue - 1 * BaseConstants.MULTIPLE_VALUE, b2).toString();
        }

        return NumberUtils.decimalFormat("#.##", newValue.doubleValue());
    }

    public static BigDecimal div(Integer v1, Integer v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.divide(b2, 2, BigDecimal.ROUND_DOWN);
    }
    public static BigDecimal div(Integer v1, Integer v2,Integer scale) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.divide(b2, scale, BigDecimal.ROUND_DOWN);
    }

}
