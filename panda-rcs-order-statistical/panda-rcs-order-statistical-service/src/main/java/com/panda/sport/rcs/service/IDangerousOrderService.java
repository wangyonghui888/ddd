package com.panda.sport.rcs.service;

/**
 * 危险投注扫描 Service
 *
 * @author :  lithan
 * @date: 2020-07-10 14:43:37
 */
public interface IDangerousOrderService {

    /**
     * 危险投注扫描
     * @param time 当前时间 毫秒
     */
    public void execute(Long time);

    /**
     * 危险投注扫描 蛇单
     */
    public void executeSnake();
}
