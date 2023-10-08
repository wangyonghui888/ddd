package com.panda.sport.rcs.service.impl;

import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.common.vo.rule.*;
import com.panda.sport.rcs.customdb.mapper.RuleExtMapper;
import com.panda.sport.rcs.redis.service.RedisService;
import com.panda.sport.rcs.service.IRuleDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 规则逻辑实现 查询数据专用Service
 *
 * @author :  lithan
 * @date: 2020-06-30 11:02:39
 */
@Service
public class RuleDataServiceImpl implements IRuleDataService {

    @Autowired
    RedisService redisService;

    @Autowired
    RuleExtMapper ruleExtMapper;

    /**
     * 财务特征类规则 统一方法  计算投注金额 和盈利金额
     *
     * @param userId
     * @return
     */
    @Override
    public FinancialRuleVo getProfitAmount(Long userId, Long startTime, Long endTime) {
        FinancialRuleVo financialRuleVo = ruleExtMapper.getProfitAndRate(userId, startTime, endTime);
        return financialRuleVo;
    }


    @Override
    public Long getUserBetNum(Long userId, Long startTime, Long endTime) {
        return ruleExtMapper.getUserBetNum(userId, startTime, endTime);
    }

    @Override
    public Long getUserSuccessBetNum(Long userId, Long startTime, Long endTime) {
        return ruleExtMapper.getUserSuccessBetNum(userId, startTime, endTime);
    }


    @Override
    public UserBetAmountVo getUserBetAmount(Long userId, Long startTime, Long endTime) {
        //从数据库查
        UserBetAmountVo vo = ruleExtMapper.getUserBetAmount(userId, startTime, endTime);
        return vo;
    }

    /**
     * 查询危险投注笔数
     *
     * @param userId
     * @return
     */
    @Override
    public Long getUserDangerousBetNum(Long userId, Long startTime, Long endTime, Long dangerousId) {
        Long num = ruleExtMapper.getUserDangerousBetNum(userId, startTime, endTime, dangerousId);
        return num;
    }


    /**
     * 投注特征类	R23-1 R23-2 R23-3	投注内容-球类所占投注比例
     */
    @Override
    public SportRateVo getSportRate(Long userId, Long sportId) {
        //获取时间段
        Long startTime = LocalDateTimeUtil.getMilli(LocalDateTime.now().plusMonths(-3));
        Long endTime = LocalDateTimeUtil.getMilli(LocalDateTime.now());
        SportRateVo financialRuleVo = ruleExtMapper.getSportRate(userId, sportId, startTime, endTime);
        return financialRuleVo;
    }

    /**
     * 笔数
     * R24 投注内容-联赛比例 获取最高的 联赛投注 笔数
     *
     * @param userId
     */
    @Override
    public List<TournamentBetNumVo> getTournamentBetNum(Long userId, Long startTime, Long endTime) {
        List<TournamentBetNumVo> list = ruleExtMapper.getTournamentBetNum(userId, startTime, endTime);
        return list;
    }

    /**
     * 笔数
     * R16	投注赛种比例
     *
     * @param userId
     */
    @Override
    public List<SportBetNumVo> getSportBetNum(Long userId, Long startTime, Long endTime) {
        List<SportBetNumVo> list  = ruleExtMapper.getSportBetNum(userId, startTime, endTime);
        return list;
    }


    /**
     * R25 投注内容-球队次数
     *
     * @param userId
     */
    @Override
    public TeamTimesVo getTeamTimes(Long userId) {
        Long startTime = LocalDateTimeUtil.getMilli(LocalDateTime.now().plusMonths(-3));
        Long endTime = LocalDateTimeUtil.getMilli(LocalDateTime.now());
        TeamTimesVo teamTimesVo = ruleExtMapper.getTeamTimes(userId, startTime, endTime);
        return teamTimesVo;
    }

    /**
     * 访问特征类	R26	代理登录判断标准
     *
     * @param userId
     */
    @Override
    public CityNumVo getCityNum(Long userId, Long startTime, Long endTime) {
        CityNumVo cityNumVo = ruleExtMapper.getCityNum(userId, startTime, endTime);
        return cityNumVo;
    }


    /**
     * 访问特征类	R28	一机多登判断标准
     */
    @Override
    public List<IpNumVo> getIpNum(Long num, Long startTime, Long endTime) {
        List<IpNumVo> list = ruleExtMapper.getIpNum(num, startTime, endTime);
        return list;
    }

    /***
     * 根据指定参数,计算符合条件的大额投注笔数
     * @param vo
     * @return java.util.List<com.panda.sport.rcs.common.vo.rule.IpNumVo>
     * @Description
     * @Author dorich
     * @Date 10:19 2020/7/11
     **/
    @Override
    public BetAmountOrderVo getLargeAmountBetOrders(RuleParameterVo vo) {
        int days = Integer.parseInt(vo.getParameter1());
        Long endTimeStamp = LocalDateTimeUtil.getDayStartTime(vo.getTime());
        long beginTimeStamp = endTimeStamp - LocalDateTimeUtil.dayMill * days;
        long limit = Integer.parseInt(vo.getParameter2());
        /*** redis中不存在数据,则从mysql 中读取, 然后写入redis, 再返回, 第二个参数 乘 100是因为 入参单位是:元,但是数据库存放的单位是分.将元转换为对应的分 必须乘100 ***/
        BetAmountOrderVo betAmount = ruleExtMapper.getLargeAmountBetOrders(vo.getUserId(), limit * 100, beginTimeStamp, endTimeStamp);
//        redisService.set(uniqueKey, betAmount, 120);
        return betAmount;
    }

    /**
     * 满额投注笔数
     * @param userId
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public Long getFullBetNum(Long userId, Long startTime, Long endTime) {
        Long num = ruleExtMapper.getFullBetNum(userId, startTime, endTime);
        return num;
    }

    /**
     * 笔数
     * R22	投注特征类	R22	投注玩法比例
     * @param userId
     */
    @Override
    public List<PlayBetNumVo> getPlayBetNum(Long userId, Long startTime, Long endTime) {
        List<PlayBetNumVo> list  = ruleExtMapper.getPlayBetNum(userId, startTime, endTime);
        return list;
    }


    /**
     * 投注特征类	R23	用户单场盈利程度
     *
     * @param userId
     */
    @Override
    public List<MatchProfitNumVo> getMatchProfitNum(Long userId, Long startTime, Long endTime, Integer minLevel, Integer maxLevel) {
        List<MatchProfitNumVo> list = ruleExtMapper.getMatchProfitNum(userId, startTime, endTime, minLevel, maxLevel);
        return list;
    }

    /**
     * 笔数
     * 投注特征类	R24	投注场次
     * @param userId
     */
    @Override
    public Long getMatchBetNum(Long userId, Long startTime, Long endTime) {
        Long times  = ruleExtMapper.getMatchBetNumVo(userId, startTime, endTime);
        return times;
    }


}
