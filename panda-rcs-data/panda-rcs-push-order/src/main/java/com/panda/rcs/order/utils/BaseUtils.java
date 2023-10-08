package com.panda.rcs.order.utils;

/**
 * 基础方法封装
 */
public class BaseUtils {

    /**
     * 多字符串参数拼接单字符串
     * @param params
     * @return
     */
    public static String toStringForParams(String ... params){
        StringBuffer sb = new StringBuffer();
        if(params.length > 0){
            for (String param : params){
                sb.append(param);
            }
        }
        return sb != null ? sb.toString() : null;
    }

    /**
     * 拼装数组为字符串
     * @param playIds
     * @return
     */
    public static String toStringForParams(Integer [] playIds){
        StringBuffer sb = new StringBuffer();
        if(playIds.length > 0){
            for (int i = 0; i < playIds.length; i ++){
                sb.append(playIds[i]);
                if(i < playIds.length - 1){
                    sb.append("_");
                }
            }
        }
        return sb != null ? sb.toString() : null;
    }
    /**
     * 排列组合计算
     *
     * @param n 大的数
     * @param k 小的数
     * @return
     */
    public static int combination(int n, int k) {
        int a = 1, b = 1;
        if (k > n / 2) {
            k = n - k;
        }
        for (int i = 1; i <= k; i++) {
            a *= (n + 1 - i);
            b *= i;
        }
        return a / b;
    }


}
