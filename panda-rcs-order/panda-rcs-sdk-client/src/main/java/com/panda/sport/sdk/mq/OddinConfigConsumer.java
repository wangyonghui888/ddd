//package com.panda.sport.sdk.mq;
//
//
//import cn.hutool.core.util.ObjectUtil;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.TypeReference;
//import com.panda.sport.data.rcs.dto.OrderBean;
//import com.panda.sport.data.rcs.dto.oddin.SelectionIdDto;
//import com.panda.sport.rcs.cache.CaCheKeyConstants;
//import com.panda.sport.sdk.common.DataRealTimeMessageBean;
//import com.panda.sport.sdk.util.RcsLocalCacheUtils;
//import com.panda.sport.sdk.vo.StandardMarketMessage;
//import com.panda.sport.sdk.vo.StandardMarketOddsMessage;
//import com.panda.sport.sdk.vo.StandardMatchMarketMessage;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
//import org.apache.rocketmq.spring.annotation.ConsumeMode;
//import org.apache.rocketmq.spring.annotation.MessageModel;
//import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
//import org.apache.rocketmq.spring.core.RocketMQListener;
//import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
//import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import static com.panda.sport.rcs.cache.CaCheKeyConstants.ODDIN_CACHE_SELECTIONID_KEY;
//
//
//@Component
//@Slf4j
//@TraceCrossThread
//@RocketMQMessageListener(topic = "STANDARD_MARKET_ODDS", consumerGroup = "rcs_risk_oddIn_market_odds_group", messageModel = MessageModel.CLUSTERING, consumeMode = ConsumeMode.CONCURRENTLY)
//
//public class OddinConfigConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {
//    @Override
//    public void onMessage(String message) {
//        if (StringUtils.isNotBlank(message)) {
//            //防止泛型擦除
//            DataRealTimeMessageBean<StandardMatchMarketMessage> msg = JSON.parseObject(message, new TypeReference<DataRealTimeMessageBean<StandardMatchMarketMessage>>() {
//            });
//            StandardMatchMarketMessage matchMessage = msg.getData();
//            //判断是否为OD数据源的相关数据
//            if (matchMessage.getStandardMatchInfoId() == null || !"OD".equalsIgnoreCase(matchMessage.getDataSourceCode())) {
//                return;
//            }
////            log.info("获取融合下发:{}开售的盘口投注项的相关数据:{}", matchMessage.getDataSourceCode(), matchMessage);
//            //缓存盘口数据(有多个盘口数据同时下发)
//            List<StandardMarketMessage> marketList = matchMessage.getMarketList();
//            if (!CollectionUtils.isEmpty(marketList)) {
//                //把玩法ID作为Key,值为StandardMarketMessage对象数组
//                Map<Long, List<StandardMarketMessage>> map = marketList.stream().collect(Collectors.groupingBy(StandardMarketMessage::getMarketCategoryId));
//                //循环获取所有的玩法ID
//                for (Long playId : map.keySet()) {
//                    //通过不同的玩法ID获取对应下所有的盘口数据
//                    List<StandardMarketMessage> standardMarket = map.get(playId);
//                    //每个盘口下有多个投注项数据,盘口ID相同,但是每个投注项ID是唯一的
//                    standardMarket.forEach(e -> {
//                        //一场赛事有滚球和早盘
//                        //盘口类型早盘:1 ,滚球0
//                        Integer marketType = e.getMarketType();
//                        //获取所欲盘口下的多组投注项目数据
//                        List<StandardMarketOddsMessage> marketOddsList = e.getMarketOddsList();
//                        //每个投注项下的相关数据
//                        for (StandardMarketOddsMessage standardMarketMessage : marketOddsList) {
//                            SelectionIdDto selectionIdDto = new SelectionIdDto();
//                            //selectionID
//                            selectionIdDto.setSelection_id(standardMarketMessage.getExtraInfo());
//                            //数据源类型
//                            selectionIdDto.setDataSourceCode(standardMarketMessage.getDataSourceCode());
//                            //玩法ID
//                            selectionIdDto.setMarketCategoryId(playId);
//                            //盘口类型
//                            selectionIdDto.setMarketType(marketType);
//                            //盘口ID
//                            selectionIdDto.setMarketId(standardMarketMessage.getMarketId());
//                            //投注项ID
//                            Long Id = standardMarketMessage.getId();
//                            //key的组装规则=数据源:玩法ID_盘口类型_盘口ID_投注项ID
//                            String key = String.format(CaCheKeyConstants.ODDIN_CACHE_SELECTIONID_KEY, selectionIdDto.getDataSourceCode(), selectionIdDto.getMarketCategoryId(), selectionIdDto.getMarketType(), selectionIdDto.getMarketId(), Id);
//                            getSelectionId(key, selectionIdDto);
//                        }
//                    });
//                }
//            }
//        }
//        return;
//    }
//
//    /**
//     * 查询缓存中是否存在对应的key
//     *
//     * @param key
//     * @param selectionIdDto
//     */
//    private void getSelectionId(String key, SelectionIdDto selectionIdDto) {
//        log.info("组装的key的结构体:{},存储的selectionId相关数据:{}", key, selectionIdDto);
//        Map<String, String> map = (Map<String, String>) RcsLocalCacheUtils.timedCache.get(key);
//        if (ObjectUtil.isNotNull(map)) {
//            log.info("从缓存中获取的selection:{}", map.get(key));
//            return;
//        } else {
//            try {
//                inSertCache(key, selectionIdDto.getSelection_id(), selectionIdDto.getMarketType());
//            } catch (Exception e) {
//                log.error("oddIn相关数据插入缓存中失败::{}::", (Object) e.getStackTrace());
//            }
//
//        }
//    }
//
//    /**
//     * 接收mq下发的SelectionID 并且缓存到本地缓存中设定过期时间
//     *
//     * @param key
//     * @param selectionID
//     * @param marketType
//     */
//    public void inSertCache(String key, String selectionID, Integer marketType) {
//        //早盘设定为6分钟过期/滚球设定为1分钟
//        Map<String, String> selecionMap = new HashMap<>();
//        selecionMap.put(key, selectionID);
//        System.out.println(selecionMap);
//        //根据type判断是早盘还是滚球,两者设定的缓存过期时间不同
//        //优化后不判断是早盘还是滚球(主要看一场赛事得盘口数据多久下发一次)
//        if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(selectionID)) {
//            RcsLocalCacheUtils.timedCache.put(key, selecionMap, 20 * 1000L);
//            log.info("selectionId数据成功插入缓存中::{}::盘口类型:{}:key值:{}", selectionID, marketType, key);
//        }
//    }
//
//    /**
//     * 组装selectionId_key
//     *
//     * @param orderBean
//     * @return
//     */
//    public String toAssembleKey(OrderBean orderBean) {
//        //玩法ID
//        Integer playId = orderBean.getItems().get(0).getPlayId();
//        //盘口ID
//        Long marketId = orderBean.getItems().get(0).getMarketId();
//        //盘口类型
//        Integer MatchType = orderBean.getItems().get(0).getMatchType();
//        //投注项ID
//        Long playOptionsId = orderBean.getItems().get(0).getPlayOptionsId();
//        //数据源
//        String dataSourceCode = orderBean.getItems().get(0).getDataSourceCode();
//
//        return String.format(ODDIN_CACHE_SELECTIONID_KEY, dataSourceCode, playId, MatchType, marketId, playOptionsId);
//    }
//
//    @Override
//    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
//        defaultMQPushConsumer.setConsumeThreadMin(64);
//        defaultMQPushConsumer.setConsumeThreadMax(256);
//    }
//}
