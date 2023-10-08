package com.panda.rcs.cleanup.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.rcs.cleanup.entity.RcsUserConfig;
import com.panda.rcs.cleanup.entity.RcsUserConfigNew;

import java.util.List;

/**
 * user-config 优化配置
 *
 * @description:
 * @author: magic
 * @create: 2022-07-18 15:15
 **/
public interface IRcsUserConfigNewService extends IService<RcsUserConfigNew> {

    void convertOldDataTask();

//    void verifySportIds();
}