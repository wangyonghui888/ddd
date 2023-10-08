package com.panda.sport.rcs.third.util.encrypt;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author Beulah
 * @date 2023/3/28 19:09
 * @description md5加密工具
 */
@Slf4j
public class MD5Util {

    //秘钥因子
    public final static String SECRET = "panda_third_external_token";

    /**
     * MD5方法
     *
     * @param text 明文
     * @return 密文
     */
    public static String md5Hex(String text) {
        try {
            return DigestUtils.md5Hex(text + SECRET);
        } catch (Exception e) {
            log.error("MD5加密异常", e);
        }
        return "";
    }

    public static String sha256Hex(String text) {
        try {
            return DigestUtils.sha256Hex(text + SECRET);
        } catch (Exception e) {
            log.error("sha256加密异常", e);
        }
        return "";
    }


    /**
     * MD5验证方法 根据传入的密钥进行验证
     *
     * @param text 明文
     * @param md5  密文
     * @return true/false
     */
    public static boolean verifyMD5Hex(String text, String md5) {
        try {
            String md5Text = md5Hex(text);
            return md5.equalsIgnoreCase(md5Text);
        } catch (Exception e) {
            log.error("MD5秘钥验证异常", e);
        }
        return false;
    }

    public static boolean verifySHA256Hex(String text, String md5) {
        try {
            String md5Text = sha256Hex(text);
            return md5.equalsIgnoreCase(md5Text);
        } catch (Exception e) {
            log.error("MD5秘钥验证异常", e);
        }
        return false;
    }

}