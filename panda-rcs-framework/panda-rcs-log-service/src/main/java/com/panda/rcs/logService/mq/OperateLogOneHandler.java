package com.panda.rcs.logService.mq;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.logService.mapper.RcsOperateLogMapper;
import com.panda.rcs.logService.service.impl.LogDataChangeServiceImpl;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogParameters;
import com.panda.rcs.logService.utils.BaseUtils;
import com.panda.rcs.logService.vo.RcsQuotaBusinessLimitLog;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "rcs_log_service", topic = "OPERATION_LOG_TO_RISK")
public class OperateLogOneHandler implements RocketMQListener<String> {

    @Autowired
    private RcsOperateLogMapper rcsOperateLogMapper;


    @Override
    public void onMessage(String data) {
        try{
        log.info("::::操盤日誌消費開始-消费数据->{}", data);
            List<RcsOperateLog> list = JSONArray.parseArray(data, RcsOperateLog.class);
             if(null!=list&& !list.isEmpty()
             ){
                 rcsOperateLogMapper.bathInserts(list);
             }
        }catch (Exception e){
            log.error("::{}::操盘日志-设置值异常",e);
        }
    }





}
