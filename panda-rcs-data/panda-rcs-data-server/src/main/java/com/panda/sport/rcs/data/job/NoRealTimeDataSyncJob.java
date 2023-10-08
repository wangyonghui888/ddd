package com.panda.sport.rcs.data.job;

import com.panda.sport.rcs.data.sync.StandardTxThirdMarketOddsConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @ClassName TestNoRealTimeDataSyncJob
 * @Description: 静态数据定时器
 * @Author Vector
 * @Date 2019/9/28
 **/
@Slf4j
@Component
class NoRealTimeDataSyncJob {
    @Autowired
    private StandardTxThirdMarketOddsConsumer standardTxThirdMarketOddsConsumer;

    /**
     * 百家赔过期检测
     *
     * @Date: 2021/1/1
     */
    @Scheduled(fixedDelay = 20 * 1000)
    public void multiOddsExpiredData() {
        try {
            standardTxThirdMarketOddsConsumer.multiOddsExpiredData(null);
        } catch (Exception e) {
            log.error("百家赔过期检测异常:", e);
        }
    }

}
