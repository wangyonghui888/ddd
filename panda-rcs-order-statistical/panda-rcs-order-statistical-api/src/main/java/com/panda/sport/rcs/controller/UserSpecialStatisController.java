package com.panda.sport.rcs.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.utils.CopyUtils;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.common.vo.WeekDaylVo;
import com.panda.sport.rcs.common.vo.api.request.IpTagSetReqVo;
import com.panda.sport.rcs.common.vo.api.request.UserBehaviorReqVo;
import com.panda.sport.rcs.common.vo.api.response.*;
import com.panda.sport.rcs.db.entity.RiskUserVisitIpTag;
import com.panda.sport.rcs.db.service.IUserSpecialStatisService;
import com.panda.sport.rcs.service.IUserSpecialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 用户行为详情-投注偏好/财务特征 前端控制器
 * </p>
 *
 * @author lithan auto
 * @since 2020-06-21
 */
@RestController
@RequestMapping("/userSpecialStatis")
@Api(tags = "用户行为详情")
public class UserSpecialStatisController {

    @Autowired
    IUserSpecialStatisService userSpecialStatisService;

    //用户行为详情 接口service
    @Autowired
    IUserSpecialService userSpecialService;

    /**
     * 投注偏好详情/财务特征详情-占比-球类
     */
    @ApiOperation(value = "财务特征详情-球类", notes = "财务特征详情,sportId为0表示全部,其他表示具体球类id")
    @RequestMapping(value = "/finance/sport", method = {RequestMethod.POST})
    public Result<List<ListBySportResVo>> getFinanceListBySport(@RequestBody @Valid UserBehaviorReqVo vo) {
        //各球类统计 根据sportid分组 汇总
        List<ListBySportResVo> list = userSpecialService.getListBySport(vo);
        if (ObjectUtils.isEmpty(list)) {
            return Result.succes(list);
        }
        //全部汇总统计 sportId为0
        vo.setIsAll(1);
        List<ListBySportResVo> allList = userSpecialService.getListBySport(vo);
        //合并结果集
        allList.addAll(list);
        return Result.succes(allList);
    }

    /**
     * 投注偏好详情-占比-球类
     */
    @ApiOperation(value = "投注偏好详情-占比-球类", notes = "投注偏好详情-占比-球类")
    @RequestMapping(value = "/bet/rate/sport", method = {RequestMethod.POST})
    public Result<List<ListBySportResVo>> getBetRateListBySport(@RequestBody @Valid UserBehaviorReqVo vo) {
        //返回结果  包括前五 和其他
        List<ListBySportResVo> resList = new ArrayList<>();
        vo.setOrderColumn("betAmount");
        //各球类统计
        List<ListBySportResVo> sportlist = userSpecialService.getListBySport(vo);
        //如果无投注
        if (ObjectUtils.isEmpty(sportlist)) {
            return Result.succes(resList);
        }
        //如果有投注记录
        //全部汇总统计
        vo.setIsAll(1);
        List<ListBySportResVo> allList = userSpecialService.getListBySport(vo);
        //如果不超过5种球类 直接返回前五条即可  无其他
        if (sportlist.size() <= 5) {
            resList.addAll(sportlist);
            return Result.succes(resList);
        }
        //超过5种球类 计算其他
        ListBySportResVo otherVo = new ListBySportResVo();
        otherVo.setBetAmount(allList.get(0).getBetAmount());
        otherVo.setSportId(0);
        otherVo.setSportName("其他");
        //其他投注金额 等于 总金额-前五金额
        for (int i = 0; i < 5; i++) {
            ListBySportResVo sportResVo = sportlist.get(i);
            resList.add(sportResVo);
            otherVo.setBetAmount(otherVo.getBetAmount().subtract(sportResVo.getBetAmount()));
        }
        resList.add(otherVo);
        return Result.succes(resList);

    }

