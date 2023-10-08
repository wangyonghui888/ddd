package com.panda.sport.sdk.listeners;

import java.math.BigDecimal;
import java.util.Objects;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.panda.sport.sdk.mq.Producer;
import com.panda.sport.sdk.util.RcsLocalCacheUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum;
import com.panda.sport.sdk.annotation.AutoInitMethod;
import com.panda.sport.sdk.constant.LimitRedisKeys;
import com.panda.sport.sdk.core.JedisClusterServer;

import static com.panda.sport.sdk.constant.LimitRedisKeys.SPECIAL_USER_CONFIG;
import static com.panda.sport.sdk.constant.LimitRedisKeys.USER_LABEL_CONFIG;
import static com.panda.sport.sdk.constant.RedisKeys.TEMPLATE_TOURNAMENT_AMOUNT;

/**
 * @author :  lithan
 * @Description :  单关限额配置刷新处理类
 * @Date: 2020-01-13 12:23
 */
@Singleton
@AutoInitMethod(init = "init")
public class LimitHandler {
    private static final Logger log = LoggerFactory.getLogger(LimitHandler.class);

    @Inject
    JedisClusterServer jedisClusterServer;
    @Inject
    Producer producer;


    /**
     * 删除对应玩法 盘口位置 限额 的缓存
     **/
    public Boolean hand(String body) {
        log.info("【rcs_limit_cache_clear_sdk】限额清理接收到参数：{}", body);
        try {
            JSONObject json = JSONObject.parseObject(body);
            Object data = json.get("dataType");
            Integer type = json.getInteger("type");
            String tagId = json.getString("tagId");
            if (Objects.isNull(data) && StringUtils.isBlank(tagId)) {
                return true;
            }
            //如果是用户标签限额更新
            if(StringUtils.isNotBlank(tagId)){
                json.put("dataType",type);
            }
            int dataType = json.getInteger("dataType");
            //盘口位置
            if (dataType == LimitDataTypeEnum.PLACE_LIMIT.getType()) {
                clearMarketPlace(json);
            }
            //商户限额
            if (dataType == LimitDataTypeEnum.MERCHANT_LIMIT.getType()) {
                clearRcsQuotaBusinessLimit(json);
            }
            //商户单场限额
            if (dataType == LimitDataTypeEnum.MERCHANT_SINGLE_LIMIT.getType()) {
                clearRcsQuotaMerchantSingleFieldLimit(json);
            }
            //用户单日限额
            if (dataType == LimitDataTypeEnum.USER_DAILY_LIMIT.getType()) {
                clearRcsQuotaUserDailyQuota(json);
            }
            //用户单场限额
            if (dataType == LimitDataTypeEnum.USER_SINGLE_LIMIT.getType()) {
                clearRcsQuotaUserSingleSiteQuota(json);
            }
            //用户单注单关限额
            if (dataType == LimitDataTypeEnum.USER_SINGLE_BET_LIMIT.getType()) {
                clearRcsQuotaUserSingleNote(json);
            }
            // 用户特殊限额
            if (dataType == LimitDataTypeEnum.USER_SPECIAL_LIMIT.getType()) {
                clearUserSpecialLimit(json);
            }
            // 计入串关已用额度的比例
            if (dataType == LimitDataTypeEnum.SERIES_USED_RATIO.getType()) {
                clearSeriesUsedRatio(json);
            }
            //用户标签限额
            if(dataType == LimitDataTypeEnum.TAG_LIMIT.getType()){
                clearUsedLabelLimit(json);
            }
            //综合球类 - 联赛模板限额
            if (dataType == 11) {
                clearTemplateTournamentAmount(json);
            }
        } catch (Exception e) {
            log.error("【rcs_limit_cache_clear_sdk】限额清理异常,接收到参数={},", body, e);
        }
        return true;
    }

    /**
     * 综合球类联赛模板限额
     *
     * @param json 入参
     */
    private void clearTemplateTournamentAmount(JSONObject json) {
        String tournamentId = json.getString("tournamentId");
        String matchType = json.getString("matchType");
        String value = json.getString("value");
        if (tournamentId == null || matchType == null || value == null) {
            return;
        }
        try {
            String newKey = TEMPLATE_TOURNAMENT_AMOUNT + String.format("%s_%s", tournamentId, matchType);
            jedisClusterServer.setex(newKey, 24 * 60 * 60, value);
            log.info("::{}:综合球类联赛模板限额,key:{},amount:{}", tournamentId + "_" + matchType, newKey, value);
        } catch (Exception e) {
            log.error("::{}::综合球类联赛模板限额:", tournamentId + "_" + matchType, e);
        }

    }

