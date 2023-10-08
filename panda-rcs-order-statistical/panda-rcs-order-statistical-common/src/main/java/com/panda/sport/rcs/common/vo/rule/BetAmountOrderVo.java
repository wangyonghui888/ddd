package com.panda.sport.rcs.common.vo.rule;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.common.vo.rule
 * @description :   注单下注额统计结果vo
 * @date: 2020-07-11 10:26
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class BetAmountOrderVo {

    /*** 用户id ***/
    Long  uid;

    /*** 统计数据所在日期0时0分0秒的时间戳.单位:毫秒; 末尾 000 ***/
    Long  days;

    /*** 下注额阈值, ***/
    Long  betAmountLimit;

    /*** 注单个数(下注额大于betAmountLimit的注单个数) ***/
    Long  betNumbers;

    /*** 所有注单个数 ***/
    Long  betNumbersAll;

    public BetAmountOrderVo(Long uid) {
        this.uid = uid;
    }

    public BetAmountOrderVo() {
    }

    public BetAmountOrderVo(Long uid, Long days, Long betAmountLimit, Long betNumbers) {
        this.uid = uid;
        this.days = days;
        this.betAmountLimit = betAmountLimit;
        this.betNumbers = betNumbers;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getDays() {
        return days;
    }

    public void setDays(Long days) {
        this.days = days;
    }

    public Long getBetAmountLimit() {
        return betAmountLimit;
    }

    public void setBetAmountLimit(Long betAmountLimit) {
        this.betAmountLimit = betAmountLimit;
    }

    public Long getBetNumbers() {
        return betNumbers;
    }

    public void setBetNumbers(Long betNumbers) {
        this.betNumbers = betNumbers;
    }

    public Long getBetNumbersAll() {
        return betNumbersAll;
    }

    public void setBetNumbersAll(Long betNumbersAll) {
        this.betNumbersAll = betNumbersAll;
    }
}
