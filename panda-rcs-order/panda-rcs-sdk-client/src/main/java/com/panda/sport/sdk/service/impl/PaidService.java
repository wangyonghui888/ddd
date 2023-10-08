package com.panda.sport.sdk.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.limit.LimitApiService;
import com.panda.sport.data.rcs.api.third.OddinApiService;
import com.panda.sport.data.rcs.api.third.OddinApiService;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.SettleItem;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaUserSingleNoteVo;
import com.panda.sport.data.rcs.dto.oddin.TicketResultDto;
import com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.enums.DataSourceEnum;
import com.panda.sport.rcs.enums.limit.UserSpecialLimitType;
import com.panda.sport.rcs.pojo.RcsBusinessPlayPaidConfig;
import com.panda.sport.rcs.pojo.RcsBusinessSingleBetConfig;
import com.panda.sport.rcs.utils.SpringContextUtils;
import com.panda.sport.sdk.constant.BaseConstants;
import com.panda.sport.sdk.constant.LimitRedisKeys;
import com.panda.sport.sdk.constant.RedisKeys;
import com.panda.sport.sdk.constant.SdkConstants;
import com.panda.sport.sdk.core.JedisClusterServer;
import com.panda.sport.sdk.exception.LogicException;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.mapper.RcsTournamentTemplateJumpConfigMapper;
import com.panda.sport.sdk.mq.Producer;
import com.panda.sport.sdk.util.PropertiesUtil;
import com.panda.sport.sdk.util.RcsLocalCacheUtils;
import com.panda.sport.sdk.vo.RcsTournamentTemplateJumpConfig;
import net.logstash.logback.encoder.org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.panda.sport.sdk.constant.RedisKeys.TEMPLATE_TOURNAMENT_AMOUNT;


@Singleton
public class PaidService {
    private static final Logger log = LoggerFactory.getLogger(PaidService.class);

    @Inject
    JedisClusterServer jedisClusterServer;
    @Inject
    Producer producer;
    @Inject
    private LuaPaidService luaPaidService;
    @Inject
    UserPlayMaxPaid userPlayMaxPaid;

    @Inject
    private RcsPaidConfigServiceImp rcsPaidConfigService;

    @Inject
    LimitConfigService limitConfigService;

    @Inject
    SpecialVipService specialVipService;

    public static final List<String> sportIds = Lists.newArrayList("6", "11", "12", "13", "14", "15", "16");

    @Inject
    OddinApiService oddinApiService;


    /**
     * 获取当前用户投注项剩余最大投注金额
     *
     * @param order                 注单信息
     * @param rcsQuotaBusinessLimit 商户限额配置
     * @return 限额值
     */
    public Long getUserSelectsMaxBetAmountV4(ExtendBean order, RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit) {
        String indexKey = StringUtils.isBlank(order.getOrderId()) ? order.getUserId() : order.getOrderId();
        //【用户维度限额逻辑】
        RcsQuotaUserSingleNoteVo rcsQuotaUserSingleNoteVo = queryMatchAndTournamentMaxBetAmount(order, rcsQuotaBusinessLimit);
        long singleOrderMaxBet = rcsQuotaUserSingleNoteVo.getSinglePayLimit().longValue();

        //【商户维度限额逻辑】
        long minAmount = limitConfigService.getBusinessAvailablePaymentV2(NumberUtils.toLong(order.getBusId()), indexKey, rcsQuotaBusinessLimit);
        //bug-43223
        if(minAmount<=0){//代表商户不再接单
            return minAmount;
        }
        //【综合维度限额逻辑】
        Long redisMaxAmount = luaPaidService.getUserSelectsMaxBetAmountV3(order, rcsQuotaBusinessLimit, rcsQuotaUserSingleNoteVo.getCumulativeCompensationPlaying());

        // 新版 lua
        //Long redisMaxAmount = luaPaidService.getUserSelectsMaxBetAmountV4(order, rcsQuotaBusinessLimit, rcsQuotaUserSingleNoteVo.getCumulativeCompensationPlaying());
        minAmount = Math.min(minAmount, redisMaxAmount);
        //各维度限额最大赔付需要除以赔率
        BigDecimal oddsDiff = new BigDecimal(order.getOdds()).subtract(new BigDecimal("1"));
        minAmount = new BigDecimal(minAmount).divide(oddsDiff, 0, RoundingMode.DOWN).longValue();
        log.info("::{}::限额-单注综合结果:{}/赔率:{} = 综合结果可投额度:{}", indexKey, redisMaxAmount, oddsDiff, minAmount);

        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%【特殊抽水限额逻辑】%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//
        Map<String, BigDecimal> dynamicLimitMap = limitConfigService.dynamicLimitNew(order, rcsQuotaBusinessLimit.getUserQuotaRatio());
        if (dynamicLimitMap != null) {
            singleOrderMaxBet = dynamicLimitMap.get("dynamicLimit").longValue();
            int lowHighType = dynamicLimitMap.get("lowHighType").intValue();
            if (lowHighType == 1) {
                //高赔保底投注限额
                BigDecimal highOddScopeBetLimit = dynamicLimitMap.get("highOddScopeBetLimit");
                //如果单注可投额度 比保底额度小
                if (highOddScopeBetLimit != null) {
                    if (singleOrderMaxBet < highOddScopeBetLimit.longValue()) {
                        //如果y >=保底额度 取保底额度
                        //如果 y < "高赔：单注保底投注限额"：该投注项的单注可投金额为y。
                        singleOrderMaxBet = Math.min(minAmount, highOddScopeBetLimit.longValue());
                    }
                }
            }
        }
        minAmount = Math.min(minAmount, singleOrderMaxBet);
        minAmount = handlerGuaranteeBet(order,new BigDecimal(minAmount),rcsQuotaBusinessLimit,indexKey).longValue();
        minAmount = minAmount / 100;
        minAmount = minAmount<=0?0:minAmount;
        log.info("::{}::限额-单注最终限额结果:{}", indexKey, minAmount);
        if (minAmount <= 0) {
            return 0L;
        }
        return minAmount;
    }


