package com.panda.sport.rcs.task.job.tourTemplate;

import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainRefMapper;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateComposeModel;
import com.panda.sport.rcs.task.config.RedissonManager;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
@JobHandler(value = "liveFootballSyncTemplateJobHandler")
public class LiveFootballSyncTemplateJob extends IJobHandler {

    private final RcsTournamentTemplatePlayMargainRefMapper playMargainRefMapper;
    private final RedissonManager redissonManager;

    private static String REDIS_LOCK_KEY  ="improve_football_sync_template_lock";

    //滚球
    private static String redis_data_key_live ="improve_football_sync_template_data_live";

    private final ImproveFootballSyncTemplate improveFootballSyncTemplate;

    public LiveFootballSyncTemplateJob(RcsTournamentTemplatePlayMargainRefMapper playMargainRefMapper,RedissonManager redissonManager,
         ImproveFootballSyncTemplate improveFootballSyncTemplate) {
        this.playMargainRefMapper = playMargainRefMapper;
        this.redissonManager = redissonManager;
        this.improveFootballSyncTemplate = improveFootballSyncTemplate;
    }

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        String linkId = "liveFootballSyncTemplateJobHandler"+ DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss");
        try {
            log.info("::{}::-开始同步足球滚球赛事模板", linkId);
            redissonManager.lock(REDIS_LOCK_KEY, 30);
            List<RcsTournamentTemplateComposeModel> liveList = playMargainRefMapper.selectAllLiveTemplatesByFoot();
            improveFootballSyncTemplate.syncTemplate(liveList,linkId,redis_data_key_live);
        }catch (Exception e) {
            log.error("::{}::-执行足球滚球定时任务同步模板错误:{}",linkId, e.getMessage(),e);
        } finally {
            redissonManager.unlock(REDIS_LOCK_KEY);
        }
        return SUCCESS;
    }
}
