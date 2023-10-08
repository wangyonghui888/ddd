package com.panda.sport.rcs.data.sync;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.mq.RcsConsumer;
import com.panda.sport.rcs.data.service.IStandardMatchInfoService;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.dto.ChangeMatchOverDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = "CHANGE_MATCH_OVER",
        consumerGroup = "RCS_DATA_CHANGE_MATCH_OVER_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class ChangeMatchOverConsumer extends RcsConsumer<Request<ChangeMatchOverDTO>> {

    @Autowired
    IStandardMatchInfoService iStandardMatchInfoService;

    @Override
    protected String getTopic() {
        return "CHANGE_MATCH_OVER";
    }

    @Override
    public Boolean handleMs(Request<ChangeMatchOverDTO> dto) {
        try {
            ChangeMatchOverDTO data = dto.getData();
            StandardMatchInfo info = iStandardMatchInfoService.getById(data.getMatchId());
            if(null!=info.getEndTime()&&0!=info.getEndTime()){
                log.info("::{}::overTime有值 :{}","RDCMOG_"+dto.getLinkId()+"_"+data.getMatchId(),data.getMatchId());
            } else {
                UpdateWrapper<StandardMatchInfo> objectUpdateWrapper = new UpdateWrapper<>();
                objectUpdateWrapper.lambda().eq(StandardMatchInfo::getId,data.getMatchId());
                StandardMatchInfo standardMatchInfo = new StandardMatchInfo();
                standardMatchInfo.setEndTime(System.currentTimeMillis());
                iStandardMatchInfoService.update(standardMatchInfo,objectUpdateWrapper);
                log.info("::{}::完成入库:{}","RDCMOG_"+dto.getLinkId()+"_"+data.getMatchId(),data.getMatchId());
            }
        } catch (Exception e) {
            log.error("::{}::,{},{},{}","RDCMOG_"+dto.getLinkId(), JsonFormatUtils.toJson(dto),e.getMessage(), e);
            return false;
        }
        return true;
    }
}
