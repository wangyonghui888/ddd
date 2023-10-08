package com.panda.sport.sdk.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.SettleItem;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaUserSingleNoteVo;
import com.panda.sport.sdk.annotation.AutoInitMethod;
import com.panda.sport.sdk.constant.LimitRedisKeys;
import com.panda.sport.sdk.core.JedisClusterServer;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.util.FileUtil;
import com.panda.sport.sdk.util.RcsLocalCacheUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;


@Singleton
@AutoInitMethod(init = "init")
public class SpecialVipService {
    private static final Logger log = LoggerFactory.getLogger(SpecialVipService.class);
    @Inject
    JedisClusterServer jedisClusterServer;
    private String shakeyV3;
    private String prizeShaKeyV3;
    @Inject
    LuaPaidService luaPaidService;
    @Inject
    LimitConfigService limitConfigService;

    public void init() {
        String text = new FileUtil().getFileTxt("/lua/orderSave_new_v3_vip.lua");
        log.info("orderSave_new_v3_vip 脚本内容text:{}", text);
        shakeyV3 = jedisClusterServer.scriptLoad(text);
        if (shakeyV3 == null) {
            throw new RcsServiceException("lua脚本 orderSave_new_v3_vip 加载失败");
        }
        text = new FileUtil().getFileTxt("/lua/orderPrize_new_v3_vip.lua");
        log.info("orderPrize_new_v3_vip 脚本内容text:{}", text);
        prizeShaKeyV3 = jedisClusterServer.scriptLoad(text);
        if (prizeShaKeyV3 == null) {
            throw new RcsServiceException("lua脚本 orderPrize_new_v3_vip 加载失败");
        }
    }

    public Map<String, Object> saveOrderV3(ExtendBean extendBean, String rec) {
        Map<String, Object> result = new HashMap<>(1);

        String busId = extendBean.getBusId();
        String matchId = extendBean.getMatchId();
        String suffix = "_{" + busId + "_" + matchId + "}";

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
        BigDecimal maxPaidMoney = new BigDecimal(String.valueOf(extendBean.getItemBean().getBetAmount())).multiply(new BigDecimal(extendBean.getOdds()));
        params.add(maxPaidMoney.toPlainString());
        params.add(rec == null ? "" : rec);
        params.add("13");
        //用户单场限额
        BigDecimal userSingeMatchMaxPay = luaPaidService.getSpecialMatchUserAmount(extendBean);
//		params.add(rcsQuotaUserSingleNoteVo.getCumulativeCompensationPlaying().toString());//用户最高玩法赔付
        params.add(userSingeMatchMaxPay.toPlainString());
//		params.add(userDayLimit.toPlainString());
//		params.add(merchantSingleMatchMaxPay.toPlainString());
        params.add(extendBean.getItemBean().getHandleAfterOddsValue().toString());
        List<String> keys = new ArrayList<>();
        keys.add("A" + suffix);
        ArrayList<Object> ret = (ArrayList<Object>) jedisClusterServer.evalsha(shakeyV3, keys, params);
        String code = String.valueOf(ret.get(0));
        String msg = String.valueOf(ret.get(1));
        result.put("keys", keys);
        result.put("values", params);
        result.put("code", code);
        result.put("msg", msg);
        result.put("list", ret);
        return result;
    }

