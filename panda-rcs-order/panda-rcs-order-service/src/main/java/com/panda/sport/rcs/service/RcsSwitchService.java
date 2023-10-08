package com.panda.sport.rcs.service;

import com.panda.sport.rcs.vo.HttpResponse;

/**
 * @author wiker
 * @date 2023/8/20 16:37
 **/
public interface RcsSwitchService {
    /**
     * @Description   //分页查询
     * @Param [current, size]
     * @Author  tim
     * @Date   2023/8/2
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.panda.sport.rcs.pojo.RcsOmitConfig>
     **/
    HttpResponse<?> editSwitch(Integer status);
    String getMissOrderSwitchStatus();
}