    /**
     * 单注保底投注限额
     * */
    public BigDecimal handlerGuaranteeBet(ExtendBean order,BigDecimal money,RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit,String indexKey){
        if(order.getDataSourceCode().equals(DataSourceEnum.RC.getDataSource())){//如果是C01赛事则不需要走保底
            return money;
        }
        indexKey = StringUtils.isBlank(order.getOrderId()) ? order.getUserId() : order.getOrderId();
        String playId = order.getPlayId();
        boolean contains = Arrays.asList("7","20","74","103").contains(playId);
        if(!contains){//不是这四种玩法直接返回
            log.info("::{}::限额-单注保底投注限额-不是特定四种内玩法:{}", indexKey, playId);
            return money;
        }
        String limitKey = LimitRedisKeys.getMatchSingleBetPlayLimitKey(Integer.parseInt(order.getSportId()), LimitDataTypeEnum.USER_SINGLE_BET_LIMIT, order.getMatchId(), order.getIsScroll(), order.getPlayId());
        //单注保底限额
        String singleHedgeAmountLimitStr = RcsLocalCacheUtils.getValue(limitKey + ":singleHedgeAmount", jedisClusterServer::get);
        if(StringUtils.isBlank(singleHedgeAmountLimitStr)){//为空则根据不同玩法给默认值
            //  74 半场比分 20 上半场比分
            singleHedgeAmountLimitStr = "74".equals(playId) || "20".equals(playId) ? "150000" : singleHedgeAmountLimitStr;
            // 7 全场比分 103 半全场比分
            singleHedgeAmountLimitStr = "7".equals(playId) || "103".equals(playId) ? "250000" : singleHedgeAmountLimitStr;
        }
        BigDecimal singleHedgeAmount = new BigDecimal(singleHedgeAmountLimitStr);
        if(money.compareTo(singleHedgeAmount)>=0){
            log.info("::{}::限额-单注保底投注限额-大于保底投注:{}", indexKey, money);
            return money;
        }
        log.info("::{}::限额-单注保底投注限额-小于保底投注:{}", indexKey, money);
        RcsQuotaUserSingleNoteVo rcsQuotaUserSingleNoteVo = queryMatchAndTournamentMaxBetAmount(order, rcsQuotaBusinessLimit);
        Long minAmount = luaPaidService.getUserSelectsMaxBetAmountV3(order, rcsQuotaBusinessLimit, rcsQuotaUserSingleNoteVo.getCumulativeCompensationPlaying());
        BigDecimal oddsDiff = new BigDecimal(order.getOdds()).subtract(new BigDecimal("1"));
        BigDecimal minAmountBd = new BigDecimal(minAmount).divide(oddsDiff, 0, RoundingMode.DOWN);
        log.info("::{}::限额-单注保底投注限额-重新计算投注:{}", indexKey, minAmountBd);
        if(minAmountBd.compareTo(singleHedgeAmount)==-1){
            return minAmountBd;
        }
        BigDecimal userQuotaBetRatio = rcsQuotaBusinessLimit.getUserQuotaBetRatio();
        log.info("::{}::限额-单注保底投注限额-取单注保底金额:{}::乘以通用单关单注限额百分比::{}", indexKey, singleHedgeAmount,userQuotaBetRatio);
        singleHedgeAmount = singleHedgeAmount.multiply(userQuotaBetRatio);
        return singleHedgeAmount;
    }
    /**
     * 获取用户单注最大赔付配置
     *
     * @param order                 订单
     * @param rcsQuotaBusinessLimit 商户
     * @return 单关配置
     */
    public RcsQuotaUserSingleNoteVo queryMatchAndTournamentMaxBetAmount(ExtendBean order, RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit) {
        String indexKey = StringUtils.isBlank(order.getOrderId()) ? order.getUserId() : order.getOrderId();
        String logKey = StringUtils.isBlank(order.getOrderId()) ? "限额" : "投注";
        RcsQuotaUserSingleNoteVo singleNoteVo = limitConfigService.getAllRcsQuotaUserSingleNote(order, rcsQuotaBusinessLimit);
        BigDecimal money;
        //特殊玩法的走if逻辑
        if (singleNoteVo.getSingleBetLimit() != null) {
            Double odds = order.getItemBean().getHandleAfterOddsValue();
            BigDecimal payLimit = singleNoteVo.getSinglePayLimit().divide(new BigDecimal(odds.toString()).subtract(new BigDecimal(NumberUtils.LONG_ONE)), 2, RoundingMode.DOWN);
            BigDecimal betLimit = singleNoteVo.getSingleBetLimit();
            money = betLimit.compareTo(payLimit) <= 0 ? betLimit : payLimit;
            singleNoteVo.setSinglePayLimit(money);
            log.info("::{}::{}-足球次要特殊玩法,最大赔付取值:{}", indexKey, logKey, money);
            return singleNoteVo;
        }
        money = singleNoteVo.getSinglePayLimit();
        if (money.compareTo(new BigDecimal(0)) == 0) {
            log.warn("::{}::{}-单注最大赔付值异常:{}", indexKey, logKey, money);
        }
        //为空转换成最大值
        Double odds = order.getItemBean().getHandleAfterOddsValue();
        if (odds > 2) {
            money = money.divide(new BigDecimal(odds.toString()).subtract(new BigDecimal(NumberUtils.LONG_ONE)), 2, RoundingMode.DOWN);
        }
        singleNoteVo.setSinglePayLimit(money);
        log.info("::{}::{}-赛事模板最大赔付结果:{}", indexKey, logKey, money);
        return singleNoteVo;
    }


