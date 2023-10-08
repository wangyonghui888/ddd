package com.panda.sport.rcs.mgr.repository.impl;

import com.panda.sport.rcs.mgr.repository.TOrderDetailExtRepository;
import com.panda.sport.rcs.pojo.TOrderDetailExtDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class TOrderDetailExtRepositoryImpl implements TOrderDetailExtRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Integer queryOrderStatus(String betNo) {
        Query query = new Query(Criteria.where("betNo").is(betNo));
        TOrderDetailExtDO orderStatusResult = mongoTemplate.findOne(query,TOrderDetailExtDO.class,"t_order_detail_ext");
        return null!=orderStatusResult?orderStatusResult.getOrderStatus():null;
    }

    @Override
    public void updateOrderDetailExtStatus(String orderNo, String orderStatus) {
        Query quey = new Query(Criteria.where("orderNo").is(orderNo));
        Update update =
                new Update()
                        .set("updateTime", new Date())
                        .set("handleStatus", 1)
                        .set("orderStatus", Integer.valueOf(orderStatus));
        mongoTemplate.updateMulti(quey, update, "t_order_detail_ext");
    }

    @Override
    public void saveOrUpdateTOrderDetailExt(List<TOrderDetailExtDO> exts) {
        mongoTemplate.insertAll(exts);
    }

    @Override
    public void updateOrderDetailExt(List<TOrderDetailExtDO> exts) {
        if(exts.size()>0){
            exts.stream().forEach(e->{
                Query quey = new Query(Criteria.where("betNo").is(e.getBetNo()));
                Update update =
                        new Update()
                                .set("maxAcceptTime", e.getMaxAcceptTime())
                                .set("handleStatus", e.getHandleStatus())
                                .set("orderStatus", e.getOrderStatus());
                mongoTemplate.updateFirst(quey, update, TOrderDetailExtDO.class);
            });
        }
    }
}
