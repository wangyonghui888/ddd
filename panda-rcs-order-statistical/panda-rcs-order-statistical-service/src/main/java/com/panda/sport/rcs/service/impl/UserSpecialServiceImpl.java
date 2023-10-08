package com.panda.sport.rcs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.sport.rcs.common.enums.BetStageEnum;
import com.panda.sport.rcs.common.enums.BetTypeEnum;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.common.vo.DangerousVo;
import com.panda.sport.rcs.common.vo.WeekDaylVo;
import com.panda.sport.rcs.common.vo.api.request.IpTagSetReqVo;
import com.panda.sport.rcs.common.vo.api.request.UserBehaviorReqVo;
import com.panda.sport.rcs.common.vo.api.response.*;
import com.panda.sport.rcs.customdb.entity.DataEntity;
import com.panda.sport.rcs.customdb.mapper.LanguageInfoMapper;
import com.panda.sport.rcs.customdb.mapper.UserSpecialStatisExtMapper;
import com.panda.sport.rcs.customdb.service.ILanguageService;
import com.panda.sport.rcs.db.entity.OrderOptionOddChange;
import com.panda.sport.rcs.db.entity.RiskOrderStatisticsByIp;
import com.panda.sport.rcs.db.entity.RiskUserVisitIp;
import com.panda.sport.rcs.db.entity.RiskUserVisitIpTag;
import com.panda.sport.rcs.db.mapper.UserProfileDangerousBetRuleMapper;
import com.panda.sport.rcs.db.service.IOrderStaticsForIpService;
import com.panda.sport.rcs.db.service.IRiskUserVisitIpService;
import com.panda.sport.rcs.db.service.IRiskUserVisitIpTagService;
import com.panda.sport.rcs.service.IUserSpecialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserSpecialServiceImpl implements IUserSpecialService {

    Logger log = LoggerFactory.getLogger(TagServiceImpl.class);

    @Autowired
    UserSpecialStatisExtMapper userSpecialStatisMapper;

    @Autowired
    IRiskUserVisitIpService riskUserVisitIpService;

    @Autowired
    IRiskUserVisitIpTagService riskUserVisitIpTagService;

    @Autowired
    ILanguageService languageService;

    @Autowired
    LanguageInfoMapper languageInfoMapper;

    @Autowired
    UserProfileDangerousBetRuleMapper userProfileDangerousBetRuleMapper;

    @Autowired
    private IOrderStaticsForIpService staticsForIpService;

    @Override
    public List<ListBySportResVo> getListBySport(UserBehaviorReqVo vo) {
        List<ListBySportResVo> list = userSpecialStatisMapper.getListBySport(vo);
        list.forEach(e -> {
            e.setSportName(languageService.getSportName(e.getSportId()));
        });
        return list;
    }

    @Override
    public List<ListByTournamentResVo> getListByTournament(UserBehaviorReqVo vo) {
        List<ListByTournamentResVo> list = userSpecialStatisMapper.getListByTournament(vo);
        list.forEach(e -> {
            if(ObjectUtils.isNotNull(e.getTournamentId())){
                e.setTournamentName(languageService.getTournamentName(new Long(e.getTournamentId())));
                e.setSportName(e.getSportName());
                DataEntity  dataEntity=languageInfoMapper.getTournamentCodeNameById(e.getTournamentId());
                if(dataEntity!=null) {
                    long sportId = dataEntity.getSportId();
                    e.setSportName(languageService.getSportName(sportId));
                }
            }
        });
        return list;
    }

    @Override
    public List<ListByPlayResVo> getListByPlay(UserBehaviorReqVo vo) {
        List<ListByPlayResVo> list = userSpecialStatisMapper.getListByPlay(vo);
        list.forEach(e -> {
                    e.setPlayName(languageService.getPlayName(e.getSportId(), e.getPlayId()));
                    e.setSportName(languageService.getSportName(e.getSportId()));
                }
        );
        return list;
    }

    @Override
    public List<ListByTeamResVo> getListByTeam(UserBehaviorReqVo vo) {
        List<ListByTeamResVo> list = userSpecialStatisMapper.getListByTeam(vo);
        list.forEach(e ->{
            if(ObjectUtils.isNotNull(e.getTeamId())){
                e.setTeamName(languageService.getTeamName(new Long(e.getTeamId())));
                long sportId = languageInfoMapper.getTeamNameCodeById(e.getTeamId()).getSportId();
                e.setSportName(languageService.getSportName(sportId));
            }
        });
        return list;
    }

    @Override
    public List<ListByMarketResVo> getListByMarket(UserBehaviorReqVo vo) {
        List<ListByMarketResVo> list = userSpecialStatisMapper.getListByMarket(vo);
        list.forEach(e -> e.setName(""));
        return list;
    }

    @Override
    public List<ListByOddsResVo> getListByOdds(UserBehaviorReqVo vo) {
        List<ListByOddsResVo> list = userSpecialStatisMapper.getListByOdds(vo);
        list.forEach(e -> e.setName(""));
        return list;
    }

    @Override
    public List<ListByBetScopeResVo> getListByBetScope(UserBehaviorReqVo vo) {
        List<ListByBetScopeResVo> list = userSpecialStatisMapper.getListByBetScope(vo); 
        list.forEach(e -> e.setName(""));
        return list;
    }

    @Override
    public List<ListByMainResVo> getListByMain(UserBehaviorReqVo vo) {
        List<ListByMainResVo> list = userSpecialStatisMapper.getListByMain(vo);
        list.forEach(e -> e.setName(getMainNameByType(e.getMainType())));
        return list;
    }

    private String getMainNameByType(int type) {
        if (1 == type) {
            return "正盘";
        }
        return "副盘";
    }

    @Override
    public List<ListByOppositeResVo> getListByOpposite(UserBehaviorReqVo vo) {
        List<ListByOppositeResVo> list = userSpecialStatisMapper.getListByOpposite(vo); 
        list.forEach(e -> e.setName(""));
        return list;
    }

    @Override
    public List<UserDangerousOrderResVo> getDangerousList(UserBehaviorReqVo vo) {
        DangerousVo dangerousVo = new DangerousVo();
        dangerousVo.setUserId(vo.getUserId());
        dangerousVo.setBeginTime(vo.getBeginDate());
        dangerousVo.setEndTime(vo.getEndDate());
        List<UserDangerousOrderResVo> list = userSpecialStatisMapper.getDangerousList(dangerousVo);
        for (UserDangerousOrderResVo userDangerousOrderResVo : list) {
            String ruleName = userProfileDangerousBetRuleMapper.selectById(userDangerousOrderResVo.getDangerousId()).getRuleName();
            userDangerousOrderResVo.setRuleName(ruleName);
            userDangerousOrderResVo.setAvgAmount(userDangerousOrderResVo.getAvgAmount().divide(new BigDecimal("1"), 2, BigDecimal.ROUND_DOWN));
        }
        return list;
    }

    @Override
    public List<AccessListResVo> getAccessList(UserBehaviorReqVo vo) {
        List<AccessListResVo> list = new ArrayList<>();
        //查出所有IP
        LambdaQueryWrapper<RiskUserVisitIp> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RiskUserVisitIp::getUserId, vo.getUserId());
        queryWrapper.ge(RiskUserVisitIp::getLoginDate, vo.getBeginDate());
        queryWrapper.le(RiskUserVisitIp::getLoginDate, vo.getEndDate());
        queryWrapper.orderByDesc(RiskUserVisitIp::getLoginDate);
        List<RiskUserVisitIp> riskUserVisitIpList = riskUserVisitIpService.list(queryWrapper);

        //设置单日异地 关联账户等属性
        for (RiskUserVisitIp riskUserVisitIp : riskUserVisitIpList) {
            LambdaQueryWrapper<RiskOrderStatisticsByIp> wrapper = new LambdaQueryWrapper();
            wrapper.eq(RiskOrderStatisticsByIp::getIp, riskUserVisitIp.getIp());
            RiskOrderStatisticsByIp one = staticsForIpService.getOne(wrapper);

            if(ObjectUtils.isEmpty(one.getTagId())){
                riskUserVisitIp.setTagId(null);
            }else{
                riskUserVisitIp.setTagId(Long.valueOf(one.getTagId()));
            }

            if (ObjectUtils.isEmpty(riskUserVisitIp.getTagId()) || riskUserVisitIp.getTagId() == 0) {
                riskUserVisitIp.setTagId(5L);
            }
            AccessListResVo accessListResVo = new AccessListResVo();
            BeanUtils.copyProperties(riskUserVisitIp, accessListResVo);
            //ip标签名设置
            LambdaQueryWrapper<RiskUserVisitIpTag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
            RiskUserVisitIpTag tag = riskUserVisitIpTagService.getById(riskUserVisitIp.getTagId());
            if (ObjectUtils.isNotEmpty(tag)) {
                accessListResVo.setTagName(tag.getTag());
            }
            //单日异地判断
            /*LambdaQueryWrapper<RiskUserVisitIp> visitIpLambdaQueryWrapper = new LambdaQueryWrapper<RiskUserVisitIp>();
            visitIpLambdaQueryWrapper.eq(RiskUserVisitIp::getUserId, riskUserVisitIp.getUserId());
            visitIpLambdaQueryWrapper.eq(RiskUserVisitIp::getLoginDate, riskUserVisitIp.getLoginDate());
            List<RiskUserVisitIp> visitIpList = riskUserVisitIpService.list(visitIpLambdaQueryWrapper);*/


            List<RiskUserVisitIp> visitIpList = riskUserVisitIpService.queryByUserIdAndLoginDate(riskUserVisitIp.getUserId(),riskUserVisitIp.getLoginDate());

            if (visitIpList.size() > 1) {
                accessListResVo.setIsAnotherPlace(1);
            }
            //关联账户数量设置
            LambdaQueryWrapper<RiskUserVisitIp> relationQueryWrapper = new LambdaQueryWrapper<RiskUserVisitIp>();
            relationQueryWrapper.eq(RiskUserVisitIp::getIp, riskUserVisitIp.getIp());
            //relationQueryWrapper.eq(RiskUserVisitIp::getLoginDate, riskUserVisitIp.getLoginDate());
            relationQueryWrapper.groupBy(RiskUserVisitIp::getUserId, RiskUserVisitIp::getIp);
            relationQueryWrapper.select(RiskUserVisitIp::getUserId);
            List<RiskUserVisitIp> relationIpList = riskUserVisitIpService.list(relationQueryWrapper);
            if (relationIpList.size() > 1) {
                accessListResVo.setRelationUserNum(relationIpList.size());
                List<UserResVo> relationUserList = new ArrayList<>();
                for (RiskUserVisitIp visitIp : relationIpList) {
                    UserResVo userResVo = languageInfoMapper.getUser(visitIp.getUserId());
                    relationUserList.add(userResVo);
                }
                accessListResVo.setRelationUserList(relationUserList);
            }
            list.add(accessListResVo);
        }
        return list;
    }

    @Override
    public boolean setTag(IpTagSetReqVo vo) {
        LambdaUpdateWrapper<RiskUserVisitIp> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(RiskUserVisitIp::getTagId, vo.getTagId());
        wrapper.eq(RiskUserVisitIp::getIp, vo.getIp());
        riskUserVisitIpService.update(wrapper);
        return true;
    }

    @Override
    public List<RiskUserVisitIpTag> getIpTagList() {
        LambdaQueryWrapper<RiskUserVisitIpTag> wrapper = new LambdaQueryWrapper<>();
        return riskUserVisitIpTagService.list();
    }


    /***
     * 工具方法
     * 需求:以自然周为1个周期进行统计，第1个、最后一个周期可能不足7天，按其实际天数统计即可
     * 根据时间段 获取区间内多个周 的时间范围
     * 举例:2020-06-09至2020-06-26 会分为如下几个区间
     * {"startDay":"2020-06-09","endDay":"2020-06-14"},
     * {"startDay":"2020-06-15","endDay":"2020-06-21"},
     * {"startDay":"2020-06-22","endDay":"2020-06-26"}
     * @return
     */
    @Override
    public List<WeekDaylVo> getDayList(Long startTime, Long endTime) {
        LocalDate start = LocalDateTimeUtil.milliToLocalDateTime(startTime).toLocalDate();
        LocalDate end = LocalDateTimeUtil.milliToLocalDateTime(endTime).toLocalDate();

        List<WeekDaylVo> list = new ArrayList<>();
        LocalDate today = start;
        WeekDaylVo weekDaylVo = null;
        while (!today.isAfter(end)) {
            DayOfWeek dayOfWeek = today.getDayOfWeek();
            if (dayOfWeek.equals(DayOfWeek.MONDAY) || today.equals(start)) {
                weekDaylVo = new WeekDaylVo();
                weekDaylVo.setStartDay(today);
            }
            if (dayOfWeek.equals(DayOfWeek.SUNDAY) || today.equals(end)) {
                weekDaylVo.setEndDay(today);
            }
            if (ObjectUtils.isNotEmpty(weekDaylVo.getStartDay()) && ObjectUtils.isNotEmpty(weekDaylVo.getEndDay())) {
                list.add(weekDaylVo);
            }
            today = today.plusDays(1);
        }
        return list;
    }
    /**
     * @Description   根据投注类型统计
     * @Param [vo]
     * @Author  myname
     * @Date  11:58 2021/1/12
     * @return java.util.List<com.panda.sport.rcs.common.vo.api.response.ListByBetTypeResVo>
     **/
    @Override
    public List<ListByBetTypeResVo> getListByBetType(UserBehaviorReqVo vo) {
        List<ListByBetTypeResVo> list = userSpecialStatisMapper.getListByBetType(vo);
        list.forEach(e -> {

            if(e.getBetType() == null){
                e.setBetTypeName("全部");
                e.setBetType(0);
            }else {
                e.setBetTypeName(BetTypeEnum.get(e.getBetType()).getName());
            }
        });
        return list;
    }
    /**
     * @Description   根据投注阶段统计
     * @Param [vo]
     * @Author  myname
     * @Date  11:58 2021/1/12
     * @return java.util.List<com.panda.sport.rcs.common.vo.api.response.ListByBetStageResVo>
     **/
    @Override
    public List<ListByBetStageResVo> getListByBetStage(UserBehaviorReqVo vo) {
        List<ListByBetStageResVo> list = userSpecialStatisMapper.getListByBetStage(vo);
        list.forEach(e -> {
            if(e.getBetStage() == null){
                e.setBetStageName("全部");
                e.setBetStage(0);
            }else {
                e.setBetStageName(BetStageEnum.get(e.getBetStage()).getName());
            }
        });
        return list;
    }
}