    /**
     * 订单入库限额校验
     *
     * @param order                 订单
     * @param rec                   矩阵
     * @param rcsQuotaBusinessLimit 商户
     * @return 是否通过
     */
    public Boolean saveOrderAndValidateV4(ExtendBean order, String rec, RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit) {
        //商户是否停止接单
        long busAmount = limitConfigService.getBusinessAvailablePaymentV2(NumberUtils.toLong(order.getBusId()), order.getOrderId(), rcsQuotaBusinessLimit);
        //【判断商户是否停止接单】
        if (busAmount <= 0) {
            throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_LIMIT, "商户维度最大限额效验超限:" + busAmount);
        }
        //如果是虚拟赛事则转变为其他赛种用于取通用限额配置
        String sportId = order.getSportId();
        boolean contains = SdkConstants.VIRSTUAL_SPORT.contains(Integer.valueOf(sportId));
        order.setSportId(contains?"-1":sportId);
        //单注最大限额
        RcsQuotaUserSingleNoteVo rcsQuotaUserSingleNoteVo = limitConfigService.getAllRcsQuotaUserSingleNote(order, rcsQuotaBusinessLimit);
        order.setSportId(sportId);
        BigDecimal oddsValue = new BigDecimal(order.getOdds()).subtract(new BigDecimal("1"));
        BigDecimal singlePayLimit = rcsQuotaUserSingleNoteVo.getSinglePayLimit();
        //最大可下注 = 订单最大赔付/赔率
        Long orderMaxPay;
        if (oddsValue.compareTo(BigDecimal.ZERO) == 0) {
            orderMaxPay = singlePayLimit.longValue();
        } else {
            orderMaxPay = singlePayLimit.divide(oddsValue, 2, RoundingMode.HALF_UP).longValue();
        }
        orderMaxPay = handlerGuaranteeBet(order,new BigDecimal(orderMaxPay),rcsQuotaBusinessLimit,null).longValue();
        //【判断最大赔付是否足够】
        if (order.getOrderMoney() > orderMaxPay) {
            log.error("::{}::投注-最大单注限额超限,限额:{},投注额:{}", order.getOrderId(), orderMaxPay, order.getOrderMoney());
            throw new RcsServiceException(-1, "最大单注限额维度超限");
        }
        log.info("::{}::投注-最大单注赔付配置:{},最大可下注额度:{}", order.getOrderId(), rcsQuotaUserSingleNoteVo.getSinglePayLimit(), orderMaxPay);
        Long singleOrderMaxBet = null;
        //综合球种处理 忽略【单注单关限额】就是说优先判断综合赛种，那么把用户单注查询获取位置优化到下面
        if (sportIds.contains(order.getSportId())) {
            Long maxBetOrMaxPay = getTournamentTemplateJumpConfig(order.getTournamentId(), "0".equals(order.getIsScroll()) ? "1" : "0", order.getOrderId());
            log.info("::{}::投注-综合球类联赛级别的限额和限付金额:{}，联赛id={}", order.getOrderId(), maxBetOrMaxPay, order.getTournamentId());
            if (maxBetOrMaxPay != null) {
                long maxBet = new BigDecimal(maxBetOrMaxPay).divide(new BigDecimal(order.getOdds()).subtract(new BigDecimal("1")), 0, RoundingMode.DOWN).longValue();
                //忽略额度管控-单注单关限额
                singleOrderMaxBet = Math.min(maxBetOrMaxPay, maxBet) * 100;
                //特殊用户百分比
                BigDecimal percentage = limitConfigService.getUserLimitPercentage(order.getUserId(), order.getOrderId());
                singleOrderMaxBet = new BigDecimal(singleOrderMaxBet).multiply(percentage).longValue();
            }
        }
        //用户单注限额配置  上面没获取或者不是对应赛种则走
        if (singleOrderMaxBet == null) {
            BigDecimal money = rcsQuotaUserSingleNoteVo.getSinglePayLimit();
            if (rcsQuotaUserSingleNoteVo.getSingleBetLimit() != null) {
                Double odds = order.getItemBean().getHandleAfterOddsValue();
                BigDecimal payLimit = money.divide(new BigDecimal(odds.toString()).subtract(new BigDecimal(NumberUtils.LONG_ONE)), 2, RoundingMode.DOWN);
                BigDecimal betLimit = rcsQuotaUserSingleNoteVo.getSingleBetLimit();
                money = betLimit.compareTo(payLimit) <= 0 ? betLimit : payLimit;
                log.info("::{}::投注-足球次要特殊玩法,赛事模板限额:{}", order.getOrderId(), money);
            } else {
                Double odds = order.getItemBean().getHandleAfterOddsValue();
                if (odds > 2) {
                    money = money.divide(new BigDecimal(odds.toString()).subtract(new BigDecimal(NumberUtils.LONG_ONE)), 2, RoundingMode.DOWN);
                    log.info("::{}::投注-最大单注限额赔率大于:2,计算得到限额:{}", order.getOrderId(), money);
                }
            }
            singleOrderMaxBet = money.longValue();

            singleOrderMaxBet = handlerGuaranteeBet(order,new BigDecimal(singleOrderMaxBet),rcsQuotaBusinessLimit,null).longValue();
        }

