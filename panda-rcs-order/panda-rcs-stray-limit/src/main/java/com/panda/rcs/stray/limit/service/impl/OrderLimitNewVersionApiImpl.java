package com.panda.rcs.stray.limit.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.panda.rcs.stray.limit.entity.constant.NumberConstant;
import com.panda.rcs.stray.limit.entity.constant.RedisKeyConstant;
import com.panda.rcs.stray.limit.entity.vo.*;
import com.panda.rcs.stray.limit.enums.SeriesTypeEnum;
import com.panda.rcs.stray.limit.service.*;
import com.panda.rcs.stray.limit.utils.BaseUtils;
import com.panda.rcs.stray.limit.utils.RedisUtils;
import com.panda.rcs.stray.limit.wrapper.CommonService;
import com.panda.sport.data.rcs.api.OrderLimitNewVersionApi;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.limit.LimitApiService;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.limit.*;
import com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.enums.LNBasktballEnum;
import com.panda.sport.rcs.enums.RedisCmdEnum;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.pojo.limit.RcsBusinessPlayPaidConfigVo;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import com.panda.sport.rcs.utils.SeriesTypeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.MDC;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class OrderLimitNewVersionApiImpl implements OrderLimitNewVersionApi {

    @Reference(check = false, lazy = true, retries = 1, timeout = 3000)
    private LimitApiService limitApiService;
    private final RedisUtils redisUtils;
    private final IRcsMerchantHighRiskLimitService iRcsMerchantHighRiskLimitService;
    private final IRcsMerchantSingleLimitService iRcsMerchantSingleLimitService;
    private final IRcsMerchantSeriesConfigService iRcsMerchantSeriesConfigService;
    private final IRcsMerchantLimitCompensationService iRcsMerchantLimitCompensationService;
    private final IRcsMerchantSportLimitService iRcsMerchantSportLimitService;
    private final IRcsMerchantLowLimitService iRcsMerchantLowLimitService;
    private final BaseService baseService;
    private final CommonService commonService;
    private final RcsMerchantIntervalService rcsMerchantIntervalService;

    /**
     * 查询限额信息入口
     *
     * @param request 订单信息
     * @return 限额信息
     */
    @Override
    public Response queryMaxBetAmountByOrder(Request<OrderBean> request) {
        OrderBean orderBean = request.getData();
        String globalId = request.getGlobalId();
        MDC.put("X-B3-TraceId", globalId);
        Map<Integer, BigDecimal> oddsHighRisk = BaseUtils.calTotalEuOdds(orderBean.getItems());
        //返回业务的数据对象
        List<RcsBusinessPlayPaidConfigVo> rcsBusinessPlayPaidConfigVos = new ArrayList<>();
        //获取串关类型组合，比如2001 3001 3004...
        Map<Integer, Integer> tempMap = BaseUtils.getEmptyArray(orderBean.getItems().size());
        //获取总欧赔
        //订单扩展对象
        List<ExtendBean> extendBeanList = this.getExtendBean(orderBean);
        //查询商户信息
        RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimitResVo = this.getBusinessLimit(orderBean.getTenantId());
        //获取当前日期
        String dateExpect = DateUtils.DateToString(new Date());
        //检查商户今日串关限额是否为空
        boolean isMerchantCheck = checkBusinessTodayAmount(orderBean, dateExpect, rcsQuotaBusinessLimitResVo);
        //检查用户单场和今日玩法赔付
        boolean isUserTodayAmount = checkUserSingeAmount(orderBean, extendBeanList, rcsQuotaBusinessLimitResVo);
        if (!isMerchantCheck || !isUserTodayAmount) {
            for (Integer seriesType : tempMap.keySet()) {
                rcsBusinessPlayPaidConfigVos.add(this.returnData(String.valueOf(seriesType), NumberConstant.LONG_ZERO));
            }
            return Response.success(rcsBusinessPlayPaidConfigVos);
        }

        //串关总赔付限额
        BigDecimal strayTotal = this.getStrayTotal(orderBean, dateExpect, rcsQuotaBusinessLimitResVo);
        log.info("::{}::2.0串关获取总赔付限额:{}", globalId, strayTotal);
        //串关单日赛种赔付限额
        BigDecimal straySingleDay = this.getStraySingleDay(orderBean, dateExpect, rcsQuotaBusinessLimitResVo);
        log.info("::{}::2.0串关获取单日赛种限额:{}", globalId, straySingleDay);
        Integer seriesNum = SeriesTypeUtils.getSeriesType(orderBean.getSeriesType());
        //高风险串关类型赔付限额
        BigDecimal highRiskStraySingleDay = this.getHighRiskStraySingleDay(orderBean, seriesNum);
        log.info("::{}::2.0串关获取高风险配置限额:{}", globalId, highRiskStraySingleDay);
        //串关单日类型赔付限额
        BigDecimal straySingleDaySeries = this.getStraySingleDaySeries(orderBean, seriesNum, dateExpect, rcsQuotaBusinessLimitResVo);
        log.info("::{}::2.0串关获取串关类型配置限额:{}", globalId, straySingleDaySeries);
        List<BigDecimal> exchangeList = Arrays.asList(highRiskStraySingleDay, strayTotal, straySingleDay, straySingleDaySeries);
        //以球种进行分组
        Map<String, List<ExtendBean>> sportMap = extendBeanList.stream().collect(Collectors.groupingBy(ExtendBean::getSportId));
        //查询高风险区间信息 如果是多球种则默认取其他高风险配置
        Integer sportId = sportMap.size() > 1 ? -1 : Integer.valueOf(extendBeanList.get(0).getSportId());
        log.info("::{}::2.0串关高风险查询球种类型:{}", globalId, sportId);
        for (Map.Entry<Integer, BigDecimal> entry : oddsHighRisk.entrySet()) {
            Integer seriesType = entry.getKey();
            BigDecimal oddsAmount = entry.getValue();
            boolean highRisk = isHighRisk(seriesType, oddsAmount, sportId);
            log.info("::{}::2.0串关类型:{},总欧赔:{},是否高风险:{}", globalId, seriesType, oddsAmount, highRisk);
            //处于高风险区域
            if (highRisk) {
                BigDecimal item = exchangeList.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
                //计算P0 最小投注额/总欧赔-1
                BigDecimal minPay = item.divide(oddsAmount.subtract(BigDecimal.ONE), NumberConstant.NUM_TWO, RoundingMode.HALF_DOWN);
                //用计算后的值和最大赔付配置比较，取最小
                RcsMerchantInterval rcsMerchantInterval = this.getMinMerchantLimit(orderBean.getItems(), seriesType);
                log.info("::{}::高风险最大赔付金额:{},计算后金额:{}::", globalId, rcsMerchantInterval.getMaxIntervalAmount(), minPay);
                BigDecimal tempVal = this.min(minPay, rcsMerchantInterval.getMaxIntervalAmount());
                List<Integer> userSingleStrayList = this.getUserSingleAmount(orderBean, extendBeanList, rcsQuotaBusinessLimitResVo);
                rcsBusinessPlayPaidConfigVos.add(userSingleStrayList.contains(NumberConstant.NUM_ZERO) ? this.returnData(String.valueOf(seriesType), NumberConstant.LONG_ZERO) : this.returnData(String.valueOf(seriesType), tempVal.longValue()));
            } else {
                RcsMerchantLowLimit rcsMerchantLowLimit = iRcsMerchantLowLimitService.queryByStrayType(seriesType);
                BigDecimal lowRiskPay = highRiskStraySingleDay.divide(oddsAmount.subtract(BigDecimal.ONE), NumberConstant.NUM_TWO, RoundingMode.HALF_DOWN);
                lowRiskPay = this.max(lowRiskPay, rcsMerchantLowLimit.getMinAmount());
                log.info("::{}::低风险最大赔付金额:{},单日额度用完最低可投注金额:{}::", globalId, lowRiskPay, rcsMerchantLowLimit.getMinAmount());
                RcsBusinessPlayPaidConfigVo rcsBusinessPlayPaidConfigVo = strayTotal.compareTo(BigDecimal.ZERO) > NumberConstant.NUM_ZERO &&
                        straySingleDaySeries.compareTo(BigDecimal.ZERO) > NumberConstant.NUM_ZERO &&
                        straySingleDay.compareTo(BigDecimal.ZERO) > NumberConstant.NUM_ZERO ?
                        this.returnData(String.valueOf(seriesType), lowRiskPay.longValue()) :
                        this.returnData(String.valueOf(seriesType), rcsMerchantLowLimit.getMinAmount().longValue());

                BigDecimal bigDecimal = handlerSpecialUser(orderBean, lowRiskPay, strayTotal, highRiskStraySingleDay, oddsAmount, globalId);
                log.info("::{}::低风险初始限额金额:{}:特殊限额后金额:{}", globalId, rcsBusinessPlayPaidConfigVo.getOrderMaxPay(), bigDecimal.longValue());
                rcsBusinessPlayPaidConfigVo.setOrderMaxPay(bigDecimal.longValue());
                rcsBusinessPlayPaidConfigVos.add(rcsBusinessPlayPaidConfigVo);
            }
        }
        return Response.success(rcsBusinessPlayPaidConfigVos);
    }

    /***
     * 判断用户是否是特殊限额模式
     * lowRiskPay 原始限额结果值
     * strayTotal 串关总赔付限额
     * straySingleDaySeries 串关单日类型赔付限额
     * oddsAmount 赔率
     * */
    public BigDecimal handlerSpecialUser(OrderBean orderBean, BigDecimal lowRiskPay, BigDecimal strayTotal
            , BigDecimal straySingleDaySeries, BigDecimal oddsAmount, String globalId) {
        String key = "risk:trade:rcs_user_special_bet_limit_config:" + orderBean.getUid();
        String type = redisUtils.hget(key, "type");
        if (StringUtils.isBlank(type) || (!"2".equals(type) && !"3".equals(type))) {//不是特殊限额用户
            return lowRiskPay;
        }
        String percentage = redisUtils.hget(key, "percentage");
        String specialUserAmountStr = redisUtils.hget(key, "2_-1_single_game_claim_limit");
       /* percentage = "2".equals(type)?percentage:null;
        specialUserAmountStr = "3".equals(type)?specialUserAmountStr:null;*/
        if (StringUtils.isBlank(percentage) && StringUtils.isBlank(specialUserAmountStr)) {//特殊限额数据为空
            return lowRiskPay;
        }
        if ("2".equals(type)) {//特殊百分比类型
            log.info("::{}::低风险用户特殊限额模式百分比:{},总赔付限额:{},串关单日类型赔付限额{}::", globalId, percentage, strayTotal, straySingleDaySeries);
//            straySingleDaySeries = StringUtils.isNotBlank(percentage) ? straySingleDaySeries.multiply(new BigDecimal(percentage)):straySingleDaySeries;
            BigDecimal min = this.min(strayTotal, straySingleDaySeries);
            log.info("::{}::低风险用户特殊限额模式总赔付额:{},串关单日赔付限额:{},计算最小值:{}::", globalId, strayTotal, straySingleDaySeries, min);
            lowRiskPay = min.divide(oddsAmount.subtract(BigDecimal.ONE), NumberConstant.NUM_TWO, RoundingMode.HALF_DOWN);
            return lowRiskPay;
        }
        BigDecimal specialUserAmount = new BigDecimal(specialUserAmountStr).divide(new BigDecimal("100"));
        //特殊单注单场类型
        String specialUserNoteAmountStr = redisUtils.hget(key, "2_-1_single_note_claim_limit");
        BigDecimal min = strayTotal;
        if (StringUtils.isNoneBlank(specialUserNoteAmountStr) && StringUtils.isNoneBlank(specialUserAmountStr)) {
            straySingleDaySeries = new BigDecimal(specialUserNoteAmountStr).divide(new BigDecimal("100"));
            min = this.min(strayTotal, straySingleDaySeries);
        }
        /*specialUserAmountStr = new BigDecimal(specialUserAmountStr).divide(new BigDecimal("100")).toString();
        log.info("::{}::低风险用户特殊限额模式::总额度:{},串关单注赔付限额:{},串关单日赔付限额:{},串关单日类型赔付限额{}::", globalId,strayTotal,specialUserNoteAmountStr, specialUserAmountStr,straySingleDaySeries);
        //如果设置特殊单注单场模式并且串关单日赔付限额不为空,则取串关赔付限额比较取最小值
        straySingleDaySeries = StringUtils.isNotBlank(specialUserAmountStr)?this.min(new BigDecimal(specialUserAmountStr),straySingleDaySeries):straySingleDaySeries;
        strayTotal = StringUtils.isNotBlank(specialUserAmountStr) ? this.min(new BigDecimal(specialUserAmountStr),strayTotal):strayTotal;
        BigDecimal min = this.min(strayTotal, straySingleDaySeries);*/
        log.info("::{}::低风险用户特殊限额单注单场模式:{},串关单注赔付限额:{},计算最小值:{}::", globalId, specialUserAmountStr, specialUserNoteAmountStr, min);
        lowRiskPay = min.divide(oddsAmount.subtract(BigDecimal.ONE), NumberConstant.NUM_TWO, RoundingMode.HALF_DOWN);
        return lowRiskPay;
    }

    /**
     * 低风险特殊用户限额处理注单成功串关总赔付更新
     */
    public boolean handlerSpecialUserSaveOrder(OrderBean orderBean, double strayPayment, boolean isHighRisk, List<RedisUpdateVo> redisUpdateList, String dateExpect, RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimitResVo) {
        if (isHighRisk) {//高风险不用处理
            return true;
        }
        String key = "risk:trade:rcs_user_special_bet_limit_config:" + orderBean.getUid();
        String type = redisUtils.hget(key, "type");
        if (StringUtils.isBlank(type) || (!"2".equals(type) && !"3".equals(type))) {//不是特殊限额用户
            return true;
        }
        String percentage = redisUtils.hget(key, "percentage");
        String specialUserAmountStr = redisUtils.hget(key, "2_-1_single_game_claim_limit");
        if (StringUtils.isBlank(percentage) && StringUtils.isBlank(specialUserAmountStr)) {//特殊限额数据为空
            return true;
        }
        //计入用户单日串关已用总赔付 商户:用户
        String strayPaymentKey = String.format(RedisKeyConstant.SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_KEY, orderBean.getTenantId(), orderBean.getUid(), dateExpect);
        double strayUsedUserPlay = redisUtils.incrByFloat(strayPaymentKey, strayPayment);
        redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.INCRBYFLOAT.getCmd(), strayPaymentKey, "", String.valueOf(strayPayment), String.valueOf(strayUsedUserPlay)));
        //单日串关总赔付配置
        BigDecimal strayUsedUserPlayConfig = this.getDefaultAmount(orderBean, iRcsMerchantSeriesConfigService.queryRedisCache().getSeriesPayoutTotalAmount().multiply(rcsQuotaBusinessLimitResVo.getUserQuotaRatio()), NumberConstant.NUM_TWO);
        log.info("::{}::2.0串关保存订单,单日串关总赔付限额---累计金额:{},结束金额:{},配置金额:{}", orderBean.getOrderNo(), strayPayment, strayUsedUserPlay, strayUsedUserPlayConfig);
        if (new BigDecimal(String.valueOf(strayUsedUserPlay)).compareTo(strayUsedUserPlayConfig.multiply(new BigDecimal(NumberConstant.NUM_ONE_HUNDRED))) > NumberConstant.NUM_ZERO) {
            return false;
        }
        redisUtils.expire(strayPaymentKey, NumberConstant.LONG_NINETY, TimeUnit.DAYS);
        return true;
    }

    /**
     * 保存订单校验入口
     *
     * @param request 订单信息
     * @return 检查结果
     */
    @Override
    public Response saveOrderCheckAmount(Request<OrderBean> request) {
        //订单信息
        OrderBean orderBean = request.getData();
        String globalId = request.getGlobalId();
        MDC.put("X-B3-TraceId", globalId);
        //更新记录
        List<RedisUpdateVo> redisUpdateList = new ArrayList<>(NumberConstant.NUM_TWO * NumberConstant.NUM_TEN);

        String dateExpect = DateUtils.DateToString(new Date());
        //查询商户信息
        RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimitResVo = this.getBusinessLimit(orderBean.getTenantId());
        //检查单日商户额度是否用完
        if (!checkBusinessTodayAmount(orderBean, dateExpect, rcsQuotaBusinessLimitResVo)) {
            String msg = "2.0串关商户单日限额额度已用完";
            return Response.success(new OrderCheckResultVo(false, msg, redisUpdateList));
        }
        List<ExtendBean> extendBeanList = this.getExtendBean(orderBean);
        Integer seriesType = orderBean.getSeriesType();
        //获取M串N中的M
        Integer seriesNum = SeriesTypeUtils.getSeriesType(seriesType);
        //获取总欧赔
        BigDecimal oddsRisk = BaseUtils.calTotalEuOdds(orderBean.getItems()).get(seriesType);
        //统计赛事次数
        int totalSeriesNum = SeriesTypeEnum.getNum(seriesType, extendBeanList.size());
        //用户的消耗配置金额:订单总金额(分)*总港赔(总欧赔-1)
        double strayPayment = new BigDecimal(orderBean.getOrderAmountTotal()).multiply(oddsRisk.subtract(BigDecimal.ONE)).setScale(NumberConstant.NUM_TWO, RoundingMode.HALF_DOWN).doubleValue();
        //以球种进行分组
        Map<String, List<ExtendBean>> sportMap = extendBeanList.stream().collect(Collectors.groupingBy(ExtendBean::getSportId));
        //查询高风险区间信息 如果是多球种则默认取其他高风险配置
        Integer sportId = sportMap.size() > 1 ? -1 : Integer.valueOf(extendBeanList.get(0).getSportId());
        boolean isHighRisk = isHighRisk(seriesType, oddsRisk, sportId);
        log.info("::{}::2.0串关保存订单高风险查询球种类型:{}", globalId, sportId);
        boolean handlerSpecialUser = false;
        for (ExtendBean extendBean : extendBeanList) {
            log.info("::{}::2.0串关保存订单:{},总欧赔:{},是否高风险:{}", globalId, seriesType, oddsRisk, isHighRisk);
            //计入用户单场限额
            double singlePayment = Double.parseDouble(String.valueOf(new BigDecimal(extendBean.getOrderMoney()).multiply(new BigDecimal(extendBean.getOdds()).subtract(BigDecimal.ONE)).multiply(new BigDecimal(totalSeriesNum)).setScale(NumberConstant.NUM_TWO, RoundingMode.HALF_DOWN)));
            String userSingleMatchHashKey = RedisKeyConstant.getUserSingleMatchHashKey(extendBean.getDateExpect(), String.valueOf(orderBean.getTenantId()), extendBean.getSportId(), String.valueOf(orderBean.getUid()), extendBean.getMatchId(), extendBean.getIsScroll());
            String usedUserSingleStr = redisUtils.hget(userSingleMatchHashKey, RedisKeyConstant.USER_SINGLE_MATCH_HASH_FIELD);
            log.info("::{}::2.0串关保存订单用户单关单场限额已经累计:{}", orderBean.getOrderNo(), usedUserSingleStr);
            BigDecimal usedUserSingle = StringUtils.isNotBlank(usedUserSingleStr) ? new BigDecimal(usedUserSingleStr) : BigDecimal.ZERO;
            //查询用户的单场累计赔付额度
            BigDecimal usedUserSingleConfig = this.getRcsQuotaUserSingleSiteQuotaData(extendBean, rcsQuotaBusinessLimitResVo);
            //由于累计的分，所以配置金额需要
            if (usedUserSingle.compareTo(usedUserSingleConfig) > NumberConstant.NUM_ZERO) {
                //用户单场赔付额度已用完
                commonService.redisCallback(redisUpdateList);
                String msg = "用户单关单场赔付额度已用完";
                return Response.success(new OrderCheckResultVo(false, msg, redisUpdateList));
            }
            //计入累计
            double endUsedUserSingle = redisUtils.hincrByFloat(userSingleMatchHashKey, RedisKeyConstant.USER_SINGLE_MATCH_HASH_FIELD, singlePayment);
            redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.HINCRBYFLOAT.getCmd(), userSingleMatchHashKey, RedisKeyConstant.USER_SINGLE_MATCH_HASH_FIELD, String.valueOf(singlePayment), String.valueOf(endUsedUserSingle)));
            log.info("::{}::2.0串关保存订单,单场限额---累计金额:{},结束金额:{},配置金额:{}", orderBean.getOrderNo(), singlePayment, endUsedUserSingle, usedUserSingleConfig);
            //计入玩法累计赔付限额
            // 需求2607 Ln(4)：如果是篮球，且是Ln模式,那么联控玩法跟随主控玩法限额值
            String playId = extendBean.getPlayId();
            String playType = extendBean.getPlayType();
            if(extendBean.getSportId().equals(SportIdEnum.BASKETBALL.getId().toString())) {
                //如果是篮球，那么就从redis取值判断下是否为LN模式
                String key = String.format(RedisKeyConstant.TRADING_TYPE_KEY,extendBean.getMatchId(),extendBean.getPlayId(),extendBean.getItemBean().getMatchType().toString());
                log.info("::{}::串关保存订单,LN模式下联控玩法额度跟随主控玩法KEY: {}",key);
                if (redisUtils.exists(key)) {
                    String lnValue = redisUtils.get(key);
                    if (lnValue.equals("4")) {
                        playId = LNBasktballEnum.getNameById(Integer.valueOf(playId)).toString();

                        //并且playType要替换成主控
                        //阶段  冠军玩法走mts/虚拟赛事 可以不设置此字段
                        if (extendBean.getItemBean().getMatchType() != 3 && !NumberConstant.VIRSTUAL_SPORT.contains(extendBean.getSportId())) {
                            //TODO 过了提测再优化
                            playType = limitApiService.queryPlayInfoById(Integer.valueOf(sportId), Integer.valueOf(playId)).getData();
                        }
                        log.info("::{}::串关保存订单,LN模式下联控玩法额度跟随主控玩法4{}::", playId);
                    }
                }
            }
            String field = String.format(RedisKeyConstant.USER_SINGLE_MATCH_PLAY_HASH_FIELD, playId, extendBean.getIsScroll(), playType);
            String usedUserPlayStr = redisUtils.hget(userSingleMatchHashKey, field);
            log.info("::{}::2.0串关保存订单用户玩法限额已经累计:{}", orderBean.getOrderNo(), usedUserPlayStr);
            BigDecimal usedUserPlay = CommonUtils.toBigDecimal(usedUserPlayStr, BigDecimal.ZERO);
            //查询用户玩法累加赔付额度
            RcsQuotaUserSingleNoteVo rcsQuotaUserSingleNoteVo = getRcsQuotaUserSingleNoteVo(extendBean, rcsQuotaBusinessLimitResVo);
            if (usedUserPlay.compareTo(rcsQuotaUserSingleNoteVo.getCumulativeCompensationPlaying()) > NumberConstant.NUM_ZERO) {
                //用户单关玩法赔付额度已用完
                commonService.redisCallback(redisUpdateList);
                String msg = "用户单关玩法赔付额度已用完";
                return Response.success(new OrderCheckResultVo(false, msg, redisUpdateList));
            }
            double endUsedUserPlay = redisUtils.hincrByFloat(userSingleMatchHashKey, field, singlePayment);
            log.info("::{}::2.0串关保存订单,玩法累计限额---累计金额:{},结束金额:{},配置金额:{}", orderBean.getOrderNo(), singlePayment, endUsedUserPlay, rcsQuotaUserSingleNoteVo.getCumulativeCompensationPlaying());
            redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.HINCRBYFLOAT.getCmd(), userSingleMatchHashKey, field, String.valueOf(singlePayment), String.valueOf(endUsedUserPlay)));
            redisUtils.expire(userSingleMatchHashKey, NumberConstant.LONG_NINETY, TimeUnit.DAYS);


            if (isHighRisk) {
                //计入串关每个赛种已用额度
                double payment = BigDecimal.valueOf(strayPayment).divide(BigDecimal.valueOf(orderBean.getItems().size()), NumberConstant.NUM_TWO, RoundingMode.HALF_DOWN).setScale(NumberConstant.NUM_TWO, RoundingMode.HALF_DOWN).doubleValue();
                String sportTypeKey = String.format(RedisKeyConstant.SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_RACE_KEY, orderBean.getTenantId(), orderBean.getUid(), extendBean.getSportId(), dateExpect);
                double sportTypePlay = redisUtils.incrByFloat(sportTypeKey, payment);
                redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.INCRBYFLOAT.getCmd(), sportTypeKey, "", String.valueOf(payment), String.valueOf(sportTypePlay)));
                //查询串关的每个赛种配置
                RcsMerchantSportLimit rcsMerchantSportLimit = iRcsMerchantSportLimitService.queryBySportId(Integer.valueOf(extendBean.getSportId()));
                //没有获取到用其他的赛种去查询
                if (Objects.isNull(rcsMerchantSportLimit)) {
                    rcsMerchantSportLimit = iRcsMerchantSportLimitService.queryBySportId(NumberConstant.NUM_MINUS_ONE);
                }
                BigDecimal strayLimitAmountConfig = this.getDefaultAmount(orderBean,rcsMerchantSportLimit.getStrayLimitAmount().multiply(rcsQuotaBusinessLimitResVo.getUserQuotaRatio()), NumberConstant.NUM_TWO);
                log.info("::{}::2.0串关保存订单,玩法累计限额---累计金额:{},结束金额:{},串关赛种:{},配置金额:{}", orderBean.getOrderNo(), payment, sportTypePlay, extendBean.getSportId(),strayLimitAmountConfig);
                if (new BigDecimal(String.valueOf(sportTypePlay)).compareTo(strayLimitAmountConfig.multiply(new BigDecimal(NumberConstant.NUM_ONE_HUNDRED))) > NumberConstant.NUM_ZERO) {
                    //用户单关玩法赔付额度已用完
                    commonService.redisCallback(redisUpdateList);
                    String msg = "用户单日串关赛种赔付额度已用完";
                    return Response.success(new OrderCheckResultVo(false, msg, redisUpdateList));
                }
                redisUtils.expire(sportTypeKey, NumberConstant.LONG_NINETY, TimeUnit.DAYS);

                //计算用户串关单场已用额度
                String userStrayLimitKey = String.format(RedisKeyConstant.PAID_DATE_BUS_REDIS_MATCH_CACHE, orderBean.getUid(), orderBean.getTenantId(), extendBean.getMatchId());
                String userStrayLimitPlayStr = redisUtils.get(userStrayLimitKey);
                BigDecimal userStrayLimitPlay = CommonUtils.toBigDecimal(userStrayLimitPlayStr, BigDecimal.ZERO);
                log.info("::{}::2.0串关保存订单用户串关单场限额已经累计:{},key:{}", orderBean.getOrderNo(), userStrayLimitPlay, userStrayLimitKey);
                if (Objects.nonNull(rcsQuotaBusinessLimitResVo.getUserSingleStrayLimit()) && userStrayLimitPlay.compareTo(BigDecimal.valueOf(rcsQuotaBusinessLimitResVo.getUserSingleStrayLimit())) > NumberConstant.NUM_ZERO) {
                    commonService.redisCallback(redisUpdateList);
                    String msg = "用户串关单场额度已用完";
                    return Response.success(new OrderCheckResultVo(false, msg, redisUpdateList));
                }
                double endUserStrayLimitPlay = redisUtils.incrByFloat(userStrayLimitKey, payment);
                redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.INCRBYFLOAT.getCmd(), userStrayLimitKey, "", String.valueOf(payment), String.valueOf(endUserStrayLimitPlay)));
                log.info("::{}::2.0串关保存订单,用户单场串关---累计金额:{},结束金额:{},配置金额:{}", orderBean.getOrderNo(), payment, endUserStrayLimitPlay, rcsQuotaBusinessLimitResVo.getUserSingleStrayLimit());
                redisUtils.expire(userStrayLimitKey, NumberConstant.LONG_NINETY, TimeUnit.DAYS);
            }
            if (!isHighRisk && !handlerSpecialUser) {
                handlerSpecialUser = true;
                boolean b = handlerSpecialUserSaveOrder(orderBean, strayPayment, isHighRisk, redisUpdateList, dateExpect, rcsQuotaBusinessLimitResVo);
                if (!b) {
                    commonService.redisCallback(redisUpdateList);
                    String msg = "用户串关类型单日总赔付额度已用完";
                    return Response.success(new OrderCheckResultVo(false, msg, redisUpdateList));
                }
            }
        }

        //处于高风险才去计算
        if (isHighRisk) {
            //计入用户单日串关已用总赔付 商户:用户
            String strayPaymentKey = String.format(RedisKeyConstant.SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_KEY, orderBean.getTenantId(), orderBean.getUid(), dateExpect);
            double strayUsedUserPlay = redisUtils.incrByFloat(strayPaymentKey, strayPayment);
            redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.INCRBYFLOAT.getCmd(), strayPaymentKey, "", String.valueOf(strayPayment), String.valueOf(strayUsedUserPlay)));
            //单日串关总赔付配置
            BigDecimal strayUsedUserPlayConfig = this.getDefaultAmount(orderBean, iRcsMerchantSeriesConfigService.queryRedisCache().getSeriesPayoutTotalAmount().multiply(rcsQuotaBusinessLimitResVo.getUserQuotaRatio()), NumberConstant.NUM_TWO);
            log.info("::{}::2.0串关保存订单,单日串关总赔付限额---累计金额:{},结束金额:{},配置金额:{}", orderBean.getOrderNo(), strayPayment, strayUsedUserPlay, strayUsedUserPlayConfig);
            if (new BigDecimal(String.valueOf(strayUsedUserPlay)).compareTo(strayUsedUserPlayConfig.multiply(new BigDecimal(NumberConstant.NUM_ONE_HUNDRED))) > NumberConstant.NUM_ZERO) {
                commonService.redisCallback(redisUpdateList);
                String msg = "用户串关单日总赔付额度已用完";
                return Response.success(new OrderCheckResultVo(false, msg, redisUpdateList));
            }
            redisUtils.expire(strayPaymentKey, NumberConstant.LONG_NINETY, TimeUnit.DAYS);
            //计算用户单日串关类型可赔付额度 商户:用户:串关类型中M
            String strayTypePaymentKey = String.format(RedisKeyConstant.SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_SERIES_KEY, orderBean.getTenantId(), orderBean.getUid(), seriesNum, dateExpect);
            Double strayTypeUsedUserPlay = redisUtils.incrByFloat(strayTypePaymentKey, strayPayment);
            redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.INCRBYFLOAT.getCmd(), strayTypePaymentKey, "", String.valueOf(strayPayment), String.valueOf(strayTypeUsedUserPlay)));
            //用户单日串关类型配置
            RcsMerchantLimitCompensation rcsMerchantLimitCompensation = iRcsMerchantLimitCompensationService.queryBySeriesType(seriesNum);
            BigDecimal seriesLimitAmountConfig = this.getDefaultAmount(orderBean, rcsMerchantLimitCompensation.getSeriesLimitAmount().multiply(rcsQuotaBusinessLimitResVo.getUserQuotaRatio()), NumberConstant.NUM_TWO);
            log.info("::{}::2.0串关保存订单,单日串关类型限额---累计金额:{},结束金额:{},配置金额:{}", orderBean.getOrderNo(), strayPayment, strayTypeUsedUserPlay, seriesLimitAmountConfig);
            //累计大于的时候才用完
            if (new BigDecimal(String.valueOf(strayTypeUsedUserPlay)).compareTo(seriesLimitAmountConfig.multiply(new BigDecimal(NumberConstant.NUM_ONE_HUNDRED))) > NumberConstant.NUM_ZERO) {
                commonService.redisCallback(redisUpdateList);
                String msg = "用户串关类型单日总赔付额度已用完";
                return Response.success(new OrderCheckResultVo(false, msg, redisUpdateList));
            }
            redisUtils.expire(strayTypePaymentKey, NumberConstant.LONG_NINETY, TimeUnit.DAYS);
        }
        /*boolean b = handlerSpecialUserSaveOrder(orderBean, strayPayment, isHighRisk, redisUpdateList,dateExpect,rcsQuotaBusinessLimitResVo);
        if(!b){
            commonService.redisCallback(redisUpdateList);
            String msg = "用户串关类型单日总赔付额度已用完";
            return Response.success(new OrderCheckResultVo(false, msg, redisUpdateList));
        }*/
        saveRedisUpdateRecord(orderBean.getOrderNo(), isHighRisk, redisUpdateList);
        log.info("::{}::2.0串关投注校验成功:{}", orderBean.getOrderNo(), JSON.toJSONString(redisUpdateList));
        String msg = "2.0串关投注校验成功";
        return Response.success(new OrderCheckResultVo(true, msg, redisUpdateList));
    }


    private void saveRedisUpdateRecord(String orderNo, boolean isHighRisk, List<RedisUpdateVo> redisUpdateList) {
        if (CollectionUtils.isEmpty(redisUpdateList)) {
            return;
        }
        // 新模式，是否高赔
        redisUpdateList.add(new RedisUpdateVo(RedisCmdEnum.LIMIT_MODE.getCmd(), String.valueOf(NumberConstant.NUM_ONE), isHighRisk ? String.valueOf(NumberConstant.NUM_ONE) : String.valueOf(NumberConstant.NUM_ZERO), String.valueOf(NumberConstant.NUM_ZERO), String.valueOf(NumberConstant.NUM_ZERO)));
        String key = String.format(RedisKeyConstant.SERIES_REDIS_UPDATE_RECORD_KEY, orderNo);
        redisUtils.set(key, JSON.toJSONString(redisUpdateList));
        log.info("::{}::2.0串关订单入库校验-缓存Redis更新记录：key:{}", orderNo, key);
        redisUtils.expire(key, NumberConstant.LONG_NINETY, TimeUnit.DAYS);
    }

    /**
     * 返回是否新旧串关模式
     *
     * @param businessId 商户ID
     * @return 返回1开0关
     */
    @Override
    public Response queryBusinessSwitch(String businessId) {
        return Response.success(baseService.queryBusinessSwitch(businessId));
    }

    /**
     * 检查用户单场和玩法累计赔付是否还有
     *
     * @param orderBean      订单信息
     * @param extendBeanList 扩展投注项
     * @return 赔付是否还有余额
     */
    private boolean checkUserSingeAmount(OrderBean orderBean, List<ExtendBean> extendBeanList, RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimitResVo) {
        //用户单日玩法
        List<BigDecimal> singleNoteList = new ArrayList<>();
        //用户单场
        List<BigDecimal> singleSiteQuotaList = new ArrayList<>();

        extendBeanList.forEach(extendBean -> {
            String userSingleMatchHashKey = RedisKeyConstant.getUserSingleMatchHashKey(extendBean.getDateExpect(), extendBean.getBusId(), extendBean.getSportId(), String.valueOf(orderBean.getUid()), extendBean.getMatchId(), extendBean.getIsScroll());
            //用户单场累计赔付额度
            String singleSiteQuotaVal = redisUtils.hget(userSingleMatchHashKey, RedisKeyConstant.USER_SINGLE_MATCH_HASH_FIELD);
            BigDecimal userSingle = StringUtils.isNoneBlank(singleSiteQuotaVal) ? new BigDecimal(singleSiteQuotaVal) : BigDecimal.ZERO;
            //返回用户单场配置，早盘或者滚球
            BigDecimal item1 = this.getRcsQuotaUserSingleSiteQuotaData(extendBean, rcsQuotaBusinessLimitResVo);
            singleSiteQuotaList.add(item1.subtract(userSingle).compareTo(BigDecimal.ZERO) > NumberConstant.NUM_ZERO ? item1.subtract(userSingle) : BigDecimal.ZERO);
            // 查询用户玩法累计赔付限额
            // 需求2607 Ln(4)：如果是篮球，且是Ln模式,那么联控玩法跟随主控玩法限额值
            String playId = extendBean.getPlayId();
            String playType = extendBean.getPlayType();
            log.info("::{}::当前extendBean{}::", JSONObject.toJSONString(extendBean));
            if(extendBean.getSportId().equals(SportIdEnum.BASKETBALL.getId().toString())) {
                String key = String.format(RedisKeyConstant.TRADING_TYPE_KEY,extendBean.getMatchId(),extendBean.getPlayId(),extendBean.getItemBean().getMatchType());
                log.info("::{}::串关LN模式下联控玩法额度跟随主控玩法KEY: {}",key);
                if (redisUtils.exists(key)) {
                    String lnValue = redisUtils.get(key);
                    if (lnValue.equals("4")) {
                        playId = LNBasktballEnum.getNameById(Integer.valueOf(playId)).toString();

                        //并且playType要替换成主控
                        //阶段  冠军玩法走mts/虚拟赛事 可以不设置此字段
                        if (extendBean.getItemBean().getMatchType() != 3 && !NumberConstant.VIRSTUAL_SPORT.contains(extendBean.getSportId())) {
                            //TODO 过了提测再优化
                            playType = limitApiService.queryPlayInfoById(Integer.valueOf(extendBean.getSportId()), Integer.valueOf(playId)).getData();
                        }
                        log.info("::{}::LN模式下联控玩法额度跟随主控玩法5{}::", playId);
                    }
                }
            }
            String field = String.format(RedisKeyConstant.USER_SINGLE_MATCH_PLAY_HASH_FIELD, playId, extendBean.getIsScroll(), playType);
            String singleNoteVal = redisUtils.hget(userSingleMatchHashKey, field);
            BigDecimal usedUserPlay = StringUtils.isNoneBlank(singleNoteVal) ? new BigDecimal(singleNoteVal) : BigDecimal.ZERO;
            //查询用户玩法累加赔付额度
            RcsQuotaUserSingleNoteVo rcsQuotaUserSingleNoteVo = getRcsQuotaUserSingleNoteVo(extendBean, rcsQuotaBusinessLimitResVo);
            log.info("2.0串关用户单场累计key:{},value:{},玩法value:{},单场配置:{},玩法配置:{},单场剩余:{},玩法累计:{}", userSingleMatchHashKey, userSingle, usedUserPlay, item1, rcsQuotaUserSingleNoteVo.getCumulativeCompensationPlaying(), item1.subtract(userSingle), rcsQuotaUserSingleNoteVo.getCumulativeCompensationPlaying().subtract(usedUserPlay));
            singleNoteList.add(rcsQuotaUserSingleNoteVo.getCumulativeCompensationPlaying().subtract(usedUserPlay).compareTo(BigDecimal.ZERO) > NumberConstant.NUM_ZERO ? rcsQuotaUserSingleNoteVo.getCumulativeCompensationPlaying().subtract(usedUserPlay) : BigDecimal.ZERO);
        });

        BigDecimal minNote = singleNoteList.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal minQuota = singleSiteQuotaList.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        return minNote.compareTo(BigDecimal.ZERO) > NumberConstant.NUM_ZERO && minQuota.compareTo(BigDecimal.ZERO) > NumberConstant.NUM_ZERO;
    }

    private List<Integer> getUserSingleAmount(OrderBean orderBean, List<ExtendBean> extendBeanList, RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimitResVo) {
        //获取当前日期
        List<Integer> userSingleStrayList = new ArrayList<>();
        extendBeanList.forEach(extendBean -> {
            //商户单场单日累计额度
            String userSingleStrayLimitKey = String.format(RedisKeyConstant.PAID_DATE_BUS_REDIS_MATCH_CACHE, orderBean.getUid(), orderBean.getTenantId(), extendBean.getMatchId());
            String userSingleStrayLimitCash = redisUtils.get(userSingleStrayLimitKey);
            BigDecimal userSingleStrayLimitTotal = StringUtils.isNoneBlank(userSingleStrayLimitCash) ? new BigDecimal(userSingleStrayLimitCash) : BigDecimal.ZERO;
            if (Objects.nonNull(rcsQuotaBusinessLimitResVo.getUserSingleStrayLimit()) && (rcsQuotaBusinessLimitResVo.getUserSingleStrayLimit() == NumberConstant.LONG_ZERO || BigDecimal.valueOf(rcsQuotaBusinessLimitResVo.getUserSingleStrayLimit()).compareTo(userSingleStrayLimitTotal) < NumberConstant.NUM_ZERO)) {
                userSingleStrayList.add(NumberConstant.NUM_ZERO);
            }
        });
        return userSingleStrayList;
    }

    /**
     * 检查商户今日额度是否已经用完
     *
     * @param dateExpect 日期信息
     * @param orderBean  订单扩展信息
     * @return 真假
     */
    private boolean checkBusinessTodayAmount(OrderBean orderBean, String dateExpect, RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimitResVo) {
        //查询商户限额开关
        int limitSwitch = rcsQuotaBusinessLimitResVo.getBusinessSingleDayLimitSwitch();
        log.info("::{}::2.0串关商户单日限额开关:{}", orderBean.getOrderNo(), limitSwitch);
        if (limitSwitch == NumberConstant.NUM_ZERO) {
            return true;
        }
        //商户串关单日累计额度
        String strayKey = String.format(RedisKeyConstant.PAID_DATE_BUS_SERIES_REDIS_CACHE, dateExpect, orderBean.getTenantId());
        String strayAmount = redisUtils.get(strayKey);
        BigDecimal remainingStray = StringUtils.isNoneBlank(strayAmount) ? new BigDecimal(strayAmount) : BigDecimal.ZERO;

        //商户单日累计
        String singleKey = String.format(RedisKeyConstant.PAID_DATE_BUS_REDIS_CACHE, dateExpect, orderBean.getTenantId());
        String singVal = redisUtils.get(singleKey);
        BigDecimal remainingSing = StringUtils.isNoneBlank(singVal) ? new BigDecimal(singVal) : BigDecimal.ZERO;
        log.info("2.0串关==商户串关单日限额已用额度:{},配置额度:{},剩余额度:{},商户单日累计:{},配置额度:{},剩余额度:{}",
                remainingStray, rcsQuotaBusinessLimitResVo.getBusinessSingleDaySeriesLimit(), BigDecimal.valueOf(rcsQuotaBusinessLimitResVo.getBusinessSingleDaySeriesLimit()).subtract(remainingStray),
                remainingSing, rcsQuotaBusinessLimitResVo.getBusinessSingleDayLimit(), BigDecimal.valueOf(rcsQuotaBusinessLimitResVo.getBusinessSingleDayLimit()).subtract(remainingSing));
        log.info("::2.0串关查询商户单日限额信息,单关key:{},单关值:{},串关key:{},串关值:{}", singleKey, singVal, strayKey, strayAmount);
        return BigDecimal.valueOf(rcsQuotaBusinessLimitResVo.getBusinessSingleDaySeriesLimit()).subtract(remainingStray).compareTo(BigDecimal.ZERO) > NumberConstant.NUM_ZERO &&
                BigDecimal.valueOf(rcsQuotaBusinessLimitResVo.getBusinessSingleDayLimit()).subtract(remainingSing).compareTo(BigDecimal.ZERO) > NumberConstant.NUM_ZERO;

    }


    /**
     * 返回限额数据给业务
     *
     * @param seriesType 串关类型 2001 3001...
     * @param maxPay     最大投注金额
     * @return 限额数据
     */
    private RcsBusinessPlayPaidConfigVo returnData(String seriesType, Long maxPay) {
        RcsBusinessPlayPaidConfigVo rcsBusinessPlayPaidConfigVo = new RcsBusinessPlayPaidConfigVo();
        rcsBusinessPlayPaidConfigVo.setOrderMaxPay(maxPay);
        rcsBusinessPlayPaidConfigVo.setType(seriesType);
        rcsBusinessPlayPaidConfigVo.setMinBet(NumberConstant.LONG_ZERO);
        return rcsBusinessPlayPaidConfigVo;
    }

    /**
     * 获取最大的赔付
     *
     * @param seriesType 串关类型  比如 2001
     * @return 高风险投注的最大和最小投注
     */
    private RcsMerchantInterval getMinMerchantLimit(List<OrderItem> orderItemList, Integer seriesType) {
        //以球种进行分组
        Map<Integer, List<OrderItem>> sportMap = orderItemList.stream().collect(Collectors.groupingBy(OrderItem::getSportId));
        //查询高风险区间信息 如果是多球种则默认取其他高风险配置
        Integer sportId = sportMap.size() > 1 ? -1 : Integer.valueOf(orderItemList.get(0).getSportId());
        List<RcsMerchantInterval> arrList = new ArrayList<>(orderItemList.size());
        orderItemList.forEach(orderItem -> {
            RcsMerchantInterval rcsMerchantInterval = rcsMerchantIntervalService.queryBySportAndStrayType(sportId, seriesType);
            if (Objects.isNull(rcsMerchantInterval)) {
                rcsMerchantInterval = rcsMerchantIntervalService.queryBySportAndStrayType(NumberConstant.NUM_MINUS_ONE, seriesType);
                log.info("::{}::2.0串关获取高风险配置最大投注配置金额:{}", orderItem.getOrderNo(), JSON.toJSONString(rcsMerchantInterval));
            }
            arrList.add(rcsMerchantInterval);
        });
        return arrList.stream().min(Comparator.comparing(RcsMerchantInterval::getMaxIntervalAmount)).orElse(new RcsMerchantInterval());
    }

    /**
     * 判断 用户注单是否处于高风险区域
     *
     * @param seriesType 串关类型比如 2001
     * @param sumOdd     赔率
     * @return 真假
     */
    private boolean isHighRisk(Integer seriesType, BigDecimal sumOdd, Integer sportId) {
        List<HighRiskObjConfig> highRiskObjConfigs = iRcsMerchantSingleLimitService.querySingleLimit(seriesType, sportId);
        if (Objects.nonNull(highRiskObjConfigs) && highRiskObjConfigs.size() > NumberConstant.NUM_ZERO) {
            for (HighRiskObjConfig highRiskObjConfig : highRiskObjConfigs) {
                if (sumOdd.compareTo(highRiskObjConfig.getMin()) >= NumberConstant.NUM_ZERO && sumOdd.compareTo(highRiskObjConfig.getMax()) <= NumberConstant.NUM_ZERO) {
                    return true;
                }
            }
        }
        return false;
    }


    //获取用户type值
    private String getUserSpecialLimitType(String userId) {
        // 用户特殊限额类型,0-无,1-标签限额,2-特殊百分比限额,3-特殊单注单场限额,4-特殊vip限额
        String userSpecialLimitKey = RedisKeyConstant.getUserSpecialLimitKey(userId);
        String userSpecialLimitType = RcsLocalCacheUtils.getValue(userSpecialLimitKey, RedisKeyConstant.USER_SPECIAL_LIMIT_TYPE_FIELD, redisUtils::hget);
        log.info("额度查询-串关-用户特殊限额类型：{}", userSpecialLimitType);
        return userSpecialLimitType;
    }

    // 用户无特殊限额 或者特殊单注单场限额未设置单注赔付参数
    //获取如果等于3的时候值并且获取单注赔付
    private BigDecimal getSingleNoteClaimLimit(String userId, BigDecimal defaultValue) {
        String key = RedisKeyConstant.getUserSpecialLimitKey(userId);
        String field = RedisKeyConstant.getSingleNoteClaimLimitField(String.valueOf(NumberConstant.NUM_TWO), String.valueOf(NumberConstant.NUM_MINUS_ONE));
        String value = RcsLocalCacheUtils.getValue(key, field, redisUtils::hget);
        log.info("2.0额度查询-串关-用户特殊限额-单注投注/赔付限额：{}", value);
        if (StringUtils.isNotBlank(value)) {
            return new BigDecimal(value).divide(new BigDecimal(NumberConstant.NUM_ONE_HUNDRED), NumberConstant.NUM_TWO, BigDecimal.ROUND_DOWN);
        }
        return defaultValue;
    }

    // 用户无特殊限额 或者特殊单注单场限额未设置单注赔付参数
    //获取如果等于3的时候值并且获取单日串关赔付
    private BigDecimal getSingleGameClaimLimit(String userId, BigDecimal defaultValue) {
        String key = RedisKeyConstant.getUserSpecialLimitKey(userId);
        String field = RedisKeyConstant.getSingleGameClaimLimitField(String.valueOf(NumberConstant.NUM_TWO), String.valueOf(NumberConstant.NUM_MINUS_ONE));
        String value = RcsLocalCacheUtils.getValue(key, field, redisUtils::hget);
        log.info("2.0额度查询-串关-用户特殊限额-单日串关/赔付限额：{}", value);
        if (StringUtils.isNotBlank(value)) {
            return new BigDecimal(value).divide(new BigDecimal(NumberConstant.NUM_ONE_HUNDRED), NumberConstant.NUM_TWO, BigDecimal.ROUND_DOWN);
        }
        return defaultValue;
    }

    /**
     * 如果是高危险投注限额的时候获取用户单注串关的配置
     * 以串关类型维度查询
     *
     * @param orderBean  订单对象
     * @param seriesType 串关类型 比如 2001 3001
     * @return 最小的配置值
     */
    private BigDecimal getHighRiskStraySingleDay(OrderBean orderBean, Integer seriesType) {
        List<BigDecimal> arrList = new ArrayList<>(orderBean.getItems().size());
        Integer seriesNum = seriesType > NumberConstant.NUM_EIGHT ? NumberConstant.NUM_EIGHT : seriesType;
        orderBean.getItems().forEach(orderItem -> {
            RcsMerchantHighRiskLimit rcsMerchantHighRiskLimit = iRcsMerchantHighRiskLimitService.queryFilterData(orderItem.getSportId(), orderItem.getTurnamentLevel(), seriesNum);
            if (Objects.isNull(rcsMerchantHighRiskLimit)) {
                rcsMerchantHighRiskLimit = iRcsMerchantHighRiskLimitService.queryFilterData(NumberConstant.NUM_MINUS_ONE, orderItem.getTurnamentLevel(), seriesNum);
                //去拿其他配置
                log.info("::串关高风险配置没有获取到去拿其他配置::");
            }
            arrList.add(Objects.isNull(rcsMerchantHighRiskLimit) || Objects.isNull(rcsMerchantHighRiskLimit.getSeriesAmount()) ? BigDecimal.ZERO : rcsMerchantHighRiskLimit.getSeriesAmount());
        });
        //获取配置里面的最小值
        return this.getDefaultAmount(orderBean, arrList.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO), NumberConstant.NUM_ONE);
    }

    /**
     * 获取用户单日串关总赔付额度
     *
     * @param orderBean 订单对象
     * @return 用户单日串关总赔付
     */
    private BigDecimal getStrayTotal(OrderBean orderBean, String dateExpect, RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimitResVo) {
        //单日串关总赔付
        BigDecimal sumAmount = iRcsMerchantSeriesConfigService.queryRedisCache().getSeriesPayoutTotalAmount();
        //户单日串关已用总赔付
        String strayPaymentKey = String.format(RedisKeyConstant.SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_KEY, orderBean.getTenantId(), orderBean.getUid(), dateExpect);
        String strayPaymentStr = redisUtils.get(strayPaymentKey);
        BigDecimal strayPaymentAmount = BigDecimal.ZERO;
        if (StringUtils.isNotBlank(strayPaymentStr)) {
            strayPaymentAmount = new BigDecimal(strayPaymentStr);
        }
        BigDecimal tempAmount = this.getDefaultAmount(orderBean, sumAmount.multiply(rcsQuotaBusinessLimitResVo.getUserStrayQuotaRatio())
                .setScale(NumberConstant.NUM_TWO, RoundingMode.HALF_DOWN), NumberConstant.NUM_TWO);
        log.info("::2.0串关单日总赔付获取到值:{}", tempAmount);

        BigDecimal remainingAmount = tempAmount.subtract(strayPaymentAmount.divide(new BigDecimal(NumberConstant.NUM_ONE_HUNDRED), NumberConstant.NUM_TWO, BigDecimal.ROUND_DOWN));
        log.info("2.0串关单日总赔付剩余:{}", remainingAmount);
        return remainingAmount.compareTo(BigDecimal.ZERO) <= NumberConstant.NUM_ZERO ? BigDecimal.ZERO : remainingAmount;
    }

    /**
     * 获取用户单日串关赛种赔付额度
     *
     * @param orderBean 订单对象
     * @return 用户单日串关赛种赔付额度
     */
    private BigDecimal getStraySingleDay(OrderBean orderBean, String dateExpect, RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimitResVo) {
        List<BigDecimal> itemList = new ArrayList<>();
        log.info("::{}::2.0串关限额获取商户信息::{}", orderBean.getOrderNo(), JSON.toJSONString(rcsQuotaBusinessLimitResVo));
        orderBean.getItems().forEach(orderItem -> {
            RcsMerchantSportLimit tempAmount = iRcsMerchantSportLimitService.queryBySportId(orderItem.getSportId());
            //没有获取到用其他的赛种去查询
            if (Objects.isNull(tempAmount)) {
                tempAmount = iRcsMerchantSportLimitService.queryBySportId(NumberConstant.NUM_MINUS_ONE);
            }
            //获取缓存里面已用的值
            String sportTypeKey = String.format(RedisKeyConstant.SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_RACE_KEY, orderBean.getTenantId(), orderBean.getUid(), orderItem.getSportId(), dateExpect);
            String cashStr = redisUtils.get(sportTypeKey);
            BigDecimal cashAmount = BigDecimal.ZERO;
            if (StringUtils.isNotBlank(cashStr)) {
                cashAmount = new BigDecimal(cashStr);
            }
            BigDecimal straySportAmount = rcsQuotaBusinessLimitResVo.getUserStrayQuotaRatio().multiply(tempAmount.getStrayLimitAmount()).setScale(NumberConstant.NUM_TWO, RoundingMode.HALF_DOWN).subtract(cashAmount.divide(new BigDecimal(NumberConstant.NUM_ONE_HUNDRED), NumberConstant.NUM_TWO, BigDecimal.ROUND_DOWN));
            log.info("::{}::2.0串关获取串关赛种赔付类型最终值::{}", orderBean.getOrderNo(), straySportAmount);
            itemList.add(straySportAmount);
        });
        String type = this.getUserSpecialLimitType(String.valueOf(orderBean.getUid()));
        BigDecimal bigDecimal = itemList.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        bigDecimal = StringUtils.isNotBlank(type)&&"2".equals(type)?getLaimLimitForTwo(orderBean.getUid().toString(),bigDecimal):bigDecimal;
        return bigDecimal;
    }

    /**
     * 获取用户单日串关类型赔付限额
     *
     * @param orderBean  订单对象
     * @param seriesType 串关类型 比如 2001 3001 。。。。
     * @return 用户单日串关类型赔付限额
     */
    private BigDecimal getStraySingleDaySeries(OrderBean orderBean, Integer seriesType, String dateExpect, RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimitResVo) {
        RcsMerchantLimitCompensation tempAmount = iRcsMerchantLimitCompensationService.queryBySeriesType(seriesType);
        String strayTypePaymentKey = String.format(RedisKeyConstant.SINGLE_DAY_STRAY_TOTAL_MERCHANT_USER_SERIES_KEY, orderBean.getTenantId(), orderBean.getUid(), seriesType, dateExpect);
        String strayTypeStr = redisUtils.get(strayTypePaymentKey);
        BigDecimal strayTypeAmount = BigDecimal.ZERO;
        if (StringUtils.isNotBlank(strayTypeStr)) {
            strayTypeAmount = new BigDecimal(strayTypeStr);
        }
        log.info("::{}::2.0单日串关类型赔付限额获取到累加值:{}", orderBean.getOrderNo(), strayTypeAmount);
        BigDecimal subtract = tempAmount.getSeriesLimitAmount().multiply(rcsQuotaBusinessLimitResVo.getUserStrayQuotaRatio()).setScale(NumberConstant.NUM_TWO, RoundingMode.HALF_DOWN).subtract(strayTypeAmount.divide(new BigDecimal(NumberConstant.NUM_ONE_HUNDRED), NumberConstant.NUM_TWO, BigDecimal.ROUND_DOWN));
        String type = this.getUserSpecialLimitType(String.valueOf(orderBean.getUid()));
        subtract = StringUtils.isNotBlank(type) && "2".equals(type) ? getLaimLimitForTwo(orderBean.getUid().toString(), subtract) : subtract;
        return subtract;
    }

    //获取用户type=2的时候
    private BigDecimal getLaimLimitForTwo(String userId, BigDecimal defaultValue) {
        String key = RedisKeyConstant.getUserSpecialLimitKey(userId);
        String value = RcsLocalCacheUtils.getValue(key, RedisKeyConstant.USER_SPECIAL_LIMIT_PERCENTAGE_FIELD, redisUtils::hget);
        log.info("2.0额度查询-用户特殊限额等于2：{}", value);
        if (StringUtils.isNotBlank(value)) {
            return new BigDecimal(value).multiply(defaultValue).setScale(NumberConstant.NUM_TWO, RoundingMode.HALF_DOWN);
        }
        return defaultValue;
    }


    /**
     * 根据商户ID获取商户的限额信息
     *
     * @param businessId 商户ID
     * @return 商户限额信息
     */
    private RcsQuotaBusinessLimitResVo getBusinessLimit(Long businessId) {
        // 先从缓存取
        String key = String.format(RedisKeyConstant.MERCHANT_LIMIT_KEY, businessId);
        String value = RcsLocalCacheUtils.getValue(key, redisUtils::get);
        log.info("2.0串关额度-查询，Redis获取商户限额：key={},field={},value={}", key, businessId, value);
        if (StringUtils.isNotBlank(value)) {
            return JSON.parseObject(value, RcsQuotaBusinessLimitResVo.class);
        }
        // 缓存没有调用rpc接口查询
        Response<RcsQuotaBusinessLimitResVo> response = limitApiService.getRcsQuotaBusinessLimit(businessId.toString());
        log.info("2.0串关额度查询，调用rpc获取商户限额：response={}", JSON.toJSONString(response));
        if (response == null || response.getCode() != Response.SUCCESS || response.getData() == null) {
            throw new RcsServiceException(NumberConstant.NUM_MINUS_ONE, "调用rpc获取商户限额失败");
        }
        RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimitResVo = response.getData();
        redisUtils.setex(key, rcsQuotaBusinessLimitResVo, 5L, TimeUnit.DAYS);
        return rcsQuotaBusinessLimitResVo;
    }

    /**
     * 获取用户单注单关限额
     *
     * @param order 订单扩展信息
     * @return 返回用户单注单关限额信息
     */
    private RcsQuotaUserSingleNoteVo getRcsQuotaUserSingleNoteVo(ExtendBean order, RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimitResVo) {
        String sportId = order.getSportId();
        Integer tournamentLevel = convertTournamentLevel(order.getTournamentLevel());
        String matchId = order.getMatchId();
        String isScroll = order.getIsScroll();
        // 需求2607 Ln(4)：如果是篮球，且是Ln模式,那么联控玩法跟随主控玩法限额值
        String playId = order.getPlayId();
        if(sportId.equals(SportIdEnum.BASKETBALL.getId().toString())){
            String key = String.format(RedisKeyConstant.TRADING_TYPE_KEY,matchId,playId,order.getItemBean().getMatchType());
            log.info("::{}::获取用户单注单关限额,LN模式下联控玩法额度跟随主控玩法KEY: {}",key);
            if(redisUtils.exists(key)) {
                String lnValue = redisUtils.get(key);
                if (lnValue.equals("4")) {
                    playId = LNBasktballEnum.getNameById(Integer.valueOf(playId)).toString();
                    log.info("::{}::获取用户单注单关限额,LN模式下联控玩法额度跟随主控玩法6{}::", playId);
                }
            }
        }
        // 先从缓存取
        RcsQuotaUserSingleNoteVo vo = getUserSingleNoteVo(sportId, matchId, isScroll, playId, Long.valueOf(order.getBusId()), rcsQuotaBusinessLimitResVo);
        if (vo != null) {
            return vo;
        }
        // 缓存没有调用rpc接口查询
        Response<MatchLimitDataVo> response = getMatchLimitData(sportId, tournamentLevel, matchId, LimitDataTypeEnum.USER_SINGLE_BET_LIMIT);
        log.info("::{}::2.0串关调用rpc获取用户单注单关限额：response={}", order.getOrderId(), JSON.toJSONString(response));
        if (response == null || response.getCode() != Response.SUCCESS || response.getData() == null || response.getData().getRcsQuotaUserSingleNoteVoList().size() <= NumberConstant.NUM_ZERO) {
            log.info("::{}::2.0串关未获取到用户单注单关限额配置:{}", order.getOrderId(), JSON.toJSONString(order));
            // throw new RcsServiceException(NumberConstant.NUM_MINUS_ONE, "2.0串关调用rpc获取用户单注单关限额失败");
        } else {
            List<RcsQuotaUserSingleNoteVo> list = response.getData().getRcsQuotaUserSingleNoteVoList();
            list.forEach(config -> {
                String betState = String.valueOf(config.getBetState());
                String configPlayId = String.valueOf(config.getPlayId());
                String playPaymentLimitKey = String.format(RedisKeyConstant.USER_MATCH_PLAY_LIMIT_KEY, sportId, matchId, betState, configPlayId);
                // 玩法累计赔付限额
                BigDecimal playPaymentLimit = Objects.isNull(config.getCumulativeCompensationPlaying()) ? BigDecimal.ZERO : config.getCumulativeCompensationPlaying();
                if (config.getBetState() == 0) {
                    redisUtils.setex(playPaymentLimitKey, playPaymentLimit, NumberConstant.NUM_SEVEN, TimeUnit.DAYS);
                } else {
                    redisUtils.setex(playPaymentLimitKey, playPaymentLimit, NumberConstant.NUM_FOUR, TimeUnit.HOURS);
                }
            });
        }
        vo = getUserSingleNoteVo(sportId, matchId, isScroll, playId, Long.valueOf(order.getBusId()), rcsQuotaBusinessLimitResVo);
        //滚球没取到 就拿早盘的配置 兼容早盘提前开滚球
        if (Integer.valueOf(isScroll) == NumberConstant.NUM_ONE && vo == null) {
            vo = getUserSingleNoteVo(sportId, matchId, String.valueOf(NumberConstant.NUM_ZERO), playId, Long.valueOf(order.getBusId()), rcsQuotaBusinessLimitResVo);
            log.info("::{}::2.0串关限额本次滚球获取用户单注单关限额 读取早盘配置", order.getOrderId());
        }
        if (vo == null && Integer.valueOf(order.getSportId()) == NumberConstant.NUM_MINUS_ONE) {
            log.error("::{}::2.0串关未配置用户单注单关限额：{}", order.getOrderId(), JSON.toJSONString(order));
            throw new RcsServiceException(NumberConstant.NUM_MINUS_THREE, "未配置用户单注单关限额");
        }
        if (vo == null) {
            log.info("::{}::额度查询-用户单注单关限额-使用其他-1配置", order.getOrderId());
            order.setSportId(String.valueOf(NumberConstant.NUM_MINUS_ONE));
            RcsQuotaUserSingleNoteVo result = getRcsQuotaUserSingleNoteVo(order, rcsQuotaBusinessLimitResVo);
            order.setSportId(sportId);
            return result;
        }
        return vo;
    }

    private RcsQuotaUserSingleNoteVo getUserSingleNoteVo(String sportId, String matchId, String matchType, String playId, Long busId, RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimitResVo) {
        // 先获取联赛设置的playType为-1的单注投注限额和玩法累计赔付
        RcsQuotaUserSingleNoteVo vo = getRcsQuotaUserSingleNoteVo(sportId, matchId, matchType, playId, busId, rcsQuotaBusinessLimitResVo);
        if (vo != null) {
            return vo;
        }
        vo = getRcsQuotaUserSingleNoteVo(sportId, matchId, matchType, String.valueOf(NumberConstant.NUM_MINUS_ONE), busId, rcsQuotaBusinessLimitResVo);
        return vo;
    }

    private Response<MatchLimitDataVo> getMatchLimitData(final String sportId, final Integer tournamentLevel, final String matchId, final LimitDataTypeEnum limitDataTypeEnum) {
        MatchLimitDataReqVo reqVo = new MatchLimitDataReqVo();
        reqVo.setSportId(Integer.valueOf(sportId));
        reqVo.setTournamentLevel(convertTournamentLevel(tournamentLevel));
        reqVo.setMatchId(Long.valueOf(matchId));
        reqVo.setDataTypeList(Lists.newArrayList(limitDataTypeEnum.getType()));
        Request<MatchLimitDataReqVo> request = new Request<>();
        request.setData(reqVo);
        return limitApiService.getMatchLimitData(request);
    }

    private int convertTournamentLevel(Integer tournamentLevel) {
        if (tournamentLevel == null || tournamentLevel <= NumberConstant.NUM_ZERO) {
            // -1表示未评级
            return NumberConstant.NUM_MINUS_ONE;
        }
        return tournamentLevel;
    }

    private RcsQuotaUserSingleNoteVo getRcsQuotaUserSingleNoteVo(String sportId, String matchId, String matchType, String playId, Long busID, RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimitResVo) {
        // 玩法累计赔付限额
        String playPaymentLimitKey = String.format(RedisKeyConstant.USER_MATCH_PLAY_LIMIT_KEY, sportId, matchId, matchType, playId);
        String playPaymentLimit = redisUtils.get(playPaymentLimitKey);
        log.info("2.0串关Redis获取玩法累计赔付限额：key={},value={}", playPaymentLimitKey, playPaymentLimit);
        // 用户限额比例
        BigDecimal userQuotaRatio = rcsQuotaBusinessLimitResVo.getUserQuotaRatio();
        if (StringUtils.isNotBlank(playPaymentLimit)) {
            RcsQuotaUserSingleNoteVo vo = new RcsQuotaUserSingleNoteVo();
            vo.setCumulativeCompensationPlaying(new BigDecimal(playPaymentLimit).multiply(userQuotaRatio).setScale(NumberConstant.NUM_TWO, RoundingMode.HALF_DOWN));
            return vo;
        }
        return null;
    }


    /**
     * 获取用户单场限额
     *
     * @param order 订单信息
     * @return 用户单场限额信息
     */
    private BigDecimal getRcsQuotaUserSingleSiteQuotaData(ExtendBean order, RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimitResVo) {
        Integer matchTypeNew = Integer.valueOf(order.getIsScroll()) == NumberConstant.NUM_ZERO ? NumberConstant.NUM_ONE : NumberConstant.NUM_ZERO;
        String singleKey = String.format(RedisKeyConstant.USER_MATCH_SINGLE_LIMIT_KEY, order.getSportId(), matchTypeNew, order.getMatchId());
        String userSingleSiteQuota = redisUtils.get(singleKey);
        // 用户限额比例
        BigDecimal userQuotaRatio = rcsQuotaBusinessLimitResVo.getUserQuotaRatio();
        //特殊用户百分比
        BigDecimal percentage = getUserLimitPercentage(order.getUserId());
        if (StringUtils.isBlank(userSingleSiteQuota)) {
            // 没读到缓存  调用接口查询
            Response<MatchLimitDataVo> response = getMatchLimitData(order.getSportId(), order.getTournamentLevel(), order.getMatchId(), LimitDataTypeEnum.USER_SINGLE_LIMIT);
            log.info("2.0串关额度查询-调用rpc获取用户单场限额：response={}", JSON.toJSONString(response));
            RcsQuotaUserSingleSiteQuotaVo singleSiteQuotaVo = null;
            if (response != null && response.getData() != null && response.getCode() == Response.SUCCESS) {
                singleSiteQuotaVo = response.getData().getRcsQuotaUserSingleSiteQuotaVo();
            }
            if (singleSiteQuotaVo == null && String.valueOf(NumberConstant.NUM_MINUS_ONE).equals(order.getSportId())) {
                throw new RcsServiceException(NumberConstant.NUM_MINUS_THREE, "2.0串关读取用户单场限额数据异常");
            }
            if (singleSiteQuotaVo == null) {
                log.info("2.0串关额度查询-用户单场限额-使用其他-1配置");
                String sportId = order.getSportId();
                order.setSportId(String.valueOf(NumberConstant.NUM_MINUS_ONE));
                BigDecimal amount = getRcsQuotaUserSingleSiteQuotaData(order, rcsQuotaBusinessLimitResVo);
                order.setSportId(sportId);
                return amount;
            }
            BigDecimal earlyLimit = singleSiteQuotaVo.getEarlyUserSingleSiteQuota();
            BigDecimal liveLimit = singleSiteQuotaVo.getLiveUserSingleSiteQuota();
            log.info("2.0串关额度查询-获取 用户单场限额rpc处理完成:{}", JSON.toJSONString(singleSiteQuotaVo));
            //如果是早盘就设置 7天过期时间
            if (StringUtils.equalsIgnoreCase(order.getIsScroll(), String.valueOf(NumberConstant.NUM_ZERO))) {
                redisUtils.setex(singleKey, earlyLimit, NumberConstant.NUM_SEVEN, TimeUnit.DAYS);
                return earlyLimit.multiply(userQuotaRatio).multiply(percentage).setScale(NumberConstant.NUM_TWO, RoundingMode.HALF_DOWN);

            } else {
                //滚球设置4小时过期时间
                redisUtils.setex(singleKey, liveLimit, NumberConstant.NUM_FOUR, TimeUnit.HOURS);
                return liveLimit.multiply(userQuotaRatio).multiply(percentage).setScale(NumberConstant.NUM_TWO, RoundingMode.HALF_DOWN);
            }

        }
        log.info("2.0串关额度查询-用户单注单关限额:{}", JSON.toJSONString(userSingleSiteQuota));
        return new BigDecimal(userSingleSiteQuota).multiply(userQuotaRatio).multiply(percentage).setScale(NumberConstant.NUM_TWO, RoundingMode.HALF_DOWN);
    }

    //获取限额百分比
    private BigDecimal getUserLimitPercentage(String uid) {
        String key = RedisKeyConstant.getUserSpecialLimitKey(uid);
        String type = RcsLocalCacheUtils.getValue(key, RedisKeyConstant.USER_SPECIAL_LIMIT_TYPE_FIELD, redisUtils::hget);
        String percentage = RcsLocalCacheUtils.getValue(key, RedisKeyConstant.USER_SPECIAL_LIMIT_PERCENTAGE_FIELD, redisUtils::hget);


        log.info("2.0串关额度查询- 百分比 :{}:{}", type, percentage);
        if (StringUtils.isNotBlank(type) && type.equals(String.valueOf(NumberConstant.NUM_TWO)) && StringUtils.isNotBlank(percentage)) {
            log.info("2.0串关额度查询- 特殊百分比~~~生效 :{}:{}", type, percentage);
            return new BigDecimal(percentage);
        }

        String tagId = getUserTag(uid);
        String tagKey = RedisKeyConstant.getUserTagLimitKey(tagId);

        //如果特殊限额取不到  则从标签去取比例    0 无  1标签限额  2特殊百分比限额 3特殊单注单场限额 4特殊vip限额
        //标签限额配置 5分钟本地缓存
        percentage = RcsLocalCacheUtils.getValue(tagKey + RedisKeyConstant.USER_SPECIAL_LIMIT_PERCENTAGE_FIELD, redisUtils::get);

        if ((StringUtils.isBlank(type) || type.equals(String.valueOf(NumberConstant.NUM_ONE))) && StringUtils.isNotBlank(percentage)) {
            log.info("2.0串关额度查询- 标签限额~~~生效 :{}:{}", type, percentage);
            return new BigDecimal(percentage);
        }
        if (StringUtils.isBlank(percentage)) {
            RcsLocalCacheUtils.timedCache.put(tagKey + RedisKeyConstant.USER_SPECIAL_LIMIT_PERCENTAGE_FIELD, String.valueOf(NumberConstant.NUM_ONE));
        }
        return new BigDecimal(String.valueOf(NumberConstant.NUM_ONE));
    }


    private List<ExtendBean> getExtendBean(OrderBean orderBean) {
        List<OrderItem> orderItemList = orderBean.getItems();
        List<ExtendBean> resultList = new ArrayList<>(orderItemList.size());
        orderItemList.forEach(orderItem -> resultList.add(commonService.buildExtendBean(orderBean, orderItem)));
        return resultList;
    }

    /**
     * 获取用户标签
     *
     * @param userId 用户ID
     * @return 用户标签
     */
    private String getUserTag(String userId) {

        //先从缓存查
        String tagKey = RedisKeyConstant.getTagKey();
        String tagId = RcsLocalCacheUtils.getValue(tagKey + userId, redisUtils::get);
        if (StringUtils.isNotBlank(tagId)) {
            log.info("缓存获取获取用户标签：response={}", tagId);
            return tagId;
        }
        //RPC查询
        Request<Long> request = new Request<>();
        request.setData(Long.valueOf(userId));
        Response<Integer> response = limitApiService.getUserTag(request);
        log.info("调用rpc获取获取用户标签：response={}", JSON.toJSONString(response));
        if (response == null || response.getCode() != Response.SUCCESS) {
            throw new RcsServiceException(NumberConstant.NUM_MINUS_THREE, "调用rpc获取取用户标失败");
        }
        tagId = response.getData().toString();
        redisUtils.set(tagKey + userId, tagId);

        redisUtils.expire(tagKey + userId, 90, TimeUnit.DAYS);

        return tagId;
    }

    /**
     * 处理金额和特殊用户做一个比较
     *
     * @param orderBean  订单对象
     * @param amount     配置金额
     * @param amountType 1串关单注最大赔付，2.串关单日最大赔付
     * @return 处理后金额
     */
    private BigDecimal getDefaultAmount(OrderBean orderBean, BigDecimal amount, int amountType) {
        String type = this.getUserSpecialLimitType(String.valueOf(orderBean.getUid()));
        if (StringUtils.equalsIgnoreCase(type, String.valueOf(NumberConstant.NUM_TWO))) {
            amount = this.getLaimLimitForTwo(String.valueOf(orderBean.getUid()), amount);
            return amount;
        } else if (StringUtils.equalsIgnoreCase(type, String.valueOf(NumberConstant.NUM_THREE)) && amountType == NumberConstant.NUM_ONE) {
            amount = this.getSingleNoteClaimLimit(String.valueOf(orderBean.getUid()), amount);
            return amount;
        } else if (StringUtils.equalsIgnoreCase(type, String.valueOf(NumberConstant.NUM_THREE)) && amountType == NumberConstant.NUM_TWO) {
            amount = this.getSingleGameClaimLimit(String.valueOf(orderBean.getUid()), amount);
        }
        return amount;

    }

    private BigDecimal min(BigDecimal a, BigDecimal b) {
        return (a.compareTo(b) <= NumberConstant.NUM_ZERO) ? a : b;
    }

    private BigDecimal max(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) <= NumberConstant.NUM_ZERO ? b : a;
    }
}
