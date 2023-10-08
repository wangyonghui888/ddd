package com.panda.sport.rcs.credit.service.champion;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.rcs.cache.redis.RedisUtils;
import com.panda.sport.rcs.common.FileReadUtils;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.credit.constants.ChampionRedisKey;
import com.panda.sport.rcs.credit.constants.ErrorCode;
import com.panda.sport.rcs.credit.service.AbstractLimitService;
import com.panda.sport.rcs.credit.service.CreditLimitService;
import com.panda.sport.rcs.credit.service.impl.LimitConfigService;
import com.panda.sport.rcs.exeception.LogicException;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.pojo.RcsChampionTradeConfig;
import com.panda.sport.rcs.pojo.limit.RedisUpdateVo;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import com.panda.sport.rcs.vo.RcsBusinessPlayPaidConfigVo;
import com.panda.sport.rcs.wrapper.champion.RcsChampionTradeConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 冠军玩法限额服务
 * @Author : Paca
 * @Date : 2021-06-09 14:54
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
public abstract class AbstractChampionLimitService extends AbstractLimitService {

    @Autowired
    private RcsChampionTradeConfigService rcsChampionTradeConfigService;

    @Autowired
    private LimitConfigService limitConfigService;

    private RedisUtils redisUtils;

    private String championBetShakey;

    public AbstractChampionLimitService(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
        String championBetText = FileReadUtils.readFileContent("lua/championBet.lua");
        championBetShakey = redisUtils.scriptLoad(championBetText);
        if (championBetShakey == null) {
            throw new RcsServiceException("冠军玩法下注脚本加载失败");
        }
    }

    @Override
    public List<RcsBusinessPlayPaidConfigVo> queryBetLimit(OrderBean orderBean) {
        commonCheckOrderBean(orderBean);
        for (OrderItem orderItem : orderBean.getItems()) {
            commonCheckOrderItem(orderItem);
        }
        // 最高可投小于最低可投时，最高可投统一设置为0
        List<RcsBusinessPlayPaidConfigVo> list = queryBetLimit(orderBean, orderBean.getItems().get(0));
        for (RcsBusinessPlayPaidConfigVo vo : list) {
            if (vo.getOrderMaxPay() < vo.getMinBet()) {
                vo.setOrderMaxPay(0L);
            }
        }
        return list;
    }

    protected abstract List<RcsBusinessPlayPaidConfigVo> queryBetLimit(OrderBean orderBean, OrderItem orderItem);

