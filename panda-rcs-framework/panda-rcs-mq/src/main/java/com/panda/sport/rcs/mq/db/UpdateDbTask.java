package com.panda.sport.rcs.mq.db;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UpdateDbTask {
	
    //循环任务是否开启
    private static boolean isStart = false;
	
	private static Long currentTime = System.currentTimeMillis();
	
	private static ThreadPoolExecutor pool = new ThreadPoolExecutor(20, 40, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10000));
	
	@SuppressWarnings("unchecked")
	private static void execute() {
        if (DataCache.getSize() <= 0) {
            return;
        }
        log.info("缓存数据开始执行，当前剩余数量：{}", DataCache.getSize());
        Map<String, Object> dataMap = DataCache.getAllCache();
        for (Iterator<Map.Entry<String, Object>> it = dataMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Object> item = it.next();
            pool.execute(() -> {
            	String key = item.getKey();
            	Object val = item.getValue();
            	UpdateDataApi api = DataCache.getApi(key);
            	if(api == null ) {
            		log.warn("当前缓存数据没有获取到处理类，请检查配置是否正确：{}",JSONObject.toJSONString(item));
            		return ;
            	}
                log.info("缓存数据开始执行， 开始执行：{},key:{},value:{},api:{}", dataMap.size(), item.getKey(),val,api);
                boolean isSuccess = api.updateData(val);
                log.info("缓存数据开始执行， 结束执行：{},key:{},value:{},执行结果：{}", dataMap.size(), item.getKey(),val,isSuccess);
            });
            it.remove();
        }
    }

	public static void startUpdateThread() {
        if (isStart) {
            return;
        }
        isStart = true;
        log.info("启动数据库延迟缓存更新定时任务。。。");

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (System.currentTimeMillis() - currentTime > 500L) {
                            currentTime = System.currentTimeMillis();//放在前面赋值是因为怕执行时间过长
                            execute();
                        } else if (DataCache.getSize() >= 100) {
                            currentTime = System.currentTimeMillis();//放在前面赋值是因为怕执行时间过长
                            execute();
                        }
                        Thread.currentThread().sleep(300L);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }).start();
    }
}