    /**
     * 投注偏好详情-趋势-球类
     */
    @ApiOperation(value = "投注偏好详情-趋势-球类", notes = "投注偏好详情-趋势-球类")
    @RequestMapping(value = "/bet/trend/sport", method = {RequestMethod.POST})
    public Result<List<ListTrendBySportResVo>> getBetTrendListBySport(@RequestBody @Valid UserBehaviorReqVo vo) {
        List<ListTrendBySportResVo> dataList = new ArrayList<>();
        //拆分周区间
        List<WeekDaylVo> dayList = userSpecialService.getDayList(vo.getBeginDate(), vo.getEndDate());
        //循环处理每个周的分组数据
        for (WeekDaylVo weekDaylVo : dayList) {
            UserBehaviorReqVo paramVo = CopyUtils.clone(vo,UserBehaviorReqVo.class);
            //得到某个周的数据
            paramVo.setBeginDate(LocalDateTimeUtil.getMilli(weekDaylVo.getStartDay().atStartOfDay()));
            paramVo.setEndDate(LocalDateTimeUtil.getMilli(weekDaylVo.getEndDay().atStartOfDay()));
            Result<List<ListBySportResVo>> result = getBetRateListBySport(paramVo);
            //组装返回数据
            ListTrendBySportResVo trendBySportResVo = new ListTrendBySportResVo();
            trendBySportResVo.setBeginDate(paramVo.getBeginDate());
            trendBySportResVo.setEndDate(paramVo.getEndDate());
            trendBySportResVo.setList(result.getData());
            dataList.add(trendBySportResVo);
        }
        return Result.succes(dataList);
    }

    /**
     * 投注偏好详情-占比-联赛
     */
    @ApiOperation(value = "投注偏好详情-占比-联赛", notes = "投注偏好详情-占比-联赛")
    @RequestMapping(value = "/bet/rate/tournament", method = {RequestMethod.POST})
    public Result<List<ListByTournamentResVo>> getBetRateListByTournament(@RequestBody @Valid UserBehaviorReqVo vo) {
        //返回结果  包括前五 和其他
        List<ListByTournamentResVo> resList = new ArrayList<>();
        //分组统计
        vo.setOrderColumn("betAmount");
        List<ListByTournamentResVo> tournamentList = userSpecialService.getListByTournament(vo);
        //如果无投注
        if (ObjectUtils.isEmpty(tournamentList)) {
            return Result.succes(resList);
        }

        //如果有投注记录
        //全部汇总统计
        vo.setIsAll(1);
        List<ListByTournamentResVo> allList = userSpecialService.getListByTournament(vo);
        //如果不超过5种 直接返回前五条即可  无其他
        if (tournamentList.size() <= 5) {
            resList.addAll(tournamentList);
            return Result.succes(resList);
        }
        //超过5种球类 计算其他
        ListByTournamentResVo otherVo = new ListByTournamentResVo();
        otherVo.setBetAmount(allList.get(0).getBetAmount());
        otherVo.setTournamentId(0L);
        otherVo.setTournamentName("其他");
        otherVo.setSportName("其他");
        //其他投注金额 等于 总金额-前五金额
        for (int i = 0; i < 5; i++) {
            ListByTournamentResVo sportResVo = tournamentList.get(i);
            resList.add(sportResVo);
            otherVo.setBetAmount(otherVo.getBetAmount().subtract(sportResVo.getBetAmount()));
        }
        resList.add(otherVo);
        return Result.succes(resList);
    }

