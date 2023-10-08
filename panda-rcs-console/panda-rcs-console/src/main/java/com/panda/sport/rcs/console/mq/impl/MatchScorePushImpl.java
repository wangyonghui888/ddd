package com.panda.sport.rcs.console.mq.impl;


import com.panda.sport.rcs.console.dao.MatchStatisticsInfoDetailFlowingMapper;
import com.panda.sport.rcs.console.dao.MatchStatisticsInfoFlowingMapper;
import com.panda.sport.rcs.console.pojo.MatchStatisticsInfoDetailFlowing;
import com.panda.sport.rcs.console.pojo.MatchStatisticsInfoFlowing;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MatchScorePushImpl extends ConsumerAdapter<MatchStatisticsInfoFlowing> {

    @Autowired
    MatchStatisticsInfoFlowingMapper matchStatisticsInfoFlowingMapper;
    @Autowired
    MatchStatisticsInfoDetailFlowingMapper matchStatisticsInfoDetailFlowingMapper;

    public MatchScorePushImpl() {
        super("WS_STATISTICS_NOTIFY_TOPIC", "");
    }

    @Override
    public Boolean handleMs(MatchStatisticsInfoFlowing data, Map<String, String> paramsMap) {
        try {
            log.info("MatchStatisticsInfoFlowing存入MQ消息队列:{}",JsonFormatUtils.toJson(data));
            List<MatchStatisticsInfoDetailFlowing> matchStatisticsInfoDetailList = data.getList();
            if(!CollectionUtils.isEmpty(matchStatisticsInfoDetailList)) {
                for (MatchStatisticsInfoDetailFlowing matchStatisticsInfoDetailFlowing : matchStatisticsInfoDetailList) {
                    matchStatisticsInfoDetailFlowing.setOId(data.getStandardMatchId());
                    matchStatisticsInfoDetailFlowing.setId(null);
                    matchStatisticsInfoDetailFlowing.setStandardMatchId(data.getStandardMatchId());
                    matchStatisticsInfoDetailFlowing.setLinkId(data.getGlobalId());
                }
                matchStatisticsInfoDetailFlowingMapper.batchInsert(matchStatisticsInfoDetailList);
            }
        } catch (Exception e) {
            log.error("MatchStatisticsInfoFlowing存入MQ消息队列错误:{}"+JsonFormatUtils.toJson(data)+e.getMessage(), e);
            return false;
        }
        return true;
    }

}
