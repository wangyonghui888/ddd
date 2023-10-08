package com.panda.sport.rcs.credit.matrix;

import java.util.HashMap;
import java.util.Map;

public class MatrixDataZip {

    private static char[] charSet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static Map<Integer, Character> keySet = new HashMap<>();
    static {
        for(int i=0; i < charSet.length;i++){
            keySet.put(i,charSet[i]);
        }
    }

    /**
     * @Description  压缩
     * @Param [status]
     * @Author  max
     * @Date  11:18 2020/1/22
     * @return java.lang.String
     **/
    public static String compression(String status){
        String result="";
        int length = 0;
        for (int i = 0; i < (status.length() / 2);i++){
            String index = status.substring(length,length+2);
            Character character = keySet.get(Integer.parseInt(index));
            result += character + ",";
            length += 2;
        }
        if(result.endsWith(",")){
            result = result.substring(0,result.length() - 1);
        }

        return result;
    }
    
    public static String queryMatrixStatus(Integer[][] statusArrays){
        String compressionStr = "";
        for(int m = 0 ;m < statusArrays.length ; ++m) {
            for (int n = 0; n < statusArrays[m].length; ++n) {
                if(statusArrays[m][n] != null) {
                    compressionStr += statusArrays[m][n];
                }
            }
        }
        
        compressionStr = compressionStr + "9";
        compressionStr = MatrixDataZip.compression(compressionStr);
        return compressionStr;
    }
}
