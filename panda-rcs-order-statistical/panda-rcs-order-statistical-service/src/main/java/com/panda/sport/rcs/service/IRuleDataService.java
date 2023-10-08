package com.panda.sport.rcs.service;

import com.panda.sport.rcs.common.bean.RuleResult;
import com.panda.sport.rcs.common.vo.rule.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 规则逻辑实现 查询数据专用Service
 *
 * @author :  lithan
 * @date: 2020-06-28 09:50:05
 */
public interface IRuleDataService {


    /**
     * 财务特征类规则 统一方法  计算投注金额 和盈利金额
     *
     * @param userId
     * @return
     */
    public FinancialRuleVo getProfitAmount(Long userId, Long startTime, Long endTime);

    /**
     * 用户投注笔数 包括失败的
     *
     * @param userId
     * @return
     */
    public Long getUserBetNum(Long userId, Long startTime, Long endTime);

    /**
     * 用户投注笔数 只算成功的
     *
     * @param userId
     * @return
     */
    public Long getUserSuccessBetNum(Long userId, Long startTime, Long endTime);

    /**
     * 用户投注笔数
     *
     * @param userId
     * @return
     */
    public UserBetAmountVo getUserBetAmount(Long userId, Long startTime, Long endTime);

    /**
     * 查询用户危险投注笔数
     *
     * @param userId
     * @return
     */
    public Long getUserDangerousBetNum(Long userId, Long startTime, Long endTime, Long dangerousId);

    /**
     * 投注特征类	R23-1 R23-2 R23-3	投注内容-球类所占投注比例
     */
    public SportRateVo getSportRate(Long userId, Long sportId);

    /**
     * R16	投注赛种比例  获取时间段类 球类投注最多的 笔数
     */
    public List<SportBetNumVo> getSportBetNum(Long userId, Long startTime, Long endTime);

    /**
     * R17	投注联赛比例
     */
    public List<TournamentBetNumVo> getTournamentBetNum(Long userId, Long startTime, Long endTime);


    /**
     * R25 投注内容-球队次数
     */
    public TeamTimesVo getTeamTimes(Long userId);

    /**
     * 访问特征类	R5	代理登录判断标准
     */
    public CityNumVo getCityNum(Long userId, Long startTime, Long endTime);


    /**
     * 投注特征类	R20  大额投注笔数
     */
    public List<IpNumVo> getIpNum(Long num, Long startTime, Long endTime);

    /***
     * 根据指定参数,计算符合条件的大额投注笔数
     * @param vo
     * @return java.util.List<com.panda.sport.rcs.common.vo.rule.IpNumVo>
     * @Description
     * @Author dorich
     * @Date 10:19 2020/7/11
     **/
    BetAmountOrderVo getLargeAmountBetOrders(RuleParameterVo vo);

    /**
     * 满额投注笔数
     * @param userId
     * @param startTime
     * @param endTime
     * @return
     */
    Long getFullBetNum(Long userId, Long startTime, Long endTime);


    /**
     * R22	投注玩法比例
     */
    public List<PlayBetNumVo> getPlayBetNum(Long userId, Long startTime, Long endTime);


    /**
     * 投注特征类	R23	用户单场盈利程度
     */
    public List<MatchProfitNumVo> getMatchProfitNum(Long userId, Long startTime, Long endTime, Integer minLevel, Integer maxLevel);
    /**
     * R24	投注场次
     */
    public Long getMatchBetNum(Long userId, Long startTime, Long endTime);
}

