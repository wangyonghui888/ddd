package com.panda.sport.rcs.common.utils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author lithan
 * @description
 * @date 2020/2/17 13:49
 */
public class ExcelUtils {
    /**
     * 文件路径
     */
    public static String filePath = "/opt/logs/panda-rcs-order-statistical/panda-rcs-order-statistical-api/file/";
    /**
     * 创建表头
     * @param workbook
     * @param sheet
     */
    public static void createTitle(HSSFWorkbook workbook,HSSFSheet sheet,String [] title){
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < title.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(title[i]);
        }
    }

    /**
     * 生成excel文件
     * @param filename
     * @param workbook
     * @throws Exception
     */
    public static  String buildExcelFile(String filename,HSSFWorkbook workbook) throws Exception{


        File path=new File(filePath);
        if(!path.exists()){
            path.mkdirs();
        }

        File file=new File(path.getAbsolutePath(), filename);


        FileOutputStream fos = new FileOutputStream(file);
        workbook.write(fos);


        fos.flush();
        fos.close();
        //filename = Base64.encodeBase64String(filename.getBytes());

        return filename;
    }

    /**
     * 浏览器下载excel
     * @param filename
     * @param workbook
     * @param response
     * @throws Exception
     */
    public static  void download(String filename, HSSFWorkbook workbook, HttpServletResponse response) throws Exception{

        buildExcelFile(filename,workbook);

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode(filename, "utf-8"));
        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    /**
     * @Description   生成随机文件名，防止上传文件后文件名重复
     * @Param []
     * @Author  toney
     * @Date  0:08 2020/7/6
     * @return java.lang.String
     **/
    public static String generateRandomFilename(){
        String RandomFilename = "";
        Random rand = new Random();//生成随机数
        int random = rand.nextInt();

        Calendar calCurrent = Calendar.getInstance();
        int intDay = calCurrent.get(Calendar.DATE);
        int intMonth = calCurrent.get(Calendar.MONTH) + 1;
        int intYear = calCurrent.get(Calendar.YEAR);
        String now = String.valueOf(intYear) + "_" + String.valueOf(intMonth) + "_" +
                String.valueOf(intDay) + "_";

        RandomFilename = now + String.valueOf(random > 0 ? random : ( -1) * random);

        return RandomFilename;
    }
    /**
     * @Description   生成文件名
     * @Param [fileName]
     * @Author  toney
     * @Date  0:08 2020/7/6
     * @return java.lang.String
     **/
    public static  String generatorFileName(String fileName ) throws Exception{
        Date date =new Date();
        fileName += "_"+ generateRandomFilename()+".xls";
        //fileName=Base64.encodeBase64String(fileName.getBytes())+".xls";
        return fileName;
    }
}