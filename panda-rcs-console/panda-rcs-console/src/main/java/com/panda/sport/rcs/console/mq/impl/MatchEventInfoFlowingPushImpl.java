package com.panda.sport.rcs.console.mq.impl;

import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.console.dao.MatchEventInfoFlowingMapper;
import com.panda.sport.rcs.console.pojo.MatchEventInfoFlowing;
import com.panda.sport.rcs.console.pojo.MatchEventInfoMessage;
import com.panda.sport.rcs.console.pojo.Request;
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
public class MatchEventInfoFlowingPushImpl extends ConsumerAdapter<Request<List<MatchEventInfoMessage>>> {
 /*   @Autowired
    MatchEventInfoFlowingMapper matchEventInfoFlowingMapper;


    public MatchEventInfoFlowingPushImpl() {
        super(MqConstants.MATCH_EVENT_INFO_TO_RISK, "");
    }*/

    @Override
    public Boolean handleMs(Request<List<MatchEventInfoMessage>> request, Map<String, String> paramsMap) throws Exception {
   /*     try {
            log.info("flowing-MatchEventInfoMessageMQ消息队列:{}", JsonFormatUtils.toJson(request));
            List<MatchEventInfoMessage> datas = request.getData();
            for (MatchEventInfoMessage data : datas) {
                MatchEventInfoFlowing matchEventInfoFlowing = BeanCopyUtils.copyProperties(data, MatchEventInfoFlowing.class);
                matchEventInfoFlowing.setId(null);
                matchEventInfoFlowing.setOId(data.getId());
                matchEventInfoFlowing.setLinkId(request.getLinkId());
                matchEventInfoFlowing.setCreateTime(System.currentTimeMillis());
                matchEventInfoFlowingMapper.insertSelective(matchEventInfoFlowing);
            }
        } catch (Exception e) {
            log.error("flowing-MatchEventInfoMessageMQ消息队列错误"+JsonFormatUtils.toJson(request)+e.getMessage(), e);
            return false;
        }*/
        return true;
    }

}
