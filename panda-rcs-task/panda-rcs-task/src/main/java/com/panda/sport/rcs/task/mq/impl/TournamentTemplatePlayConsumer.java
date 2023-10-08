package com.panda.sport.rcs.task.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mongo.MarketCategory;
import com.panda.sport.rcs.task.service.MatchServiceImpl;
import com.panda.sport.rcs.vo.TournamentTemplateCategoryVo;
import com.panda.sport.rcs.vo.TournamentTemplatePlayVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 玩法最大盘口数更新
 *
 * @author enzo
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "Tournament_Template_Play",
        consumerGroup = "rcs_task_Tournament_Template_Play",
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
public class TournamentTemplatePlayConsumer implements RocketMQListener<JSONObject>{

    @Autowired
    MongoTemplate mongotemplate;

    @Autowired
    MatchServiceImpl matchService;

    @Override
    public void onMessage(JSONObject msg) {
        Long matchId = null;
        log.info("TournamentTemplatePlayConsumer接收参数:{}", JsonFormatUtils.toJson(msg));
        try {
            String data = msg.getString("data");
            if (StringUtils.isBlank(data)) return ;
            TournamentTemplatePlayVo vo = JsonFormatUtils.fromJson(data, TournamentTemplatePlayVo.class);
            matchId = vo.getStandardMatchId().longValue();

            if(String.valueOf(matchId).length()<7){
                return;
            }

            if(matchId==null)return ;
            //1：早盘；0：滚球
            if (matchService.isLive(matchId)) {
                if (1 == vo.getMatchType()) return ;
            } else {
                if (0 == vo.getMatchType()) return ;
            }
            List<TournamentTemplateCategoryVo> categoryList = vo.getCategoryList();
            if (!CollectionUtils.isEmpty(categoryList)) {
                for (TournamentTemplateCategoryVo cavo : categoryList) {
                    Long playId = cavo.getPlayId().longValue();
                    try {
                        Query query = new Query();
                        query.addCriteria(Criteria.where("matchId").is(String.valueOf(matchId)).and("id").is(playId));
                        Update update = new Update();
                        if (null != cavo.getMarketCount())
                            update.set("marketCount", cavo.getMarketCount());
                        if (null != cavo.getOddsAdjustRange())
                            update.set("oddsAdjustRange", cavo.getOddsAdjustRange());
                        if (null != cavo.getMarketAdjustRange())
                            update.set("marketAdjustRange", cavo.getMarketAdjustRange());
                        mongotemplate.updateFirst(query, update, MarketCategory.class);
                    } catch (Exception e) {
                        log.error(e.getMessage()+matchId+"/"+playId, e);
                    }
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
