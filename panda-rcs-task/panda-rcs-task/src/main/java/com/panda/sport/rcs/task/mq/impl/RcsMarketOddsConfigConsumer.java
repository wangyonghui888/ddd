package com.panda.sport.rcs.task.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.util.UuidUtils;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.mapper.RcsBroadCastMapper;
import com.panda.sport.rcs.mapper.RcsLanguageInternationMapper;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mapper.StandardSportMarketMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplateMapper;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsBroadCast;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.dto.RcsBroadCastDTO;
import com.panda.sport.rcs.pojo.dto.TournamentTemplateMarginDto;
import com.panda.sport.rcs.task.enums.LanguageTypeDataEnum;
import com.panda.sport.rcs.task.utils.LanguageUtils;
import com.panda.sport.rcs.task.utils.RcsMarketOddsConfigUtil;
import com.panda.sport.rcs.vo.operation.RealTimeVolumeBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.task.mq.impl
 * @Description :  注单过来处理预警与否
 * @Date: 2020-09-13 13:45
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
public class RcsMarketOddsConfigConsumer extends ConsumerAdapter<List<RealTimeVolumeBean>> {
    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    private RcsTournamentTemplateMapper rcsTournamentTemplateMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private RcsBroadCastMapper rcsBroadCastMapper;
    @Autowired
    private RcsLanguageInternationMapper languageInternationMapper;
    @Autowired
    private StandardSportMarketMapper standardSportMarketMapper;
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;
    @Autowired
    private LanguageUtils languageUtils;
    /**
     *
     */
    private static final String AWAY = "away";
    /**
     * redis 缓存 赛事数据  队伍对战数据等
     */
    private static final String PREFIX_TEAM_NAME = "rcs:ws:team::%s";
    /**
     * redis 缓存  公告已经发送次数 早盘和滚球进行了区分
     */
    private static final String KEY= "rcs:warn:match_id:play_id:market_id:match_type:%s:%s:%s:%s";


    public RcsMarketOddsConfigConsumer() {
        super("rcs_predict_odds_data",null);
    }

    @Autowired
    private MessageCenterConsumer messageCenterConsumer;
    /**
     * @Description  订单过来预警处理
     * @Param [realTimeVolumeBeanList, map]
     * @Author  kimi
     * @Date   2020/9/16
     * @return java.lang.Boolean
     **/
    @Override
    public Boolean handleMs(List<RealTimeVolumeBean> realTimeVolumeBeanList, Map<String, String> map) throws Exception {
        try {
            log.info("realTimeVolumeBeanList保存收到:{}",JSONObject.toJSONString(realTimeVolumeBeanList));
            if(CollectionUtils.isEmpty(realTimeVolumeBeanList)) {
                log.info("数据为空！跳过暂不处理！");
                return true;
            }
            RealTimeVolumeBean realTimeVolumeBean = realTimeVolumeBeanList.get(0);
            if (SportIdEnum.BASKETBALL.isYes(realTimeVolumeBean.getSportId())) {
                // 篮球没有出涨预警
                return true;
            }
            Integer isChuZhang = RcsMarketOddsConfigUtil.getPlayIds(realTimeVolumeBean.getSportId(), realTimeVolumeBean.getPlayId());
            double actualIncreaseLimit = 0;
            String str;
            if (isChuZhang==1) {
                if (realTimeVolumeBeanList.size() != 2) {
                    log.info("出涨额的配置不是两项盘口");
                    return true;
                } else {
                    actualIncreaseLimit = realTimeVolumeBean.getSumMoney().subtract(realTimeVolumeBeanList.get(1).getSumMoney()).doubleValue();
                    if (actualIncreaseLimit < 0) {
                        actualIncreaseLimit = -actualIncreaseLimit;
                    }
                }
                str="up";
            } else {
                double maxCompensation = 0;
                double betTotal = 0;
                for (RealTimeVolumeBean rcsMarketOddsConfig : realTimeVolumeBeanList) {
                    betTotal = betTotal + rcsMarketOddsConfig.getSumMoney().doubleValue();
                    if (rcsMarketOddsConfig.getPaidAmount() != null && rcsMarketOddsConfig.getPaidAmount().doubleValue() > maxCompensation) {
                        maxCompensation = rcsMarketOddsConfig.getPaidAmount().doubleValue();
                    }
                }
                actualIncreaseLimit = maxCompensation - betTotal;
                str="pay";
            }
            log.info("actualIncreaseLimit:{}", actualIncreaseLimit);
            StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectById(realTimeVolumeBean.getMatchId());
            if (standardMatchInfo==null){
                log.info("赛事未找到 可能是冠军玩法暂时不处理预警");
                return true;
            }
            Integer matchType = Integer.parseInt(realTimeVolumeBean.getMatchType())-1;
            String clientkey = String.format(KEY, String.valueOf(realTimeVolumeBean.getMatchId()), String.valueOf(realTimeVolumeBean.getPlayId()),
                String.valueOf(realTimeVolumeBean.getMatchMarketId()), String.valueOf(matchType));
            int oldFrequency = 0;
            String s = redisClient.get(clientkey);
            if (s != null) {
                oldFrequency = Integer.parseInt(s);
            }
            TournamentTemplateMarginDto tournamentTemplateMarginDto;
            tournamentTemplateMarginDto=rcsTournamentTemplateMapper.selectRcsTournamentTemplateMarket(1-matchType, 3, standardMatchInfo.getId().intValue(),
                realTimeVolumeBean.getPlayId(), realTimeVolumeBean.getMarketIndex());
            if (tournamentTemplateMarginDto == null) {
                tournamentTemplateMarginDto = rcsTournamentTemplateMapper.selectRcsTournamentTemplateMarketSub(1-matchType, 3, standardMatchInfo.getId().intValue(),
                        realTimeVolumeBean.getPlayId(), realTimeVolumeBean.getMarketIndex());
                if (tournamentTemplateMarginDto == null) {
                    log.error("未找到数据");
                    return true;
                }
            }
            int i = tournamentTemplateMarginDto.getMarketWarn() * tournamentTemplateMarginDto.getMaxSingleBetAmount();
            int newFrequency = (int) (actualIncreaseLimit) / i;
            if (newFrequency > oldFrequency) {
                redisClient.setExpiry(clientkey, newFrequency,  24 * 60 * 60L);
                RcsBroadCast rcsBroadCast=new RcsBroadCast();
                rcsBroadCast.setMsgType(1);
                rcsBroadCast.setMsgId(UuidUtils.generateUuid());
                rcsBroadCast.setExtendsField(String.valueOf(standardMatchInfo.getId()));
                rcsBroadCast.setStatus(1);
                int value = (int) (actualIncreaseLimit)-i;
                StandardSportMarket standardSportMarket = standardSportMarketMapper.selectById(realTimeVolumeBean.getMatchMarketId());
                String content = getContent(standardMatchInfo, standardSportMarket, value, str, i, actualIncreaseLimit, isChuZhang);
                rcsBroadCast.setContent(content);
                rcsBroadCast.setExtendsField1(String.valueOf(matchType));
                RcsBroadCastDTO rcsBroadCastDTO = creatRcsBroadCastDTO(rcsBroadCast, standardMatchInfo.getSportId(), matchType);
                log.info("构造出涨预警消息体:{}", JSONObject.toJSONString(rcsBroadCastDTO));
                messageCenterConsumer.handleMs(rcsBroadCastDTO,null);
            }
            return true;
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return true;
        }
    }

