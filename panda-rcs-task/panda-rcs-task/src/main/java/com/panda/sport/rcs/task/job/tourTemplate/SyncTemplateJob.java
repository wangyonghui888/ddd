package com.panda.sport.rcs.task.job.tourTemplate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainRefMapper;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateComposeModel;
import com.panda.sport.rcs.task.config.RedissonManager;
import com.panda.sport.rcs.task.wrapper.IRcsMatchMarketConfigService;

import lombok.extern.slf4j.Slf4j;

/**
 * 同步联赛模板参数到操盘弹窗
 */
//@Component
//@Slf4j

public class SyncTemplateJob {
//    @Autowired
//    private RcsTournamentTemplatePlayMargainRefMapper playMargainRefMapper;
//    @Autowired
//    private IRcsMatchMarketConfigService IRcsMatchMarketConfigService;
//
//    private RedissonManager redissonManager;
//
//    public SyncTemplateJob(RedissonManager redissonManager) {
//        this.redissonManager = redissonManager;
//    }
//
//        @Scheduled(cron = "0/3 * * * * ?")
//    public void syncFootTemplateParams() {
//        try {
//            log.info("足球定时任务线程名,{}", Thread.currentThread().getName());
//            redissonManager.lock("scan_tournament_template_margin_ref_lock_foot", 30);
//            List<RcsTournamentTemplateComposeModel> list = playMargainRefMapper.selectAllTemplatesByFoot();
//            log.info("同步模板数据到赛事配置任务开始-足球,{}", JSONObject.toJSONString(list));
//            handleTemplateData(list);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        } finally {
//            redissonManager.unlock("scan_tournament_template_margin_ref_lock_foot");
//        }
//    }
//
//    @Scheduled(cron = "0/3 * * * * ?")
//    public void syncBasketTemplateParams() {
//        try {
//            log.info("篮球定时任务线程名,{}", Thread.currentThread().getName());
//            redissonManager.lock("scan_tournament_template_margin_ref_lock_basket", 30);
//            List<RcsTournamentTemplateComposeModel> list = playMargainRefMapper.selectAllTemplatesByBasket();
//            log.info("同步模板数据到赛事配置任务开始-篮球,{}", JSONObject.toJSONString(list));
//            handleTemplateData(list);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        } finally {
//            redissonManager.unlock("scan_tournament_template_margin_ref_lock_basket");
//        }
//    }
//
//    @Scheduled(cron = "0/3 * * * * ?")
//    public void syncTennisTemplateParams() {
//        try {
//            log.info("网球定时任务线程名,{}", Thread.currentThread().getName());
//            redissonManager.lock("scan_tournament_template_margin_ref_lock_tennis", 30);
//            List<RcsTournamentTemplateComposeModel> list  = playMargainRefMapper.selectAllTemplatesByTennis();
//            log.info("同步模板数据到赛事配置任务开始-网球,{}", JSONObject.toJSONString(list));
//            handleTemplateData(list);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        } finally {
//            redissonManager.unlock("scan_tournament_template_margin_ref_lock_tennis");
//        }
//    }
//
//    @Scheduled(cron = "0/3 * * * * ?")
//    public void syncPingPongTemplateParams() {
//        try {
//            log.info("乒乓球定时任务线程名,{}", Thread.currentThread().getName());
//            redissonManager.lock("scan_tournament_template_margin_ref_lock_pingpong", 30);
//            List<RcsTournamentTemplateComposeModel> list  = playMargainRefMapper.selectAllTemplatesByPingPong();
//            log.info("同步模板数据到赛事配置任务开始-乒乓球,{}", JSONObject.toJSONString(list));
//            handleTemplateData(list);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        } finally {
//            redissonManager.unlock("scan_tournament_template_margin_ref_lock_pingpong");
//        }
//    }
//
//    @Scheduled(cron = "0/3 * * * * ?")
//    public void syncVolleyBallTemplateParams() {
//        try {
//            log.info("排球定时任务线程名,{}", Thread.currentThread().getName());
//            redissonManager.lock("scan_tournament_template_margin_ref_lock_volleyball", 30);
//            List<RcsTournamentTemplateComposeModel> list  = playMargainRefMapper.selectAllTemplatesByVolleyBall();
//            log.info("同步模板数据到赛事配置任务开`始-排球,{}", JSONObject.toJSONString(list));
//            handleTemplateData(list);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        } finally {
//            redissonManager.unlock("scan_tournament_template_margin_ref_lock_volleyball");
//        }
//    }
//
//    @Scheduled(cron = "0/3 * * * * ?")
//    public void syncSnookerTemplateParams() {
//        try {
//            log.info("斯诺克定时任务线程名,{}", Thread.currentThread().getName());
//            redissonManager.lock("scan_tournament_template_margin_ref_lock_snooker", 30);
//            List<RcsTournamentTemplateComposeModel> list  = playMargainRefMapper.selectAllTemplatesBySnooker();
//            log.info("同步模板数据到赛事配置任务开始-斯诺克,{}", JSONObject.toJSONString(list));
//            handleTemplateData(list);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        } finally {
//            redissonManager.unlock("scan_tournament_template_margin_ref_lock_snooker");
//        }
//    }
//
//    private void handleTemplateData(List<RcsTournamentTemplateComposeModel> list) {
//        if (!CollectionUtils.isEmpty(list)) {
//            for (RcsTournamentTemplateComposeModel model : list) {
//                try {
//                    IRcsMatchMarketConfigService.insertFromTemplate(model);
//                } catch (Exception e) {
//                    log.error(e.getMessage(), e);
//                }
//            }
//        }
//    }
//
//
//    /**
//     * 扩展兜底  10秒一次，跟定时任务错开处理
//     *
//     * @param 设定文件
//     * @return void    返回类型
//     * @throws
//     * @Title: syncTemplateParams
//     * @Description: TODO
//     * <p>
//     * bug 23014 #【操盘】rain#滚球走早盘的抽水设置     sean建议兜底方案暂时注释
//     */
//    @Scheduled(cron = "11,34,55 * * * * ?")
//    public void syncTemplateParamsAndNoMid() {
//        try {
//            redissonManager.lock("scan_tournament_template_margin_ref_lock", 30);
//            List<RcsTournamentTemplateComposeModel> list = playMargainRefMapper.selectAllTemplatesByNoMid();
//            log.info("兜底-同步模板数据到赛事配置任务开始,{}", JSONObject.toJSONString(list));
//            handleTemplateData(list);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        } finally {
//            redissonManager.unlock("scan_tournament_template_margin_ref_lock");
//        }
//    }
}
