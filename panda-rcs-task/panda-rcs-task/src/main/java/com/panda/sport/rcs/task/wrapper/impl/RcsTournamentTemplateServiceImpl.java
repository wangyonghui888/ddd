package com.panda.sport.rcs.task.wrapper.impl;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.mapper.StandardSportTournamentMapper;
import com.panda.sport.rcs.mapper.tourTemplate.*;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.StandardSportTournament;
import com.panda.sport.rcs.pojo.dto.TournamentTemplateDto;
import com.panda.sport.rcs.pojo.tourTemplate.*;
import com.panda.sport.rcs.task.wrapper.IRcsTournamentTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.trade.wrapper.tourTemplate
 * @Description :  联赛模板
 * @Date: 2020-05-10 20:39
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@Slf4j
public class RcsTournamentTemplateServiceImpl extends ServiceImpl<RcsTournamentTemplateMapper, RcsTournamentTemplate> implements IRcsTournamentTemplateService {
    @Autowired
    private RcsTournamentTemplateMapper templateMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private StandardSportTournamentMapper tournamentMapper;
    @Autowired
    private RcsTournamentTemplatePlayMargainMapper playMarginMapper;
    @Autowired
    private RcsTournamentTemplatePlayMargainRefMapper playMarginRefMapper;
    @Autowired
    private RcsTournamentTemplatePlayMargainHistoryMapper playMarginHistoryMapper;
    @Autowired
    private RcsTournamentTemplatePlayMargainRefHistoryMapper playMarginRefHistoryMapper;
    @Autowired
    private RcsTournamentTemplateAcceptEventMapper acceptEventMapper;
    @Autowired
    private RcsTournamentTemplateAcceptEventHistoryMapper acceptEventHistoryMapper;

    /**
     * 按联赛 id进行搜索，取联赛配置
     *
     * @param tournamentId
     * @return
     */
    @Override
    public List<TournamentTemplateDto> queryByTournamentId(Long tournamentId, Integer sportId, Integer matchType) {
        String json = redisClient.get(String.format(RedisKeys.TOURTEMPLATE_TOUR_ID, sportId, matchType, tournamentId));
        if (StringUtils.isNotEmpty(json)) {
            return JSONObject.parseObject(json, new TypeReference<List<TournamentTemplateDto>>() {
            });
        } else {
            List<TournamentTemplateDto> list = templateMapper.queryByTournamentId(tournamentId, sportId, matchType);
            if (list != null && list.size() > 0) {
                redisClient.setExpiry(String.format(RedisKeys.TOURTEMPLATE_TOUR_ID, sportId, tournamentId), JSONObject.toJSONString(list), 10L);
            }
            return list;
        }
    }


    /**
     * 按联赛级别进行搜索,取模板
     *
     * @param tournamentLevel,sportId,matchType
     * @return
     */
    @Override
    public List<TournamentTemplateDto> queryByTournamentLevel(Integer tournamentLevel, Integer sportId, Integer matchType) {
        String json = redisClient.get(String.format(RedisKeys.TOURTEMPLATE_TOUR_LEVEL, sportId, matchType, tournamentLevel));
        if (StringUtils.isNotEmpty(json)) {
            return JSONObject.parseObject(json, new TypeReference<List<TournamentTemplateDto>>() {
            });
        } else {
            List<TournamentTemplateDto> list = templateMapper.queryByTournamentLevel(tournamentLevel, sportId, matchType);
            if (list != null) {
                redisClient.setExpiry(String.format(RedisKeys.TOURTEMPLATE_TOUR_LEVEL, sportId, matchType, tournamentLevel), JSONObject.toJSONString(list), 10L);
            }
            return list;
        }
    }

    /**
     * 查询配置
     *
     * @param matchInfo
     * @return
     */
    @Override
    public List<TournamentTemplateDto> query(StandardMatchInfo matchInfo, Integer matchType) {
        List<TournamentTemplateDto> list = queryByTournamentId(matchInfo.getStandardTournamentId(), matchInfo.getSportId().intValue(), matchType);
        if (list == null || list.size() == 0) {
            StandardSportTournament standardSportTournament = tournamentMapper.selectById(matchInfo.getStandardTournamentId());
            if (standardSportTournament != null) {
                list = queryByTournamentLevel(standardSportTournament.getTournamentLevel(), matchInfo.getSportId().intValue(), matchType);
            }
        }
        return list;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncTemplateToHistory(List<RcsTournamentTemplatePlayMargain> playMarginList) {
        List<Long> marginIds = playMarginList.stream().map(RcsTournamentTemplatePlayMargain::getId).collect(Collectors.toList());
        //获取分时margin数据
        QueryWrapper<RcsTournamentTemplatePlayMargainRef> wrapper = new QueryWrapper<>();
        wrapper.lambda().in(RcsTournamentTemplatePlayMargainRef::getMargainId, marginIds);
        List<RcsTournamentTemplatePlayMargainRef> playMarginRefList = playMarginRefMapper.selectList(wrapper);
        List<Long> marginRefIds = playMarginRefList.stream().map(RcsTournamentTemplatePlayMargainRef::getId).collect(Collectors.toList());

        //将margin数据添加到历史表
        List<RcsTournamentTemplatePlayMargainHistory> playMarginHistoryList = BeanCopyUtils.copyPropertiesList(playMarginList, RcsTournamentTemplatePlayMargainHistory.class);
        playMarginHistoryMapper.insertOrUpdateBatch(playMarginHistoryList);
        List<RcsTournamentTemplatePlayMargainRefHistory> playMarginRefHistoryList = BeanCopyUtils.copyPropertiesList(playMarginRefList, RcsTournamentTemplatePlayMargainRefHistory.class);
        playMarginRefHistoryMapper.insertOrUpdateBatch(playMarginRefHistoryList);

        //清除margin数据
        playMarginMapper.deleteBatchIds(marginIds);
        playMarginRefMapper.deleteBatchIds(marginRefIds);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncTemplateAcceptEventToHistory(List<RcsTournamentTemplateAcceptConfig> configList) {
        List<Long> configIds = configList.stream().map(RcsTournamentTemplateAcceptConfig::getId).collect(Collectors.toList());
        //获取滚球接拒单事件数据
        QueryWrapper<RcsTournamentTemplateAcceptEvent> wrapper = new QueryWrapper<>();
        wrapper.lambda().in(RcsTournamentTemplateAcceptEvent::getAcceptConfigId, configIds);
        List<RcsTournamentTemplateAcceptEvent> acceptEventList = acceptEventMapper.selectList(wrapper);
        if(CollectionUtils.isNotEmpty(acceptEventList)){
            //将滚球玩法集接拒单配置数据移入到历史表
            List<RcsTournamentTemplateAcceptEventHistory> acceptEventHistoryList = BeanCopyUtils.copyPropertiesList(acceptEventList, RcsTournamentTemplateAcceptEventHistory.class);
            acceptEventHistoryMapper.insertOrUpdateBatch(acceptEventHistoryList);
            //清除滚球玩法集接拒单配置数据
            List<Long> acceptEventIds = acceptEventList.stream().map(RcsTournamentTemplateAcceptEvent::getId).collect(Collectors.toList());
            acceptEventMapper.deleteBatchIds(acceptEventIds);
        }
    }
}