    /**
     * 投注偏好详情-趋势-联赛
     */
    @ApiOperation(value = "投注偏好详情-趋势-联赛", notes = "投注偏好详情-趋势-联赛")
    @RequestMapping(value = "/bet/trend/tournament", method = {RequestMethod.POST})
    public Result<List<ListTrendByTournamentResVo>> getBetTrendListByTournament(@RequestBody @Valid UserBehaviorReqVo vo) {
        List<ListTrendByTournamentResVo> dataList = new ArrayList<>();
        //拆分周区间
        List<WeekDaylVo> dayList = userSpecialService.getDayList(vo.getBeginDate(), vo.getEndDate());
        //循环处理每个周的分组数据
        for (WeekDaylVo weekDaylVo : dayList) {

            UserBehaviorReqVo paramVo = CopyUtils.clone(vo,UserBehaviorReqVo.class);
            //得到某个周的数据
            paramVo.setBeginDate(LocalDateTimeUtil.getMilli(weekDaylVo.getStartDay().atStartOfDay()));
            paramVo.setEndDate(LocalDateTimeUtil.getMilli(weekDaylVo.getEndDay().atStartOfDay()));
            Result<List<ListByTournamentResVo>> result = getBetRateListByTournament(paramVo);
            //组装返回数据
            ListTrendByTournamentResVo resVo = new ListTrendByTournamentResVo();
            resVo.setBeginDate(paramVo.getBeginDate());
            resVo.setEndDate(paramVo.getEndDate());
            resVo.setList(result.getData());
            dataList.add(resVo);
        }
        return Result.succes(dataList);
    }


    /**
     * 投注偏好详情/财务特征详情-占比-联赛
     */
    @ApiOperation(value = "财务特征详情-联赛", notes = "财务特征详情,tournamentId为0表示全部,其他表示具体联赛id")
    @RequestMapping(value = "/finance/tournament", method = {RequestMethod.POST})
    public Result<List<ListByTournamentResVo>> getFinanceListByTournament(@RequestBody @Valid UserBehaviorReqVo vo) {
        //联赛统计 根据tournamentId分组 汇总
        List<ListByTournamentResVo> list = userSpecialService.getListByTournament(vo);
        if (ObjectUtils.isEmpty(list)) {
            return Result.succes(list);
        }
        //全部汇总统计
        vo.setIsAll(1);
        List<ListByTournamentResVo> allList = userSpecialService.getListByTournament(vo);
        //合并结果集
        allList.forEach(e -> e.setTournamentName("全部"));
        allList.addAll(list);
        return Result.succes(allList);
    }

    /**
     * 投注偏好详情/财务特征详情-占比-玩法
     */
    @ApiOperation(value = "财务特征详情-玩法", notes = "财务特征详情,playId为0表示全部,其他表示具体玩法id ")
    @RequestMapping(value = "/finance/play", method = {RequestMethod.POST})
    public Result<List<ListByPlayResVo>> getFinanceListByPlay(@RequestBody @Valid UserBehaviorReqVo vo) {
        //联赛统计 根据playId分组 汇总
        List<ListByPlayResVo> list = userSpecialService.getListByPlay(vo);
        if (ObjectUtils.isEmpty(list)) {
            return Result.succes(list);
        }
        //全部汇总统计
        vo.setIsAll(1);
        List<ListByPlayResVo> allList = userSpecialService.getListByPlay(vo);
        //合并结果集
        allList.addAll(list);
        return Result.succes(allList);
    }

    /**
     * 投注偏好详情-占比-玩法
     */
    @ApiOperation(value = "投注偏好详情-占比-玩法", notes = "投注偏好详情-占比-玩法")
    @RequestMapping(value = "/bet/rate/paly", method = {RequestMethod.POST})
    public Result<List<ListByPlayResVo>> getBetRateListByPlay(@RequestBody @Valid UserBehaviorReqVo vo) {
        //返回结果  包括前五 和其他
        List<ListByPlayResVo> resList = new ArrayList<>();
        //分组统计
        vo.setOrderColumn("betAmount");
        List<ListByPlayResVo> playList = userSpecialService.getListByPlay(vo);
        //如果无投注
        if (ObjectUtils.isEmpty(playList)) {
            return Result.succes(resList);
        }

        //如果有投注记录
        //全部汇总统计
        vo.setIsAll(1);
        List<ListByPlayResVo> allList = userSpecialService.getListByPlay(vo);
        //如果不超过5种 直接返回前五条即可  无其他
        if (playList.size() <= 5) {
            resList.addAll(playList);
            return Result.succes(resList);
        }
        //超过5种球类 计算其他
        ListByPlayResVo otherVo = new ListByPlayResVo();
        otherVo.setBetAmount(allList.get(0).getBetAmount());
        otherVo.setPlayId(0);
        otherVo.setSportId(0);
        otherVo.setSportName("其他");
        otherVo.setPlayName("其他");
        //其他投注金额 等于 总金额-前五金额
        for (int i = 0; i < 5; i++) {
            ListByPlayResVo sportResVo = playList.get(i);
            resList.add(sportResVo);
            otherVo.setBetAmount(otherVo.getBetAmount().subtract(sportResVo.getBetAmount()));
        }
        resList.add(otherVo);
        return Result.succes(resList);
    }

