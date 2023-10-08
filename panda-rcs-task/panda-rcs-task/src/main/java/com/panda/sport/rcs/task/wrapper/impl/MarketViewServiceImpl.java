package com.panda.sport.rcs.task.wrapper.impl;

import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mapper.RcsMatchMarketConfigMapper;
import com.panda.sport.rcs.mongo.MarketCategory;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.MatchBetChange;
import com.panda.sport.rcs.pojo.MatchPeriod;
import com.panda.sport.rcs.pojo.MatchStatisticsInfo;
import com.panda.sport.rcs.pojo.statistics.RcsMatchDimensionStatistics;
import com.panda.sport.rcs.task.wrapper.MarketViewService;
import com.panda.sport.rcs.task.wrapper.MatchPeriodService;
import com.panda.sport.rcs.task.wrapper.MatchStatisticsInfoService;
import com.panda.sport.rcs.task.wrapper.RcsCodeService;
import com.panda.sport.rcs.task.wrapper.statistics.RcsMatchDimensionStatisticsService;
import com.panda.sport.rcs.vo.MatchMarketLiveOddsVo;
import com.panda.sport.rcs.vo.statistics.RcsMatchDimensionStatisticsVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 盘口视图展示服务类
 */

@Service
@Slf4j
public class MarketViewServiceImpl implements MarketViewService {

    @Autowired
    MatchStatisticsInfoService matchStatisticsInfoService;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    RcsMatchMarketConfigMapper rcsMatchMarketConfigMapper;

    @Autowired
    private RcsMatchDimensionStatisticsService matchDimensionStatisticsService;
    @Autowired
    private MatchPeriodService matchPeriodService;
    @Autowired
    private ProducerSendMessageUtils sendMessage;
    @Autowired
    private RcsCodeService rcsCodeService;

    @Override
    public boolean updateMatchBetChange(RcsMatchDimensionStatistics matchDimensionStatistics) {
        log.info("更新同联赛赛事实货量updateMatchBetChange{}", JsonFormatUtils.toJson(matchDimensionStatistics));
        try {
            if (matchDimensionStatistics != null) {
                MatchBetChange matchBetChange = new MatchBetChange();
                BeanUtils.copyProperties(matchDimensionStatistics, matchBetChange);
                MatchStatisticsInfo info = matchStatisticsInfoService.getMatchInfoByMatchId(matchBetChange.getMatchId());
                if (info != null) {
                    matchBetChange.setSet1Score(info.getSet1Score());
                    matchBetChange.setScore(info.getScore());
                    matchBetChange.setPeriod(info.getPeriod());
                    matchBetChange.setSecondsMatchStart(info.getSecondsMatchStart());
                    matchBetChange.setMatchPeriodId(info.getPeriod());
                    MatchPeriod one = matchPeriodService.getOne(matchBetChange.getMatchId(), info.getPeriod());
                    if (one != null) matchBetChange.setPeriodScore(one.getScore());
                    Long[] matchIds = {matchBetChange.getMatchId()};
                    //近一小时货量
                    List<RcsMatchDimensionStatisticsVo> rcsMatchDimensionStatisticsVos = matchDimensionStatisticsService.searchNearlyOneHourRealTimeValue(matchIds);

                    if (!CollectionUtils.isEmpty(rcsMatchDimensionStatisticsVos)) {
                        matchBetChange.setTotalValueOneHour(rcsMatchDimensionStatisticsVos.get(0).getRealTimeValue());
                    }
                    sendMessage.sendMessage(MqConstants.WS_MATCH_BET_CHANGED_TOPIC, MqConstants.WS_MATCH_BET_CHANGED_TAG, "", matchBetChange);
                }
            }
        } catch (Exception e) {
            log.error("推送同联赛赛事实货量失败", e);
            return false;
        }
        return true;
    }

    @Override
    public Long getRollNum(Long standardTournamentId) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        Long beginTime = System.currentTimeMillis() - 1000 * 60 * 60 * 4L;
        Criteria criteriaTime = Criteria.where("matchStartTime").gte(DateUtils.transferLongToDateStrings(beginTime));
        criteria.andOperator(criteriaTime);
        criteria.and("standardTournamentId").is(standardTournamentId);
        criteria.and("liveOddBusiness").is(1).and("matchStatus").in(1, 2, 10);
        query.addCriteria(criteria);
        long count = mongoTemplate.count(query, MatchMarketLiveOddsVo.class);
        return count;
    }

    @Override
    public Long getOtherCategoryNum(Long matchId) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("matchId").is(String.valueOf(matchId));
        String jsonIds = rcsCodeService.getValue("other_category", "0");
        if (StringUtils.isNotBlank(jsonIds)) {
            List<Long> longs = JsonFormatUtils.fromJsonArray(jsonIds, Long.class);
            criteria.and("id").in(longs);
        }
        long count = mongoTemplate.count(query.addCriteria(criteria), MarketCategory.class);
        return count;
    }


}
