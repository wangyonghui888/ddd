package com.panda.sport.rcs.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@RefreshScope
public class UserProfileUserTagChangeNacosConfig {

    @Value("${statistical.observe.ch.name:观察名单}")
    String ch_observeName;
    @Value("${statistical.exception.ch.user:异常会员}")
    String ch_exceptionUser;

    @Value("${statistical.observe.en.name:observed}")
    String en_observeName;
    @Value("${statistical.exception.en.user:abnormal}")
    String en_exceptionUser;

    @Value("${statistical.observe.array:3,4,5}")
    String observeArray;
    @Value("${statistical.exception.array:6,7,8}")
    String exceptionArray;

    @Value("${statistical.setting.time:1662969600000}")
    String dateSetting;
}
