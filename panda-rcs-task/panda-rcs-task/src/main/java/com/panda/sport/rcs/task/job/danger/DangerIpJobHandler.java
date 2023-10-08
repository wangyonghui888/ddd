package com.panda.sport.rcs.task.job.danger;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.pojo.danger.RcsDangerIp;
import com.panda.sport.rcs.pojo.dto.RiskFpListReqDTO;
import com.panda.sport.rcs.task.job.danger.entity.BigDataResponseVo;
import com.panda.sport.rcs.task.job.danger.entity.QueryCommParams;
import com.panda.sport.rcs.task.utils.HttpUtils;
import com.panda.sport.rcs.task.wrapper.UserIpRiskService;
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
import java.util.Date;
import java.util.List;

/**
 * 危险IP统计表 定时任务入库 每天凌晨0点15分执行
 */
@Slf4j
@Component
@JobHandler(value = "dangerIpJobHandler")
public class DangerIpJobHandler extends IJobHandler {

    @Value("${user.portrait.risk.http.url.prefix}")
    String urlPrefix;

    @Value("${user.portrait.http.appId}")
    String appId;

    @Value("${rcs.danger.data.db.status:false}")
    private boolean dangerStatus;

    @Resource
    private UserIpRiskService userIpRiskService;

    @Autowired
    private RedisClient redisClient;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        log.info("--------------危险Ip数据同步任务开始--------------");
        try {
            RiskFpListReqDTO vo = new RiskFpListReqDTO();
            Date beginOfDay = DateUtil.beginOfDay(DateUtil.yesterday());
            Date endOfDay = DateUtil.endOfDay(DateUtil.date());
            if(StringUtils.isNotEmpty(s)){
                vo.setStartTime(s);
            } else {
                vo.setStartTime(DateUtil.formatDateTime(beginOfDay));
            }
            vo.setEndTime(DateUtil.formatDateTime(endOfDay));
            vo.setPage(QueryCommParams.page);
            vo.setPageSize(QueryCommParams.pageSize);
            vo.setQueryType(1);
            String url = urlPrefix.concat(QueryCommParams.DANGER_IP_REQUEST_URL);
            log.info("::危险Ip同步请求参数={}", JSON.toJSONString(vo));

            String result = HttpUtils.post(url, JSON.toJSONString(vo), appId);
            if (StringUtils.isNotEmpty(result)) {
                BigDataResponseVo<JSONObject> dangerIp = JSONObject.parseObject(result, new TypeReference<BigDataResponseVo<JSONObject>>() {
                });

                List<RcsDangerIp> dangerIps = new ArrayList<>();
                if(dangerIp.getCode().equals(QueryCommParams.SUCCESS_CODE) && dangerIp.getData() != null && dangerIp.getData().size() > 0){
                    dangerIp.getData().forEach(ip -> {
                        log.info("::{}::同步最新危险Ip数据", ip.getString("ip"));
                        String key = QueryCommParams.DANGER_IP_KEY + ip.getString("ip");
                        redisClient.set(key, System.currentTimeMillis());
                        redisClient.expireKey(key, QueryCommParams.REDIS_EXPIRE_TIME);

                        RcsDangerIp dangerIpVo = new RcsDangerIp();
                        dangerIpVo.setDangerIp(ip.getString("ip"));
                        dangerIpVo.setCreateTime(System.currentTimeMillis());
                        dangerIps.add(dangerIpVo);
                    });

                    if(dangerStatus){
                        if (!CollectionUtils.isEmpty(dangerIps) && dangerIps.size() > 0) {
                            userIpRiskService.saveBatch(dangerIps);
                        }
                    }
                }

            }
        } catch (Exception ex) {
            log.error("::危险Ip数据同步异常", ex);
            XxlJobLogger.log(ex.getMessage(), ex);
        }
        log.info("---------------危险Ip数据同步任务完成--------------");
        return ReturnT.SUCCESS;
    }

}
