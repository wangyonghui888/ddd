package com.panda.sport.rcs.oddin.grpc.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.oddin.OddinOrderInfoDto;
import com.panda.sport.data.rcs.dto.oddin.TicketDto;
import com.panda.sport.data.rcs.vo.oddin.RejectReasonVo;
import com.panda.sport.data.rcs.vo.oddin.TicketVo;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.MatchTypeReportEnum;
import com.panda.sport.rcs.oddin.client.GrpcTicketClient;
import com.panda.sport.rcs.oddin.entity.common.pojo.RcsOddinOrderDj;
import com.panda.sport.rcs.oddin.entity.ots.Enums;
import com.panda.sport.rcs.oddin.entity.ots.TicketOuterClass;
import com.panda.sport.rcs.oddin.enums.DataSourceEnum;
import com.panda.sport.rcs.oddin.enums.GrpcConnectionStatusEnum;
import com.panda.sport.rcs.oddin.grpc.TicketGrpcService;
import com.panda.sport.rcs.oddin.grpc.handler.TicketGrpcHandler;
import com.panda.sport.rcs.oddin.pool.GrpcTicketClientPool;
import com.panda.sport.rcs.oddin.service.RcsOrderService;
import com.panda.sport.rcs.oddin.service.TicketOrderService;
import com.panda.sport.rcs.oddin.service.handler.IOrderHandlerService;
import com.panda.sport.rcs.oddin.service.handler.TicketOrderHandler;
import com.panda.sport.rcs.oddin.service.handler.impl.OrderHandlerServiceImpl;
import com.panda.sport.rcs.oddin.util.DateUtil;
import com.panda.sport.rcs.oddin.util.DjMqUtils;
import com.panda.sport.rcs.oddin.util.SendMessageUtils;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

import static com.panda.sport.rcs.cache.CaCheKeyConstants.ODDIN_ORDER_INFO_KEY;
import static com.panda.sport.rcs.oddin.common.Constants.*;
import static com.panda.sport.rcs.oddin.common.RedisKeyConstants.*;
import static com.panda.sport.rcs.oddin.enums.GrpcConnectionStatusEnum.CONNECTED;
import static com.panda.sport.rcs.oddin.enums.GrpcConnectionStatusEnum.DISCONNECTED;
import static com.panda.sport.rcs.oddin.util.ParamUtils.splitOrderNo;

@Slf4j
@Service
public class TicketGrpcServiceImpl implements TicketGrpcService {
    @Resource
    private TicketOrderService ticketOrderService;
    @Resource
    private TicketGrpcHandler ticketGrpcHandler;
    @Resource
    private RcsOrderService rcsOrderService;
    @Resource
    private DjMqUtils djMqUtils;
    @Resource
    private RedisClient redisClient;
    @Resource
    private TicketOrderHandler ticketOrderHandler;
    @Resource
    private SendMessageUtils rocketProducer;
    @Resource
    private IOrderHandlerService orderHandlerService;


    /**
     * 注单
     *
     * @param ticketDto
     */
    @Override
    public void ticket(TicketDto ticketDto) {
        TicketOuterClass.Ticket ticket = ticketGrpcHandler.getTicket(ticketDto);
        log.info("::{}::准备进行grpc请求数据商注单接口，ticketGrpcServiceImp接收到的入参ticketDto:{}", ticketDto.getId(), JSONObject.toJSONString(ticketDto));
        try {
            GrpcTicketClient client = GrpcTicketClientPool.borrowObject();
            TicketOuterClass.TicketRequest request = TicketOuterClass.TicketRequest.newBuilder().setTicket(ticket).build();
            log.info("::{}::{}::投注-发送GRPC请求参数:{}", MDC.get("linkId"), ticketDto.getId(), com.google.protobuf.util.JsonFormat.printer().print(request));
            client.orderObserver.onNext(request);
            GrpcTicketClientPool.returnObject(client);
            Long startTime = ticketDto.getTimestamp().getTime();
            log.info("::{}::注单请求消耗时间为::{}ms", ticketDto.getId(), (System.currentTimeMillis() - startTime));
        } catch (Exception e) {
            log.error("::{}::{}::投注-请求异常:", MDC.get("linkId"), ticketDto.getId(), e);
        }
    }

