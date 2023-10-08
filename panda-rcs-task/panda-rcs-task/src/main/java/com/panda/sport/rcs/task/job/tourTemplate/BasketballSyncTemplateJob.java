package com.panda.sport.rcs.task.job.tourTemplate;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainRefMapper;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateComposeModel;
import com.panda.sport.rcs.task.config.RedissonManager;
import com.panda.sport.rcs.utils.CommonUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author :  koala
 * @Project Name :  panda-rcs-task
 * @Package Name :  com.panda.sport.rcs.task.job.tourTemplate
 * @Description :  篮球同步模板
 * @Date: 2022-02-16 10:35
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
@JobHandler(value = "basketballSyncTemplateJobHandler")
public class BasketballSyncTemplateJob extends IJobHandler {


    /**
     * 指定事件驱动的篮球赛事IDs,现有xxlJob将不对指定赛事同步节点
     */
    private final RcsTournamentTemplatePlayMargainRefMapper playMargainRefMapper;
    private final CommonRunMethod commonRunMethod;
    private final RedissonManager redissonManager;

    @Value("${task.ref.basketballSyncJob:true}")
    private boolean basketballSyncJob;
    public BasketballSyncTemplateJob(RcsTournamentTemplatePlayMargainRefMapper playMargainRefMapper, CommonRunMethod commonRunMethod, RedissonManager redissonManager) {
        this.playMargainRefMapper = playMargainRefMapper;
        this.commonRunMethod = commonRunMethod;
        this.redissonManager = redissonManager;
    }

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        if (basketballSyncJob) {
            String linkId = "basketballSyncTemplateJobHandler";
            try {
                CommonUtils.mdcPut(linkId);
                log.info("::{}::-开始同步篮球赛事模板,开始时间:{}", linkId, DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                redissonManager.lock("scan_tournament_template_margin_ref_lock_basket", 30);
                List<RcsTournamentTemplateComposeModel> list = playMargainRefMapper.selectAllTemplatesByBasket();
                commonRunMethod.handleTemplateData(linkId,list);
                log.info("::{}::-结束同步篮球赛事模板,任务参数:{},结束时间:{}", linkId, JSONObject.toJSONString(list), DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e) {
                log.error("::{}::-执行篮球定时任务同步模板错误:{}", linkId, e.getMessage(), e);
            } finally {
                redissonManager.unlock("scan_tournament_template_margin_ref_lock_basket");
            }
        }else{
            log.info("BasketballSyncTemplateJob.execute basketballSyncJob:false");
        }
        return SUCCESS;
    }


}
