package com.panda.sport.rcs.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.utils
 * @Description :  汉字转换为拼音
 * @Date: 2020-06-02 21:24
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
public class WordToPinYinUtil {
    /**
     * @return java.lang.String
     * @Description 汉字转换为拼音
     * @Param [chinese]
     * @Author toney
     * @Date 21:24 2020/6/2
     **/
    public static String ToPinyin(String chinese) {
        chinese=getChinese(chinese);
        String pinyinStr = "";
        char[] newChar = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < newChar.length; i++) {
            if (newChar[i] > 128) {
                try {
                    pinyinStr += PinyinHelper.toHanyuPinyinStringArray(newChar[i], defaultFormat)[0];
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinStr += newChar[i];
            }
        }
        return pinyinStr;
    }

    /**
     * @return java.lang.String
     * @Description 汉字转换为拼音首字母
     * @Param [str]
     * @Author toney
     * @Date 21:25 2020/6/2
     **/
    public static String ToFirstChar(String str) {
    	String convert = "";
    	try {
    		str= getChinese(str);
            for (int j = 0; j < str.length(); j++) {
                char word = str.charAt(j);
                String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
                if (pinyinArray != null) {
                    convert += pinyinArray[0].charAt(0);
                } else {
                    convert += word;
                }
            }
    	}catch (Exception e) {
    		log.error(e.getMessage(),e);
    	}
    	return convert.toUpperCase();
    }
    
    public static String getFirshChar(String str) {
    	String convert = "";
    	try {
    		convert = ToFirstChar(str);
    		if(StringUtils.isBlank(convert)) return "";
    		
    		return convert.substring(0,1);
    	}catch (Exception e) {
    		log.error(e.getMessage(),e);
    	}
    	return convert;
    }
    
    /**
     * @Description   获取字符串中的中文和英文首字母
     * @Param [str]
     * @Author  toney
     * @Date  21:41 2020/6/2
     * @return java.lang.String
     **/
    private static String getChinese(String str){
        String regex = "[a-zA-Z\\u4e00-\\u9fa5]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        StringBuilder sb =new StringBuilder();
        while(matcher.find()){
            for(int i = 0;i <= matcher.groupCount();i++){
                sb.append(matcher.group(0));
            }
        }

        return sb.toString();
    }

    public static void main(String[] args) {
    	String allChinese = "abc";
        System.out.println(ToFirstChar(allChinese));
    }
}