    /**
     * 获取最大最小值第四个版本
     *
     * @param order
     * @return
     */
    public Long getUserSelectsMaxBetAmountV4(ExtendBean order, BigDecimal userSingeMatchMaxPay) {
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
        String suffix = "_{" + busId + "_" + matchId + "}";
        String prefix = "RCS:RISK:SPECIAL:" + dateExpect + ":" + busId + ":" + sportId + ":";
        String userMatchPlayMarketKey = prefix + userId + ":" + matchId + ":" + playId + ":" + marketId + ":" + matchType + ":" + playType + suffix;
        String userMatchKey = prefix + userId + ":" + matchId + suffix;

        //用户单场赔付累积
        BigDecimal userMatchAllPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.hget(userMatchKey , "USER_MATCH_ALL_PAID")).orElse("0"));
        //String userMatchAllPaid = RcsLocalCacheUtils.getValue(userMatchKey, "USER_MATCH_ALL_PAID", jedisClusterServer::hget);
        log.info("::{}::额度查询-VIP用户单场赔付累积：{}:RcsLocalCacheUtils-缓存:{}", order.getOrderId() == null ? order.getUserId() : order.getOrderId(), userMatchAllPaidMoney, userMatchKey+"USER_MATCH_ALL_PAID");

        BigDecimal notRecUserMatchPaidMoney = new BigDecimal(0);
        BigDecimal recUserMatchPaidMoney = new BigDecimal(0);
        /**如果是矩阵 重新计算**************/
        if (order.getRecType() == 0) {
            String allRecStr = Optional.ofNullable(jedisClusterServer.hget(userMatchKey ,"ALL_KEYS")).orElse(null);
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
                recUserMatchPaidMoney = recMinMoney.multiply(new BigDecimal("-1"));
                log.info("::{}::额度查询-VIP用户单场赔付累积重新从矩阵取值{}:缓存Key:{} , 矩阵额度：{}，单场累计：{}", order.getOrderId() == null ? order.getUserId() : order.getOrderId(), userMatchAllPaidMoney, userMatchKey, recUserMatchPaidMoney, notRecUserMatchPaidMoney);
            }
        } else {
            recUserMatchPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.hget(userMatchKey , "REC_MAX_PAID")).orElse("0"));
            recUserMatchPaidMoney = recUserMatchPaidMoney.multiply(new BigDecimal("-1"));
            BigDecimal playMaxPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.hget(userMatchPlayMarketKey , "MAX_PLAY_PAID")).orElse("0"));
            BigDecimal optionMaxPaidMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.hget(userMatchPlayMarketKey, optionId)).orElse("0"));
            BigDecimal allOrderBetMoney = new BigDecimal(Optional.ofNullable(jedisClusterServer.hget(userMatchPlayMarketKey , "allOrderMoney")).orElse("0"));
            BigDecimal marketPaidMoney = playMaxPaidMoney.subtract(optionMaxPaidMoney).add(allOrderBetMoney);
            notRecUserMatchPaidMoney = userMatchAllPaidMoney.subtract(recUserMatchPaidMoney).subtract(marketPaidMoney);
            log.info("::{}::额度查询-VIP用户单场赔付-非矩阵查询,当前矩阵最大赔付:{},结果：{}", order.getOrderId() == null ? order.getUserId() : order.getOrderId(), recUserMatchPaidMoney, notRecUserMatchPaidMoney);
        }
        BigDecimal resAmount = userSingeMatchMaxPay.subtract(recUserMatchPaidMoney).subtract(notRecUserMatchPaidMoney);
        if (resAmount.compareTo(new BigDecimal(Long.MAX_VALUE)) > 0) {
            resAmount = new BigDecimal(Long.MAX_VALUE);
        }
        return resAmount.longValue();
    }

    /**
     * @return void
     * @Description 结算矩阵处理
     * @Param [extendBean, settleItem, rec]
     * @Author max
     * @Date 14:55 2020/2/20
     **/
    public void prizeHandleV3(ExtendBean extendBean, SettleItem settleItem, String rec,RcsQuotaBusinessLimitResVo resVo) {

        Map<String, Object> result = new HashMap<>(1);
        List<String> keys = new ArrayList<>();
        String busId = extendBean.getBusId();
        String matchId = extendBean.getMatchId();
        String suffix = "_{" + busId + "_" + matchId + "}";
        keys.add("A" + suffix);

        extendBean.getItemBean().setOddsValue(new BigDecimal(extendBean.getOdds()).multiply(new BigDecimal("100000")).doubleValue());
        List<String> params = setParams(extendBean, rec,resVo);

        ArrayList<Object> ret = (ArrayList<Object>) jedisClusterServer.evalsha(prizeShaKeyV3, keys, params);
        String code = String.valueOf(ret.get(0));

        result.put("keys", keys);
        result.put("values", params);
        result.put("code", code);
        result.put("msg", ret.get(1));
        log.info("vip结算派彩处理成功：order:{},result:{}", JSONObject.toJSON(extendBean), JSONObject.toJSONString(result));
    }

    /**
     * @return void
     * @Description 获取矩阵参数
     * @Param [extendBean, rec]
     * @Author max
     * @Date 14:55 2020/2/20
     **/
    public List<String> setParams(ExtendBean extendBean, String rec,RcsQuotaBusinessLimitResVo resVo) {
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

//		RcsBusinessPlayPaidConfig userPlayConfig = userPlayMaxPaid.getPlayConfig(extendBean);
//		if (userPlayConfig == null) {
//			throw new RcsServiceException(-1, "用户玩法配置为空");
//		}
        //用户单注单关限额(单注投注赔付限额/玩法累计赔付限额)
        RcsQuotaUserSingleNoteVo rcsQuotaUserSingleNoteVo = limitConfigService.getRcsQuotaUserSingleNoteVoNew(extendBean,  resVo);


//		RcsBusinessUserPaidConfig userConfig = userMatchMaxPaid.getUserConfig(extendBean);
//		if (userConfig == null) {
//			throw new RcsServiceException(-1, "用户单场或者单日配置为空");
//		}
//		//用户单日限额
//		UserDayLimit userDailyQuotaVo = limitConfigService.getUserDailyLimit(extendBean);
//		BigDecimal userDayLimit = userDailyQuotaVo.getDayCompensationTotal();
//		if(userDayLimit.compareTo(userDailyQuotaVo.getDayCompensation())>0){
//			userDayLimit = userDailyQuotaVo.getDayCompensation();
//		}
        //用户单场限额
        BigDecimal userSingeMatchMaxPay = luaPaidService.getSpecialMatchUserAmount(extendBean);

//		RcsBusinessMatchPaidConfig matchConfig = singleMacthMaxPaid.getMatchPaidConfig(extendBean);
//		if (matchConfig == null) {
//			throw new RcsServiceException(-1, "单场赛事配置为空");
//		}
        //商户单场限额
//		RcsQuotaMerchantSingleFieldLimitVo merchantSingleFieldLimitVo = getRcsQuotaMerchantSingleFieldLimitData(extendBean);
//		BigDecimal merchantSingleMatchMaxPay = new BigDecimal(merchantSingleFieldLimitVo.getEarlyMorningPaymentLimit());
//		if(extendBean.getIsScroll().equals("1")){
//			merchantSingleMatchMaxPay = new BigDecimal(merchantSingleFieldLimitVo.getLiveBallPayoutLimit());
//		}

        //用户累计玩法佩服
        //params.add(userPlayConfig.getPlayMaxPay().toString());
        //params.add(rcsQuotaUserSingleNoteVo.getCumulativeCompensationPlaying().toString());

        //用户单场赔付
        params.add(userSingeMatchMaxPay.toPlainString());
        //用户单日赔付
        //params.add(userDayLimit.toPlainString());

        //商户单场限额
//		params.add(matchConfig.getMatchMaxPayVal().toPlainString());
        //params.add(merchantSingleMatchMaxPay.toString());

        params.add(extendBean.getItemBean().getHandleAfterOddsValue().toString());
        params.add(extendBean.getProfit().toString());
        return params;
    }


}
