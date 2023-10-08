package com.panda.rcs.cleanup.job;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.panda.rcs.cleanup.dto.ClearRedisHistoricalDataReqDto;
import com.panda.rcs.cleanup.mapper.MatchMapper;
import com.panda.rcs.cleanup.service.MatchService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Project Name : panda-rcs-framework
 * @Package Name : panda-rcs-framework
 * @Description : 清除Redis历史数据
 * @Author : Paca
 * @Date : 2022-03-08 21:41
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Component
@JobHandler(value = "clearRedisHistoricalDataJob")
public class ClearRedisHistoricalDataJob extends IJobHandler {

    @Autowired
    private MatchMapper matchMapper;
    @Autowired
    private MatchService matchService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("清除Redis历史数据：" + param);
        log.info("清除Redis历史数据：" + param);
        if (StringUtils.isBlank(param)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "入参为空");
        }
        ClearRedisHistoricalDataReqDto reqDto = JSON.parseObject(param, ClearRedisHistoricalDataReqDto.class);
        Long matchIdStart = reqDto.getMatchIdStart();
        Long matchIdEnd = reqDto.getMatchIdEnd();
        Integer clearType = reqDto.getClearType();
        for (long i = matchIdStart; i <= matchIdEnd; i = i + 500) {
            long end = i + 499;
            if (end > matchIdEnd) {
                end = matchIdEnd;
            }
            handle(i, end, clearType);
        }
        return ReturnT.SUCCESS;
    }

    private void handle(long matchIdStart, long matchIdEnd, int clearType) {
        List<Long> matchIds = matchMapper.selectMatchIds(matchIdStart, matchIdEnd);
        XxlJobLogger.log("查询范围内的赛事：matchIdStart={},matchIdEnd={},matchIds={}", matchIdStart, matchIdEnd, JSON.toJSONString(matchIds));
        log.info("查询范围内的赛事：matchIdStart={},matchIdEnd={},matchIds={}", matchIdStart, matchIdEnd, JSON.toJSONString(matchIds));
        if (CollectionUtils.isEmpty(matchIds)) {
            matchIds = Lists.newArrayList();
        }
        for (Long matchId = matchIdStart; matchId <= matchIdEnd; matchId++) {
            if (matchIds.contains(matchId)) {
                // 库中存在的赛事不清除
                continue;
            }
            matchService.clearRedisByMatchId(matchId, clearType);
        }
    }

}
