package com.panda.sport.rcs.test;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
@Slf4j
public class OSCpuUtils {
	 
	/**
	 * 功能：获取Linux系统cpu使用率
	 */
	public static float cpuUsage() {
		try {
			Map<?, ?> map1 = OSCpuUtils.cpuinfo();
			Thread.sleep(5 * 1000);
			Map<?, ?> map2 = OSCpuUtils.cpuinfo();
 
			long user1 = Long.parseLong(map1.get("user").toString());
			long nice1 = Long.parseLong(map1.get("nice").toString());
			long system1 = Long.parseLong(map1.get("system").toString());
			long idle1 = Long.parseLong(map1.get("idle").toString());
 
			long user2 = Long.parseLong(map2.get("user").toString());
			long nice2 = Long.parseLong(map2.get("nice").toString());
			long system2 = Long.parseLong(map2.get("system").toString());
			long idle2 = Long.parseLong(map2.get("idle").toString());
 
			long total1 = user1 + system1 + nice1;
			long total2 = user2 + system2 + nice2;
			float total = total2 - total1;
 
			long totalIdle1 = user1 + nice1 + system1 + idle1;
			long totalIdle2 = user2 + nice2 + system2 + idle2;
			float totalidle = totalIdle2 - totalIdle1;
 
			float cpusage = (total / totalidle) * 100;
			System.out.println("cpu使用率:"+ cpusage+"%");
			return cpusage;
		} catch (InterruptedException e) {
			log.error("获取Linux系统cpu使用率失败", e);
		}
		return 0;
	}

	/**
	 * 功能：CPU使用信息
	 */
	public static Map<?, ?> cpuinfo() {
		InputStreamReader inputs = null;
		BufferedReader buffer = null;
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			inputs = new InputStreamReader(new FileInputStream("/proc/stat"));
			buffer = new BufferedReader(inputs);
			String line = "";
			while (true) {
				line = buffer.readLine();
				if (line == null) {
					break;
				}
				if (line.startsWith("cpu")) {
					StringTokenizer tokenizer = new StringTokenizer(line);
					List<String> temp = new ArrayList<String>();
					while (tokenizer.hasMoreElements()) {
						String value = tokenizer.nextToken();
						temp.add(value);
					}
					map.put("user", temp.get(1));
					map.put("nice", temp.get(2));
					map.put("system", temp.get(3));
					map.put("idle", temp.get(4));
					map.put("iowait", temp.get(5));
					map.put("irq", temp.get(6));
					map.put("softirq", temp.get(7));
					map.put("stealstolen", temp.get(8));
					break;
				}
			}
		} catch (Exception e) {
			log.error("CPU使用信息, 内存使用率查询失败", e);
		} finally {
			try {
				buffer.close();
				inputs.close();
			} catch (Exception e2) {
				log.error("資源关閉失敗", e2);
			}
		}
		return map;
	}
 
	/**
	 * 功能：内存使用率
	 */
	public static long memoryUsage() {
		Map<String, Object> map = new HashMap<String, Object>();
		InputStreamReader inputs = null;
		BufferedReader buffer = null;
		try {
			inputs = new InputStreamReader(new FileInputStream("/proc/meminfo"));
			buffer = new BufferedReader(inputs);
			String line = "";
			while (true) {
				line = buffer.readLine();
				if (line == null)
					break;
				int beginIndex = 0;
				int endIndex = line.indexOf(":");
				if (endIndex != -1) {
					String key = line.substring(beginIndex, endIndex);
					beginIndex = endIndex + 1;
					endIndex = line.length();
					String memory = line.substring(beginIndex, endIndex);
					String value = memory.replace("kB", "").trim();
					map.put(key, value);
				}
			}
 
			long memTotal = Long.parseLong(map.get("MemTotal").toString());
			System.out.println("内存总量"+memTotal+"KB");
			long memFree = Long.parseLong(map.get("MemFree").toString());
			System.out.println("剩余内存"+memFree+"KB");
			long memused = memTotal - memFree;
			System.out.println("已用内存"+memused+"KB");
			long buffers = Long.parseLong(map.get("Buffers").toString());
			long cached = Long.parseLong(map.get("Cached").toString());
 
			double usage = (double) (memused - buffers - cached) / memTotal * 100;
			System.out.println("内存使用率"+usage+"%");
			
			return memFree;
		} catch (Exception e) {
			log.error("内存使用率查询失败", e);
		} finally {
			try {
				buffer.close();
				inputs.close();
			} catch (Exception e2) {
				log.error("資源关閉失敗", e2);
			}
		}
		return 0;
	}
	
	
	/**
	 * 主入口
	 * @param args
	 */
	public static void main(String[] args) {
//		//1. 创建计时器类
//		Timer timer = new Timer();
//		//2. 创建任务类
//		TimerTask task = new TimerTask() {
//			@Override
//			public void run() {
					//cup使用率
					float cpuUsage = cpuUsage();
					if(cpuUsage > 10.0 ){
						//SendMail.sendMail("xxxxx@qq.com", "服务器cpu使用率过高，请注意查看", "服务器提醒");
					}
					//内存使用情况
					long memoryUsage = memoryUsage();
					if((memoryUsage/1024) < 100){
//						SendMail.sendMail("xxxxx@qq.com","服务器内存剩余空间不足，请注意查看", "服务器提醒");
					}
					System.out.println("-----------");
//			}
//		};
//		timer.schedule(task, 1000, 1000*10);
		
	}
}