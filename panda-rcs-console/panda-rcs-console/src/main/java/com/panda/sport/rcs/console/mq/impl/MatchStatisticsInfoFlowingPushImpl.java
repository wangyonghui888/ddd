package com.panda.sport.rcs.console.mq.impl;


import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.console.dao.MatchStatisticsInfoDetailFlowingMapper;
import com.panda.sport.rcs.console.dao.MatchStatisticsInfoFlowingMapper;
import com.panda.sport.rcs.console.pojo.MatchStatisticsInfoFlowing;
import com.panda.sport.rcs.console.pojo.Request;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class MatchStatisticsInfoFlowingPushImpl extends ConsumerAdapter<Request<MatchStatisticsInfoFlowing>> {
/*
    @Autowired
    MatchStatisticsInfoFlowingMapper matchStatisticsInfoFlowingMapper;
    @Autowired
    MatchStatisticsInfoDetailFlowingMapper matchStatisticsInfoDetailFlowingMapper;

    public MatchStatisticsInfoFlowingPushImpl() {
        super(MqConstants.STANDARD_MATCH_STATISTICS, "");
    }*/

    @Override
    public Boolean handleMs(Request<MatchStatisticsInfoFlowing> request, Map<String, String> paramsMap) {
/*        try {
            log.info("MatchStatisticsInfoFlowing存入MQ消息队列:{}",JsonFormatUtils.toJson(request));
            MatchStatisticsInfoFlowing data = request.getData();
            if(data!=null) {
                data.setGlobalId(request.getLinkId());
                data.setOId(data.getId());
                data.setId(null);
                matchStatisticsInfoFlowingMapper.insert(data);
            }
        } catch (Exception e) {
            log.error("MatchStatisticsInfoFlowing存入MQ消息队列错误:{}"+JsonFormatUtils.toJson(request)+e.getMessage(), e);
            return false;
        }*/
        return true;
    }

}
