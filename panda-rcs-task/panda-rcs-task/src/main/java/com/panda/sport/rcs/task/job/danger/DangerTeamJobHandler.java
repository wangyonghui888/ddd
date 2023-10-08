package com.panda.sport.rcs.task.job.danger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.sport.rcs.core.cache.client.RedisClient;

import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.task.job.danger.entity.BigDataResponseVo;
import com.panda.sport.rcs.pojo.danger.RcsDangerTeam;
import com.panda.sport.rcs.task.job.danger.entity.QueryCommParams;
import com.panda.sport.rcs.task.mq.bean.RcsDangerTeamMessage;
import com.panda.sport.rcs.task.utils.HttpUtils;
import com.panda.sport.rcs.task.wrapper.TyRiskTeamService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 危险球队 定时任务入库 每天凌晨0点15分执行-全部数据id过滤
 */
@Slf4j
@Component
@JobHandler(value = "dangerTeamJobHandler")
public class DangerTeamJobHandler extends IJobHandler {

    @Value("${user.portrait.risk.http.url.prefix}")
    String urlPrefix;

    @Value("${user.portrait.http.appId}")
    String appId;

    @Value("${rcs.danger.data.db.status:false}")
    private boolean dangerStatus;

    @Resource
    private TyRiskTeamService riskTeamService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private ProducerSendMessageUtils sendMessage;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        log.info("--------------危险球队数据同步任务开始--------------");
        try {
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("pageSize", QueryCommParams.pageSize);
            requestParams.put("page", QueryCommParams.page);
            log.info("::危险球队同步请求参数={}", JSON.toJSONString(requestParams));
            String url = urlPrefix.concat(QueryCommParams.DANGER_TEAM_REQUEST_URL);
            String result = HttpUtils.post(url, JSON.toJSONString(requestParams), appId);
            log.info("危险球队返回值打印："+result);
            if (StringUtils.isNotEmpty(result)) {
                BigDataResponseVo<RcsDangerTeam> dangerTeamResponse = JSONObject.parseObject(result, new TypeReference<BigDataResponseVo<RcsDangerTeam>>() {
                });
                if (dangerTeamResponse.getCode().equals(QueryCommParams.SUCCESS_CODE) && dangerTeamResponse.getData() != null && dangerTeamResponse.getData().size() > 0) {
                    List<RcsDangerTeam> dangerTeamVos = new ArrayList<>();
                    dangerTeamResponse.getData().forEach(team -> {
                        String key = QueryCommParams.DANGER_TEAM_KEY + team.getTeamId();
                        RcsDangerTeamMessage teamVo = new RcsDangerTeamMessage();
                        teamVo.setRiskLevel(team.getRiskLevel());
                        teamVo.setStatus(team.getStatus());
                        teamVo.setTeamId(team.getTeamId());
                        teamVo.setCreateTime(System.currentTimeMillis());
                        teamVo.setType("sync");
                        log.info("::危险球队::球队Id={}::危险等级={}::当前状态={}", team.getTeamId(), team.getRiskLevel(), team.getStatus());
                        dangerTeamVos.add(teamVo);
                        redisClient.set(key, JSON.toJSONString(teamVo));
                        redisClient.expireKey(key, QueryCommParams.REDIS_EXPIRE_TIME);
                        sendMessage.sendMessage("Danger_Team_Data", "sync", String.valueOf(teamVo.getTeamId()), teamVo);
                    });

                    if(dangerStatus){
                        if (!CollectionUtils.isEmpty(dangerTeamVos) && dangerTeamVos.size() > 0) {
                            riskTeamService.saveBatch(dangerTeamVos);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("::危险球队数据同步异常", ex);
            XxlJobLogger.log(ex.getMessage(), ex);
        }
        log.info("--------------危险球队数据同步任务执行完成--------------");
        return ReturnT.SUCCESS;
    }

}
