package com.panda.sport.rcs.customdb.service;

import com.panda.sport.rcs.customdb.entity.StaticsItemEntity;
import com.panda.sport.rcs.customdb.entity.StaticsUserDateEntity;
import com.panda.sport.rcs.db.entity.TOrderDetail;

import java.util.List;

/**
 * <p>
 * 统计用户投注信息 服务类
 * </p>
 *
 * @author
 * @since 2020-06-23
 */
public interface StaticsItemService {

    /***
     * 按照运动种类种类统计某个用户的订单信息
     * @param uid             用户uid
     * @param timeBegin       开始时间的时间戳,单位:ms
     * @param timeEnd         结束时间的时间戳,单位:ms
     * @return java.util.List<com.panda.sport.rcs.customdb.entity.StaticsItemEntity>
     * @Description
     * @Author dorich
     * @Date 16:08 2020/6/23
     **/
    List<StaticsItemEntity> staticsBySportId(long uid, long timeBegin, long timeEnd);

    List<TOrderDetail> queryOrderByCondition(long uid, long timeBegin, long timeEnd);

    List<StaticsItemEntity> staticsByTournamentId(long uid, long timeBegin, long timeEnd);

    List<StaticsItemEntity> staticsByPlayId(long uid, long timeBegin, long timeEnd);

    List<StaticsItemEntity> staticsByTeamId(long uid, long timeBegin, long timeEnd);

    List<StaticsItemEntity> staticsByMarketType(long uid, long timeBegin, long timeEnd);

    List<StaticsItemEntity> staticsByOddsValue(long uid, long timeBegin, long timeEnd);

    List<StaticsItemEntity> staticsByBetAmount(long uid, long timeBegin, long timeEnd);

    List<StaticsItemEntity> staticsByMainMarket(long uid, long timeBegin, long timeEnd);

    List<StaticsItemEntity> staticsByHedge(long uid, long timeBegin, long timeEnd);

    List<StaticsUserDateEntity> fetchUserId(long timeBegin, long timeEnd);


    List<Long> fetchBasketBallUserId(long timeBegin, long timeEnd);



    List<StaticsItemEntity> fetchHedgeAnalyzeUserId(long timeBegin, long timeEnd);

    /**
     * @Description   投注类型
     * @Param [uid, timeBegin, timeEnd]
     * @Author toney
     * @Date  11:57 2021/1/9
     * @return java.util.List<com.panda.sport.rcs.customdb.entity.StaticsItemEntity>
     **/
    List<StaticsItemEntity> staticsByBetType(Long uid, Long timeBegin, Long timeEnd);

    /**
     * @Description   投注阶段 （早盘、滚球）
     * @Param [uid, timeBegin, timeEnd]
     * @Author toney
     * @Date  15:18 2021/1/9
     * @return java.util.List<com.panda.sport.rcs.customdb.entity.StaticsItemEntity>
     **/
    List<StaticsItemEntity> staticsByBetStage(Long uid, Long timeBegin, Long timeEnd);

}
