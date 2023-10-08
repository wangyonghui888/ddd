package com.panda.sport.rcs.service;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.service
 * @description :   分析用户订单是否存在对赌投注的情况
 * @date: 2020-06-28 10:05
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface IUserOrderHedgeAnalyzeService {

    /***
     *  分析从 timeStamp指定的时间戳开始,某个时间段的用户订单是否存在对赌投注情况
     * @param timeStamp
     * @return void
     * @Description //TODO
     * @Author dorich
     * @Date 10:12 2020/6/28
     **/
    void analyzeUserOrderHedge(long timeStamp);

    /****
     * 分析从 timeStamp指定的时间戳开始,某个时间段指定用户的订单是否存在对冲投注情况
     * @param uid                       指定用户的uid
     * @param timeStampBegin            开始时间戳
     * @param timeStampEnd              结束事件戳
     * @return void
     * @Description 
     * @Author dorich
     * @Date 13:58 2020/7/10
     **/
    void analyzeUserOrderHedge(long uid, long timeStampBegin, long timeStampEnd);
}
