package com.panda.sport.rcs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsUserConfig;
import com.panda.sport.rcs.pojo.RcsUserConfigNew;

import java.util.List;

/**
 * user-config 优化配置
 *
 * @description:
 * @author: magic
 * @create: 2022-07-09 10:15
 **/
public interface IRcsUserConfigNewService extends IService<RcsUserConfigNew> {

    List<RcsUserConfig> getByUserId(long userId);

}