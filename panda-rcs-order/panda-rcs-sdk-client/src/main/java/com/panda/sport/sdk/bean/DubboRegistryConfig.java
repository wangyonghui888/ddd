package com.panda.sport.sdk.bean;

import com.alibaba.dubbo.config.RegistryConfig;
import com.panda.sport.sdk.annotation.AutoInsert;

@AutoInsert(prefix = "sdk.dubbo.registry")
public class DubboRegistryConfig extends RegistryConfig{

}
