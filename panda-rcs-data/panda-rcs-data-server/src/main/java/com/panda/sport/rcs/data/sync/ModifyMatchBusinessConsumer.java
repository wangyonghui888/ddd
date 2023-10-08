package com.panda.sport.rcs.data.sync;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.panda.merge.bo.StandardSportMarketCategoryBO;
import com.panda.merge.bo.StandardSportOddsFieldsTempletBO;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.mq.RcsConsumer;
import com.panda.sport.rcs.data.service.IStandardMatchInfoService;
import com.panda.sport.rcs.data.service.IStandardMatchTeamRelationService;
import com.panda.sport.rcs.data.service.IStandardSportTeamService;
import com.panda.sport.rcs.data.service.RcsLanguageInternationService;
import com.panda.sport.rcs.data.utils.DataCache;
import com.panda.sport.rcs.data.utils.WordsTools;
import com.panda.sport.rcs.pojo.*;
import com.panda.sport.rcs.pojo.dto.I18nItemDTO;
import com.panda.sport.rcs.pojo.dto.StandardMatchInfoDTO;
import com.panda.sport.rcs.pojo.dto.StandardSportTeamDTO;
import com.panda.sport.rcs.pojo.dto.StandardSportTournamentDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = "modify_match_business",
        consumerGroup = "RCS_DATA_MODIFY_MATCH_BUSINESS_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class ModifyMatchBusinessConsumer extends RcsConsumer<Request<List<StandardMatchInfoDTO>>> {

    @Autowired
    IStandardMatchInfoService iStandardMatchInfoService;
    @Autowired
    IStandardSportTeamService iStandardSportTeamService;
    @Autowired
    IStandardMatchTeamRelationService iStandardMatchTeamRelationService;
    @Autowired
    RcsLanguageInternationService rcsLanguageInternationService;


    protected static final String MATCH_TEMP_INFO = "matchTempInfo";
    protected static final String MATCH_TEMP_INFO_BEGIN_TIME = "matchTempInfoBeginTime";
    private static final String RCS_DATA_KEY_CACHE_KEY = RedisKeys.RCS_DATA_KEY_CACHE_KEY;

    //入库的语言类型
    private static final List<String> saveLanguages= Arrays.asList("zs","zh","en");

    @Autowired
    private RedisClient redisClient;

    @Override
    protected String getTopic() {
        return "modify_match_business";
    }

    @Override
    @Trace
    public Boolean handleMs(Request<List<StandardMatchInfoDTO>> dto) {
        try {
            if (dto == null || dto.getData() == null || dto.getData().size() == 0) {
                return true;
            }
            List<StandardMatchInfoDTO> matchInfoDatas = dto.getData();
            List<StandardMatchInfo> standardMatchInfos = copyList(StandardMatchInfo.class, matchInfoDatas);
            List<StandardSportTeam> standardSportTeams = new ArrayList<>();
            ArrayList<StandardMatchTeamRelation> standardMatchTeamRelations = new ArrayList<>();
            List<I18nItemDTO> i18nItemBOS = new ArrayList<>();
            for (StandardMatchInfoDTO matchInfoDatum : matchInfoDatas) {
                log.info("::{}::","RDMMG_"+dto.getLinkId()+"_"+matchInfoDatum.getId());
                List<StandardSportTeamDTO> sportTeamList = matchInfoDatum.getSportTeamList();
                if(!CollectionUtils.isEmpty(matchInfoDatum.getIl8nMatchPositionList())){
                    i18nItemBOS.addAll(matchInfoDatum.getIl8nMatchPositionList());
                }
                standardSportTeams.addAll(copyList(StandardSportTeam.class, sportTeamList));
                for (StandardSportTeamDTO standardSportTeamBO : sportTeamList) {
                    i18nItemBOS.addAll(standardSportTeamBO.getIl8nNameList());
                    StandardMatchTeamRelation matchTeamRelation = new StandardMatchTeamRelation();
                    matchTeamRelation.setStandardMatchId(matchInfoDatum.getId());
                    matchTeamRelation.setStandardTeamId(standardSportTeamBO.getId());
                    matchTeamRelation.setMatchPosition(standardSportTeamBO.getMatchTeamRelation().getMatchPosition());
                    matchTeamRelation.setRemark(standardSportTeamBO.getMatchTeamRelation().getRemark());
                    long relationTime = System.currentTimeMillis();
                    matchTeamRelation.setCreateTime(relationTime);
                    matchTeamRelation.setModifyTime(relationTime);
                    standardMatchTeamRelations.add(matchTeamRelation);
                }
            }
            dataProcessing(standardMatchInfos,dto.getLinkId());
            insertLanguageInternation(i18nItemBOS,dto.getLinkId());
            //球队插入
            iStandardSportTeamService.batchInsertOrUpdate(standardSportTeams);
            //球队赛事插入
            iStandardMatchTeamRelationService.batchInsertOrUpdate(standardMatchTeamRelations);
            //赛事插入
            iStandardMatchInfoService.batchInsertOrUpdate(standardMatchInfos);
        } catch (Exception e) {
            log.error( "::{}::{},{}","RDMMG_"+dto.getLinkId(),e.getMessage(), e);
        }
        return true;
    }
    private void dataProcessing(List<StandardMatchInfo> standardMatchInfos, String linkId) {
        try {
            if(CollectionUtils.isEmpty(standardMatchInfos)) {return;}
            for (StandardMatchInfo standardMatchInfo : standardMatchInfos) {
                if ("Sold".equals(standardMatchInfo.getLiveMatchSellStatus())) {
                    standardMatchInfo.setLiveOddBusiness(1);
                } else {
                    standardMatchInfo.setLiveOddBusiness(0);
                }
                if ("Sold".equals(standardMatchInfo.getPreMatchSellStatus())) {
                    standardMatchInfo.setPreMatchBusiness(1);
                } else {
                    standardMatchInfo.setPreMatchBusiness(0);
                }
                //matchOver 为1的时候 liveOddBusiness  preMatchBusiness这两个状态变为0
                if(standardMatchInfo!=null&&standardMatchInfo.getMatchOver()==1){
                    standardMatchInfo.setLiveOddBusiness(0);
                    standardMatchInfo.setPreMatchBusiness(0);
                    standardMatchInfo.setMatchStatus(3);
                }
                //设置第三方赛事信息
                try {
                    if (!CollectionUtils.isEmpty(standardMatchInfo.getThirdMatchInfoList())){
                        List<ThirdMatchBO> thirdMatchBOS = JSONObject.parseObject(JsonFormatUtils.toJson(standardMatchInfo.getThirdMatchInfoList()), new TypeReference<List<ThirdMatchBO>>() {});
                        if (!CollectionUtils.isEmpty(thirdMatchBOS)){standardMatchInfo.setThirdMatchListStr(JsonFormatUtils.toJson(thirdMatchBOS));}
                    }
                } catch (Exception e) {
                    log.error("::{}::{},{}","RDMMG_"+linkId,e.getMessage(), e);
                }
                //设定赛事信息缓存
                try {
                    String key1 = String.format(RCS_DATA_KEY_CACHE_KEY, MATCH_TEMP_INFO, standardMatchInfo.getId());
                    redisClient.hSet(key1,"period",String.valueOf(standardMatchInfo.getMatchPeriodId()));
                    String keyBeginTime = String.format(RCS_DATA_KEY_CACHE_KEY, MATCH_TEMP_INFO_BEGIN_TIME, standardMatchInfo.getId());
                    redisClient.setExpiry(keyBeginTime,String.valueOf(standardMatchInfo.getMatchPeriodId()),3*24*60*60l);
                    if(standardMatchInfo.getMatchType()==2||standardMatchInfo.getMatchType()==3){
                        //电竞
                        redisClient.hSet(key1,"isESport",String.valueOf(standardMatchInfo.getId()));
                    }
                    redisClient.expireKey(key1,2 * 24 * 60 * 60);
                } catch (Exception e) {
                    log.error("::{}::{},{}","RDMMG_"+linkId,e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("::{}::{},{}","RDMMG_"+linkId,e.getMessage(), e);
        }
    }

    /**
     * @MethodName:
     * @Description: list拷贝
     * @Param:
     * @Return:
     * @Author: Vector
     * @Date: 2019/9/30
     **/
    public static <T, E> List<E> copyList(Class<E> classOfE, List<T> list) throws IllegalAccessException, InstantiationException {
        List<E> eLis = new ArrayList<E>();
        for (T t : list) {
            E e = classOfE.newInstance();
            //高速反射值进createTime
            e = BeanCopyUtils.copyProperties(t, classOfE);
            MethodAccess access = MethodAccess.get(classOfE);
            int index = access.getIndex("setCreateTime", Long.class);
            access.invoke(e,index,System.currentTimeMillis());

            try {
                if(t instanceof StandardSportTournamentDTO) {
                    Method method = t.getClass().getMethod("getIl8nNameList");
                    List<I18nItemDTO> i18List = (List<I18nItemDTO>) method.invoke(t);

                    int code = access.getIndex("setNameCode", Long.class);
                    access.invoke(e,code,getNameCode(i18List));
                    //区域id
                    Method method2 = t.getClass().getMethod("getStandardSportRegionId");
                    Long regionId = (Long) method2.invoke(t);
                    int code2 = access.getIndex("setRegionId", Long.class);
                    access.invoke(e,code2,regionId);
                }else if(t instanceof StandardSportTeamDTO) {
                    Method method = t.getClass().getMethod("getIl8nNameList");
                    List<I18nItemDTO> i18List = (List<I18nItemDTO>) method.invoke(t);

                    int code = access.getIndex("setNameCode", Long.class);
                    access.invoke(e,code,getNameCode(i18List));
                }else if(t instanceof StandardSportOddsFieldsTempletBO) {
                    Method method = t.getClass().getMethod("getI18nNameList");
                    List<I18nItemDTO> i18List = (List<I18nItemDTO>) method.invoke(t);

                    int code = access.getIndex("setNameCode", Long.class);
                    access.invoke(e,code,getNameCode(i18List));
                }else if(t instanceof StandardSportMarketCategoryBO) {
                    Method method = t.getClass().getMethod("getI18nNameList");
                    List<I18nItemDTO> i18List = (List<I18nItemDTO>) method.invoke(t);

                    int code = access.getIndex("setNameCode", Long.class);
                    access.invoke(e,code,getNameCode(i18List));
                }
            }catch (Exception e1) {
                log.warn(e1.getMessage(),e1);
            }
            eLis.add(e);
        }
        return eLis;
    }

    private static Long getNameCode(List<I18nItemDTO> il8nNameList) {
        if(il8nNameList != null && il8nNameList.size() > 0 ) {
            return il8nNameList.get(0).getNameCode();
        }
        return null;
    }

    /**
     * 国际化检测
     * @param list
     * @param linkId
     * @return
     */
    private static Map insertLanguageInternationCheck(List<I18nItemDTO> list, String linkId) {
        log.info("::{}::国际化过滤进来size{}" ,linkId, list.size());
        ArrayList<RcsLanguageInternation> rcsLanguageInternations = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        Map<Long, List<I18nItemDTO>> collect = list.stream().filter(a -> saveLanguages.contains(a.getLanguageType())).collect(Collectors.groupingBy(I18nItemDTO::getNameCode));
        Iterator<Map.Entry<Long, List<I18nItemDTO>>> iterator = collect.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, List<I18nItemDTO>> next = iterator.next();
            Long key = next.getKey();
            try {
                StringBuilder stb = new StringBuilder();
                List<I18nItemDTO> valueList = next.getValue();
                for (I18nItemDTO i18nItemBO : valueList) {
                    stb.append(i18nItemBO.getNameCode())
                            .append(i18nItemBO.getLanguageType())
                            .append(i18nItemBO.getText());
                }
                LinkedHashMap map = transferMapJson(valueList);
                String oMd52 = DataCache.insertCheckCache.getIfPresent(String.valueOf(key));
                String l2 = JsonFormatUtils.toJson(map);
                String nMd52 = DigestUtils.md5DigestAsHex(stb.toString().getBytes());
                if (StringUtils.isBlank(oMd52) || (!oMd52.equals(nMd52))) {
                    RcsLanguageInternation rcsLanguageInternation = new RcsLanguageInternation();
                    rcsLanguageInternation.setNameCode(WordsTools.stringValueOf(key));
                    rcsLanguageInternation.setText(l2);
                    rcsLanguageInternations.add(rcsLanguageInternation);
                }
                DataCache.insertCheckCache.put(String.valueOf(key), nMd52);
            } catch (Exception e) {
                log.error("::{}::{},{}","RDMMG_"+linkId,e.getMessage(), e);
            }
        }
        log.info("::{}::国际化过滤出去size{}" ,linkId, rcsLanguageInternations.size());
        HashMap<Object, Object> map = new HashMap<>();
        map.put("newVersion", rcsLanguageInternations);
        return map;
    }

    private static LinkedHashMap transferMapJson(List<I18nItemDTO> valueList) {
        LinkedHashMap<Object, Object> objectObjectHashMap = new LinkedHashMap<>();
        for (I18nItemDTO i18nItemBO : valueList) {
            objectObjectHashMap.put(i18nItemBO.getLanguageType(),i18nItemBO.getText());
        }
        return objectObjectHashMap;
    }

    /**
     * @MethodName:
     * @Description: 插入国际化数据
     * @Param:
     * @Return:
     * @Author: V
     * @Date: 2019/9/28
     **/
    private void insertLanguageInternation(List<I18nItemDTO> il8nMatchPositionList, String linkId)  {
        try {
            Map map = insertLanguageInternationCheck(il8nMatchPositionList,linkId);
            Object newVersion = map.get("newVersion");
            if (null != newVersion) {
                ArrayList<RcsLanguageInternation> rcsLanguageInternations = (ArrayList<RcsLanguageInternation>) newVersion;
                rcsLanguageInternationService.batchInsertOrUpdate(rcsLanguageInternations);
            }
        } catch (Exception e) {
            log.error("::{}::{},{},{}","RDMMG_"+linkId,e.getMessage(),JsonFormatUtils.toJson(il8nMatchPositionList), e);
        }
    }

}
