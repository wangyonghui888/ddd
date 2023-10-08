package com.panda.sport.rcs.repository.impl;

import com.google.common.collect.Lists;
import com.mongodb.client.result.UpdateResult;
import com.panda.sport.rcs.pojo.TOrderDetailExtDO;
import com.panda.sport.rcs.repository.TOrderDetailExtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class TOrderDetailExtRepositoryImpl implements TOrderDetailExtRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public TOrderDetailExtDO queryUnHandleOrderExt() {
        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(Criteria.where("handleStatus").is(0),
                                                    Criteria.where("handleStatus").is(null))).limit(1);
        query.with(Sort.by(Sort.Direction.DESC, "crtTime"));
        return mongoTemplate.findOne(query, TOrderDetailExtDO.class);
    }

    @Override
    public TOrderDetailExtDO queryLatestOrderExt() {
        Query query = new Query();
        query.limit(1);
        query.with(Sort.by(Sort.Direction.DESC, "crtTime"));
        return mongoTemplate.findOne(query, TOrderDetailExtDO.class);
    }

    @Override
    public List<TOrderDetailExtDO> selectWaitedOrderList(Map<String, Object> params) {
        Integer size = Integer.valueOf(ObjectUtils.nullSafeToString(params.get("size")));
        Long scanTableCreateTime = Long.valueOf(ObjectUtils.nullSafeToString(params.get("scanTableCreateTime")));
        Integer start =  Integer.valueOf(ObjectUtils.nullSafeToString(params.get("start")));
        Long currentTime = Long.valueOf(ObjectUtils.nullSafeToString(params.get("currentTime")));
        Query query = new Query();
        query.addCriteria(
                        Criteria.where("handleStatus")
                                .is(0)
                                .orOperator(
                                        Criteria.where("orderStatus").gt(0),
                                        Criteria.where("maxAcceptTime")
                                                .lt(currentTime)
                                                .and("orderStatus")
                                                .in(Lists.newArrayList(0, -1)))
                                .and("createTime").gte(scanTableCreateTime)
                )
                .skip(start)
                .limit(size);
        query.with(Sort.by(Sort.Direction.DESC, "crtTime"));

        return mongoTemplate.find(query, TOrderDetailExtDO.class);
    }

    @Override
    public Integer updateOrderDetailExtStatusByOrderNo(TOrderDetailExtDO tOrderDetailExt) {
        Criteria criteria = new Criteria();
        criteria = criteria.and("orderNo").is(tOrderDetailExt.getOrderNo());
        if(tOrderDetailExt.getOrderStatus() != 2) {
            criteria = criteria.and("betNo").is(tOrderDetailExt.getBetNo());
        }
        Query query = new Query(criteria);
        Update update = new Update();
        update.set("updateTime", new Date()).set("handleStatus",1).set("orderStatus",tOrderDetailExt.getOrderStatus());

        UpdateResult result = mongoTemplate.updateMulti(query, update, TOrderDetailExtDO.class);
        return Math.toIntExact(result.getMatchedCount());
    }

    @Override
    public List<TOrderDetailExtDO> getOrderDetailExtByIds(List<String> ids) {
        Query query = new Query(Criteria.where("orderNo").in(ids));
        return mongoTemplate.find(query,TOrderDetailExtDO.class,"t_order_detail_ext");
    }

    @Override
    public Integer updateIgnorePauseOrder(TOrderDetailExtDO tOrderDetailExt) {
        Criteria criteria = new Criteria();
        criteria = criteria.and("betNo").is(tOrderDetailExt.getBetNo());
        Query query = new Query(criteria);

        Update update = new Update();
        update.set("updateTime", new Date()).set("handleStatus",0).set("orderStatus",0);

        UpdateResult result = mongoTemplate.updateMulti(query, update, TOrderDetailExtDO.class);
        return Math.toIntExact(result.getMatchedCount());
    }
}
