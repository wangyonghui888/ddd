package com.panda.sport.rcs.common.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.List;

public class ExportUtils<T> {

    public void export(HttpServletResponse response,String fileName, String sheetName, String columns[], String attributes[], List<T> list) throws Exception {

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(sheetName);
        HSSFRow row = sheet.createRow(0);
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式

        HSSFCell cell = null;

        //创建标题
        for(int i=0;i<columns.length;i++){
            cell = row.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(style);
        }

        //创建内容
        for (int i = 0; i < list.size(); i++) {

            //当前数据
            Object o1 = list.get(i);
            Class<?> class1 = o1.getClass();
            row = sheet.createRow((int) i + 1);
            for (int a = 0; a < attributes.length; a++) {

                String str = attributes[a];
                Field field = class1.getDeclaredField(str);
                field.setAccessible(true);
                Object value = field.get(o1);
                if (value != null) {
                    row.createCell(a).setCellValue(String.valueOf(value));
                }
            }

        }

        response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        response.setHeader("Content-Transfer-Encoding", "binary");
        ServletOutputStream os = response.getOutputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            wb.write(os);
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            bis.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
                os.flush();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