        Map<String, BigDecimal> dynamicLimitMap = limitConfigService.dynamicLimitNew(order, rcsQuotaBusinessLimit.getUserQuotaRatio());
        if (dynamicLimitMap != null) {
            singleOrderMaxBet = dynamicLimitMap.get("dynamicLimit").longValue();
            int lowHighType = dynamicLimitMap.get("lowHighType").intValue();
            if (lowHighType == 1) {
                //高赔保底投注限额
                BigDecimal highOddScopeBetLimit = dynamicLimitMap.get("highOddScopeBetLimit");
                //如果单注可投额度 比保底额度小
                if (highOddScopeBetLimit != null) {
                    if (singleOrderMaxBet < highOddScopeBetLimit.longValue()) {
                        //如果单注可投额度 比保底额度小
                        singleOrderMaxBet = highOddScopeBetLimit.longValue();
                    }
                }
            }
        }
        //【扩大5%之后判断是否超限】
        log.info("::{}::投注-最大单注限额:{}", order.getOrderId(), singleOrderMaxBet);
        if (singleOrderMaxBet.compareTo(Long.MAX_VALUE) != 0) {
            singleOrderMaxBet = singleOrderMaxBet * 105 / 100;
            log.info("::{}::投注-最大单注限额扩大5%后:{}", order.getOrderId(), singleOrderMaxBet);
        }
        if (order.getOrderMoney() > singleOrderMaxBet) {
            log.warn("::{}::投注-最大单注限额超限,配置金额:{},投注金额:{}", order.getOrderId(), singleOrderMaxBet, order.getOrderMoney());
            throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_LIMIT, "当前订单超过最大赔付,最大单注限额超限:" + singleOrderMaxBet / 100);
        }
        //一些单日或者单场的维度处理
        Map<String, Object> result = luaPaidService.saveOrderV3(order, rec, rcsQuotaUserSingleNoteVo, rcsQuotaBusinessLimit);
        //此数据保存  用于mts下单后 回滚
        String luaCacheKey = "rcs:order:lua:result:" + order.getItemBean().getOrderNo();
        jedisClusterServer.setex(luaCacheKey, 7 * 24 * 60 * 60, JSONObject.toJSONString(result));
        //发送到队列做回滚
        if (!"1".equals(String.valueOf(result.get("code")))) {
            producer.sendMsg("ORDER_SAVE_ROLLBACK", JSONObject.toJSONString(result));
            throw new LogicException(Integer.parseInt(String.valueOf(result.get("code"))), String.valueOf(result.get("msg")));
        }
        return true;
    }

    private Long getTournamentTemplateJumpConfig(Long tournamentId, String matchType, String indexKey) {
        String newKey = TEMPLATE_TOURNAMENT_AMOUNT + String.format("%s_%s", tournamentId, matchType);
        String redisVal = jedisClusterServer.get(newKey);
        log.info("::{}::投注-综合球类联赛级别的限额和限付金额:{}，联赛id={},获取key:{}", indexKey, redisVal, tournamentId, newKey);
        if (!StringUtils.isEmpty(redisVal)) {
            return Long.valueOf(redisVal);
        }
        try {
            RcsTournamentTemplateJumpConfigMapper rcsTournamentTemplateJumpConfigMapper = SpringContextUtils.getBeanByClass(RcsTournamentTemplateJumpConfigMapper.class);
            log.info("::{}::投注-综合球种限额数据库查询:{},tournamentId={},matchType={}", indexKey, rcsTournamentTemplateJumpConfigMapper, tournamentId, matchType);
            RcsTournamentTemplateJumpConfig rcsTournamentTemplateJumpConfig = rcsTournamentTemplateJumpConfigMapper.selectOne(new LambdaQueryWrapper<RcsTournamentTemplateJumpConfig>()
                    .eq(RcsTournamentTemplateJumpConfig::getTournamentId, tournamentId)
                    .eq(RcsTournamentTemplateJumpConfig::getMatchType, matchType));
            log.info("::{}::投注-综合球种限额数据库查询:{}", indexKey, JSONObject.toJSONString(rcsTournamentTemplateJumpConfig));
            if (Objects.nonNull(rcsTournamentTemplateJumpConfig)) {
                jedisClusterServer.set(newKey, String.valueOf(rcsTournamentTemplateJumpConfig.getMaxSingleBetAmount()));
                return rcsTournamentTemplateJumpConfig.getMaxSingleBetAmount();
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("::{}::投注-综合球种限额数据库查询异常:", indexKey, e);
        }
        return null;
    }


    /**
     * 结算
     *
     * @param settleItem 结算参数
     * @param extendBean 订单
     * @param rec        矩阵
     * @param resVo      返回
     */
    public void prizeHandle(SettleItem settleItem, ExtendBean extendBean, String rec, RcsQuotaBusinessLimitResVo resVo) {
        String key = LimitRedisKeys.getUserSpecialLimitKey(extendBean.getUserId());
        String type = RcsLocalCacheUtils.getValue(key, LimitRedisKeys.USER_SPECIAL_LIMIT_TYPE_FIELD, jedisClusterServer::hget);
        if (StringUtils.isNotBlank(type) && type.equals("4")) {
            specialVipService.prizeHandleV3(extendBean, settleItem, rec, resVo);
        } else {
            luaPaidService.prizeHandleV3(extendBean, settleItem, rec);
        }
    }

    /**
     *拉单
     * @param dto
     * @return
     */
    public Response pullSingle(TicketResultDto dto) {
        Request<TicketResultDto> request = new Request<>();
        request.setData(dto);
        return oddinApiService.pullSingle(request);
    }

}
