package com.panda.sport.rcs.monitor.task.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class LogbackMonitorAppender<E> extends ch.qos.logback.core.rolling.RollingFileAppender<E>{
	
	private Long lastTimeFileSize = null;
	
	private String lastFileName = null;

	@Override
	public void start() {
		super.start();
		
		if(LogMonitorApi.containsKey("MONITOR_" + getName()))  {
			if(!LogMonitorApi.getAppenderList().get("MONITOR_" + getName()).isStarted()) {
				LogMonitorApi.getAppenderList().remove("MONITOR_" + getName());
			}else {
				return;
			}
		}
		RandomAccessFile randomFile = null;
		try {
			randomFile = new RandomAccessFile(new File(getFile()), "r");
			lastTimeFileSize = randomFile.length();
			lastFileName = getFile();
			LogMonitorApi.addAppender("MONITOR_" + getName(),this);
		}catch (FileNotFoundException e) {
			lastTimeFileSize = 0l;
			lastFileName = getFile();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(randomFile != null) {
				try {
					randomFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Long getLastTimeFileSize() {
		return lastTimeFileSize;
	}

	public void setLastTimeFileSize(Long lastTimeFileSize) {
		this.lastTimeFileSize = lastTimeFileSize;
	}

	public String getLastFileName() {
		return lastFileName;
	}

	public void setLastFileName(String lastFileName) {
		this.lastFileName = lastFileName;
	}
	
}
