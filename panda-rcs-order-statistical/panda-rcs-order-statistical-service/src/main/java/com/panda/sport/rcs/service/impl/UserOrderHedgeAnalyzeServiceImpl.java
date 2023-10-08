package com.panda.sport.rcs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.common.utils.ThreadUtil;
import com.panda.sport.rcs.customdb.entity.StaticsItemEntity;
import com.panda.sport.rcs.customdb.service.impl.StaticsItemServiceImpl;
import com.panda.sport.rcs.db.entity.TOrderDetail;
import com.panda.sport.rcs.db.entity.UserProfileOrderTag;
import com.panda.sport.rcs.db.service.TOrderDetailService;
import com.panda.sport.rcs.db.service.impl.UserProfileOrderTagServiceImpl;
import com.panda.sport.rcs.service.IUserOrderHedgeAnalyzeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.service.impl
 * @description :   分析用户订单是否存在对赌投注的情况服务实现
 * @date: 2020-06-28 10:17
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service("userOrderHedgeAnalyzeServiceImpl")
public class UserOrderHedgeAnalyzeServiceImpl implements IUserOrderHedgeAnalyzeService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    StaticsItemServiceImpl staticsItemService;

    @Autowired
    TOrderDetailService orderDetailService;

    @Autowired
    UserProfileOrderTagServiceImpl userProfileOrderTagService;

    /***
     *  分析从 timeStamp指定的时间戳开始,某个时间段的用户订单是否存在对赌投注情况。暂定7天内的数据
     * @param beginTime
     * @return void
     * @Description
     * @Author dorich
     * @Date 10:12 2020/6/28
     **/
    @Override
    public void analyzeUserOrderHedge(long beginTime) {

        /***
         * 1. 查找指定时间内所有投注的玩家;遍历玩家做后续步骤
         * 2. 分析对冲投注的订单
         *  ***/
        long endTime = beginTime + LocalDateTimeUtil.dayMill;

        String msg = "对冲投注开始" + LocalDateTimeUtil.milliToLocalDateTime(beginTime) + ";截止时间戳:" + LocalDateTimeUtil.milliToLocalDateTime(endTime);
        log.info(msg);

        /***查找指定时间内所有投注的玩家;遍历玩家做后续步骤***/
        List<StaticsItemEntity> uidList = staticsItemService.fetchHedgeAnalyzeUserId(beginTime, endTime);
        msg = "共查到满足时间要求的用户个数:" + uidList.size() + ";" + msg;
        log.info(msg);
        for (StaticsItemEntity entity : uidList) {
            ThreadUtil.submit(()->{
                try {
                    log.info("准备分析用户:" + entity.getUid() + "的投注对冲注单");
                    analyzeUserOrderHedge(entity.getUid(), beginTime, endTime);
                    log.info("完成分析用户:" + entity.getUid() + "的投注对冲注单");
                } catch (Exception e) {
                    String tip = String.format("用户(uid:%s)订单对冲分析失败.", entity.getUid());
                    log.error(tip, e);
                }
            });
        }
        log.info("对冲订单分析结束." + msg);
    }

    @Override
    public void analyzeUserOrderHedge(long uid, long timeStampBegin, long timeStampEnd) {
        /**
         * 1. 查找当前玩家的指定时间内所有的订单;
         * 2. 分析订单是否存在对赌情况;
         * 3. 保存或者更新该订单在  user_profile_order_tag 中对赌 投注情况.
         *
         *  ***/
        List<UserProfileOrderTag> orderTags = new ArrayList<>();
        /***查找当前玩家的指定时间内所有的订单;***/
        List<TOrderDetail> orderDetails = staticsItemService.queryOrderByCondition(uid, timeStampBegin, timeStampEnd);
        if (CollectionUtils.isEmpty(orderDetails)) {
            return;
        }
        /***分析订单是否存在对赌情况;***/
        Map<Long, List<TOrderDetail>> orderGroupByMarket = orderDetails.stream().collect(Collectors.groupingBy(TOrderDetail::getMarketId));
        Set<Long> multiOrderMarkets = orderGroupByMarket.keySet().stream().filter(e -> orderGroupByMarket.get(e).size() > 1).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(multiOrderMarkets)) {
            String timeTip = String.format("%s-%s", timeStampBegin, timeStampEnd);
            log.info("在时间范围内(" + timeTip + "),uid为" + uid + "用户,无对冲的投注");
            return;
        }
        for (Long marketId : multiOrderMarkets) {
            List<TOrderDetail> marketOrders = orderGroupByMarket.get(marketId);
            Map<Long, List<TOrderDetail>> orderGroupBySelectionId = marketOrders.stream().collect(Collectors.groupingBy(TOrderDetail::getPlayOptionsId));
            /*** 同一个盘口的多个投注项上有该用户的订单,说明该用户在指定盘口存在对冲注单***/
            if (orderGroupBySelectionId.keySet().size() > 1) {
                for (TOrderDetail orderDetail : marketOrders) {
                    UserProfileOrderTag orderTag = new UserProfileOrderTag();
                    orderTag.setBetNo(orderDetail.getBetNo());
                    orderTag.setOrderNo(orderDetail.getOrderNo());
                    orderTag.setIsInverse(1);
                    orderTags.add(orderTag);
                }
            }
        }
        /*** 将对冲信息保存至表 ***/
        updateUserProfileOrderTag(orderTags);
    }

    /***
     *
     * @param orderTags
     * @return void
     * @Description
     * @Author dorich
     * @Date 14:00 2020/6/28
     **/
    public void updateUserProfileOrderTag(List<UserProfileOrderTag> orderTags) {
        if (CollectionUtils.isEmpty(orderTags)) {
            return;
        }
        //先删除 支持重复跑任务
        Set<String> betOrderSets = orderTags.stream().map(UserProfileOrderTag::getBetNo).collect(Collectors.toSet());
        QueryWrapper<UserProfileOrderTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(UserProfileOrderTag::getBetNo, betOrderSets);
        userProfileOrderTagService.remove(queryWrapper);

        /***此处所有订单单号都需要整理并入库 ***/
        List<UserProfileOrderTag> userProfileOrderTags = new ArrayList<>();
        for (UserProfileOrderTag tag : orderTags) {
            UserProfileOrderTag newTag = new UserProfileOrderTag();
            newTag.setIsInverse(1);
            newTag.setBetNo(tag.getBetNo());
            newTag.setOrderNo(tag.getOrderNo());
            userProfileOrderTags.add(newTag);
        }
        /*** 批量保存数据 ***/
        if (!CollectionUtils.isEmpty(userProfileOrderTags)) {
            userProfileOrderTagService.saveBatch(userProfileOrderTags);
        }
    }
}
