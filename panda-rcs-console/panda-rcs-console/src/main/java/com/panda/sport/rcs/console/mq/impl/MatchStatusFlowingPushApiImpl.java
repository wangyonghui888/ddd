package com.panda.sport.rcs.console.mq.impl;


import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.console.dao.MatchStatisticsInfoFlowingMapper;
import com.panda.sport.rcs.console.dao.MatchStatusFlowingMapper;
import com.panda.sport.rcs.console.pojo.MatchStatusFlowing;
import com.panda.sport.rcs.console.pojo.Request;
import com.panda.sport.rcs.console.pojo.StandardMatchStatusMessage;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class MatchStatusFlowingPushApiImpl extends ConsumerAdapter<Request<StandardMatchStatusMessage>> {

    @Autowired
    MatchStatusFlowingMapper matchStatusFlowingMapper;
    @Autowired
    MatchStatisticsInfoFlowingMapper matchStatisticsInfoFlowingMapper;

    public MatchStatusFlowingPushApiImpl() {
        super(MqConstants.STANDARD_MATCH_STATUS, "");
    }

    @Override
    public Boolean handleMs(Request<StandardMatchStatusMessage> request, Map<String, String> paramsMap) {
        try {
            log.info("StandardMatchStatus-flowing存入MQ消息队列:{}",JsonFormatUtils.toJson(request));
            persistence(request);
        } catch (Exception e) {
            log.error("StandardMatchStatus-flowing存入MQ消息队列错误:{}"+JsonFormatUtils.toJson(request)+e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * @Description: 持久化
     * @Author: Vector
     * @Date:
    **/
    public void persistence(Request<StandardMatchStatusMessage> dto) {
            StandardMatchStatusMessage standardMatchStatusDTO = dto.getData();
            MatchStatusFlowing  matchStatusFlowing = null;
            matchStatusFlowing = BeanCopyUtils.copyProperties(standardMatchStatusDTO,MatchStatusFlowing.class);
            matchStatusFlowing.setOId(standardMatchStatusDTO.getStandardMatchId());
            matchStatusFlowing.setLinkId(dto.getLinkId());
            if(matchStatusFlowing!=null)matchStatusFlowingMapper.insertSelective(matchStatusFlowing);
    }

}
