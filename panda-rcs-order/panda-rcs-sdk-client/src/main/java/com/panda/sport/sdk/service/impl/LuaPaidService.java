package com.panda.sport.sdk.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.limit.LimitApiService;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.SettleItem;
import com.panda.sport.data.rcs.dto.limit.MatchLimitDataReqVo;
import com.panda.sport.data.rcs.dto.limit.MatchLimitDataVo;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaMerchantSingleFieldLimitVo;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaUserSingleNoteVo;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaUserSingleSiteQuotaVo;
import com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum;
import com.panda.sport.data.rcs.dto.special.RcsUserSpecialBetLimitConfigDTO;
import com.panda.sport.rcs.dto.limit.UserDayLimit;
import com.panda.sport.rcs.enums.OrderTypeEnum;
import com.panda.sport.rcs.enums.SportEnum;
import com.panda.sport.rcs.enums.TradeTypeEnum;
import com.panda.sport.rcs.enums.limit.UserSpecialLimitType;
import com.panda.sport.rcs.pojo.RcsBusinessMatchPaidConfig;
import com.panda.sport.rcs.pojo.RcsBusinessPlayPaidConfig;
import com.panda.sport.rcs.pojo.RcsBusinessUserPaidConfig;
import com.panda.sport.sdk.annotation.AutoInitMethod;
import com.panda.sport.sdk.bean.LNBasktballEnum;
import com.panda.sport.sdk.bean.NacosProperitesConfig;
import com.panda.sport.sdk.bean.SportIdEnum;
import com.panda.sport.sdk.constant.BaseConstants;
import com.panda.sport.sdk.constant.LimitRedisKeys;
import com.panda.sport.sdk.constant.RedisKeys;
import com.panda.sport.sdk.constant.SdkConstants;
import com.panda.sport.sdk.core.JedisClusterServer;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.util.DateUtils;
import com.panda.sport.sdk.util.FileUtil;
import com.panda.sport.sdk.util.RcsLocalCacheUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


@Singleton
@AutoInitMethod(init = "init")
public class LuaPaidService {
    private static final Logger log = LoggerFactory.getLogger(LuaPaidService.class);
    @Inject
    JedisClusterServer jedisClusterServer;
    @Inject
    LimitDelayService limitDelayService;

    private String shakeyV3;
    private String prizeShaKeyV3;
    private String rallBackShakey;
    private String rallBackShakeyVip;

    @Inject
    LimitApiService limitApiService;

    @Inject
    LimitConfigService limitConfigService;
    @Inject
    private MerchantLimitWarnService merchantLimitWarnService;

    @Inject
    private RcsPaidConfigServiceImp rcsPaidConfigService;

    public void init() {
        String text = new FileUtil().getFileTxt("/lua/orderSave_new_v3.lua");
        log.info("lua 脚本内容text:{}", text);
        shakeyV3 = jedisClusterServer.scriptLoad(text);
        if (shakeyV3 == null) {
            throw new RcsServiceException("lua脚本orderSave_new_v3加载失败");
        }

        text = new FileUtil().getFileTxt("/lua/orderPrize_new_v3.lua");

        log.info("lua 脚本内容text:{}", text);
        prizeShaKeyV3 = jedisClusterServer.scriptLoad(text);
        if (prizeShaKeyV3 == null) {
            throw new RcsServiceException("lua脚本orderPrize_new_v3加载失败");
        }

        text = new FileUtil().getFileTxt("/lua/orderSaveRollback_new_v3.lua");
        log.info("lua rallBackShakey脚本内容text:{}", text);
        rallBackShakey = jedisClusterServer.scriptLoad(text);
        if (rallBackShakey == null) {
            throw new RcsServiceException("lua脚本rallBackShakey加载失败");
        }

        text = new FileUtil().getFileTxt("/lua/orderSaveRollback_new_v3_vip.lua");
        log.info("lua rallBackShakeyVip 脚本内容text:{}", text);
        rallBackShakeyVip = jedisClusterServer.scriptLoad(text);
        if (rallBackShakeyVip == null) {
            throw new RcsServiceException("lua脚本rallBackShakeyVip加载失败");
        }
    }


