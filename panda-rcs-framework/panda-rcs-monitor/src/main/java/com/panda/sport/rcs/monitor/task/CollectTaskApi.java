package com.panda.sport.rcs.monitor.task;

import com.panda.sport.rcs.monitor.utils.OsUtis;

public abstract class CollectTaskApi implements Runnable{
	
	//是否只执行一次
	public Boolean isStartOne = false;
	
	public Boolean isExe = false;
	
	//second
	public Long exetime = 60l;
	
	public String ip = OsUtis.getIp();
	
	public String pid = OsUtis.getPid();
	
	public String topic = "RCS_COLLECT_DATA";
	
	public CollectTaskApi(Long exetime) {
		this.exetime = exetime;
	}
	
	@Override
	public void run() {
		if(isStartOne == true && isExe == false) {
			isExe = true;
			execute();
		}else if(isStartOne == false) {
			execute();
		}
	}
	
	public abstract void execute();

	public Long getExetime() {
		return exetime;
	}

	public void setExetime(Long exetime) {
		this.exetime = exetime;
	}
	
	public boolean isStart() {
		return true;
	}
	
}
