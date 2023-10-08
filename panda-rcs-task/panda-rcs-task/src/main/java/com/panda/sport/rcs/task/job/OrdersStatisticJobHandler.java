package com.panda.sport.rcs.task.job;

import com.google.common.base.Strings;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.report.CalcSettleItem;
import com.panda.sport.rcs.task.config.RedissonManager;
import com.panda.sport.rcs.task.wrapper.order.ITSettleService;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.task.wrapper.OrderStatisticTimeService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.task.job
 * @Description :  TODO
 * @Date: 2019-10-26 15:40
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@JobHandler(value = "ordersStatisticJobHandler")
@Component
@Slf4j
//@RefreshScope
public class OrdersStatisticJobHandler extends IJobHandler {

    @Autowired
    private ITSettleService settleService;

    @Autowired
    ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    private OrderStatisticTimeService orderStatisticTimeService;

    @Autowired
    RedisClient redisClient;

    @Autowired
    private RedissonManager redissonManager;

    //递增幅度
    public static int INCRMINS =  24*60;

    private static Integer start = 0;

    private static Integer size = 10000;

    @Override
    public ReturnT<String> execute(String params) throws Exception {
    	String linkId = "ordersStatisticJobHandler";
    	CommonUtils.mdcPut(linkId);
        //格式 2019-10-22
        String startDate = params;
        log.info("::{}::-OrdersStatisticJobHandler---------------->>执行开始,startDate:{}", linkId,startDate);
        log.info("::{}::-OrdersStatisticJobHandler...执行开始,startDate:{}", linkId,startDate);
        //重新初始化任务数据
        Date currentTime = Calendar.getInstance().getTime();
        String lock = String.format("RCS:LOCK:SETTLE_WITH_DATE_%s", DateUtils.DateToString(currentTime));
        String globalLock = "RCS:LOCK:SETTLE:INITLOCK";
        String timeKey = "RCS:KEY:SCHEDULER:STARTTIME";
        Long endTime = getStartDate(currentTime)[1];
        if(!Strings.isNullOrEmpty(params)){
            //初始化数据
            try {
                //防止分布式任务互相串数据
                redisClient.set(globalLock,1);
                Date dt = DateUtils.strToDate(startDate,FastDateFormat.getInstance("yyyy-MM-dd"));
                Date dt1 = DateUtils.addNHour(dt,12);
                initData(dt1.getTime(),endTime);
                //手动重新统计，重置开始时间
                Date dt2 = DateUtils.addNMinute(dt1, -INCRMINS);
                redisClient.set(timeKey,dt2.getTime());

            } catch (Exception e) {
            	log.error("::{}::-任务输入日期格式不对，放弃任务,params:{}", linkId, params,e);
            }finally {
                redisClient.set(globalLock,0);
            }
            return SUCCESS;
        }

        Object cachedLock = redisClient.getObj(globalLock,Integer.class);
        if(cachedLock!=null && ((Integer)cachedLock) == 1){
        	log.error("::{}::-全局任务还在运行，不运行同时RUN 报表计划", linkId);
            return SUCCESS;
        }
        //默认统计昨天的数据,手动设置日期后，从设置日期开始统计
        try {
            redissonManager.lock(lock);
            Long startTime = (Long)redisClient.getObj(timeKey, Long.class);
            if(startTime == null){
                //当日第一次执行
                startTime = getStartDate(currentTime)[0];
                redisClient.set(timeKey,startTime);
            }else {
                //按INCRMINS分钟递增
                startTime = DateUtils.addMinutes(startTime,INCRMINS);
                redisClient.set(timeKey,startTime);
            }
            redissonManager.unlock(lock);

            log.info("::{}::-查询报表数据,stardDate：{},endDate:{} ", linkId,DateUtils.parseDate(startTime,"yyyy-MM-dd HH:mm:ss"),
                    DateUtils.parseDate(endTime,"yyyy-MM-dd HH:mm:ss"));
            if(startTime >= endTime){
            	log.info("::{}::-任务已执行到当前日的12点整，不可重复执行", linkId, DateUtils.DateToString(currentTime));
                //按INCRMINS分钟回退
                startTime = DateUtils.addMinutes(startTime,-INCRMINS);
                redisClient.set(timeKey,startTime);
                return SUCCESS;
            }
            Long count = settleService.getCountCustomizedOrder(startTime,DateUtils.addMinutes(startTime,INCRMINS));
            Long pages = (long)Math.ceil(BigDecimal.valueOf(count).divide(BigDecimal.valueOf(size)).doubleValue());
            if(pages > 0){
                for(int page = start ; page < pages; page++){
                    List<CalcSettleItem> items = settleService.getCustomizedOrderList(startTime,DateUtils.addMinutes(startTime,INCRMINS),page*size,size);
                    sendMsgToMQ(items,page);
                }
            }
            log.info("::{}::-查询报表数据 ,pages:{},count :{}", linkId,pages,count);

            log.info("::{}::-OrdersStatisticJobHandler---------------->>执行结束", linkId);
        } catch (Exception e  ) {
        	log.info("::{}::-执行报表统计任务失败", linkId, e);
        }finally{
            redissonManager.unlock(lock);
        }
        return SUCCESS;
    }

    private void initData(Long startTime,Long endTime){
    	String linkId = CommonUtils.getLinkIdByMdc();
        try {
            orderStatisticTimeService.initDataByDate(startTime,endTime);
        } catch (Exception e) {
            log.error("::{}::-初始化报表数据库失败", linkId,e );
        }
    }

    private void sendMsgToMQ(List<CalcSettleItem> items,Integer page){
    	String linkId = CommonUtils.getLinkIdByMdc();
        if(items.size() > 0){
            Long key = items.get(0).getBetNo();
            producerSendMessageUtils.sendMessage(MqConstants.UNCALC_SETTLE_BATCH_TOPIC, MqConstants.SETTLE_STATISTIC_TAG, key+"", items);
            log.info("::{}::-查询报表数据, 数据key：{} ,当前页：{}", linkId,key,page);
        }
    }

    private Long[] getStartDate(Date currentTime){
    	String linkId = CommonUtils.getLinkIdByMdc();
        Long startTime = 0l;
        Long endTime = 0l;
        //统计昨天
        if(DateUtils.getHourByDate(currentTime) < 12 ){
            log.info("::{}::-执行任务时间为12点前，统计前天的数据，当前时间 ： {}", linkId, currentTime);
            startTime = DateUtils.getTwelveOfDiffDay(-2);
            endTime = DateUtils.getTwelveOfDiffDay(-1);
        }else{
            log.info("::{}::-执行任务时间为12点后，统计昨天的数据，当前时间 ： {},", linkId, currentTime);
            startTime = DateUtils.getTwelveOfDiffDay(-1);
            endTime = DateUtils.getTwelveOfDiffDay(0);
        }
        return new Long[]{startTime,endTime};
    }

}
