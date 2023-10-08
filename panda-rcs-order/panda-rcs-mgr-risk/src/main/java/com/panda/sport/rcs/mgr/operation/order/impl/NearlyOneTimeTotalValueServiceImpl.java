package com.panda.sport.rcs.mgr.operation.order.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mapper.RcsLockMapper;
import com.panda.sport.rcs.mgr.aspect.RcsLockSeriesTypeEnum;
import com.panda.sport.rcs.mgr.aspect.RcsLockable;
import com.panda.sport.rcs.mgr.operation.order.CalcOrder;
import com.panda.sport.rcs.mgr.operation.order.impl.CalcOrderBase;
import com.panda.sport.rcs.mgr.paid.annotion.Order;
import com.panda.sport.rcs.mgr.wrapper.ITOrderDetailService;
import com.panda.sport.rcs.mgr.wrapper.statistics.RcsTotalValueNearlyOneTimeService;
import com.panda.sport.rcs.pojo.TOrderDetail;
import com.panda.sport.rcs.pojo.statistics.RcsTotalValueNearlyOneTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.operation.order.impl
 * @Description :  近一小时货量
 * @Date: 2019-12-30 20:33
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Order(200)
public class NearlyOneTimeTotalValueServiceImpl extends CalcOrderBase implements CalcOrder {
    @Autowired
    private RcsTotalValueNearlyOneTimeService rcsTotalValueNearlyOneTimeService;

    @Autowired
    private ITOrderDetailService orderDetailService;

    /**
     * 等待时长
     */
    private Long waitLongTime = 60 * 60 * 1000L;

    /**
     * @Description   删除记录
     * @Param []
     * @Author  myname
     * @Date  15:37 2019/12/31
     * @return void
     **/
    private void deleteRecord() {
        //更新订单时间
        //如果存在数据，从表中取出最小的ID
        try {
            List<RcsTotalValueNearlyOneTime> list = rcsTotalValueNearlyOneTimeService.list();
            log.info("近一小时获取{}个数据，列表bean:{}", list.size(), list.size());
            for (RcsTotalValueNearlyOneTime bean : list) {
                if (System.currentTimeMillis() - bean.getUpdateTime() >  waitLongTime) {
                    QueryWrapper<TOrderDetail> query = new QueryWrapper<>();
                    query.eq("match_id", bean.getMatchId());
                    query.gt("bet_time", System.currentTimeMillis() - waitLongTime);
                    query.orderByAsc("id").last("limit 1");
                    TOrderDetail detail = orderDetailService.getOne(query);
                    if (detail == null ) {
                        QueryWrapper<RcsTotalValueNearlyOneTime>  rcsTotalValueNearlyOneTimeQueryWrapper = new QueryWrapper<>();
                        rcsTotalValueNearlyOneTimeQueryWrapper.lambda().eq(RcsTotalValueNearlyOneTime::getMatchId,bean.getMatchId());
                        rcsTotalValueNearlyOneTimeService.remove(rcsTotalValueNearlyOneTimeQueryWrapper);
                        log.info("删除记录:{}", JsonFormatUtils.toJson(bean));
                        continue;
                    }
                    if(!bean.getOrderDetailId().equals(detail.getId())){
                        bean.setMatchId(bean.getMatchId());
                        bean.setUpdateTime(detail.getBetTime());
                        bean.setOrderDetailId(detail.getId());
                        rcsTotalValueNearlyOneTimeService.updateById(bean);
                        log.info("近一时更新,bean:{}", JsonFormatUtils.toJson(bean));
                    }
                }
            }
        } catch (Exception ex) {
            log.error("删除记录近一时任务报错{}", ex.getMessage(), ex);
        }
    }

    /**
     * 每20秒执行一次
     */
    //@PostConstruct
    public void initConfigCache() {
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
            deleteRecord();
        }, 20, 10, TimeUnit.SECONDS);
    }

    /**
     * 处理
     *
     * @param orderBean
     */
    @Override
    @RcsLockable(key = "nearly_oneTime_total_value",seriesType = RcsLockSeriesTypeEnum.Single)
    public void orderHandle(OrderBean orderBean, Integer type) {
        //只取单条数据,不要串关的
        if (orderBean.getSeriesType() != 1) {
            log.warn("::{}::近一时数据处理：串关数据不处理，bean{}" ,orderBean.getOrderNo(),JsonFormatUtils.toJson(orderBean));
            return;
        }

        //拒单
        if(orderBean.getOrderStatus()!=1){
            log.warn("::{}::期望值-近一小时-接收拒单mq，不在计算：",orderBean.getOrderNo());
            return;
        }

        for (OrderItem item : orderBean.getItems()) {
            //判断是不是足球
            if(item.getSportId()!=1){
                continue;
            }
            QueryWrapper<TOrderDetail> query = new QueryWrapper<>();
            query.eq("bet_no", item.getBetNo());
            TOrderDetail orderDetail = orderDetailService.getOne(query);

            QueryWrapper<RcsTotalValueNearlyOneTime> oneTimeQuery = new QueryWrapper<>();
            oneTimeQuery.eq("match_id", item.getMatchId());
            RcsTotalValueNearlyOneTime bean = rcsTotalValueNearlyOneTimeService.getOne(oneTimeQuery);
            if (bean == null) {
                bean = new RcsTotalValueNearlyOneTime();
                bean.setOrderDetailId(orderDetail.getId());
                bean.setMatchId(item.getMatchId());
                bean.setUpdateTime(System.currentTimeMillis());
                rcsTotalValueNearlyOneTimeService.save(bean);
            }
        }
    }

    public static void main(String[] args){
        Long time = System.currentTimeMillis() - 60 * 60 * 1000;
        Long time1 = 1577521592694L;
        System.out.println( time1<time);
    }
}