    @Override
    public Map<String, Object> checkOrder(OrderBean orderBean) {
        commonCheckOrderBean(orderBean);
        if (StringUtils.isEmpty(orderBean.getOrderNo())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "订单号orderNo不能为空！");
        }
        for (OrderItem orderItem : orderBean.getItems()) {
            commonCheckOrderItem(orderItem);
        }
        List<RedisUpdateVo> redisUpdateList = Lists.newArrayList();
        Map<String, Object> resultMap = checkOrder(orderBean, redisUpdateList);
        if ("0".equals(String.valueOf(resultMap.get("infoCode")))) {
            String key = String.format(ChampionRedisKey.LIMIT_REDIS_UPDATE_RECORD_KEY, orderBean.getOrderNo());
            redisUtils.set(key, JSON.toJSONString(redisUpdateList));
            redisUtils.expire(key, 90L, TimeUnit.DAYS);
        }
        return resultMap;
    }

    protected abstract Map<String, Object> checkOrder(OrderBean orderBean, List<RedisUpdateVo> redisUpdateList);

    protected JSONArray executeLua(List<String> keys, List<String> args) {
        try {
            log.info("冠军玩法额度查询，lua脚本入参：shakey={},keys={},args={}", championBetShakey, keys, args);
            Object result = redisUtils.evalsha(championBetShakey, keys, args);
            log.info("冠军玩法额度查询，lua脚本返回结果：result=" + JSON.toJSONString(result));
            return JSON.parseArray(JSON.toJSONString(result));
        } catch (Exception e) {
            log.warn("冠军玩法额度查询，lua脚本执行异常", e);
        }
        return null;
    }

    /**
     * 获取商户玩法限额，单位分
     *
     * @param matchId
     * @param marketId
     * @return
     */
    protected long getMerchantPlayLimit(Long matchId, Long marketId) {
        String key = ChampionRedisKey.Limit.getChampionKey(matchId, marketId);
        String field = ChampionRedisKey.Limit.MERCHANT_PLAY_FIELD;
        String value = redisUtils.hget(key, field);
        log.info("冠军玩法额度，商户玩法限额：key={},field={},value={}", key, field, value);
        BigDecimal limit;
        if (StringUtils.isNotBlank(value)) {
            limit = CommonUtils.toBigDecimal(value);
        } else {
            limit = getChampionLimit(matchId, marketId, field);
        }
        return limit.multiply(RcsConstant.HUNDRED).longValue();
    }

    /**
     * 获取用户玩法限额，单位分
     *
     * @param matchId
     * @param marketId
     * @return
     */
    protected long getUserPlayLimit(Long matchId, Long marketId) {
        String key = ChampionRedisKey.Limit.getChampionKey(matchId, marketId);
        String field = ChampionRedisKey.Limit.USER_PLAY_FIELD;
        String value = redisUtils.hget(key, field);
        log.info("冠军玩法额度，用户玩法限额：key={},field={},value={}", key, field, value);
        BigDecimal limit;
        if (StringUtils.isNotBlank(value)) {
            limit = CommonUtils.toBigDecimal(value);
        } else {
            limit = getChampionLimit(matchId, marketId, field);
        }
        return limit.multiply(RcsConstant.HUNDRED).longValue();
    }

    /**
     * 获取用户单注限额，单位分
     *
     * @param matchId
     * @param marketId
     * @return
     */
    protected long getUserSingleBetLimit(Long matchId, Long marketId) {
        String key = ChampionRedisKey.Limit.getChampionKey(matchId, marketId);
        String field = ChampionRedisKey.Limit.USER_SINGLE_BET_FIELD;
        String value = redisUtils.hget(key, field);
        log.info("冠军玩法额度，用户单注限额：key={},field={},value={}", key, field, value);
        BigDecimal limit;
        if (StringUtils.isNotBlank(value)) {
            limit = CommonUtils.toBigDecimal(value);
        } else {
            limit = getChampionLimit(matchId, marketId, field);
        }
        return limit.multiply(RcsConstant.HUNDRED).longValue();
    }

    /**
     * 用户单项限额，单位分
     *
     * @param matchId
     * @param marketId
     * @param optionId
     * @return
     */
    protected long getUserOptionLimit(Long matchId, Long marketId, Long optionId) {
        String key = ChampionRedisKey.Limit.getChampionKey(matchId, marketId);
        String field = String.valueOf(optionId);
        String value = redisUtils.hget(key, field);
        log.info("冠军玩法额度，用户单项限额：key={},field={},value={}", key, field, value);
        BigDecimal limit;
        if (StringUtils.isNotBlank(value)) {
            limit = CommonUtils.toBigDecimal(value);
        } else {
            limit = getChampionLimit(matchId, marketId, field);
        }
        return limit.multiply(RcsConstant.HUNDRED).longValue();
    }

    private BigDecimal getChampionLimit(Long matchId, Long marketId, String field) {
        String key = ChampionRedisKey.Limit.getChampionKey(matchId, marketId);
        List<RcsChampionTradeConfig> list = rcsChampionTradeConfigService.getChampionLimit(marketId);
        log.info("冠军玩法额度，所有限额查询：marketId={},result={}", marketId, JSON.toJSONString(list));
        if (CollectionUtils.isEmpty(list)) {
            BigDecimal defaultValue = getChampionLimitDefault(field);
            redisUtils.hset(key, field, defaultValue.toPlainString());
            log.info("冠军玩法额度，数据库配置为空取默认值：key={},field={},defaultValue={}", key, field, defaultValue.toPlainString());
            return defaultValue;
        }
        Map<String, String> hashMap = Maps.newHashMapWithExpectedSize(list.size());
        list.forEach(config -> {
            Integer type = config.getType();
            if (type == null) {
                return;
            }
            String value = config.getAmount().toPlainString();
            if (type == 1) {
                hashMap.put(ChampionRedisKey.Limit.MERCHANT_PLAY_FIELD, value);
            } else if (type == 2) {
                hashMap.put(ChampionRedisKey.Limit.USER_PLAY_FIELD, value);
            } else if (type == 3) {
                hashMap.put(ChampionRedisKey.Limit.USER_SINGLE_BET_FIELD, value);
            } else if (type == 4) {
                String optionId = config.getOddsFieldsId();
                if (optionId == null) {
                    return;
                }
                hashMap.put(optionId, value);
            }
        });
        redisUtils.hmset(key, hashMap);
        log.info("冠军玩法额度，缓存限额：key={},hashMap={}", key, hashMap);
        String value = hashMap.get(field);
        if (StringUtils.isNotBlank(value)) {
            return CommonUtils.toBigDecimal(value);
        } else {
            BigDecimal defaultValue = getChampionLimitDefault(field);
            redisUtils.hset(key, field, defaultValue.toPlainString());
            log.info("冠军玩法额度，未查询到配置取默认值：key={},field={},defaultValue={}", key, field, defaultValue.toPlainString());
            return defaultValue;
        }
    }

    private BigDecimal getChampionLimitDefault(String field) {
        if (ChampionRedisKey.Limit.MERCHANT_PLAY_FIELD.equals(field)) {
            // 商户玩法赔付限额：默认值1000万
            return new BigDecimal(1000_0000);
        } else if (ChampionRedisKey.Limit.USER_PLAY_FIELD.equals(field)) {
            // 用户玩法累计赔付限额：默认值100万
            return new BigDecimal(100_0000);
        } else if (ChampionRedisKey.Limit.USER_SINGLE_BET_FIELD.equals(field)) {
            // 用户单注投注赔付限额：默认值50万
            return new BigDecimal(50_0000);
        } else {
            // 用户单项赔付限额：未配置则不校验，返回0
            return BigDecimal.ZERO;
        }
    }

    protected RcsQuotaBusinessLimitResVo getLimitRatio(OrderBean orderBean) {
        Long tenantId = orderBean.getTenantId();
        String creditId = orderBean.getAgentId();
        Long userId = orderBean.getUid();
        Integer limitType = orderBean.getLimitType();
        RcsQuotaBusinessLimitResVo limitRatio;
        if (NumberUtils.INTEGER_TWO.equals(limitType)) {
            limitRatio = limitConfigService.getBusinessLimit(creditId);
        } else {
            limitRatio = limitConfigService.getBusinessLimit(tenantId.toString());
        }
        if (limitRatio.getChampionBusinessProportion() == null) {
            limitRatio.setChampionBusinessProportion(BigDecimal.ONE);
        }
        if (limitRatio.getChampionUserProportion() == null) {
            limitRatio.setChampionUserProportion(BigDecimal.ONE);
        }
        String userConfigKey = "risk:trade:rcs_user_special_bet_limit_config:" + userId;
        String championLimitRateField = "championLimitRate";
        String value = RcsLocalCacheUtils.getValue(userConfigKey,championLimitRateField,redisUtils::hget);
        log.info("冠军玩法额度查询，冠军玩法限额比例：key={},field={},value={}", userConfigKey, championLimitRateField, value);
        if (StringUtils.isNotBlank(value)) {
            limitRatio.setChampionUserProportion(CommonUtils.toBigDecimal(value));
        }
        return limitRatio;
    }

    protected Map<Long, Long> getMerchantBetUsed(Long tenantId, String creditId, Long matchId, Long marketId) {
        String key = ChampionRedisKey.Used.getMerchantBetKey(tenantId, creditId, matchId, marketId);
        Map<String, String> map = redisUtils.hgetAll(key);
        log.info("冠军玩法额度，商户维度每个投注项投注额累计：key={},map={}", key, JSON.toJSONString(map));
        if (CollectionUtils.isEmpty(map)) {
            return Maps.newHashMap();
        }
        Map<Long, Long> result = Maps.newHashMap();
        map.forEach((k, v) -> {
            long optionId = NumberUtils.toLong(k);
            long betTotal = NumberUtils.toLong(v);
            result.put(optionId, betTotal);
        });
        return result;
    }

    protected Map<Long, Long> getMerchantPaymentUsed(Long tenantId, String creditId, Long matchId, Long marketId) {
        String key = ChampionRedisKey.Used.getMerchantPaymentKey(tenantId, creditId, matchId, marketId);
        Map<String, String> map = redisUtils.hgetAll(key);
        log.info("冠军玩法额度，商户维度每个投注项期望赔付累计：key={},map={}", key, JSON.toJSONString(map));
        if (CollectionUtils.isEmpty(map)) {
            return Maps.newHashMap();
        }
        Map<Long, Long> result = Maps.newHashMap();
        map.forEach((k, v) -> {
            long optionId = NumberUtils.toLong(k);
            long betTotal = NumberUtils.toLong(v);
            result.put(optionId, betTotal);
        });
        return result;
    }

    protected Map<Long, Long> getUserBetUsed(Long userId, Long matchId, Long marketId) {
        String key = ChampionRedisKey.Used.getUserBetKey(userId, matchId, marketId);
        Map<String, String> map = redisUtils.hgetAll(key);
        log.info("冠军玩法额度，用户维度每个投注项投注额累计：key={},map={}", key, JSON.toJSONString(map));
        if (CollectionUtils.isEmpty(map)) {
            return Maps.newHashMap();
        }
        Map<Long, Long> result = Maps.newHashMap();
        map.forEach((k, v) -> {
            long optionId = NumberUtils.toLong(k);
            long betTotal = NumberUtils.toLong(v);
            result.put(optionId, betTotal);
        });
        return result;
    }

    protected Map<Long, Long> getUserPaymentUsed(Long userId, Long matchId, Long marketId) {
        String key = ChampionRedisKey.Used.getUserPaymentKey(userId, matchId, marketId);
        Map<String, String> map = redisUtils.hgetAll(key);
        log.info("冠军玩法额度，用户维度每个投注项期望赔付累计：key={},map={}", key, JSON.toJSONString(map));
        if (CollectionUtils.isEmpty(map)) {
            return Maps.newHashMap();
        }
        Map<Long, Long> result = Maps.newHashMap();
        map.forEach((k, v) -> {
            long optionId = NumberUtils.toLong(k);
            long betTotal = NumberUtils.toLong(v);
            result.put(optionId, betTotal);
        });
        return result;
    }

    @Override
    public int orderType() {
        return 1;
    }

    private void commonCheckOrderBean(OrderBean orderBean) {
        if (CreditLimitService.checkNo(orderBean.getTenantId())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "商户ID不能为空！");
        }
        if (CreditLimitService.checkNo(orderBean.getUid())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "用户ID不能为空！");
        }
        if (CollectionUtils.isEmpty(orderBean.getItems())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "投注项items不能为空！");
        }
        if (NumberUtils.INTEGER_TWO.equals(orderBean.getLimitType())) {
            if (StringUtils.isBlank(orderBean.getAgentId())) {
                throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "信用代理ID不能为空！");
            }
        }
    }

    private void commonCheckOrderItem(OrderItem orderItem) {
//        if (checkNo(orderItem.getSportId())) {
//            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "items赛种sportId不能为空！");
//        }
        if (CreditLimitService.checkNo(orderItem.getMatchId())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "items赛事matchId不能为空！");
        }
        if (CreditLimitService.checkNo(orderItem.getPlayId())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "items玩法playId不能为空！");
        }
        if (CreditLimitService.checkNo(orderItem.getMarketId())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "items盘口marketId不能为空！");
        }
        Integer matchType = orderItem.getMatchType();
        if (matchType == null || matchType != 3) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "items投注类型matchType有误！");
        }
        if (CreditLimitService.checkNo(orderItem.getOddsValue())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "items赔率oddsValue不能为空！");
        }
//        if (orderItem.getTurnamentLevel() == null) {
//            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "items联赛等级turnamentLevel不能为空！");
//        }
//        if (StringUtils.isBlank(orderItem.getDateExpect())) {
//            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "items赛事账务日dateExpect不能为空！");
//        }
        if (CreditLimitService.checkNo(orderItem.getPlayOptionsId())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "items投注项playOptionsId不能为空！");
        }
    }
}