    //盘口位置
    private void clearMarketPlace(JSONObject json) {
        Long matchId = json.getLong("matchId");
        Long playId = json.getLong("playId");
        String subPlayId = json.getString("subPlayId");
        Integer placeNum = json.getInteger("marketIndex");
        Long maxBetAmount = json.getLong("val");
        if (maxBetAmount == null || matchId == null) {
            return;
        }
        String mqKey = matchId + "_" + playId + "_" + placeNum;
        try {
            String key = String.format(LimitRedisKeys.MARKET_PLACE_KEY, matchId, playId, subPlayId, placeNum);
            jedisClusterServer.setex(key, 4 * 60 * 60, maxBetAmount.toString());
            log.info("::{}:缓存刷新盘口位置完成,key:{},amount:{}", mqKey, key, maxBetAmount);
            //广播刷新本地缓存
            JSONArray localCacheKeys = new JSONArray();
            JSONObject keys = new JSONObject();
            keys.put("type", "market_place_limit");
            keys.put("key", key);
            keys.put("value", maxBetAmount);
            localCacheKeys.add(keys);
            producer.sendMsg("rcs_local_cache_clear_sdk", "market_place_limit", mqKey, JSON.toJSONString(localCacheKeys));
        } catch (Exception e) {
            log.error("::{}::删除对应玩法盘口位置限额异常:", mqKey, e);
        }
    }

    //商户限额
    private void clearRcsQuotaBusinessLimit(JSONObject json) {
        String businessId = json.getString("businessId");
        if (businessId == null) {
            return;
        }
        try {
            jedisClusterServer.del(LimitRedisKeys.MERCHANT_LIMIT_KEY + businessId);
            log.info("::{}::删除商户限额完成{}", businessId, json);
        } catch (Exception e) {
            log.error("::{}::删除商户限额异常:", businessId, e);
        }
    }

    //商户单场限额
    private void clearRcsQuotaMerchantSingleFieldLimit(JSONObject json) {
        String matchId = json.getString("matchId");
        String matchType = json.getString("matchType");
        String val = json.getString("val");
        //商户通用模板变更
        if (matchId == null || val == null) {
            //risk 刷新 redis
            return;
        }
        matchType = "0".equals(matchType) ? "1" : "0";
        String mqKey = matchId + "_" + matchType;
        try {
            int expire = "0".equals(matchType) ? 120 * 60 * 60 : 4 * 60 * 60;
            String sportId = json.getString("sportId");
            BigDecimal amount = new BigDecimal(val).multiply(new BigDecimal("100"));
            String limitKey = LimitRedisKeys.getMatchMerchantSingleLimitKey(sportId, LimitDataTypeEnum.MERCHANT_SINGLE_LIMIT, matchId, matchType);
            jedisClusterServer.setex(limitKey, expire, amount.toPlainString());
            log.info("::{}:缓存刷新商户单场限额完成,key:{},amount:{}", mqKey, limitKey, amount);
            //广播刷新本地缓存
            JSONArray localCacheKeys = new JSONArray();
            JSONObject keys = new JSONObject();
            keys.put("type", "merchant_single_match_limit");
            keys.put("key", limitKey);
            keys.put("value", amount);
            localCacheKeys.add(keys);
            producer.sendMsg("rcs_local_cache_clear_sdk", "merchant_single_match_limit", mqKey, localCacheKeys.toJSONString());
        } catch (Exception e) {
            log.error("::{}::清理用户单注单关-商户单场限额配置异常:", mqKey, e);
        }
    }