    /**
     * 注单响应方法
     *
     * @return
     */
    public StreamObserver<TicketOuterClass.TicketResponse> createTicketResponseStreamObserver() {
        return new StreamObserver<TicketOuterClass.TicketResponse>() {
            @Override
            public void onNext(TicketOuterClass.TicketResponse value) {
                //心跳处理
                if (value.hasKeepalive()) {
                    //收到心跳回复，先检查之前是否断连，如果断连则需要删除缓存，并发mq消息通知下游
                    try {
                        //处理校验注单链接是否正常的缓存
                        String validateTime = redisClient.get(VALIDATE_TICKET_GRPC_CONNECTION);
                        if (StringUtils.isNotBlank(validateTime)) {
                            redisClient.delete(VALIDATE_TICKET_GRPC_CONNECTION);
                        }
                        //处理链接断开推送消息给电竞
                        String status = redisClient.get(ODDIN_GRPC_CONNECT_STATUS_KEY);
                        if (DISCONNECTED.getCode().toString().equals(status)) {
                            //删除缓存中的断联数据
                            redisClient.delete(ODDIN_GRPC_CONNECT_STATUS_KEY);
                            //发送mq消息通知下游连接已恢复
                            djMqUtils.sendMessage(RCS_RISK_ODDIN_ON_PRODUCER_STATUS_CHANGE_TO_DJ, JSONObject.toJSONString(Response.success(CONNECTED.getValue())));
                            log.info("::{}::topic收到投注接口恢复的通知,消息为:{}", RCS_RISK_ODDIN_ON_PRODUCER_STATUS_CHANGE_TO_DJ, CONNECTED.getValue());
                        }
                        Timestamp timestamp = value.getKeepalive().getTimestamp();
                        log.info("收到注单心跳回复--->{} ", DateUtil.format_sss(new Date(timestamp.getSeconds() * 1000)));
                    } catch (Exception e) {
                        try {
                            log.error("处理心跳回复异常:{}", com.google.protobuf.util.JsonFormat.printer().print(value), e);
                        } catch (InvalidProtocolBufferException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
                //订单状态处理
                if (value.hasState()) {
                    try {
                        String orderNo = value.getState().getId();
                        log.info("::{}::{}::{}::收到投注返回:{}", ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), value.getState().getTicketStatus(), orderNo, com.google.protobuf.util.JsonFormat.printer().print(value));
                        if (Enums.AcceptanceStatus.ACCEPTANCE_STATUS_PENDING.toString().equalsIgnoreCase(value.getState().getTicketStatus().toString())) {
                            log.warn("::{}::{}::收到投注返回状态为:ACCEPTANCE_STATUS_PENDING,先不处理继续等待", MDC.get("linkId"), orderNo);
                            return;
                        }


                        //当订单状态为RESULTING_STATUS_VOIDED表示无效订单，进行撤单
                        if ("RESULTING_STATUS_VOIDED".equalsIgnoreCase(value.getState().getTicketStatus().toString())) {
                            String voidedKey = String.format(RESULTING_STATUS_VOIDED_ORDER_REDIS_LOCK_KEY, value.getState().getId());
                            String voidedLock = redisClient.get(voidedKey);
                            if (StringUtils.isNotBlank(voidedLock)) {
                                log.warn("::{}::订单无效已经进行了撤单处理，无需再进行重复操作", orderNo);
                                return;
                            }
                            redisClient.setExpiry(voidedLock, RESULTING_STATUS_VOIDED_ORDER_EXIST, ODDIN_ROBACK_ORDER_TIME);
                        } else {
                            String redisKey = ODDIN_ROBACK_ORDER_TAG.concat("-").concat(value.getState().getId());
                            String exists = redisClient.get(redisKey);
                            if (StringUtils.isNotBlank(exists)) {
                                log.warn("::{}::{}::订单信息已经处理过,不做重复处理", ticketOrderHandler.getGlobalIdFromCacheByOrderNo(orderNo), orderNo);
                                return;
                            }
                            redisClient.setExpiry(redisKey, ODDIN_ROBACK_ORDER_EXISTS, ODDIN_ROBACK_ORDER_TIME);
                        }

                        //注单回调数据后续处理
                        TicketVo vo = ticketGrpcHandler.transferResponseToVo(value);
                        rocketProducer.sendMessage(RCS_RISK_ODDIN_ORDER_CALLBACK, "oddin_ticket_callback", orderNo, vo);
                    } catch (Exception e) {
                        try {
                            log.error("处理注单回调数据异常{}:", com.google.protobuf.util.JsonFormat.printer().print(value), e);
                        } catch (InvalidProtocolBufferException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
                //最大限额处理
                if (value.hasMaxStake()) {
                    log.info("maxStake received: " + value.getMaxStake());
                }
                //结算状态处理
                if (value.hasCashout()) {
                    log.info("cashout received: " + value.getCashout());
                }
            }

            @Override
            public void onError(Throwable t) {
                log.info("注单连接异常,STREAM ERROR:", t);
                //删除校验链接断开的缓存
                redisClient.delete(VALIDATE_TICKET_GRPC_CONNECTION);
                //grpc链接断开，先存缓存，然后发mq通知下游连接已断开
                redisClient.set(ODDIN_GRPC_CONNECT_STATUS_KEY, GrpcConnectionStatusEnum.DISCONNECTED.getCode());
                //发送mq到下游
                djMqUtils.sendMessage(RCS_RISK_ODDIN_ON_PRODUCER_STATUS_CHANGE_TO_DJ, JSONObject.toJSONString(Response.error(503, (DISCONNECTED.getValue()))));
                log.info("::{}::topic收到投注接口断联的通知,消息为:{}", RCS_RISK_ODDIN_ON_PRODUCER_STATUS_CHANGE_TO_DJ, DISCONNECTED.getValue());
                //发生断连服务，销毁连接池连接
                GrpcTicketClientPool.destroyPool();
            }

            @Override
            public void onCompleted() {
            }
        };
    }

}
