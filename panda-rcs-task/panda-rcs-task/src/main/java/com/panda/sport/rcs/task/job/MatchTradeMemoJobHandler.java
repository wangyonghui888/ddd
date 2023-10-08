package com.panda.sport.rcs.task.job;

import com.beust.jcommander.internal.Lists;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.dto.MatchTradeMemoRemindDTO;
import com.panda.sport.rcs.task.wrapper.RcsMatchUserMemoRefService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 赛事操盘备忘录WS推送扫描
 *
 * @author riben
 * @date 2021-2-6
 */
@Component
@Slf4j
public class MatchTradeMemoJobHandler{

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    private RcsMatchUserMemoRefService matchUserMemoRefService;

//    @Scheduled(cron = "*/5 * * * * ?")
    public void execute() {
        try {
            log.info("开始执行扫描赛事操盘备忘录数据");

            Map<String, List<Long>> traderRemindMatchIds = matchUserMemoRefService.getTradeRemindMatchMemos();
            if(MapUtils.isEmpty(traderRemindMatchIds)){
                log.info("扫描赛事操盘备忘录数据结束，无提醒操盘手查看备忘录数据！");
                return;
            }
            log.info("扫描赛事操盘备忘录数据结束，需要提醒操盘手查看备忘录的赛事数据 : {}", traderRemindMatchIds);
            List<MatchTradeMemoRemindDTO> remindDTOS = Lists.newArrayList();
            traderRemindMatchIds.forEach((taderId, matchIds) -> {
                MatchTradeMemoRemindDTO remindDTO = new MatchTradeMemoRemindDTO();
                remindDTO.setTraderId(taderId);
                remindDTO.setMatchIds(matchIds);
                remindDTOS.add(remindDTO);
            });

            Request<List<MatchTradeMemoRemindDTO>> msg = new Request<List<MatchTradeMemoRemindDTO>>();
            msg.setData(remindDTOS);
            msg.setLinkId(UUID.randomUUID().toString().replace("-", "") + "_matchTradeMemoJobHandler_trade");
            producerSendMessageUtils.sendMessage("WS_REMIND_TRADE_READ_MEMO","task-remind-trader", "", msg);
            log.info("扫描赛事操盘备忘录数据结束，已将数据发送WS服务！");
        } catch (Exception e) {
            log.error("赛事操盘未读备忘录扫描错误" + e.getMessage(), e);
        }
    }


}
