package com.panda.sport.rcs.task.job.danger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.pojo.RiskUserGroup;
import com.panda.sport.rcs.pojo.danger.RcsDangerUser;
import com.panda.sport.rcs.task.job.danger.entity.BigDataResponseVo;
import com.panda.sport.rcs.task.job.danger.entity.QueryCommParams;
import com.panda.sport.rcs.task.utils.HttpUtils;
import com.panda.sport.rcs.task.wrapper.RiskUserGroupService;
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
 * 用户组定时任务入库 每天凌晨0点15分执行 全部数据
 */
@Slf4j
@Component
@JobHandler(value = "dangerPlayerGroupJobHandler")
public class DangerPlayerGroupJobHandler extends IJobHandler {

    @Value("${user.portrait.risk.http.url.prefix}")
    String urlPrefix;
//    String urlPrefix = "http://uat-bigdata-auth-gateway.sportxxxr1pub.com/risk";

    @Value("${user.portrait.http.appId}")
    String appId;

    @Value("${rcs.danger.data.db.status:false}")
    private boolean dangerStatus;

    @Resource
    private RiskUserGroupService riskUserGroupService;

    @Autowired
    private RedisClient redisClient;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        log.info("--------------用户玩家组数据同步任务开始--------------");
        try {
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("page", QueryCommParams.page);
            requestParams.put("pageSize", QueryCommParams.pageSize);
            requestParams.put("orderBy", QueryCommParams.ORDER_BY);
            requestParams.put("order", QueryCommParams.ORDER);
            log.info("::危险玩家组数据同步请求参数={}", JSON.toJSONString(requestParams));
            String result = HttpUtils.post(urlPrefix.concat(QueryCommParams.DANGER_USER_GROUP_REQUEST_URL), JSON.toJSONString(requestParams), appId);
            if (StringUtils.isNotEmpty(result)) {
                BigDataResponseVo<RiskUserGroup> dataResponseVo = JSONObject.parseObject(result, new TypeReference<BigDataResponseVo<RiskUserGroup>>() {
                });
                if (dataResponseVo.getCode().equals(QueryCommParams.SUCCESS_CODE) && dataResponseVo.getData() != null && dataResponseVo.getData().size() > 0) {
                    List<RcsDangerUser> dangerUsers = new ArrayList<>();
                    for (RiskUserGroup u : dataResponseVo.getData()){
                        log.info("::玩家组数据信息::玩家组Id={}::::玩家组级别={}", u.getId(), u.getDangerLevel());
                        Map<String, Long> userRequestMap = new HashMap<>();
                        userRequestMap.put("id", u.getId());
                        log.info("::危险玩家组-用户玩家组数据同步请求参数={}", JSON.toJSONString(userRequestMap));
                        String userResult = HttpUtils.post(urlPrefix.concat(QueryCommParams.DANGER_USER_LINK_GROUP_REQUEST_URL), JSON.toJSONString(userRequestMap), appId);
                        if (StringUtils.isNotEmpty(userResult)) {
                            BigDataResponseVo<JSONObject> userResponse = JSONObject.parseObject(userResult, new TypeReference<BigDataResponseVo<JSONObject>>() {
                            });
                            if (userResponse.getCode().equals(QueryCommParams.SUCCESS_CODE) && userResponse.getData() != null && userResponse.getData().size() > 0) {
                                userResponse.getData().forEach(o -> {
                                    Long uid = o.getLong("uid");

                                    String key = QueryCommParams.DANGER_PLAY_GROUP_KEY + uid;
                                    redisClient.set(key, u.getDangerLevel());
                                    redisClient.expireKey(key, QueryCommParams.REDIS_EXPIRE_TIME);

                                    RcsDangerUser user = new RcsDangerUser();
                                    user.setUserId(uid);
                                    user.setRiskLevel(u.getDangerLevel());
                                    user.setUserGroupId(u.getId());
                                    user.setCreateTime(System.currentTimeMillis());
                                    log.info("::危险玩家组-用户玩家组::用户Id={}::::玩家组Id={}::::玩家组级别={}", uid, u.getId(), u.getDangerLevel());
                                    dangerUsers.add(user);
                                });
                            }
                        }
                    }

                    if(dangerStatus){
                        if (!CollectionUtils.isEmpty(dangerUsers) && dangerUsers.size() > 0) {
                            riskUserGroupService.saveBatch(dangerUsers);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("::用户玩家组数据同步异常", ex);
            XxlJobLogger.log(ex.getMessage(), ex);
        }
        log.info("--------------用户玩家组数据同步任务开完成--------------");
        return ReturnT.SUCCESS;
    }

}
