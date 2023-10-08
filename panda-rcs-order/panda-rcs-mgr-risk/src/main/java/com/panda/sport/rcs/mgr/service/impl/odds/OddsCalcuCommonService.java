package com.panda.sport.rcs.mgr.service.impl.odds;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.panda.sport.data.rcs.dto.ThreewayOverLoadTriggerItem;
import com.panda.sport.data.rcs.dto.TwowayDoubleOverLoadTriggerItem;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.DataSourceTypeEnum;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @Description   //新加的一下方法
 * @Param
 * @Author  sean
 * @Date   2021/11/30
 * @return
 **/
@Service
@Slf4j
public class OddsCalcuCommonService {
    @Autowired
    RedisClient redisClient;

    private static List<Integer> HALF_COURT = Lists.newArrayList(17,19);
    private static List<Integer> FULL_COURT = Lists.newArrayList(1,4);
    private static List<Integer> THREE_ODDS_TYPE = Lists.newArrayList(1,17);

    /**
     * @Description   1274需求联动的玩法
     * @Param [playId]
     * @Author  sean
     * @Date   2021/11/30
     * @return java.util.List<java.lang.Long>
     **/
    public static List<Integer>  getLinkPlays(Integer playId){
        if (HALF_COURT.contains(playId)){
            return HALF_COURT;
        }else if (FULL_COURT.contains(playId)){
            return FULL_COURT;
        }else {
            return Lists.newArrayList(playId);
        }
    }
    /**
     * @Description   //让分大小玩法统计次数
     * @Param [twoWayDouble, config]
     * @Author  sean
     * @Date   2021/11/30
     * @return boolean
     **/
    public boolean isChangeTimesOver(ThreewayOverLoadTriggerItem twoWayDouble, RcsMatchMarketConfig config) {

        //获取次数
        String times = redisClient.hGet(String.format(RcsConstant.RCS_COUNT_TIMES,config.getMatchId()),config.getMarketId().toString());
        log.info("::{}::,缓存次数：{}",config.getMarketId(),times);
        // 独赢玩法
        if (THREE_ODDS_TYPE.contains(config.getPlayId().intValue())){
            Integer newTimes = NumberUtils.INTEGER_ONE;
            JSONObject count = JSONObject.parseObject(times);
            if (StringUtils.isNotBlank(times)){
                Object time = count.get(config.getOddsType());
                if (ObjectUtils.isEmpty(time)){
                    count = new JSONObject().fluentPut(config.getOddsType(),newTimes);
                }else {
                    newTimes = Integer.parseInt(count.get(config.getOddsType()).toString());
                    newTimes += 1;
                    count.put(config.getOddsType(),newTimes);
                }
            }else {
                count = new JSONObject().fluentPut(config.getOddsType(),newTimes);
            }
            redisClient.hSet(String.format(RcsConstant.RCS_COUNT_TIMES,config.getMatchId()),twoWayDouble.getMarketId().toString(),count.toJSONString());
            if (newTimes < 3){
                return Boolean.FALSE;
            }
        }else {
            TwowayDoubleOverLoadTriggerItem item = (TwowayDoubleOverLoadTriggerItem) twoWayDouble;
            Integer count = StringUtils.isNotBlank(times) ? Integer.parseInt(times) : NumberUtils.INTEGER_ZERO;
            // 主和大 为正
            if (BaseConstants.ODD_TYPE_1.equalsIgnoreCase(item.getOddsType()) ||
                    BaseConstants.ODD_TYPE_OVER.equalsIgnoreCase(item.getOddsType())){
                // 跳反方向需要清零
                if (count < 0){
                    count = 1;
                }else {
                    count += 1;
                }
            }else {
                // 跳反方向需要清零
                if (count > 0){
                    count = -1;
                }else {
                    count -= 1;
                }
            }
            redisClient.hSet(String.format(RcsConstant.RCS_COUNT_TIMES,config.getMatchId()),twoWayDouble.getMarketId().toString(),count.toString());
            // 单枪三次封盘，累计两次封盘
            if (item.getLimitLevel() == 1 && count < 3 && count > -3
                    && NumberUtils.INTEGER_ZERO.intValue() == config.getOddChangeRule()){
                return Boolean.FALSE;
            }else if (count < 2 && count > -2){
                return Boolean.FALSE;
            }
        }
        redisClient.expireKey(String.format(RcsConstant.RCS_COUNT_TIMES,config.getMatchId()), RcsConstant.BET_EXIST_TIME.intValue());
        return Boolean.TRUE;
    }
    /**
     * @Description   //封盘MQ消息封装
     * @Param [twoWayDouble, config]
     * @Author  sean
     * @Date   2021/11/30
     * @return com.alibaba.fastjson.JSONObject
     **/
    public JSONObject getSealMQJson(ThreewayOverLoadTriggerItem item, RcsMatchMarketConfig config) {
        List<Integer> playIdList = OddsCalcuCommonService.getLinkPlays(item.getPlayId());
        JSONObject obj =  new JSONObject().fluentPut("tradeLevel", NumberUtils.INTEGER_TWO)
                .fluentPut("sportId", NumberUtils.INTEGER_ONE)
                .fluentPut("matchId", item.getMatchId())
                .fluentPut("placeNum", config.getMarketIndex())
                .fluentPut("status", NumberUtils.INTEGER_ONE.toString())
                .fluentPut("linkedType", 7)
                .fluentPut("remark", "早盘跳赔触发封盘");
        if (HALF_COURT.contains(item.getPlayId()) || FULL_COURT.contains(item.getPlayId())){
            if (DataSourceTypeEnum.AUTOMATIC.getValue().intValue() == config.getDataSource()){
                obj.fluentPut("tradeLevel", 5).fluentPut("playIdList", playIdList);
            }else {
                if (1 == item.getPlayId()){
                    obj.fluentPut("playId", 4);
                }else if(4 == item.getPlayId()){
                    obj.fluentPut("playId", 1);
                }else if (17 == item.getPlayId()){
                    obj.fluentPut("playId", 19);
                }else if(19 == item.getPlayId()){
                    obj.fluentPut("playId", 17);
                }
                obj.put("isClose",1);
            }
        }else{
            obj.fluentPut("playId", item.getPlayId()).fluentPut("subPlayId", config.getSubPlayId());
        }
        return obj;
    }
}
