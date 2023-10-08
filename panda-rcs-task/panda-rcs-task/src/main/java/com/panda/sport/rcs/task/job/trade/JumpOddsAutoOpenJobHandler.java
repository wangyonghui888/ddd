package com.panda.sport.rcs.task.job.trade;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.panda.merge.dto.Request;
import com.panda.sport.data.rcs.dto.RedisCacheSyncBean;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.enums.TradeLevelEnum;
import com.panda.sport.rcs.enums.TradeStatusEnum;
import com.panda.sport.rcs.mapper.RcsTradeConfigMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsTradeConfig;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.task.wrapper.StandardMatchInfoService;
import com.panda.sport.rcs.utils.CommonUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Project Name : panda-rcs-task
 * @Package Name : panda-rcs-task
 * @Description : 早盘跳赔封盘，到今日（账务日）自动开盘，每天中午12点执行一次
 * @Author : Paca
 * @Date : 2021-02-10 14:17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Component
@JobHandler(value = "jumpOddsAutoOpenJobHandler")
public class JumpOddsAutoOpenJobHandler extends IJobHandler {

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private RcsTradeConfigMapper rcsTradeConfigMapper;
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;
    @Autowired
    private RedisClient redisClient;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
    	String rootLinkId = "jumpOddsAutoOpenJobHandler";
        long start = System.currentTimeMillis();
        try {
        	CommonUtils.mdcPut(rootLinkId);
            log.info("::{}::-早盘跳赔封盘自动开盘开始" ,rootLinkId);

            List<StandardMatchInfo> list = standardMatchInfoService.getCurrentBillDayMatchInfo(Lists.newArrayList(1L, 2L));
            if (CollectionUtils.isEmpty(list)) {
                String msg = "";
                log.info("::{}::-当前账务日无赛事" ,rootLinkId);
                return new ReturnT<>(msg);
            }
            clearCountTimes(list);

            List<String> matchIdList = list.stream().map(matchInfo -> String.valueOf(matchInfo.getId())).collect(Collectors.toList());
            List<RcsTradeConfig> configList = getJumpOddsSealConfig(matchIdList);
            if (CollectionUtils.isEmpty(configList)) {
                String msg = "当前账务日赛事无早盘跳赔封盘";
                log.info("::{}::-当前账务日赛事无早盘跳赔封盘" ,rootLinkId);
                return new ReturnT<>(msg);
            }

            Map<String, Long> sportIdMap = list.stream().collect(Collectors.toMap(matchInfo -> String.valueOf(matchInfo.getId()), StandardMatchInfo::getSportId));
            configList.forEach(config -> {
                String matchId = config.getMatchId();
                String playId = config.getTargerData();
                log.info("::{}::-早盘跳赔封盘自动开盘：matchId={},playId={},id={}" ,rootLinkId, matchId, playId, config.getId());
                RcsTradeConfig lastPlayConfig = rcsTradeConfigMapper.getLastPlayConfig(matchId, playId);
                if (lastPlayConfig != null && lastPlayConfig.getId().compareTo(config.getId()) > 0) {
                    log.warn("::{}::-早盘跳赔玩法已有相关操作：matchId={},playId={},id={},lastId={}" ,rootLinkId, matchId, playId, config.getId(), lastPlayConfig.getId());
                    return;
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tradeLevel", config.getTraderLevel());
                jsonObject.put("sportId", sportIdMap.get(matchId));
                jsonObject.put("matchId", matchId);
                jsonObject.put("playId", playId);
                jsonObject.put("status", TradeStatusEnum.OPEN.getStatus());
                jsonObject.put("linkedType", 8);
                jsonObject.put("remark", "跳赔封盘自动开盘");
                String linkId = String.format("jumpOddsSealAutoOpen_%s_%s_task", matchId, playId);
                Request<JSONObject> request = new Request<>();
                request.setData(jsonObject);
                request.setLinkId(linkId);
                request.setDataSourceTime(System.currentTimeMillis());
                log.info("::{}::-发送开盘MQ：topic=RCS_TRADE_UPDATE_MARKET_STATUS,linkId={}"  ,rootLinkId, linkId);
                producerSendMessageUtils.sendMessage("RCS_TRADE_UPDATE_MARKET_STATUS", config.getId().toString(), request.getLinkId(), request);
            });
            return SUCCESS;
        } catch (Exception e) {
            XxlJobLogger.log(e);
            log.error("::{}::-早盘跳赔封盘自动开盘异常:{}" ,rootLinkId, e.getMessage());
            ReturnT<String> result = FAIL;
            result.setMsg("早盘跳赔封盘自动开盘异常");
            result.setContent(e.getMessage());
            return result;
        } finally {
            long end = System.currentTimeMillis();
            log.info("::{}::-早盘跳赔封盘自动开盘结束，耗时{}毫秒" ,rootLinkId, end - start);
        }
    }

    /**
     * 获取跳赔封盘配置
     *
     * @param matchIdList
     * @return
     */
    private List<RcsTradeConfig> getJumpOddsSealConfig(List<String> matchIdList) {
        LambdaQueryWrapper<RcsTradeConfig> wrapper = Wrappers.lambdaQuery();
        wrapper.in(RcsTradeConfig::getMatchId, matchIdList)
                .eq(RcsTradeConfig::getTraderLevel, TradeLevelEnum.PLAY.getLevel())
                .eq(RcsTradeConfig::getStatus, TradeStatusEnum.SEAL.getStatus())
                .eq(RcsTradeConfig::getSourceType, 7);
        return rcsTradeConfigMapper.selectList(wrapper);
    }

    private void clearCountTimes(List<StandardMatchInfo> list) {
    	String linkId = CommonUtils.getLinkIdByMdc();
        list.forEach(matchInfo -> {
            if (!SportIdEnum.isFootball(matchInfo.getSportId())) {
                return;
            }
            //String key = String.format("rcs:risk:change:count:times:%s", matchInfo.getId());
            //redisClient.delete(key);
            //waldkir-redis集群-发送至risk进行delete
            String tag = matchInfo.getId().toString();
            String linkId1 = tag + "_" + System.currentTimeMillis();
            String key = String.format("rcs:risk:change:count:times:%s", matchInfo.getId());
            RedisCacheSyncBean syncBean = RedisCacheSyncBean.build(key, key);
            log.info("::{}::,发送MQ消息linkId={}",tag, syncBean);
            producerSendMessageUtils.sendMessage("RCS_RISK_REDIS_CACHE_SYNC", tag, linkId1, syncBean);
            log.info("::{}::-清除统计跳分次数：key={}",linkId , key);
        });
    }
}
