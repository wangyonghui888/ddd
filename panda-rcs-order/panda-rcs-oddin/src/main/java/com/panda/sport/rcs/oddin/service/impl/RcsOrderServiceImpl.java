package com.panda.sport.rcs.oddin.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.vo.oddin.RejectReasonVo;
import com.panda.sport.data.rcs.vo.oddin.TicketVo;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.enums.OrderStatusEnum;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.oddin.entity.ots.Enums;
import com.panda.sport.rcs.oddin.service.RcsOrderService;
import com.panda.sport.rcs.oddin.service.handler.IOrderHandlerService;
import com.panda.sport.rcs.oddin.service.handler.TicketOrderHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.bind.Element;
import java.util.HashMap;
import java.util.Map;

import static com.panda.sport.rcs.oddin.common.Constants.RESULTING_STATUS_VOIDED_REJECT_CODE;
import static com.panda.sport.rcs.oddin.common.Constants.RESULTING_STATUS_VOIDED_REJECT_RESON;

/**
 * 处理风控注单业务：接拒单/撤单
 *
 * @author Z9-conway
 */
@Slf4j
@Service
public class RcsOrderServiceImpl implements RcsOrderService {

    @Resource
    IOrderHandlerService orderHandlerService;
    @Resource
    TicketOrderHandler ticketOrderHandler;

    @Override
    public void rejectOrder(TicketVo vo) {
        log.info("::{}::{}::oddin订单接拒:{}", ticketOrderHandler.getGlobalIdFromCacheByOrderNo(vo.getId()), vo.getId(), JSON.toJSONString(vo));
        try {
            //如果数据商已接受改注单走内部接单逻辑
            if (Enums.AcceptanceStatus.ACCEPTANCE_STATUS_ACCEPTED.toString().equalsIgnoreCase(vo.getTicket_status())) {
                orderHandlerService.orderByPa(vo);
            } else if (Enums.AcceptanceStatus.ACCEPTANCE_STATUS_REJECTED.toString().equalsIgnoreCase(vo.getTicket_status())) {
                //数据商拒绝接单,直接通知业务更新订单状态
                orderHandlerService.orderByThird(vo);
            } else if ("RESULTING_STATUS_VOIDED".equalsIgnoreCase(vo.getTicket_status())) {
                log.info("::{}::数据商返回无效订单，进行撤单", vo.getId());
                TicketVo ticketVo = new TicketVo();
                ticketVo.setId(vo.getId());
                RejectReasonVo rejectReasonVo = new RejectReasonVo();
                rejectReasonVo.setCode(RESULTING_STATUS_VOIDED_REJECT_CODE);
                rejectReasonVo.setMessage(RESULTING_STATUS_VOIDED_REJECT_RESON);
                ticketVo.setReject_reson(rejectReasonVo);
                orderHandlerService.orderByThird(ticketVo);
            }
        } catch (Exception e) {
            log.info("::{}::{}::oddin回调体育订单处理异常:{}", ticketOrderHandler.getGlobalIdFromCacheByOrderNo(vo.getId()), vo.getId(), e.toString());
        }
    }


}
