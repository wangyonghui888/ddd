package com.panda.sport.rcs.data.repository.impl;

import com.google.common.collect.Lists;
import com.panda.sport.rcs.data.repository.TOrderDetailExtRepository;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateAcceptConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class TOrderDetailExtRepositoryImpl implements TOrderDetailExtRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void updateOrderDetailExtToMongo(Long currentTime, RcsTournamentTemplateAcceptConfig config) {
        Long matchId = config.getMatchId();
        Integer categorySetId = config.getCategorySetId();
        Integer eventTypeNumber = config.getEventTypeNumber();
        Integer maxWaitTime = config.getMaxWaitTime();
        String eventCode = config.getEventCode();


        if (Lists.newArrayList(0, 1, 2, 3).contains(eventTypeNumber)) {
            // 主要更新條件
            Criteria criteria =
                    Criteria.where("handleStatus")
                            .is(0)
                            .and("orderStatus")
                            .is(0)
                            .and("categorySetId")
                            .is(categorySetId)
                            .and("matchId")
                            .is(matchId);

            Query query = new Query(criteria);
            Update update = new Update().set("updateTime", new Date());

            // 事件 0:安全、2:封盤、3:拒單、1:危險
            switch (eventTypeNumber) {
                case 0:
                    criteria.orOperator(
                            Criteria.where("currentEventType").is(1),
                            Criteria.where("currentEventType").is(2),
                            Criteria.where("currentEventType").is(0).and("maxAcceptTime").lt(currentTime));

                    update.set("orderStatus", 1);
                    break;

                case 2:
                case 3:
                    update
                            .set("orderStatus", 2)
                            .set("currentEvent", eventCode)
                            .set("currentEventType", eventTypeNumber);
                    break;

                case 1:
                    criteria.and("currentEventType").is(0);

                    update
                            .set("maxWait", maxWaitTime)
                            .set("maxAcceptTime", currentTime + maxWaitTime * 1000)
                            .set("currentEvent", eventCode)
                            .set("currentEventType", eventTypeNumber);
                    break;
            }
            mongoTemplate.updateMulti(query, update, "t_order_detail_ext");
        }
    }
}
