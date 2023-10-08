package com.panda.sport.rcs.customdb.mapper;

import com.panda.sport.rcs.common.vo.DangerousVo;
import com.panda.sport.rcs.common.vo.api.request.UserBehaviorReqVo;
import com.panda.sport.rcs.common.vo.api.response.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户行为详情-投注偏好/财务特征-日统计表 Mapper 接口
 * </p>
 *
 * @author lithan auto
 * @since 2020-06-21
 */
public interface UserSpecialStatisExtMapper {
    /**
     * 投注偏好详情 -  根据球类分组 获取  投注金额 笔均投注金额 盈利金额 盈利率 盈利注单比
     *
     * @return
     */
    public List<ListBySportResVo> getListBySport(@Param("param") UserBehaviorReqVo vo);

    /**
     * 投注偏好详情 -  根据联赛id分组 获取  投注金额 笔均投注金额 盈利金额 盈利率 盈利注单比
     *
     * @return
     */
    public List<ListByTournamentResVo> getListByTournament(@Param("param") UserBehaviorReqVo vo);

    /**
     * @Description 投注偏好详情 - 投注类型
     * @Param [vo]
     * @Author  myname
     * @Date  12:27 2021/1/12
     * @return java.util.List<com.panda.sport.rcs.common.vo.api.response.ListByBetTypeResVo>
     **/
    List<ListByBetTypeResVo> getListByBetType(@Param("param") UserBehaviorReqVo vo);
    /**
     * @Description   投注偏好详情 - 投注阶段
     * @Param [vo]
     * @Author  myname
     * @Date  13:36 2021/1/12
     * @return java.util.List<com.panda.sport.rcs.common.vo.api.response.ListByBetStageResVo>
     **/
    List<ListByBetStageResVo> getListByBetStage(@Param("param") UserBehaviorReqVo vo);

    /**
     * 投注偏好详情 -  根据玩法id和sportId分组 获取  投注金额 笔均投注金额 盈利金额 盈利率 盈利注单比
     *
     * @return
     */
    public List<ListByPlayResVo> getListByPlay(@Param("param") UserBehaviorReqVo vo);

    /**
     * 投注偏好详情 -  根据球队id分组 获取  投注金额 笔均投注金额 盈利金额 盈利率 盈利注单比
     *
     * @return
     */
    public List<ListByTeamResVo> getListByTeam(@Param("param") UserBehaviorReqVo vo);

    /**
     * 投注偏好详情 -  根据market_type分组 获取  投注金额
     *
     * @return
     */
    public List<ListByMarketResVo> getListByMarket(@Param("param") UserBehaviorReqVo vo);

    /**
     * 投注偏好详情 -  根据赔率区间分组 获取  投注金额
     *
     * @return
     */
    public List<ListByOddsResVo> getListByOdds(@Param("param") UserBehaviorReqVo vo);

    /**
     * 投注偏好详情 -  根据投注金额区间分组 获取  投注金额
     *
     * @return
     */
    public List<ListByBetScopeResVo> getListByBetScope(@Param("param") UserBehaviorReqVo vo);

    /**
     * 投注偏好详情 -  根据正副盘 获取  投注金额
     *
     * @return
     */
    public List<ListByMainResVo> getListByMain(@Param("param") UserBehaviorReqVo vo);

    /**
     * 投注偏好详情 -  根据正副盘 获取  投注金额
     *
     * @return
     */
    public List<ListByOppositeResVo> getListByOpposite(@Param("param") UserBehaviorReqVo vo);

    /**
     * 危险投注行为
     *
     * @return
     */
    public List<UserDangerousOrderResVo> getDangerousList(@Param("param") DangerousVo vo);

    /**
     * 访问特征详情
     * @return
     */
    //public List<AccessListResVo> getAccessList(@Param("param") UserBehaviorReqVo vo);
}
