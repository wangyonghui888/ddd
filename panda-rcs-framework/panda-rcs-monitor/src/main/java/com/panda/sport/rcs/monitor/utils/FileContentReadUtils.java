package com.panda.sport.rcs.monitor.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.panda.sport.rcs.monitor.task.log.LogbackMonitorAppender;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class FileContentReadUtils {

    /** 
      * 实时输出日志信息 
      * @param logFile 日志文件   lastTimeFileSize   上次文件大小  
      * @throws IOException 
      */
    public static String realtimeShowLog(LogbackMonitorAppender appender) throws IOException {
    	//指定文件可读可写  
        RandomAccessFile randomFile = null;

        try {
        	randomFile = new RandomAccessFile(new File(appender.getFile()), "r");
        	//获得变化部分的  
            randomFile.seek(appender.getLastTimeFileSize());

            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = randomFile.readLine()) != null) {
            	sb.append(new String(line.getBytes("ISO-8859-1"),"utf-8")).append("\r\n");
            }
            appender.setLastTimeFileSize(randomFile.length());

            return sb.toString();
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }finally {
			if(randomFile != null ) randomFile.close();
		}
        return null;
    }
}