    //用户单日限额
    private void clearRcsQuotaUserDailyQuota(JSONObject json) {
        String sportId = json.getString("sportId");
        try {
            JSONArray localCacheKeys = new JSONArray();
            //单关
            String dayCompensation = json.getString("val2");
            if (StringUtils.isNotBlank(dayCompensation)) {
                String limitKey = LimitRedisKeys.USER_DAY_LIMIT_KEY + sportId;
                String amount = new BigDecimal(dayCompensation).multiply(new BigDecimal("100")).toPlainString();
                jedisClusterServer.setex(limitKey, 30 * 24 * 60 * 60, amount);
                JSONObject keys = new JSONObject();
                keys.put("type", "user_single_day_limit");
                keys.put("key", limitKey);
                keys.put("value", amount);
                localCacheKeys.add(keys);
            }
            //串关
            String crossDayCompensation = json.getString("val3");
            if (StringUtils.isNotBlank(crossDayCompensation)) {
                String limitKey = LimitRedisKeys.USER_DAY_SERIES_LIMIT_KEY + sportId;
                String crossAmount = new BigDecimal(crossDayCompensation).multiply(new BigDecimal("100")).toPlainString();
                jedisClusterServer.setex(limitKey, 30 * 24 * 60 * 60, crossAmount);
                JSONObject keys = new JSONObject();
                keys.put("type", "user_single_day_limit");
                keys.put("key", limitKey);
                keys.put("value", crossAmount);
                localCacheKeys.add(keys);
            }
            producer.sendMsg("rcs_local_cache_clear_sdk", "user_single_day_limit", sportId, localCacheKeys.toJSONString());
            log.info("::{}::更新用户单日限额完成：{}", sportId, json);
        } catch (Exception e) {
            log.error("::{}::更新用户单日限额常：", sportId, e);
        }
    }

    //用户单场限额
    private void clearRcsQuotaUserSingleSiteQuota(JSONObject json) {
        Integer sportId = json.getInteger("sportId");
        if (sportId == null) {
            return;
        }
        String matchId = json.getString("matchId");
        String matchType = json.getString("matchType");
        String val = json.getString("val");
        //单注单关通用模板变更
        if (matchId == null || val == null) {
            //risk 刷新redis
            return;
        }
        String mqKey = matchId + "_" + sportId;
        try {
            BigDecimal amount = new BigDecimal(val).multiply(new BigDecimal("100"));
            matchType = "0".equals(matchType) ? "1" : "0";
            //赛事模板变更
            int expire = "0".equals(matchType) ? 120 * 60 * 60 : 4 * 60 * 60;
            String limitKey = LimitRedisKeys.getMatchSingleLimitKey(sportId.toString(), LimitDataTypeEnum.USER_SINGLE_LIMIT, matchId, matchType);
            String limitKeyKoala = LimitRedisKeys.getUserSingleLimitKey(sportId.toString(), matchId, matchType);
            jedisClusterServer.setex(limitKey, expire, amount.toPlainString());
            jedisClusterServer.setex(limitKeyKoala, expire, amount.toPlainString());
            log.info("::{}::限额刷新用户单场完成,单关key:{},串关key:{},param:{}", mqKey, limitKey, limitKeyKoala, json);
            //广播刷新本地缓存
            JSONArray localCacheKeys = new JSONArray();
            JSONObject keys = new JSONObject();
            keys.put("type", "user_single_match_limit");
            keys.put("key", limitKey);
            keys.put("value", amount);
            localCacheKeys.add(keys);
            producer.sendMsg("rcs_local_cache_clear_sdk", "user_single_match_limit", mqKey, localCacheKeys.toJSONString());
        } catch (Exception e) {
            log.error("::{}::清理用户单注单关- 赛事模板限额配置异常:", mqKey, e);
        }
    }

