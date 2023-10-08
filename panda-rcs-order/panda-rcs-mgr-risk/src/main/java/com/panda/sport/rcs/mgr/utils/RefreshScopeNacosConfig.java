package com.panda.sport.rcs.mgr.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@RefreshScope
public class RefreshScopeNacosConfig {
    // 设备货量
    @Value("${device.volume.percentage.pc:1}")
    private String deviceVolumePc;

    @Value("${device.volume.percentage.h5:0.6}")
    private String deviceVolumeH5;

    @Value("${device.volume.percentage.app:0.3}")
    private String deviceVolumeApp;
}
