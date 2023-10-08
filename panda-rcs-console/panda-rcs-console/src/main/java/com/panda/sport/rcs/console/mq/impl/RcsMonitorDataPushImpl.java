package com.panda.sport.rcs.console.mq.impl;


import com.panda.sport.rcs.console.common.utils.DateUtils;
import com.panda.sport.rcs.console.dao.RcsMonitorDataMapper;
import com.panda.sport.rcs.console.pojo.RcsLogFomat;
import com.panda.sport.rcs.console.pojo.RcsMonitorData;
import com.panda.sport.rcs.console.service.RCSMonitorService;
import com.panda.sport.rcs.console.service.RcsLogFomatService;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RcsMonitorDataPushImpl extends ConsumerAdapter<RcsMonitorData> {


    @Autowired
    RcsMonitorDataMapper rcsMonitorDataMapper;

    public RcsMonitorDataPushImpl() {
        super("RCS_MONITOR_DATA", "");
    }

    @Override
    public Boolean handleMs(RcsMonitorData bean, Map<String, String> paramsMap) {
        try {
/*            String yyyyMMddHH = DateUtils.getFormatTime(new Date(), "yyyyMMddHH");
            bean.setCreateTimeHours(yyyyMMddHH);
            rcsMonitorDataMapper.insertBean(bean);*/
        } catch (Exception e) {
            log.error("RcsMonitorData存入MQ消息队列错误:{}" + JsonFormatUtils.toJson(bean) + e.getMessage(), e);
            return false;
        }
        return true;
    }


}