    /**
     * 投注偏好详情-趋势-玩法
     */
    @ApiOperation(value = "投注偏好详情-趋势-玩法", notes = "投注偏好详情-趋势-玩法")
    @RequestMapping(value = "/bet/trend/paly", method = {RequestMethod.POST})
    public Result<List<ListTrendByPlayResVo>> getBetTrendListByPlay(@RequestBody @Valid UserBehaviorReqVo vo) {
        List<ListTrendByPlayResVo> dataList = new ArrayList<>();
        //拆分周区间
        List<WeekDaylVo> dayList = userSpecialService.getDayList(vo.getBeginDate(), vo.getEndDate());
        //循环处理每个周的分组数据
        for (WeekDaylVo weekDaylVo : dayList) {

            UserBehaviorReqVo paramVo = CopyUtils.clone(vo,UserBehaviorReqVo.class);
            //得到某个周的数据
            paramVo.setBeginDate(LocalDateTimeUtil.getMilli(weekDaylVo.getStartDay().atStartOfDay()));
            paramVo.setEndDate(LocalDateTimeUtil.getMilli(weekDaylVo.getEndDay().atStartOfDay()));
            Result<List<ListByPlayResVo>> result = getBetRateListByPlay(paramVo);
            //组装返回数据
            ListTrendByPlayResVo resVo = new ListTrendByPlayResVo();
            resVo.setBeginDate(paramVo.getBeginDate());
            resVo.setEndDate(paramVo.getEndDate());
            resVo.setList(result.getData());
            dataList.add(resVo);
        }
        return Result.succes(dataList);
    }



    /**
     * 投注偏好详情-趋势-球队
     */
    @ApiOperation(value = "投注偏好详情-趋势-球队", notes = "投注偏好详情-趋势-球队")
    @RequestMapping(value = "/bet/trend/team", method = {RequestMethod.POST})
    public Result<List<ListTrendByTeamResVo>> getBetTrendListByTeam(@RequestBody @Valid UserBehaviorReqVo vo) {
        List<ListTrendByTeamResVo> dataList = new ArrayList<>();
        //拆分周区间
        List<WeekDaylVo> dayList = userSpecialService.getDayList(vo.getBeginDate(), vo.getEndDate());
        //循环处理每个周的分组数据
        for (WeekDaylVo weekDaylVo : dayList) {

            UserBehaviorReqVo paramVo = CopyUtils.clone(vo,UserBehaviorReqVo.class);
            //得到某个周的数据
            paramVo.setBeginDate(LocalDateTimeUtil.getMilli(weekDaylVo.getStartDay().atStartOfDay()));
            paramVo.setEndDate(LocalDateTimeUtil.getMilli(weekDaylVo.getEndDay().atStartOfDay()));
            Result<List<ListByTeamResVo>> result = getBetRateListByTeam(paramVo);
            //组装返回数据
            ListTrendByTeamResVo resVo = new ListTrendByTeamResVo();
            resVo.setBeginDate(paramVo.getBeginDate());
            resVo.setEndDate(paramVo.getEndDate());
            resVo.setList(result.getData());
            dataList.add(resVo);
        }
        return Result.succes(dataList);
    }


