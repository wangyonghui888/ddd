package com.panda.sport.rcs.task.job.tourTemplate;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainRefMapper;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateComposeModel;
import com.panda.sport.rcs.task.config.RedissonManager;
import com.panda.sport.rcs.utils.CommonUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author :
 * @Project Name :  panda-rcs-task
 * @Package Name :  com.panda.sport.rcs.task.job.tourTemplate
 * @Description :  羽毛球同步模板
 * @Date: 2022-02-16 10:35
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
@JobHandler(value = "badmintonSyncTemplateJobHandler")
public class BadmintonTemplateJob extends IJobHandler {
    private final RcsTournamentTemplatePlayMargainRefMapper playMargainRefMapper;
    private final CommonRunMethod commonRunMethod;
    private final RedissonManager redissonManager;

    public BadmintonTemplateJob(RcsTournamentTemplatePlayMargainRefMapper playMargainRefMapper, CommonRunMethod commonRunMethod, RedissonManager redissonManager) {
        this.playMargainRefMapper = playMargainRefMapper;
        this.commonRunMethod = commonRunMethod;
        this.redissonManager = redissonManager;
    }


    @Override
    public ReturnT<String> execute(String s) throws Exception {
    	String linkId = "badmintonSyncTemplateJobHandler";
        try {
        	CommonUtils.mdcPut(linkId);
        	log.info("::{}::-badmintonSyncTemplateJobHandler，开始同步羽毛球赛事模板,开始时间:{}" ,linkId, DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
            redissonManager.lock("scan_tournament_template_margin_ref_lock_badminton", 30);
            List<RcsTournamentTemplateComposeModel> list = playMargainRefMapper.selectAllTemplatesByBadminton();
            commonRunMethod.handleTemplateData(linkId,list);
        	log.info("::{}::-badmintonSyncTemplateJobHandler，结束同步羽毛球赛事模板,任务参数:{},结束时间:{}" ,linkId, JSONObject.toJSONString(list), DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            log.error("::{}::-执行羽毛球定时任务同步模板错误:{}",linkId, e.getMessage(), e);
            return new ReturnT<>(500, e.getMessage());
        } finally {
            redissonManager.unlock("scan_tournament_template_margin_ref_lock_badminton");
        }
        return SUCCESS;
    }
}
