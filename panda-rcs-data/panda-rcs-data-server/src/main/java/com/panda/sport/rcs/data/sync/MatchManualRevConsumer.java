package com.panda.sport.rcs.data.sync;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.data.mq.RcsConsumer;
import com.panda.sport.rcs.data.service.IStandardMatchInfoService;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.pojo.MatchMarketLiveBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 支持手动rev赛事消费(数据下发)
 *
 * @author abel
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "AO_PRE_REVERSE_MATCHS",
        consumerGroup = "RCS_DATA_AO_PRE_REVERSE_MATCHS_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchManualRevConsumer extends RcsConsumer<JSONObject> {

    @Resource
    private IStandardMatchInfoService standardMatchInfoService;
    @Autowired
    private MongoTemplate mongotemplate;

    @Override
    protected String getTopic() {
        return "AO_PRE_REVERSE_MATCHS";
    }

    public void test(JSONObject message){
        handleMs(message);
    }

    @Override
    protected Boolean handleMs(JSONObject message) {
        if (message == null) {
            return true;
        }
        String linkId = message.getString("linkId");
        try {
            JSONArray matchIdsJson = message.getJSONArray("matchIds");
            if (matchIdsJson == null) {
                log.warn("{}::ManualRev赛事::空的赛事id", linkId);
                return true;
            }
            List<Long> matchIds = matchIdsJson.toJavaList(Long.class);
            if (CollUtil.isEmpty(matchIds)) {
                log.warn("{}::ManualRev赛事::空的赛事id集合", linkId);
                return true;
            }
            handler(matchIds, linkId);
        } catch (Exception e) {
            log.error("{}::ManualRev赛事::处理错误::{}", linkId, message, e);
        }
        return true;
    }

    /**
     * 更新mysql库和mongoDB
     *
     * @param matchIds
     */
    @Transactional(rollbackFor = Throwable.class)
    private void handler(List<Long> matchIds, String linkId) {
        long startTime = System.currentTimeMillis();
        standardMatchInfoService.updateMatchMauManualRevStatus(matchIds);
        updateMongoDB(matchIds, linkId);
        long timeLen = System.currentTimeMillis() - startTime;
        log.info("{}::ManualRev赛事::支持的赛事::{}::总耗时::{}", linkId, matchIds, timeLen);
    }

    /**
     * 更新芒果
     *
     * @param matchIds
     * @param linkId
     */
    private void updateMongoDB(List<Long> matchIds, String linkId) {
        long startTime = System.currentTimeMillis();
        //1.将所有早盘更新为不支持
        Update update = new Update();
        update.set("manualRev", 0);

        // 3=结束 4=关闭 和所有非滚球状态
        List<Integer> matchStatusParam = CollUtil.newCopyOnWriteArrayList(RcsConstant.LIVE_MATCH_STATUS);
        matchStatusParam.add(3);
        matchStatusParam.add(4);
        Criteria matachStatusCriteria = Criteria.where("sportId").is(SportIdEnum.FOOTBALL.getId())
                .and("matchStatus").nin(matchStatusParam);
        mongotemplate.updateMulti(new Query().addCriteria(matachStatusCriteria), update, MatchMarketLiveBean.class);
        //2.将指定赛事更新为支持
        List<List<Long>> splitList = CollUtil.split(matchIds, 500);
        Update updateYes = new Update();
        updateYes.set("manualRev", 1);
        for (List<Long> list : splitList) {
            Query query = new Query();
            Criteria yesCriteria = Criteria.where("matchId").in(list);
            query.addCriteria(yesCriteria);
            mongotemplate.updateMulti(query, updateYes, MatchMarketLiveBean.class);
        }
        long timeLen = System.currentTimeMillis() - startTime;
        log.info("{}::ManualRev赛事::支持的mongoDB赛事::耗时::{}", linkId, timeLen);
    }
}
