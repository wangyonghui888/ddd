package com.panda.sport.rcs.task.job.match;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.enums.*;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mongo.MarketCategory;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.task.wrapper.MongoService;
import com.panda.sport.rcs.utils.i18n.CommonUtils;
import com.panda.sport.rcs.vo.MarketStatusUpdateVO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * L/A+模式默认切换
 *
 * @author black
 * @ClassName: MatchLinkedSwitchJobHandler
 * @Description: TODO
 * @date 2021年5月22日 下午2:06:44
 */
@JobHandler(value = "matchLinkedSwitchJobHandler")
@Component
@Slf4j
public class MatchLinkedSwitchJobHandler extends IJobHandler {

    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    private MongoService mongoService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        try {
            log.info("开始执行L/A+模式默认切换(matchLinkedSwitchJobHandler)");

            Map<String, Object> queryParams = Maps.newHashMap();
            List<Map<String, Object>> list = standardMatchInfoMapper.querySwitchLinkedList(queryParams);
            if (CollectionUtils.isEmpty(list)) {
                return ReturnT.SUCCESS;
            }
            List<Map<String, Object>> liveList = Lists.newArrayList();
            String uuid = CommonUtils.getUUID();
            list.forEach(map -> {
                try {
                    String matchId = String.valueOf(map.get("id"));
                    String categoryId = String.valueOf(map.get("category_id"));
                    String matchType = String.valueOf(map.get("match_type"));
                    Long playId = Long.valueOf(String.valueOf(map.get("category_id")));

                    //切换到L模式
                    if (!RcsConstant.BASKETBALL_SINGLE_DOUBLE_PLAY.contains(playId)) {
                        Map<String, Object> bean = Maps.newHashMap();
                        bean.put("matchId", matchId);
                        bean.put("categoryId", categoryId);
                        bean.put("sportId", SportIdEnum.BASKETBALL.getId());
                        bean.put("tradeLevel", TradeLevelEnum.PLAY.getLevel());
                        bean.put("tradeType", TradeEnum.LINKAGE.getCode());
                        bean.put("linkedType", LinkedTypeEnum.DEFAULT_L.getCode());
                        bean.put("matchType", matchType);
                        bean.put("newFlag", 1);
                        // L/A+模式默认切换不封盘
                        bean.put("isSeal", YesNoEnum.N.getValue());
                        bean.put("uuid", uuid);
                        // 滚球延迟切换
                        if ("0".equals(matchType)) {
                            liveList.add(bean);
                            return;
                        }
                        sendMessage(matchId, categoryId, bean, uuid);
                    }

                    //切换到A+模式
                    if (RcsConstant.BASKETBALL_SINGLE_DOUBLE_PLAY.contains(playId)) {
                        log.info("::{}::开始执行单双玩法A+模式默认切换playId:{}", matchId, categoryId);
                        basketballSwitchAutoPlus(SportIdEnum.BASKETBALL.getId(), Long.valueOf(matchId), Integer.valueOf(matchType), playId);
                    }
                } catch (Exception e) {
                    log.error("L/A+模式默认切换异常", e);
                }
            });
            if (CollectionUtils.isEmpty(liveList)) {
                return ReturnT.SUCCESS;
            }
            CommonUtils.sleep(TimeUnit.SECONDS, 10L);
            liveList.forEach(bean -> {
                try {
                    String matchId = String.valueOf(bean.get("matchId"));
                    String categoryId = String.valueOf(bean.get("categoryId"));
                    sendMessage(matchId, categoryId, bean, uuid);
                } catch (Exception e) {
                    log.error("L/A+模式默认切换异常", e);
                }
            });
        } catch (Exception e) {
            log.error("L/A+模式默认切换异常", e);
        }
        return ReturnT.SUCCESS;
    }

    private void sendMessage(String matchId, String playId, Map<String, Object> bean, String uuid) {
        standardMatchInfoMapper.saveAutoSwitchLinked(bean);
        String tag = matchId + "_" + playId;
        String key = tag + "_" + uuid;
        producerSendMessageUtils.sendMessage("RCS_MARKET_TRADE_TYPE", tag, key, bean);
    }

    /**
     * 篮球切换A+模式
     *
     * @param sportId
     * @param matchId
     * @param matchType
     * @param playId
     */
    private void basketballSwitchAutoPlus(Long sportId, Long matchId, Integer matchType, Long playId) {
        if (SportIdEnum.BASKETBALL.isNo(sportId)) {
            return;
        }

        if(playId != null){
            //篮球单双玩法切换A+模式更新mongo玩法数据
            MarketCategory marketCategory = new MarketCategory();
            marketCategory.setMatchId(String.valueOf(matchId));
            marketCategory.setId(playId);
            marketCategory.setTradeType(TradeEnum.AUTOADD.getCode());
            Map maps = new HashMap<>();
            maps.put("matchId", String.valueOf(matchId));
            maps.put("id", playId);
            mongoService.update(maps, "rcs_market_category", marketCategory);
            log.info("::{}::篮球单双玩法切换A+模式更新mongo玩法数据,playId:{},tradeType:{}", matchId, playId, marketCategory.getTradeType());
        }

        CommonUtils.sleep(TimeUnit.MILLISECONDS, 500);
        // 玩法切换成A+
        String uuid = CommonUtils.getUUID();

        MarketStatusUpdateVO updateVO = new MarketStatusUpdateVO();
        updateVO.setTradeLevel(TradeLevelEnum.PLAY.getLevel());
        updateVO.setSportId(sportId);
        updateVO.setMatchId(matchId);
        updateVO.setCategoryId(playId);
        updateVO.setTradeType(TradeEnum.AUTOADD.getCode());
        updateVO.setLinkedType(LinkedTypeEnum.TRADE_MODE.getCode());
        updateVO.setRemark("单双类玩法默认切换到切A+模式：" + uuid);
        updateVO.setMatchType(matchType);
        updateVO.setIsSeal(YesNoEnum.N.getValue());
        updateVO.setNewFlag(1);
        String tag = matchId + "_" + playId;
        String key = tag + "_" + uuid;
        log.info("开始执行单双玩法A+模式默认切换消息推送tag:{},key:{}", tag, key);

        Map<String, Object> map = Maps.newHashMap();
        map.put("matchId", matchId);
        map.put("categoryId", playId);
        map.put("matchType", matchType);
        //保存入库
        standardMatchInfoMapper.saveAutoSwitchLinked(map);

        //消息推送
        producerSendMessageUtils.sendMessage("RCS_MARKET_TRADE_TYPE", tag, key, updateVO);

    }
}