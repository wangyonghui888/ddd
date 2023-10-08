package com.panda.sport.rcs.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.sport.rcs.ProducerSendMessageUtils;
import com.panda.sport.rcs.common.bean.RuleResult;
import com.panda.sport.rcs.common.enums.DangerousEnum;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.common.vo.MatchLimitDataReqVo;
import com.panda.sport.rcs.common.vo.rule.*;
import com.panda.sport.rcs.customdb.mapper.RuleExtMapper;
import com.panda.sport.rcs.customdb.service.ILanguageService;
import com.panda.sport.rcs.db.entity.RiskUserVisitIp;
import com.panda.sport.rcs.db.service.IRiskUserVisitIpService;
import com.panda.sport.rcs.redis.service.RedisService;
import com.panda.sport.rcs.service.IRuleDataService;
import com.panda.sport.rcs.service.IRuleService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 规则逻辑实现
 *
 * @author :  lithan
 * @date: 2020-06-28 09:50:05
 */
@Service
public class RuleServiceImpl implements IRuleService {

    /**
     * 一天的毫秒数
     */
    private static final Long ONE_DAY_MILL = 24 * 60 * 60 * 1000L;

    Logger log = LoggerFactory.getLogger(RuleServiceImpl.class);

    @Autowired
    RuleExtMapper ruleExtMapper;

    @Autowired
    IRuleDataService ruleDataService;

    @Autowired
    IRiskUserVisitIpService riskUserVisitIpService;
    @Autowired
    ILanguageService languageService;
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;
    @Autowired
    RedisService redisService;

    /**
     * R1	用户盈亏金额	"一段时间（参数1天）内，用户盈亏金额位于[参数2,参数3)区间；盈亏金额=派彩金额-已结算注单的投注金额
     * 若某个参数不填，则所在区间方向不限制，例如只有参数2，则表示盈亏金额>=参数2；例如只有参数3，则表示盈亏金额<参数3
     * 输出值：判断结果（1/0）；实际值（实际亏损金额）"
     */
    @Override
    public RuleResult<BigDecimal> r1(RuleParameterVo vo) {
        log.info("R1用户盈亏金额{}", JSONObject.toJSONString(vo));
        //多少天内
        Long days = Long.valueOf(vo.getParameter1());
        //区间值  小
        BigDecimal min = ObjectUtils.isNotEmpty(vo.getParameter2()) ? new BigDecimal(vo.getParameter2()) : BigDecimal.valueOf(Long.MIN_VALUE);
        //区间值  大
        BigDecimal max = ObjectUtils.isNotEmpty(vo.getParameter3()) ? new BigDecimal(vo.getParameter3()) : BigDecimal.valueOf(Long.MAX_VALUE);

        //获取时间段
        Long endTime = vo.getTime();
        Long startTime = endTime - (days * ONE_DAY_MILL);
        //计算盈利金额
        FinancialRuleVo ruleVo = ruleDataService.getProfitAmount(vo.getUserId(), startTime, endTime);
        if (ObjectUtils.isEmpty(ruleVo)) {
            ruleVo = new FinancialRuleVo();
            log.info("用户{}规则计算盈利金额{}-{}无数据", vo.getUserId(), min, max);
            //return RuleResult.init(false, BigDecimal.ZERO);
            ruleVo.setProfitAmount(BigDecimal.ZERO);
            ruleVo.setBetAmount(BigDecimal.ZERO);
        }
        //对比 是否满足范围
        boolean flag = false;
        if (ruleVo.getProfitAmount().compareTo(min) >= 0 && ruleVo.getProfitAmount().compareTo(max) <= 0) {
            flag = true;
        }
        log.info("用户{}规则计算盈利金额{}-{}结果:{}", vo.getUserId(), min, max, flag);
        return RuleResult.init(flag, ruleVo.getProfitAmount().setScale(2,BigDecimal.ROUND_DOWN));
    }

