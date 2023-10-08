package com.panda.sport.rcs.service;

/**
 * 篮球标签扫描 Service
 *
 * @author :  lithan
 * @date: 2021-12-06
 */
public interface IBastetBallService {

    /**
     *
     *
     * @return
     */
   public void execute(Long time);


    /**
     * 专注/部分篮球赌客转ufo-$或好脚
     * @param time
     */
    void executeBasetToOther(Long time);

}
