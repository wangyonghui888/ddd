package com.panda.sport.rcs.customdb.mapper;

import com.panda.sport.rcs.customdb.entity.BasketBallWinEntity;
import com.panda.sport.rcs.customdb.entity.StaticsItemEntity;
import com.panda.sport.rcs.customdb.entity.StaticsUserDateEntity;
import com.panda.sport.rcs.db.entity.TOrderDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 统计用户投注信息 Mapper 接口
 * </p>
 *                        
 * @author  
 * @since 2020-06-23
 */

public interface StaticsItemExtMapper {

    List<TOrderDetail> queryOrderByCondition(@Param("uid") long uid, @Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd);

    List<StaticsItemEntity> staticsBySportId(@Param("uid") long uid, @Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd);

    List<StaticsItemEntity> staticsByTournamentId(@Param("uid") long uid, @Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd);

    List<StaticsItemEntity> staticsByPlayId(@Param("uid") long uid, @Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd);

    List<StaticsItemEntity> staticsByTeamId(@Param("uid") long uid, @Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd);

    List<StaticsItemEntity> staticsByMarketType(@Param("uid") long uid, @Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd);

    List<StaticsItemEntity> staticsByOddsValue(@Param("uid") long uid, @Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd);

    List<StaticsItemEntity> staticsByBetAmount(@Param("uid") long uid, @Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd);

    List<StaticsItemEntity> staticsByMainMarket(@Param("uid") long uid, @Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd);

    List<StaticsItemEntity> staticsByHedge(@Param("uid") long uid, @Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd);

    List<StaticsUserDateEntity> fetchUserId(@Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd);

    List<Long> fetchBasketBallUserId(@Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd);

    Long fetchUserIdSportId(@Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd,@Param("userId") long userId);

    List<Long> getAllSportIdByUser(@Param("userId") long userId);




    List<Long> fetchBetTagUserId(@Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd,@Param("userId") long userId);

    Long getUserBetTag(@Param("userId") long userId);


    Long getUserUnactive(@Param("userId") long userId);


    Long getUserCreateTime(@Param("userId") long userId);



    Long getUserTagLastTime(@Param("userId") long userId);


    List<Long> getUserByTag(@Param("tagId") Integer tagId);


    BasketBallWinEntity getBasketBallWinNum(@Param("userId") long userId);







    List<StaticsItemEntity> fetchHedgeAnalyzeUserId(@Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd);


        
    /**
     * @Description   统计投注类型 （单关、串关、冠军玩法）
     * @Param [uid, timeBegin, timeEnd]
     * @Author toney
     * @Date  15:18 2021/1/9
     * @return java.util.List<com.panda.sport.rcs.customdb.entity.StaticsItemEntity>
     **/
    List<StaticsItemEntity> staticsByBetType(@Param("uid") long uid,@Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd);
    /**
     * @Description  投注阶段 （早盘、滚球）
     * @Param [uid, timeBegin, timeEnd]
     * @Author toney
     * @Date  12:08 2021/1/9
     * @return java.util.List<com.panda.sport.rcs.customdb.entity.StaticsItemEntity>
     **/
    List<StaticsItemEntity> staticsByBetStage(@Param("uid") long uid,@Param("timeBegin") long timeBegin, @Param("timeEnd") long timeEnd);


}
