package com.panda.sport.sdk.mq;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.sdk.constant.LimitRedisKeys;
import com.panda.sport.sdk.util.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.stereotype.Component;

/**
 * @Description 监控限额对应配置变更，广播刷新本地缓存
 * @Author beulah
 **/
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "rcs_local_cache_clear_sdk",
        consumerGroup = "rcs_local_cache_clear_sdk_group",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class LimitLocalCacheClearConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {


    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }


    public LimitLocalCacheClearConsumer() {
    }

    @Override
    public void onMessage(String body) {
        try {
            if (body != null && (body.contains("user_single_limit") || body.contains("merchant_single_limit") || body.contains("user_single_bet_limit"))) {
                body = body.substring(1, body.length() - 1).replace("\\", "");
            }
            clearLocalCache(JSONObject.parseArray(body));
        } catch (Exception e) {
            log.error("【rcs_local_cache_clear_sdk】刷新本地缓存异常,接收到参数={},", body, e);
        }
    }


    /**
     * 本地缓存同步清理
     *
     * @param jsonArray [
     *                  {
     *                  "type": "user_day_limit",
     *                  "sportId": "123",
     *                  "value": 333
     *                  },
     *                  {
     *                  "type": "user_tag",
     *                  "userId": "123",
     *                  "value": null
     *                  },
     *                  {
     *                  "type": "merchant_limit",
     *                  "merchantId": "123",
     *                  "value": null
     *                  }
     *                  ]
     */
    public void clearLocalCache(JSONArray jsonArray) {
        jsonArray.forEach(e -> {
            JSONObject jsonObject = (JSONObject) e;
            String type = jsonObject.getString("type");
            log.info("::{}::同步更新本地缓存开始", type);
            if (StringUtils.isNotBlank(type)) {
                String value = jsonObject.getString("value");
                String key = jsonObject.getString("key");
                if (StringUtils.isNotBlank(key)) {
                    putOrRemoveLocalCache(key, value);
                } else {
                    if (type.equals("user_tag")) {
                        putOrRemoveLocalCache(LimitRedisKeys.getTagtKey() + jsonObject.getString("userId"), value);
                    } else if (type.equals("user_special_bet_limit_config")) {
                        //用户赛种限额配置
                        JSONArray userBetRateList = jsonObject.getJSONArray("userBetRateList");
                        if (userBetRateList != null && userBetRateList.size() > 0) {
                            userBetRateList.forEach(u -> {
                                JSONObject userBetRate = (JSONObject) u;
                                String k = userBetRate.getString("key");
                                String v = userBetRate.getString("value");
                                putOrRemoveLocalCache(k, v);
                            });
                        }
                        String mainKey = LimitRedisKeys.getUserSpecialLimitKey(jsonObject.getString("userId"));
                        //特殊货量
                        String specialQuantityPercentage = jsonObject.getString("specialQuantityPercentage");
                        if (StringUtils.isNotBlank(specialQuantityPercentage)) {
                            putOrRemoveLocalCache(mainKey + "specialQuantityPercentage", specialQuantityPercentage);
                        }
                        //冠军玩法限额比例
                        String championLimitRate = jsonObject.getString("championLimitRate");
                        if (StringUtils.isNotBlank(championLimitRate)) {
                            putOrRemoveLocalCache(mainKey + "championLimitRate", championLimitRate);
                        }
                        //用户特殊限额
                        JSONArray userSpecialBetLimitList = jsonObject.getJSONArray("userSpecialBetLimitList");
                        putOrRemoveLocalCache(mainKey + LimitRedisKeys.USER_SPECIAL_LIMIT_TYPE_FIELD, null);
                        if (userSpecialBetLimitList != null && userSpecialBetLimitList.size() > 0) {
                            putOrRemoveLocalCache(mainKey + LimitRedisKeys.USER_PERCENTAGE_LIMIT_FIELD, null);
                            putOrRemoveLocalCache(mainKey + "1_-1_single_game_claim_limit", null);
                            putOrRemoveLocalCache(mainKey + "1_0_single_game_claim_limit", null);
                            putOrRemoveLocalCache(mainKey + "1_1_single_game_claim_limit", null);
                            putOrRemoveLocalCache(mainKey + "1_2_single_game_claim_limit", null);
                            putOrRemoveLocalCache(mainKey + "2_-1_single_game_claim_limit", null);
                            putOrRemoveLocalCache(mainKey + "2_0_single_game_claim_limit", null);
                            putOrRemoveLocalCache(mainKey + "2_1_single_game_claim_limit", null);
                            putOrRemoveLocalCache(mainKey + "2_2_single_game_claim_limit", null);


                            putOrRemoveLocalCache(mainKey + "1_-1_single_note_claim_limit", null);
                            putOrRemoveLocalCache(mainKey + "1_0_single_note_claim_limit", null);
                            putOrRemoveLocalCache(mainKey + "1_1_single_note_claim_limit", null);
                            putOrRemoveLocalCache(mainKey + "1_2_single_note_claim_limit", null);
                            putOrRemoveLocalCache(mainKey + "2_-1_single_note_claim_limit", null);
                            putOrRemoveLocalCache(mainKey + "2_0_single_note_claim_limit", null);
                            putOrRemoveLocalCache(mainKey + "2_1_single_note_claim_limit", null);
                            putOrRemoveLocalCache(mainKey + "2_2_single_note_claim_limit", null);

                        }
                    }
                }
            }
        });
    }

    private void putOrRemoveLocalCache(String key, String value) {
        if (StringUtils.isBlank(value) || "null".equals(value)) {
            RcsLocalCacheUtils.timedCache.remove(key);
        } else {
            RcsLocalCacheUtils.timedCache.put(key, value);
        }
    }

    public static void main(String[] args) {
        //String body = "[{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:-1.dataType.3:matchType.0:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:-1.dataType.3:matchType.1:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:1.dataType.3:matchType.0:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:1.dataType.3:matchType.1:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:2.dataType.3:matchType.0:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:2.dataType.3:matchType.1:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:3.dataType.3:matchType.0:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:3.dataType.3:matchType.1:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:4.dataType.3:matchType.0:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:4.dataType.3:matchType.1:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:5.dataType.3:matchType.0:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:5.dataType.3:matchType.1:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:6.dataType.3:matchType.0:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:6.dataType.3:matchType.1:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:7.dataType.3:matchType.0:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:7.dataType.3:matchType.1:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:8.dataType.3:matchType.0:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:8.dataType.3:matchType.1:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:9.dataType.3:matchType.0:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:9.dataType.3:matchType.1:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:10.dataType.3:matchType.0:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:10.dataType.3:matchType.1:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:11.dataType.3:matchType.0:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:11.dataType.3:matchType.1:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:12.dataType.3:matchType.0:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:12.dataType.3:matchType.1:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:13.dataType.3:matchType.0:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:13.dataType.3:matchType.1:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:14.dataType.3:matchType.0:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:14.dataType.3:matchType.1:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:15.dataType.3:matchType.0:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:15.dataType.3:matchType.1:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:16.dataType.3:matchType.0:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:16.dataType.3:matchType.1:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:17.dataType.3:matchType.0:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:17.dataType.3:matchType.1:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:18.dataType.3:matchType.0:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:18.dataType.3:matchType.1:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:19.dataType.3:matchType.0:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:19.dataType.3:matchType.1:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:20.dataType.3:matchType.0:UserSingle\"},{\"type\":\"user_single_limit\",\"key\":\"rcs:limit:sportId.6:tournamentLevel:20.dataType.3:matchType.1:UserSingle\"}]";

        String body = "[{\"type\":\"user_single_bet_limit_match\",\"value\":\"300000\",\"key\":\"rcs:limit:sportId.1:dataType.4:matchId.3458347:matchType.0:playId.379:UserPlaySinglePayment:singlePay\"},{\"type\":\"user_single_bet_limit_match\",\"value\":\"900000\",\"key\":\"rcs:limit:sportId.1:dataType.4:matchId.3458347:matchType.0:playId.379:UserPlaySinglePayment:playTotal\"}]";
        JSONArray jsonArray = JSONObject.parseArray(body);
        System.out.println(jsonArray);
    }
}