    //用户特殊限额
    private void clearUserSpecialLimit(JSONObject json) {
        String userId = json.getString("userId");
        log.info("::{}::更新用户特殊限额缓存:{}", userId, json);
        try {
            //删除限时
            jedisClusterServer.del(String.format(SPECIAL_USER_CONFIG, userId));
            String userLabelConfigKey = String.format(USER_LABEL_CONFIG, userId);
            jedisClusterServer.del(userLabelConfigKey);

            JSONArray localList = new JSONArray();
            JSONObject localMap = new JSONObject();
            //更新对应赛种延迟缓存
            JSONArray sportIdList = json.getJSONArray("sportIdList");
            if (sportIdList != null && sportIdList.size() > 0) {
                String rcsUserConfigVo = json.getString("rcsUserConfigVo");
                sportIdList.forEach(e -> {
                    jedisClusterServer.hset(userLabelConfigKey, e.toString(), rcsUserConfigVo);
                    jedisClusterServer.expire(userLabelConfigKey, 60 * 60 * 24 * 30);
                });
            }
            //用户赛种限额配置
            JSONArray userBetRateList = json.getJSONArray("userBetRateList");
            log.info("::{}::更新用户特殊限额缓存-用户赛种配置：{}", userId, userBetRateList);
            if (userBetRateList != null && userBetRateList.size() > 0) {
                JSONArray localCacheKeys = new JSONArray();
                String userSportTypeBetLimitKey = String.format("risk:trade:rcs_user_sport_type_bet_limit_config:%s", userId);
                jedisClusterServer.del(userSportTypeBetLimitKey);
                userBetRateList.forEach(e -> {
                    JSONObject jsonObject = JSONObject.parseObject(e.toString());
                    //设置用户赛种配置添加[{}]对象作为,接口标识为[]数组
                    if (jsonObject.getString("sportId") != null) {
                        jedisClusterServer.hset(userSportTypeBetLimitKey, jsonObject.getString("sportId"), jsonObject.getString("betRate"));
                        //刷新本地缓存
                        JSONObject betRateMap = new JSONObject();
                        betRateMap.put("key", userSportTypeBetLimitKey + jsonObject.getString("sportId"));
                        betRateMap.put("value", jsonObject.getString("betRate"));
                        localCacheKeys.add(betRateMap);
                    }
                });
                localMap.put("userBetRateList", localCacheKeys);
            }

            //用户特殊限额配置
            String userSpecialBetLimitKey = String.format("risk:trade:rcs_user_special_bet_limit_config:%s", userId);
            jedisClusterServer.hset(userSpecialBetLimitKey, "errorMark", "1");
            //特殊货量
            String specialQuantityPercentage = json.getString("specialQuantityPercentage");
            log.info("::{}::更新用户特殊限额缓存-特殊货量：{}", userId, specialQuantityPercentage);
            if (StringUtils.isNotBlank(specialQuantityPercentage)) {
                jedisClusterServer.hset(userSpecialBetLimitKey, "specialQuantityPercentage", specialQuantityPercentage);
                localMap.put("specialQuantityPercentage", specialQuantityPercentage);
            }
            //冠军玩法限额比例
            String championLimitRate = json.getString("championLimitRate");
            log.info("::{}::更新用户特殊限额缓存-冠军玩法限额比例：{}", userId, championLimitRate);
            if (StringUtils.isNotBlank(championLimitRate)) {
                jedisClusterServer.hset(userSpecialBetLimitKey, "championLimitRate", championLimitRate);
                localMap.put("championLimitRate", specialQuantityPercentage);
            }
            //用户特殊限额
            jedisClusterServer.hset(userSpecialBetLimitKey, "type", json.getString("type"));
            JSONArray jsonArray = json.getJSONArray("jsonArray");
            log.info("::{}::更新用户特殊限额缓存-用户特殊限额：{}", userId, jsonArray);
            if (jsonArray != null && jsonArray.size() > 0) {

                JSONObject jsonObject1 = JSONObject.parseObject(jsonArray.get(0).toString());
                String percentage1 = jsonObject1.getString("percentage");
                if (StringUtils.isNotBlank(percentage1)) {
                    jedisClusterServer.hset(userSpecialBetLimitKey, "percentage", percentage1);
                }

                jsonArray.forEach(e -> {
                    JSONObject jsonObject = JSONObject.parseObject(e.toString());
                    String percentage = jsonObject.getString("percentage");
                    /*if (StringUtils.isNotBlank(percentage)) {
                        jedisClusterServer.hset(userSpecialBetLimitKey, "percentage", percentage);
                    }*/
                    String singleNoteClaimLimit = jsonObject.getString("singleNoteClaimLimit");
                    String key1 = jsonObject.getString("key1");
                    if (StringUtils.isNotBlank(key1)) {
                        if (StringUtils.isNotBlank(singleNoteClaimLimit)) {
                            jedisClusterServer.hset(userSpecialBetLimitKey, key1, new BigDecimal(singleNoteClaimLimit).multiply(new BigDecimal("100")).toPlainString());
                        } else {
                            jedisClusterServer.hdel(userSpecialBetLimitKey, key1);
                        }
                    }
                    String singleGameClaimLimit = jsonObject.getString("singleGameClaimLimit");
                    String key2 = jsonObject.getString("key2");
                    if (StringUtils.isNotBlank(key2)) {
                        if (StringUtils.isNotBlank(singleGameClaimLimit)) {
                            jedisClusterServer.hset(userSpecialBetLimitKey, key2, new BigDecimal(singleGameClaimLimit).multiply(new BigDecimal("100")).toPlainString());
                        } else {
                            jedisClusterServer.hdel(userSpecialBetLimitKey, key2);
                        }
                    }

                });
                localMap.put("userSpecialBetLimitList", jsonArray);
            }
            localMap.put("type", "user_special_bet_limit_config");
            localMap.put("userId", userId);
            localList.add(localMap);
            producer.sendMsg("rcs_local_cache_clear_sdk", "user_special_bet_limit_config", userId, JSONObject.toJSONString(localList));
        } catch (Exception e) {
            log.error("::{}::更新用户特殊限额缓存异常：", userId, e);
        }
    }

