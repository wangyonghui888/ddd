package com.panda.rcs.stray.limit.wrapper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.stray.limit.entity.constant.BaseConstant;
import com.panda.rcs.stray.limit.entity.constant.NumberConstant;
import com.panda.rcs.stray.limit.entity.constant.RedisKeyConstant;
import com.panda.rcs.stray.limit.utils.RedisUtils;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.limit.RedisUpdateVo;
import com.panda.sport.rcs.enums.RedisCmdEnum;
import com.panda.sport.rcs.mapper.RcsBusinessConfigMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.StandardSportMarketCategory;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 公共服务
 * @Author : Paca
 * @Date : 2022-04-02 19:08
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Service
public class CommonService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    RcsBusinessConfigMapper rcsBusinessConfigMapper;

    /**
     * 获取用户特殊限额类型，0-无，1-标签限额（已作废），2-特殊百分比限额，3-特殊单注单场限额，4-特殊VIP限额
     *
     * @param userId
     * @return
     */
    public String getUserSpecialLimitType(String userId) {
        String key = RedisKeyConstant.getUserSpecialLimitKey(userId);
        String hashValue = RcsLocalCacheUtils.getValue(key,RedisKeyConstant.USER_SPECIAL_LIMIT_TYPE_FIELD,redisUtils::hget);
        log.info("额度查询-用户特殊限额类型：key={},hashKey={},hashValue={}", key, "type", hashValue);
        return hashValue;
    }

    /**
     * 是否特殊VIP限额
     *
     * @param userId
     * @return
     */
    public boolean isSpecialVipLimit(String userId) {
        return String.valueOf(NumberConstant.NUM_FOUR).equals(getUserSpecialLimitType(userId));
    }

    public void redisCallback(List<RedisUpdateVo> redisUpdateList) {
        if (CollectionUtils.isEmpty(redisUpdateList)) {
            return;
        }
        redisUpdateList.forEach(vo -> {
            BigDecimal value = CommonUtils.toBigDecimal(vo.getValue(), BigDecimal.ZERO).negate();
            exeIncrByCmd(vo.getCmd(), vo.getKey(), vo.getField(), value);
        });
    }

    public void exeIncrByCmd(String cmd, String key, String field, BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) == NumberConstant.NUM_ZERO) {
            return;
        }
        if (RedisCmdEnum.isIncrBy(cmd)) {
            redisUtils.incrBy(key, value.longValue());
        } else if (RedisCmdEnum.isIncrByFloat(cmd)) {
            redisUtils.incrByFloat(key, value.doubleValue());
        } else if (RedisCmdEnum.isHincrBy(cmd)) {
            redisUtils.hincrBy(key, field, value.longValue());
        } else if (RedisCmdEnum.isHincrByFloat(cmd)) {
            redisUtils.hincrByFloat(key, field, value.doubleValue());
        } else {
            return;
        }
        redisUtils.expire(key, RedisKeyConstant.LIMIT_SPORT_EXPIRY_KEY, TimeUnit.DAYS);
    }


    public void initJsonObject(String key, Object value){
        JSONObject json = new JSONObject();
        json.put("key", key);
        json.put("value", value);
        producerSendMessageUtils.sendMessage("rcs_stray_limit_cache_update", "", key, json);
}

    /**
     * 订单扩展信息
     *
     * @param bean 订单信息
     * @param item 投注项信息
     * @return 订单扩展信息
     */
    public ExtendBean buildExtendBean(OrderBean bean, OrderItem item) {
        ExtendBean extend = new ExtendBean();
        extend.setSeriesType(bean.getSeriesType());
        extend.setItemId(item.getBetNo());
        extend.setOrderId(item.getOrderNo());
        extend.setBusId(String.valueOf(bean.getTenantId()));
        extend.setHandicap(item.getMarketValue());
        extend.setCurrentScore(item.getScoreBenchmark());
        //item  1 ：早盘 ，2： 滚球盘， 3： 冠军盘
        extend.setIsScroll(String.valueOf(item.getMatchType()).equals(String.valueOf(NumberConstant.NUM_TWO)) ? String.valueOf(NumberConstant.NUM_ONE) : String.valueOf(NumberConstant.NUM_ZERO));
        //冠军盘标识
        extend.setIsChampion(item.getMatchType() == NumberConstant.NUM_THREE ? NumberConstant.NUM_ONE : NumberConstant.NUM_ZERO);
        extend.setMatchId(String.valueOf(item.getMatchId()));
        extend.setPlayId(item.getPlayId() + "");
        extend.setSelectId(String.valueOf(item.getPlayOptionsId()));
        extend.setSportId(String.valueOf(item.getSportId()));
        extend.setUserId(String.valueOf(item.getUid()));
        extend.setOdds(String.valueOf(item.getHandleAfterOddsValue()));
        extend.setMarketId(item.getMarketId().toString());

        //阶段
        if (item.getMatchType() == NumberConstant.NUM_THREE) {
            extend.setPlayType(String.valueOf(NumberConstant.NUM_ZERO));
        } else {
            extend.setPlayType(this.getPlayType(item));
        }

        if (item.getBetAmount() != null) {
            extend.setOrderMoney(item.getBetAmount());
            extend.setCurrentMaxPaid(new BigDecimal(item.getBetAmount()).multiply((new BigDecimal(extend.getOdds()).setScale(NumberConstant.NUM_TWO, RoundingMode.HALF_DOWN).subtract(new BigDecimal(NumberConstant.NUM_ONE)))).longValue());
        } else {
            extend.setOrderMoney(NumberConstant.LONG_ZERO);
            extend.setCurrentMaxPaid(NumberConstant.LONG_ZERO);
        }
        extend.setTournamentLevel(item.getTurnamentLevel());
        extend.setTournamentId(item.getTournamentId());
        extend.setDateExpect(item.getDateExpect());
        extend.setDataSourceCode(item.getDataSourceCode());

        extend.setItemBean(item);

        if (StringUtils.isBlank(extend.getHandicap())) {
            extend.setHandicap(String.valueOf(NumberConstant.NUM_ZERO));
        }
        if (StringUtils.isBlank(extend.getCurrentScore())) {
            extend.setCurrentScore(BaseConstant.ZERO_ZERO);
        }
        extend.setSubPlayId(item.getSubPlayId());
        return extend;
    }

    private String getPlayType(OrderItem item) {
        String key = String.format(RedisKeyConstant.RCS_STANDARD_SPORT_CATEGORY_ALL, item.getPlayId(), item.getSportId());
        String value=RcsLocalCacheUtils.getValue(key,redisUtils::get);
        if(StringUtils.isBlank(value)){
            StandardSportMarketCategory standardSportMarketCategory = rcsBusinessConfigMapper.queryPlayById(item.getPlayId(), item.getSportId());
            if (Objects.nonNull(standardSportMarketCategory)) {
                redisUtils.setex(key, String.valueOf(standardSportMarketCategory.getTheirTime()), NumberConstant.NUM_SEVEN, TimeUnit.DAYS);
                //发送更新本地缓存
                this.initJsonObject(key, String.valueOf(standardSportMarketCategory.getTheirTime()));
                return String.valueOf(standardSportMarketCategory.getTheirTime());
            }
        }
        return value;
    }
}
