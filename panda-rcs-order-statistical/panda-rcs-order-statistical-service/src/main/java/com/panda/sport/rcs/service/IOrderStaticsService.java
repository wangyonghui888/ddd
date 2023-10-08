package com.panda.sport.rcs.service;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.service
 * @description :  用户订单统计服务
 * @date: 2020-06-23 13:18
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface IOrderStaticsService {

    /***
     * 统计某个用户昨天的订单信息
     * @param uid
     * @return void
     * @Description 统计
     * @Author dorich
     * @Date 13:55 2020/6/23
     **/
    void staticsUserOrder(long uid);

    /***
     * 统计某个用户指定时间戳范围内的订单信息(时间范围: 当前时间戳对应日期的前一天)
     * @param uid
     * @param timeStamp
     * @return void
     * @Description //TODO
     * @Author dorich
     * @Date 10:19 2020/7/10
     **/
    void staticsUserOrder(long uid, Long timeStampBegin,Long timeStampEnd);

    /***
     * 统计指定时间所在前一天的数据
     * @param timeStamp
     * @return void
     * @Description  
     * @Author dorich
     * @Date 9:05 2020/7/1
     **/
    void staticsOrderForUsers(long timeStamp,long endStamp);

}