    private String getContent(StandardMatchInfo standardMatchInfo,StandardSportMarket standardSportMarket,int value,String str,int i,double actualIncreaseLimit,Integer isChuZhang){
        HashMap<String,String> hashMap=new HashMap<>();
        for (LanguageTypeDataEnum languageTypeDataEnum : LanguageTypeDataEnum.values()){
            String type = languageTypeDataEnum.getType();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(languageUtils.getHomeNameAndAwayName(standardMatchInfo.getId(), type)).append("(").append(standardMatchInfo.getMatchManageId()).append("):")
                .append(languageUtils.getPlayName(standardSportMarket.getMarketCategoryId(), type)).append(languageUtils.getPlayLanguageNameByType(type));
            if (standardSportMarket.getAddition1()!=null){
                stringBuilder.append(standardSportMarket.getAddition1());
            }
            if (i==actualIncreaseLimit){
                stringBuilder.append(languageUtils.getMarketLanguageNameByType(isChuZhang,type));
            }else {
                if (type.equals(LanguageTypeDataEnum.EN.getType())){
                    stringBuilder .append(languageUtils.getMarketLanguageNameByType(type)).append(languageUtils.getUpOrPay(type,str)).append(" ¥").append(value).append("。");
                }else {
                    stringBuilder .append(languageUtils.getMarketLanguageNameByType(type)).append(languageUtils.getUpOrPay(type,str)).append(value).append("元。");
                }
            }
            hashMap.put(type,stringBuilder.toString());
        }
        return JSONObject.toJSONString(hashMap);
    }

    /**
     * @Description  发送mq给ws
     * @Param [rcsBroadCast]
     * @Author  kimi
     * @Date   2020/10/24
     * @return void
     **/
    private RcsBroadCastDTO  creatRcsBroadCastDTO(RcsBroadCast rcsBroadCast,Long sportId,Integer matchType){
            RcsBroadCastDTO rcsBroadCastDTO=new RcsBroadCastDTO();
            rcsBroadCastDTO.setMatchType(matchType);
            rcsBroadCastDTO.setRcsBroadCast(rcsBroadCast);
            rcsBroadCastDTO.setSportId(sportId);
            return rcsBroadCastDTO;
    }








}