    /**
     * 投注偏好详情-占比-联赛
     */
    @ApiOperation(value = "投注偏好详情-占比-球队", notes = "投注偏好详情-占比-球队 Team")
    @RequestMapping(value = "/bet/rate/team", method = {RequestMethod.POST})
    public Result<List<ListByTeamResVo>> getBetRateListByTeam(@RequestBody @Valid UserBehaviorReqVo vo) {
        //返回结果  包括前五 和其他
        List<ListByTeamResVo> resList = new ArrayList<>();
        //分组统计
        vo.setOrderColumn("betAmount");
        List<ListByTeamResVo> teamList = userSpecialService.getListByTeam(vo);
        //如果无投注
        if (ObjectUtils.isEmpty(teamList)) {
            return Result.succes(resList);
        }

        //如果有投注记录
        //全部汇总统计
        vo.setIsAll(1);
        List<ListByTeamResVo> allList = userSpecialService.getListByTeam(vo);
        //如果不超过5种 直接返回前五条即可  无其他
        if (teamList.size() <= 5) {
            resList.addAll(teamList);
            return Result.succes(resList);
        }
        //超过5种球类 计算其他
        ListByTeamResVo otherVo = new ListByTeamResVo();
        otherVo.setBetAmount(allList.get(0).getBetAmount());
        otherVo.setTeamId(0L);
        otherVo.setTeamName("其他");
        otherVo.setSportName("其他");
        //其他投注金额 等于 总金额-前五金额
        for (int i = 0; i < 5; i++) {
            ListByTeamResVo bean = teamList.get(i);
            resList.add(bean);
            otherVo.setBetAmount(otherVo.getBetAmount().subtract(bean.getBetAmount()));
        }
        resList.add(otherVo);
        return Result.succes(resList);
    }

    /**
     * 投注偏好详情-占比-盘口类型
     */
    @ApiOperation(value = "投注偏好详情-占比-盘口类型", notes = "投注偏好详情-占比-盘口类型 Market")
    @RequestMapping(value = "/bet/rate/market", method = {RequestMethod.POST})
    public Result<List<ListByMarketResVo>> getBetRateListByMarket(@RequestBody @Valid UserBehaviorReqVo vo) {
        List<ListByMarketResVo> marketList = userSpecialService.getListByMarket(vo);
        marketList.forEach( e-> e.setName(""));
        return Result.succes(marketList);
    }

    /**
     * 投注偏好详情-占比-赔率区间
     */
    @ApiOperation(value = "投注偏好详情-占比--赔率区间", notes = "投注偏好详情-占比--赔率区间 odds  显示[1-1.3 )、[1.3-1.5 )、[1.5-2 )、[2,3 )、[3,5 )、[5,10 )、>10等赔率区间")
    @RequestMapping(value = "/bet/rate/odds", method = {RequestMethod.POST})
    public Result<List<ListByOddsResVo>> getBetRateListByOdds(@RequestBody @Valid UserBehaviorReqVo vo) {
        List<ListByOddsResVo> list = userSpecialService.getListByOdds(vo);
        list.forEach( e-> e.setName(""));
        return Result.succes(list);
    }

    /**
     * 投注偏好详情-占比-投注金额区间
     */
    @ApiOperation(value = "投注偏好详情-占比--投注金额区间", notes = "投注偏好详情-占比--投注金额区间 1对应<1000、2对应1000,2000 )、3对应[2000,5000 )、4对应[5000,10000 )、5对应>10000 ")
    @RequestMapping(value = "/bet/rate/betScope", method = {RequestMethod.POST})
    public Result<List<ListByBetScopeResVo>> getBetRateListByBetScope(@RequestBody @Valid UserBehaviorReqVo vo) {
        List<ListByBetScopeResVo> list = userSpecialService.getListByBetScope(vo);
        list.forEach( e-> e.setName(""));
        return Result.succes(list);
    }

    /**
     * 投注偏好详情-占比-正副盘
     */
    @ApiOperation(value = "投注偏好详情-占比-正副盘", notes = "投注偏好详情-占比-正副盘")
    @RequestMapping(value = "/bet/rate/main", method = {RequestMethod.POST})
    public Result<List<ListByMainResVo>> getBetRateListByMain(@RequestBody @Valid UserBehaviorReqVo vo) {
        List<ListByMainResVo> list = userSpecialService.getListByMain(vo);
        list.forEach( e-> e.setName(e.getMainType()==1?"正盘":"副盘"));
        return Result.succes(list);
    }

