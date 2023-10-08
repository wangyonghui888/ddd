package com.panda.sport.rcs.service;

import com.panda.sport.rcs.common.vo.WeekDaylVo;
import com.panda.sport.rcs.common.vo.api.request.IpTagSetReqVo;
import com.panda.sport.rcs.common.vo.api.request.UserBehaviorReqVo;
import com.panda.sport.rcs.common.vo.api.response.*;
import com.panda.sport.rcs.db.entity.RiskUserVisitIpTag;

import java.util.List;

public interface IUserSpecialService {

    /**
     * 根据球类统计
     *
     * @param vo
     * @return
     */
    public List<ListBySportResVo> getListBySport(UserBehaviorReqVo vo);


    /**
     * @Description   根据投注类型统计
     * @Param [vo]
     * @Author  myname
     * @Date  11:54 2021/1/12
     * @return java.util.List<com.panda.sport.rcs.common.vo.api.response.ListBySportResVo>
     **/
    public List<ListByBetTypeResVo> getListByBetType(UserBehaviorReqVo vo);


    /**
     * @Description   根据投注阶段统计
     * @Param [vo]
     * @Author  myname
     * @Date  11:54 2021/1/12
     * @return java.util.List<com.panda.sport.rcs.common.vo.api.response.ListBySportResVo>
     **/
    public List<ListByBetStageResVo> getListByBetStage(UserBehaviorReqVo vo);

    /**
     * 根据联赛统计
     *
     * @param vo
     * @return
     */
    public List<ListByTournamentResVo> getListByTournament(UserBehaviorReqVo vo);

    /**
     * 根据玩法统计
     *
     * @param vo
     * @return
     */
    public List<ListByPlayResVo> getListByPlay(UserBehaviorReqVo vo);

    /**
     * 根据球队统计
     *
     * @param vo
     * @return
     */
    public List<ListByTeamResVo> getListByTeam(UserBehaviorReqVo vo);

    /**
     * 根据盘口统计
     *
     * @param vo
     * @return
     */
    public List<ListByMarketResVo> getListByMarket(UserBehaviorReqVo vo);

    /**
     * 根据赔率区间统计
     *
     * @param vo
     * @return
     */
    public List<ListByOddsResVo> getListByOdds(UserBehaviorReqVo vo);

    /**
     * 根据投注金额区间统计
     *
     * @param vo
     * @return
     */
    public List<ListByBetScopeResVo> getListByBetScope(UserBehaviorReqVo vo);

    /**
     * 根据 正副盘统计
     *
     * @param vo
     * @return
     */
    public List<ListByMainResVo> getListByMain(UserBehaviorReqVo vo);


    /**
     * 根据 对冲投注统计
     *
     * @param vo
     * @return
     */
    public List<ListByOppositeResVo> getListByOpposite(UserBehaviorReqVo vo);


    /**
     * 危险投注行为
     *
     * @param vo
     * @return
     */
    public List<UserDangerousOrderResVo> getDangerousList(UserBehaviorReqVo vo);

    /**
     * 访问特征详情
     *
     * @param vo
     * @return
     */
    public List<AccessListResVo> getAccessList(UserBehaviorReqVo vo);

    /**
     * 把ip设置对应的标签
     *
     * @param vo
     * @return
     */
    public boolean setTag(IpTagSetReqVo vo);

    /**
     * 把ip设置对应的标签
     *
     * @return
     */
    public List<RiskUserVisitIpTag> getIpTagList();

    /**
     * 根据时间区间 返回多个周内  第一天和最后一天
     *
     * @return
     */
    public List<WeekDaylVo> getDayList(Long startTime, Long endTime);


}