    /**
     * R2	盈利率标准	"一段时间（参数1天）内，用户盈利率位于[参数2,参数3)区间；
     * 盈利率=（派彩金额-已结算注单的投注金额）/已结算注单的投注金额
     * 若某个参数不填，则所在区间方向不限制，例如只有参数2，则表示盈利率>=参数2；
     * 输出值：判断结果（1/0）；实际值（实际盈利率）"
     */
    @Override
    public RuleResult<String> r2(RuleParameterVo vo) {
        log.info("用户规则计算盈利比率范围{}", JSONObject.toJSONString(vo));
        //多少天内
        Long days = Long.valueOf(vo.getParameter1());
        //区间值  小
        BigDecimal min = ObjectUtils.isNotEmpty(vo.getParameter2()) ? new BigDecimal(vo.getParameter2()) : BigDecimal.valueOf(Long.MIN_VALUE);
        //区间值  大
        BigDecimal max = ObjectUtils.isNotEmpty(vo.getParameter3()) ? new BigDecimal(vo.getParameter3()) : BigDecimal.valueOf(Long.MAX_VALUE);
        //获取时间段
        Long endTime = vo.getTime();
        Long startTime = endTime - (days * ONE_DAY_MILL);
        //获取投注金额 和盈利金额
        FinancialRuleVo ruleVo = ruleDataService.getProfitAmount(vo.getUserId(), startTime,endTime);
        //计算盈利率
        BigDecimal profitRate = BigDecimal.ZERO;
        if (ObjectUtils.isEmpty(ruleVo)) {
            ruleVo = new FinancialRuleVo();
            log.info("用户{}规则计算盈利比率{}范围{}无数据", vo.getUserId(), min, max);
//            return RuleResult.init(false, BigDecimal.ZERO.multiply(new BigDecimal(100))+"%");
            ruleVo.setBetAmount(BigDecimal.ZERO);
            ruleVo.setProfitAmount(BigDecimal.ZERO);
        }else {
            //计算盈利率
            profitRate = ruleVo.getProfitAmount().divide(ruleVo.getBetAmount(), 4, BigDecimal.ROUND_DOWN);
        }

        //对比 是否满足范围
        boolean flag = false;
        if (profitRate.compareTo(min) >= 0 && profitRate.compareTo(max) <= 0) {
            flag = true;
        }
        log.info("用户{}规则计算盈利比率{}范围{}结果:{}", vo.getUserId(), min, max, flag);
        return RuleResult.init(flag, profitRate.multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_DOWN)+"%");
    }

    /**
     * R3	投注笔数标准	"一段时间（参数1天）内，用户成功投注笔数位于[参数2,参数3)区间；
     * * 若某个参数不填，则所在区间方向不限制，例如只有参数2，则表示成功投注笔数>=参数2；
     * * 输出值：判断结果（1/0）； "
     *
     * @param vo
     * @return
     */
    @Override
    public RuleResult<Long> r3(RuleParameterVo vo) {
        log.info("R3投注笔数标准{}", JSONObject.toJSONString(vo));
        //多少天内
        Long days = Long.valueOf(vo.getParameter1());
        //区间值  小
        BigDecimal min = ObjectUtils.isNotEmpty(vo.getParameter2()) ? new BigDecimal(vo.getParameter2()) : BigDecimal.valueOf(Long.MIN_VALUE);
        //区间值  大
        BigDecimal max = ObjectUtils.isNotEmpty(vo.getParameter3()) ? new BigDecimal(vo.getParameter3()) : BigDecimal.valueOf(Long.MAX_VALUE);
        //获取时间段
        Long endTime = vo.getTime();
        Long startTime = endTime - (days * ONE_DAY_MILL);
        //获取用户投注笔数
        Long userBetNum = ruleDataService.getUserSuccessBetNum(vo.getUserId(), startTime, endTime);
        //对比 是否满足范围
        boolean flag = false;
        if (userBetNum.compareTo(min.longValue()) >= 0 && userBetNum.compareTo(max.longValue()) <= 0) {
            flag = true;
        }
        log.info("用户{}R3投注笔数标准{}范围{}结果:{}", vo.getUserId(), min, max, flag);
        return RuleResult.init(flag, userBetNum);
    }

    /**
     * R4	投注金额标准	"一段时间（参数1天）内，用户成功投注金额位于[参数2,参数3)区间；
     * 若某个参数不填，则所在区间方向不限制，例如只有参数3，则表示成功投注金额<参数3；
     * 输出值：判断结果（1/0）；"
     *
     * @param vo
     * @return
     */
    @Override
    public RuleResult<BigDecimal> r4(RuleParameterVo vo) {
        log.info("规则计算 R4 投注金额标准 {}", JSONObject.toJSONString(vo));
        //多少天内
        Long days = Long.valueOf(vo.getParameter1());
        //区间值  小
        BigDecimal min = ObjectUtils.isNotEmpty(vo.getParameter2()) ? new BigDecimal(vo.getParameter2()) : BigDecimal.valueOf(Long.MIN_VALUE);
        //区间值  大
        BigDecimal max = ObjectUtils.isNotEmpty(vo.getParameter3()) ? new BigDecimal(vo.getParameter3()) : BigDecimal.valueOf(Long.MAX_VALUE);
        //获取时间段
        Long endTime = vo.getTime();
        Long startTime = endTime - (days * ONE_DAY_MILL);
        //获取投注金额
        UserBetAmountVo userBetAmount = ruleDataService.getUserBetAmount(vo.getUserId(), startTime, endTime);
        if (ObjectUtils.isEmpty(userBetAmount)) {
            userBetAmount = new UserBetAmountVo();
            log.info("用户{}R4 投注金额标{}范围{}无数据", vo.getUserId(), min, max);
            //return RuleResult.init(false, BigDecimal.ZERO);
            userBetAmount.setBetAmount(BigDecimal.ZERO);
        }
        //对比 是否满足范围
        boolean flag = false;
        if (userBetAmount.getBetAmount().compareTo(min) >= 0 && userBetAmount.getBetAmount().compareTo(max) <= 0) {
            flag = true;
        }
        log.info("用户{}R4 投注金额标{}范围{}结果:{}", vo.getUserId(), min, max, flag);
        return RuleResult.init(flag, userBetAmount.getBetAmount().setScale(2,BigDecimal.ROUND_DOWN));
    }


    /**
     * 访问特征类	R5	代理登录判断标准	"一段时间（参数1天）内，出现单日访问IP来自>=参数2个城市的情况
     * 输出值：判断结果（1/0）；实际值（城市数量）"
     *
     * @param vo
     * @return
     */
    @Override
    public RuleResult<Long> r5(RuleParameterVo vo) {
        Long days = Long.valueOf(vo.getParameter1());
        //获取时间段
        Long endTime = vo.getTime();
        Long startTime = endTime - (days * ONE_DAY_MILL);

        CityNumVo cityNumVo = ruleDataService.getCityNum(vo.getUserId(), startTime, endTime);
        if (ObjectUtils.isEmpty(cityNumVo)) {
            log.info("用户{}R5代理登录判断标准无数据", vo.getUserId());
            //return RuleResult.init(false, 0L);
            cityNumVo = new CityNumVo();
            cityNumVo.setCityNum(0L);
        }
        //对比 是否满足范围
        boolean flag = false;
        if (cityNumVo.getCityNum() > Long.valueOf(vo.getParameter2())) {
            flag = true;
        }
        return RuleResult.init(flag, cityNumVo.getCityNum());
    }

    /**
     * 访问特征类	R6	危险IP判断标准	"一段时间（参数1天）内，访问IP为危险IP的数量>=参数2
     * 输出值：判断结果（1/0）；实际值（实际危险IP数量）"
     *
     * @param vo
     * @return
     */
    @Override
    public RuleResult<Integer> r6(RuleParameterVo vo) {
        //获取时间段
        String startTime = LocalDateTimeUtil.format(LocalDateTime.now().plusDays(Long.valueOf(vo.getParameter1())));
        String endTime = LocalDateTimeUtil.format(LocalDateTime.now());
        //查询是否有危险ip
        LambdaQueryWrapper<RiskUserVisitIp> queryWrapper = new LambdaQueryWrapper<>();
        //1代表危险ip
        queryWrapper.eq(RiskUserVisitIp::getTagId, 1L);
        queryWrapper.eq(RiskUserVisitIp::getUserId, vo.getUserId());
        queryWrapper.ge(RiskUserVisitIp::getLoginDate, startTime);
        queryWrapper.le(RiskUserVisitIp::getLoginDate, endTime);
        List<RiskUserVisitIp> list = riskUserVisitIpService.list(queryWrapper);
        //判断危险ip个数是否满足
        if (ObjectUtils.isEmpty(list) || list.size() < Integer.valueOf(vo.getParameter2())) {
            return RuleResult.init(false, 0);
        }
        log.info("用户{}R27危险IP判断标准{}", vo.getUserId(), JSONObject.toJSON(list));
        return RuleResult.init(true, list.size());
    }

    /**
     * R7	一机多登判断标准	"一段时间（参数1天）内，出现同一个IP地址有>=参数2个账号登录的情况
     * 输出值：判断结果（1/0）；实际值（实际账号数量）""
     *
     * @param vo
     * @return
     */
    @Override
    public RuleResult<Integer> r7(RuleParameterVo vo) {
        //多少天内
        Long days = Long.valueOf(vo.getParameter1());
        //数量
        Long num = Long.valueOf(vo.getParameter2());
        //获取时间段
        Long endTime = vo.getTime();
        Long startTime = endTime - (days * ONE_DAY_MILL);
        //获取 出现多个账户的ip
        List<IpNumVo> ipNumVoList = ruleDataService.getIpNum(num, startTime, endTime);
        if (ObjectUtils.isEmpty(ipNumVoList)) {
            return RuleResult.init(false, 0);
        }
        log.info("超过{}个账号登录的IP:{}", vo.getParameter1(), ipNumVoList.size());

        //校验用户是否属于某个ip里面
        for (IpNumVo ipNumVo : ipNumVoList) {
            LambdaQueryWrapper<RiskUserVisitIp> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RiskUserVisitIp::getUserId, vo.getUserId());
            queryWrapper.eq(RiskUserVisitIp::getIp, ipNumVo.getIp());
            queryWrapper.ge(RiskUserVisitIp::getLoginDate, startTime);
            queryWrapper.le(RiskUserVisitIp::getLoginDate, endTime);
            List<RiskUserVisitIp> list = riskUserVisitIpService.list(queryWrapper);
            if (ObjectUtils.isNotEmpty(list)) {
                log.info("{}存在同ip多账户:{}", vo.getUserId(), JSONObject.toJSON(ipNumVo));
                return RuleResult.init(true, ipNumVo.getNum());
            }
        }
        return RuleResult.init(false, 0);
    }

    /**
     * R8	蛇单投注笔数标准	"一段时间（参数1天）内，标记为蛇单投注的注单笔数位于[参数2,参数3)区间
     * 若某个参数不填，则所在区间方向不限制，例如只有参数2，则表示蛇单投注笔数>=参数2；
     * 输出值：判断结果（1/0）；实际值（实际笔数）"
     *
     * @param vo
     * @return
     */
    @Override
    public RuleResult<Long> r8(RuleParameterVo vo) {
        log.info("规则计算 R8 蛇单投注笔数标准 {}", JSONObject.toJSONString(vo));
        //多少天内
        Long days = Long.valueOf(vo.getParameter1());
        //区间值  小
        Long min = ObjectUtils.isNotEmpty(vo.getParameter2()) ? Long.valueOf(vo.getParameter2()) : Long.MIN_VALUE;
        //区间值  大
        Long max = ObjectUtils.isNotEmpty(vo.getParameter3()) ? Long.valueOf(vo.getParameter3()) : Long.MAX_VALUE;
        //获取时间段
        Long endTime = vo.getTime();
        Long startTime = endTime - (days * ONE_DAY_MILL);
        //危险投注笔数 1-蛇单
        Long dangerousBetNum = ruleDataService.getUserDangerousBetNum(vo.getUserId(), startTime, endTime, DangerousEnum.SNAKE.getId());
        //对比 是否满足范围
        boolean flag = false;
        if (dangerousBetNum >= min && dangerousBetNum <= max) {
            flag = true;
        }
        log.info("用户{} R8 蛇单投注笔数标准{}范围{}结果:{}", vo.getUserId(), min, max, flag);
        return RuleResult.init(flag, dangerousBetNum);
    }

    /**
     * R9	资讯单投注笔数标准	"一段时间（参数1天）内，标记为资讯投注的注单笔数位于[参数2,参数3)区间
     * 若某个参数不填，则所在区间方向不限制，例如只有参数2，则表示资讯单投注笔数>=参数2；
     * 输出值：判断结果（1/0）；实际值（实际笔数）"
     *
     * @param vo
     * @return
     */
    @Override
    public RuleResult<Long> r9(RuleParameterVo vo) {
        log.info("规则计算 R9资讯单投注笔数标准 {}", JSONObject.toJSONString(vo));
        //多少天内
        Long days = Long.valueOf(vo.getParameter1());
        //区间值  小
        Long min = ObjectUtils.isNotEmpty(vo.getParameter2()) ? Long.valueOf(vo.getParameter2()) : Long.MIN_VALUE;
        //区间值  大
        Long max = ObjectUtils.isNotEmpty(vo.getParameter3()) ? Long.valueOf(vo.getParameter3()) : Long.MAX_VALUE;
        //获取时间段
        Long endTime = vo.getTime();
        Long startTime = endTime - (days * ONE_DAY_MILL);
        //危险投注笔数 3-资讯单
        Long dangerousBetNum = ruleDataService.getUserDangerousBetNum(vo.getUserId(), startTime, endTime, DangerousEnum.INFORMATION.getId());
        //对比 是否满足范围
        boolean flag = false;
        if (dangerousBetNum >= min && dangerousBetNum <= max) {
            flag = true;
        }
        log.info("用户{} R9资讯单投注笔数标准{}范围{}结果:{}", vo.getUserId(), min, max, flag);
        return RuleResult.init(flag, dangerousBetNum);
    }

    /**
     * R10 水单投注笔数标准	"一段时间（参数1天）内，标记为打水投注的注单笔数位于[参数2,参数3)区间
     * 若某个参数不填，则所在区间方向不限制，例如只有参数2，则表示水单投注笔数>=参数2；
     * 输出值：判断结果（1/0）；实际值（实际笔数）"
     *
     * @param vo
     * @return
     */
    @Override
    public RuleResult<Long> r10(RuleParameterVo vo) {
        log.info("规则计算 R10 水单投注笔数标准 {}", JSONObject.toJSONString(vo));
        //多少天内
        Long days = Long.valueOf(vo.getParameter1());
        //区间值  小
        Long min = ObjectUtils.isNotEmpty(vo.getParameter2()) ? Long.valueOf(vo.getParameter2()) : Long.MIN_VALUE;
        //区间值  大
        Long max = ObjectUtils.isNotEmpty(vo.getParameter3()) ? Long.valueOf(vo.getParameter3()) : Long.MAX_VALUE;
        //获取时间段
        Long endTime = vo.getTime();
        Long startTime = endTime - (days * ONE_DAY_MILL);
        //危险投注笔数 2-打水单
        Long dangerousBetNum = ruleDataService.getUserDangerousBetNum(vo.getUserId(), startTime, endTime, DangerousEnum.WATER.getId());
        //对比 是否满足范围
        boolean flag = false;
        if (dangerousBetNum >= min && dangerousBetNum <= max) {
            flag = true;
        }
        log.info("用户{} R10 水单投注笔数标准{}范围{}结果:{}", vo.getUserId(), min, max, flag);
        return RuleResult.init(flag, dangerousBetNum);
    }

    /**
     * R11	篮球打洞笔数标准	"一段时间（参数1天）内，标记为篮球打洞的注单笔数位于[参数2,参数3)区间
     * 若某个参数不填，则所在区间方向不限制，例如只有参数2，则表示篮球打洞投注笔数>=参数2；
     * 输出值：判断结果（1/0）；实际值（实际笔数）"
     *
     * @param vo
     * @return
     */
    @Override
    public RuleResult<Long> r11(RuleParameterVo vo) {
        log.info("规则计算 R11 篮球打洞笔数标准 {}", JSONObject.toJSONString(vo));
        //多少天内
        Long days = Long.valueOf(vo.getParameter1());
        //区间值  小
        Long min = ObjectUtils.isNotEmpty(vo.getParameter2()) ? Long.valueOf(vo.getParameter2()) : Long.MIN_VALUE;
        //区间值  大
        Long max = ObjectUtils.isNotEmpty(vo.getParameter3()) ? Long.valueOf(vo.getParameter3()) : Long.MAX_VALUE;
        //获取时间段
        Long endTime = vo.getTime();
        Long startTime = endTime - (days * ONE_DAY_MILL);
        //危险投注笔数 4-篮球打洞
        Long dangerousBetNum = ruleDataService.getUserDangerousBetNum(vo.getUserId(), startTime, endTime, DangerousEnum.BASKETBALL.getId());
        //对比 是否满足范围
        boolean flag = false;
        if (dangerousBetNum >= min && dangerousBetNum <= max) {
            flag = true;
        }
        log.info("用户{} R11 篮球打洞笔数标准{}范围{}结果:{}", vo.getUserId(), min, max, flag);
        return RuleResult.init(flag, dangerousBetNum);
    }

    /**
     * R12	蛇单投注比例标准	"一段时间（参数1天）内，蛇单投注笔数/总投注笔数位于[参数2,参数3)区间
     * 输出值：判断结果（1/0）；实际值（实际比例）"
     *
     * @param vo
     * @return
     */
    @Override
    public RuleResult<String> r12(RuleParameterVo vo) {
        log.info("规则计算 R12 蛇单投注比例标准{}", JSONObject.toJSONString(vo));
        //多少天内
        Long days = Long.valueOf(vo.getParameter1());
        //区间值  小
        BigDecimal min = ObjectUtils.isNotEmpty(vo.getParameter2()) ? new BigDecimal(vo.getParameter2()) : BigDecimal.valueOf(Long.MIN_VALUE);
        //区间值  大
        BigDecimal max = ObjectUtils.isNotEmpty(vo.getParameter3()) ? new BigDecimal(vo.getParameter3()) : BigDecimal.valueOf(Long.MAX_VALUE);
        //获取时间段
        Long endTime = vo.getTime();
        Long startTime = endTime - (days * ONE_DAY_MILL);
        //危险投注笔数 1-蛇蛋
        Long dangerousBetNum = ruleDataService.getUserDangerousBetNum(vo.getUserId(), startTime, endTime, DangerousEnum.SNAKE.getId());
        //获取总投注笔数
        Long userBetNum = ruleDataService.getUserBetNum(vo.getUserId(), startTime, endTime);
        if (dangerousBetNum == 0 || userBetNum == 0) {
            return RuleResult.init(false, "0.00%");
        }
        //计算比率
        BigDecimal rate = new BigDecimal(dangerousBetNum).divide(new BigDecimal(userBetNum), 4, BigDecimal.ROUND_DOWN);
        //对比 是否满足范围
        boolean flag = false;
        if (rate.compareTo(min) >= 0 && rate.compareTo(max) <= 0) {
            flag = true;
        }
        log.info("用户{} R12 蛇单投注比例标准{}范围{}结果:{}", vo.getUserId(), min, max, flag);
        return RuleResult.init(flag, rate.multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_DOWN)+"%");
    }

    /**
     * R13	资讯单投注比例标准	"一段时间（参数1天）内，资讯投注笔数/总投注笔数位于[参数2,参数3)区间
     * 输出值：判断结果（1/0）；实际值（实际比例）"
     *
     * @param vo
     * @return
     */
    @Override
    public RuleResult<String> r13(RuleParameterVo vo) {
        log.info("规则计算 R13 资讯单投注比例标准{}", JSONObject.toJSONString(vo));
        //多少天内
        Long days = Long.valueOf(vo.getParameter1());
        //区间值  小
        BigDecimal min = ObjectUtils.isNotEmpty(vo.getParameter2()) ? new BigDecimal(vo.getParameter2()) : BigDecimal.valueOf(Long.MIN_VALUE);
        //区间值  大
        BigDecimal max = ObjectUtils.isNotEmpty(vo.getParameter3()) ? new BigDecimal(vo.getParameter3()) : BigDecimal.valueOf(Long.MAX_VALUE);
        //获取时间段
        Long endTime = vo.getTime();
        Long startTime = endTime - (days * ONE_DAY_MILL);
        //危险投注笔数 3-咨询单
        Long dangerousBetNum = ruleDataService.getUserDangerousBetNum(vo.getUserId(), startTime, endTime, DangerousEnum.INFORMATION.getId());
        //获取总投注笔数
        Long userBetNum = ruleDataService.getUserBetNum(vo.getUserId(), startTime, endTime);
        if (dangerousBetNum == 0 || userBetNum == 0) {
            return RuleResult.init(false, "0.0%");
        }
        //计算比率
        BigDecimal rate = new BigDecimal(dangerousBetNum).divide(new BigDecimal(userBetNum), 4, BigDecimal.ROUND_DOWN);
        //对比 是否满足范围
        boolean flag = false;
        if (rate.compareTo(min) >= 0 && rate.compareTo(max) <= 0) {
            flag = true;
        }
        log.info("用户{} R13 资讯单投注比例标准{}范围{}结果:{}", vo.getUserId(), min, max, flag);
        return RuleResult.init(flag, rate.multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_DOWN)+"%");
    }

    /**
     * R14	水单投注比例标准	"一段时间（参数1天）内，打水投注笔数/总投注笔数位于[参数2,参数3)区间
     * 输出值：判断结果（1/0）；实际值（实际比例）"
     *
     * @param vo
     * @return
     */
    @Override
    public RuleResult<String> r14(RuleParameterVo vo) {
        log.info("规则计算 R14 水单投注比例标准{}", JSONObject.toJSONString(vo));
        //多少天内
        Long days = Long.valueOf(vo.getParameter1());
        //区间值  小
        BigDecimal min = ObjectUtils.isNotEmpty(vo.getParameter2()) ? new BigDecimal(vo.getParameter2()) : BigDecimal.valueOf(Long.MIN_VALUE);
        //区间值  大
        BigDecimal max = ObjectUtils.isNotEmpty(vo.getParameter3()) ? new BigDecimal(vo.getParameter3()) : BigDecimal.valueOf(Long.MAX_VALUE);
        //获取时间段
        Long endTime = vo.getTime();
        Long startTime = endTime - (days * ONE_DAY_MILL);
        //危险投注笔数 2-水单
        Long dangerousBetNum = ruleDataService.getUserDangerousBetNum(vo.getUserId(), startTime, endTime, DangerousEnum.WATER.getId());
        //获取总投注笔数
        Long userBetNum = ruleDataService.getUserBetNum(vo.getUserId(), startTime, endTime);
        if (dangerousBetNum == 0 || userBetNum == 0) {
            return RuleResult.init(false, "0.00%");
        }
        //计算比率
        BigDecimal rate = new BigDecimal(dangerousBetNum).divide(new BigDecimal(userBetNum), 4, BigDecimal.ROUND_DOWN);
        //对比 是否满足范围
        boolean flag = false;
        if (rate.compareTo(min) >= 0 && rate.compareTo(max) <= 0) {
            flag = true;
        }
        log.info("用户{} R14 水单投注比例标准{}范围{}结果:{}", vo.getUserId(), min, max, flag);
        return RuleResult.init(flag, rate.multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_DOWN)+"%");
    }

    /**
     * R15	篮球打洞比例标准	"一段时间（参数1天）内，篮球打洞投注笔数/总投注笔数位于[参数2,参数3)区间
     * 输出值：判断结果（1/0）；实际值（实际比例）"
     *
     * @param vo
     * @return
     */
    @Override
    public RuleResult<String> r15(RuleParameterVo vo) {
        log.info("规则计算 R15 篮球打洞比例标准{}", JSONObject.toJSONString(vo));
        //多少天内
        Long days = Long.valueOf(vo.getParameter1());
        //区间值  小
        BigDecimal min = ObjectUtils.isNotEmpty(vo.getParameter2()) ? new BigDecimal(vo.getParameter2()) : BigDecimal.valueOf(Long.MIN_VALUE);
        //区间值  大
        BigDecimal max = ObjectUtils.isNotEmpty(vo.getParameter3()) ? new BigDecimal(vo.getParameter3()) : BigDecimal.valueOf(Long.MAX_VALUE);
        //危险投注笔数 4-篮球打洞
        //获取时间段
        Long endTime = vo.getTime();
        Long startTime = endTime - (days * ONE_DAY_MILL);
        Long dangerousBetNum = ruleDataService.getUserDangerousBetNum(vo.getUserId(), startTime, endTime, DangerousEnum.BASKETBALL.getId());
        //获取总投注笔数
        Long userBetNum = ruleDataService.getUserBetNum(vo.getUserId(), startTime, endTime);
        if (dangerousBetNum == 0 || userBetNum == 0) {
            return RuleResult.init(false, "0.00%");
        }
        //计算比率
        BigDecimal rate = new BigDecimal(dangerousBetNum).divide(new BigDecimal(userBetNum), 4, BigDecimal.ROUND_DOWN);
        //对比 是否满足范围
        boolean flag = false;
        if (rate.compareTo(min) >= 0 && rate.compareTo(max) <= 0) {
            flag = true;
        }
        log.info("用户{} R15 篮球打洞比例标准{}范围{}结果:{}", vo.getUserId(), min, max, flag);
        return RuleResult.init(flag, rate.multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_DOWN)+"%");
    }

    /**
     * R16	投注赛种比例	"一段时间（参数1天）内，某一赛种投注笔数/总投注笔数位于[参数2,参数3)区间
     * 注：统计所有状态的注单
     * 输出值：判断结果（1/0）；实际值（实际比例）"
     *
     * @param vo
     * @return
     */
    @Override
    public RuleResult<String> r16(RuleParameterVo vo) {
        //多少天内
        Long days = Long.valueOf(vo.getParameter1());
        //区间值  小
        BigDecimal min = ObjectUtils.isNotEmpty(vo.getParameter2()) ? new BigDecimal(vo.getParameter2()) : BigDecimal.valueOf(Long.MIN_VALUE);
        //区间值  大
        BigDecimal max = ObjectUtils.isNotEmpty(vo.getParameter3()) ? new BigDecimal(vo.getParameter3()) : BigDecimal.valueOf(Long.MAX_VALUE);
        //获取时间段
        Long endTime = vo.getTime();
        Long startTime = endTime - (days * ONE_DAY_MILL);
        //获取投注球类笔数
        List<SportBetNumVo> list = ruleDataService.getSportBetNum(vo.getUserId(), startTime, endTime);
        log.info("r16所有数据{}", JSONObject.toJSON(list));
        if (ObjectUtils.isEmpty(list)) {
            return RuleResult.init(false, "no data");
        }
        //获取总投注笔数
        Long userBetNum = ruleDataService.getUserBetNum(vo.getUserId(), startTime, endTime);
        boolean flag = false;
        String result = "";
        for (SportBetNumVo sportBetNumVo : list) {
            if (sportBetNumVo == null) {
                continue;
            }
            //计算占比
            BigDecimal rate = new BigDecimal(sportBetNumVo.getBetNum()).divide(new BigDecimal(userBetNum), 4, BigDecimal.ROUND_DOWN)
                    .multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_DOWN);
            //对比 是否满足范围  只要其中一个满足就可以
            if (!flag && rate.compareTo(min) >= 0 && rate.compareTo(max) <= 0) {
                flag = true;
            }
            if (rate.compareTo(min) >= 0 && rate.compareTo(max) <= 0) {
                result += String.format("赛种:%s-%s  | ", languageService.getSportName(sportBetNumVo.getSportId()), rate.setScale(2,BigDecimal.ROUND_DOWN)+"%");
            }

        }
        return RuleResult.init(flag, result);
    }

    /**
     * 投注特征类	R17	投注联赛比例	"一段时间（参数1天）内，某一联赛投注笔数/总投注笔数位于[参数2,参数3)区间
     * 注：统计所有状态的注单
     * 输出值：判断结果（1/0）；实际值（实际比例）"
     *
     * @param vo
     * @return
     */
    @Override
    public RuleResult<String> r17(RuleParameterVo vo) {
        //多少天内
        Long days = Long.valueOf(vo.getParameter1());
        //区间值  小
        BigDecimal min = ObjectUtils.isNotEmpty(vo.getParameter2()) ? new BigDecimal(vo.getParameter2()) : BigDecimal.valueOf(Long.MIN_VALUE);
        //区间值  大
        BigDecimal max = ObjectUtils.isNotEmpty(vo.getParameter3()) ? new BigDecimal(vo.getParameter3()) : BigDecimal.valueOf(Long.MAX_VALUE);
        //获取时间段
        Long endTime = vo.getTime();
        Long startTime = endTime - (days * ONE_DAY_MILL);
        //获取投注金额最高的联赛
        List<TournamentBetNumVo> list = ruleDataService.getTournamentBetNum(vo.getUserId(), startTime, endTime);
        log.info("r17所有数据{}", JSONObject.toJSON(list));
        if (ObjectUtils.isEmpty(list)) {
            return RuleResult.init(false, "no data");
        }
        //获取总投注笔数
        Long userBetNum = ruleDataService.getUserBetNum(vo.getUserId(), startTime, endTime);

        boolean flag = false;
        String result = "";
        for (TournamentBetNumVo tournamentBetNumVo : list) {
            //计算占比
            BigDecimal rate = new BigDecimal(tournamentBetNumVo.getBetNum()).multiply(new BigDecimal("100").divide(new BigDecimal(userBetNum), 2, BigDecimal.ROUND_DOWN));
            //对比 是否满足范围
            if (!flag && rate.compareTo(min) >= 0 && rate.compareTo(max) <= 0) {
                flag = true;
            }
            if (rate.compareTo(BigDecimal.ZERO) != 0) {
                result += String.format("联赛:%s-%s  | ", tournamentBetNumVo.getTournamentName(), rate.setScale(2,BigDecimal.ROUND_DOWN) + "%");
            }
        }
        return RuleResult.init(flag, result);
    }

    /***
     * R18	满额注单笔数	"一段时间（参数1天）内，满额注单笔数达到参数2
     * 注：统计所有状态的注单
     * 输出值：判断结果（1/0）；实际值（实际笔数）"
     *
     * @param vo*/
    @Override
    public RuleResult<Long> r18(RuleParameterVo vo) {
        try {
            //多少天内
            Long days = Long.valueOf(vo.getParameter1());
            //范围
            int min = Integer.parseInt(vo.getParameter2());
            //获取时间段
            Long endTime = vo.getTime();
            Long startTime = endTime - (days * ONE_DAY_MILL);
            Long fullBetNum = ruleDataService.getFullBetNum(vo.getUserId(), startTime, endTime);
            if (fullBetNum >= min) {
                return RuleResult.init(true, fullBetNum);
            }
        } catch (Exception e) {
            log.error("规则R18处理失败", e);
        }
        return RuleResult.init(false, 0L);
    }

    /**
     * R19	满额注单比例	"一段时间（参数1天）内，满额注单笔数/总投注笔数达到参数2
     * 注：统计所有状态的注单
     * 输出值：判断结果（1/0）；实际值（实际比例）"
     *
     * @param vo
     */
    @Override
    public RuleResult<String> r19(RuleParameterVo vo) {
        try {
            //多少天内
            Long days = Long.valueOf(vo.getParameter1());
            //范围
            BigDecimal min = new BigDecimal(vo.getParameter2());
            //获取时间段
            Long endTime = vo.getTime();
            Long startTime = endTime - (days * ONE_DAY_MILL);
            //满额笔数
            Long fullBetNum = ruleDataService.getFullBetNum(vo.getUserId(), startTime, endTime);
            //获取总投注笔数
            Long userBetNum = ruleDataService.getUserBetNum(vo.getUserId(), startTime, endTime);
            //比率
            BigDecimal rate = new BigDecimal(fullBetNum).divide(new BigDecimal(userBetNum), 4, BigDecimal.ROUND_DOWN);
            if (rate.compareTo(min) > 0) {
                return RuleResult.init(true, rate.multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_DOWN)+"%");
            }
        } catch (Exception e) {
            log.error("规则R19处理失败", e);
        }
        return RuleResult.init(false, BigDecimal.ZERO.multiply(new BigDecimal(100))+"%");
    }


    /***
     * 大额注单笔数.
     * 一段时间（参数1天）内，达到指定金额（参数2）的注单笔数>=参数3
     * 注：统计所有状态的注单
     * 输出值：判断结果（1/0）；实际值（实际笔数）
     * @param vo   规则使用的参数
     * @return com.panda.sport.rcs.common.bean.RuleResult<java.math.BigDecimal>
     * @Description
     * @Author dorich
     * @Date 9:34 2020/7/11
     **/
    @Override
    public RuleResult<BigDecimal> r20(RuleParameterVo vo) {
        try {
            int numbers = Integer.parseInt(vo.getParameter3());
            BetAmountOrderVo betAmountOrder = ruleDataService.getLargeAmountBetOrders(vo);
            if (ObjectUtils.isNotEmpty(betAmountOrder) && betAmountOrder.getBetNumbers() >= numbers) {
                return RuleResult.init(true, new BigDecimal(betAmountOrder.getBetNumbers()).setScale(2,BigDecimal.ROUND_DOWN));
            }
        } catch (Exception e) {
            log.error("规则R20处理失败", e);
        }
        return RuleResult.init(false, BigDecimal.ZERO);
    }

    /***
     * 大额注单比例
     * 一段时间（参数1天）内，达到指定金额（参数2）的注单笔数/总投注笔数>=参数3
     * 注：统计所有状态的注单
     * 输出值：判断结果（1/0）；实际值（实际比例）
     * @param vo
     * @return com.panda.sport.rcs.common.bean.RuleResult<java.math.BigDecimal>
     * @Description
     * @Author dorich
     * @Date 9:35 2020/7/11
     **/
    @Override
    public RuleResult<String> r21(RuleParameterVo vo) {
        BigDecimal resultRate = BigDecimal.ZERO;
        try {
            BigDecimal rate = new BigDecimal(vo.getParameter3());
            BetAmountOrderVo betAmountOrder = ruleDataService.getLargeAmountBetOrders(vo);
            if (betAmountOrder == null || betAmountOrder.getBetNumbersAll()==null) {
                return RuleResult.init(false, "0.00%");
            }
            /*** 满足要求的  订单个数/ 订单总个数  >= 比例 等价于     订单个数 >= 比例 * 订单总个数 . 但是计算机除法没有小数,因此直接使用乘法 ***/
            if (betAmountOrder.getBetNumbersAll() > 0) {
                resultRate = BigDecimal.valueOf(betAmountOrder.getBetNumbers()).divide(BigDecimal.valueOf(betAmountOrder.getBetNumbersAll()), 4, BigDecimal.ROUND_DOWN);
            }
            if (resultRate.compareTo(rate) > 0) {
                return RuleResult.init(true, resultRate.multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_DOWN)+"%");
            }
        } catch (Exception e) {
            log.error("规则R20处理失败", e);
        }
        return RuleResult.init(false, resultRate.multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_DOWN)+"%");
    }

    /**
     * "一段时间（参数1天）内，某一玩法投注笔数/总投注笔数位于[参数2,参数3)区间
     * 注：仅统计成功状态的注单
     * 判断结果（1/0）；输出值：（赛种）玩法1-实际比例 | （赛种）玩法2-实际比例……
     * 注：输出的实际值，仅输出达到规则条件的玩法"
     *
     * @param vo
     * @return
     */
    @Override
    public RuleResult<String> r22(RuleParameterVo vo) {
        //多少天内
        Long days = Long.valueOf(vo.getParameter1());
        //区间值  小
        BigDecimal min = ObjectUtils.isNotEmpty(vo.getParameter2()) ? new BigDecimal(vo.getParameter2()) : BigDecimal.valueOf(Long.MIN_VALUE);
        //区间值  大
        BigDecimal max = ObjectUtils.isNotEmpty(vo.getParameter3()) ? new BigDecimal(vo.getParameter3()) : BigDecimal.valueOf(Long.MAX_VALUE);
        //获取时间段
        Long endTime = vo.getTime();
        Long startTime = endTime - (days * ONE_DAY_MILL);
        //获取玩法投注笔数
        List<PlayBetNumVo> list = ruleDataService.getPlayBetNum(vo.getUserId(), startTime, endTime);
        log.info("r22所有数据{}", JSONObject.toJSON(list));
        if (ObjectUtils.isEmpty(list)) {
            return RuleResult.init(false, "no data");
        }
        //获取总投注笔数
        Long userBetNum = ruleDataService.getUserBetNum(vo.getUserId(), startTime, endTime);

        boolean flag = false;
        String result = "";
        for (PlayBetNumVo playBetNumVo : list) {
            //计算占比
            BigDecimal rate = new BigDecimal(playBetNumVo.getBetNum()).divide(new BigDecimal(userBetNum), 4, BigDecimal.ROUND_DOWN);
            //rate = rate.multiply(new BigDecimal("100"));
            //对比 是否满足范围
            if (!flag && rate.compareTo(min) >= 0 && rate.compareTo(max) <= 0) {
                flag = true;
            }
            result += String.format("（%s）%s-%s | ", languageService.getSportName(playBetNumVo.getSportId()),
                    languageService.getPlayName(playBetNumVo.getSportId(), playBetNumVo.getPlayId()), rate.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN) + "%");
        }
        return RuleResult.init(flag, result);
    }


    /**
     * "一段时间（参数1天）内，在位于[参数2，参数3)的联赛级别的盈利赛事（数量记为x）中，盈利金额除以“赛事联赛模板中用户单场限额”位于[参数4，参数5)内的赛事数量/x>=参数6
     * 判断结果（1/0）；输出值：参数1-(参数2-1)级联赛，参数4<=盈利金额/用户单场限额<参数5的赛事占全部盈利赛事的比例 = 实际比例值
     * 例如：1-5级联赛，盈利金额/用户单场限额<50%的盈利赛事占全部盈利赛事的比例 = 90%
     * 6级以下联赛，盈利金额/用户单场限额<50%的盈利赛事占全部盈利赛事的比例 = 90%    （参数3不填）
     * 5级以上联赛，盈利金额/用户单场限额<50%的盈利赛事占全部盈利赛事的比例 = 90%   （参数2不填）"
     *
     * @param vo
     * @return
     */
    @Override
    public RuleResult<String> r23(RuleParameterVo vo) {
        //多少天内
        Long days = Long.valueOf(vo.getParameter1());
        //联赛等级区间值  小
        Integer minLevel = ObjectUtils.isNotEmpty(vo.getParameter2()) ? Integer.valueOf(vo.getParameter2()) : 0;
        //联赛等级区间值  大
        Integer maxLevel = ObjectUtils.isNotEmpty(vo.getParameter3()) ? Integer.valueOf(vo.getParameter3()) : 21;
        //比率区间值  小
        BigDecimal minRate = ObjectUtils.isNotEmpty(vo.getParameter4()) ? new BigDecimal(vo.getParameter4()) : BigDecimal.valueOf(0);
        //比率等级区间值  大
        BigDecimal maxRate = ObjectUtils.isNotEmpty(vo.getParameter5()) ? new BigDecimal(vo.getParameter5()) : BigDecimal.valueOf(Long.MAX_VALUE);
        //参考值
        BigDecimal lastRate = ObjectUtils.isNotEmpty(vo.getParameter6()) ? new BigDecimal(vo.getParameter6()) : BigDecimal.valueOf(Long.MAX_VALUE);
        //获取时间段
        Long endTime = vo.getTime();
        Long startTime = endTime - (days * ONE_DAY_MILL);

        //获取 用户单场盈利
        List<MatchProfitNumVo> list = ruleDataService.getMatchProfitNum(vo.getUserId(), startTime, endTime, minLevel, maxLevel);
        //盈利赛事数量
        Integer winMatchNum = list.size();
        //盈利金额除以“赛事联赛模板中用户单场限额”位于[参数4，参数5)内的赛事数量/x>=参数6
        Integer winBetweenNum = 0;
        for (MatchProfitNumVo matchProfitNumVo : list) {
            if(matchProfitNumVo.getMatchType()!=2){
                matchProfitNumVo.setMatchType(1);
            }
            //联赛末班中的用户单场限额
            Long templateLimit = getMatchUserSingleLimt(matchProfitNumVo.getSportId().intValue(), matchProfitNumVo.getTournamentLevel().intValue(), matchProfitNumVo.getMatchId(), matchProfitNumVo.getMatchType())/100;
            BigDecimal rate = new BigDecimal(matchProfitNumVo.getProfitAmount()).divide(new BigDecimal(templateLimit), 4, BigDecimal.ROUND_DOWN);
            if (rate.compareTo(minRate) >= 0 && rate.compareTo(maxRate) < 0) {
                winBetweenNum++;
            }
        }
        BigDecimal resultRate = winMatchNum == 0 ? BigDecimal.ZERO : new BigDecimal(winBetweenNum).divide(new BigDecimal(winMatchNum), 4, BigDecimal.ROUND_DOWN);
        //.multiply(new BigDecimal("100"))
        String resultMsg = String.format("%s-%s级联赛,", minLevel, maxLevel);
        if (minLevel == Integer.MIN_VALUE) {
            resultMsg = String.format("%s级以下联赛,", maxLevel);
        } else if (maxLevel == Integer.MAX_VALUE) {
            resultMsg = String.format("%s级以上联赛,", minLevel);
        }
        resultMsg += String.format("盈利金额/用户单场限额 在%s-%s范围内的 盈利赛事占全部盈利赛事的比例 = %s", minRate, maxRate, resultRate.multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_DOWN)+"%");
        if (resultRate.compareTo(lastRate) >= 0) {
            return RuleResult.init(true, resultMsg);
        }
        return RuleResult.init(false, resultMsg);
    }

    /**
     * 后去早盘滚球限额配置
     *
     * @param sportId
     * @param tournamentLevel
     * @param matchId
     * @return
     */
    private Long getMatchUserSingleLimt(Integer sportId, Integer tournamentLevel, Long matchId, Integer matchType) {
        final String RCS_MATCH_USER_SINGLE_LIMIT_KEY = "rcs_portrait_match_user_single_limit";
        final String RCS_MATCH_USER_SINGLE_LIMIT_MATCH = "match_id.%s.match_type.%s";
        String key = String.format(RCS_MATCH_USER_SINGLE_LIMIT_MATCH, matchId, matchType);
        long t1 = System.currentTimeMillis();

        String limitStr = redisService.getString(RCS_MATCH_USER_SINGLE_LIMIT_KEY + key);
        if (!StringUtils.isBlank(limitStr)) {
            Long limit = Long.valueOf(limitStr);
            long t2 = System.currentTimeMillis();
            log.info("获取赛事用户单场限额直接缓存读取完成{},{},耗时{}", matchId, limit, (t2 - t1));
            return limit;
        } else {
            MatchLimitDataReqVo matchLimitDataReqVo = new MatchLimitDataReqVo();
            matchLimitDataReqVo.setMatchId(matchId);
            matchLimitDataReqVo.setTournamentLevel(tournamentLevel);
            matchLimitDataReqVo.setSportId(sportId);
            List<Integer> list = new ArrayList<>();
            list.add(3);
            matchLimitDataReqVo.setDataTypeList(list);
            producerSendMessageUtils.sendMessage("rcs_match_user_single_limit:" + matchId, JSONObject.toJSONString(matchLimitDataReqVo));
            log.info("获取赛事用户单场限额 缓存未读取到,发送rcs_match_user_single_limit去limit读取" + JSONObject.toJSONString(matchLimitDataReqVo));
        }

        try {
            while (true) {
                long t2 = System.currentTimeMillis();
                if ((t2 - t1) > 4000) {
                    log.info("获取赛事用户单场限额超时{},{},耗时{}", matchId, Long.MAX_VALUE, (t2 - t1));
                    return Long.MAX_VALUE;
                }
                limitStr = redisService.getString(RCS_MATCH_USER_SINGLE_LIMIT_KEY + key);
                if (!StringUtils.isBlank(limitStr)) {
                    BigDecimal limit = new BigDecimal(limitStr);
                    log.info("获取赛事用户单场限额 重新 缓存读取完成{},{},耗时{}", matchId, limit, (t2 - t1));
                    return limit.longValue();
                }
                Thread.sleep(100);
            }
        } catch (Exception e) {
            log.info("获取赛事用户单场限额异常{},{}", e.getMessage(), e);
        }
        log.info("获取赛事用户单场限额异常,返回默认值" + matchId);
        return Long.MAX_VALUE;
    }

    /**
     * "一段时间（参数1天）内，用户成功投注的赛事数量位于[参数2,参数3)区间；
     * 判断结果（1/0）；输出值：实际场次数量
     * 注：若参数4为空，输出的实际值中不包含“赛种-”"
     *
     * @param vo
     * @return
     */
    @Override
    public RuleResult<String> r24(RuleParameterVo vo) {
        //多少天内
        Long days = Long.valueOf(vo.getParameter1());
        //区间值  小
        BigDecimal min = ObjectUtils.isNotEmpty(vo.getParameter2()) ? new BigDecimal(vo.getParameter2()) : BigDecimal.valueOf(Long.MIN_VALUE);
        //区间值  大
        BigDecimal max = ObjectUtils.isNotEmpty(vo.getParameter3()) ? new BigDecimal(vo.getParameter3()) : BigDecimal.valueOf(Long.MAX_VALUE);
        //获取时间段
        Long endTime = vo.getTime();
        Long startTime = endTime - (days * ONE_DAY_MILL);
        //获取投注笔数
        Long times = ruleDataService.getMatchBetNum(vo.getUserId(), startTime, endTime);
        log.info("r24所有数据{}", times);
        boolean flag = false;
        //对比 是否满足范围
        if (times >= min.longValue() && times < max.longValue()) {
            flag = true;
        }
        return RuleResult.init(flag, times.toString());
    }

}
