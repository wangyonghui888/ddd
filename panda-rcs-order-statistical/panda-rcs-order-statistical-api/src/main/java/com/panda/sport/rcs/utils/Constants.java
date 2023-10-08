package com.panda.sport.rcs.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @author :  skyKong
 * @Project Name :  Constants
 * @Package Name :  om.panda.sport.rcs.utils
 * @Description :  基础类型
 * @Date: 2023-01-26 15:18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class Constants {

    public  static  final  String TagType="标签类别";
    public  static  final  String EnglishTagName="标签英文名称";
    public  static  final  String TagName="标签中文名称";
    public  static  final  String TagRecheckDays="标签自动化规则-复核时间段";
    public  static  final  String IsRecheck="标签自动化规则-循环复核";
    public  static  final  String IsCalculate="标签判断规则-停止计算";
    public  static  final  String TagDetail="标签说明";
    public  static  final  String TagColor="标签展示样式-颜色";
    public  static  final  String TagImgUrl="标签展示样式-标签图标";
    public  static  final  String IsAuto="标签自动化规则-自动化标签";
    public  static  final  String IsDefault="标签自动化规则-默认标签";
    public  static  final  String IsRollback="标签自动化规则-不达标退至上级标签";
    public  static  final  String FatherId="标签自动化规则-上级标签";
    public  static  final  String RiskStatus="允许风控措施";

    public  static  final String GroupRule_1="标签判断规则-%s-参数1";
    public  static  final String GroupRule_2="标签判断规则-%s-参数2";
    public  static  final String GroupRule_3="标签判断规则-%s-参数3";
    public  static  final String GroupRule_4="标签判断规则-%s-参数4";
    public  static  final String GroupRule_5="标签判断规则-%s-参数5";
    public  static  final String GroupRule_6="标签判断规则-%s-参数6";

    public  static  final String SingeRule_1="标签判断规则-%s-参数1";
    public  static  final String SingeRule_2="标签判断规则-%s-参数2";
    public  static  final String SingeRule_3="标签判断规则-%s-参数3";
    public  static  final String SingeRule_4="标签判断规则-%s-参数4";
    public  static  final String SingeRule_5="标签判断规则-%s-参数5";
    public  static  final String SingeRule_6="标签判断规则-%s-参数6";

    public  static  final String SingeRule="标签判断规则-%s";

    public  static  final String SingeRule_Rule="标签判断规则-独立规则";

    public  static  final String SingeRule_Group="标签判断规则-组合规则";

    public  static  final String LOG_INSERT="新增";

    public static final String logCode="10020";

    public static final String RCS_BUSINESS_LOG_SAVE = "rcs_business_log_save";

    public static final String TAG_TITLE = "标签管理";
    public static final String SECOND_TAG = "二级标签";

    public static final String DELETE = "刪除";

    public static final String NONE = "无";

    public static final String ADD_TAG = "新增标签";

    public static final String EDIT_EN_TAG = "修改英文标签";
    public static final String EDIT_ZS_TAG = "修改中文标签";
    public static final String EDIT_EN_DETAIL = "修改英文說明";
    public static final String EDIT_ZS_DETAIL = "修改中文說明";

    public static final String DELETE_TAG = "删除标签";

    public static final String UPDATE_USER_SECOND_TAGS_TOPIC = "UPDATE_USER_SECOND_TAGS_TOPIC";

    public static String getTagTypeByValue(Integer tagType){
        String levelName = "";
        switch (tagType){
            case 0:
            case 1:
                levelName = "基本属性类";
                break;
            case 2:
                levelName = "投注特征类";
                break;
            case 3:
                levelName = "访问特征类";
                break;
            case 4:
                levelName = "财务特征类";
                break;
            default:
                break;
        }
        return levelName;
    }
    public static Integer getUserId() throws Exception {
       return  TradeUserUtils.getUserId();
    }

    public static Integer getAppId() throws Exception {
        return  TradeUserUtils.getAppId();
    }
}
