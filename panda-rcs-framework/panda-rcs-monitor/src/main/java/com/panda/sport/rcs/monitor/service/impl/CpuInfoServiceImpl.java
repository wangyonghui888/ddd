package com.panda.sport.rcs.monitor.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.panda.sport.rcs.monitor.entity.SysInfoBean;
import com.panda.sport.rcs.monitor.service.CpuInfoService;
import com.panda.sport.rcs.monitor.task.hand.JstackStreamReadHandle;
import com.panda.sport.rcs.monitor.task.hand.TopAllThreadStreamReadHandle;
import com.panda.sport.rcs.monitor.task.hand.TopStreamReadHandle;
import com.panda.sport.rcs.monitor.utils.OsUtis;

@Service
public class CpuInfoServiceImpl implements CpuInfoService {
	
	Logger log = LoggerFactory.getLogger(CpuInfoServiceImpl.class);
	
	private static String pid = OsUtis.getPid();
	
	private static boolean IS_LINUX_OS  = OsUtis.isLinux();
	
	private static String linuxVersion = System.getProperty("os.version");
	
	private static Long LAST_SYS_TIME = 0l;
	
	ExecutorService execute = new ThreadPoolExecutor(4, 4, 120L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(),
            new ThreadFactoryBuilder()
            .setNameFormat("CPU-data-collection" + "-%d")
            .setDaemon(true)
            .setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
            {
                @Override
                public void uncaughtException(Thread t, Throwable e)
                {
                    log.error("Unexpected exception in thread: " + t, e);
                    Throwables.propagate(e);
                }
            })
            .build());
	
    @Override
    public SysInfoBean get() {
        return init();
    }

    private SysInfoBean init() {
    	if(!IS_LINUX_OS) {
    		return null;
    	}
    	SysInfoBean bean = new SysInfoBean();
    	bean.setUuid(UUID.randomUUID().toString().replace("-", ""));
    	putCPURateForLinux(bean);
        return bean;
    }
    
    /**
   	 * 获取Linux环境下JVM的内存占用率
   	 * 
   	 * @return
   	 */
   	public String getMemoryRateForLinux() {
   		Process pro = null;
   		Runtime r = Runtime.getRuntime();
   		String remCount = "";
   		try {
   			String command = "top -b  -n 1 -H -p" + pid;
   			pro = r.exec(command);
   			BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
   			in.readLine();
   			in.readLine();
   			in.readLine();
   			in.readLine();
   			in.readLine();
   			in.readLine();
   			in.readLine();
   			StringTokenizer ts = new StringTokenizer(in.readLine());
   			int i = 1;
   			while (ts.hasMoreTokens()) {
   				i++;
   				ts.nextToken();
   				if (i == 10) {
   					remCount = ts.nextToken();
   				}
   			}
   			in.close();
   			pro.destroy();
   		} catch (Exception e) {
   			log.error(e.getMessage());
   		}
   		return remCount;
   	}
   	
    
    /**
	 * 获取linux环境下JVM的cpu占用率
	 * 
	 * @return
	 */
	public void putCPURateForLinux(SysInfoBean bean) {
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(new String[] { "sh", "-c", "top -b -n 1 -p " + pid });
			process.getErrorStream().close();
			Future<SysInfoBean> future = execute.submit(new TopStreamReadHandle(process.getInputStream(),"INPUT",bean,pid));
			SysInfoBean result = future.get(5000, TimeUnit.SECONDS);
			if(result == null ) {
				log.warn("没有获取到SysInfoBean,bean:{}",JSONObject.toJSONString(bean));
				return;
			}
			
			//cpu大于200，需要导出堆栈日志查看数据,5分钟触发一次
			if(Double.parseDouble(result.getServiceBean().getCpu().trim()) > 200 
					&& System.currentTimeMillis() - LAST_SYS_TIME > 1000 * 60 * 5) {
				//处理top命令获取下面所有线程的cpu
				Process processTopList = null;
				try {
					processTopList = Runtime.getRuntime().exec(new String[] { "sh", "-c", "top -b -H -n 1 -p " + pid });
					processTopList.getErrorStream().close();
					future = execute.submit(new TopAllThreadStreamReadHandle(processTopList.getInputStream(),"INPUT",bean,pid));
					future.get(5000, TimeUnit.SECONDS);
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}finally {
					processTopList.destroy();
				}
				
				//导出堆栈日志
				Process processJstack = null;
				try {
					processJstack = Runtime.getRuntime().exec(new String[] { "sh", "-c", "jstack -l " + pid });
					processJstack.getErrorStream().close();
					future = execute.submit(new JstackStreamReadHandle(processJstack.getInputStream(),"INPUT",bean,pid));
					future.get(5000, TimeUnit.SECONDS);
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}finally {
					processJstack.destroy();
				}
				
				LAST_SYS_TIME = System.currentTimeMillis();
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		} finally {
			process.destroy();
		}
	}
	
	 private void freeResource(InputStream is, InputStreamReader isr, BufferedReader br) {
			try {
				if (is != null)
					is.close();
				if (isr != null)
					isr.close();
				if (br != null)
					br.close();
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
		}
	 
	 
}
