package com.panda.sport.rcs.predict.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.pojo.TOrderDetail;
import com.panda.sport.rcs.pojo.statistics.RcsTotalValueNearlyOneTime;
import com.panda.sport.rcs.predict.service.LastHourService;
import com.panda.sport.rcs.predict.service.RcsTotalValueNearlyOneTimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 预测货量表 服务实现类
 * </p>
 *
 * @author lithan auto
 * @since 2020-07-18
 */
@Service
@Slf4j
public class LastHourServiceImpl implements LastHourService {

    @Autowired
    private RcsTotalValueNearlyOneTimeService rcsTotalValueNearlyOneTimeService;

    @Autowired
    private TOrderDetailMapper orderDetailMapper;

    //等待时长
    private Long waitLongTime = 60 * 60 * 1000L;


    /**
     * 记录当前下单的赛事 保证其记录在RcsTotalValueNearlyOneTime表中
     * 该表记录赛事近一个小时 的第一笔注单 的时间
     * @param item
     */
    @Override
    public void matchLastOrderTime(OrderItem item, Integer type) {
        //判断是不是足球
        if (item.getSportId() != 1 && item.getSportId() != 2) {
            return;
        }
        //取消注单 特意设置为时间为0  表示这个赛事记录的时间肯定过期了   然后立即触发 去寻找该赛事最近一小时的第一笔注单时间
        if (type == -1) {
            UpdateWrapper<RcsTotalValueNearlyOneTime> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(RcsTotalValueNearlyOneTime::getMatchId, item.getMatchId());
            updateWrapper.lambda().set(RcsTotalValueNearlyOneTime::getUpdateTime, 0L);
            rcsTotalValueNearlyOneTimeService.update(updateWrapper);
            //立即触发
            deleteRecord();
        } else {
            //查找该赛事是否记录  如果没有记录  则记录
            QueryWrapper<RcsTotalValueNearlyOneTime> oneTimeQuery = new QueryWrapper<>();
            oneTimeQuery.eq("match_id", item.getMatchId());
            RcsTotalValueNearlyOneTime bean = rcsTotalValueNearlyOneTimeService.getOne(oneTimeQuery);

            if (bean == null) {
                QueryWrapper<TOrderDetail> query = new QueryWrapper<>();
                query.eq("bet_no", item.getBetNo());
                TOrderDetail orderDetail = orderDetailMapper.selectOne(query);

                bean = new RcsTotalValueNearlyOneTime();
                bean.setOrderDetailId(orderDetail.getId());
                bean.setMatchId(item.getMatchId());
                bean.setUpdateTime(System.currentTimeMillis());
                rcsTotalValueNearlyOneTimeService.save(bean);
            }
        }
    }

    /**
     * 每20秒执行一次
     */
    // @PostConstruct
    public void initConfigCache() {
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
            deleteRecord();
        }, 20, 10, TimeUnit.SECONDS);
    }

    //更新订单时间
    private void deleteRecord() {
        //如果存在数据，从表中取出最小的ID
        try {
            List<RcsTotalValueNearlyOneTime> list = rcsTotalValueNearlyOneTimeService.list();
            log.info("近一小时获取{}个数据", list.size());
            for (RcsTotalValueNearlyOneTime bean : list) {
                //如果时间已经过期 超过一小时
                if (System.currentTimeMillis() - bean.getUpdateTime() > waitLongTime) {
                    //查询最近一个小时内的第一笔订单
                    QueryWrapper<TOrderDetail> query = new QueryWrapper<>();
                    query.select("min(id) as id");
                    query.eq("match_id", bean.getMatchId());
                    query.gt("bet_time", System.currentTimeMillis() - waitLongTime);
                    query.eq("order_status", 1);
                    TOrderDetail detail = orderDetailMapper.selectOne(query);
                    //如果没有  就删除该赛事的记录
                    if (detail == null || detail.getId()==null) {
                        QueryWrapper<RcsTotalValueNearlyOneTime> rcsTotalValueNearlyOneTimeQueryWrapper = new QueryWrapper<>();
                        rcsTotalValueNearlyOneTimeQueryWrapper.lambda().eq(RcsTotalValueNearlyOneTime::getMatchId, bean.getMatchId());
                        rcsTotalValueNearlyOneTimeService.remove(rcsTotalValueNearlyOneTimeQueryWrapper);
                        log.info("删除记录:{}", JsonFormatUtils.toJson(bean));
                        continue;
                    }
                    detail = orderDetailMapper.selectById(detail.getId());
                    //如果有  并且和现在记录的不一致  则更新为最新的
                    if (!bean.getOrderDetailId().equals(detail.getId())) {
                        bean.setMatchId(bean.getMatchId());
                        bean.setUpdateTime(detail.getBetTime());
                        bean.setOrderDetailId(detail.getId());
                        rcsTotalValueNearlyOneTimeService.updateById(bean);
                        log.info("近一时更新,bean:{}", JsonFormatUtils.toJson(bean));
                    }
                }
            }
        } catch (Exception ex) {
            log.error("近一时任务报错", ex.getMessage(), ex);
        }
    }
}
