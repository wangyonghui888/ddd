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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author :  koala
 * @Project Name :  panda-rcs-task-group
 * @Package Name :  com.panda.sport.rcs.task.job.tourTemplate
 * @Description :  同步赛事模板兜底操作
 * @Date: 2022-02-22 11:47
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
@JobHandler(value = "templateParamsAndNoMidJob")
public class TemplateParamsAndNoMidJob extends IJobHandler {

    private final RcsTournamentTemplatePlayMargainRefMapper playMargainRefMapper;
    private final CommonRunMethod commonRunMethod;
    private final RedissonManager redissonManager;

    public TemplateParamsAndNoMidJob(RcsTournamentTemplatePlayMargainRefMapper playMargainRefMapper, CommonRunMethod commonRunMethod, RedissonManager redissonManager) {
        this.playMargainRefMapper = playMargainRefMapper;
        this.commonRunMethod = commonRunMethod;
        this.redissonManager = redissonManager;
    }

    @Override
    public ReturnT<String> execute(String s) throws Exception {
    	String linkId = "templateParamsAndNoMidJob";
        try {
        	CommonUtils.mdcPut(linkId);
            if(StringUtils.isBlank(s)){
            	log.info("::{}::-兜底-同步模板数据到赛事配置任务错误,参数不能为空" ,linkId);
                throw new RcsServiceException("兜底-同步模板数据到赛事配置任务发送错误，参数不能为空");
            }else{
                Set<Integer> sportIds =  Arrays.stream(s.trim().split(","))
                        .filter(StringUtils::isNotBlank)
                        .peek(String::trim)
                        .map(Integer::valueOf)
                        .collect(Collectors.toSet());
                log.info("::{}::-开始兜底-同步模板数据到赛事配置任务,线程信息:{},开始时间:{},赛种id:{}" ,linkId, Thread.currentThread().getName(), DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"),s);
                redissonManager.lock("scan_tournament_template_margin_ref_lock", 30);
                List<RcsTournamentTemplateComposeModel> list = playMargainRefMapper.selectAllTemplatesByNoMid(sportIds);
                commonRunMethod.handleTemplateData(linkId,list);
                log.info("::{}::-结束兜底-同步模板数据到赛事配置任务,任务参数:{},结束时间:{},赛种id:{}" ,linkId,JSONObject.toJSONString(list), DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"),s);
            }
        } catch (Exception e) {
        	log.info("::{}::-兜底-同步模板数据到赛事配置任务错误:{}" ,linkId, e.getMessage(),e);
            throw new RcsServiceException("兜底-同步模板数据到赛事配置任务发送错误:"+e);
        } finally {
            redissonManager.unlock("scan_tournament_template_margin_ref_lock");
        }
        return SUCCESS;
    }
}
