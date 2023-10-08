package com.panda.sport.rcs.customdb.service.impl;

import com.panda.sport.rcs.customdb.entity.StaticsItemEntity;
import com.panda.sport.rcs.customdb.entity.StaticsUserDateEntity;
import com.panda.sport.rcs.customdb.mapper.StaticsItemExtMapper;
import com.panda.sport.rcs.customdb.service.StaticsItemService;
import com.panda.sport.rcs.db.entity.TOrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 统计用户投注信息 服务类
 * </p>
 *
 * @author
 * @since 2020-06-23
 */
@Service("staticsItemService")
public class StaticsItemServiceImpl implements StaticsItemService {

    @Autowired
    private StaticsItemExtMapper staticsItemExtMapper;

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
    @Override
    public List<StaticsItemEntity> staticsBySportId(long uid, long timeBegin, long timeEnd) {
        return staticsItemExtMapper.staticsBySportId(uid, timeBegin, timeEnd);
    }

    @Override
    public List<TOrderDetail> queryOrderByCondition(long uid, long timeBegin, long timeEnd) {
        return staticsItemExtMapper.queryOrderByCondition(uid, timeBegin, timeEnd);
    } 

    @Override
    public List<StaticsItemEntity> staticsByTournamentId(long uid, long timeBegin, long timeEnd) {
        return staticsItemExtMapper.staticsByTournamentId(uid, timeBegin, timeEnd);
    }

    @Override
    public List<StaticsItemEntity> staticsByPlayId(long uid, long timeBegin, long timeEnd) {
        return staticsItemExtMapper.staticsByPlayId(uid, timeBegin, timeEnd);
    }

    @Override
    public List<StaticsItemEntity> staticsByTeamId(long uid, long timeBegin, long timeEnd) {
        return staticsItemExtMapper.staticsByTeamId(uid, timeBegin, timeEnd);
    }

    @Override
    public List<StaticsItemEntity> staticsByMarketType(long uid, long timeBegin, long timeEnd) {
        return staticsItemExtMapper.staticsByMarketType(uid, timeBegin, timeEnd);
    }

    @Override
    public List<StaticsItemEntity> staticsByOddsValue(long uid, long timeBegin, long timeEnd) {
        return staticsItemExtMapper.staticsByOddsValue(uid, timeBegin, timeEnd);
    }

    @Override
    public List<StaticsItemEntity> staticsByBetAmount(long uid, long timeBegin, long timeEnd) {
        return staticsItemExtMapper.staticsByBetAmount(uid, timeBegin, timeEnd);
    }

    @Override
    public List<StaticsItemEntity> staticsByMainMarket(long uid, long timeBegin, long timeEnd) {
        List<StaticsItemEntity> list = staticsItemExtMapper.staticsByMainMarket(uid, timeBegin, timeEnd);
        for (StaticsItemEntity staticsItemEntity : list) {
            staticsItemEntity.setChildType(staticsItemEntity.getChildType().equals("0") ? "1" : "2");
        }
        return list;
    }

    @Override
    public List<StaticsItemEntity> staticsByHedge(long uid, long timeBegin, long timeEnd) {
        return staticsItemExtMapper.staticsByHedge(uid, timeBegin, timeEnd);
    }

    @Override
    public List<StaticsUserDateEntity> fetchUserId(long timeBegin, long timeEnd) {
        return staticsItemExtMapper.fetchUserId(timeBegin, timeEnd);
    }

    @Override
    public List<Long> fetchBasketBallUserId(long timeBegin, long timeEnd) {
        return staticsItemExtMapper.fetchBasketBallUserId(timeBegin, timeEnd);
    }



    @Override
    public List<StaticsItemEntity> fetchHedgeAnalyzeUserId(long timeBegin, long timeEnd) {
        return staticsItemExtMapper.fetchHedgeAnalyzeUserId(timeBegin, timeEnd);
    }


    @Override
    public List<StaticsItemEntity> staticsByBetType(Long uid, Long timeBegin, Long timeEnd) {
        return staticsItemExtMapper.staticsByBetType(uid, timeBegin, timeEnd);
    }
    /**
     * @Description  投注阶段 （早盘、滚球）
     * @Param [uid, timeBegin, timeEnd]
     * @Author toney
     * @Date  12:08 2021/1/9
     * @return java.util.List<com.panda.sport.rcs.customdb.entity.StaticsItemEntity>
     **/
    @Override
    public List<StaticsItemEntity> staticsByBetStage(Long uid, Long timeBegin, Long timeEnd) {
        return staticsItemExtMapper.staticsByBetStage(uid, timeBegin, timeEnd);
    }
}
