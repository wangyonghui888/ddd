package com.panda.sport.rcs.customdb.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.panda.sport.rcs.customdb.entity.DataEntity;
import com.panda.sport.rcs.customdb.entity.LanguageInfo;
import com.panda.sport.rcs.customdb.mapper.LanguageInfoMapper;
import com.panda.sport.rcs.customdb.service.ILanguageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.customdb.service.impl
 * @description :  TODO
 * @date: 2020-07-22 16:45
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service("languageServiceImpl")
public class LanguageServiceImpl implements ILanguageService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LanguageInfoMapper languageInfoMapper;

    /*** 保存nameCode  和  具体的中文信息的map key: sportId; value:体育种类名称  ***/
    private final Map<Long, String> language = new HashMap<>();

    /*** 保存运动种类名称的 map.  key: sportId;value:体育种类名称 ***/
    private final Map<Long, String> sportName = new HashMap<>();

    /*** 保存w玩法名称的 map.  key: sportId; value: 玩法的语言信息,格式: map, key:玩法id,value:该运动种类下,玩法的名称 ***/
    private final Map<Long, Map<Long, String>> playName = new HashMap<>();

    /*** 保存 球队 种类名称的 map.  key: 球队id; value: 球队名称 ***/
    private final Map<Long, String> teamName = new HashMap<>();

    /*** 保存联赛种类名称的 map.  key: 联赛id; value: 联赛种类 ***/
    private final Map<Long, String> tournamentName = new HashMap<>();

    @PostConstruct
    public void initial() {
        /*** 处理运动种类的语言信息 ***/
        List<DataEntity> sportInfo = languageInfoMapper.getSportName();
        log.info("赛种缓存获取:" + JSONObject.toJSONString(sportInfo));
        sportInfo.forEach(e -> sportName.put(e.getSportId(), e.getName()));
        sportName.put(new Long(0), "全部");
        /*** 处理玩法的语言信息 ***/
        List<DataEntity> playInfo = languageInfoMapper.getPlayNameCode();
        log.info("玩法缓存获取:" + JSONObject.toJSONString(playInfo));

        Set<Long> nameCodeSet = playInfo.stream().map(DataEntity::getNameCode).collect(Collectors.toSet());
        
        List<LanguageInfo> languageInfos = languageInfoMapper.getLanguageInfo(nameCodeSet);
        List<LanguageInfo> languageInfosEsport = languageInfoMapper.getEsportLanguageInfo(nameCodeSet);
        languageInfos.addAll(languageInfosEsport);

        /*** key: nameCode, value: 语言信息对象列表(正确数据时 这个对象应该只有一个)***/
        Map<Long, List<LanguageInfo>> languageInfosGroupByNameCode = languageInfos.stream().collect(Collectors.groupingBy(LanguageInfo::getNameCode));
        log.info(JSONObject.toJSONString(languageInfosGroupByNameCode));
        log.info("共查到玩法语言信息个数:" + playInfo.size() + ";玩法的涉及的语言信息编码个数:" + nameCodeSet.size() + ";依次查询到的语言内容个数:" + languageInfosGroupByNameCode.size());

        Map<Long, List<DataEntity>> playDataGroupBy = playInfo.stream().collect(Collectors.groupingBy(DataEntity::getSportId));

        Set<Long> sportIdSet = playDataGroupBy.keySet();

        log.info("玩法信息中,共涉及运动种类:{} 个", sportIdSet.size());
        for (Long sportId : sportIdSet) {
            Map<Long, String> playLanguageInfo = new HashMap<>();
            List<DataEntity> playData = playDataGroupBy.get(sportId);
            Map<Long, List<DataEntity>> playDataGroupByPlayId = playData.stream().collect(Collectors.groupingBy(DataEntity::getPlayId));
            Set<Long> playIdSet = playDataGroupByPlayId.keySet();
            log.info("运动种类(id:{})的玩法信息中,共涉及玩法种类:{} 个", sportId, playIdSet.size());
            for (Long playId : playIdSet) {
                DataEntity entity = playDataGroupByPlayId.get(playId).get(0);
                List<LanguageInfo> temp = languageInfosGroupByNameCode.get(entity.getNameCode());
                if (null == temp) {
                    playLanguageInfo.put(entity.getPlayId(), "");
                    continue;
                }
                String name = temp.get(0).getName();
                //临时方案 处理 特别玩法显示文字
                name = name.replace("{$competitor1}", "主队");
                name = name.replace("{$competitor2}", "客队");
                playLanguageInfo.put(entity.getPlayId(), name);
            }
            playName.put(sportId, playLanguageInfo);
        }
        playName.put(new Long(0), new HashMap<>());
    }


    @Override
    public List<LanguageInfo> getLanguageInfo(Set<Long> nameCodeSet) {
        List<LanguageInfo> languageInfos = languageInfoMapper.getLanguageInfo(nameCodeSet);
        return languageInfos;
    }


    @Override
    public String getLanguageByNameCode(Long nameCode) {
        String name = language.get(nameCode);
        if (null == name) {
            Set<Long> nameCodes = new HashSet<>();
            nameCodes.add(nameCode);
            List<LanguageInfo> languageInfos = this.getLanguageInfo(nameCodes);
            languageInfos.forEach(e -> language.put(e.getNameCode(), e.getName()));
        }
        return language.get(nameCode);
    }


    @Override
    public String getSportName(Long sportId) {
        return sportName.get(sportId);
    }

    @Override
    public String getSportName(Integer sportId) {
        return sportName.get(new Long(sportId));
    }

    @Override
    public String getPlayName(Long sportId, Long playId) {
        try {
            if (sportId == 0) {
                return "全部";
            }
            return playName.get(sportId).get(playId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "--";
    }

    @Override
    public String getPlayName(Integer sportId, Long playId) {
        if (sportId == 0) {
            return "全部";
        }
        return playName.get(new Long(sportId)).get(playId);
    }

    @Override
    public String getPlayName(Integer sportId, Integer playId) {
        if (sportId == 0) {
            return "全部";
        }
        //兼容  后面新加的赛种和玩法 可能缓存没有
        if (playName.get(new Long(sportId)) == null || playName.get(new Long(sportId)).get(new Long(playId)) == null) {

            log.info(sportId + "玩法名称获取失败:赛种识别错误 重新读取 赛种和玩法" + playId);

                initial();

        }
        if(playName.get(new Long(sportId)) == null || playName.get(new Long(sportId)).get(new Long(playId)) == null){
            return String.format("%s",playId);
        }
        return playName.get(new Long(sportId)).get(new Long(playId));
    }

    @Override
    public String getTournamentName(Long tournamentId) {

        String name = tournamentName.get(tournamentId);

        if (null != name) {
            return name;
        }
        tournamentName.put(tournamentId, "");
        /***执行到此处说明联赛名称在内存中不存在 ***/
        DataEntity entity = languageInfoMapper.getTournamentCodeNameById(tournamentId);
        if (null == entity) {
            tournamentName.put(tournamentId, "");
            return "";
        }
        List<LanguageInfo> languageInfos = languageInfoMapper.getLanguageInfo(new HashSet<>(Arrays.asList(entity.getNameCode())));
        if (CollectionUtils.isNotEmpty(languageInfos)) {
            tournamentName.put(tournamentId, languageInfos.get(0).getName());
        }

        return tournamentName.get(tournamentId);
    }

    @Override
    public String getTeamName(Long teamId) {
        String name = teamName.get(teamId);

        if (null != name) {
            return name;
        }
        teamName.put(teamId, "");
        /***执行到此处说明联赛名称在内存中不存在 ***/
        DataEntity entity = languageInfoMapper.getTeamNameCodeById(teamId);
        if (null == entity) {
            return "";
        }


        List<LanguageInfo> languageInfos = languageInfoMapper.getLanguageInfo(new HashSet<>(Arrays.asList(entity.getNameCode())));
        if (CollectionUtils.isNotEmpty(languageInfos)) {
            teamName.put(teamId, languageInfos.get(0).getName());
        }
        return teamName.get(teamId);
    }
}
