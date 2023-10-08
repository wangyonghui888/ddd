package com.panda.sport.rcs.mgr.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.common.base.Preconditions;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.mapper.TUserMapper;
import com.panda.sport.rcs.mgr.mq.bean.UserItemBean;
import com.panda.sport.rcs.mgr.wrapper.TUserLabelService;
import com.panda.sport.rcs.mgr.wrapper.UserRegisteredService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsUserConfigNew;
import com.panda.sport.rcs.pojo.TUser;
import com.panda.sport.rcs.pojo.TUserLabel;
import com.panda.sport.rcs.pojo.vo.RcsUserConfigNewConfig;
import com.panda.sport.rcs.service.IRcsUserConfigNewService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author :  Sean
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.rpc.mq.impl
 * @Description :  接收业务组推送的用户注册信息MQ消息
 * @Date: 2019-10-21 11:07
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "panda_rcs_rpc_user",
        consumerGroup = "panda_rcs_rpc_user_group",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class UserRegisteredConsumer implements RocketMQListener<UserItemBean>, RocketMQPushConsumerLifecycleListener {

    private static List<Long> sportIdList = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 341L, 21L, 22L, 23L, 24L, 25L, 26L, 27L, 28L, 29L, 30L, 31L, 32L, 33L, 34L, 35L, 36L, 37L, 38L, 39L, 40L, 41L, 50L, 100L, 101L, 103L, 104L, 1001L, 1002L, 1004L, 1007L, 1008L, 1009L, 1010L, 1011L, 1012L);

    @Autowired
    private UserRegisteredService userRegisteredService;

    @Autowired
    private TUserLabelService tUserLabelService;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    IRcsUserConfigNewService rcsUserConfigNewService;

    @Autowired
    TUserMapper userMapper;

    //TUserLabel表批量插入使用
    private static Map<String, TUserLabel> userLabelMap = new ConcurrentHashMap();
    // RcsUserConfigNew 表批量插入使用
    private static Map<String, RcsUserConfigNew> userConfigNewMap = new ConcurrentHashMap();

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Override
    public void onMessage(UserItemBean msg) {
        log.info("::{}::注册用户用户mq{}", msg.getUserId(), JSONObject.toJSONString(msg));

        try {
            Preconditions.checkNotNull(msg);
            TUser user = BeanCopyUtils.copyProperties(msg, TUser.class);
            user.setUid(msg.getUserId());
            userRegisteredService.saveUserRegistered(user);

            LambdaQueryWrapper<TUserLabel> userLabelLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLabelLambdaQueryWrapper.eq(TUserLabel::getUid, msg.getUserId());
            List<TUserLabel> userLabelList = tUserLabelService.list(userLabelLambdaQueryWrapper);
            if(ObjectUtils.isNotEmpty(userLabelList)){
                Map<String, Object> columnMap = new HashMap<>();
                columnMap.put("uid", msg.getUserId());
                tUserLabelService.removeByMap(columnMap);
            }

            TUserLabel tUserLabel = new TUserLabel();
            tUserLabel.setUid(msg.getUserId());
            tUserLabel.setStatus(1);
            tUserLabel.setUserLevel(msg.getUserLevel());
            tUserLabel.setSportList(getListLabel(msg.getSportList()));
            tUserLabel.setTournamentList(getListLabel(msg.getTournamentList()));
            tUserLabel.setPlayList(getListLabel(msg.getPlayList()));
            tUserLabel.setOrderTypeList(getListLabel(msg.getOrderTypeList()));
            tUserLabel.setOrderStageList(getListLabel(msg.getOrderStageList()));
            //tUserLabelService.save(tUserLabel);
            //添加到队列
            userLabelMap.put(msg.getUserId().toString(), tUserLabel);

            //用户注册时默认同步字段 提前结算 状态为 是
            RcsUserConfigNew rcsUserConfigNew = rcsUserConfigNewService.getOne(new LambdaQueryWrapper<RcsUserConfigNew>().eq(RcsUserConfigNew::getUserId, msg.getUserId()));
            if (rcsUserConfigNew == null) {
                rcsUserConfigNew = new RcsUserConfigNew();
                rcsUserConfigNew.setConfig(JSONObject.toJSONString(sportIdList.stream().map(e -> {
                    RcsUserConfigNewConfig rcsUserConfigNewConfig = new RcsUserConfigNewConfig();
                    rcsUserConfigNewConfig.setSportId(e);
                    return rcsUserConfigNewConfig;
                }).collect(Collectors.toList())));
                rcsUserConfigNew.setUserId(msg.getUserId());
                rcsUserConfigNew.setSettlementInAdvance(msg.getSettleInAdvance());
                rcsUserConfigNew.setSpecialBettingLimit(1);
                //rcsUserConfigNewService.save(rcsUserConfigNew);
                userConfigNewMap.put(msg.getUserId().toString(), rcsUserConfigNew);
                log.info("用户::{}::进行了rcsUserConfigNewService.save操作,参数为{}", msg.getUserId(), JSONObject.toJSONString(rcsUserConfigNew));
            }

            //注册用户没有标签变更不需要发mq
//            Map<String, Object> map = new HashMap<>();
//            map.put("userId", msg.getUserId());
//            map.put("tagId", msg.getUserLevel());
//            producerSendMessageUtils.sendMessage("RCS_LIMIT_USER_TAG_CHANGE","",msg.getUserId().toString(), map);
//            log.info("通知用户::{}::等级发生改变:{}", msg.getUserId(), msg.getUserLevel());

        } catch (Exception e) {
            log.error("::{}:: 注册用户用户mq ERROR {}",msg.getUserId(),e.getMessage(), e);
        }
        return;
    }

    /**
     * TUserLabel和RcsUserConfigNew表 批量插入
     */
    @PostConstruct
    public void initUserData() {
        log.info("用户注册批量插入初始化...");
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
            try {
                List<TUserLabel> userLabelList = new ArrayList<>();
                userLabelMap.forEach((k, v) -> {
                    log.info("::{}::用户TUserLabel进入批量插入", v.getUid());
                    userLabelList.add(v);
                    userLabelMap.remove(k);
                });
                if (userLabelList.size() > 0) {
                    userMapper.userLabelSaveList(userLabelList);
                    log.info("::{}::本次插入条数{},成功,", "用户TUserLabel批量插入", userLabelList.size());
                }

                List<RcsUserConfigNew> userConfigNewList = new ArrayList<>();
                userConfigNewMap.forEach((k, v) -> {
                    log.info("::{}::用户RcsUserConfigNew进入批量插入", v.getUserId());
                    userConfigNewList.add(v);
                    userConfigNewMap.remove(k);
                });
                if (userConfigNewList.size() > 0) {
                    userMapper.userConfigNewSaveList(userConfigNewList);
                    log.info("::{}::本次插入条数{},成功,", "用户TUserLabel批量插入", userLabelList.size());
                }
            } catch (Exception e) {
                log.info("::{}::用户其他数据批量插入异常{}:{}", e.getMessage(), e);
            }
        }, 5, 3, TimeUnit.SECONDS);
    }



    private String getListLabel(List<String> s) {
        if (CollectionUtils.isEmpty(s)) {
            return null;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (int x = 0; x < s.size(); x++) {
                stringBuilder.append(s.get(x));
                if (x < s.size() - 1) {
                    stringBuilder.append(",");
                }
            }
            return stringBuilder.toString();
        }
    }
}
