package com.panda.sport.rcs.task.job.danger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.TyRiskTournament;
import com.panda.sport.rcs.pojo.danger.RcsDangerTournament;
import com.panda.sport.rcs.task.job.danger.entity.BigDataResponseVo;
import com.panda.sport.rcs.task.job.danger.entity.QueryCommParams;
import com.panda.sport.rcs.task.mq.bean.RcsDangerTournamentMessage;
import com.panda.sport.rcs.task.utils.HttpUtils;
import com.panda.sport.rcs.task.wrapper.TyRiskTournamentService;
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
 * 用户组定时任务入库 每天凌晨0点15分执行
 */
@Slf4j
@Component
@JobHandler(value = "dangerTournamentJobHandler")
public class DangerTournamentJobHandler extends IJobHandler {

    @Value("${user.portrait.risk.http.url.prefix}")
    String urlPrefix;

    @Value("${user.portrait.http.appId}")
    String appId;

    @Value("${rcs.danger.data.db.status:false}")
    private boolean dangerStatus;

    @Resource
    private TyRiskTournamentService riskTournamentService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private ProducerSendMessageUtils sendMessage;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        log.info("--------------危险联赛数据同步任务开始--------------");
        try {
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("pageSize", QueryCommParams.pageSize);
            requestParams.put("page", QueryCommParams.page);
            log.info("::危险联赛同步请求参数={}", JSON.toJSONString(requestParams));
            String result = HttpUtils.post(urlPrefix.concat(QueryCommParams.DANGER_TOURNAMENT_REQUEST_URL), JSON.toJSONString(requestParams), appId);
            log.info("危险联赛返回值打印："+result);
            if (StringUtils.isNotEmpty(result)) {
                BigDataResponseVo<TyRiskTournament> dangerTournamentResponse = JSONObject.parseObject(result, new TypeReference<BigDataResponseVo<TyRiskTournament>>() {
                });

                if (dangerTournamentResponse.getCode().equals(QueryCommParams.SUCCESS_CODE) && dangerTournamentResponse.getData() != null && dangerTournamentResponse.getData().size() > 0) {
                    List<RcsDangerTournament> dangerTournamentVos = new ArrayList<>();
                    dangerTournamentResponse.getData().forEach(t -> {
                        String key = QueryCommParams.DANGER_TOURNAMENT_KEY + t.getId();
                        RcsDangerTournamentMessage dangerTournament = new RcsDangerTournamentMessage();
                        dangerTournament.setCreateTime(System.currentTimeMillis());
                        dangerTournament.setTournamentId(Long.parseLong(t.getId()));
                        dangerTournament.setRiskLevel(t.getRiskLevel());
                        dangerTournament.setStatus(t.getStatus());
                        dangerTournament.setType("sync");
                        log.info("::危险联赛::联赛Id={}::危险等级={}::当前状态={}", t.getId(), t.getRiskLevel(), t.getStatus());
                        dangerTournamentVos.add(dangerTournament);
                        redisClient.set(key, JSON.toJSONString(dangerTournament));
                        redisClient.expireKey(key, QueryCommParams.REDIS_EXPIRE_TIME);
                        sendMessage.sendMessage("Danger_Tournament_Data", "", String.valueOf(dangerTournament.getTournamentId()), dangerTournament);

                    });

                    if(dangerStatus){
                        if (!CollectionUtils.isEmpty(dangerTournamentVos) && dangerTournamentVos.size() > 0) {
                            riskTournamentService.saveBatch(dangerTournamentVos);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("::危险联赛数据同步异常", ex);
            XxlJobLogger.log(ex.getMessage(), ex);
        }
        log.info("--------------危险联赛数据同步任务执行完成--------------");
        return ReturnT.SUCCESS;
    }

}
