package com.panda.sport.rcs.data.sync;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.client.naming.utils.RandomUtils;
import com.alibaba.nacos.common.util.UuidUtils;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.common.OddsValueConvertUtils;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.config.RedissonManager;
import com.panda.sport.rcs.data.mq.RcsConsumer;
import com.panda.sport.rcs.data.service.MongoService;
import com.panda.sport.rcs.data.utils.RDSProducerSendMessageUtils;
import com.panda.sport.rcs.data.utils.RcsDataRedis;
import com.panda.sport.rcs.enums.MarketStatusEnum;
import com.panda.sport.rcs.enums.OddsTypeEnum;
import com.panda.sport.rcs.pojo.MultiOddsExpiredBO;
import com.panda.sport.rcs.pojo.ThirdSportMarketMessage;
import com.panda.sport.rcs.pojo.ThirdSportMarketOdds;
import com.panda.sport.rcs.pojo.dto.StandardTxThirdMarketOddsDTO;
import com.panda.sport.rcs.pojo.dto.StandardTxThirdMarketPlayDTO;
import com.panda.sport.rcs.utils.ListUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 百家赔入库转发
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "STANDARD_TX_THIRD_MARKET_ODDS",
        consumerGroup = "RCS_DATA_RCS_STANDARD_TX_THIRD_MARKET_ODDS_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class StandardTxThirdMarketOddsConsumer extends RcsConsumer<Request<StandardTxThirdMarketOddsDTO>> {

    @Autowired
    MongoService mongoService;

    @Autowired
    MongoTemplate mongoTemplate;

    //topic
    private static final String  RCS_MULTIPLE_ODDS = "RCS_MULTIPLE_ODDS";
    //表
    private static final String  MULTIPLE_ODDS = "multiple_odds";
    //数据源
    private static final String MULTISOURCE = "MULTI-Source";
    /**
     * 分布式锁
     * 防止multiOddsExpiredData方法和MQ消费冲突
     *
     */
    private static final String LOCK_MULTISOURCE = "LOCK-MULTI-Source";

    protected static final String RCS_DATA_KEY_CACHE_KEY = RedisKeys.RCS_DATA_KEY_CACHE_KEY;

    protected static final String REDIS_KEY_MULTI_ODDS_WEIGHT_VALUE = "rcs:tournament:template:baijia:config:matchId:%s:matchType:%s";

    protected static final String MULTIPLE_ODDS_EXPIRED_DATA = "rcs:multipleOdds:expiredMarkData";

    protected static final String MULTIPLE_ODDS_INSERT_LOCK = "multipleOddsInsertLock";

    protected static final List<Long> passPlayIdA = Arrays.asList(2L,4L,18L,19L);

    protected static final List<Long> passPlayIdB = Arrays.asList(1L,2L,4L,17L,18L,19L);

    protected static final List<Long> passPlayIdC = Arrays.asList(1L,17L);

    protected static final String MATCH_TEMP_INFO = "matchTempInfo";

    @Autowired
    protected RDSProducerSendMessageUtils sendMessage;

    @Autowired
    protected RcsDataRedis redisClient;
    @Autowired
    protected RedissonManager redissonManager;

    @Override
    protected String getTopic() {
        return "STANDARD_TX_THIRD_MARKET_ODDS";
    }

    /**
     * @Description: 百家赔盘口赔率变化通知
     * @Author: V
     * @Date: 2019/12/12
     **/
    @Override
    public Boolean handleMs(Request<StandardTxThirdMarketOddsDTO> msg) {
        log.info("::{}::百家赔-datasync","RDRSTTMOG_"+msg.getLinkId());
        try {
            redissonManager.lock(LOCK_MULTISOURCE, 3*1000);
            StandardTxThirdMarketOddsDTO data = msg.getData();
            String standardMatchInfoId = data.getStandardMatchInfoId();
            List<ThirdSportMarketMessage> marketList = data.getMarketList();
            String weightTedisKey = String.format(REDIS_KEY_MULTI_ODDS_WEIGHT_VALUE,data.getStandardMatchInfoId(),marketList.get(0).getMarketType());
            Map map1 = redisClient.hGetAllToObj(weightTedisKey);
            Map<String,Float> weightMap = transfer(map1);
            //weightMap= JSONObject.parseObject("{\"TX-V9BET\":20.0,\"TX-188bet\":20.0,\"TX-978Bet\":20.0,\"TX-InPlayMatrix\":20.0,\"cautionValue\":5.0,\"TX-Singbet\":20.0}",new TypeReference<Map<String,Float>>() {});
            log.info("::{}::百家赔-权重值,{},{}", "RDRSTTMOG_" + msg.getLinkId() + "_" + standardMatchInfoId, data.getStandardMatchInfoId() + "|" + marketList.get(0).getMarketType());

            //datasourcecode+marketid   body
            Map<String,ThirdSportMarketMessage> sourceMarketIdMap = new HashMap<>();
            List<Long> playIds = marketList.stream().map(ThirdSportMarketMessage::getMarketCategoryId).collect(Collectors.toList());
            List<String> dataSources = marketList.stream().map(ThirdSportMarketMessage::getDataSourceCode).collect(Collectors.toList());
            Query query = new Query();
            Criteria criteria = new Criteria();
            criteria.and("matchId").is(data.getStandardMatchInfoId());
            criteria.and("marketCategoryId").in(playIds);
            criteria.and("dataSourceCode").ne(MULTISOURCE);
            query.addCriteria(criteria);
            List<StandardTxThirdMarketPlayDTO> multipleOdds = mongoTemplate.find(query,StandardTxThirdMarketPlayDTO.class);
//            StringBuilder  sb=new StringBuilder();
//            sb.append("[{\"matchId\":\"3087016\",\"sportId\":1,\"matchType\":0,\"marketCategoryId\":2,\"dataSourceCode\":\"TX-null\",\"insertTime\":1645765943412,\"updateTime\":1645765943412,\"marketList\":[{\"id\":null,\"relationMarketId\":\"140363107504823229\",\"marketCategoryId\":2,\"thirdMarketSourceId\":\"2369907_246_3_1\",\"marketType\":1,\"dataSourceCode\":\"TX-null\",\"status\":0,\"thirdMarketSourceStatus\":0,\"oddsTypeName\":\"FT - Over Under\",\"oddsValue\":\"3\",\"orderType\":null,\"oddsName\":\"FT - Over Under\",\"oddsMetric\":null,\"addition1\":\"3\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"addition5\":null,\"createTime\":null,\"modifyTime\":1645765943234,\"insertTime\":1645765943412,\"updateTime\":1645765943412,\"offerLineId\":1,\"dbData\":null,\"thirdSportMarketOddsList\":[{\"id\":\"148355915903039553\",\"oddsId\":\"148355915903039553\",\"marketId\":\"140363107504823229\",\"active\":1,\"oddsType\":\"Over\",\"addition1\":null,\"addition2\":null,\"addition3\":null,\"addition4\":null,\"addition5\":null,\"thirdOddsFieldSourceId\":\"2369907_246_3_1_1\",\"orderOdds\":1,\"name\":null,\"nameExpressionValue\":\"3.0\",\"oddsValue\":173000,\"fieldOddsValue\":\"1.73\",\"paOddsValue\":null,\"originalOddsValue\":181889,\"dataSourceCode\":\"TX-null\",\"isWarn\":null},{\"id\":\"146392313417572024\",\"oddsId\":\"146392313417572024\",\"marketId\":\"140363107504823229\",\"active\":1,\"oddsType\":\"Under\",\"addition1\":null,\"addition2\":null,\"addition3\":null,\"addition4\":null,\"addition5\":null,\"thirdOddsFieldSourceId\":\"2369907_246_3_1_2\",\"orderOdds\":2,\"name\":null,\"nameExpressionValue\":\"3.0\",\"oddsValue\":209000,\"fieldOddsValue\":\"2.09\",\"paOddsValue\":null,\"originalOddsValue\":222115,\"dataSourceCode\":\"TX-null\",\"isWarn\":null}]}]},{\"matchId\":\"3087016\",\"sportId\":1,\"matchType\":0,\"marketCategoryId\":4,\"dataSourceCode\":\"TX-null\",\"insertTime\":1645765911645,\"updateTime\":1645766151224,\"marketList\":[{\"id\":null,\"relationMarketId\":\"142399150963116520\",\"marketCategoryId\":4,\"thirdMarketSourceId\":\"2369907_33_-0.5_1\",\"marketType\":1,\"dataSourceCode\":\"TX-null\",\"status\":0,\"thirdMarketSourceStatus\":0,\"oddsTypeName\":\"FT - Asian Handicap\",\"oddsValue\":\"-0.5\",\"orderType\":null,\"oddsName\":\"FT - Asian Handicap\",\"oddsMetric\":null,\"addition1\":\"-0.5\",\"addition2\":\"-0.5\",\"addition3\":\"\",\"addition4\":\"\",\"addition5\":null,\"createTime\":null,\"modifyTime\":1645766150712,\"insertTime\":1645766151224,\"updateTime\":1645766151224,\"offerLineId\":1,\"dbData\":null,\"thirdSportMarketOddsList\":[{\"id\":null,\"oddsId\":\"142024282712394550\",\"marketId\":\"142399150963116520\",\"active\":1,\"oddsType\":\"1\",\"addition1\":\"51425\",\"addition2\":null,\"addition3\":null,\"addition4\":null,\"addition5\":null,\"thirdOddsFieldSourceId\":\"2369907_33_-0.5_1_1\",\"orderOdds\":1,\"name\":null,\"nameExpressionValue\":\"-0.5\",\"oddsValue\":195000,\"fieldOddsValue\":\"1.95\",\"paOddsValue\":null,\"originalOddsValue\":203309,\"dataSourceCode\":\"TX-null\",\"isWarn\":null},{\"id\":null,\"oddsId\":\"143080716007182505\",\"marketId\":\"142399150963116520\",\"active\":1,\"oddsType\":\"2\",\"addition1\":\"51426\",\"addition2\":null,\"addition3\":null,\"addition4\":null,\"addition5\":null,\"thirdOddsFieldSourceId\":\"2369907_33_-0.5_1_2\",\"orderOdds\":2,\"name\":null,\"nameExpressionValue\":\"-0.5\",\"oddsValue\":189000,\"fieldOddsValue\":\"1.89\",\"paOddsValue\":null,\"originalOddsValue\":196796,\"dataSourceCode\":\"TX-null\",\"isWarn\":null}]}]}]");
//            multipleOdds= JSONObject.parseObject(sb.toString(),new TypeReference<List<StandardTxThirdMarketPlayDTO>>() {});
            log.info("::{}::百家赔,{}","RDRSTTMOG_"+msg.getLinkId()+"_"+standardMatchInfoId,data.getStandardMatchInfoId());

            //组合全量数据到ourceMarketIdMap 以盘口级别组合
            combineToMap(marketList, sourceMarketIdMap, multipleOdds,weightMap,data.getSportId(),data.getStandardMatchInfoId(),msg.getLinkId(),1);
            //log.info("::{}::百家赔-sourceMarketIdMap:{},{}",msg.getLinkId(),JsonFormatUtils.toJson(sourceMarketIdMap),data.getStandardMatchInfoId());

            //id OddsMap  datasourcecode+marketid   body   权重map   生成MULTISOURCE数据源盘口  生成成oddsIdsMap
            Map<String, Map<String, Float>> oddsIdsMap = new HashMap<>();
            Map<String, ThirdSportMarketMessage> weightSourceMarketIdMap = generateMarketMultiSourceData(oddsIdsMap, sourceMarketIdMap,weightMap,1);

            if((!CollectionUtils.isEmpty(weightMap))&&weightMap.size()>1){
                //计算新赔率和预警
                Map<String, String> expreidOddsIdAndSource = getExpreidOddsIdAndSource(sourceMarketIdMap, marketList.get(0).getMarketType(),standardMatchInfoId,msg.getLinkId());
                calculateWeightAndWarnVal(weightMap, oddsIdsMap,expreidOddsIdAndSource);
                //百家赔入值
                inputVal(weightMap, oddsIdsMap, weightSourceMarketIdMap);
            }

            //组合入库
            ArrayList<ThirdSportMarketMessage> newobjects = save(data, sourceMarketIdMap, weightSourceMarketIdMap,"RDRSTTMOG_"+msg.getLinkId());

            if(!compareDataSource(dataSources,weightMap)){
                log.info("::{}::百家赔-权重值没有此数据源:{},{},{}","RDRSTTMOG_"+msg.getLinkId()+"_"+standardMatchInfoId,msg.getDataSourceCode(),JsonFormatUtils.toJson(weightMap),data.getStandardMatchInfoId()+"|"+marketList.get(0).getMarketType());
                return true;
            }
            if(CollectionUtils.isEmpty(weightMap)||weightMap.size()<2){
                log.info("::{}::百家赔-没有权重配置:{},{}","RDRSTTMOG_"+msg.getLinkId()+"_"+standardMatchInfoId,data.getStandardMatchInfoId(),marketList.get(0).getMarketType());
                return true;
            }
            if(CollectionUtils.isEmpty(newobjects)){
                log.info("::{}::百家赔-sendList-null{}","RDRSTTMOG_"+msg.getLinkId()+"_"+standardMatchInfoId,data.getStandardMatchInfoId());
                return true;
            }

            //发送
            sendToWS("RDRSTTMOG_"+msg.getLinkId(), data, playIds, newobjects,1);
        } catch (Exception e) {
            log.error("::{}::百家赔-datasync-百家赔错误{},{},{}", "RDRSTTMOG_" + msg.getLinkId(), JsonFormatUtils.toJson(msg), e.getMessage(), e);
        } finally {
            redissonManager.unlock(LOCK_MULTISOURCE);
        }
        return true;
    }

    /**
     * 删
     * @param sourceMarketIdMap
     * @param marketType
     * @param matchId
     * @param linkId
     * @return
     */
    private Map<String,String> getExpreidOddsIdAndSource(Map<String, ThirdSportMarketMessage> sourceMarketIdMap, Integer marketType, String matchId, String linkId) {
        if(CollectionUtils.isEmpty(sourceMarketIdMap)||1==marketType){return new HashMap();}
        String key = String.format(RCS_DATA_KEY_CACHE_KEY, MATCH_TEMP_INFO, matchId);
        String period = redisClient.hGet(key, "period");
        //暂停和未开赛阶段过滤
        if("31".equals(period) || "0".equals(period)){
            log.info("::{}::百家赔::{}::暂停-未开赛:{}", linkId, matchId, period);
            return new HashMap();
        }
        long now = System.currentTimeMillis();
        // now = 1645598566148L;
        HashMap<String, String> map = new HashMap<>();
        //超时的加进去
        for (String s : sourceMarketIdMap.keySet()) {
            ThirdSportMarketMessage thirdSportMarketMessage= sourceMarketIdMap.get(s);
            List<ThirdSportMarketOdds> thirdSportMarketOddsList = thirdSportMarketMessage.getThirdSportMarketOddsList();
            for (ThirdSportMarketOdds thirdSportMarketOdds : thirdSportMarketOddsList) {
                if(passPlayIdC.contains(thirdSportMarketMessage.getMarketCategoryId())&&now-thirdSportMarketMessage.getUpdateTime()>5*60*1000){
                    map.put(thirdSportMarketOdds.getOddsId()+"_"+thirdSportMarketMessage.getDataSourceCode(),thirdSportMarketMessage.getDataSourceCode());
                }else if(passPlayIdA.contains(thirdSportMarketMessage.getMarketCategoryId())&&now-thirdSportMarketMessage.getUpdateTime()>1*60*1000){
                    map.put(thirdSportMarketOdds.getOddsId()+"_"+thirdSportMarketMessage.getDataSourceCode(),thirdSportMarketMessage.getDataSourceCode());
                }
            }
        }
        //未超时的减去
        for (String s : sourceMarketIdMap.keySet()) {
            ThirdSportMarketMessage thirdSportMarketMessage= sourceMarketIdMap.get(s);
            List<ThirdSportMarketOdds> thirdSportMarketOddsList = thirdSportMarketMessage.getThirdSportMarketOddsList();
            for (ThirdSportMarketOdds thirdSportMarketOdds : thirdSportMarketOddsList) {
                if(passPlayIdC.contains(thirdSportMarketMessage.getMarketCategoryId())&&now-thirdSportMarketMessage.getUpdateTime()<5*60*1000){
                    map.remove(thirdSportMarketOdds.getOddsId()+"_"+thirdSportMarketMessage.getDataSourceCode(),thirdSportMarketMessage.getDataSourceCode());
                }else if(passPlayIdA.contains(thirdSportMarketMessage.getMarketCategoryId())&&now-thirdSportMarketMessage.getUpdateTime()<1*60*1000){
                    map.remove(thirdSportMarketOdds.getOddsId()+"_"+thirdSportMarketMessage.getDataSourceCode(),thirdSportMarketMessage.getDataSourceCode());
                }
            }
        }
        //log.info("::{}::百家赔-计算过期赔率:{}，{},{}",linkId,now,JsonFormatUtils.toJson(map),matchId);
        log.info("::{}::百家赔-计算过期赔率:{}，{}","RDRSTTMOG_"+linkId+"_"+matchId,now,matchId);
        return map;
    }

    /**
     * 去重复盘口
     * @param sourceMarketIdMap
     */
    private void delDuplicateMarketId(Map<String, ThirdSportMarketMessage> sourceMarketIdMap) {
        delDuplicateMarketIdA(sourceMarketIdMap);
        delDuplicateMarketIdB(sourceMarketIdMap);
    }

    /**
     * 去重复盘口id
     * @param sourceMarketIdMap
     */
    private void delDuplicateMarketIdA(Map<String, ThirdSportMarketMessage> sourceMarketIdMap) {
        List<ThirdSportMarketMessage> objects = mapToList(sourceMarketIdMap);
        HashMap<String, List<ThirdSportMarketMessage>> duplicateMap = new HashMap<>();
        //重复标记
        HashMap<String, ThirdSportMarketMessage> markMap = new HashMap<>();
        for (ThirdSportMarketMessage thirdSportMarketMessage : objects) {
            Object mark = markMap.get(thirdSportMarketMessage.getDataSourceCode()+"_"+thirdSportMarketMessage.getRelationMarketId());
            if(null!=mark){
                List dupList = duplicateMap.get(thirdSportMarketMessage.getDataSourceCode()+"_"+thirdSportMarketMessage.getRelationMarketId());
                if(CollectionUtils.isEmpty(dupList)){
                    dupList=new ArrayList();
                }
                dupList.add(markMap.get(thirdSportMarketMessage.getDataSourceCode()+"_"+thirdSportMarketMessage.getRelationMarketId()));
                dupList.add(thirdSportMarketMessage);
                duplicateMap.put(thirdSportMarketMessage.getDataSourceCode()+"_"+thirdSportMarketMessage.getRelationMarketId(),dupList);
            }
            markMap.put(thirdSportMarketMessage.getDataSourceCode()+"_"+thirdSportMarketMessage.getRelationMarketId(),thirdSportMarketMessage);
        }

        if(CollectionUtils.isEmpty(duplicateMap)){return;}
        for (String key : duplicateMap.keySet()) {
            List<ThirdSportMarketMessage> thirdSportMarketMessages = duplicateMap.get(key);
            Long min  = thirdSportMarketMessages.get(0).getModifyTime();
            ThirdSportMarketMessage minObj = thirdSportMarketMessages.get(0);
            for (int i = 0; i < thirdSportMarketMessages.size(); i++) {
                if (min > thirdSportMarketMessages.get(i).getModifyTime()){
                    min = thirdSportMarketMessages.get(i).getModifyTime();
                    minObj=thirdSportMarketMessages.get(i);
                }
            }
            sourceMarketIdMap.remove(minObj.getDataSourceCode() + "_" + minObj.getMarketCategoryId() + "_" + minObj.getOfferLineId());
        }
    }

    /**
     * 去重复盘口 addtion1
     * @param sourceMarketIdMap
     */
    private void delDuplicateMarketIdB(Map<String, ThirdSportMarketMessage> sourceMarketIdMap) {
        List<ThirdSportMarketMessage> objects = mapToList(sourceMarketIdMap);
        HashMap<String, List<ThirdSportMarketMessage>> duplicateMap = new HashMap<>();
        //重复标记
        HashMap<String, ThirdSportMarketMessage> markMap = new HashMap<>();
        for (ThirdSportMarketMessage thirdSportMarketMessage : objects) {
            Object mark = markMap.get(thirdSportMarketMessage.getDataSourceCode()+"_"+thirdSportMarketMessage.getMarketCategoryId()+"_"+thirdSportMarketMessage.getAddition1());
            if(null!=mark){
                List dupList = duplicateMap.get(thirdSportMarketMessage.getDataSourceCode()+"_"+thirdSportMarketMessage.getMarketCategoryId()+"_"+thirdSportMarketMessage.getAddition1());
                if(CollectionUtils.isEmpty(dupList)){
                    dupList=new ArrayList();
                }
                dupList.add(markMap.get(thirdSportMarketMessage.getDataSourceCode()+"_"+thirdSportMarketMessage.getMarketCategoryId()+"_"+thirdSportMarketMessage.getAddition1()));
                dupList.add(thirdSportMarketMessage);
                duplicateMap.put(thirdSportMarketMessage.getDataSourceCode()+"_"+thirdSportMarketMessage.getMarketCategoryId()+"_"+thirdSportMarketMessage.getAddition1(),dupList);
            }
            markMap.put(thirdSportMarketMessage.getDataSourceCode()+"_"+thirdSportMarketMessage.getMarketCategoryId()+"_"+thirdSportMarketMessage.getAddition1(),thirdSportMarketMessage);
        }

        if(CollectionUtils.isEmpty(duplicateMap)){return;}
        for (String key : duplicateMap.keySet()) {
            List<ThirdSportMarketMessage> thirdSportMarketMessages = duplicateMap.get(key);
            Long min  = thirdSportMarketMessages.get(0).getModifyTime();
            ThirdSportMarketMessage minObj = thirdSportMarketMessages.get(0);
            for (int i = 0; i < thirdSportMarketMessages.size(); i++) {
                if (min > thirdSportMarketMessages.get(i).getModifyTime()){
                    min = thirdSportMarketMessages.get(i).getModifyTime();
                    minObj=thirdSportMarketMessages.get(i);
                }
            }
            sourceMarketIdMap.remove(minObj.getDataSourceCode() + "_" + minObj.getMarketCategoryId() + "_" + minObj.getOfferLineId());
        }
    }

    /**
     *发送到ws
     * @param linkId
     * @param data
     * @param playIds
     * @param newobjects
     * @param channel  1上游  2过期检测 3权重
     */
    private void sendToWS(String linkId, StandardTxThirdMarketOddsDTO data, List<Long> playIds, ArrayList<ThirdSportMarketMessage> newobjects, int channel) {
        StandardTxThirdMarketOddsDTO standardTxThirdMarketOddsDTO = new StandardTxThirdMarketOddsDTO();
        standardTxThirdMarketOddsDTO.setMarketList(newobjects);
        standardTxThirdMarketOddsDTO.setStandardMatchInfoId(data.getStandardMatchInfoId());
        standardTxThirdMarketOddsDTO.setMatchType(data.getMatchType());
        standardTxThirdMarketOddsDTO.setSportId(data.getSportId());
        standardTxThirdMarketOddsDTO.setMarketCategoryIds(playIds);
        standardTxThirdMarketOddsDTO.setChannel(channel);
        standardTxThirdMarketOddsDTO.setAddition1s(newobjects.stream().filter(e->passPlayIdA.contains(e.getMarketCategoryId())).map(ThirdSportMarketMessage::getAddition1).collect(Collectors.toList()));
        standardTxThirdMarketOddsDTO.setGlobalId(linkId);
        Request<StandardTxThirdMarketOddsDTO> resultRequest = new Request<>();
        resultRequest.setDataSourceCode(MULTISOURCE);
        resultRequest.setData(standardTxThirdMarketOddsDTO);
        resultRequest.setLinkId(linkId);
        sendMessage.sendMessage(RCS_MULTIPLE_ODDS, null,linkId, resultRequest);
    }

    /**
     * 保存
     * @param data
     * @param sourceMarketIdMap
     * @param weightSourceMarketIdMap
     * @param linkId
     * @return
     */
    private ArrayList<ThirdSportMarketMessage> save(StandardTxThirdMarketOddsDTO data, Map<String, ThirdSportMarketMessage> sourceMarketIdMap, Map<String, ThirdSportMarketMessage> weightSourceMarketIdMap, String linkId) {
        long now = System.currentTimeMillis();
        ArrayList<ThirdSportMarketMessage> newobjects = new ArrayList<>();
        sourceMarketIdMap.putAll(weightSourceMarketIdMap);
        delDuplicateMarketId(sourceMarketIdMap);
        for (String key : sourceMarketIdMap.keySet()) {
            newobjects.add(sourceMarketIdMap.get(key));
        }
        if(data != null && CollUtil.isNotEmpty(data.getMarketList())) {
            printLSLog(data.getMarketList().get(0).getDataSourceCode(), newobjects,linkId, data.getStandardMatchInfoId(), "入库");
        }
        Map<String, List<ThirdSportMarketMessage>> collect1 = newobjects.stream().collect(Collectors.groupingBy(e -> e.getDataSourceCode() + "_" + e.getMarketCategoryId()));
        //log.info("::{}::百家赔-newobjects值{},{},now{}",linkId,JsonFormatUtils.toJson(collect1),data.getStandardMatchInfoId(),now);
        for (String key : collect1.keySet()) {
            List<ThirdSportMarketMessage> thirdSportMarketMessages ;
            try {
                thirdSportMarketMessages = collect1.get(key).stream().sorted(Comparator.comparing(ThirdSportMarketMessage::getOfferLineId)).collect(Collectors.toList());
            }catch (Exception e){
                thirdSportMarketMessages = collect1.get(key);
            }
            try {
                //LS数据源给的三项盘投注项顺序是 1 X 2,需要调整为 1 2 X
                if (CollUtil.isNotEmpty(thirdSportMarketMessages)) {
                    for (ThirdSportMarketMessage market : thirdSportMarketMessages) {
                        //目前百家赔只有 全场独赢(1L) 和 半场独赢(17L)是三项盘
                        if (!(market.getMarketCategoryId() == 1L || market.getMarketCategoryId() == 17L)) {
                            continue;
                        }
                        if (CollUtil.isEmpty(market.getThirdSportMarketOddsList())) {
                            continue;
                        }
                        List<ThirdSportMarketOdds> oddsList = market.getThirdSportMarketOddsList();
                        List<ThirdSportMarketOdds> oddsSortList = new ArrayList<>();
                        oddsSortList.add(oddsList.stream().filter(o -> OddsTypeEnum.isHome(o.getOddsType())).findFirst().get());
                        oddsSortList.add(oddsList.stream().filter(o -> OddsTypeEnum.isAway(o.getOddsType())).findFirst().get());
                        oddsSortList.add(oddsList.stream().filter(o -> OddsTypeEnum.DRAW.equals(o.getOddsType())).findFirst().get());
                        market.setThirdSportMarketOddsList(oddsSortList);
                    }
                }
            } catch (Exception e){
                log.error("::{}::百家赔三项盘排序错误", linkId, e);
            }

            //==================测试重复代码
//            HashMap<String, Object> objectObjectHashMap = new HashMap<>();
//            for (ThirdSportMarketMessage thirdSportMarketMessage : thirdSportMarketMessages) {
//                if(objectObjectHashMap.get(thirdSportMarketMessage.getRelationMarketId())!=null){
//                    System.out.println(1);
//                }
//                objectObjectHashMap.put(thirdSportMarketMessage.getRelationMarketId(),"1");
//            }
            //==================
            StandardTxThirdMarketPlayDTO standardTxThirdMarketPlayDTO = new StandardTxThirdMarketPlayDTO();
            standardTxThirdMarketPlayDTO.setMarketList(thirdSportMarketMessages);
            standardTxThirdMarketPlayDTO.setUpdateTime(now);
            standardTxThirdMarketPlayDTO.setDataSourceCode(thirdSportMarketMessages.get(0).getDataSourceCode());
            standardTxThirdMarketPlayDTO.setMarketCategoryId(thirdSportMarketMessages.get(0).getMarketCategoryId());
            standardTxThirdMarketPlayDTO.setSportId(data.getSportId());
            standardTxThirdMarketPlayDTO.setMatchType(data.getMatchType());
            standardTxThirdMarketPlayDTO.setMatchId(data.getStandardMatchInfoId());
            Map map = new HashMap();
            map.put("matchId", data.getStandardMatchInfoId());
            map.put("marketCategoryId", thirdSportMarketMessages.get(0).getMarketCategoryId());
            map.put("dataSourceCode", thirdSportMarketMessages.get(0).getDataSourceCode());
            //mongoService.upsert(map, MULTIPLE_ODDS, standardTxThirdMarketPlayDTO);
            String lockKey = String.format(RCS_DATA_KEY_CACHE_KEY, MULTIPLE_ODDS_INSERT_LOCK, data.getStandardMatchInfoId() + "|" + thirdSportMarketMessages.get(0).getMarketCategoryId() + "|" + thirdSportMarketMessages.get(0).getDataSourceCode()+ "|" + thirdSportMarketMessages.get(0).getMarketType());

            if (mongoService.exists(map, MULTIPLE_ODDS, standardTxThirdMarketPlayDTO)) {
                log.info("::{}::百家赔-update1-{},{},{}",linkId+"_"+data.getStandardMatchInfoId(),data.getStandardMatchInfoId(),thirdSportMarketMessages.get(0).getMarketCategoryId(),thirdSportMarketMessages.get(0).getDataSourceCode());
                mongoService.update(map, MULTIPLE_ODDS, standardTxThirdMarketPlayDTO);
            } else {
                long lockId = RandomUtils.nextLong();
                boolean lock = redisClient.lock(lockKey, lockId, 10);
                if(lock){
                    log.info("::{}::百家赔-insert1-{},{},{}",linkId+"_"+data.getStandardMatchInfoId(),data.getStandardMatchInfoId(),thirdSportMarketMessages.get(0).getMarketCategoryId(),thirdSportMarketMessages.get(0).getDataSourceCode());
                    standardTxThirdMarketPlayDTO.setInsertTime(now);
                    mongoService.insert(standardTxThirdMarketPlayDTO, MULTIPLE_ODDS);
                }else {
                    log.info("::{}::百家赔-update2-{},{},{}",linkId+"_"+data.getStandardMatchInfoId(),data.getStandardMatchInfoId(),thirdSportMarketMessages.get(0).getMarketCategoryId(),thirdSportMarketMessages.get(0).getDataSourceCode());
                    mongoService.update(map, MULTIPLE_ODDS, standardTxThirdMarketPlayDTO);
                }
                redisClient.unlock(lockKey, lockId);
            }
        }
        return newobjects;
    }


    /**
     * 百家赔入值
     *
     * @param weightMap
     * @param oddsIdsMap
     * @param weightSourceMarketIdMap
     * @return
     */
    private void inputVal(Map<String, Float> weightMap, Map<String, Map<String, Float>> oddsIdsMap, Map<String, ThirdSportMarketMessage> weightSourceMarketIdMap) {
        for (String key : weightSourceMarketIdMap.keySet()) {
            ThirdSportMarketMessage thirdSportMarketMessage = weightSourceMarketIdMap.get(key);
            List<ThirdSportMarketOdds> thirdSportMarketOddsList = thirdSportMarketMessage.getThirdSportMarketOddsList();
            if (CollectionUtils.isEmpty(thirdSportMarketOddsList)) {
                continue;
            }
            for (ThirdSportMarketOdds thirdSportMarketOdds : thirdSportMarketOddsList) {
                Map<String, Float> dataSourceOddsMap = oddsIdsMap.get(String.valueOf(thirdSportMarketOdds.getOddsId()));
                thirdSportMarketOdds.setOddsValue(dataSourceOddsMap.get("weightOdds").intValue());
                thirdSportMarketOdds.setIsWarn(dataSourceOddsMap.get("warnRatio") > weightMap.get("cautionValue") / 100 ? true : false);
                thirdSportMarketOdds.setFieldOddsValue(OddsValueConvertUtils.convertAndDefaultDisply(null, dataSourceOddsMap.get("weightOdds").intValue()));
            }
        }
    }

    /**
     * 计算新赔率和预警
     *
     * @param weightMap
     * @param oddsIdsMap
     * @param expreidMap 过期玩法 <oddsid+source,source> 不参与计算
     */
    private void calculateWeightAndWarnVal(Map<String, Float> weightMap, Map<String, Map<String, Float>> oddsIdsMap, Map<String, String> expreidMap) {
        for (String oddsId : oddsIdsMap.keySet()) {
            int weightSize = 0;
            Float weightOdds = 0F;
            Float avgOdds = 0F;
            Float avgWeightValue = 0F;
            Map<String, Float> oddsMap = oddsIdsMap.get(oddsId);
            for (String source : weightMap.keySet()) {
                if (source.equals("cautionValue")) {
                    continue;
                }
                Float odds = oddsMap.get(source);
                if (odds != null && odds.floatValue() != 0 && null == expreidMap.get(oddsId + "_" + source)) {
                    weightSize = weightSize + 1;
                    avgOdds = avgOdds + odds;
                }
            }
            for (String source : weightMap.keySet()) {
                if (source.equals("cautionValue")) {
                    continue;
                }
                Float odds = oddsMap.get(source);
                if (odds == null || odds.floatValue() == 0 || null != expreidMap.get(oddsId + "_" + source)) {
                    Float divideWeightValue = 0F;
                    if (0 != weightSize) {
                        divideWeightValue = weightMap.get(source) / weightSize;
                    }
                    avgWeightValue = avgWeightValue + divideWeightValue;
                }
            }
            for (String source : oddsMap.keySet()) {
                if (null != weightMap.get(source) && null == expreidMap.get(oddsId + "_" + source)) {
                    weightOdds = weightOdds + (oddsMap.get(source) * ((weightMap.get(source) + avgWeightValue) / 100));
                }
            }
            Float warnRatio = 0F;
            if (0 != weightSize) {
                warnRatio = Math.abs(weightOdds - (avgOdds / weightSize)) / (avgOdds / weightSize);
            }
            oddsMap.put("warnRatio",warnRatio==null?0:warnRatio);
            oddsMap.put("weightOdds",weightOdds);
        }
    }

    /**
     * datasourcecode+marketid   body   权重map   生成MULTISOURCE数据源盘口  生成成oddsIdsMap
     * @param oddsIdsMap
     * @param sourceMarketIdMap
     * @param weightMap
     * @param channel 1上游  2过期检测 3权重
     * @return
     */
    private Map<String, ThirdSportMarketMessage> generateMarketMultiSourceData(Map<String, Map<String, Float>> oddsIdsMap, Map<String, ThirdSportMarketMessage> sourceMarketIdMap, Map<String, Float> weightMap, int channel) {
        //权重盘口map
        Map<String,ThirdSportMarketMessage> weightSourceMarketIdMap = new HashMap<>();
        //重复赔率比较时间map
        Map<String,Long> duplicateMap = new HashMap<>();
        for (String key : sourceMarketIdMap.keySet()) {
            ThirdSportMarketMessage thirdSportMarketMessage = sourceMarketIdMap.get(key);
            List<ThirdSportMarketOdds> thirdSportMarketOddsList = thirdSportMarketMessage.getThirdSportMarketOddsList();
            for (ThirdSportMarketOdds thirdSportMarketOdds : thirdSportMarketOddsList) {
                Map<String, Float> dataSourceOddsMap = oddsIdsMap.get(String.valueOf(thirdSportMarketOdds.getOddsId()));
                if(null==dataSourceOddsMap){
                    dataSourceOddsMap = new HashMap<>();
                    dataSourceOddsMap.put(thirdSportMarketOdds.getDataSourceCode(),thirdSportMarketOdds.getOddsValue().floatValue());
                    Long upt = duplicateMap.get(thirdSportMarketOdds.getDataSourceCode() + "_" + thirdSportMarketOdds.getOddsId());
                    if(null==upt||thirdSportMarketMessage.getUpdateTime()>upt){
                        oddsIdsMap.put(String.valueOf(thirdSportMarketOdds.getOddsId()),dataSourceOddsMap);
                    }
                    duplicateMap.put(thirdSportMarketOdds.getDataSourceCode()+"_"+thirdSportMarketOdds.getOddsId(),thirdSportMarketMessage.getUpdateTime());
                }else{
                    dataSourceOddsMap.put(thirdSportMarketOdds.getDataSourceCode(),thirdSportMarketOdds.getOddsValue().floatValue());
                }
            }
            //null==thirdSportMarketMessage.getDbData()  不用库里数据产生multi-source  权重修改进行计算
            if(((!CollectionUtils.isEmpty(weightMap))&&weightMap.size()>1&&null!=weightMap.get(thirdSportMarketMessage.getDataSourceCode()))||3==channel) {
                ThirdSportMarketMessage thirdSportMarketMessage1 = BeanCopyUtils.deepCopyProperties(thirdSportMarketMessage, ThirdSportMarketMessage.class);
                thirdSportMarketMessage1.setDataSourceCode(MULTISOURCE);
                List<ThirdSportMarketOdds> thirdSportMarketOddsList1 = thirdSportMarketMessage1.getThirdSportMarketOddsList();
                for (ThirdSportMarketOdds thirdSportMarketOdds : thirdSportMarketOddsList1) {
                    thirdSportMarketOdds.setDataSourceCode(MULTISOURCE);
                }
                if(CollectionUtils.isEmpty(thirdSportMarketOddsList1)){
                    ThirdSportMarketMessage thirdSportMarketMessage2 = weightSourceMarketIdMap.get(MULTISOURCE + "_" + thirdSportMarketMessage.getRelationMarketId());
                    if(null==thirdSportMarketMessage2){
                        weightSourceMarketIdMap.put(MULTISOURCE + "_" + thirdSportMarketMessage.getRelationMarketId(), thirdSportMarketMessage1);
                    }
                }else{
                    weightSourceMarketIdMap.put(MULTISOURCE + "_" + thirdSportMarketMessage.getRelationMarketId(), thirdSportMarketMessage1);
                }
            }
        }
        return weightSourceMarketIdMap;
    }


    /**
     * 组合全量数据到ourceMarketIdMap 以盘口级别组合
     * @param marketList
     * @param sourceMarketIdMap
     * @param multipleOdds
     * @param weightMap
     * @param sportId
     * @param linkId
     * @param channel  1上游  2过期检测 3权重
     */
    private void combineToMap(List<ThirdSportMarketMessage> marketList, Map<String, ThirdSportMarketMessage> sourceMarketIdMap, List<StandardTxThirdMarketPlayDTO> multipleOdds, Map<String, Float> weightMap, Long sportId, String matchId, String linkId, int channel) {
        long now = System.currentTimeMillis();
        if (!CollectionUtils.isEmpty(multipleOdds)) {
            List<String> closeMarktIds = Collections.EMPTY_LIST;
            if (!CollectionUtils.isEmpty(marketList) && marketList.get(0).getDataSourceCode().startsWith("LS")) {
                String currDataSource = marketList.get(0).getDataSourceCode();
                List<ThirdSportMarketMessage> currDataSourceList = marketList.stream()
                        .filter(item -> item.getDataSourceCode().equals(currDataSource))
                        .collect(Collectors.toList());
                //同数据源所有的盘口id都是唯一的
                closeMarktIds = currDataSourceList.stream()
                        .filter(item -> MarketStatusEnum.CLOSE.getState() == item.getStatus())
                        .map(ThirdSportMarketMessage::getRelationMarketId)
                        .collect(Collectors.toList());
                //打印日志
                for(ThirdSportMarketMessage item : currDataSourceList){
                    if(!closeMarktIds.contains(item.getRelationMarketId())){
                        continue;
                    }
                    log.info("{}::百家赔LS::{}::{}::玩法::{}::数据源::{}::盘口和坑位::{}", linkId,
                            matchId, "关盘移除", item.getMarketCategoryId(), currDataSource, item.getRelationMarketId());
                }
            }
            for (StandardTxThirdMarketPlayDTO multipleOdd : multipleOdds) {
                List<ThirdSportMarketMessage> marketList1 = multipleOdd.getMarketList();
                for (ThirdSportMarketMessage thirdSportMarketMessage : marketList1) {
                    //关盘的数据不要再次入库了，保存关盘数据不处理
                    if(closeMarktIds.contains(thirdSportMarketMessage.getRelationMarketId())){
                        continue;
                    }
                    thirdSportMarketMessage.setDbData(1);
                    if ((null!=weightMap && null != weightMap.get(thirdSportMarketMessage.getDataSourceCode()))||3==channel) {
                        thirdSportMarketMessage.setUpdateTime(thirdSportMarketMessage.getUpdateTime());
                        sourceMarketIdMap.put(thirdSportMarketMessage.getDataSourceCode() + "_" + thirdSportMarketMessage.getMarketCategoryId() + "_" + thirdSportMarketMessage.getOfferLineId(), thirdSportMarketMessage);
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(marketList)) {
            //LS数据源朋友OfferLineId坑位字段，这里需要自己给值
            marketList = offerLineCreateGroup(marketList, multipleOdds, linkId);
            printLSLog(marketList.get(0).getDataSourceCode(), marketList,linkId, matchId, "坑位赋值后");
            for (ThirdSportMarketMessage thirdSportMarketMessage : marketList) {
                if(0==thirdSportMarketMessage.getMarketType()){
                    //滚球5分钟标记
                    MultiOddsExpiredBO multiOddsExpiredBO = new MultiOddsExpiredBO();
                    multiOddsExpiredBO.setTime(now);
                    multiOddsExpiredBO.setSportId(sportId);
                    multiOddsExpiredBO.setMarketCategoryId(thirdSportMarketMessage.getMarketCategoryId());
                    multiOddsExpiredBO.setMatchId(matchId);
                    multiOddsExpiredBO.setDataSourceCode(thirdSportMarketMessage.getDataSourceCode());
                    multiOddsExpiredBO.setOfferLineId(thirdSportMarketMessage.getOfferLineId());
                    multiOddsExpiredBO.setRelationMarketId(thirdSportMarketMessage.getRelationMarketId());
                    redisClient.hSetAverage(MULTIPLE_ODDS_EXPIRED_DATA,thirdSportMarketMessage.getDataSourceCode()+"_"+matchId+"_"+thirdSportMarketMessage.getMarketCategoryId()+"_"+thirdSportMarketMessage.getRelationMarketId(),JsonFormatUtils.toJson(multiOddsExpiredBO));
                }
                ThirdSportMarketMessage thirdSportMarketMessage1 = sourceMarketIdMap.get(thirdSportMarketMessage.getDataSourceCode() + "_" + thirdSportMarketMessage.getMarketCategoryId() + "_" + thirdSportMarketMessage.getOfferLineId());
                if (MarketStatusEnum.CLOSE.getState() == thirdSportMarketMessage.getStatus()) {
                    continue;
                }
                if(CollectionUtils.isEmpty(thirdSportMarketMessage.getThirdSportMarketOddsList())||0==thirdSportMarketMessage.getThirdSportMarketOddsList().get(0).getOddsValue().intValue()){continue;}
                if (null == thirdSportMarketMessage1) {
                    List<ThirdSportMarketOdds> thirdSportMarketOddsList = thirdSportMarketMessage.getThirdSportMarketOddsList();
                    for (ThirdSportMarketOdds thirdSportMarketOdds : thirdSportMarketOddsList) {
                        thirdSportMarketOdds.setOddsId(thirdSportMarketOdds.getId());
                        thirdSportMarketOdds.setFieldOddsValue(OddsValueConvertUtils.convertAndDefaultDisply(null, Integer.valueOf(thirdSportMarketOdds.getOddsValue())));
                    }
                    thirdSportMarketMessage.setUpdateTime(now);
                    thirdSportMarketMessage.setInsertTime(now);
                    if (passPlayIdB.contains(thirdSportMarketMessage.getMarketCategoryId())) {
                        sourceMarketIdMap.put(thirdSportMarketMessage.getDataSourceCode() + "_" + thirdSportMarketMessage.getMarketCategoryId() + "_" + thirdSportMarketMessage.getOfferLineId(), thirdSportMarketMessage);
                    }
                } else {
                    thirdSportMarketMessage1.setRelationMarketId(thirdSportMarketMessage.getRelationMarketId());
                    thirdSportMarketMessage1.setMarketCategoryId(thirdSportMarketMessage.getMarketCategoryId());
                    thirdSportMarketMessage1.setThirdMarketSourceId(thirdSportMarketMessage.getThirdMarketSourceId());
                    thirdSportMarketMessage1.setMarketType(thirdSportMarketMessage.getMarketType());
                    thirdSportMarketMessage1.setDataSourceCode(thirdSportMarketMessage.getDataSourceCode());
                    thirdSportMarketMessage1.setStatus(thirdSportMarketMessage.getStatus());
                    thirdSportMarketMessage1.setThirdMarketSourceStatus(thirdSportMarketMessage.getThirdMarketSourceStatus());
                    thirdSportMarketMessage1.setOddsTypeName(thirdSportMarketMessage.getOddsTypeName());
                    thirdSportMarketMessage1.setOddsValue(thirdSportMarketMessage.getOddsValue());
                    thirdSportMarketMessage1.setOddsName(thirdSportMarketMessage.getOddsName());
                    thirdSportMarketMessage1.setAddition1(thirdSportMarketMessage.getAddition1());
                    thirdSportMarketMessage1.setAddition2(thirdSportMarketMessage.getAddition2());
                    thirdSportMarketMessage1.setAddition3(thirdSportMarketMessage.getAddition3());
                    thirdSportMarketMessage1.setAddition4(thirdSportMarketMessage.getAddition4());
                    thirdSportMarketMessage1.setAddition5(thirdSportMarketMessage.getAddition5());
                    thirdSportMarketMessage1.setModifyTime(thirdSportMarketMessage.getModifyTime());
                    thirdSportMarketMessage1.setUpdateTime(now);
                    thirdSportMarketMessage1.setDbData(null);
                    thirdSportMarketMessage1.setOfferLineId(thirdSportMarketMessage.getOfferLineId());
                    List<ThirdSportMarketOdds> thirdSportMarketOddsList = thirdSportMarketMessage.getThirdSportMarketOddsList();
                    for (ThirdSportMarketOdds thirdSportMarketOdds : thirdSportMarketOddsList) {
                        thirdSportMarketOdds.setOddsId(thirdSportMarketOdds.getId());
                        thirdSportMarketOdds.setFieldOddsValue(OddsValueConvertUtils.convertAndDefaultDisply(null, Integer.valueOf(thirdSportMarketOdds.getOddsValue())));
                    }
                    thirdSportMarketMessage1.setThirdSportMarketOddsList(thirdSportMarketOddsList);
                }
            }

            ArrayList<ThirdSportMarketMessage> newobjects = new ArrayList<>();
            for (String key : sourceMarketIdMap.keySet()) {
                newobjects.add(sourceMarketIdMap.get(key));
            }
        }
    }

    /**
     * 根据数据源和玩法分组后进行坑位计算
     * @param marketList 下发的赔率
     * @param multipleOdds 数据库中有的赔率
     * @param linkid
     * @return 库赔率+下发的赔率坑位赋值后的数据
     */
    private List<ThirdSportMarketMessage> offerLineCreateGroup(List<ThirdSportMarketMessage> marketList,List<StandardTxThirdMarketPlayDTO> multipleOdds, String linkid){
        if (CollectionUtils.isEmpty(marketList)) {
            return marketList;
        }
        if (!marketList.get(0).getDataSourceCode().startsWith("LS")) {
            return marketList;
        }
        List<ThirdSportMarketMessage> newList = new ArrayList<>();
        //按照数据源分组,LS会一次下发bet365和1Xbet等赔率
        Map<String, List<ThirdSportMarketMessage>> groupByDataSource = marketList.stream().collect(Collectors.groupingBy(ThirdSportMarketMessage::getDataSourceCode));
        for(String dataSource: groupByDataSource.keySet()){
            //根据玩法分组
            List<ThirdSportMarketMessage> dataSourceList = groupByDataSource.get(dataSource);
            //如果没有坑位的数据源，那么每次保存都需要将所有数据重新排序后入口
            Map<Long, List<ThirdSportMarketMessage>> playMap = dataSourceList.stream().collect(Collectors.groupingBy(ThirdSportMarketMessage::getMarketCategoryId));
            for(Long playId: playMap.keySet()){
                List<ThirdSportMarketMessage> allMarkets = offerLineCreate(playMap.get(playId), multipleOdds, linkid);
                newList.addAll(allMarkets);
            }
        }
        return newList;
    }

    /**
     * 坑位赋值
     * LS数据源无坑位信息所以需要自己给值
     * @param marketList 下发的赔率
     * @param multipleOdds 库中存在的赔率
     * @return 返回全量带坑位的数
     */
    private List<ThirdSportMarketMessage> offerLineCreate(List<ThirdSportMarketMessage> marketList,List<StandardTxThirdMarketPlayDTO> multipleOdds, String linkid){
        if (CollectionUtils.isEmpty(marketList)) {
            return marketList;
        }
        if(marketList.get(0).getOfferLineId() != null){
            return marketList;
        }
        log.info("{}::百家赔坑位赋值", linkid);
        //将库中的数据和下发的数据一起排序（排查掉关盘的）
        List<ThirdSportMarketMessage> tempList = CollUtil.newCopyOnWriteArrayList(marketList);
        //关盘的不参与坑位排序
        tempList = tempList.stream().filter(item -> MarketStatusEnum.CLOSE.getState() != item.getStatus()).collect(Collectors.toList());
        List<String> closeMarktIds = tempList.stream()
                .filter(item -> MarketStatusEnum.CLOSE.getState() == item.getStatus())
                .map(ThirdSportMarketMessage::getRelationMarketId)
                .collect(Collectors.toList());
        //将数据库中的该玩法盘口也加进来
        if (!CollectionUtils.isEmpty(multipleOdds)) {
            List<String> marketIds = marketList.stream().map(ThirdSportMarketMessage::getRelationMarketId).collect(Collectors.toList());
            List<ThirdSportMarketMessage> dbList = new ArrayList<>();
            List<StandardTxThirdMarketPlayDTO> currDataSourceList = multipleOdds.stream()
                    .filter(item -> StrUtil.equals(item.getDataSourceCode(), marketList.get(0).getDataSourceCode()))
                    .collect(Collectors.toList());
            List<StandardTxThirdMarketPlayDTO> currPlayList = currDataSourceList.stream()
                    .filter(item -> item.getMarketCategoryId().equals( marketList.get(0).getMarketCategoryId()))
                    .collect(Collectors.toList());
            for (StandardTxThirdMarketPlayDTO item : currPlayList) {
                if (CollUtil.isEmpty(item.getMarketList())) {
                    continue;
                }

                for(ThirdSportMarketMessage market : item.getMarketList()){
                    //已经存在的不需要添加
                    if(marketIds.contains(market.getRelationMarketId())){
                        continue;
                    }
                    //关盘的不要添加
                    if(closeMarktIds.contains(market.getRelationMarketId())){
                        continue;
                    }
                    //MQ下发的投注项Id字段是Id，但是mongoDB中保存的是oddsId,所以从数据库查询出来id是空的
                    List<ThirdSportMarketOdds> oddsList = market.getThirdSportMarketOddsList();
                    if(CollUtil.isNotEmpty(oddsList)) {
                        for (ThirdSportMarketOdds odds : oddsList) {
                            if (odds.getId() == null && odds.getOddsId() != null) {
                                odds.setId(odds.getOddsId());
                            }
                        }
                    }
                    //这里一定要复制新的，防止后续有修改
                    ThirdSportMarketMessage copyBean = BeanCopyUtils.deepCopyProperties(market, ThirdSportMarketMessage.class);
                    dbList.add(copyBean);
                }
            }
            tempList.addAll(dbList);
        }
        //根据投注项赔率查的绝对值排序
        tempList.forEach(market -> {
            Integer oodsValue1 = market.getThirdSportMarketOddsList().get(0).getOriginalOddsValue();
            Integer oodsValue2 = market.getThirdSportMarketOddsList().get(1).getOriginalOddsValue();
            Integer oddsMetric = Math.abs(oodsValue1 - oodsValue2);
            market.setOddsMetric(Long.valueOf(oddsMetric));
        });
        ListUtils.sort(tempList, true, "thirdMarketSourceStatus", "oddsMetric", "oddsValue");
        //给offerLineId坑位赋值
        for (int i = 0; i < tempList.size(); i++) {
            tempList.get(i).setOfferLineId(i + 1);
        }
        return tempList;
        //给下发的数据坑位赋值
       /* for (ThirdSportMarketMessage item : marketList) {
            Optional<ThirdSportMarketMessage> sortItem = tempList.stream().filter(market-> StrUtil.equals(market.getRelationMarketId(),item.getRelationMarketId())).findFirst();
            if(!sortItem.isPresent()){
                continue;
            }
            item.setOfferLineId(sortItem.get().getOfferLineId());
        }*/
    }

    private Map<String, Float> transfer(Map<String,Object> obj2) {
        if (CollectionUtils.isEmpty(obj2)){return null;}
        Map<String, Float> map =new HashMap<>();
        for (String key : obj2.keySet()) {
            map.put(key,Float.valueOf(obj2.get(key).toString()));
        }
        return map;
    }


    /**
     * 修改权重调用
     * @param sportId
     * @param matchId
     * @param marketType
     * @param linkId
     */
    public void notifyModifyWeight(Long sportId, String matchId, String marketType, String linkId) {
        String weightTedisKey = String.format(REDIS_KEY_MULTI_ODDS_WEIGHT_VALUE,matchId,marketType);
        Map map1 = redisClient.hGetAllToObj(weightTedisKey);
        Map<String,Float> weightMap = transfer(map1);
        log.info("::{}::百家赔-权重值:{},","RDTMTCTG_"+linkId+"_"+matchId,matchId+"|"+marketType);

        //datasourcecode+marketid   body
        Map<String,ThirdSportMarketMessage> sourceMarketIdMap = new HashMap<>();
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("matchId").is(matchId);
        criteria.and("dataSourceCode").ne(MULTISOURCE);
        query.addCriteria(criteria);
        List<StandardTxThirdMarketPlayDTO> multipleOdds = mongoTemplate.find(query,StandardTxThirdMarketPlayDTO.class);
        log.info("::{}::百家赔-queryMongoResult:{}","RDTMTCTG_"+linkId+"_"+matchId,matchId);
        if(CollectionUtils.isEmpty(multipleOdds)){return;}
        List<Long> playIds = multipleOdds.stream().map(StandardTxThirdMarketPlayDTO::getMarketCategoryId).collect(Collectors.toList());

        //组合全量数据到ourceMarketIdMap 以盘口级别组合
        combineToMap(null, sourceMarketIdMap, multipleOdds,weightMap, sportId, matchId, linkId, 3);
        //log.info("::{}::百家赔-sourceMarketIdMap:{}",linkId,JsonFormatUtils.toJson(sourceMarketIdMap));

        //id OddsMap  datasourcecode+marketid   body   权重map   生成MULTISOURCE数据源盘口  生成成oddsIdsMap
        Map<String, Map<String, Float>> oddsIdsMap = new HashMap<>();
        Map<String, ThirdSportMarketMessage> weightSourceMarketIdMap = generateMarketMultiSourceData(oddsIdsMap, sourceMarketIdMap,weightMap, 3);

        if((!CollectionUtils.isEmpty(weightMap))&&weightMap.size()>1){
            //计算新赔率和预警
            Map<String, String> expreidOddsIdAndSource = getExpreidOddsIdAndSource(sourceMarketIdMap, multipleOdds.get(0).getMarketList().get(0).getMarketType(), matchId, linkId);
            calculateWeightAndWarnVal(weightMap, oddsIdsMap, expreidOddsIdAndSource);

            //百家赔入值
            inputVal(weightMap, oddsIdsMap, weightSourceMarketIdMap);
        }

        //保存
        StandardTxThirdMarketOddsDTO standardTxThirdMarketOddsDTO = new StandardTxThirdMarketOddsDTO();
        standardTxThirdMarketOddsDTO.setSportId(sportId);
        standardTxThirdMarketOddsDTO.setMatchType(multipleOdds.get(0).getMatchType());
        standardTxThirdMarketOddsDTO.setStandardMatchInfoId(String.valueOf(matchId));
        ArrayList<ThirdSportMarketMessage> newobjects = save(standardTxThirdMarketOddsDTO, sourceMarketIdMap, weightSourceMarketIdMap,"RDTMTCTG_"+linkId);

        if(CollectionUtils.isEmpty(weightMap)||weightMap.size()<2){
            log.info("::{}::百家赔-没有权重配置:{},{}","RDTMTCTG_"+linkId+"_"+matchId,matchId,0);
            return ;
        }
        if(CollectionUtils.isEmpty(newobjects)){
            log.info("::{}::百家赔-sendList-null{},{}","RDTMTCTG_"+linkId+"_"+matchId,matchId,JsonFormatUtils.toJson(newobjects));
            return ;
        }
        //发送
        sendToWS("RDTMTCTG_"+linkId, standardTxThirdMarketOddsDTO, playIds, newobjects,3);
    }

    /**
     * 过期玩法调用
     * @param s
     */
    public void multiOddsExpiredData(String s) {
        String linkId0 = UuidUtils.generateUuid();
        log.info("::{}::百家赔过期检测","MOED_"+linkId0);
        long now = System.currentTimeMillis();
        Map<String, MultiOddsExpiredBO> map = redisClient.hGetAllAverage2(MULTIPLE_ODDS_EXPIRED_DATA, MultiOddsExpiredBO.class);
        log.info("::{}::百家赔过期检测{}", "MOED_" + linkId0, now);
        if (CollectionUtils.isEmpty(map)) {
            return;
        }
        try {
            redissonManager.lock(LOCK_MULTISOURCE, 3 * 1000);
            List<MultiOddsExpiredBO> mutilOddsExpireds = mapToList(map);
            Map<String, List<MultiOddsExpiredBO>> collect = mutilOddsExpireds.stream().filter(e -> passPlayIdC.contains(e.getMarketCategoryId()) && now - e.getTime() > 5 * 60 * 1000 || passPlayIdA.contains(e.getMarketCategoryId()) && now - e.getTime() > 1 * 60 * 1000).collect(Collectors.groupingBy(e -> e.getMatchId()));
            for (String key0 : collect.keySet()) {
                List<MultiOddsExpiredBO> multiOddsExpiredBOS = collect.get(key0);
                List<Long> playIds = multiOddsExpiredBOS.stream().map(MultiOddsExpiredBO::getMarketCategoryId).collect(Collectors.toList());
                String matchId = multiOddsExpiredBOS.get(0).getMatchId();
                String linkId = linkId0+"|"+matchId;
                String weightTedisKey = String.format(REDIS_KEY_MULTI_ODDS_WEIGHT_VALUE,matchId,0);
                Map map1 = redisClient.hGetAllToObj(weightTedisKey);
                Map<String,Float> weightMap = transfer(map1);
                log.info("::{}::百家赔过期检测,{}","MOED_"+linkId+"_"+matchId,matchId+"|"+0);

                if(CollectionUtils.isEmpty(weightMap)||weightMap.size()<2){
                    log.info("::{}::百家赔-没有权重配置:{},{}","MOED_"+linkId+"_"+matchId,matchId,0);
                    continue;
                }

                //datasourcecode+marketid   body
                Map<String,ThirdSportMarketMessage> sourceMarketIdMap = new HashMap<>();
                Query query = new Query();
                Criteria criteria = new Criteria();
                criteria.and("matchId").is(matchId);
                criteria.and("marketCategoryId").in(playIds);
                criteria.and("dataSourceCode").ne(MULTISOURCE);
                query.addCriteria(criteria);
                List<StandardTxThirdMarketPlayDTO> multipleOdds = mongoTemplate.find(query,StandardTxThirdMarketPlayDTO.class);
                log.info("::{}::百家赔-queryMongoResult,{}","MOED_"+linkId+"_"+matchId,matchId);

                if(CollectionUtils.isEmpty(multipleOdds)){return;}
                //组合全量数据到ourceMarketIdMap 以盘口级别组合
                combineToMap(null, sourceMarketIdMap, multipleOdds,weightMap, multipleOdds.get(0).getSportId(), matchId, linkId, 2);
                //log.info("::{}::百家赔-sourceMarketIdMap:{},{}",linkId,JsonFormatUtils.toJson(sourceMarketIdMap),matchId);

                //id OddsMap  datasourcecode+marketid   body   权重map   生成MULTISOURCE数据源盘口  生成成oddsIdsMap
                Map<String, Map<String, Float>> oddsIdsMap = new HashMap<>();
                Map<String, ThirdSportMarketMessage> weightSourceMarketIdMap = generateMarketMultiSourceData(oddsIdsMap, sourceMarketIdMap,weightMap, 2);

                if((!CollectionUtils.isEmpty(weightMap))&&weightMap.size()>1){
                    //计算新赔率和预警
                    Map<String, String> expreidOddsIdAndSource = getExpreidOddsIdAndSource(sourceMarketIdMap, multipleOdds.get(0).getMarketList().get(0).getMarketType(),matchId,linkId);
                    calculateWeightAndWarnVal(weightMap, oddsIdsMap,expreidOddsIdAndSource);
                    //百家赔入值
                    inputVal(weightMap, oddsIdsMap, weightSourceMarketIdMap);
                }

                //save
                StandardTxThirdMarketOddsDTO standardTxThirdMarketOddsDTO = new StandardTxThirdMarketOddsDTO();
                standardTxThirdMarketOddsDTO.setSportId(multipleOdds.get(0).getSportId());
                standardTxThirdMarketOddsDTO.setMatchType(multipleOdds.get(0).getMatchType());
                standardTxThirdMarketOddsDTO.setStandardMatchInfoId(String.valueOf(matchId));
                ArrayList<ThirdSportMarketMessage> newobjects = save(standardTxThirdMarketOddsDTO, sourceMarketIdMap, weightSourceMarketIdMap,"MOED_"+linkId);

                if(CollectionUtils.isEmpty(weightMap)||weightMap.size()<2){
                    log.info("::{}::百家赔-没有权重配置:{},{}","MOED_"+linkId+"_"+matchId,matchId,0);
                    continue;
                }
                if(CollectionUtils.isEmpty(newobjects)){
                    log.info("::{}::百家赔-sendList-null,{}","MOED_"+linkId+"_"+matchId,matchId);
                    continue;
                }
                //发送
                sendToWS("MOED_"+linkId, standardTxThirdMarketOddsDTO, playIds, newobjects,2);
            }
        } catch (Exception e) {
            log.error("::{}::百家赔过期检测错误{},{}", "MOED_" + linkId0, e.getMessage(), e);
        } finally {
            redissonManager.unlock(LOCK_MULTISOURCE);
            for (String key : map.keySet()) {
                MultiOddsExpiredBO multiOddsExpiredBO = map.get(key);
                if(now-multiOddsExpiredBO.getTime()>15*60*1000){
                    redisClient.hashRemoveAverage(MULTIPLE_ODDS_EXPIRED_DATA,multiOddsExpiredBO.getDataSourceCode()+"_"+multiOddsExpiredBO.getMatchId()+"_"+multiOddsExpiredBO.getMarketCategoryId()+"_"+multiOddsExpiredBO.getRelationMarketId());
                }
            }
        }
    }

    /**
     * 无效超时权重计算
     * @param weightMap
     * @param oddsIdsMap
     * @param multiOddsExpiredBOS
     * @param sourceMarketIdMap
     */
    private void calculateExpiredWeightAndWarnVal(Map<String, Float> weightMap, Map<String, Map<String, Float>> oddsIdsMap, List<MultiOddsExpiredBO> multiOddsExpiredBOS, Map<String, ThirdSportMarketMessage> sourceMarketIdMap) {
        for (String oddsId : oddsIdsMap.keySet()) {
            int weightSize = 0;
            Float weightOdds = 0F;
            Float avgOdds = 0F;
            Float avgWeightValue = 0F;
            Map<String, Float> oddsMap = oddsIdsMap.get(oddsId);
            Map<String, Float> weightMap1 = BeanUtil.copyProperties(weightMap, HashMap.class);
            //删除过期的相应该的要计算权重
            for (String key : sourceMarketIdMap.keySet()) {
                ThirdSportMarketMessage thirdSportMarketMessage = sourceMarketIdMap.get(key);
                List<ThirdSportMarketOdds> thirdSportMarketOddsList = thirdSportMarketMessage.getThirdSportMarketOddsList();
                for (ThirdSportMarketOdds thirdSportMarketOdds : thirdSportMarketOddsList) {
                    if(thirdSportMarketOdds.getOddsId().equals(oddsId)){
                        for (MultiOddsExpiredBO multiOddsExpiredBO : multiOddsExpiredBOS) {
                            if(multiOddsExpiredBO.getDataSourceCode().equals(thirdSportMarketMessage.getDataSourceCode())&&
                                    multiOddsExpiredBO.getMarketCategoryId().equals(thirdSportMarketMessage.getMarketCategoryId())
                            ){
                                weightMap1.remove(multiOddsExpiredBO.getDataSourceCode());
                            }
                        }
                    }
                }
            }

            for (String source : weightMap1.keySet()) {
                if(source.equals("cautionValue")){continue;}
                Float odds = oddsMap.get(source);
                if(odds!=null&&odds.floatValue()!=0){
                    weightSize=weightSize+1;
                    avgOdds=avgOdds+odds;
                }
            }
            for (String source : weightMap1.keySet()) {
                if(source.equals("cautionValue")){continue;}
                Float odds = oddsMap.get(source);
                if(odds==null||odds.floatValue()==0){
                    Float divideWeightValue=0F;
                    if(0!=weightSize){
                        divideWeightValue = weightMap.get(source)/weightSize;
                    }
                    avgWeightValue=avgWeightValue+divideWeightValue;
                }
            }
            for (String source : oddsMap.keySet()) {
                if(null!=weightMap1.get(source)){
                    weightOdds = weightOdds+(oddsMap.get(source)*((weightMap1.get(source)+avgWeightValue)/100));
                }
            }
            Float warnRatio=0F;
            if(0!=weightSize){
                warnRatio = Math.abs(weightOdds-(avgOdds/weightSize))/(avgOdds);
            }
            oddsMap.put("warnRatio",warnRatio==null?0:warnRatio);
            oddsMap.put("weightOdds",weightOdds);
        }
    }

    /**
     * 早盘滚球切换清理百家赔数据
     * @param matchId
     * @param linkId
     */
    public void clearMultiOddsDataByoddsLive(Long matchId, String linkId) {
        log.info("::{}::百家赔-早盘滚球切换清理百家赔数据清理:{}",linkId+"_"+matchId,matchId);
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("matchId").is(String.valueOf(matchId));
        query.addCriteria(criteria);
        mongoTemplate.remove(query,StandardTxThirdMarketPlayDTO.class);
    }

    /**
     * 数据源是否交集
     * @param dataSources
     * @param weightMap
     * @return
     */
    private boolean compareDataSource(List<String> dataSources, Map<String, Float> weightMap) {
        if(CollectionUtils.isEmpty(dataSources)||CollectionUtils.isEmpty(weightMap)){return false;}
        for (String dataSource : dataSources) {
            for (String source : weightMap.keySet()) {
                if(dataSource.equals(source)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 转换mapToList
     * @param map
     * @param <E>
     * @return
     */
    private <E> List<E> mapToList(Map<String, E> map) {
        ArrayList<E> objects = new ArrayList<>();
        for (String key : map.keySet()) {
            objects.add(map.get(key));
        }
        return objects;
    }

    private void printLSLog(String mqDataSource, List<ThirdSportMarketMessage> newobjects,String linkId,String standardMatchInfoId, String step){
        if(CollUtil.isEmpty(newobjects)){
            return;
        }
        if (!mqDataSource.startsWith("LS")) {
            return;
        }
        //根据数据源和玩法分分组后在打印日志
        Map<String, List<ThirdSportMarketMessage>> t1 = newobjects.stream().collect(Collectors.groupingBy(ThirdSportMarketMessage::getDataSourceCode));
        for(String dataSource: t1.keySet()){
            //根据玩法分组
            List<ThirdSportMarketMessage> dataSourceList = t1.get(dataSource);
            Map<Long, List<ThirdSportMarketMessage>> playMap = dataSourceList.stream().collect(Collectors.groupingBy(ThirdSportMarketMessage::getMarketCategoryId));
            for(Long playId : playMap.keySet()){
                List<ThirdSportMarketMessage> list = playMap.get(playId);
                Map<String,Integer> map = new HashMap<>();
                for(ThirdSportMarketMessage item : list){
                    map.put(item.getAddition1(), item.getOfferLineId());
                }
                log.info("{}::百家赔LS::{}::{}::玩法::{}::数据源::{}::盘口和坑位::{}", linkId,
                        standardMatchInfoId, step, playId,dataSource, map);
            }
        }
    }


}