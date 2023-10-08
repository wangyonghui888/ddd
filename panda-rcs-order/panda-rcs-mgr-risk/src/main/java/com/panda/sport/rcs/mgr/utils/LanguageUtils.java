package com.panda.sport.rcs.mgr.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.RcsLanguageInternationMapper;
import com.panda.sport.rcs.mgr.enums.LanguageTypeDataEnum;
import com.panda.sport.rcs.vo.ConditionVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: xindaima
 * @description:
 * @author: kimi
 * @create: 2021-02-14 12:20
 **/
@Component
public class LanguageUtils {
    @Autowired
    private  RedisClient redisClient;
    @Autowired
    private RcsLanguageInternationMapper languageInternationMapper;
    /**
     * 存储玩法多语言名字名字
     */
    private  HashMap<String, HashMap<String,String>> hashMap;
    /**
     *
     */
    private  final String AWAY = "away";
    /**
     * redis 缓存 赛事数据  队伍对战数据等
     */
    private  final String PREFIX_TEAM_NAME = "rcs:ws:team::%s";
    /**
     * @Description   获取赛事主队和客队名字
     * @Param [matchId]
     * @Author  kimi
     * @Date   2020/9/18
     * @return java.lang.String
     **/
    public String getHomeNameAndAwayName(Long matchId,String type){
        Map<String, Object> matchInfoCache = getMatchInfoCache(matchId);
        Map<String, Object> homeNameMap = (Map<String, Object>)matchInfoCache.get("homeName");
        Object homeName = homeNameMap.get(type);
        if (homeName==null){
            homeName=homeNameMap.get("en");
        }
        Map<String, Object> awayNameMap =(Map<String, Object>) matchInfoCache.get("awayName");
        Object awayName = awayNameMap.get(type);
        if (awayName==null){
            awayName=homeNameMap.get("en");
        }
        return homeName+" VS "+awayName;
    }

    public  Map<String, Object> getMatchInfoCache(Long matchId) {
        String residStr = redisClient.get(String.format(PREFIX_TEAM_NAME,matchId));
        Map<String, Object> match ;
        if(StringUtils.isBlank(residStr)){
            match = getMatchMarketTeamVos(matchId);
            redisClient.setExpiry(String.format(PREFIX_TEAM_NAME,matchId),match,24*60*60L);
        }else {
            match = JSONObject.parseObject(residStr);
        }
        return match;
    }

