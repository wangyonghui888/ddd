package com.panda.sport.rcs.credit.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.credit.CreditLimitApiService;
import com.panda.sport.data.rcs.dto.credit.CreditConfigDto;
import com.panda.sport.data.rcs.dto.credit.CreditConfigSaveDto;
import com.panda.sport.rcs.credit.service.impl.RemainLimitService;
import com.panda.sport.rcs.entity.vo.RemainLimitReqVo;
import com.panda.sport.rcs.entity.vo.RemainLimitResVo;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mapper.StandardSportTournamentMapper;
import com.panda.sport.rcs.pojo.RcsOperateMerchantsSet;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.StandardSportTournament;
import com.panda.sport.rcs.service.IRcsOperateMerchantsSetService;
import com.panda.sport.rcs.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用网代理配置
 * @Author : Paca
 * @Date : 2021-05-08 12:35
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@RestController
@RequestMapping(value = "/credit/config")
public class CreditConfigController {

    @Autowired
    private CreditLimitApiService creditLimitApiService;
    @Autowired
    private RemainLimitService remainLimitService;

    @Autowired
    private IRcsOperateMerchantsSetService rcsOperateMerchantsSetService;

    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    private StandardSportTournamentMapper standardSportTournamentMapper;

    /**
     * 查询信用代理最大限额、信用代理限额
     *
     * @param request
     * @return
     */
    @PostMapping("/queryCreditLimitConfig")
    public Response<CreditConfigDto> queryCreditLimitConfig(@RequestBody Request<CreditConfigDto> request) {
        return creditLimitApiService.queryCreditLimitConfig(request);
    }

    /**
     * 保存或更新商户配置、代理配置
     *
     * @param request
     * @return
     */
    @PostMapping("/saveOrUpdateCreditLimitConfig")
    public Response<Boolean> saveOrUpdateCreditLimitConfig(@RequestBody Request<CreditConfigSaveDto> request) {
        return creditLimitApiService.saveOrUpdateCreditLimitConfig(request);
    }

//    @PostMapping("/queryMaxBetMoneyBySelect")
//    public Response queryMaxBetMoneyBySelect(@RequestBody Request<OrderBean> requestParam) {
//        return creditLimitApiService.queryMaxBetMoneyBySelect(requestParam);
//    }

//    @PostMapping("/saveOrderAndValidateMaxPaid")
//    public Response saveOrderAndValidateMaxPaid(Request<OrderBean> requestParam) {
//        return creditLimitApiService.saveOrderAndValidateMaxPaid(requestParam);
//    }

    @PostMapping("/getRemainLimit")
    public Response getRemainLimit(@RequestBody RemainLimitReqVo reqVo) {
        String uuid = CommonUtils.getUUID();
        MDC.put("X-B3-TraceId", uuid);
        log.info("信用额度::{}::获取剩余限额开始：{}", reqVo.getUserId(), JSON.toJSONString(reqVo));
        RemainLimitResVo resVo = new RemainLimitResVo();
        Integer type = reqVo.getType();
        String creditAgentId = reqVo.getCreditAgentId();
        Long userId = reqVo.getUserId();

        LambdaQueryWrapper<RcsOperateMerchantsSet> merchantQueryWrapper = Wrappers.lambdaQuery();
        merchantQueryWrapper.eq(RcsOperateMerchantsSet::getMerchantsId, creditAgentId);
        RcsOperateMerchantsSet operateMerchantsSet = rcsOperateMerchantsSetService.getOne(merchantQueryWrapper);
        if (operateMerchantsSet == null) {
            return Response.error(Response.FAIL, "信用代理不存在：" + uuid);
        }
        Long merchantId = operateMerchantsSet.getCreditParentId();
        if (NumberUtils.INTEGER_TWO.equals(type)) {
            // 串关剩余额度查询
            remainLimitService.getSeriesRemainLimit(merchantId, creditAgentId, userId, resVo);
        } else {
            // 单关剩余额度查询
            String matchManageId = reqVo.getMatchManageId();
            Integer playId = reqVo.getPlayId();
            LambdaQueryWrapper<StandardMatchInfo> matchInfoQueryWrapper = Wrappers.lambdaQuery();
            matchInfoQueryWrapper.eq(StandardMatchInfo::getMatchManageId, matchManageId);
            StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectOne(matchInfoQueryWrapper);
            if (standardMatchInfo == null) {
                return Response.error(Response.FAIL, "赛事不存在：" + uuid);
            }
            StandardSportTournament standardSportTournament = standardSportTournamentMapper.selectById(standardMatchInfo.getStandardTournamentId());
            if (standardSportTournament != null) {
                standardMatchInfo.setTournamentLevel(standardSportTournament.getTournamentLevel());
            }
            remainLimitService.getSingleRemainLimit(standardMatchInfo, merchantId, creditAgentId, userId, playId, resVo);
        }
        Response response = Response.success(resVo);
        response.setMsg(uuid);
        return response;
    }
}
