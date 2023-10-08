package com.panda.sport.rcs.task.job.tourTemplate;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.exeception.RcsServiceException;
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
 * @author :  koala
 * @Project Name :  panda-rcs-task
 * @Package Name :  com.panda.sport.rcs.task.job.tourTemplate
 * @Description :  网球同步模板定时任务
 * @Date: 2022-02-16 11:16
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
@JobHandler(value = "tennisSyncTemplateJobHandler")
public class TennisSyncTemplateJob extends IJobHandler {
    private final RcsTournamentTemplatePlayMargainRefMapper playMargainRefMapper;
    private final CommonRunMethod commonRunMethod;
    private final RedissonManager redissonManager;

    public TennisSyncTemplateJob(RcsTournamentTemplatePlayMargainRefMapper playMargainRefMapper, CommonRunMethod commonRunMethod, RedissonManager redissonManager) {
        this.playMargainRefMapper = playMargainRefMapper;
        this.commonRunMethod = commonRunMethod;
        this.redissonManager = redissonManager;
    }

    @Override
    public ReturnT<String> execute(String s) {
    	String linkId = "tennisSyncTemplateJobHandler";
        try {
        	CommonUtils.mdcPut(linkId);
        	log.info("::{}::-开始同步网球赛事模板,开始时间:{}" ,linkId, DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
            redissonManager.lock("scan_tournament_template_margin_ref_lock_tennis", 30);
            List<RcsTournamentTemplateComposeModel> list = playMargainRefMapper.selectAllTemplatesByTennis();
            commonRunMethod.handleTemplateData(linkId,list);
            log.info("::{}::-结束同步网球赛事模板,任务参数:{},结束时间:{}" ,linkId, JSONObject.toJSONString(list), DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            log.error("::{}::-执行网球定时任务同步模板错误:{}" ,linkId, e.getMessage(),e);
            throw new RcsServiceException("执行网球定时任务同步模板错误:"+e);
        } finally {
            redissonManager.unlock("scan_tournament_template_margin_ref_lock_tennis");
        }

        return SUCCESS;
    }
}
