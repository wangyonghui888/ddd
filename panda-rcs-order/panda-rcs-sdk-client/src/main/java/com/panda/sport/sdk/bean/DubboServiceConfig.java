package com.panda.sport.sdk.bean;

import com.alibaba.dubbo.config.ServiceConfig;
import com.panda.sport.sdk.annotation.AutoInsert;

/**
 * @author lithan
 * @description
 * @date 2020/1/28 17:21
 */
@AutoInsert(prefix = "sdk.dubbo.application")
public class DubboServiceConfig extends ServiceConfig {
}