    /**
     * 综合维度 优化
     * 包含用户单日，用户单场，商户单场，玩法累计
     *
     * @param extendBean               订单
     * @param rec                      矩阵
     * @param rcsQuotaUserSingleNoteVo 单关单注配置
     * @param rcsQuotaBusinessLimit    商户配置
     * @return 限额结果
     */
    public Map<String, Object> saveOrderV3(ExtendBean extendBean, String rec, RcsQuotaUserSingleNoteVo rcsQuotaUserSingleNoteVo, RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit) {
        String busId = extendBean.getBusId();
        String matchId = extendBean.getMatchId();
        String sportId = extendBean.getSportId();
        String userId = extendBean.getUserId();
        Integer tournamentLevel = extendBean.getTournamentLevel();
        String orderNo = extendBean.getOrderId();
        String suffix = "_{" + busId + "_" + matchId + "}";
        //传入的参数
        List<String> params = new ArrayList<>();
        params.add(extendBean.getDateExpect());
        params.add(extendBean.getBusId());
        params.add(extendBean.getSportId());
        params.add(extendBean.getUserId());
        params.add(extendBean.getMatchId());
        // 需求2607 Ln(4)：如果是篮球，且是Ln模式,那么联控玩法跟随主控玩法限额值
        String playId = extendBean.getPlayId();
        String playType = extendBean.getPlayType();
        if(extendBean.getSportId().equals(SportIdEnum.BASKETBALL.getId().toString())){
            String key = LimitRedisKeys.getTradingTypeStatusKey(extendBean.getMatchId(),extendBean.getPlayId(),extendBean.getItemBean().getMatchType().toString());
            log.info("::{}::单场LN模式下联控玩法额度跟随主控玩法KEY: {}",key);
            if(jedisClusterServer.exists(key)) {
                String lnValue = jedisClusterServer.get(key);
                if (lnValue.equals("4")) {
                    playId = LNBasktballEnum.getNameById(Integer.valueOf(extendBean.getPlayId())).toString();

                    //并且playType要替换成主控
                    //阶段  冠军玩法走mts/虚拟赛事 可以不设置此字段
                    if (extendBean.getItemBean().getMatchType() != 3 && !SdkConstants.VIRSTUAL_SPORT.contains(extendBean.getSportId())) {
                        playType = rcsPaidConfigService.getPlayProcess(String.valueOf(extendBean.getSportId()), playId);
                    }
                    log.info("::{}::单场LN模式下联控玩法额度跟随主控玩法:赛事阶段:{}", playId,playType);
                }
            }
        }
        params.add(playId);
        String marketId = extendBean.getMarketId();
        params.add(StringUtils.isBlank(marketId)?"":marketId);
        params.add(extendBean.getIsScroll());
        params.add(StringUtils.isBlank(playType)?"":playType);
        params.add(extendBean.getOrderId());
        params.add(extendBean.getOrderMoney().toString());
        params.add(extendBean.getRecType().toString());
        params.add(extendBean.getSelectId());
        //最高赔付金额
        BigDecimal maxPaidMoney = new BigDecimal(String.valueOf(extendBean.getItemBean().getBetAmount())).multiply(new BigDecimal(extendBean.getOdds()));
        params.add(maxPaidMoney.toPlainString());
        params.add(rec == null ? "" : rec);
        params.add("13");

        //2、用户单场限额
        BigDecimal userSingeMatchMaxPay = getRcsQuotaUserSingleSiteQuotaData(extendBean, rcsQuotaBusinessLimit);
        log.info("::{}::投注-用户单场限额:{}", extendBean.getOrderId(), userSingeMatchMaxPay);
        //特殊用户单场赔付限额
        BigDecimal specialUserAmount = getSpecialMatchUserAmount(extendBean);
        log.info("::{}::投注-用户单场特殊限额:{}", extendBean.getOrderId(), specialUserAmount);
        if (specialUserAmount.compareTo(userSingeMatchMaxPay) < 0) {
            userSingeMatchMaxPay = specialUserAmount;
            log.info("::{}::投注-用户单场【取】特殊用户单场:{}", extendBean.getOrderId(), userSingeMatchMaxPay);
        }

        //3、商户单场限额
        BigDecimal merchantSingleMatchMaxPay = getRcsQuotaMerchantSingleFieldLimitData(extendBean, rcsQuotaBusinessLimit.getBusinessSingleDayGameProportion());
        log.info("::{}::投注-商户单场限额:{}", extendBean.getOrderId(), userSingeMatchMaxPay);

        //4、玩法赔付累计限额
        BigDecimal cumulativeCompensationPlaying = rcsQuotaUserSingleNoteVo.getCumulativeCompensationPlaying();
        cumulativeCompensationPlaying = cumulativeCompensationPlaying.multiply(new BigDecimal("1.05"));
        log.info("::{}::投注-玩法累计限额:{},扩大5%后:{}", extendBean.getOrderId(), rcsQuotaUserSingleNoteVo.getCumulativeCompensationPlaying(), cumulativeCompensationPlaying);

        //1、用户单日限额
        UserDayLimit userDailyQuotaVo = limitConfigService.getUserSingleDailyLimit(sportId, userId, tournamentLevel, matchId, rcsQuotaBusinessLimit.getUserQuotaRatio(), orderNo);
        BigDecimal userDayLimit = userDailyQuotaVo.getDayCompensationTotal();
        if (userDayLimit.compareTo(userDailyQuotaVo.getDayCompensation()) > 0) {
            userDayLimit = userDailyQuotaVo.getDayCompensation();
        }
        log.info("::{}::投注-用户单日限额:{}", extendBean.getOrderId(), userDayLimit);

        params.add(cumulativeCompensationPlaying.toString());//用户最高玩法赔付
        params.add(userSingeMatchMaxPay.toPlainString());
        params.add(userDayLimit.toPlainString());
        params.add(merchantSingleMatchMaxPay.toPlainString());
        params.add(extendBean.getItemBean().getHandleAfterOddsValue().toString());


        String prefix = "RCS:RISK:" + extendBean.getDateExpect() + ":" + busId + ":" + sportId + ":";
        String userMatchKey = prefix + userId + ":" + matchId + ":" + extendBean.getIsScroll() + suffix;
        String secondKey = extendBean.getPlayId() + '_' + extendBean.getIsScroll() + '_' + playType;

        BigDecimal userMatchPlayPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.hget(userMatchKey, secondKey)).orElse("0"));
        log.info("::{}::{}-用户玩法赔付配置:{},已用:{},缓存Key:{}", userMatchPlayPaidMoney, userMatchKey + "--->" + secondKey);
        //玩法剩余  和 用户单日剩余 对比  取小的

        log.info("::{}::投注-lua 处理前参数:{}", extendBean.getOrderId(), JSON.toJSONString(params));

        List<String> keys = new ArrayList<>();
        keys.add("A" + suffix);
        ArrayList<Object> ret = (ArrayList<Object>) jedisClusterServer.evalsha(shakeyV3, keys, params);
        //lua 结果处理
        String code = String.valueOf(ret.get(0));
        String msg = String.valueOf(ret.get(1));
        log.info("::{}::投注-lua 结果处理:{}", extendBean.getOrderId(), JSON.toJSONString(ret));
        //成功需要处理用户单日额度
        if ("1".equals(code)) {
            String betDateExpect = com.panda.sport.rcs.common.DateUtils.getDateExpect(extendBean.getItemBean().getBetTime());
            String dayCompensationKey = LimitRedisKeys.getDayCompensationKey(betDateExpect, busId, extendBean.getUserId());
            long paymentAmount = new BigDecimal(ret.get(3).toString()).longValue();
            log.info("::{}::投注-lua 结果处理1:{}", extendBean.getOrderId(), paymentAmount);
            //不拆成String结构的理由：用户维度key分散在各个节点，且这个key是小key,设置好过期时间即可
            Long usedDay = jedisClusterServer.hincrBy(dayCompensationKey, extendBean.getSportId(), paymentAmount);
            Long usedDayAll = jedisClusterServer.hincrBy(dayCompensationKey, LimitRedisKeys.TOTAL_FIELD, paymentAmount);
            if (extendBean.getIsScroll().equals("1")) {
                jedisClusterServer.expire(dayCompensationKey, 24 * 60 * 60);
            } else {
                jedisClusterServer.expire(dayCompensationKey, 90 * 24 * 60 * 60);
            }
            log.info("::{}::投注-单日额度计算,key:{},赛种已用额度:{},单日总赔付:{}", extendBean.getOrderId(), dayCompensationKey, usedDay, usedDayAll);
            //增加参数  用于回滚 用户单日赛种/用户单日总限额 需要的key
            //下标为4 缓存key
            ret.add(dayCompensationKey);
            //下标为5 赛种id
            ret.add(extendBean.getSportId());
            if (usedDay > userDailyQuotaVo.getDayCompensation().doubleValue()) {
                code = "-4";
                msg = "用户单日(赛种)限额拒单,配置:" + userDailyQuotaVo.getDayCompensation().doubleValue() + "计算后:" + usedDay / 100.0;
            }
            if (usedDayAll > userDailyQuotaVo.getDayCompensationTotal().doubleValue()) {
                code = "-4";
                msg = "用户单日(总)限额拒单,配置:" + userDailyQuotaVo.getDayCompensationTotal().doubleValue() + "计算后:" + usedDayAll / 100.0;
            }
        }
        Map<String, Object> result = new HashMap<>(5);
        result.put("keys", keys);
        result.put("values", params);
        result.put("code", code);
        result.put("msg", msg);
        result.put("list", ret);
        return result;
    }

    public Map<String, Object> saveOrderV4(ExtendBean extendBean, String rec, RcsQuotaUserSingleNoteVo rcsQuotaUserSingleNoteVo, RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit) {
        String busId = extendBean.getBusId();
        String matchId = extendBean.getMatchId();
        String sportId = extendBean.getSportId();
        String userId = extendBean.getUserId();
        Integer tournamentLevel = extendBean.getTournamentLevel();
        String orderNo = extendBean.getOrderId();

        //1、用户单日限额
        UserDayLimit userDailyQuotaVo = limitConfigService.getUserSingleDailyLimit(sportId, userId, tournamentLevel, matchId, rcsQuotaBusinessLimit.getUserQuotaRatio(), orderNo);
        BigDecimal userDayLimit = userDailyQuotaVo.getDayCompensationTotal();
        if (userDayLimit.compareTo(userDailyQuotaVo.getDayCompensation()) > 0) {
            userDayLimit = userDailyQuotaVo.getDayCompensation();
        }
        log.info("::{}::投注-综合维度-用户单日限额:{}", extendBean.getOrderId(), userDayLimit);

        //2、用户单场限额
        BigDecimal userSingeMatchMaxPay = getRcsQuotaUserSingleSiteQuotaData(extendBean, rcsQuotaBusinessLimit);
        log.info("::{}::投注-综合维度-用户单场限额:{}", extendBean.getOrderId(), userSingeMatchMaxPay);
        //特殊用户单场赔付限额
        BigDecimal specialUserAmount = getSpecialMatchUserAmount(extendBean);
        log.info("::{}::投注-综合维度-用户单场特殊限额:{}", extendBean.getOrderId(), specialUserAmount);
        if (specialUserAmount.compareTo(userSingeMatchMaxPay) < 0) {
            userSingeMatchMaxPay = specialUserAmount;
            log.info("::{}::投注-综合维度-用户单场 取-特殊单场：{}", extendBean.getOrderId(), userSingeMatchMaxPay);
        }

        //3、商户单场限额
        BigDecimal merchantSingleMatchMaxPay = getRcsQuotaMerchantSingleFieldLimitData(extendBean, rcsQuotaBusinessLimit.getBusinessSingleDayLimitProportion());
        log.info("::{}::投注-综合维度-商户单场限额:{}", extendBean.getOrderId(), userSingeMatchMaxPay);

        //4、玩法赔付累计限额
        BigDecimal cumulativeCompensationPlaying = rcsQuotaUserSingleNoteVo.getCumulativeCompensationPlaying();
        cumulativeCompensationPlaying = cumulativeCompensationPlaying.multiply(new BigDecimal("1.05"));
        log.info("::{}::投注-综合维度-玩法累计限额:{}，扩大5%后:{}", extendBean.getOrderId(), rcsQuotaUserSingleNoteVo.getCumulativeCompensationPlaying(), cumulativeCompensationPlaying);

        List<Object> ret = java2evalsha(extendBean, rec, cumulativeCompensationPlaying, userSingeMatchMaxPay, merchantSingleMatchMaxPay);
        //lua 结果处理
        String code = String.valueOf(ret.get(0));
        String msg = String.valueOf(ret.get(1));
        //成功需要处理用户单日额度
        if ("1".equals(code)) {
            String betDateExpect = com.panda.sport.rcs.common.DateUtils.getDateExpect(extendBean.getItemBean().getBetTime());
            String dayCompensationKey = LimitRedisKeys.getDayCompensationKey(betDateExpect, busId, extendBean.getUserId());
            long paymentAmount = new BigDecimal(ret.get(3).toString()).longValue();
            //不拆成String结构的理由：用户维度key分散在各个节点，且这个key是小key,设置好过期时间即可
            Long usedDay = jedisClusterServer.hincrBy(dayCompensationKey, extendBean.getSportId(), paymentAmount);
            Long usedDayAll = jedisClusterServer.hincrBy(dayCompensationKey, LimitRedisKeys.TOTAL_FIELD, paymentAmount);
            if (extendBean.getIsScroll().equals("1")) {
                jedisClusterServer.expire(dayCompensationKey, 24 * 60 * 60);
            } else {
                jedisClusterServer.expire(dayCompensationKey, 90 * 24 * 60 * 60);
            }
            log.info("::{}::单日额度计算:订单:{} :key{}:赛种{}:赛种额度:{}单日总:{}", extendBean.getOrderId(), extendBean.getItemBean().getOrderNo(), dayCompensationKey, extendBean.getSportId(), usedDay, usedDayAll);
            //增加参数  用于回滚 用户单日赛种/用户单日总限额 需要的key
            //下标为4 缓存key
            ret.add(dayCompensationKey);
            //下标为5 赛种id
            ret.add(extendBean.getSportId());
            if (usedDay > userDailyQuotaVo.getDayCompensation().doubleValue()) {
                code = "-4";
                msg = "用户单日(赛种)限额拒单，配置：" + userDailyQuotaVo.getDayCompensation().doubleValue() + "计算后：" + usedDay / 100.0;
            }
            if (usedDayAll > userDailyQuotaVo.getDayCompensationTotal().doubleValue()) {
                code = "-4";
                msg = "用户单日(总)限额拒单，配置：" + userDailyQuotaVo.getDayCompensationTotal().doubleValue() + "计算后：" + usedDayAll / 100.0;
            }
        }
        Map<String, Object> result = new HashMap<>(5);
        /*result.put("keys", keys);
        result.put("values", params);*/
        result.put("code", code);
        result.put("msg", msg);
        result.put("list", ret);
        return result;
    }


    /**
     * lua 改 Java 2023-01-08
     * 原因：redis集中引起单节点故障，故舍弃lua的事务性 ，改用java代码实现
     *
     * @param extendBean
     * @param rec
     * @return
     */
    private List<Object> java2evalsha(ExtendBean extendBean, String rec, BigDecimal userMatchPlayPaidMoneyByDB, BigDecimal userMatchAllPaidAmountByDB, BigDecimal singleMatchAllPaidAmountByDB) {
        //传入的参数
        String dateExpect = extendBean.getDateExpect();
        String busId = extendBean.getBusId();
        String sportId = extendBean.getSportId();
        String userId = extendBean.getUserId();
        String matchId = extendBean.getMatchId();
        String playId = extendBean.getPlayId();
        String marketId = extendBean.getMarketId();
        String matchType = extendBean.getIsScroll();
        String playType = extendBean.getPlayType();
        String orderId = extendBean.getOrderId();
        String optionId = extendBean.getSelectId();
        String recLength = "13";
        Double odd = extendBean.getItemBean().getHandleAfterOddsValue();
        //投注金额
        Double betAmount = Double.valueOf(extendBean.getOrderMoney());
        Map<String, Double> resultTable = new HashMap<String, Double>() {{
            put("1", betAmount);
            put("2", betAmount / 2);
            put("3", betAmount * (odd - 1) * -1);
            put("4", betAmount / 2 * (odd - 1) * -1);
            put("5", 0d);
        }};

        Map<String, List<Integer>> mytable = new HashMap<String, List<Integer>>() {{
            put("0", Arrays.asList(0, 0));
            put("1", Arrays.asList(0, 1));
            put("2", Arrays.asList(0, 2));
            put("3", Arrays.asList(0, 3));
            put("4", Arrays.asList(0, 4));
            put("5", Arrays.asList(0, 5));
            put("6", Arrays.asList(0, 6));
            put("7", Arrays.asList(0, 7));
            put("8", Arrays.asList(0, 8));
            put("9", Arrays.asList(0, 9));
            put("A", Arrays.asList(1, 0));
            put("B", Arrays.asList(1, 1));
            put("C", Arrays.asList(1, 2));
            put("D", Arrays.asList(1, 3));
            put("E", Arrays.asList(1, 4));
            put("F", Arrays.asList(1, 5));
            put("G", Arrays.asList(1, 6));
            put("H", Arrays.asList(1, 7));
            put("I", Arrays.asList(1, 8));
            put("J", Arrays.asList(1, 9));
            put("K", Arrays.asList(2, 0));
            put("L", Arrays.asList(2, 1));
            put("M", Arrays.asList(2, 2));
            put("N", Arrays.asList(2, 3));
            put("O", Arrays.asList(2, 4));
            put("P", Arrays.asList(2, 5));
            put("Q", Arrays.asList(2, 6));
            put("R", Arrays.asList(2, 7));
            put("S", Arrays.asList(2, 8));
            put("T", Arrays.asList(2, 9));
            put("U", Arrays.asList(3, 0));
            put("V", Arrays.asList(3, 1));
            put("W", Arrays.asList(3, 2));
            put("X", Arrays.asList(3, 3));
            put("Y", Arrays.asList(3, 4));
            put("Z", Arrays.asList(3, 5));
            put("a", Arrays.asList(3, 6));
            put("b", Arrays.asList(3, 7));
            put("c", Arrays.asList(3, 8));
            put("d", Arrays.asList(3, 9));
            put("e", Arrays.asList(4, 0));
            put("f", Arrays.asList(4, 1));
            put("g", Arrays.asList(4, 2));
            put("h", Arrays.asList(4, 3));
            put("i", Arrays.asList(4, 4));
            put("j", Arrays.asList(4, 5));
            put("k", Arrays.asList(4, 6));
            put("l", Arrays.asList(4, 7));
            put("m", Arrays.asList(4, 8));
            put("n", Arrays.asList(4, 8));
            put("o", Arrays.asList(5, 0));
            put("p", Arrays.asList(5, 1));
            put("q", Arrays.asList(5, 2));
            put("r", Arrays.asList(5, 3));
            put("s", Arrays.asList(5, 4));
            put("t", Arrays.asList(5, 5));
            put("u", Arrays.asList(5, 6));
            put("v", Arrays.asList(5, 7));
            put("w", Arrays.asList(5, 8));
            put("x", Arrays.asList(5, 9));
            put("y", Arrays.asList(6, 0));
            put("z", Arrays.asList(6, 1));
        }};

        //组装相关key
        StringBuffer sb = new StringBuffer();
        String suffix = sb.append("_").append(busId).append("_").append(matchId).append(":").toString();
        sb.setLength(0);
        String prefix = sb.append("RCS:RISK:").append(dateExpect).append(":").append(busId).append(":").append(sportId).append(":").toString();
        sb.setLength(0);
        sb.append(userId).append(":").append(matchId).append(":").append(playId).append(":").append(marketId).append(":").append(matchType).append(":").append(playType);
        String userMatchPlayMarketKey = prefix + sb.toString() + suffix;
        sb.setLength(0);
        sb.append(userId).append(":").append(matchId).append(":").append(matchType);
        String userMatchKey = prefix + sb + suffix;
        String userKey = prefix + userId;
        String singleMatchInfoKey = prefix + matchId + ":V2" + suffix;
        sb.setLength(0);
        sb.append(matchId).append(":").append(playId).append(":").append(marketId);
        String singleMatchMarketKey = prefix + sb.toString() + suffix;


        List<Object> response = new ArrayList<>();
        String errMsg;

        //最高赔付金额
        BigDecimal maxPaidMoney = new BigDecimal(String.valueOf(extendBean.getItemBean().getBetAmount())).multiply(new BigDecimal(extendBean.getOdds()));
        Double currentPaidMoney = Double.valueOf(maxPaidMoney.toPlainString());
        /**
         * 1、玩法赔付累计限额维度计算
         */
        //当前投注项最大赔付累计, 包含本金（本金 * oddValue）
        Double optionMaxPaidMoney = jedisClusterServer.hincrByFloat(userMatchPlayMarketKey, optionId, currentPaidMoney);
        //--盘口投注额累计
        Double allOrderBetMoney = jedisClusterServer.incrByFloat(userMatchPlayMarketKey + "allOrderMoney", betAmount);

        //--盘口最大投注项赔付额(包括本金) 用于对冲计算
        String maxOptionPaidKey = userMatchPlayMarketKey + "maxOptionPaid";
        String result = jedisClusterServer.get(maxOptionPaidKey);
        Double redisMaxOptionPaid = 0d;
        if (StringUtils.isNotBlank(result)) {
            redisMaxOptionPaid = Double.valueOf(result);
        }
        if (redisMaxOptionPaid < optionMaxPaidMoney) {
            redisMaxOptionPaid = optionMaxPaidMoney;
            jedisClusterServer.set(maxOptionPaidKey, redisMaxOptionPaid.toString());
        }

        //这里涉及到对冲的逻辑：意思就是说我当前项的盈利要减去另一项的本金，剩下的就是我这个盘口应该要赔付的钱
        //tip：这里不好理解是因为它是站在盘口的维度来计算这个玩法的累计的, 有点绕，考虑优化

        //--盘口最大赔付 最大赔付的项-总投注额
        Double maxPlayPaid = redisMaxOptionPaid - allOrderBetMoney;
        String maxPlayPaidKey = userMatchPlayMarketKey + "MAX_PLAY_PAID";
        result = jedisClusterServer.get(maxPlayPaidKey);
        jedisClusterServer.set(maxPlayPaidKey, maxPlayPaid.toString());
        if (StringUtils.isBlank(result)) {
            result = "0";
        }
        //--这次算出来的盘口最大赔付-上次记录的盘口最大赔付 得到这个差值，就是该注单产生的赔付值
        Double diffVal = maxPlayPaid - Double.valueOf(result);

        //--玩法赔付值累计
        String secondKey = playId + "_" + matchType + "_" + playType;
        Double userMatchPlayPaidMoney = jedisClusterServer.incrByFloat(userMatchKey + secondKey, diffVal);
        // --与操盘后台配置的玩法累计总额比较
        if (userMatchPlayPaidMoney > Double.valueOf(userMatchPlayPaidMoneyByDB.toPlainString())) {
            errMsg = "用户玩法赔付限额拒单，配置：" + userMatchPlayPaidMoneyByDB + ",计算后：" + userMatchPlayPaidMoney;
            response.add("-2");
            response.add(errMsg);
            return response;
        }

        //矩阵数据类型
        Integer isRecType = extendBean.getRecType();
        //矩阵各个维度的结果（标识每个维度烧的输赢状态）
        String recData = rec == null ? "" : rec;

        //2、用户赛事单场限额维度计算 *********************
        //--isRecType == 0 ：一些规定的比分玩法是需要使用矩阵来计算赔付额度的
        if (isRecType == 0) {
            String userMatchKey2 = userMatchKey + "ALL_KEYS";
            List<String> allRecArray = new LinkedList<>();
            String allKeysResult = Optional.ofNullable(jedisClusterServer.get(userMatchKey2)).orElse(null);
            if (StringUtils.isNotBlank(allKeysResult)) {
                allRecArray = Arrays.asList(allKeysResult.split(","));
            }
            Double min = null;
            Integer index = 0;
            // D,X,X,X,X,X,V,X,X,X,X,X,X,D,X,X,X,X,X,V,X,X,X,X,X,X,D,X,X,X,X,X,V,X,X,X,X,X,X,D,X,X,X,X,X,V,X,X,X,X,X,X,D,X,X,X,X,X,V,X,X,X,X,X,X,D,X,X,X,X,X,V,X,X,X,X,X,X,D,X,X,X,X,X,d
            String[] matrixStatus = recData.split(",");
            for (String k : matrixStatus) {
                List<Integer> status = mytable.get(k);
                if (CollectionUtils.isEmpty(status)) {
                    break;
                }
                for (Integer win_or_lose : status) {
                    if (win_or_lose == 9) {
                        break;
                    }
                    //根据改位置的输赢状态得到 应该赔付的金额 累加到redis
                    Double money = resultTable.get(win_or_lose);
                    if (allRecArray.get(index) != null) {
                        money += Double.valueOf(allRecArray.get(index));
                    }
                    allRecArray.set(index, money.toString());
                    //找到最大赔付
                    if (min == null || min > money) {
                        min = money;
                    }
                    index++;
                }
            }
            //将累计的矩阵赔付信息重置到redis
            jedisClusterServer.set(userMatchKey2, String.join(",", allRecArray));
            //重置矩阵最大赔付值
            String recMaxPaidKey = userMatchKey + "REC_MAX_PAID";
            String recMaxPaid = jedisClusterServer.get(recMaxPaidKey);
            if (StringUtils.isEmpty(recMaxPaid)) {
                recMaxPaid = "0";
            }
            //上次的最大赔付-这次的最大赔付 = 这次产生的差值
            diffVal = Double.valueOf(recMaxPaid) - min;
            //替换矩阵的最大赔付值，这个不是累计，就是最大的那个配置
            jedisClusterServer.set(recMaxPaidKey, min.toString());
        }
        //累计这次产生的矩阵配置差值
        Double userMatchAllPaidAmount = jedisClusterServer.incrByFloat(userMatchKey + "USER_MATCH_ALL_PAID", diffVal);

        if (userMatchAllPaidAmount > Double.valueOf(userMatchAllPaidAmountByDB.toPlainString())) {
            errMsg = "用户赛事限额拒单，配置：" + userMatchAllPaidAmountByDB + ",计算后：" + userMatchAllPaidAmount;
            response.add("-3");
            response.add(errMsg);
            return response;
        }


        //3、商户赛事单场限额维度计算 *********************
        Double diffValMatch = 0d;
        if (isRecType == 0) {
            String singleMatchInfoKey2 = singleMatchInfoKey + "ALL_KEYS";
            List<String> allRecArray = new LinkedList<>();
            String allKeysResult = Optional.ofNullable(jedisClusterServer.get(singleMatchInfoKey2)).orElse(null);
            if (StringUtils.isNotBlank(allKeysResult)) {
                allRecArray = Arrays.asList(allKeysResult.split(","));
            }
            Double min = null;
            Integer index = 0;
            // D,X,X,X,X,X,V,X,X,X,X,X,X,D,X,X,X,X,X,V,X,X,X,X,X,X,D,X,X,X,X,X,V,X,X,X,X,X,X,D,X,X,X,X,X,V,X,X,X,X,X,X,D,X,X,X,X,X,V,X,X,X,X,X,X,D,X,X,X,X,X,V,X,X,X,X,X,X,D,X,X,X,X,X,d
            String[] matrixStatus = recData.split(",");
            for (String k : matrixStatus) {
                List<Integer> status = mytable.get(k);
                if (CollectionUtils.isEmpty(status)) {
                    break;
                }
                for (Integer win_or_lose : status) {
                    if (win_or_lose == 9) {
                        break;
                    }
                    //根据改位置的输赢状态得到 应该赔付的金额 累加到redis
                    Double money = resultTable.get(win_or_lose);
                    if (allRecArray.get(index) != null) {
                        money += Double.valueOf(allRecArray.get(index));
                    }
                    allRecArray.set(index, money.toString());
                    //找到最大赔付
                    if (min == null || min > money) {
                        min = money;
                    }
                    index++;
                }
            }
            //将累计的矩阵赔付信息重置到redis
            jedisClusterServer.set(singleMatchInfoKey2, String.join(",", allRecArray));
            //重置矩阵最大赔付值
            String recMaxPaidKey = singleMatchInfoKey + "REC_MAX_PAID";
            String maxPlayPaidMatch = jedisClusterServer.get(recMaxPaidKey);
            if (StringUtils.isEmpty(maxPlayPaidMatch)) {
                maxPlayPaidMatch = "0";
            }
            //上次的最大赔付-这次的最大赔付 = 这次产生的差值
            diffValMatch = Double.valueOf(maxPlayPaidMatch) - min;
            //替换矩阵的最大赔付值，这个不是累计，就是最大的那个配置
            jedisClusterServer.set(recMaxPaidKey, min.toString());

        } else {
            Double optionMaxPaidMoneyMatch = jedisClusterServer.hincrByFloat(singleMatchMarketKey, optionId, currentPaidMoney);
            Double allOrderBetMoneyMatch = jedisClusterServer.incrByFloat(singleMatchMarketKey + "allOrderMoney", betAmount);
            String redisMaxOptionPaidMatch = jedisClusterServer.get(singleMatchMarketKey + "maxOptionPaid");
            if (StringUtils.isBlank(redisMaxOptionPaidMatch)) {
                redisMaxOptionPaidMatch = "0";
            }
            if (Double.valueOf(redisMaxOptionPaidMatch) < optionMaxPaidMoneyMatch) {
                redisMaxOptionPaidMatch = optionMaxPaidMoneyMatch.toString();
            }

            jedisClusterServer.set(singleMatchMarketKey + "maxOptionPaid", redisMaxOptionPaidMatch);

            Double maxPlayPaidMatch = Double.valueOf(redisMaxOptionPaidMatch) - allOrderBetMoneyMatch;
            String redisMaxPlayPaidMatch = jedisClusterServer.get(singleMatchMarketKey + "MAX_PLAY_PAID");
            if (StringUtils.isBlank(redisMaxPlayPaidMatch)) {
                redisMaxPlayPaidMatch = "0";
            }
            jedisClusterServer.set(singleMatchMarketKey + "MAX_PLAY_PAID", maxPlayPaidMatch.toString());
            diffValMatch = maxPlayPaidMatch - Double.valueOf(redisMaxPlayPaidMatch);
            jedisClusterServer.set(singleMatchInfoKey + playId, maxPlayPaidMatch.toString());
        }

        Double singleMatchAllPaidAmount = jedisClusterServer.incrByFloat(singleMatchInfoKey + "MAX_MATCH_PAID", diffValMatch);
        if (singleMatchAllPaidAmount > Double.valueOf(singleMatchAllPaidAmountByDB.toPlainString())) {
            errMsg = "单场赛事限额拒单，配置：" + singleMatchAllPaidAmountByDB + ",计算后：" + singleMatchAllPaidAmount;
            response.add("-5");
            response.add(errMsg);
            return response;
        }

        //redis 设置过期时间
        if ("1".equals(matchType)) {
            jedisClusterServer.expire(userMatchPlayMarketKey, 24 * 60 * 60);
            jedisClusterServer.expire(userMatchKey, 24 * 60 * 60);
            jedisClusterServer.expire(singleMatchInfoKey, 24 * 60 * 60);
            jedisClusterServer.expire(singleMatchMarketKey, 24 * 60 * 60);
        }
        if ("0".equals(matchType)) {
            jedisClusterServer.expire(userMatchPlayMarketKey, 90 * 24 * 60 * 60);
            jedisClusterServer.expire(userMatchKey, 90 * 24 * 60 * 60);
            jedisClusterServer.expire(singleMatchInfoKey, 90 * 24 * 60 * 60);
            jedisClusterServer.expire(singleMatchMarketKey, 90 * 24 * 60 * 60);
        }
        response.add("1");
        response.add("SUCCESS");
        response.add(userKey);
        response.add(diffVal);
        return response;
    }


    /**
     * 获取最大最小值第三个版本
     *
     * @param order 订单信息
     * @return 综合维度限额结果
     * @Author Beulah 标识新版逻辑
     */
    public Long getUserSelectsMaxBetAmountV3(ExtendBean order, RcsQuotaBusinessLimitResVo resVo, BigDecimal playTotal) {

        String busId = order.getBusId();
        String matchId = order.getMatchId();
        String dateExpect = order.getDateExpect();
        String matchType = order.getIsScroll();
        String playId = order.getPlayId();
        String playType = order.getPlayType();
        String optionId = order.getSelectId();
        String sportId = order.getSportId();
        String marketId = order.getMarketId();
        String userId = order.getUserId();
        String isScroll = order.getIsScroll();
        Integer tournamentLevel = order.getTournamentLevel();
        BigDecimal userQuotaRatio = resVo.getUserQuotaRatio();
        String suffix = "_{" + busId + "_" + matchId + "}";
        String prefix = "RCS:RISK:" + dateExpect + ":" + busId + ":" + sportId + ":";
        String zkPlayId = order.getPlayId();
        String userMatchPlayMarketKey = prefix + userId + ":" + matchId + ":" + zkPlayId + ":" + marketId + ":" + matchType + ":" + playType + suffix;
        String userMatchKey = prefix + userId + ":" + matchId + ":" + matchType + suffix;
        String singleMatchInfoKey = prefix + matchId + ":V2" + suffix;
        /*if(order.getDataSourceCode().equals(OrderTypeEnum.REDCAT.getDataSource())){
            singleMatchInfoKey = prefix + matchId +":"+isScroll+ ":V2" + suffix;
        }*/
        singleMatchInfoKey = prefix + matchId +":"+isScroll+ ":V2" + suffix;
        String singleMatchMarketKey = prefix + matchId + ":" + playId + ":" + marketId + suffix;

        String indexKey = StringUtils.isBlank(order.getOrderId()) ? order.getUserId() : order.getOrderId();
        String logKey = StringUtils.isBlank(order.getOrderId()) ? "限额" : "投注";

        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%用户单日赔付维度处理%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        UserDayLimit userDailyQuotaVo = limitConfigService.getUserSingleDailyLimit(sportId, userId, tournamentLevel, matchId, userQuotaRatio, indexKey);
        //用户单日赔付累积
        String betDateExpect = com.panda.sport.rcs.common.DateUtils.getDateExpect(System.currentTimeMillis());
        String dayCompensationKey = LimitRedisKeys.getDayCompensationKey(betDateExpect, busId, userId);
        List<String> usedDayList = jedisClusterServer.hmget(dayCompensationKey, sportId, LimitRedisKeys.TOTAL_FIELD);
        //单日赛种已用
        BigDecimal usedDay = new BigDecimal(Optional.ofNullable(usedDayList.size() == 2 ? usedDayList.get(0) : null).orElse("0"));
        //单日总赔付已用
        BigDecimal usedDayAll = new BigDecimal(Optional.ofNullable(usedDayList.size() == 2 ? usedDayList.get(1) : null).orElse("0"));

        BigDecimal remainderSport = userDailyQuotaVo.getDayCompensation().subtract(usedDay);
        log.info("::{}::{}-用户单日赔付::赛种单日限额:{},赛种赔付已用:{},赛种赔付剩余:{}",
                indexKey, logKey, userDailyQuotaVo.getDayCompensation(), usedDay, remainderSport);

        BigDecimal remainderALL = userDailyQuotaVo.getDayCompensationTotal().subtract(usedDayAll);
        log.info("::{}::{}-用户单日赔付::赛种单日总限额:{},总赔付已用:{},总赔付剩余:{}",
                indexKey, logKey, userDailyQuotaVo.getDayCompensationTotal(), usedDayAll, remainderALL);
        //单日剩余 取值  对比赛种剩余 和 总剩余  取小的
        BigDecimal remainder = remainderSport;
        if (remainderALL.compareTo(remainder) < 0) {
            remainder = remainderALL;
        }
        //-22677950.0000
        log.info("::{}::{}-用户单日赔付计算得到最终【剩余】:{}", indexKey, logKey, remainder);


        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%用户玩法赔付维度处理%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        BigDecimal optionMaxPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.hget(userMatchPlayMarketKey, optionId)).orElse("0"));
        log.info("::{}::{}-投注项最大赔付值:{},缓存key:{}", indexKey, logKey, optionMaxPaidMoney, userMatchPlayMarketKey + "--->" + optionId);
        //当前订单 盘口累积投注金额
        BigDecimal allOrderBetMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.hget(userMatchPlayMarketKey, "allOrderMoney")).orElse("0"));
        log.info("::{}::{}-盘口累积投注金额:{},缓存key:{}", indexKey, logKey, allOrderBetMoney, userMatchPlayMarketKey + "--->allOrderMoney");
        //当前订单  盘口最多赔付多少钱(包括本金)
        BigDecimal redisMaxOptionPaid = new BigDecimal(Optional.ofNullable(jedisClusterServer.hget(userMatchPlayMarketKey, "maxOptionPaid")).orElse("0"));
        log.info("::{}::{}-盘口最大赔付:{},缓存key:{}", indexKey, logKey, redisMaxOptionPaid, userMatchPlayMarketKey + "--->maxOptionPaid");
        BigDecimal playMaxPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.hget(userMatchPlayMarketKey, "MAX_PLAY_PAID")).orElse("0"));
        log.info("::{}::{}-玩法最大赔付:{},缓存key:{}", indexKey, logKey, playMaxPaidMoney, userMatchPlayMarketKey + "--->MAX_PLAY_PAID");

        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%用户单场赔付维度处理%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        BigDecimal userSingeMatchMaxPay = getRcsQuotaUserSingleSiteQuotaData(order, resVo);
        //特殊用户单场赔付限额
        BigDecimal specialUserAmount = getSpecialMatchUserAmount(order);
        if (specialUserAmount.compareTo(userSingeMatchMaxPay) < 0) {
            userSingeMatchMaxPay = specialUserAmount;
            log.info("::{}::{}-用户单场【取】特殊会员单场赔付:{} ", indexKey, logKey, specialUserAmount);
        }
        //用户单场赔付累积
        BigDecimal userMatchAllPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.hget(userMatchKey, "USER_MATCH_ALL_PAID")).orElse("0"));
        log.info("::{}::{}-用户单场赔付已用:{},缓存Key:{}", indexKey, logKey, userMatchAllPaidMoney, userMatchKey + "--->USER_MATCH_ALL_PAID");
        BigDecimal notRecUserMatchPaidMoney = new BigDecimal(0);
        BigDecimal recUserMatchPaidMoney = new BigDecimal(0);
        //用户单场矩阵参与限额计算
        if (order.getRecType() == 0) {
            String allRecStr = jedisClusterServer.hget(userMatchKey, "ALL_KEYS");
            if (!StringUtils.isBlank(allRecStr)) {//是空的
                String[] allRecArray = allRecStr.split(",");
                int j = 0;
                BigDecimal recMinMoney = new BigDecimal(Long.MAX_VALUE);
                BigDecimal recAllMinMoney = new BigDecimal(Long.MAX_VALUE);
                Long[][] recVal = JSONObject.parseObject(order.getRecVal(), new TypeReference<Long[][]>() {
                });
                for (int i = 0; i < allRecArray.length; i++) {
                    int index = i % 13;
                    BigDecimal recMinMoneyTemp = new BigDecimal(allRecArray[i]);
                    if (recVal[j][index] < 0) {//亏损/庄家赔
                        if (recMinMoneyTemp.compareTo(recMinMoney) < 0) {
                            recMinMoney = recMinMoneyTemp;
                        }
                    }
                    if (recMinMoneyTemp.compareTo(recAllMinMoney) < 0) {
                        recAllMinMoney = recMinMoneyTemp;
                    }
                    if ((i + 1) % 13 == 0) j++;
                }

                /*//下面两句代码理解：就是用用户该玩法下的最大赔付去替换原来的矩阵最大赔付，也就是说默认如果用户在改位置输，那么其他位置就应该是赢，所以这两者去对冲值
                //此处应该写成
                BigDecimal subtract = recAllMinMoney.subtract(recMinMoney);
                notRecUserMatchPaidMoney = userMatchAllPaidMoney.add(subtract);
*/
                notRecUserMatchPaidMoney = userMatchAllPaidMoney.add(recAllMinMoney);
                recUserMatchPaidMoney = recMinMoney.multiply(new BigDecimal("-1"));
                log.info("::{}::{}-用户单场赔付矩阵最大赔付值:{},亏损最大值:{}", indexKey, logKey, recAllMinMoney, recMinMoney);
                log.info("::{}::{}-用户单场赔付矩阵额度:{},非矩阵额度:{}", indexKey, logKey, recUserMatchPaidMoney, notRecUserMatchPaidMoney);
            }
        } else {
            recUserMatchPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.hget(userMatchKey, "REC_MAX_PAID")).orElse("0"));
            recUserMatchPaidMoney = recUserMatchPaidMoney.multiply(new BigDecimal("-1"));
            log.info("::{}::{}-用户单场非矩阵查询,当场最大赔付:{},缓存key:{}", indexKey, logKey, recUserMatchPaidMoney, userMatchKey + "--->REC_MAX_PAID");
            BigDecimal marketPaidMoney = playMaxPaidMoney.subtract(optionMaxPaidMoney).add(allOrderBetMoney);

            //（赛事总赔付-矩阵赔付 = 非矩阵玩法的累计赔付）-投注项对冲
            //也就是不参与矩阵的玩法，不需要计算矩阵的赔付
            notRecUserMatchPaidMoney = userMatchAllPaidMoney.subtract(recUserMatchPaidMoney).subtract(marketPaidMoney);
            log.info("::{}::{}-用户单场非矩阵查询,盘口最大赔付:{},非矩阵赔付:{}", indexKey, logKey, marketPaidMoney, notRecUserMatchPaidMoney);
        }
        //用户单场剩余（设置 - 用户改比分最大预盈利 - 赛事剩余）
        BigDecimal tempSingle = userSingeMatchMaxPay.subtract(recUserMatchPaidMoney).subtract(notRecUserMatchPaidMoney);
        log.info("::{}::{}-用户单场对冲剩余额度:{}", indexKey, logKey, tempSingle);

        //用户单注单关限额  当前玩法剩余维度
        // 需求2607 Ln（4）：如果是篮球，且是Ln模式,那么联控玩法跟随主控玩法限额值
        if(order.getSportId().equals(SportIdEnum.BASKETBALL.getId().toString())) {
            String key = LimitRedisKeys.getTradingTypeStatusKey(matchId,zkPlayId,order.getItemBean().getMatchType().toString());
            log.info("::{}::LN模式下联控玩法额度跟随主控玩法KEY: {}",key);
            if(jedisClusterServer.exists(key)) {
                String lnValue = jedisClusterServer.get(key);
                if(lnValue.equals("4")) {
                    zkPlayId = LNBasktballEnum.getNameById(Integer.valueOf(zkPlayId)).toString();

                    //并且playType要替换成主控
                    //阶段  冠军玩法走mts/虚拟赛事 可以不设置此字段
                    if (order.getItemBean().getMatchType() != 3 && !SdkConstants.VIRSTUAL_SPORT.contains(order.getSportId())) {
                        playType = rcsPaidConfigService.getPlayProcess(String.valueOf(order.getSportId()), zkPlayId);
                    }
                    log.info("::{}::LN模式下联控玩法额度跟随主控玩法2{}::", zkPlayId);
                }
            }
        }
        String secondKey = zkPlayId + '_' + matchType + '_' + playType;
        BigDecimal userMatchPlayPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.hget(userMatchKey, secondKey)).orElse("0"));
        playMaxPaidMoney = playTotal.subtract(userMatchPlayPaidMoney);
        log.info("::{}::{}-用户玩法赔付配置:{},已用:{},剩余:{},缓存Key:{}", indexKey, logKey, playTotal, userMatchPlayPaidMoney, playMaxPaidMoney, userMatchKey + "--->" + secondKey);
        //玩法剩余  和 用户单日剩余 对比  取小的
        if (playMaxPaidMoney.compareTo(remainder) > 0) {
            playMaxPaidMoney = remainder;
            log.info("::{}::{}-玩法剩余与单日剩余比较【取】单日限额剩余赔付值:{}", indexKey, logKey, playMaxPaidMoney);
        } else {
            log.info("::{}::{}-玩法剩余与单日剩余比较【取】玩法剩余赔付值:{}", indexKey, logKey, playMaxPaidMoney);
        }
        //对冲 剩余赔付 + （盘口最大赔付-投注项最大赔付）
        BigDecimal tempPlay = playMaxPaidMoney.add(redisMaxOptionPaid).subtract(optionMaxPaidMoney);
        log.info("::{}::{}-剩余额度对冲之后玩法剩余:{}", indexKey, logKey, tempPlay);
        //玩法对冲之后得剩余值 和 用户单场剩余 对比取小
        if (tempPlay.compareTo(tempSingle) > 0) {
            playMaxPaidMoney = tempSingle;
            log.info("::{}::{}-玩法对冲剩余与单场剩余比较【取】用户单场赔付剩余:{}", indexKey, logKey, playMaxPaidMoney);
        } else {
            playMaxPaidMoney = tempPlay;
            log.info("::{}::{}-玩法对冲剩余与单场剩余比较【取】玩法赔付对冲剩余:{}", indexKey, logKey, playMaxPaidMoney);

        }
        if (playMaxPaidMoney.compareTo(new BigDecimal(Long.MAX_VALUE)) > 0) {
            playMaxPaidMoney = new BigDecimal(Long.MAX_VALUE);
        }
        long minPaid = playMaxPaidMoney.longValue();
        if (minPaid <= 0) {
            log.warn("::{}::{}-最大赔付值超出,剩余额度:{}", indexKey, logKey, minPaid);
            return 0L;
        }

        if (order.getRecType() == 0) {
            //用户赛事  矩阵最大赔付
            BigDecimal recMaxPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.hget(userMatchKey, "REC_MAX_PAID")).orElse("0"));
            //用户单场赔付  还剩下多少
            BigDecimal userMatchMaxPaidMoney = userSingeMatchMaxPay.subtract(userMatchAllPaidMoney);
            log.info("::{}::{}-用户维度计算,用户单场剩余:{}", indexKey, logKey, userMatchMaxPaidMoney);
            if (userMatchMaxPaidMoney.compareTo(remainder) > 0) {
                userMatchMaxPaidMoney = remainder;
            }
            log.info("::{}::{}-用户维度计算,用户单场剩余与单日剩余比较取值:{}", logKey, indexKey, userMatchMaxPaidMoney);
            BigDecimal userRecCurrentMaxPaidMoney = userMatchMaxPaidMoney.subtract(recMaxPaidMoney);
            String allRecStr = jedisClusterServer.hget(userMatchKey, "ALL_KEYS");
            log.info("::{}::{}-用户维度计算,矩阵:{}", indexKey, logKey, allRecStr);
            if (StringUtils.isBlank(allRecStr)) {//是空的
                minPaid = Math.min(minPaid, userRecCurrentMaxPaidMoney.longValue());
                log.info("::{}::{}-用户维度计算,没有矩阵,用户赛事赔付:{},矩阵赔付:{},配置值:{},最小额度:{}", indexKey, logKey,
                        userMatchAllPaidMoney.toPlainString(), recMaxPaidMoney.toPlainString(), userSingeMatchMaxPay, minPaid);
            } else {
                String[] allRecArray = allRecStr.split(",");
                int j = 0;
                BigDecimal recMinMoney = new BigDecimal(minPaid);
                Long[][] recVal = JSONObject.parseObject(order.getRecVal(), new TypeReference<Long[][]>() {
                });
                for (int i = 0; i < allRecArray.length; i++) {
                    int index = i % 13;
                    if (recVal[j][index] < 0) {//亏损
                        BigDecimal recMinMoneyTemp = userRecCurrentMaxPaidMoney.add(new BigDecimal(allRecArray[i]));
                        if (recMinMoneyTemp.compareTo(recMinMoney) < 0) {
                            recMinMoney = recMinMoneyTemp;
                        }
                    }

                    if ((i + 1) % 13 == 0) j++;
                }

                minPaid = Math.min(minPaid, recMinMoney.longValue());
                log.info("::{}::{}-用户维度计算,注单矩阵:{}", indexKey, logKey, order.getRecVal());
                log.info("::{}::{}-矩阵重新对比结果:{}", indexKey, logKey, minPaid);
            }

        }

        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%商户单场赔付维度处理%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        BigDecimal merchantSingleMatchMaxPay = getRcsQuotaMerchantSingleFieldLimitData(order, resVo.getBusinessSingleDayGameProportion());
        BigDecimal maxMatchPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.hget(singleMatchInfoKey, "MAX_MATCH_PAID")).orElse("0"));
        log.info("::{}::{}-赛事维度计算,赛事累积赔付:{}", indexKey, logKey, maxMatchPaidMoney);
        if (order.getRecType() == 0) {
            BigDecimal recMaxPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.hget(singleMatchInfoKey, "REC_MAX_PAID")).orElse("0"));
            log.info("::{}::{}-赛事维度计算,矩阵最大赔付:{}", indexKey, logKey, recMaxPaidMoney);
            //当前赛事剩余额度
            BigDecimal currentMatchMaxPaidMoney = merchantSingleMatchMaxPay.subtract(maxMatchPaidMoney).subtract(recMaxPaidMoney);
            String matchRecValStr = jedisClusterServer.hget(singleMatchInfoKey, "ALL_KEYS");
            log.info("::{}::{}-赛事维度计算,赛事剩余:{},赛事矩阵:{}", indexKey, logKey, currentMatchMaxPaidMoney, matchRecValStr);
            if (matchRecValStr == null) {
                minPaid = Math.min(minPaid, currentMatchMaxPaidMoney.longValue());
                log.info("::{}::{}-赛事维度计算,无矩阵数据,minPaid:{}", indexKey, logKey, minPaid);
            } else {
                String[] allRecArray = matchRecValStr.split(",");
                int j = 0;
                BigDecimal recMinMoney = new BigDecimal(minPaid);
                Long[][] recVal = JSONObject.parseObject(order.getRecVal(), new TypeReference<Long[][]>() {
                });
                for (int i = 0; i < allRecArray.length; i++) {
                    int index = i % 13;
                    if (recVal[j][index] < 0) {
                        BigDecimal recMinMoneyTemp = currentMatchMaxPaidMoney.add(new BigDecimal(allRecArray[i]));
                        if (recMinMoneyTemp.compareTo(recMinMoney) < 0) {
                            recMinMoney = recMinMoneyTemp;
                        }
                    }
                    if ((i + 1) % 13 == 0) j++;
                }

                minPaid = Math.min(minPaid, recMinMoney.longValue());
                log.info("::{}::{}-赛事维度计算,矩阵计算,注单矩阵:{}", indexKey, logKey, order.getRecVal());
                log.info("::{}::{}-赛事维度计算,矩阵计算.recMinMoney:{},minPaid:{}", indexKey, logKey, recMinMoney, minPaid);
            }
        } else {
            BigDecimal maxMatchPlayPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.hget(singleMatchMarketKey, "MAX_PLAY_PAID")).orElse("0"));
            BigDecimal maxMatchPlayBetAmountMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.hget(singleMatchMarketKey, "allOrderMoney")).orElse("0"));
            BigDecimal maxMatchPlayOptionPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.hget(singleMatchMarketKey, optionId)).orElse("0"));
            //当前投注项的剩余最大赔付
            BigDecimal currentOptionPaidMoney = merchantSingleMatchMaxPay.subtract(maxMatchPaidMoney).add(maxMatchPlayPaidMoney).subtract(maxMatchPlayPaidMoney);
            minPaid = Math.min(minPaid, currentOptionPaidMoney.longValue());
            log.info("::{}::{}-赛事维度计算,无矩阵计算,maxMatchPlayPaidMoney:{},maxMatchPlayBetAmountMoney:{}," +
                            "maxMatchPlayOptionPaidMoney:{},当前剩余额度值:{},minPaid:{}", indexKey, logKey, maxMatchPlayPaidMoney, maxMatchPlayBetAmountMoney,
                    maxMatchPlayOptionPaidMoney, currentOptionPaidMoney, minPaid);
        }
        return minPaid;
    }


    /**
     * 矩阵对冲理解
     */
    /*public static void main(String[] args) {

            BigDecimal userSingeMatchMaxPay = new BigDecimal("1000");
            BigDecimal notRecUserMatchPaidMoney;
            BigDecimal userMatchAllPaidMoney = new BigDecimal("100");

            BigDecimal recAllMinMoney = new BigDecimal("-200");
            BigDecimal recMinMoney = new BigDecimal("-500");

            BigDecimal subtract = recAllMinMoney.subtract(recMinMoney);
            notRecUserMatchPaidMoney = userMatchAllPaidMoney.add(subtract);
            System.out.println(userSingeMatchMaxPay.subtract(notRecUserMatchPaidMoney));


            notRecUserMatchPaidMoney = userMatchAllPaidMoney.add(recAllMinMoney);
            BigDecimal recUserMatchPaidMoney = recMinMoney.multiply(new BigDecimal("-1"));
            System.out.println(userSingeMatchMaxPay.subtract(recUserMatchPaidMoney).subtract(notRecUserMatchPaidMoney));
    }*/

    /**
     * 新版综合限额优化
     * 将lua中的key打散  待上线
     *
     * @param order 订单信息
     * @param resVo 商户信息
     * @return 限额值
     * @Author beulah
     */
    public Long getUserSelectsMaxBetAmountV4(ExtendBean order, RcsQuotaBusinessLimitResVo resVo, BigDecimal playTotal) {
        String indexKey = StringUtils.isBlank(order.getOrderId()) ? order.getUserId() : order.getOrderId();
        String busId = order.getBusId();
        String matchId = order.getMatchId();
        String dateExpect = order.getDateExpect();
        String matchType = order.getIsScroll();
        String playId = order.getPlayId();
        String playType = order.getPlayType();
        String optionId = order.getSelectId();
        String sportId = order.getSportId();
        String marketId = order.getMarketId();
        String userId = order.getUserId();
        Integer tournamentLevel = order.getTournamentLevel();
        BigDecimal userQuotaRatio = resVo.getUserQuotaRatio();
        String suffix = "_{" + busId + "_" + matchId + "}";
        String prefix = "RCS:RISK:" + dateExpect + ":" + busId + ":" + sportId + ":";
        String userMatchPlayMarketKey = prefix + userId + ":" + matchId + ":" + playId + ":" + marketId + ":" + matchType + ":" + playType + suffix;
        String userMatchKey = prefix + userId + ":" + matchId + ":" + matchType + suffix;
        String singleMatchInfoKey = prefix + matchId + ":V2" + suffix;
        String singleMatchMarketKey = prefix + matchId + ":" + playId + ":" + marketId + suffix;

        /**========================【用户单日】========================**/
        //用户单日限额
        UserDayLimit userDailyQuotaVo = limitConfigService.getUserSingleDailyLimit(sportId, userId, tournamentLevel, matchId, userQuotaRatio, indexKey);
        //用户单日赔付累积
        String betDateExpect = com.panda.sport.rcs.common.DateUtils.getDateExpect(System.currentTimeMillis());
        String dayCompensationKey = LimitRedisKeys.getDayCompensationKey(betDateExpect, busId, userId);
        List<String> usedDayList = jedisClusterServer.hmget(dayCompensationKey, sportId, LimitRedisKeys.TOTAL_FIELD);
        //单日赛种累积
        BigDecimal usedDay = new BigDecimal(Optional.ofNullable(!usedDayList.isEmpty() && usedDayList.size() == 2 ? usedDayList.get(0) : null).orElse("0"));
        //单日总累积
        BigDecimal usedDayAll = new BigDecimal(Optional.ofNullable(!usedDayList.isEmpty() && usedDayList.size() == 2 ? usedDayList.get(1) : null).orElse("0"));
        log.info("::{}::{}-单日赛种累积：{}，总累积：{}，Key：{}", indexKey, usedDay, usedDayAll, dayCompensationKey);
        //单日赛种剩余
        BigDecimal remainderSport = userDailyQuotaVo.getDayCompensation().subtract(usedDay);
        //单日总额度剩余
        BigDecimal remainderALL = userDailyQuotaVo.getDayCompensationTotal().subtract(usedDayAll);
        //单日剩余 取值  对比赛种剩余 和 总剩余  取小的
        BigDecimal remainder = remainderSport;
        if (remainderALL.compareTo(remainder) < 0) {
            remainder = remainderALL;
        }
        log.info("::{}::{}-单日赛种剩余：{}，总累积剩余:{}，两者取小得到:{}", indexKey, remainderSport, remainderALL, remainder);

        /**========================【用户单场】========================**/
        //用户单场限额
        BigDecimal userSingeMatchMaxPay = getRcsQuotaUserSingleSiteQuotaData(order, resVo);
        //特殊用户单场赔付限额
        BigDecimal specialUserAmount = getSpecialMatchUserAmount(order);
        if (specialUserAmount.compareTo(userSingeMatchMaxPay) < 0) {
            userSingeMatchMaxPay = specialUserAmount;
            log.info("::{}::{}-用户单场 【取】特殊会员赔付：{} ", indexKey, specialUserAmount);
        }
        //【用户赛事单场赔付累积 计算】- 分为矩阵球种和非矩阵球种两种情况计算
        BigDecimal userMatchAllPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.get(userMatchKey + "USER_MATCH_ALL_PAID")).orElse("0"));
        log.info("::{}::{}-用户单场赔付已累积：{}，Key：{}", indexKey, userMatchAllPaidMoney, userMatchKey + "USER_MATCH_ALL_PAID");
        BigDecimal notRecUserMatchPaidMoney;
        BigDecimal recUserMatchPaidMoney = new BigDecimal(0);
        //order.getRecType() == 0 比分玩法  取矩阵参与计算最大赔付
        if (order.getRecType() == 0) {
            //用户单场赔付剩余 = 用户单场-用户单场已累计
            BigDecimal userMatchMaxPaidMoney = userSingeMatchMaxPay.subtract(userMatchAllPaidMoney);
            if (userMatchMaxPaidMoney.compareTo(remainder) > 0) {
                userMatchMaxPaidMoney = remainder;
                log.info("::{}::{}-用户单场计算，赛种单日剩余 < 单场赔付剩余，取值:{}", indexKey, userMatchMaxPaidMoney.toPlainString());
            }
            BigDecimal recMaxPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.get(userMatchKey + "REC_MAX_PAID")).orElse("0"));
            String allRecStr = Optional.ofNullable(jedisClusterServer.get(userMatchKey + "ALL_KEYS")).orElse(null);
            if (StringUtils.isNotBlank(allRecStr)) {
                String[] allRecArray = allRecStr.split(",");
                int j = 0;
                BigDecimal recMinMoney = new BigDecimal(Long.MAX_VALUE);
                BigDecimal recAllMinMoney = new BigDecimal(Long.MAX_VALUE);
                Long[][] recVal = JSONObject.parseObject(order.getRecVal(), new TypeReference<Long[][]>() {
                });
                for (int i = 0; i < allRecArray.length; i++) {
                    int index = i % 13;
                    BigDecimal recMinMoneyTemp = new BigDecimal(allRecArray[i]);
                    if (recVal[j][index] < 0) {//亏损
                        if (recMinMoneyTemp.compareTo(recMinMoney) < 0) {
                            recMinMoney = recMinMoneyTemp;
                        }
                    }
                    if (recMinMoneyTemp.compareTo(recAllMinMoney) < 0) {
                        recAllMinMoney = recMinMoneyTemp;
                    }
                    if ((i + 1) % 13 == 0) j++;
                }
                notRecUserMatchPaidMoney = userMatchAllPaidMoney.add(recAllMinMoney);
                //这次的最大赔付和缓存的最大赔付的比较一下 (这里存的是负数)
                if (recMaxPaidMoney.compareTo(recMinMoney) > 0) {
                    recMinMoney = recMaxPaidMoney;
                }
                recUserMatchPaidMoney = recMinMoney.multiply(new BigDecimal("-1"));
                log.info("::{}::{}-用户单场赔付累积-重新从矩阵取值，用户单场累计:{}，矩阵最大赔付：{}，用户单场累计赔付额度：{}，Key：{}", indexKey, userMatchAllPaidMoney, recUserMatchPaidMoney, notRecUserMatchPaidMoney, userMatchKey);
            } else {
                notRecUserMatchPaidMoney = userMatchMaxPaidMoney.subtract(recMaxPaidMoney);
                log.info("::{}::{}-用户单场计算，用户赛事赔付：{}，矩阵最大赔付：{},单场配置赔付额：{}，赔付剩余-矩阵最大赔付 = 用户单场当场剩余：{}", indexKey, userMatchAllPaidMoney.toPlainString(), recMaxPaidMoney.toPlainString(), userSingeMatchMaxPay, notRecUserMatchPaidMoney);
            }
        } else {
            //用户赛事单场矩阵最大赔付
            recUserMatchPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.get(userMatchKey + "REC_MAX_PAID")).orElse("0")).multiply(new BigDecimal("-1"));
            //用户单场盘口最大赔付
            BigDecimal playMaxPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.get(userMatchPlayMarketKey + "MAX_PLAY_PAID")).orElse("0"));
            //当前盘口投注项最大赔付
            BigDecimal optionMaxPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.hget(userMatchPlayMarketKey, optionId)).orElse("0"));
            //当前盘口投注额累计
            BigDecimal allOrderBetMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.get(userMatchPlayMarketKey + "allOrderMoney")).orElse("0"));
            BigDecimal marketPaidMoney = playMaxPaidMoney.subtract(optionMaxPaidMoney).add(allOrderBetMoney);
            notRecUserMatchPaidMoney = userMatchAllPaidMoney.subtract(recUserMatchPaidMoney).subtract(marketPaidMoney);
            log.info("::{}::{}-用户单场赔付累积-非矩阵查询，用户当前矩阵最大赔付：{}，用户单场盘口最大赔付：{}，当前盘口投注项最大赔付：{}，当前盘口总累计：{}，玩法剩余：{}，用户单场累计：{}",
                    indexKey, recUserMatchPaidMoney, playMaxPaidMoney, optionMaxPaidMoney, allOrderBetMoney, marketPaidMoney, notRecUserMatchPaidMoney);
        }

        /**=========================【用户玩法累计】========================**/
        String secondKey = playId + '_' + matchType + '_' + playType;
        BigDecimal userMatchPlayPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.get(userMatchKey + secondKey)).orElse("0"));
        log.info("::{}::{}-玩法赔付累积：{}，Key：{}", indexKey, userMatchPlayPaidMoney, userMatchKey + secondKey);
        //盘口投注项最大赔付值
        //此处还是使用hash结构，因为有的计算是需要getAll出来做循环处理
        BigDecimal optionMaxPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.hget(userMatchPlayMarketKey, optionId)).orElse("0"));
        //盘口累积投注金额
        BigDecimal allOrderBetMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.get(userMatchPlayMarketKey + "allOrderMoney")).orElse("0"));
        //盘口最多赔付投注项的值(包括本金)
        BigDecimal redisMaxOptionPaid = new BigDecimal(Optional.ofNullable(jedisClusterServer.get(userMatchPlayMarketKey + "maxOptionPaid")).orElse("0"));
        //当前玩法剩余 = 总赔付配置-玩法赔付累计
        BigDecimal playMaxPaidMoney = playTotal.subtract(userMatchPlayPaidMoney);
        log.info("::{}::{}-当前玩法剩余：{}", indexKey, playMaxPaidMoney);
        //玩法剩余  和 用户单日剩余 对比  取小的
        if (playMaxPaidMoney.compareTo(remainder) > 0) {
            playMaxPaidMoney = remainder;
            log.info("::{}::{}-单日赛种剩余与单日玩法剩余比较，取值：{}", indexKey, playMaxPaidMoney);
        }
        //用户单场剩余
        BigDecimal userSingeMatchRemainder = userSingeMatchMaxPay.subtract(recUserMatchPaidMoney).subtract(notRecUserMatchPaidMoney);
        //玩法剩余
        BigDecimal tempPlay = playMaxPaidMoney.add(redisMaxOptionPaid).subtract(optionMaxPaidMoney);
        if (tempPlay.compareTo(userSingeMatchRemainder) > 0) {
            playMaxPaidMoney = userSingeMatchRemainder;
            log.info("::{}::{}-用户单场剩余 < 玩法剩余，取值：{}", indexKey, playMaxPaidMoney);
        } else {
            playMaxPaidMoney = tempPlay;
        }
        if (playMaxPaidMoney.compareTo(new BigDecimal(Long.MAX_VALUE)) > 0) {
            playMaxPaidMoney = new BigDecimal(Long.MAX_VALUE);
        }
        long minPaid = playMaxPaidMoney.longValue();
        log.info("::{}::{}-玩法维度计算，玩法最大赔付：{}，玩法累计赔付：{}，当前盘口最大赔付：{}，当前选项赔付：{}，当前盘口投注金额：{}，剩余额度：{}", indexKey,
                playTotal, userMatchPlayPaidMoney, redisMaxOptionPaid, optionMaxPaidMoney, allOrderBetMoney, minPaid);
        if (minPaid <= 0) {
            log.warn("::{}::{}-玩法维度最大赔付不够返回0， 剩余额度：{}", indexKey, minPaid);
            return 0L;
        }

        /**=========================【商户单场】========================**/
        //商户赛事单场限额-分为矩阵球种和非矩阵球种两种情况计算
        BigDecimal merchantSingleLimit = getRcsQuotaMerchantSingleFieldLimitData(order, resVo.getBusinessSingleDayGameProportion());
        if (merchantSingleLimit.compareTo(new BigDecimal(0)) <= 0) {
            log.warn("::{}::{}-商户单场赔付限额：{}", indexKey, merchantSingleLimit);
            return 0L;
        }
        //商户赛事单场累计
        BigDecimal maxMatchPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.get(singleMatchInfoKey + "MAX_MATCH_PAID")).orElse("0"));
        if (order.getRecType() == 0) {
            //商户赛事矩阵最大赔付
            BigDecimal recMaxPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.get(singleMatchInfoKey + "REC_MAX_PAID")).orElse("0"));
            //商户当前赛事剩余额度
            BigDecimal currentMatchMaxPaidMoney = merchantSingleLimit.subtract(maxMatchPaidMoney).subtract(recMaxPaidMoney);

            String matchRecValStr = Optional.ofNullable(jedisClusterServer.get(singleMatchInfoKey + "ALL_KEYS")).orElse(null);
            if (matchRecValStr == null) {
                minPaid = Math.min(minPaid, currentMatchMaxPaidMoney.longValue());
                log.info("::{}::{}-商户单场-无矩阵数据，商户单场配置：{}，商户赛事累计：{}，商户赛事剩余：{}，最终取值：{}", indexKey, merchantSingleLimit,
                        maxMatchPaidMoney.toPlainString(), currentMatchMaxPaidMoney.toPlainString(), minPaid);
            } else {
                String[] allRecArray = matchRecValStr.split(",");
                int j = 0;
                BigDecimal recMinMoney = new BigDecimal(minPaid);
                Long[][] recVal = JSONObject.parseObject(order.getRecVal(), new TypeReference<Long[][]>() {
                });
                for (int i = 0; i < allRecArray.length; i++) {
                    int index = i % 13;
                    if (recVal[j][index] < 0) {
                        BigDecimal recMinMoneyTemp = currentMatchMaxPaidMoney.add(new BigDecimal(allRecArray[i]));
                        if (recMinMoneyTemp.compareTo(recMinMoney) < 0) {
                            recMinMoney = recMinMoneyTemp;
                        }
                    }
                    if ((i + 1) % 13 == 0) j++;
                }
                minPaid = Math.min(minPaid, recMinMoney.longValue());
                log.info("::{}::{}-商户单场-有矩阵数据，商户赛事单场配置：{}，商户赛事累计：{}，商户赛事剩余：{}，最终取值：{}", indexKey, merchantSingleLimit,
                        maxMatchPaidMoney.toPlainString(), currentMatchMaxPaidMoney.toPlainString(), minPaid);
            }
        } else {
            BigDecimal maxMatchPlayPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.get(singleMatchMarketKey + "MAX_PLAY_PAID")).orElse("0"));
            BigDecimal maxMatchPlayBetAmountMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.get(singleMatchMarketKey + "allOrderMoney")).orElse("0"));
            BigDecimal maxMatchPlayOptionPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.hget(singleMatchMarketKey, optionId)).orElse("0"));
            //当前投注项的剩余最大赔付
            BigDecimal currentOptionPaidMoney = merchantSingleLimit.subtract(maxMatchPaidMoney).add(maxMatchPlayPaidMoney).subtract(maxMatchPlayPaidMoney);
            minPaid = Math.min(minPaid, currentOptionPaidMoney.longValue());
            log.info("::{}::{}-商户单场，无矩阵计算，商户单场配置：{}，商户赛事累计：{}，盘口最大赔付：{}，盘口投注累计：{}，" +
                            "盘口投注项最大赔付：{}，当前剩余额度：{}，最终取值：{}", indexKey,
                    merchantSingleLimit, maxMatchPaidMoney.toPlainString(), maxMatchPlayPaidMoney.toPlainString(), maxMatchPlayBetAmountMoney.toPlainString(),
                    maxMatchPlayOptionPaidMoney.toPlainString(), currentOptionPaidMoney.toPlainString(), minPaid);
        }
        return minPaid;
    }


    /**
     * 特殊用户赛事单场配置
     *
     * @param order 订单
     * @return 用户特殊单场
     * @Author beulah
     */
    public BigDecimal getSpecialMatchUserAmount(ExtendBean order) {
        String indexKey = StringUtils.isBlank(order.getOrderId()) ? order.getUserId() : order.getOrderId();
        String logKey = StringUtils.isBlank(order.getOrderId()) ? "限额" : "投注";
        String key = LimitRedisKeys.getUserSpecialLimitKey(order.getUserId());
        String type = RcsLocalCacheUtils.getValue(key, LimitRedisKeys.USER_SPECIAL_LIMIT_TYPE_FIELD, jedisClusterServer::hget);
        long maxValue = Long.MAX_VALUE;
        if (StringUtils.isBlank(type) || (!UserSpecialLimitType.SINGLE.getType().equals(type) && !type.equals(UserSpecialLimitType.VIP.getType()))) {
            log.info("::{}::{}-【非】特殊会员单场,默认返回:{},用户类型:{}", indexKey, logKey, maxValue, type);
            return new BigDecimal(maxValue);
        }
        //先查全部的配置
        String specialUserAmountKey = "1_-1_single_game_claim_limit";
        String specialUserAmountStr = RcsLocalCacheUtils.getValue(LimitRedisKeys.getUserSpecialLimitKey(order.getUserId()), specialUserAmountKey, jedisClusterServer::hget);
        if (StringUtils.isNotBlank(specialUserAmountStr) && !specialUserAmountStr.equals("null")) {
            log.info("::{}::{}-特殊会员单场取【全部】单场赔付:{},key:{}", indexKey, logKey, specialUserAmountStr, specialUserAmountKey);
            return new BigDecimal(specialUserAmountStr);
        }
        //再查赛种的配置
        specialUserAmountKey = "1_" + order.getSportId() + "_single_game_claim_limit";
        specialUserAmountStr = RcsLocalCacheUtils.getValue(LimitRedisKeys.getUserSpecialLimitKey(order.getUserId()), specialUserAmountKey, jedisClusterServer::hget);
        if (StringUtils.isNotBlank(specialUserAmountStr) && !specialUserAmountStr.equals("null")) {
            log.info("::{}::{}-特殊会员单场取【赛种】单场赔付:{},key:{}", indexKey, logKey, specialUserAmountStr, specialUserAmountKey);
            return new BigDecimal(specialUserAmountStr);
        }
        //足/篮 不再往下读取其他配置
        if (order.getSportId().equals("1") || order.getSportId().equals("2")) { //|| order.getSportId().equals("5")
            log.info("::{}::{}-特殊会员单场,足/篮球不再往下读取其他配置,默认返回限额:{}", indexKey, logKey, maxValue);
            return new BigDecimal(maxValue);
        }
        //再查其他赛种的配置
        specialUserAmountKey = "1_0_single_game_claim_limit";
        specialUserAmountStr = RcsLocalCacheUtils.getValue(LimitRedisKeys.getUserSpecialLimitKey(order.getUserId()), specialUserAmountKey, jedisClusterServer::hget);
        if (StringUtils.isNotBlank(specialUserAmountStr) && !specialUserAmountStr.equals("null")) {
            log.info("::{}::{}-特殊会员单场取【其他】单场赔付:{},key:{}", indexKey, logKey, specialUserAmountStr, specialUserAmountKey);
            return new BigDecimal(specialUserAmountStr);
        }
        log.info("::{}::{}-特殊会员单场,没有获取到配置....,默认返回限额:{}", indexKey, logKey, maxValue);
        return new BigDecimal(maxValue);
    }


    /**
     * 用户单场限额 新版
     * 早盘滚球分开查 减少查询次数
     *
     * @param order 订单
     * @param resVo 商户
     * @return 单场限额值
     * @Author beulah
     */
    private BigDecimal getRcsQuotaUserSingleSiteQuotaData(ExtendBean order, RcsQuotaBusinessLimitResVo resVo) {
        String indexKey = StringUtils.isBlank(order.getOrderId()) ? order.getUserId() : order.getOrderId();
        String logKey = StringUtils.isBlank(order.getOrderId()) ? "限额" : "投注";
        String limitKey;
        limitKey = LimitRedisKeys.getMatchSingleLimitKey(order.getSportId(), LimitDataTypeEnum.USER_SINGLE_LIMIT, order.getMatchId(), order.getIsScroll());
        String userSingleSiteQuota = RcsLocalCacheUtils.getValue(limitKey, jedisClusterServer::get);
        // 用户限额比例
        BigDecimal userQuotaRatio = resVo.getUserQuotaRatio();
        //特殊用户百分比
        BigDecimal percentage = limitConfigService.getUserLimitPercentage(order.getUserId(), indexKey);
        if(order.getDataSourceCode().equals(OrderTypeEnum.REDCAT.getDataSource())){
            //C01逻辑，暂时配置固定额度
            userSingleSiteQuota=String.valueOf(NacosProperitesConfig.redCatLimitConfig.getUser()) ;
            log.info("::{}::{}-C01用户单场限额:{}", indexKey, logKey, userSingleSiteQuota);
        }
        if (StringUtils.isNotBlank(userSingleSiteQuota)) {
            BigDecimal result = new BigDecimal(userSingleSiteQuota).multiply(userQuotaRatio).multiply(percentage);
            log.info("::{}::{}-用户单场赛事配置:{},限额比例:{},限额百分比:{},缓存key:{}", indexKey, logKey, userSingleSiteQuota, userQuotaRatio, percentage, limitKey);
            return result;
        }

        Integer tournamentLevel = order.getTournamentLevel() == null ? -1 : order.getTournamentLevel();
        limitKey = LimitRedisKeys.getCommonSingleLimitKey(order.getSportId(), tournamentLevel, LimitDataTypeEnum.USER_SINGLE_LIMIT, order.getIsScroll());
        userSingleSiteQuota = RcsLocalCacheUtils.getValue(limitKey, jedisClusterServer::get);
        if (StringUtils.isNotBlank(userSingleSiteQuota)) {
            BigDecimal result = new BigDecimal(userSingleSiteQuota).multiply(userQuotaRatio).multiply(percentage);
            log.info("::{}::{}-用户单场通用模板配置:{},限额比例:{},限额百分比:{},缓存key:{}", indexKey, logKey, userSingleSiteQuota, userQuotaRatio, percentage, limitKey);
            return result;
        }
        // 没读到缓存  调用接口查询
        MatchLimitDataVo resData = getMatchLimitData(order, Collections.singletonList(LimitDataTypeEnum.USER_SINGLE_LIMIT.getType()));
        RcsQuotaUserSingleSiteQuotaVo singleSiteQuotaVo = resData.getRcsQuotaUserSingleSiteQuotaVo();
        if (singleSiteQuotaVo == null && "-1".equals(order.getSportId())) {
            throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_LIMIT, "读取用户单场限额数据异常");
        }
        if (singleSiteQuotaVo == null) {
            String sportId = order.getSportId();
            order.setSportId("-1");
            BigDecimal result = getRcsQuotaUserSingleSiteQuotaData(order, resVo);
            log.info("::{}::{}-用户单场使用【其他】配置sportId=-1,取值:{}", indexKey, logKey, result);
            order.setSportId(sportId);
            return result;
        }

        BigDecimal earlyLimit = singleSiteQuotaVo.getEarlyUserSingleSiteQuota();
        BigDecimal liveLimit = singleSiteQuotaVo.getLiveUserSingleSiteQuota();
        //滚球4小时 早盘120小时
        if (singleSiteQuotaVo.getMatchId() != null) {
            //赛事模板
            jedisClusterServer.setex(LimitRedisKeys.getMatchSingleLimitKey(order.getSportId(), LimitDataTypeEnum.USER_SINGLE_LIMIT, order.getMatchId(), "0"), 120 * 60 * 60, earlyLimit.toPlainString());
            jedisClusterServer.setex(LimitRedisKeys.getMatchSingleLimitKey(order.getSportId(), LimitDataTypeEnum.USER_SINGLE_LIMIT, order.getMatchId(), "1"), 4 * 60 * 60, liveLimit.toPlainString());
        } else {
            jedisClusterServer.setex(LimitRedisKeys.getCommonSingleLimitKey(order.getSportId(), tournamentLevel, LimitDataTypeEnum.USER_SINGLE_LIMIT, "0"), 120 * 60 * 60, earlyLimit.toPlainString());
            jedisClusterServer.setex(LimitRedisKeys.getCommonSingleLimitKey(order.getSportId(), tournamentLevel, LimitDataTypeEnum.USER_SINGLE_LIMIT, "1"), 4 * 60 * 60, liveLimit.toPlainString());
        }
        log.info("::{}::{}-用户单场赔付RPC处理并更新缓存完成:{},限额比例:{},限额百分比:{}", indexKey, logKey, JSONObject.toJSONString(resData), userQuotaRatio, percentage);
        if (BaseConstants.MATCH_LIMIT_AMOUNT_TYPE_ONE.equals(order.getIsScroll())) {
            return liveLimit.multiply(userQuotaRatio).multiply(percentage);
        }
        return earlyLimit.multiply(userQuotaRatio).multiply(percentage);
    }


    /**
     * 获取商户单场限额配置
     *
     * @param order              订单
     * @param merchantQuotaRatio 商户单场限额比例
     * @return 商户单场限额配置
     * @Author beulah
     */
    private BigDecimal getRcsQuotaMerchantSingleFieldLimitData(ExtendBean order, BigDecimal merchantQuotaRatio) {
        String indexKey = StringUtils.isBlank(order.getOrderId()) ? order.getUserId() : order.getOrderId();
        String logKey = StringUtils.isBlank(order.getOrderId()) ? "限额" : "投注";
        //缓存获取
        String limitKey = LimitRedisKeys.getMatchMerchantSingleLimitKey(order.getSportId(), LimitDataTypeEnum.MERCHANT_SINGLE_LIMIT, order.getMatchId(), order.getIsScroll());
        String merchantSinglePaymentLimit = RcsLocalCacheUtils.getValue(limitKey, jedisClusterServer::get);
        Integer tournamentLevel = order.getTournamentLevel() == null ? -1 : order.getTournamentLevel();
        if(order.getDataSourceCode().equals(OrderTypeEnum.REDCAT.getDataSource())){
            //C01逻辑，暂时配置固定额度
            merchantSinglePaymentLimit=String.valueOf(NacosProperitesConfig.redCatLimitConfig.getMerchant()) ;
            log.info("::{}::{}>>C01商户单场限额:{}", indexKey, logKey,merchantSinglePaymentLimit);
        }
        if (StringUtils.isBlank(merchantSinglePaymentLimit)) {
            //赛事模板取不到 则取通用模板配置
            limitKey = LimitRedisKeys.getCommonMerchantSingleLimitKey(order.getSportId(), tournamentLevel, LimitDataTypeEnum.MERCHANT_SINGLE_LIMIT, order.getIsScroll());
            merchantSinglePaymentLimit = RcsLocalCacheUtils.getValue(limitKey, jedisClusterServer::get);
        }
        BigDecimal merchantSinglePayLimit;
        if (StringUtils.isNotBlank(merchantSinglePaymentLimit)) {
            merchantSinglePayLimit = new BigDecimal(merchantSinglePaymentLimit).multiply(merchantQuotaRatio);
            log.info("::{}::{}-商户单场缓存获取key:{},限额比例:{},限额结果:{}", indexKey, logKey, limitKey, merchantQuotaRatio, merchantSinglePayLimit);
            return merchantSinglePayLimit;
        }

        //rpc获取并设置到缓存
        if (StringUtils.isBlank(merchantSinglePaymentLimit)) {
            MatchLimitDataVo resData = getMatchLimitData(order, Collections.singletonList(LimitDataTypeEnum.MERCHANT_SINGLE_LIMIT.getType()));
            RcsQuotaMerchantSingleFieldLimitVo merchantSingleFieldLimitVo = resData.getRcsQuotaMerchantSingleFieldLimitVo();
            if (merchantSingleFieldLimitVo == null && "-1".equals(order.getSportId())) {
                throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_LIMIT, "读取商户赛事单场限额数据异常");
            }
            if (merchantSingleFieldLimitVo == null) {
                log.info("::{}::{}-商户单场使用其他配置,赛种:-1", indexKey, logKey);
                order.setSportId("-1");
                return getRcsQuotaMerchantSingleFieldLimitData(order, merchantQuotaRatio);
            }
            BigDecimal earlyLimit = new BigDecimal(merchantSingleFieldLimitVo.getEarlyMorningPaymentLimit());
            BigDecimal liveLimit = new BigDecimal(merchantSingleFieldLimitVo.getLiveBallPayoutLimit());
            //滚球4小时  赛前120小时   有matchId 来自赛事模板的数据 否则来自通用模板
            if (merchantSingleFieldLimitVo.getMatchId() == null) {
                jedisClusterServer.setex(LimitRedisKeys.getCommonMerchantSingleLimitKey(order.getSportId(), tournamentLevel, LimitDataTypeEnum.MERCHANT_SINGLE_LIMIT, "0"), 120 * 24 * 60 * 60, earlyLimit.toPlainString());
                jedisClusterServer.setex(LimitRedisKeys.getCommonMerchantSingleLimitKey(order.getSportId(), tournamentLevel, LimitDataTypeEnum.MERCHANT_SINGLE_LIMIT, "1"), 4 * 24 * 60 * 60, liveLimit.toPlainString());
            } else {
                jedisClusterServer.setex(LimitRedisKeys.getMatchMerchantSingleLimitKey(order.getSportId(), LimitDataTypeEnum.MERCHANT_SINGLE_LIMIT, order.getMatchId(), "0"), 120 * 24 * 60 * 60, earlyLimit.toPlainString());
                jedisClusterServer.setex(LimitRedisKeys.getMatchMerchantSingleLimitKey(order.getSportId(), LimitDataTypeEnum.MERCHANT_SINGLE_LIMIT, order.getMatchId(), "1"), 4 * 24 * 60 * 60, liveLimit.toPlainString());
            }
            BigDecimal result = "0".equals(order.getIsScroll()) ? earlyLimit : liveLimit;
            merchantSinglePayLimit = result.multiply(merchantQuotaRatio);
            log.info("::{}::{}-商户单场rpc处理完成,限额配置:{},限额比例:{},限额结果:{},赛种:{}", indexKey, logKey, result, merchantQuotaRatio, merchantSinglePayLimit, order.getSportId());
            return merchantSinglePayLimit;
        }
        log.info("::{}::{}-商户单场没有获取到配置,默认返回限额结果:0", indexKey, logKey);
        return new BigDecimal(0);
    }


    /**
     * 查询rpc接口  得到赛事级别的限额数据  可选多个维度
     *
     * @param order        订单
     * @param dataTypeList 标志
     * @return 赛事限额配置
     */
    private MatchLimitDataVo getMatchLimitData(ExtendBean order, List<Integer> dataTypeList) {
        Request<MatchLimitDataReqVo> request = new Request<>();
        MatchLimitDataReqVo reqVo = new MatchLimitDataReqVo();
        reqVo.setMatchId(Long.valueOf(order.getMatchId()));
        reqVo.setSportId(Integer.valueOf(order.getSportId()));
        reqVo.setTournamentLevel(order.getTournamentLevel());
        reqVo.setDataTypeList(dataTypeList);
        request.setData(reqVo);
        request.setGlobalId(MDC.get("X-B3-TraceId"));
        Response<MatchLimitDataVo> response = limitApiService.getMatchLimitData(request);
        log.info("::{}::获取rpc数据返回:{}", order.getOrderId() == null ? order.getUserId() : order.getOrderId(), JSONObject.toJSONString(response));
        if (response.getCode() != Response.SUCCESS || response.getData() == null) {
            log.info("获取rpc数据失败:{}", JSONObject.toJSONString(response));
            //throw new RcsServiceException(SdkConstants.ORDER_ERROR_CODE_LIMIT, "限额配置异常-" + response.getMsg());
        }
        MatchLimitDataVo data = response.getData();
        if (data == null) {
            data = new MatchLimitDataVo();
        }
        return data;
    }


    /**
     * @return void
     * @Description 结算矩阵处理
     * @Param [extendBean, settleItem, rec]
     * @Author max
     * @Date 14:55 2020/2/20
     **/
    public void prizeHandleV3(ExtendBean extendBean, SettleItem settleItem, String rec) {
        log.info("结算派彩处理-组装信息extendBean:{}",JSONObject.toJSONString(extendBean));

        //需求2607 Ln(4)：如果是篮球，且是Ln模式,那么联控玩法回滚主控玩法限额值
        if (extendBean.getSportId().equals(SportIdEnum.BASKETBALL.getId().toString())) {
            String key = LimitRedisKeys.getTradingTypeStatusKey(extendBean.getMatchId(), extendBean.getPlayId(), extendBean.getItemBean().getMatchType().toString());
            log.info("::{}::结算,LN模式下联控玩法额度跟随主控玩法KEY: {}",key);
            if (jedisClusterServer.exists(key)) {
                String lnValue = jedisClusterServer.get(key);
                if (lnValue.equals("4")) {
                    String zkPlayId = LNBasktballEnum.getNameById(Integer.valueOf(extendBean.getPlayId())).toString();
                    extendBean.setPlayId(zkPlayId);

                    //并且playType要替换成主控
                    //阶段  冠军玩法走mts/虚拟赛事 可以不设置此字段
                    if (extendBean.getItemBean().getMatchType() != 3 && !SdkConstants.VIRSTUAL_SPORT.contains(extendBean.getSportId())) {
                        String playType = rcsPaidConfigService.getPlayProcess(String.valueOf(extendBean.getSportId()), zkPlayId);
                        extendBean.setPlayType(playType);
                    }
                    log.info("::{}::结算,LN模式下联控玩法额度跟随主控玩法2{}", zkPlayId);
                }
            }
        }


        //世界杯特殊处理
        if (settleItem.getSettleAmount() != null) {
            log.info("::{}结算走延迟::", settleItem.getOrderNo());
            qatar(extendBean, settleItem);
            return;
        }

        String dateExpect = DateUtils.getDateExpect(settleItem.getSettleTime());
        Long currentPaidAmount = limitConfigService.businessLimitIncrBy(settleItem.getSettleTime(), extendBean.getBusId(), extendBean.getProfit());
        log.info("结算派彩处理-商户单日:{}本次金额:{}:最终金额:{}", settleItem.getOrderNo(), extendBean.getProfit(), currentPaidAmount);
        RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimitResVo = limitConfigService.getBusinessLimit(Long.valueOf(extendBean.getBusId()));
        Long businessLimit = rcsQuotaBusinessLimitResVo.getBusinessSingleDayLimit();
        log.info("结算派彩处理-商户单日最大赔付判断{},{},{},{}", settleItem.getOrderNo(), currentPaidAmount, businessLimit, currentPaidAmount >= businessLimit);
        String stopKey = String.format(RedisKeys.PAID_DATE_BUS_STOP_REDIS_CACHE, dateExpect, extendBean.getBusId());
        if (currentPaidAmount >= businessLimit) {
            jedisClusterServer.set(stopKey, BaseConstants.MERCHANT_STOP_ORDER_SIGN);
        } else {
            jedisClusterServer.set(stopKey, "0");
        }
        // 商户限额预警消息
        merchantLimitWarnService.sendMsg(Long.valueOf(extendBean.getBusId()), currentPaidAmount, businessLimit, dateExpect, settleItem.getOrderNo(), rcsQuotaBusinessLimitResVo.getBusinessName());

		/*if(!"1".equals(extendBean.getSportId())) {//足球才往下做矩阵回滚
			log.warn("不是足球，不做矩阵回滚，结算结束：{},settleItem:{}",JSONObject.toJSONString(extendBean),JSONObject.toJSONString(settleItem));
			return;
		}*/

		/*if(StringUtils.isEmpty(rec)){
			log.error("sdk-client 结算矩阵异常,settleItem:{}",JSONObject.toJSON(settleItem));
			return;
		}*/

        Map<String, Object> result = new HashMap<>(1);
        List<String> keys = new ArrayList<>();
        String busId = extendBean.getBusId();
        String matchId = extendBean.getMatchId();
        String suffix = "_{" + busId + "_" + matchId + "}";
        keys.add("A" + suffix);

        extendBean.getItemBean().setOddsValue(new BigDecimal(extendBean.getOdds()).multiply(new BigDecimal("100000")).doubleValue());
        //获取商户配置
        RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit = limitConfigService.getBusinessLimit(Long.valueOf(extendBean.getBusId()));

        List<String> params = setParams(extendBean, rec, rcsQuotaBusinessLimit);

        ArrayList<Object> ret = (ArrayList<Object>) jedisClusterServer.evalsha(prizeShaKeyV3, keys, params);
        String code = String.valueOf(ret.get(0));
        if ("1".equals(code)) {
            //兼容时间取不到值
            Long opTime = settleItem.getBetTime();
            if (opTime == null) {
                opTime = settleItem.getSettleTime();
            }
            //用户赛种单日限额 和 用户单日限额 回滚
            String betDateExpect = com.panda.sport.rcs.common.DateUtils.getDateExpect(opTime);
            String dayCompensationKey = LimitRedisKeys.getDayCompensationKey(betDateExpect, busId, extendBean.getUserId());
            long paymentAmount = new BigDecimal(ret.get(3).toString()).longValue();
            Long usedDay = 0L;
            Long usedDayAll = 0L;
            if (settleItem.getSettleAmount() != null) {
                BigDecimal dif = new BigDecimal(extendBean.getOdds()).multiply(new BigDecimal(extendBean.getOrderMoney())).subtract(new BigDecimal(settleItem.getSettleAmount()));
                // BigDecimal dif = new BigDecimal(extendBean.getOrderMoney()).subtract(new BigDecimal(settleItem.getSettleAmount()));
                Long difVal = dif.longValue() * (-1);
                usedDay = jedisClusterServer.hincrBy(dayCompensationKey, extendBean.getSportId(), difVal);
                usedDayAll = jedisClusterServer.hincrBy(dayCompensationKey, LimitRedisKeys.TOTAL_FIELD, difVal);
                log.info("结算差异：order ::{}::,difVal:{}", extendBean.getOrderId(), difVal);
            } else {
                usedDay = jedisClusterServer.hincrBy(dayCompensationKey, extendBean.getSportId(), paymentAmount);
                usedDayAll = jedisClusterServer.hincrBy(dayCompensationKey, LimitRedisKeys.TOTAL_FIELD, paymentAmount);
            }
            //jedisClusterServer.incrByFloat(ret.get(2), Double.parseDouble(String.valueOf(ret.get(3))));
            log.info("::{}:: 结算派彩处理-用户-赛种单日/单日限额缓存处理 {}={}:{}:{}", settleItem.getOrderNo(), dayCompensationKey, paymentAmount, usedDay, usedDayAll);
            //log.info(settleItem.getOrderNo() + "结算派彩处理-用户-赛种单日/单日限额缓存处理" + dayCompensationKey + "=" + paymentAmount + ":" + usedDay + ":" + usedDayAll);
        }
        result.put("keys", keys);
        result.put("values", params);
        result.put("code", code);
        result.put("msg", ret.get(1));

        log.info("结算派彩处理成功：order:{},result:{}", JSONObject.toJSON(extendBean), JSONObject.toJSONString(result));
    }

    /**
     * 卡塔尔世界杯特殊处理  只处理商户单日 和用户单日
     *
     * @param extendBean 订单
     * @param settleItem 结算参数
     */
    public void qatar(ExtendBean extendBean, SettleItem settleItem) {
        limitDelayService.initLimitDelayVo(extendBean, settleItem);
    }

    /**
     * @return void
     * @Description 获取矩阵参数
     * @Param [extendBean, rec]
     * @Author max
     * @Date 14:55 2020/2/20
     **/
    public List<String> setParams(ExtendBean extendBean, String rec, RcsQuotaBusinessLimitResVo rcsQuotaBusinessLimit) {
        //传入的参数
        List<String> params = new ArrayList<>();
        params.add(extendBean.getDateExpect());
        params.add(extendBean.getBusId());
        params.add(extendBean.getSportId());
        params.add(extendBean.getUserId());
        params.add(extendBean.getMatchId());
        params.add(extendBean.getPlayId());
        params.add(extendBean.getMarketId());
        params.add(extendBean.getIsScroll());
        params.add(extendBean.getPlayType());
        params.add(extendBean.getOrderId());
        params.add(extendBean.getOrderMoney().toString());
        params.add(extendBean.getRecType().toString());
        params.add(extendBean.getSelectId());
        //最高赔付金额
        Double maxPaidMoney = extendBean.getItemBean().getBetAmount() * extendBean.getItemBean().getHandleAfterOddsValue();
        params.add(maxPaidMoney.toString());
        params.add(rec);
        params.add("13");


        //用户单注单关限额(单注投注赔付限额/玩法累计赔付限额)
        RcsQuotaUserSingleNoteVo rcsQuotaUserSingleNoteVo = limitConfigService.getRcsQuotaUserSingleNoteVoNew(extendBean, rcsQuotaBusinessLimit);

        //用户单日限额
        UserDayLimit userDailyQuotaVo = limitConfigService.getUserDailyLimit(extendBean);
        BigDecimal userDayLimit = userDailyQuotaVo.getDayCompensationTotal();
        if (userDayLimit.compareTo(userDailyQuotaVo.getDayCompensation()) > 0) {
            userDayLimit = userDailyQuotaVo.getDayCompensation();
        }
        //用户单场限额
        BigDecimal userSingeMatchMaxPay = getRcsQuotaUserSingleSiteQuotaData(extendBean, rcsQuotaBusinessLimit);

        //商户单场限额
        BigDecimal merchantSingleMatchMaxPay = getRcsQuotaMerchantSingleFieldLimitData(extendBean, rcsQuotaBusinessLimit.getBusinessSingleDayLimitProportion());

        //用户累计玩法佩服
        //params.add(userPlayConfig.getPlayMaxPay().toString());
        params.add(rcsQuotaUserSingleNoteVo.getCumulativeCompensationPlaying().toString());

        //用户单场赔付
        params.add(userSingeMatchMaxPay.toPlainString());
        //用户单日赔付
        params.add(userDayLimit.toPlainString());

        //商户单场限额
//		params.add(matchConfig.getMatchMaxPayVal().toPlainString());
        params.add(merchantSingleMatchMaxPay.toString());

        params.add(extendBean.getItemBean().getHandleAfterOddsValue().toString());
        params.add(extendBean.getProfit().toString());
        return params;
    }

    public boolean rallBackShakey(String body) {
        JSONObject src = JSONObject.parseObject(body);
        try {
            if (!src.containsKey("code")) {
                log.warn("保存订单回滚，消息数据不包含code编码，不做处理！msg:{}", src.toJSONString());
                return true;
            }
            // -1：订单已存在  -2：用户玩法赔付拒单  -3：用户赛事限额拒单  -4：用户单日限额拒单 -5：单场赛事限额拒单   1：成功
            Integer code = src.getInteger("code");
            Integer orderStatus = src.getInteger("orderStatus");
            if (orderStatus == null) {
                orderStatus = -1;
            }
            if (code == -1) {
                log.warn("订单重复不做回滚处理！msg:{}", src.toJSONString());
                return true;
            } else if (code == -4 || orderStatus == 2) {//用户单日拒单需要先把用户的加上 infoStatus表示 mts 或者pa拒单
                JSONArray array = src.getJSONArray("list");
//        		String key = array.getString(2);
                String diffVal = array.getString(3);
//    			redisClient.incrByFloat(key, Double.parseDouble(diffVal));
                String dayCompensationKey = array.getString(4);
                log.info("code -4数据回滚处理开始:{}:赛种{}:额度{},单日回滚前:{}:{}", dayCompensationKey, array.getString(5), diffVal,
                        jedisClusterServer.hget(dayCompensationKey, array.getString(5)), jedisClusterServer.hget(dayCompensationKey, "total"));
                jedisClusterServer.hincrBy(dayCompensationKey, array.getString(5), Long.valueOf(diffVal) * -1);
                jedisClusterServer.hincrBy(dayCompensationKey, "total", Long.valueOf(diffVal) * -1);
                log.info("code -4数据回滚处理完成:{}:赛种{}:额度{},单日回滚后:{}:{}", dayCompensationKey, array.getString(5), diffVal,
                        jedisClusterServer.hget(dayCompensationKey, array.getString(5)), jedisClusterServer.hget(dayCompensationKey, "total"));
                code = 1;//重置为1做全量回滚
            }

            List<String> keysList = src.getJSONArray("keys").toJavaList(String.class);
            List<String> valuesList = src.getJSONArray("values").toJavaList(String.class);
            valuesList.add(String.valueOf(code));

            Object ret = jedisClusterServer.evalsha(rallBackShakey, keysList, valuesList);
            JSONArray jsonArr = JSONObject.parseArray(JSONObject.toJSONString(ret));
            log.info("数据回滚结果返回：{},参数：{}", jsonArr.toJSONString(), src.toJSONString());

        } catch (Exception e) {
            log.error("数据回滚异常:{}:", src, e);
            log.error(e.getMessage(), e);
        }

        return true;
    }

    public boolean rallBackShakeyVip(String body) {
        JSONObject src = JSONObject.parseObject(body);
        try {
            if (!src.containsKey("code")) {
                log.warn("保存订单回滚，消息数据不包含code编码，不做处理！msg:{}", src.toJSONString());
                return true;
            }
            // -1：订单已存在  -2：用户玩法赔付拒单  -3：用户赛事限额拒单  -4：用户单日限额拒单 -5：单场赛事限额拒单   1：成功
            Integer code = src.getInteger("code");
//			if(code == -1 ) {
//				log.warn("订单重复不做回滚处理！msg:{}",src.toJSONString());
//				return true;
//			}

            List<String> keysList = src.getJSONArray("keys").toJavaList(String.class);
            List<String> valuesList = src.getJSONArray("values").toJavaList(String.class);
            valuesList.add(String.valueOf(code));

            Object ret = jedisClusterServer.evalsha(rallBackShakeyVip, keysList, valuesList);
            JSONArray jsonArr = JSONObject.parseArray(JSONObject.toJSONString(ret));
            log.info("vip数据回滚结果返回：{},参数：{}", jsonArr.toJSONString(), src.toJSONString());

        } catch (Exception e) {
            log.error("vip数据回滚异常:{}:", src, e);
            log.error(e.getMessage(), e);
        }

        return true;
    }


    /**
     * 用户标签缓存刷新
     *
     * @return
     */
    public boolean updateUserTag(String body) {
        JSONObject src = JSONObject.parseObject(body);
        Long userId = src.getLong("userId");
        String tagId = src.getString("tagId");
        try {
//            jedisClusterServer.set(LimitRedisKeys.getTagtKey() + userId.toString(), tagId);
            jedisClusterServer.setex(LimitRedisKeys.getTagtKey() + userId, 12 * 60 * 60, tagId);
            log.info("用户：{} 标签：{} 缓存刷新完成", userId, tagId);
        } catch (Exception e) {
            log.error("用户：{} 标签：{} 缓存刷新异常:{}:", userId, tagId, e);
            log.error(e.getMessage(), e);
        }
        return true;
    }


    // 1.Ln模式下需要玩法赔付额度跟随主控玩法
    // 2.Ln模式下需要预约投注赔付额度跟随主控玩法
    // 方案：联控玩法或许额度(抵扣额度)时，使用主控玩法id，不使用自己本事玩法id和玩法阶段
    //
    public void setLNPlayIdAndPlayType(ExtendBean extendBean){
        // 需求2607 Ln(4)：如果是篮球，且是Ln模式,那么联控玩法跟随主控玩法限额值
        String playId = extendBean.getPlayId();
        String playType = extendBean.getPlayType();
        if(extendBean.getSportId().equals(SportIdEnum.BASKETBALL.getId().toString())){
            String key = LimitRedisKeys.getTradingTypeStatusKey(
                    extendBean.getMatchId(),extendBean.getPlayId(),extendBean.getItemBean().getMatchType().toString()
            );
            log.info("::{}::单场LN模式下联控玩法额度跟随主控玩法KEY: {}",key);
            if(jedisClusterServer.exists(key)) {
                String lnValue = jedisClusterServer.get(key);
                if (lnValue.equals("4")) {
                    playId = LNBasktballEnum.getNameById(Integer.valueOf(extendBean.getPlayId())).toString();

                    //并且playType要替换成主控
                    //阶段  冠军玩法走mts/虚拟赛事 可以不设置此字段
                    if (extendBean.getItemBean().getMatchType() != 3 && !SdkConstants.VIRSTUAL_SPORT.contains(extendBean.getSportId())) {
                        playType = rcsPaidConfigService.getPlayProcess(String.valueOf(extendBean.getSportId()), playId);
                    }
                    log.info("::{}::单场LN模式下联控玩法额度跟随主控玩法:赛事阶段:{}", playId,playType);
                }
            }
        }

        extendBean.setPlayType(playType);
        extendBean.setPlayId(playId);
    }

}
