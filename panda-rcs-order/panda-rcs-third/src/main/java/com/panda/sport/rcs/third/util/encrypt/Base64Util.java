package com.panda.sport.rcs.third.util.encrypt;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Beulah
 * @date 2023/3/28 19:09
 * @description 采用64个基本的ASCII码字符对数据进行重新编码
 */
public abstract class Base64Util {  
  
    /** 
     * 字符编码 
     */  
    public final static String ENCODING = "UTF-8";  
  
    /** 
     * Base64编码 
     *  
     * @param data 待编码数据 
     * @return String 编码数据 
     * @throws Exception 
     */  
    public static String encode(String data) throws Exception {  
  
        // 执行编码  
        byte[] b = Base64.encodeBase64(data.getBytes(ENCODING));  
  
        return new String(b, ENCODING);  
    }  
  
    /** 
     * Base64安全编码<br> 
     * 遵循RFC 2045实现 
     *  
     * @param data 
     *            待编码数据 
     * @return String 编码数据 
     *  
     * @throws Exception 
     */  
    public static String encodeSafe(String data) throws Exception {  
  
        // 执行编码  
        byte[] b = Base64.encodeBase64(data.getBytes(ENCODING), true);  
  
        return new String(b, ENCODING);  
    }  
  
    /** 
     * Base64解码 
     *  
     * @param data 待解码数据 
     * @return String 解码数据 
     * @throws Exception 
     */  
    public static String decode(String data) throws Exception {  
  
        // 执行解码  
        byte[] b = Base64.decodeBase64(data.getBytes(ENCODING));  
  
        return new String(b, ENCODING);  
    }  
  
}  