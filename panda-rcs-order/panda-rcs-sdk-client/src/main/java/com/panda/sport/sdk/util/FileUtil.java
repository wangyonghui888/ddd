package com.panda.sport.sdk.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtil {

    public String getFileTxt(String fileName){

        InputStream fileStream = FileUtil.class.getResourceAsStream(fileName);
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        String text = "";
        try {
            inputStreamReader = new InputStreamReader(fileStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String lua = bufferedReader.readLine();
            while (lua != null) {
                text += lua;
                lua = bufferedReader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try{
                bufferedReader.close();
                fileStream.close();
                inputStreamReader.close();
            }catch (Exception e){
            }
        }
        return text;
    }

    public static void main(String[] args) {
        String str =new FileUtil().getFileTxt("/lua/orderSave.lua");
        System.out.println(str);
    }
}
