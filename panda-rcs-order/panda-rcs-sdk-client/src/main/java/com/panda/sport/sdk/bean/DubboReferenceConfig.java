package com.panda.sport.sdk.bean;

import com.alibaba.dubbo.config.ReferenceConfig;
import com.panda.sport.sdk.annotation.AutoInsert;

@AutoInsert(prefix = "sdk.dubbo.reference" ,isAuto = false)
public class DubboReferenceConfig extends ReferenceConfig{
	
}
