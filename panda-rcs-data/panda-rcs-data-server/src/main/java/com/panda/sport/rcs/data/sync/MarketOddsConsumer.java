package com.panda.sport.rcs.data.sync;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.common.NumberUtils;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.mq.RcsConsumer;
import com.panda.sport.rcs.data.mqSerializaBean.StandardMarketMessageDTO;
import com.panda.sport.rcs.data.mqSerializaBean.StandardMarketOddsMessageDTO;
import com.panda.sport.rcs.data.mqSerializaBean.StandardMatchMarketMessageDTO;
import com.panda.sport.rcs.data.service.CommonService;
import com.panda.sport.rcs.data.service.IStandardMatchInfoService;
import com.panda.sport.rcs.data.service.RcsFirstMarketService;
import com.panda.sport.rcs.data.service.RcsMarketChampionExtService;
import com.panda.sport.rcs.data.service.RcsStandardPlaceRefService;
import com.panda.sport.rcs.data.service.StandardSportMarketOddsService;
import com.panda.sport.rcs.data.service.StandardSportMarketService;
import com.panda.sport.rcs.data.utils.MarketUtils;
import com.panda.sport.rcs.data.utils.RDSProducerSendMessageUtils;
import com.panda.sport.rcs.data.utils.RcsDataRedis;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.enums.TradeLevelEnum;
import com.panda.sport.rcs.enums.TradeStatusEnum;
import com.panda.sport.rcs.pojo.RcsFirstMarket;
import com.panda.sport.rcs.pojo.RcsMarketChampionExt;
import com.panda.sport.rcs.pojo.RcsStandardPlaceRef;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.dto.ClearDTO;
import com.panda.sport.rcs.pojo.dto.ClearSubDTO;
import com.panda.sport.rcs.utils.CollectionUtil;
import com.panda.sport.rcs.utils.NameExpressionValueUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 赔率入库-mysql
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic ="STANDARD_MARKET_ODDS",
        consumerGroup = "RCS_DATA_STANDARD_MARKET_ODDS_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MarketOddsConsumer extends RcsConsumer<Request<StandardMatchMarketMessageDTO>> {

    @Autowired
    StandardSportMarketService standardSportMarketService;
    @Autowired
    IStandardMatchInfoService standardMatchInfoService;
    @Autowired
    StandardSportMarketOddsService standardSportMarketOddsService;
    @Autowired
    RcsStandardPlaceRefService rcsStandardPlaceRefService;
    @Autowired
    RcsMarketChampionExtService rcsMarketChampionExtService;
    @Autowired
    CommonService commonService;
    @Autowired
    private RcsDataRedis redisClient;
    @Autowired
    private RcsFirstMarketService rcsFirstMarketService;
    @Autowired
    protected RDSProducerSendMessageUtils sendMessage;

    public static final String PLACE_REF_COMPARE = "placeRefCompare";
    public static final String MARKET_COMPARE = "dataMarketCompare";
    public static final String MATCH_TEMP_INFO = "matchTempInfo";
    public static final String MARKET_CHAMPION_EXTS_COMPARE = "dataMarketChampionExtsCompare";
    public static final String MARKET_ODDS_COMPARE = "dataMarketOddsCompare";
    public static final String TEMPLATE = "categoryOddTemplate";
    public static final String FIRST_MARKET = "firstMarket";
    private static final String RCS_DATA_KEY_CACHE_KEY = RedisKeys.RCS_DATA_KEY_CACHE_KEY;
    /**
     * 赛事是否有赔率缓存key
     */
    public static final String HAS_ODDS_CACHE_KEY = "has:odds:match:";
    /**
     * 赛事是否有统计事件下发缓存key
     */
    public static final String HAS_STATISTICS_CACHE_KEY = "has:statistics:match:";

    @Override
    protected String getTopic() {
        return "STANDARD_MARKET_ODDS";
    }

    /**
     * @Description: 实时盘口赔率变化通知
     * @Author: V
     * @Date: 2019/12/12
     **/
    @Override
    public Boolean handleMs(Request<StandardMatchMarketMessageDTO> msg) {
        log.info("::{}::datasync-接收盘口赔率","RDSMOG_"+msg.getLinkId());
        try {
            if (StringUtils.isNotEmpty(msg.getDataType())&&msg.getDataType().equalsIgnoreCase("HeartBeat"))
            {
                return true;
            }
            StandardMatchMarketMessageDTO data = msg.getData();
            if (data == null || data.getStandardMatchInfoId() == null) {return true;}
            long currentTime = System.currentTimeMillis();

            //1.更新赛事比赛开盘标识
            //if(data.getStatus() != null) standardMatchInfoService.updateOperateMatchStatus(data);
            List<StandardMarketMessageDTO> marketList = data.getMarketList();
            if (CollectionUtils.isEmpty(marketList)) {return true;}
            Map<Long, List<StandardMarketMessageDTO>> groupMarketMap = marketList.stream().collect(Collectors.groupingBy(bean -> bean.getMarketCategoryId()));
            Iterator<Map.Entry<Long, List<StandardMarketMessageDTO>>> iterator = groupMarketMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, List<StandardMarketMessageDTO>> next = iterator.next();
                Long playId = next.getKey();
                List<StandardMarketMessageDTO> values = next.getValue();
                //玩法批处理
                catagoryBatch(msg, data, currentTime, values,playId,0);
            }
            //发送清理概率差
            sendClearData(msg.getLinkId(),data.getMatchType(),data.getStandardMatchInfoId(),data.getSportId(),groupMarketMap);
            if(data.getSportId()==2){
                countFirstMarkt(marketList,data.getStandardMatchInfoId(),data.getMatchType(),msg);
            }
            //缓存赛事是否有赔率 bug-44113
            String oddsCacheKey = String.format(RCS_DATA_KEY_CACHE_KEY, HAS_ODDS_CACHE_KEY, data.getStandardMatchInfoId());
            if(!redisClient.exist(oddsCacheKey)){
                electronMatchOpen(data, msg.getLinkId());
            }
            boolean setHasKey = true;
            //bug-45174 o01赛事收到赔率后判断赛事是否是滚球阶段，防止先收到滚球赔率后再收到滚球阶段MQ
            if(!redisClient.exist(oddsCacheKey)
                    && ("OD".equals(data.getDataSourceCode()) || "BE".equals(data.getDataSourceCode()))){
                StandardMatchInfo matchInfo = standardMatchInfoService.getById(data.getStandardMatchInfoId());
                if(!RcsConstant.LIVE_MATCH_STATUS.contains(matchInfo.getMatchStatus())){
                    log.info("{}::电子赛事自动开盘::{}::赛事开盘::赛事非滚球状态::{}", msg.getLinkId(), data.getStandardMatchInfoId(), matchInfo.getMatchStatus());
                    setHasKey = false;
                }
            }
            if(setHasKey) {
                redisClient.setExpiry(oddsCacheKey, currentTime, 24 * 60 * 60L);
            }
        } catch (Exception e) {
            log.error("::{}::datasync-接收盘口赔率错误{},{},{}" ,"RDSMOG_"+msg.getLinkId(),JsonFormatUtils.toJson(msg), e.getMessage(), e);
        }
        return true;
    }

    /**
     * 电子赛事第一次收到赔率后判断是否有事件自动开盘
     * @param data MQ下发的赔率
     * @param linkid
     */
    private void electronMatchOpen(StandardMatchMarketMessageDTO data, String linkid){
        String dataSourceCode = data.getDataSourceCode();
        if(!("OD".equals(dataSourceCode) || "BE".equals(dataSourceCode))){
            return;
        }
        //如果是早盘，收到赔率后立即开盘,如果是滚球那么足球要判断是否有事件，篮球要判断是否有统计信息
        if(data.getMarketList().get(0).getMarketType() != 0){
            return;
        }
        boolean openflag = false;

        String statisticsCacheKey = String.format(RCS_DATA_KEY_CACHE_KEY, HAS_STATISTICS_CACHE_KEY, data.getStandardMatchInfoId());
        log.info("key={},value={}",statisticsCacheKey,redisClient.get(statisticsCacheKey));
        openflag = redisClient.exist(statisticsCacheKey);
        log.info("statisticsCacheKey={},exist={}",statisticsCacheKey,openflag);
        if(!openflag){
            String eventCacheKey = String.format(RCS_DATA_KEY_CACHE_KEY, MatchEventInfoConsumer.EVENT_CACHE_KEY, data.getStandardMatchInfoId());
            log.info("key={},value={}",eventCacheKey,redisClient.get(eventCacheKey));
            openflag = redisClient.exist(eventCacheKey);
            log.info("eventCacheKey={},exist={}",eventCacheKey,openflag);
        }
        if(!openflag){
            log.info("{}::电子赛事自动开盘::{}::赛事开盘::没有事件或统计信息不开盘", linkid, data.getStandardMatchInfoId());
            return;
        }
        log.info("{}::电子赛事自动开盘::{}::赛事开盘", linkid, data.getStandardMatchInfoId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tradeLevel", TradeLevelEnum.MATCH.getLevel());
        jsonObject.put("sportId", data.getSportId());
        jsonObject.put("matchId", data.getStandardMatchInfoId());
        jsonObject.put("status", TradeStatusEnum.OPEN.getStatus());
        jsonObject.put("remark", "电子赛事收到赔率，赛事开盘");
        jsonObject.put("linkedType", "68");
        Request<JSONObject> requestDTO = new Request<>();
        requestDTO.setData(jsonObject);
        requestDTO.setLinkId(MarketUtils.getLinkId("_PERIOD_06_BE_OPEN"));
        requestDTO.setDataSourceTime(System.currentTimeMillis());
        sendMessage.sendMessage("RCS_TRADE_UPDATE_MARKET_STATUS", data.getStandardMatchInfoId() + "_ELECTRON_OPEN", requestDTO.getLinkId(), requestDTO);

    }

    /**
     * 玩法批处理
     * @param msg
     * @param data
     * @param currentTime
     * @param marketGroupList
     * @param playId
     * @param reCallFalg  0:没有重新调用  1:再次调用
     */
    private void catagoryBatch(Request<StandardMatchMarketMessageDTO> msg, StandardMatchMarketMessageDTO data, long currentTime, List<StandardMarketMessageDTO> marketGroupList, Long playId, Integer reCallFalg) {
        try {
            if (reCallFalg > 1) {
                return;
            }
            List<RcsStandardPlaceRef> rcsStandardPlaceRefs = new ArrayList<>();
            List<StandardMarketMessageDTO> standardSportMarkets = new ArrayList<>();
            List<StandardMarketOddsMessageDTO> listStandardSportMarketOdds = new ArrayList<>();
            List<RcsMarketChampionExt> rcsMarketChampionExts = new ArrayList<>();
            //List<Map> categoryOddTemplates = new ArrayList<>();
            //boolean isSuccess = checkPlaceRefsValid(data.getStandardMatchInfoId(),playId, msg.getDataSourceTime(),reCallFalg);

            //构建buildPlaceRefs数据
            //if(isSuccess) {
            buildPlaceRefs(data.getStandardMatchInfoId(), playId, msg.getDataSourceTime(), data, rcsStandardPlaceRefs, marketGroupList, reCallFalg,msg.getLinkId());
            //}
            Integer thirdStatus = -1;
            for (StandardMarketMessageDTO marketDTO : marketGroupList) {
                if (thirdStatus != null && thirdStatus == -1) {
                    thirdStatus = handleMarketStatus(msg.getLinkId(), data.getStandardMatchInfoId(), marketDTO);
                }
                if (thirdStatus != null && thirdStatus != -1) {
                    marketDTO.setThirdMarketSourceStatus(thirdStatus);
                }
                //构建market数据
                buildMarket(msg, data, currentTime, standardSportMarkets, marketDTO, reCallFalg,msg.getLinkId());
                //获取当前盘口下的投注项数据
                buildMarketOdds(msg, currentTime, listStandardSportMarketOdds, marketDTO, reCallFalg,msg.getLinkId());
                //构建冠军盘扩展表
                buildMarketChampionExts(msg, rcsMarketChampionExts, marketDTO, reCallFalg,msg.getLinkId());
            }

            //持久化投注项数据
            standardSportMarketService.batchInsertOrUpdate(standardSportMarkets);
            CollectionUtil.removeDuplicate(listStandardSportMarketOdds);
            standardSportMarketOddsService.batchSaveOrUpdate(listStandardSportMarketOdds);
            rcsMarketChampionExtService.batchInsertOrUpdate(rcsMarketChampionExts);
            //关联数据放到最后，防止查不到盘口
            rcsStandardPlaceRefService.batchInsertOrUpdate(rcsStandardPlaceRefs);
            //commonService.insertRcsCategoryOddTemplet(categoryOddTemplates);
        }catch (Exception e) {
            ++reCallFalg;
            log.error("::{}::datasync-接收盘口赔率错误{},{},{},{}","RDSMOG_"+msg.getLinkId(),"reCallFalg is:"+reCallFalg , JSON.toJSON(marketGroupList).toString(),e.getMessage(),e);
            catagoryBatch(msg, data, currentTime, marketGroupList,playId,reCallFalg);
        }
    }

    /**
     * 构建冠军盘口扩展表
     * @param msg
     * @param rcsMarketChampionExts
     * @param marketDTO
     * @param reCallFalg
     * @param linkId
     */
    private boolean buildMarketChampionExts(Request<StandardMatchMarketMessageDTO> msg, List<RcsMarketChampionExt> rcsMarketChampionExts, StandardMarketMessageDTO marketDTO, Integer reCallFalg, String linkId) {
        if(msg.getData().getMatchType()!=1) {return true;}
        //检测盘口数据是否过时，或者是否已经存入过
        boolean isMarketValid = checkMarketChampionExtsValid(marketDTO, msg.getDataSourceTime(),reCallFalg,msg);

        if (!isMarketValid) {
            log.warn("::{}::盘口ID:{} 不存冠军盘口扩展表里","RDSMOG_"+linkId+"_"+msg.getData().getStandardMatchInfoId(),marketDTO.getId());
            return true;
        }
        //数据转换
        if (marketDTO.getId() != null) {
            RcsMarketChampionExt rcsMarketChampionExt = new RcsMarketChampionExt();
            rcsMarketChampionExt.setMarketCategoryId(marketDTO.getMarketCategoryId());
            rcsMarketChampionExt.setStandardMatchInfoId(msg.getData().getStandardMatchInfoId());
            rcsMarketChampionExt.setMarketId(marketDTO.getId());
            rcsMarketChampionExt.setSportId(msg.getData().getSportId());
            rcsMarketChampionExt.setNextSealTime(marketDTO.getAddition1());
            rcsMarketChampionExts.add(rcsMarketChampionExt);
        }
        return true;

    }

    private boolean checkMarketChampionExtsValid(StandardMarketMessageDTO bean, Long dataSourceTime, Integer reCallFalg, Request<StandardMatchMarketMessageDTO> msg) {
        try {
            if(null==bean.getId() || null==bean.getStatus()){ return false;}
            StringBuilder marketSb = new StringBuilder("")
                    .append(bean.getAddition1());
            String marketMd5 = DigestUtils.md5DigestAsHex(marketSb.toString().getBytes());
            String oMd5Str = redisClient.get(String.format(RCS_DATA_KEY_CACHE_KEY, MARKET_CHAMPION_EXTS_COMPARE, bean.getId()));
            redisClient.setExpiry(String.format(RCS_DATA_KEY_CACHE_KEY, MARKET_CHAMPION_EXTS_COMPARE, bean.getId()),dataSourceTime + "-" + marketMd5, 120L);
            if (StringUtils.isNotBlank(oMd5Str)) {
                String[] split = oMd5Str.split("-");
                String oTime = split[0];
                String oMd5 = split[1];
                if(!(NumberUtils.isNumber(oTime)&&Long.valueOf(oTime).equals(dataSourceTime)&&1==reCallFalg)) {
                    if (NumberUtils.isNumber(oTime) && Long.valueOf(oTime) > dataSourceTime) {
                        return false;
                    }
                    if (marketMd5.equals(oMd5)) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            log.error("::{}::{},{},{}","RDSMOG_"+msg.getLinkId(),JsonFormatUtils.toJson(bean),e.getMessage(),e);
        }
        return true;

    }


    /**
     * 发送清理概率差
     * @param linkId
     * @param matchType
     * @param standardMatchInfoId
     * @param sportId
     * @param marketGroupList marketSource清理
     */
    private void sendClearData(String linkId, Integer matchType, Long standardMatchInfoId, Long sportId, Map<Long, List<StandardMarketMessageDTO>> marketGroupList) {
        try {
            List<ClearSubDTO> rcsMatchMarketConfigs = new ArrayList<>();
            ArrayList<ClearSubDTO> rcsMatchMarketConfigs1 = new ArrayList<>();
            //marketSource对比清理水差
            if (MapUtils.isNotEmpty(marketGroupList)) {
                log.info("::{}::sendClearData2","RDSMOG_"+linkId+"_"+standardMatchInfoId);
                Iterator<Map.Entry<Long, List<StandardMarketMessageDTO>>> iterator = marketGroupList.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Long, List<StandardMarketMessageDTO>> next = iterator.next();
                    Long playId = next.getKey();
                    List<StandardMarketMessageDTO> values = next.getValue();
                    for (StandardMarketMessageDTO standardMarketMessageDTO : values) {
                        //构建清理概率差
                        buildClearData(rcsMatchMarketConfigs,standardMarketMessageDTO,standardMatchInfoId,linkId);
                        if (0 == standardMarketMessageDTO.getMarketType()) {
                            ClearSubDTO rcsMatchMarketConfig = new ClearSubDTO();
                            String key = String.format(RCS_DATA_KEY_CACHE_KEY, MATCH_TEMP_INFO, standardMatchInfoId);
                            String hKey = String.valueOf(standardMarketMessageDTO.getMarketCategoryId());
                            //构建赔率转滚球 marketSource
                            String marketSource = redisClient.hGet(key, hKey);
                            if (StringUtils.isBlank(marketSource)) {
                                rcsMatchMarketConfig.setPlayId(playId);
                                rcsMatchMarketConfig.setMatchId(standardMatchInfoId);
                                rcsMatchMarketConfigs1.add(rcsMatchMarketConfig);
                            } else {
                                if (!marketSource.equals(String.valueOf(standardMarketMessageDTO.getMarketSource()))) {
                                    rcsMatchMarketConfig.setPlayId(playId);
                                    rcsMatchMarketConfig.setMatchId(standardMatchInfoId);
                                    rcsMatchMarketConfigs1.add(rcsMatchMarketConfig);
                                }
                            }
                            if (null != standardMarketMessageDTO.getMarketSource()) {
                                redisClient.hSet(key, hKey, String.valueOf(standardMarketMessageDTO.getMarketSource()));
                            }
                            redisClient.expireKey(key, 2 * 24 * 60 * 60);
                        }
                    }
                }
            }
            //上游清理标识
            if (!CollectionUtils.isEmpty(rcsMatchMarketConfigs)) {
                log.info("::{}::sendClearData1","RDSMOG_"+linkId+"_"+standardMatchInfoId);
                com.panda.sport.rcs.utils.CollectionUtil.removeDuplicate(rcsMatchMarketConfigs);
                if (1 == matchType) {
                    //冠军士赛事
                    ClearDTO clearDTO = new ClearDTO();
                    clearDTO.setSportId(sportId);
                    clearDTO.setType(1);
                    clearDTO.setClearType(1);
                    clearDTO.setMatchId(standardMatchInfoId);
                    clearDTO.setList(rcsMatchMarketConfigs);
                    clearDTO.setGlobalId(linkId);
                    sendMessage.sendMessage("RCS_CLEAR_CHAMPION_MARKET", null, linkId, clearDTO, null);
                } else {
                    //普通赛事
                    ClearDTO clearDTO = new ClearDTO();
                    clearDTO.setSportId(sportId);
                    clearDTO.setType(0);
                    clearDTO.setClearType(1);
                    clearDTO.setMatchId(standardMatchInfoId);
                    clearDTO.setList(rcsMatchMarketConfigs);
                    clearDTO.setGlobalId(linkId);
                    sendMessage.sendMessage("RCS_CLEAR_MATCH_MARKET_TAG", null, linkId, clearDTO, null);
                }
            }
            //marketSource对比清理水差
            com.panda.sport.rcs.utils.CollectionUtil.removeDuplicate(rcsMatchMarketConfigs1);
            if (!CollectionUtils.isEmpty(rcsMatchMarketConfigs1)) {
                ClearDTO clearDTO = new ClearDTO();
                clearDTO.setSportId(sportId);
                clearDTO.setType(0);
                clearDTO.setClearType(15);
                clearDTO.setMatchId(standardMatchInfoId);
                clearDTO.setList(rcsMatchMarketConfigs1);
                clearDTO.setGlobalId(linkId);
                sendMessage.sendMessage("RCS_CLEAR_MATCH_MARKET_TAG", null, linkId, clearDTO, null);
            }
        } catch (Exception e) {
            log.error("::{}::,{},{},{}","RDSMOG_"+linkId,JsonFormatUtils.toJson(marketGroupList),e.getMessage(), e);
        }
    }


    /**
     * 构建清理概率差
     * @param rcsMatchMarketConfigs
     * @param marketDTO
     * @param standardMatchInfoId
     * @param linkId
     */
    private void buildClearData(List<ClearSubDTO> rcsMatchMarketConfigs, StandardMarketMessageDTO marketDTO, Long standardMatchInfoId, String linkId) {
        try {
            if(null==marketDTO||CollectionUtils.isEmpty(marketDTO.getMarketOddsList())){
                return;
            }
            List<StandardMarketOddsMessageDTO> marketOddsList = marketDTO.getMarketOddsList();
            for (StandardMarketOddsMessageDTO standardMarketOddsMessageDTO : marketOddsList) {
                if(1==standardMarketOddsMessageDTO.getClearProbability()){
                    ClearSubDTO rcsMatchMarketConfig = new ClearSubDTO();
                    rcsMatchMarketConfig.setMatchId(standardMatchInfoId);
                    rcsMatchMarketConfig.setMarketId(marketDTO.getId());
                    rcsMatchMarketConfig.setMarketOddsId(standardMarketOddsMessageDTO.getId());
                    rcsMatchMarketConfig.setOddsType(standardMarketOddsMessageDTO.getOddsType());
                    rcsMatchMarketConfig.setPlayId(marketDTO.getMarketCategoryId());
                    rcsMatchMarketConfig.setSubPlayId(String.valueOf(marketDTO.getChildMarketCategoryId()));
                    rcsMatchMarketConfigs.add(rcsMatchMarketConfig);
                }
            }
        }catch (Exception e){
            log.error("::{}::{},{},{}","RDSMOG_"+linkId,JsonFormatUtils.toJson(marketDTO),e.getMessage(),e);
        }
    }

    /**
     * 处理盘口数据
    * @Title: handleMarketInfo 
    * @Description: TODO 
    * @param @param linkId
    * @param @param standardMatchInfoId
    * @param @param marketDTO    设定文件 
    * @return void    返回类型 
    * @throws
     */
    private Integer handleMarketStatus(String linkId, Long standardMatchInfoId, StandardMarketMessageDTO marketDTO) {
    	try {
    		if(linkId != null && linkId.endsWith("super_one")) {//重置三方数据源盘口字段
        		String key = String.format("rcs:dataserver:model:change:super:a:%s", standardMatchInfoId);
        		String val = redisClient.get(key);
        		if(StringUtils.isBlank(val)) {
        			return null;
        		}
        		
        		if(linkId.equals(val.split(";")[0])) {
        			return Integer.parseInt(val.split(";")[1]);
        		}
        		
        	}
    	}catch (Exception e) {
    		log.error("::{}::{},{},{}","RDSMOG_"+linkId,standardMatchInfoId,e.getMessage(),e);
    	}
    	return null;
	}

    /**
     * 校验当前玩法数据是否需要更新
    * @Title: checkPlaceRefsValid
    * @Description: TODO
    * @param @param standardMatchInfoId
    * @param @param playId
    * @param @param dataSourceTime
    * @param @return    设定文件
    * @param reCallFalg
     * @return boolean    返回类型
    * @throws
     */
/*    private boolean checkPlaceRefsValid(Long standardMatchInfoId, Long playId, Long dataSourceTime, Integer reCallFalg) {
    	try {
            if(standardMatchInfoId == null || playId == null ){
            	return false;
            }
            String key = String.format("oddsKey:%s_%s", standardMatchInfoId,playId);
            String oMd5Str = redisClient.get(String.format(RCS_DATA_KEY_CACHE_KEY, PLACE_REF_COMPARE, key));
            if (StringUtils.isBlank(oMd5Str)) {
            	oMd5Str = "0";
            }
            if(Long.parseLong(oMd5Str) < dataSourceTime) {
            	redisClient.setExpiry(String.format(RCS_DATA_KEY_CACHE_KEY, PLACE_REF_COMPARE, key), dataSourceTime , 120L);
            	return true;
            }

        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return false;
	}*/

	/**
     * 模版入库 构建
     * @param categoryOddTemplate
     * @param marketDTO
     */
    private void buildAndSaveRedisCategoryOddTemplet(List<Map> categoryOddTemplate, StandardMarketMessageDTO marketDTO) {
        String key = String.format(RCS_DATA_KEY_CACHE_KEY, TEMPLATE,marketDTO.getMarketCategoryId());
        List strings = redisClient.getObj2(key, List.class);
        if(null == strings){strings = new ArrayList<>();}
        List<StandardMarketOddsMessageDTO> marketOddsList = marketDTO.getMarketOddsList();
        if(CollectionUtils.isEmpty(marketOddsList)){return;}
        for (StandardMarketOddsMessageDTO listStandardSportMarketOdd : marketOddsList) {
            if(!strings.contains(listStandardSportMarketOdd.getOddsType())){
                strings.add(listStandardSportMarketOdd.getOddsType());
                HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
                objectObjectHashMap.put("category",marketDTO.getMarketCategoryId());
                objectObjectHashMap.put("oddType", listStandardSportMarketOdd.getOddsType());
                categoryOddTemplate.add(objectObjectHashMap);
            }
        }
        redisClient.set(key,strings);
    }

    private void buildMarketOdds(Request<StandardMatchMarketMessageDTO> msg, long currentTime, List<StandardMarketOddsMessageDTO> listStandardSportMarketOdds, StandardMarketMessageDTO marketDTO, Integer reCallFalg, String linkId) {
        List<StandardMarketOddsMessageDTO> marketOddsDTOList = marketDTO.getMarketOddsList();
        if (!CollectionUtils.isEmpty(marketOddsDTOList)) {
            for (StandardMarketOddsMessageDTO standardSportMarketOddsDTO : marketOddsDTOList) {
                //检测赔率数据是否过时，或者是否已经存入过
                boolean isOddsValid = checkMarketOddsDataValid(standardSportMarketOddsDTO, msg.getDataSourceTime(),reCallFalg,msg);
                if (!isOddsValid) {
                    log.warn("::{}::盘口赔率ID:{} 不存盘口赔率表里","RDSMOG_"+linkId+"_"+msg.getData().getStandardMatchInfoId(),standardSportMarketOddsDTO.getId());
                    continue;
                }
                // 4.投注项数据转换
                standardSportMarketOddsDTO.setOddsFieldsTempletId(standardSportMarketOddsDTO.getOddsFieldsTemplateId());
                standardSportMarketOddsDTO.setI18nNamesStr(JsonFormatUtils.toJson(standardSportMarketOddsDTO.getI18nNames()));
                standardSportMarketOddsDTO.setI18nNames(null);
                standardSportMarketOddsDTO.setPreviousOddsValue(standardSportMarketOddsDTO.getOddsValue());
                standardSportMarketOddsDTO.setOddsValue(standardSportMarketOddsDTO.getPaOddsValue());
                standardSportMarketOddsDTO.setCreateTime(currentTime);
                String nameExpressionValue = NameExpressionValueUtils.getNameExpressionValue(marketDTO.getMarketCategoryId().intValue(), standardSportMarketOddsDTO.getOddsType(), marketDTO.getAddition1());
                standardSportMarketOddsDTO.setNameExpressionValue(nameExpressionValue);
                listStandardSportMarketOdds.add(standardSportMarketOddsDTO);
            }
        }
    }

    private boolean buildMarket(Request<StandardMatchMarketMessageDTO> msg, StandardMatchMarketMessageDTO data, long currentTime, List<StandardMarketMessageDTO> standardSportMarkets, StandardMarketMessageDTO marketDTO, Integer reCallFalg, String linkId) {
        //检测盘口数据是否过时，或者是否已经存入过
        boolean isMarketValid = checkMarketDataValid(marketDTO, msg.getDataSourceTime(),reCallFalg,msg);
        if (!isMarketValid) {
            log.warn("::{}::盘口ID:{} 不存盘口表里","RDSMOG_"+linkId+"_"+msg.getData().getStandardMatchInfoId(),marketDTO.getId());
            return true;
        }
        //2.盘口数据转换
        if (marketDTO.getId() != null) {
            if(CollectionUtils.isNotEmpty(marketDTO.getI18nNames())){
                marketDTO.setI18nNamesStr(JsonFormatUtils.toJson(marketDTO.getI18nNames()));
            }
            marketDTO.setI18nNames(null);
            marketDTO.setCreateTime(currentTime);
            marketDTO.setStandardMatchInfoId(data.getStandardMatchInfoId());
            marketDTO.setOddsMetric(marketDTO.getPlaceNum());
            if(marketDTO.getChildMarketCategoryId()==null){marketDTO.setChildMarketCategoryId(-1L);}
            standardSportMarkets.add(marketDTO);
        }
        return true;
    }

    private boolean buildPlaceRefs(Long standardMatchInfoId, Long playId, Long dataSourceTime, StandardMatchMarketMessageDTO data, List<RcsStandardPlaceRef> rcsStandardPlaceRefs, List<StandardMarketMessageDTO> marketGroupList, Integer reCallFalg, String linkId) {
        //检测盘口数据是否过时，或者是否已经存入过
        boolean isMarketValid = checkPlaceRefsDataValid(standardMatchInfoId, playId, dataSourceTime, marketGroupList, reCallFalg);
        if (!isMarketValid) {
            log.warn("::{}::PlaceID:{} 不存位置表里","RDSMOG_"+linkId+"_"+standardMatchInfoId, standardMatchInfoId+"_"+playId);
            return true;
        }
        for (StandardMarketMessageDTO marketDTO : marketGroupList) {
            if (StringUtils.isBlank(marketDTO.getPlaceNumId())||null==marketDTO.getPlaceNum()) {
                continue;
            }
            RcsStandardPlaceRef rcsStandardPlaceRef = new RcsStandardPlaceRef();
            rcsStandardPlaceRef.setPlaceId(marketDTO.getPlaceNumId());
            rcsStandardPlaceRef.setStandardMatchInfoId(data.getStandardMatchInfoId());
            rcsStandardPlaceRef.setMarketId(marketDTO.getId());
            rcsStandardPlaceRef.setMarketCategoryId(marketDTO.getMarketCategoryId());
            rcsStandardPlaceRef.setPlaceNum(marketDTO.getPlaceNum());
            rcsStandardPlaceRef.setVersionId(String.valueOf(dataSourceTime));
            rcsStandardPlaceRef.setChildMarketCategoryId(marketDTO.getChildMarketCategoryId()==null?"-1":marketDTO.getChildMarketCategoryId().toString());
            rcsStandardPlaceRefs.add(rcsStandardPlaceRef);
        }
        return true;
    }

    private boolean checkPlaceRefsDataValid(Long standardMatchInfoId, Long playId, Long dataSourceTime, List<StandardMarketMessageDTO> marketGroupList, Integer reCallFalg) {
        if (standardMatchInfoId == null || playId == null || CollectionUtils.isEmpty(marketGroupList)) {
            return false;
        }
        List<StandardMarketMessageDTO> newMarketGroupList = marketGroupList.stream().filter(a -> StringUtils.isNotBlank(a.getPlaceNumId())&&null!=a.getPlaceNum()).sorted((a, b) -> a.getPlaceNum().compareTo(b.getPlaceNum())).collect(Collectors.toList());
        StringBuilder marketSb = new StringBuilder("");
        for (StandardMarketMessageDTO bean : newMarketGroupList) {
            if (null == bean.getId() || null == bean.getStatus()) {
                continue;
            }
            marketSb.append(bean.getPlaceNumId())
                    .append(bean.getId())
                    .append(bean.getMarketCategoryId())
                    .append(bean.getPlaceNum())
                    .append(bean.getStandardMatchInfoId());
        }
        String newMd5 = DigestUtils.md5DigestAsHex(marketSb.toString().getBytes());
        String oMd5Str = redisClient.get(String.format(RCS_DATA_KEY_CACHE_KEY, PLACE_REF_COMPARE, standardMatchInfoId+"_"+playId));
        if (StringUtils.isNotBlank(oMd5Str)) {
            String[] split = oMd5Str.split("-");
            String oTime = split[0];
            String oMd5 = split[1];
            if (!(NumberUtils.isNumber(oTime) && Long.valueOf(oTime).equals(dataSourceTime) && 1 == reCallFalg)) {
                if (NumberUtils.isNumber(oTime) && Long.valueOf(oTime) > dataSourceTime) {
                    return false;
                }
                if (newMd5.equals(oMd5)) {
                    return false;
                }
            }
        }
        redisClient.setExpiry(String.format(RCS_DATA_KEY_CACHE_KEY, PLACE_REF_COMPARE, standardMatchInfoId+"_"+playId), dataSourceTime + "-" + newMd5, 120L);
        return true;
    }

    /**
     * @Description: 检查赛事盘口数据有效性
     * @Date: 2019/12/12
     **/
    private boolean checkMarketDataValid(StandardMarketMessageDTO bean, Long dataSourceTime, Integer reCallFalg, Request<StandardMatchMarketMessageDTO> msg) {
        try {
            if(null==bean.getId() || null==bean.getStatus()){ return false;}
            StringBuilder marketSb = new StringBuilder("")
                    .append(bean.getId())
                    .append(bean.getMarketCategoryId())
                    .append(bean.getMarketType())
                    .append(bean.getOddsMetric())
                    .append(bean.getAddition1())
                    .append(bean.getAddition2())
                    .append(bean.getAddition3())
                    .append(bean.getAddition4())
                    .append(bean.getAddition5())
                    .append(bean.getDataSourceCode())
                    .append(bean.getStatus())
                    .append(bean.getScopeId())
                    .append(bean.getThirdMarketSourceId())
                    .append(bean.getExtraInfo())
                    .append(bean.getMarketHeadGap())
                    .append(bean.getThirdMarketSourceStatus())
                    .append(bean.getPlaceNumStatus())
                    .append(bean.getPaStatus())
                    .append(bean.getNameCode())
                    .append(bean.getMarketSource())
                    .append(bean.getEndEdStatus())
                    .append(bean.getOldThirdMarketSourceStatus());
            String marketMd5 = DigestUtils.md5DigestAsHex(marketSb.toString().getBytes());
            String oMd5Str = redisClient.get(String.format(RCS_DATA_KEY_CACHE_KEY, MARKET_COMPARE, bean.getId()));
            if (StringUtils.isNotBlank(oMd5Str)) {
                String[] split = oMd5Str.split("-");
                String oTime = split[0];
                String oMd5 = split[1];
                if(!(NumberUtils.isNumber(oTime)&&Long.valueOf(oTime).equals(dataSourceTime)&&1==reCallFalg)) {
                    if (NumberUtils.isNumber(oTime) && Long.valueOf(oTime) > dataSourceTime) {
                        return false;
                    }
                    if (marketMd5.equals(oMd5)) {
                        return false;
                    }
                }
            }
            redisClient.setExpiry(String.format(RCS_DATA_KEY_CACHE_KEY, MARKET_COMPARE, bean.getId()),dataSourceTime + "-" + marketMd5, 120L);
        } catch (Exception e) {
            log.error("::{}::{},{},{}","RDSMOG_"+msg.getLinkId(),JsonFormatUtils.toJson(bean),e.getMessage(),e);
        }
        return true;
    }

    /**
     * @Description: 检查赛事盘口赔率数据有效性
     * @Date: 2019/12/12
     **/
    private boolean checkMarketOddsDataValid(StandardMarketOddsMessageDTO bean, Long dataSourceTime, Integer reCallFalg, Request<StandardMatchMarketMessageDTO> msg) {
        if(null==bean.getId()) {return false;}
        try {
            StringBuilder oddsSb = new StringBuilder("")
                    .append(bean.getId())
                    .append(bean.getMarketId())
                    .append(bean.getActive())
                    .append(bean.getSettlementResultText())
                    .append(bean.getOddsType())
                    .append(bean.getAddition1())
                    .append(bean.getAddition2())
                    .append(bean.getAddition3())
                    .append(bean.getAddition4())
                    .append(bean.getAddition5())
                    .append(bean.getOddsValue())
                    .append(bean.getPaOddsValue())
                    .append(bean.getOddsFieldsTemplateId())
                    .append(bean.getThirdTemplateSourceId())
                    .append(bean.getOriginalOddsValue())
                    .append(bean.getOrderOdds())
                    .append(bean.getDataSourceCode())
                    .append(bean.getThirdOddsFieldSourceId())
                    .append(bean.getExtraInfo())
                    .append(bean.getMargin())
                    .append(bean.getMarketDiffValue())
                    .append(bean.getProbability())
                    .append(bean.getProbabilityOdds())
                    .append(bean.getAnchor())
                    .append(bean.getMarginProbabilityOdds())
                    .append(bean.getNameCode())
                    .append(bean.getThirdSourceActive())
                    .append(bean.getStatus())
                    .append(JsonFormatUtils.toJson(bean.getI18nNames()));
            String oddsMd5 = DigestUtils.md5DigestAsHex(oddsSb.toString().getBytes());
            String oMd5Str = redisClient.get(String.format(RCS_DATA_KEY_CACHE_KEY, MARKET_ODDS_COMPARE, bean.getId()));
            log.info("oMd5Str RDSMOG_{},value:{}",msg.getLinkId(),oMd5Str);
            if (StringUtils.isNotBlank(oMd5Str)) {
                String[] split = oMd5Str.split("-");
                String oTime = split[0];
//                String oMd5 = split[1];
                if(!(NumberUtils.isNumber(oTime)&&Long.valueOf(oTime).equals(dataSourceTime)&&1==reCallFalg)) {
                    if (NumberUtils.isNumber(oTime) && Long.valueOf(oTime) > dataSourceTime) {
                        return false;
                    }
//                    if (oddsMd5.equals(oMd5)) {
//                        return false;
//                    }
                }
            }
            redisClient.setExpiry(String.format(RCS_DATA_KEY_CACHE_KEY, MARKET_ODDS_COMPARE, bean.getId()), dataSourceTime + "-" + oddsMd5, 120L);
        } catch (Exception e) {
            log.error("::{}::{},{},{}","RDSMOG_"+msg.getLinkId(),JsonFormatUtils.toJson(bean),e.getMessage(),e);
        }
        return true;
    }

    /**
     * 计算初盘
     * @param standardMarketMessageDTOS
     * @param standardMatchInfoId
     * @param matchType
     * @param msg
     */
    private void countFirstMarkt(List<StandardMarketMessageDTO> standardMarketMessageDTOS, Long standardMatchInfoId, Integer matchType, Request<StandardMatchMarketMessageDTO> msg) {
        try {
            if (CollectionUtils.isEmpty(standardMarketMessageDTOS)) {
                return;
            }
            if (1==matchType.intValue()) {
                return;
            }
            for (StandardMarketMessageDTO standardMarketMessageDTO : standardMarketMessageDTOS) {
                ArrayList<RcsFirstMarket> rcsFirstMarkets = new ArrayList<>();
                if (standardMarketMessageDTO.getPlaceNum() != 1) {
                    continue;
                }
                String addition1 = standardMarketMessageDTO.getAddition1();
                if(StringUtils.isBlank(addition1)){
                    log.warn("::{}::盘口值为空{}","RDSMOG_"+msg.getLinkId()+"_"+msg.getData().getStandardMatchInfoId(),JsonFormatUtils.toJson(standardMarketMessageDTO));
                    continue;
                }
                if(!NumberUtils.isNumber(addition1)){
                    continue;
                }
                Long marketCategoryId = standardMarketMessageDTO.getMarketCategoryId();
                if (!(marketCategoryId==38||marketCategoryId==39)) {continue;}
                Double value = Double.valueOf(addition1);
                if(standardMarketMessageDTO.getMarketType()==1){
                    RcsFirstMarket preEndMarket = new RcsFirstMarket();
                    preEndMarket.setStandardMatchId(standardMatchInfoId);
                    preEndMarket.setPlayId(marketCategoryId);
                    preEndMarket.setValue(String.valueOf(value));
                    preEndMarket.setType(2);
                    rcsFirstMarketService.insertOrUpdate(preEndMarket);
                }
                String firstMarketKey = String.format(RCS_DATA_KEY_CACHE_KEY, FIRST_MARKET, standardMatchInfoId+"_"+marketCategoryId);
                String mark = redisClient.get(firstMarketKey);
                if(StringUtils.isNotBlank(mark)&&mark.equals("1")){ //初盘已存
                    continue;
                }
                List list = rcsFirstMarketService.selectData(standardMatchInfoId, marketCategoryId);
                if(CollectionUtils.isNotEmpty(list)){ //初盘已存
                    redisClient.setExpiry(firstMarketKey,"1",2*60*60L);
                    continue;
                }
                //全场大小
                countFirst(standardMatchInfoId, rcsFirstMarkets, marketCategoryId, value);
                rcsFirstMarketService.batchInsert(rcsFirstMarkets);
                redisClient.setExpiry(firstMarketKey,1,2*60*60L);
            }
        } catch (Exception e) {
            log.error("::{}::{},{},{}","RDSMOG_"+msg.getLinkId(),JsonFormatUtils.toJson(standardMarketMessageDTOS),e.getMessage(),e);
        }
    }

    private void countFirst(Long standardMatchInfoId, ArrayList<RcsFirstMarket> rcsFirstMarkets, Long marketCategoryId, Double value) {
        if (marketCategoryId== 38) {
            Double value2 = value / 2;
            Double value4 = value / 4;
            //全场大小 38
            RcsFirstMarket rcsFirstMarket38 = new RcsFirstMarket();
            rcsFirstMarket38.setStandardMatchId(standardMatchInfoId);
            rcsFirstMarket38.setPlayId(38L);
            rcsFirstMarket38.setValue(String.valueOf(value));
            rcsFirstMarket38.setType(1);
            //第一节大小 45
            RcsFirstMarket rcsFirstMarket45 = new RcsFirstMarket();
            rcsFirstMarket45.setStandardMatchId(standardMatchInfoId);
            rcsFirstMarket45.setPlayId(45L);
            rcsFirstMarket45.setValue(String.valueOf(value4));
            rcsFirstMarket45.setType(1);
            //第二节大小 51
            RcsFirstMarket rcsFirstMarket51 = new RcsFirstMarket();
            rcsFirstMarket51.setStandardMatchId(standardMatchInfoId);
            rcsFirstMarket51.setPlayId(51L);
            rcsFirstMarket51.setValue(String.valueOf(value4));
            rcsFirstMarket51.setType(1);
            //第三节大小 57
            RcsFirstMarket rcsFirstMarket57 = new RcsFirstMarket();
            rcsFirstMarket57.setStandardMatchId(standardMatchInfoId);
            rcsFirstMarket57.setPlayId(57L);
            rcsFirstMarket57.setValue(String.valueOf(value4));
            rcsFirstMarket57.setType(1);
            //第四节大小 63
            RcsFirstMarket rcsFirstMarket63 = new RcsFirstMarket();
            rcsFirstMarket63.setStandardMatchId(standardMatchInfoId);
            rcsFirstMarket63.setPlayId(63L);
            rcsFirstMarket63.setValue(String.valueOf(value4));
            rcsFirstMarket63.setType(1);
            //上半场大小 18
            RcsFirstMarket rcsFirstMarket18 = new RcsFirstMarket();
            rcsFirstMarket18.setStandardMatchId(standardMatchInfoId);
            rcsFirstMarket18.setPlayId(18L);
            rcsFirstMarket18.setValue(String.valueOf(value2));
            rcsFirstMarket18.setType(1);
            //下半场大小 26
            RcsFirstMarket rcsFirstMarket26 = new RcsFirstMarket();
            rcsFirstMarket26.setStandardMatchId(standardMatchInfoId);
            rcsFirstMarket26.setPlayId(26L);
            rcsFirstMarket26.setValue(String.valueOf(value2));
            rcsFirstMarket26.setType(1);

            rcsFirstMarkets.add(rcsFirstMarket38);
            rcsFirstMarkets.add(rcsFirstMarket45);
            rcsFirstMarkets.add(rcsFirstMarket51);
            rcsFirstMarkets.add(rcsFirstMarket57);
            rcsFirstMarkets.add(rcsFirstMarket63);
            rcsFirstMarkets.add(rcsFirstMarket18);
            rcsFirstMarkets.add(rcsFirstMarket26);

        } else if (marketCategoryId == 39) {
            //全场让分
            Double value2 = value / 2;
            Double value4 = value / 4;
            //全场让分 39
            RcsFirstMarket rcsFirstMarket39 = new RcsFirstMarket();
            rcsFirstMarket39.setStandardMatchId(standardMatchInfoId);
            rcsFirstMarket39.setPlayId(39L);
            rcsFirstMarket39.setValue(String.valueOf(value));
            rcsFirstMarket39.setType(1);
            //第一节让分 56
            RcsFirstMarket rcsFirstMarket56 = new RcsFirstMarket();
            rcsFirstMarket56.setStandardMatchId(standardMatchInfoId);
            rcsFirstMarket56.setPlayId(56L);
            rcsFirstMarket56.setValue(String.valueOf(value4));
            rcsFirstMarket56.setType(1);
            //第二节让分 52
            RcsFirstMarket rcsFirstMarket52 = new RcsFirstMarket();
            rcsFirstMarket52.setStandardMatchId(standardMatchInfoId);
            rcsFirstMarket52.setPlayId(52L);
            rcsFirstMarket52.setValue(String.valueOf(value4));
            rcsFirstMarket52.setType(1);
            //第三节让分 58
            RcsFirstMarket rcsFirstMarket58 = new RcsFirstMarket();
            rcsFirstMarket58.setStandardMatchId(standardMatchInfoId);
            rcsFirstMarket58.setPlayId(58L);
            rcsFirstMarket58.setValue(String.valueOf(value4));
            rcsFirstMarket58.setType(1);
            //第四节让分 64
            RcsFirstMarket rcsFirstMarket64 = new RcsFirstMarket();
            rcsFirstMarket64.setStandardMatchId(standardMatchInfoId);
            rcsFirstMarket64.setPlayId(64L);
            rcsFirstMarket64.setValue(String.valueOf(value4));
            rcsFirstMarket64.setType(1);
            //上半场让分 19
            RcsFirstMarket rcsFirstMarket19 = new RcsFirstMarket();
            rcsFirstMarket19.setStandardMatchId(standardMatchInfoId);
            rcsFirstMarket19.setPlayId(19L);
            rcsFirstMarket19.setValue(String.valueOf(value2));
            rcsFirstMarket19.setType(1);
            //下半场让分 143
            RcsFirstMarket rcsFirstMarket143 = new RcsFirstMarket();
            rcsFirstMarket143.setStandardMatchId(standardMatchInfoId);
            rcsFirstMarket143.setPlayId(143L);
            rcsFirstMarket143.setValue(String.valueOf(value2));
            rcsFirstMarket143.setType(1);

            rcsFirstMarkets.add(rcsFirstMarket39);
            rcsFirstMarkets.add(rcsFirstMarket56);
            rcsFirstMarkets.add(rcsFirstMarket52);
            rcsFirstMarkets.add(rcsFirstMarket58);
            rcsFirstMarkets.add(rcsFirstMarket64);
            rcsFirstMarkets.add(rcsFirstMarket19);
            rcsFirstMarkets.add(rcsFirstMarket143);
        }
    }
}