    /**
     * @Description   //获取球队和联赛国际化信息
     * @Param [matchId]
     * @Author  Sean
     * @Date  20:18 2020/7/31
     * @return java.util.Map<java.lang.String,java.lang.Object>
     **/
    private  Map<String,Object> getMatchMarketTeamVos(Long matchId) {
        Map<String,Object> nameMap = Maps.newHashMap();
        List<Map<String,String>> teams = languageInternationMapper.queryTeamNameByMatchId(matchId);
        List<Map<String,String>> tournamentName = languageInternationMapper.queryTournamentNameByMatchId(matchId);
        Map<String,String> homeNames = Maps.newHashMap();
        Map<String,String> awayNames = Maps.newHashMap();
        Map<String,String> tournamentNames = Maps.newHashMap();
        Long matchStartTime = System.currentTimeMillis();
        String matchManageId = "0";
        String tournamentId = "0";
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(teams)){
            Map<String, List<Map<String,String>>> teamMap = teams.stream().collect(Collectors.groupingBy(e -> e.get("matchPosition")));
            for (Map.Entry<String, List<Map<String,String>>> map : teamMap.entrySet()){
                List<Map<String,String>> teamName = map.getValue();
                if (BaseConstants.ODD_TYPE_HOME.equalsIgnoreCase(map.getKey())){
                    homeNames = teamName.stream().collect(Collectors.toMap(e -> e.get("languageType"),e -> e.get("text")));
                }else if (AWAY.equalsIgnoreCase(map.getKey())){
                    awayNames = teamName.stream().collect(Collectors.toMap(e -> e.get("languageType"),e -> e.get("text")));
                }
            }
            matchStartTime = com.baomidou.mybatisplus.core.toolkit.ObjectUtils.isNotEmpty(teams.get(0).get("beginTime")) ? Long.parseLong(teams.get(0).get("beginTime")) : System.currentTimeMillis();
            matchManageId = teams.get(0).get("matchManageId");
        }
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(tournamentName)){
            tournamentNames = tournamentName.stream().collect(Collectors.toMap(e -> e.get("languageType"),e -> e.get("text")));
            tournamentId = tournamentName.get(0).get("tournamentId");
        }
        nameMap.put("beginTime", matchStartTime);
        nameMap.put("homeName",homeNames);
        nameMap.put("awayName",awayNames);
        nameMap.put("matchManageId",matchManageId);
        nameMap.put("tournamentNames",tournamentNames);
        nameMap.put("tournamentId",tournamentId);
        return nameMap;
    }

    /**
     * @Description   获取玩法中文名字
     * @Param [playId]
     * @Author  kimi
     * @Date   2020/9/16
     * @return java.lang.String
     **/
    public  String getPlayName(Long playId, String type, Integer sportId) {
        if (CollectionUtils.isEmpty(hashMap)) {
            List<ConditionVo> marketCategoryList = languageInternationMapper.getMarketCategoryList();
            hashMap = new HashMap<>();
            for (ConditionVo conditionVo : marketCategoryList) {
                HashMap<String, String> stringConditionVoHashMap = hashMap.get(sportId+"_"+conditionVo.getId());
                if (CollectionUtils.isEmpty(stringConditionVoHashMap)){
                    stringConditionVoHashMap=new HashMap<>();
                    hashMap.put(sportId+"_"+conditionVo.getId(),stringConditionVoHashMap);
                }
                stringConditionVoHashMap.put(conditionVo.getLanguageType(),conditionVo.getText());
            }
        }
        return hashMap.get(sportId+"_"+playId).get(type);
    }

    public   String getPlayLanguageNameByType(String type){
        if (type.equals(LanguageTypeDataEnum.ZS.getType())){
            return "玩法";
        }else if (type.equals(LanguageTypeDataEnum.EN.getType())){
            return " play ";
        }
        return null;
    }

    public   String getMarketLanguageNameByType(String type){
        if (type.equals(LanguageTypeDataEnum.ZS.getType())){
            return "盘口";
        }else if (type.equals(LanguageTypeDataEnum.EN.getType())){
            return " market";
        }
        return null;
    }

    public   String getMarketLanguageNameByType(Integer isChuZhang,String type){
        if (type.equals(LanguageTypeDataEnum.ZS.getType())){
            if (isChuZhang==1){
                return "盘口货量出涨触及预警限额。";
            }else {
                return"盘口预计赔付触及预警限额。";
            }
        }else if (type.equals(LanguageTypeDataEnum.EN.getType())){
            if (isChuZhang==1){
                return " stock reach limit warning。";
            }else {
                return " payout reach limit warning。";
            }
        }
        return null;
    }


    public String getUpOrPay(String type,String upOrPay){
        //出涨
        if (upOrPay.equals("up")){
            if (type.equals(LanguageTypeDataEnum.ZS.getType())){
                return "货量出涨超警戒值";
            }else if (type.equals(LanguageTypeDataEnum.EN.getType())){
                return " stock exceeded by";
            }
        }else {
            if (type.equals(LanguageTypeDataEnum.ZS.getType())){
                return "预计最大赔付超警戒值";
            }else if (type.equals(LanguageTypeDataEnum.EN.getType())){
                return " payout exceeded limit by";
            }
        }
        return null;
    }

    public  String getSealing(String type){
        if (type.equals("zs")){
            return "已触发早盘跳水封盘，请及时检查开启。";
        }
        else if (type.equals("en")){
            return " already suspended and paused, please check and reoffer market";
        }
        return null;
    }
}
