package com.panda.sport.rcs.dj.util;

import java.util.Random;

/**
 * @ClassName CharacterUtils
 * @Description TODO
 * @Author Administrator
 * @Date 2021/9/18 18:31
 * @Version 1.0
 **/
public class CharacterUtils {

    public static String getRandomString(int length) {

        //定义一个字符串(A-Z，a-z，0-9)即62位；
        String str = "zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";

        //由Random生成随机数
        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        //长度为几就循环几次
        for (int i = 0; i < length; i++) {
            //产生0-61的数字
            int number = random.nextInt(62);

            //将产生的数字通过length次承载到sb中
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
