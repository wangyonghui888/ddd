package com.panda.sport.rcs.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.utils
 * @Description :  TODO
 * @Date: 2020-01-15 17:48
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils{
	
	/**
     * 常见特殊字符过滤
     * 
     * @param str
     * @return
     */
    public static String filtration(String str) {
    	String regEx = "[`~!@#$%^&*()+=|{}:;\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？']";
    	str = Pattern.compile(regEx).matcher(str).replaceAll("").trim();
    	
    	return str;
    }
    
    public static List<Integer> parsingString(String s) {
        List<Integer> list = new ArrayList();
        String[] split = s.split(",");
        for (String s1 : split) {
            if (s1 != null && s1.length() != 0) {
                list.add(Integer.parseInt(s1));
            }
        }
        return list;
    }
    
    public static void main(String[] args) {
		System.out.println(filtration("1234,,1;':><?.,..,.123"));
	}
    
}
