package com.panda.sport.rcs.console.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import com.panda.sport.rcs.console.pojo.ExcelVO;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.panda.sport.rcs.console.dao.RcsMatchConfigLogsmapper;
import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.console.service.RcsMatchConfigLogsService;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.console.service.impl
 * @Description :  TODO
 * @Date: 2020-02-10 15:55
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class RcsMatchConfigLogsServiceImpl implements RcsMatchConfigLogsService {
    @Autowired
    private RcsMatchConfigLogsmapper rcsMatchConfigLogsmapper;

    @Override
    public PageDataResult getStatusList(Integer matchId, Integer pageNum, Integer pageSize) throws ParseException {
        PageDataResult pageDataResult = new PageDataResult();
        PageHelper.startPage(pageNum, pageSize);
        List<Map> rcsMatchMarketConfigLogs = rcsMatchConfigLogsmapper.selectById(matchId);
        if (CollectionUtils.isEmpty(rcsMatchMarketConfigLogs)) {
            PageHelper.startPage(1, pageSize);
            rcsMatchMarketConfigLogs = rcsMatchConfigLogsmapper.selectById(matchId);
        }
        if (!CollectionUtils.isEmpty(rcsMatchMarketConfigLogs)) {
            for (Map<String, Object> rcsMatchMarketConfigLogs1 : rcsMatchMarketConfigLogs) {
                for (Map.Entry<String, Object> entry : rcsMatchMarketConfigLogs1.entrySet()) {
                    if (entry.getKey().equals("modify_time")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = sdf.parse(entry.getValue().toString());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.add(Calendar.HOUR_OF_DAY, -14);
                        String format = sdf.format(calendar.getTime());
                        entry.setValue(format);
                    } else if (entry.getKey().equals("data_source")) {
                        String s = entry.getValue().toString();
                        if ("0".equals(s)) {
                            entry.setValue("1");
                        } else {
                            entry.setValue("0");
                        }
                    } else if (entry.getKey().equals("market_status")) {
                        String s = entry.getValue().toString();
                        if ("0".equals(s)) {
                            entry.setValue("11");
                        }
                        if ("1".equals(s)) {
                            entry.setValue("0");
                        }
                        if ("3".equals(s)) {
                            entry.setValue("1");
                        }
                    } else {
                        entry.setValue(entry.getValue().toString());
                    }
                }
                rcsMatchMarketConfigLogs1.put("change_level", 1);
            }
        }
        if (rcsMatchMarketConfigLogs.size() != 0) {
            PageInfo<Map> pageInfo = new PageInfo<>(rcsMatchMarketConfigLogs, pageNum);
            pageDataResult.setList(pageInfo.getList());
            pageDataResult.setTotals((int) pageInfo.getTotal());
        }
        return pageDataResult;
    }

    @Override
    public PageDataResult queryTradeLogList(Map<String, Object> params) {
        int pageNum = Integer.parseInt(String.valueOf(params.get("pageNum")));
        int pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
        PageDataResult pageDataResult = new PageDataResult();

        if (!params.containsKey("matchId") && !params.containsKey("marketId")) {
            return pageDataResult;
        }

        if (params.containsKey("marketId")) {
            Map<String, Object> info = rcsMatchConfigLogsmapper.queryMarketInfo(params);
            if (info == null) return pageDataResult;

            params.put("matchId", info.get("matchId"));
            params.put("playId", info.get("playId"));
        }

        if (!params.containsKey("marketId")) params.put("marketId", "");
        if (!params.containsKey("playId")) params.put("playId", "");

        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> list = rcsMatchConfigLogsmapper.queryTradeList(params);
        if (list != null && list.size() > 0) {
            PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list, pageNum);
            pageDataResult.setList(pageInfo.getList());
            pageDataResult.setTotals((int) pageInfo.getTotal());
        }

        return pageDataResult;
    }

    @Autowired
    private RedisClient redisClient;

    @Override
    @Async("asyncServiceExecutor")
    public void insertUserConfig(List<ExcelVO> collect, CountDownLatch countDownLatch) {
        try {
            if(!CollectionUtils.isEmpty(collect)){
                rcsMatchConfigLogsmapper.batchAddOrUpdateUserConfig(collect);
            }
        } finally {
            countDownLatch.countDown();
        }
    }

    @Override
    public List<String> getUserBetRate() {
        return rcsMatchConfigLogsmapper.getUserBetRate();
    }

    @Override
    public int deleteUserBetRate() {
        return rcsMatchConfigLogsmapper.deleteUserBetRate();
    }
}