    /**
     * 投注偏好详情-占比-正副盘
     */
    @ApiOperation(value = "投注偏好详情-占比-对冲投注", notes = "投注偏好详情-占比-对冲投注")
    @RequestMapping(value = "/bet/rate/opposite", method = {RequestMethod.POST})
    public Result<List<ListByOppositeResVo>> getBetRateListOpposite(@RequestBody @Valid UserBehaviorReqVo vo) {
        List<ListByOppositeResVo> list = userSpecialService.getListByOpposite(vo);
        list.forEach( e-> e.setName(""));
        return Result.succes(list);
    }



    @ApiOperation(value = "危险投注行为", notes = "危险投注行为")
    @RequestMapping(value = "/dangerous/list", method = {RequestMethod.POST})
    public Result<List<UserDangerousOrderResVo>> getDangerousList(@RequestBody @Valid UserBehaviorReqVo vo) {
        List<UserDangerousOrderResVo> list = userSpecialService.getDangerousList(vo);
        return Result.succes(list);
    }


    /**
     * 访问特征详情
     */
    @ApiOperation(value = "访问特征详情", notes = "访问特征详情")
    @RequestMapping(value = "/access/list", method = {RequestMethod.POST})
    public Result<List<AccessListResVo>> getAccessList(@RequestBody @Valid UserBehaviorReqVo vo) {
        List<AccessListResVo> list = userSpecialService.getAccessList(vo);
        return Result.succes(list);
    }

    /**
     * ip设置标签
     */
    @ApiOperation(value = "ip设置标签", notes = "ip设置标签")
    @RequestMapping(value = "/ip/setTag", method = {RequestMethod.POST})
    public Result setTag(@RequestBody @Valid IpTagSetReqVo vo) {
        boolean flag = userSpecialService.setTag(vo);
        return Result.succes(flag);
    }

    /**
     * ip标签获取
     */
    @ApiOperation(value = "ip标签获取", notes = "ip标签获取")
    @RequestMapping(value = "/ipTag/list", method = {RequestMethod.POST})
    public Result getIpTagList() {
        List<RiskUserVisitIpTag> list = userSpecialService.getIpTagList();
        return Result.succes(list);
    }



    /**
     * 投注偏好详情/财务特征详情-占比-投注类型
     */
    @ApiOperation(value = "财务特征详情-投注类型", notes = "财务特征详情,betType为0表示全部,其他表示具体投注类型")
    @RequestMapping(value = "/finance/betType", method = {RequestMethod.POST})
    public Result<List<ListByBetTypeResVo>> getFinanceListByBetType(@RequestBody @Valid UserBehaviorReqVo vo) {
        //联赛统计 根据tournamentId分组 汇总
        List<ListByBetTypeResVo> list = userSpecialService.getListByBetType(vo);
        if (ObjectUtils.isEmpty(list)) {
            return Result.succes(list);
        }
        //全部汇总统计
        vo.setIsAll(1);
        List<ListByBetTypeResVo> allList = userSpecialService.getListByBetType(vo);
        //合并结果集
        allList.addAll(list);
        return Result.succes(allList);
    }


    /**
     * 投注偏好详情/财务特征详情-占比-投注阶段
     */
    @ApiOperation(value = "财务特征详情-投注阶段", notes = "财务特征详情,betStage为0表示全部,其他表示具体投注阶段")
    @RequestMapping(value = "/finance/betStage", method = {RequestMethod.POST})
    public Result<List<ListByBetStageResVo>> getFinanceListByBetStage(@RequestBody @Valid UserBehaviorReqVo vo) {
        //联赛统计 根据tournamentId分组 汇总
        List<ListByBetStageResVo> list = userSpecialService.getListByBetStage(vo);
        if (ObjectUtils.isEmpty(list)) {
            return Result.succes(list);
        }
        //全部汇总统计
        vo.setIsAll(1);
        List<ListByBetStageResVo> allList = userSpecialService.getListByBetStage(vo);
        //合并结果集
        allList.addAll(list);
        return Result.succes(allList);
    }

}
