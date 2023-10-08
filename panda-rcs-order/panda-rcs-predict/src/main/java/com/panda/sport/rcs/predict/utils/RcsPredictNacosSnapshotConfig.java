package com.panda.sport.rcs.predict.utils;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;


/**
 * forecast 快照 配置开关
 */
@Configuration
@Data
@RefreshScope
public class RcsPredictNacosSnapshotConfig {

    @Value("${forecast.snapshot.off:true}")
    public boolean forecastSnapshotOff;

    /**
     * 货量早盘数据过期是否从数据库加载
     */
    @Value("${forecast.expiry.off:false}")
    public boolean forecastExpiryOff;


}
