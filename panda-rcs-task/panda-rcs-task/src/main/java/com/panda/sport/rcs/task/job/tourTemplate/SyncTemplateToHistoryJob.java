package com.panda.sport.rcs.task.job.tourTemplate;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplateAcceptConfigMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainMapper;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateAcceptConfig;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;
import com.panda.sport.rcs.task.wrapper.IRcsTournamentTemplateService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author :  carver
 * @Project Name :  panda-rcs-task-group
 * @Package Name :  com.panda.sport.rcs.task.job.tourTemplate
 * @Description :  将结束后的赛事，模板margin移除到历史表
 * @Date: 2020-11-05 14:41
 */
@Component
@Slf4j
@JobHandler(value = "clearTemplateToHistory")
public class SyncTemplateToHistoryJob extends IJobHandler {
    @Autowired
    private IRcsTournamentTemplateService rcsTournamentTemplateService;
    @Autowired
    private RcsTournamentTemplatePlayMargainMapper playMarginMapper;
    @Autowired
    private RcsTournamentTemplateAcceptConfigMapper acceptConfigMapper;


    /**
     * 模板margin移除到历史表
     * 定时任务每小时执行一次
     */
    @Override
    public ReturnT<String> execute(String s) {
    	String linkId = "clearTemplateToHistory";
        try {
            log.info("::{}::-将结束赛事模板margin移除到历史表,任务开始：{}" ,linkId, System.currentTimeMillis());
            List<RcsTournamentTemplatePlayMargain> list = playMarginMapper.searchHistoryMatch();
            if (!CollectionUtils.isEmpty(list)) {
                List<Long> templateIds = list.stream().map(RcsTournamentTemplatePlayMargain::getTemplateId).collect(Collectors.toList());
                log.info("::{}::-将结束赛事模板margin移除到历史表,{}" ,linkId, JSONObject.toJSONString(templateIds));
                for (RcsTournamentTemplatePlayMargain entry : list) {
                    //玩法margin配置
                    try {
                        QueryWrapper<RcsTournamentTemplatePlayMargain> wrapper = new QueryWrapper<>();
                        wrapper.lambda().in(RcsTournamentTemplatePlayMargain::getTemplateId, entry.getTemplateId());
                        List<RcsTournamentTemplatePlayMargain> playMarginList = playMarginMapper.selectList(wrapper);
                        if (!CollectionUtils.isEmpty(playMarginList)) {
                            rcsTournamentTemplateService.syncTemplateToHistory(playMarginList);
                            log.info("::{}::-将结束赛事模板margin移除到历史表完成:templateId:{}" ,linkId, entry.getTemplateId());
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }

                    //滚球玩法集接拒单配置
                    try {
                        if (entry.getMatchType().intValue() == NumberUtils.INTEGER_ZERO) {
                            QueryWrapper<RcsTournamentTemplateAcceptConfig> configWrapper = new QueryWrapper<>();
                            configWrapper.lambda().in(RcsTournamentTemplateAcceptConfig::getTemplateId, entry.getTemplateId());
                            List<RcsTournamentTemplateAcceptConfig> configList = acceptConfigMapper.selectList(configWrapper);
                            if (!CollectionUtils.isEmpty(configList)) {
                                rcsTournamentTemplateService.syncTemplateAcceptEventToHistory(configList);
                                log.info("::{}::-将结束赛事模板接拒单配置移除到历史表完成:templateId:{}" ,linkId, entry.getTemplateId());
                            }
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            log.info("::{}::-将结束赛事模板margin移除到历史表,任务结束：{}" ,linkId, System.currentTimeMillis());
        } catch (Exception e) {
            log.error("::{}::-发生错误：{}" ,linkId,e.getMessage(), e);
        }
        return SUCCESS;
    }
}
