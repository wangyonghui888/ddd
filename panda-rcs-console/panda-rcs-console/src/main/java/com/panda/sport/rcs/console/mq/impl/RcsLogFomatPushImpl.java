package com.panda.sport.rcs.console.mq.impl;


import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.console.dao.MatchStatisticsInfoFlowingMapper;
import com.panda.sport.rcs.console.dao.MatchStatusFlowingMapper;
import com.panda.sport.rcs.console.dto.RcsLogFormat;
import com.panda.sport.rcs.console.pojo.MatchStatusFlowing;
import com.panda.sport.rcs.console.pojo.RcsLogFomat;
import com.panda.sport.rcs.console.pojo.Request;
import com.panda.sport.rcs.console.pojo.StandardMatchStatusMessage;
import com.panda.sport.rcs.console.service.RcsLogFomatService;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RcsLogFomatPushImpl extends ConsumerAdapter<List<RcsLogFomat>> {


    @Autowired
    RcsLogFomatService rcsLogFomatService;

    public RcsLogFomatPushImpl() {
        super("RCS_LOG_FORMAT", "");
    }

    @Override
    public Boolean handleMs(List<RcsLogFomat> list, Map<String, String> paramsMap) {
        try {
            persistence(list);
        } catch (Exception e) {
            log.error("rcsLogFomatPush存入MQ消息队列错误:{}"+JsonFormatUtils.toJson(list)+e.getMessage(), e);
            return false;
        }
        return true;
    }


    public void persistence(List<RcsLogFomat> list) {
        rcsLogFomatService.batchInsert(list);
    }

}
