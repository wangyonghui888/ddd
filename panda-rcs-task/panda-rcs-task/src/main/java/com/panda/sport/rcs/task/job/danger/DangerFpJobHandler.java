package com.panda.sport.rcs.task.job.danger;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.pojo.RiskFp;
import com.panda.sport.rcs.pojo.danger.RcsDangerFp;
import com.panda.sport.rcs.pojo.dto.RiskFpListReqDTO;
import com.panda.sport.rcs.task.job.danger.entity.BigDataResponseVo;
import com.panda.sport.rcs.task.job.danger.entity.QueryCommParams;
import com.panda.sport.rcs.task.utils.HttpUtils;
import com.panda.sport.rcs.task.wrapper.RiskFpService;
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
 * 危险指纹池定时任务入库 每天凌晨0点15分执行
 */
@Slf4j
@Component
@JobHandler(value = "dangerFpJobHandler")
public class DangerFpJobHandler extends IJobHandler {

    @Value("${user.portrait.risk.http.url.prefix}")
    String urlPrefix;

    @Value("${user.portrait.http.appId}")
    String appId;

    @Value("${rcs.danger.data.db.status:false}")
    private boolean dangerStatus;

    @Resource
    private RiskFpService riskFpService;

    @Autowired
    private RedisClient redisClient;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        log.info("--------------危险指纹池数据同步任务开始--------------");
        try {
            RiskFpListReqDTO vo = new RiskFpListReqDTO();
            vo.setPage(QueryCommParams.page);
            vo.setPageSize(QueryCommParams.pageSize);
            Date endOfDay = DateUtil.endOfDay(DateUtil.date());
            Date beginOfDay = DateUtil.beginOfDay(DateUtil.yesterday());
            vo.setEndTime(DateUtil.formatDateTime(endOfDay));
            vo.setQueryType(1);
            if(StringUtils.isNotEmpty(s)){
                vo.setStartTime(s);
            } else {
                vo.setStartTime(DateUtil.formatDateTime(beginOfDay));
            }
            log.info("::危险指纹池同步请求参数={}", JSON.toJSONString(vo));

            String result = HttpUtils.post(urlPrefix.concat(QueryCommParams.DANGER_FP_REQUEST_URL), JSON.toJSONString(vo), appId);
            if (StringUtils.isNotEmpty(result)) {
                BigDataResponseVo<RiskFp> dangerFpResponse = JSONObject.parseObject(result, new TypeReference<BigDataResponseVo<RiskFp>>() {
                });

                List<RcsDangerFp> dangerFps = new ArrayList<>();
                if (dangerFpResponse.getCode().equals(QueryCommParams.SUCCESS_CODE) && dangerFpResponse.getData() != null && dangerFpResponse.getData().size() > 0) {
                    dangerFpResponse.getData().forEach(fp -> {
                        log.info("::{}::同步最新危险指纹数据", fp.getFingerprintId());
                        String key = QueryCommParams.DANGER_FP_KEY + fp.getFingerprintId();
                        redisClient.set(key, fp.getRiskLevel());
                        redisClient.expireKey(key, QueryCommParams.REDIS_EXPIRE_TIME);

                        RcsDangerFp rcsDangerFp = new RcsDangerFp();
                        rcsDangerFp.setFpId(fp.getFingerprintId());
                        rcsDangerFp.setFpLevel(Integer.parseInt(fp.getRiskLevel()));
                        rcsDangerFp.setCreateTime(System.currentTimeMillis());

                        dangerFps.add(rcsDangerFp);
                    });

                    if(dangerStatus){
                        if (!CollectionUtils.isEmpty(dangerFps) && dangerFps.size() > 0) {
                            riskFpService.saveBatch(dangerFps);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("riskFpList job 异常", ex);
            XxlJobLogger.log(ex.getMessage(), ex);
        }
        log.info("--------------危险指纹池数据同步任务完成--------------");
        return ReturnT.SUCCESS;
    }
}