    //用户单注单关
    private void clearRcsQuotaUserSingleNote(JSONObject json) {
        String matchId = json.getString("matchId");
        //单注单关通用模板变更
        if (matchId == null) {
            //risk 刷新
            return;
        }
        Integer sportId = json.getInteger("sportId");
        if (sportId == null) {
            return;
        }
        String matchType = json.getString("matchType");
        String playId = json.getString("playId");
        String val = json.getString("val");
        String val2 = json.getString("val2");
        String val3 = json.getString("val3");
        String val4 = json.getString("val4");
        try {
            //赛事模板变更
            int expire = "0".equals(matchType) ? 120 * 60 * 60 : 4 * 60 * 60;
            String limitKey = LimitRedisKeys.getMatchSingleBetPlayLimitKey(sportId, LimitDataTypeEnum.USER_SINGLE_BET_LIMIT, matchId, matchType, playId);
            String limitKeyKoala = LimitRedisKeys.getSinglePlayLimitKey(sportId.toString(), matchId, matchType, json.getString("playId"));
            JSONArray localCacheKeys = new JSONArray();
            if (StringUtils.isNotBlank(val)) {
                String singlePayKey = limitKey + ":singlePay";
                jedisClusterServer.setex(singlePayKey, expire, val);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("key", singlePayKey);
                jsonObject.put("value", val);
                jsonObject.put("type", "user_single_match_bet_limit");
                localCacheKeys.add(jsonObject);
            }
            if (StringUtils.isNotBlank(val2)) {
                String playTotalKey = limitKey + ":playTotal";
                jedisClusterServer.setex(playTotalKey, expire, val2);
                jedisClusterServer.setex(limitKeyKoala, expire, val2);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("key", playTotalKey);
                jsonObject.put("value", val2);
                jsonObject.put("type", "user_single_match_bet_limit");
                localCacheKeys.add(jsonObject);
            }
            if (StringUtils.isNotBlank(val3)) {
                String singleBetKey = limitKey + ":singleBet";
                jedisClusterServer.setex(singleBetKey, expire, val3);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("key", singleBetKey);
                jsonObject.put("value", val3);
                jsonObject.put("type", "user_single_match_bet_limit");
                localCacheKeys.add(jsonObject);
            }
            if (StringUtils.isNotBlank(val4)) {
                String guaranteeBetKey = limitKey + ":singleHedgeAmount";
                jedisClusterServer.setex(guaranteeBetKey, expire, val4);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("key", guaranteeBetKey);
                jsonObject.put("value", val4);
                jsonObject.put("type", "user_single_match_bet_limit");
                localCacheKeys.add(jsonObject);
            }
            log.info("::{}::限额刷新 - 用户单注完成，key:{}，param:{}", matchId + "_" + playId + "_" + sportId, limitKey, localCacheKeys.toJSONString());
            //将修改的信息同步一个mq广播 清除本地缓存
            producer.sendMsg("rcs_local_cache_clear_sdk", "user_single_match_bet_limit", "user_single_match_bet_limit", localCacheKeys.toJSONString());
        } catch (Exception e) {
            log.error("::{}::限额刷新 - 用户单注异常:{}", matchId + "_" + playId + "_" + sportId, e);
        }
    }

    private void clearUsedLabelLimit(JSONObject json){
        String value = json.getString("value");
        if(StringUtils.isBlank(value)){
            return;
        }
        String tagId = json.getString("tagId");
        String tagKey = LimitRedisKeys.getUserTagLimitKey(tagId);
        try{
            jedisClusterServer.setex(tagKey + LimitRedisKeys.USER_PERCENTAGE_LIMIT_FIELD, 30 * 24 * 60 * 60, value);
            RcsLocalCacheUtils.timedCache.put(tagKey + LimitRedisKeys.USER_PERCENTAGE_LIMIT_FIELD, value);
            log.info("::{}:: 用户标签限额更新完成:{}",tagKey, json);
        }catch (Exception e){
            log.error("::{}::用户标签限额更新异常:{}", tagKey ,json, e);
        }
    }

    /**
     * 计入串关已用额度的比例
     *
     * @param json
     */
    private void clearSeriesUsedRatio(JSONObject json) {
        try {
            jedisClusterServer.del(LimitRedisKeys.SERIES_USED_RATIO_KEY);
            log.info("删除 计入串关已用额度的比例 完成{}", json);
        } catch (Exception e) {
            log.error("删除 计入串关已用额度的比例 完成", e);
        }
    }

}
