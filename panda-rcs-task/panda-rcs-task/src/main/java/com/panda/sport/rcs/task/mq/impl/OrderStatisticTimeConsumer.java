package com.panda.sport.rcs.task.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
import com.panda.sport.rcs.pojo.report.CalcSettleItem;
import com.panda.sport.rcs.task.wrapper.OrderStatisticTimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author :  myname
 * @Project Name :  订单报表出来
 * @Package Name :  com.panda.sport.rcs.task.mq.impl
 * @Description :  TODO
 * @Date: 2019-12-24 16:10
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
public class OrderStatisticTimeConsumer extends ConsumerAdapter<List<CalcSettleItem>> {
    /**
     * @Description //线程数
     **/
    private final static int ThreadNum = 10;

    private static ThreadPoolExecutor pool = new ThreadPoolExecutor(ThreadNum, ThreadNum, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    private static AtomicInteger count = new AtomicInteger();
    @Autowired
    private OrderStatisticTimeService orderStatisticTimeService;
    public OrderStatisticTimeConsumer(){
        super(MqConstants.UNCALC_SETTLE_BATCH_TOPIC + "," +  MqConstants.SETTLE_STATISTIC_TAG,"UNCALC_SETTLE_BATCH_TOPIC" );
    }
    /**
     * @return java.lang.Boolean
     * @Description //接受消息进行处理
     * @Param [msg, paramsMap]
     * @Author kimi
     * @Date 2019/12/24
     **/
    @Override
    public Boolean handleMs(List<CalcSettleItem> calcSettleItemList, Map<String, String> paramsMap) {
        log.info("报表统计MQ消息处理类任务开始{}",calcSettleItemList.size());
        if (CollectionUtils.isEmpty(calcSettleItemList)) {
            log.error("没有收到订单结算数据");
            return Boolean.TRUE;
        }
        try {

        	while(calcSettleItemList != null && calcSettleItemList.size() > 0) {
        		if(count.get() > ThreadNum) {
                    Thread.currentThread().sleep(1000L);
        			continue;
        		}
        		count.incrementAndGet();
        		pool.execute(new Runnable() {

					@Override
					public void run() {
						CalcSettleItem item = null;
						try {
						    synchronized (calcSettleItemList){
						        if(calcSettleItemList.size() <= 0) return;
                                item = calcSettleItemList.remove(0);
                            }

		                    orderStatisticTimeService.orderStatisticTimeDealwith(item);
		                }catch (Exception l) {
		                	log.error("处理数据异常：{} ,error:{}",JSONObject.toJSONString(item),l.getMessage());
		                    log.error("报表多线程处理结算出错" + l.getMessage(), l);
		                }finally {
		                	count.decrementAndGet();
						}
					}
				});
        	}
            log.info("报表统计MQ消息处理类任务结束");
        }catch (Exception e) {
            log.error("报表处理结算出问题：" + e.getMessage(), e);

        }
        return Boolean.TRUE;
    }
}
