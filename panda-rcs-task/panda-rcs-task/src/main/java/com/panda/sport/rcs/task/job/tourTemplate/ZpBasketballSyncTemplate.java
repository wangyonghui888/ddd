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
@JobHandler(value = "zpBasketballSyncTemplateJobHandler")
public class ZpBasketballSyncTemplate extends IJobHandler {
    private final RcsTournamentTemplatePlayMargainRefMapper playMargainRefMapper;
    private final RedissonManager redissonManager;

    private static String REDIS_LOCK_KEY  ="improve_football_sync_template_lock";
    //早盘
    private static String redis_data_key_zp ="improve_football_sync_template_data_zp";

    private final ImproveBasketballSyncTemplate improveBasketballSyncTemplate;

    public ZpBasketballSyncTemplate(RcsTournamentTemplatePlayMargainRefMapper playMargainRefMapper,RedissonManager redissonManager,
                                      ImproveBasketballSyncTemplate improveBasketballSyncTemplate) {
        this.playMargainRefMapper = playMargainRefMapper;
        this.redissonManager = redissonManager;
        this.improveBasketballSyncTemplate = improveBasketballSyncTemplate;
    }

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        String linkId = "zpFootballSyncTemplateJobHandler"+ DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss");
        try {
            log.info("::{}::-开始同步足球早盘赛事模板", linkId);
            redissonManager.lock(REDIS_LOCK_KEY, 30);
            List<RcsTournamentTemplateComposeModel> zpList = playMargainRefMapper.selectAllZpTemplatesByBasket();
            improveBasketballSyncTemplate.syncTemplate(zpList,linkId,redis_data_key_zp);
        }catch (Exception e) {
            log.error("::{}::-执行足球早盘定时任务同步模板错误:{}",linkId, e.getMessage(),e);
        } finally {
            redissonManager.unlock(REDIS_LOCK_KEY);
        }
        return SUCCESS;
    }
}
