package com.panda.sport.rcs.task.wrapper.impl;


import java.util.ArrayList;
import java.util.List;

import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.task.wrapper.RcsLanguageInternationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.panda.sport.rcs.mapper.StandardSportTeamMapper;
import com.panda.sport.rcs.mapper.statistics.MatchStatisticsInfoDetailMapper;
import com.panda.sport.rcs.mongo.MatchTeamVo;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetail;
import com.panda.sport.rcs.pojo.StandardSportTeam;
import com.panda.sport.rcs.task.wrapper.StandardSportTeamService;
import com.panda.sport.rcs.vo.SportTeam;
import org.springframework.util.StringUtils;

import static com.panda.sport.rcs.constants.RedisKey.EXPRIY_TIME_2_HOURS;
import static com.panda.sport.rcs.constants.RedisKey.RCS_TASK_TEAMINFO_CACHE;

/**
 * <p>
 * 标准球队信息表.
 * 球队id 与比赛id 作为唯一性约束 服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
@Service
public class StandardSportTeamServiceImpl extends ServiceImpl<StandardSportTeamMapper, StandardSportTeam> implements StandardSportTeamService {

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private StandardSportTeamMapper sportTeamMapper;

    @Autowired
    private RcsLanguageInternationService languageService;

    @Autowired
    private MatchStatisticsInfoDetailMapper matchStatisticsInfoDetailMapper;

    @Override
    public List<SportTeam> queryTeamsByMatchId(Long matchId) {
        String key = RCS_TASK_TEAMINFO_CACHE+matchId;
        String value = redisClient.get(key);
        List<SportTeam> result = new ArrayList<>();
        if(StringUtils.isEmpty(value)){
            List<SportTeam> sportTeams = sportTeamMapper.selectTeamsByMatchId(matchId);
            if(sportTeams != null && sportTeams.size() > 0 ) {
                result = sportTeams;
            }
            redisClient.setExpiry(key,JSONObject.toJSONString(sportTeams),EXPRIY_TIME_2_HOURS);
        }else {
            result=  JSONObject.parseArray(value,SportTeam.class);
        }
    	return result;
    	
//        Object o = GuavaCache.get(String.format("sportTeam_%s", matchId));
//        List<SportTeam> sportTeams = new ArrayList<>();
//        if (null != o) {
//            sportTeams = JsonFormatUtils.fromJsonArray(JsonFormatUtils.toJson(o), SportTeam.class);
//        } else {
//            sportTeams = sportTeamMapper.selectTeamsByMatchId(matchId);
//            GuavaCache.put(String.format("sportTeam_%s", matchId), JsonFormatUtils.toJson(sportTeams));
//        }
//        return sportTeams;
    }

    @Override
    public List<MatchTeamVo> queryTeamList(Long matchId) {
        List<MatchTeamVo> teamList = new ArrayList<>();
        try {
            List<SportTeam> sportTeams = this.queryTeamsByMatchId(matchId);
            teamList = Lists.newArrayListWithCapacity(sportTeams.size());
            if (!CollectionUtils.isEmpty(sportTeams)) {
                MatchStatisticsInfoDetail matchStatisticsInfoDetail = matchStatisticsInfoDetailMapper.selectRedScore(matchId);
                for (SportTeam sourceTeam : sportTeams) {
                    MatchTeamVo team = new MatchTeamVo();
                    team.setNameCode(sourceTeam.getNameCode());
                    // 国际化
                    team.setNames(languageService.getCachedNamesMapByCode(sourceTeam.getNameCode()));
                    team.setMatchPosition(sourceTeam.getMatchPosition());
                    if (matchStatisticsInfoDetail != null) {
                        if ("home".equals(team.getMatchPosition())) {
                            team.setRedCardNum(matchStatisticsInfoDetail.getT1());
                        } else {
                            team.setRedCardNum(matchStatisticsInfoDetail.getT2());
                        }
                    }
                    teamList.add(team);
                }
            }
        }catch (Exception e){
            log.error("matchId:"+matchId+"球队名称"+e.getMessage(),e);
        }
        return teamList;
    }
}
