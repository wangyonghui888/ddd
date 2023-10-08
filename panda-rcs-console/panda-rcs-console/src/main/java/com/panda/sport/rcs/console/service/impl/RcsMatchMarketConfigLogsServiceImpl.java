package com.panda.sport.rcs.console.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.panda.sport.rcs.console.dao.RcsMatchMarketConfigLogsMapper;
import com.panda.sport.rcs.console.dto.MatchFlowingDTO;
import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.console.service.RcsMatchMarketConfigLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
public class RcsMatchMarketConfigLogsServiceImpl implements RcsMatchMarketConfigLogsService {
    @Autowired
    private RcsMatchMarketConfigLogsMapper rcsMatchMarketConfigLogsMapper;

    @Override
    public PageDataResult getStatusList(MatchFlowingDTO userSearch, Integer pageNum, Integer pageSize) {
        PageDataResult pageDataResult = new PageDataResult();
        return pageDataResult;
    }

    @Override
    public PageDataResult getStatusList(Integer matchId, Long marketId, Integer pageNum, Integer pageSize) throws ParseException {
        PageDataResult pageDataResult = new PageDataResult();
        PageHelper.startPage(pageNum, pageSize);
        List<Map> rcsMatchMarketConfigLogs = rcsMatchMarketConfigLogsMapper.selectById(matchId, marketId);
        if (CollectionUtils.isEmpty(rcsMatchMarketConfigLogs)) {
            PageHelper.startPage(1, pageSize);
            rcsMatchMarketConfigLogs = rcsMatchMarketConfigLogsMapper.selectById(matchId, marketId);
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
                    } else {
                        entry.setValue(entry.getValue().toString());
                    }
                }
                if (rcsMatchMarketConfigLogs1.get("change_level").equals("1")) {
                    Object market_status = rcsMatchMarketConfigLogs1.get("market_status");
                    if ("0".equals(market_status)) {
                        rcsMatchMarketConfigLogs1.put("market_status", "11");
                    }
                    if ("1".equals(market_status)) {
                        rcsMatchMarketConfigLogs1.put("market_status", "0");
                    }
                    if ("2".equals(market_status)) {
                        rcsMatchMarketConfigLogs1.put("market_status", "2");
                    }
                    if ("3".equals(market_status)) {
                        rcsMatchMarketConfigLogs1.put("market_status", "1");
                    }
                }
            }
        }
        if (rcsMatchMarketConfigLogs.size() != 0) {
            PageInfo<Map> pageInfo = new PageInfo<>(rcsMatchMarketConfigLogs, pageNum);
            pageDataResult.setList(pageInfo.getList());
            pageDataResult.setTotals((int) pageInfo.getTotal());
        }
        return pageDataResult;
    }
}
