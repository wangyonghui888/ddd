package com.panda.sport.rcs.oddin.grpc.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.protobuf.Timestamp;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.oddin.TicketResultDto;
import com.panda.sport.data.rcs.vo.oddin.TicketResultVo;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.oddin.client.GrpcPullSingleClient;
import com.panda.sport.rcs.oddin.entity.ots.TicketResultOuterClass;
import com.panda.sport.rcs.oddin.grpc.PullSingleGrpcService;
import com.panda.sport.rcs.oddin.grpc.handler.TicketGrpcHandler;
import com.panda.sport.rcs.oddin.pool.GrpcPullSingleClientPool;
import com.panda.sport.rcs.oddin.util.DateUtil;
import com.panda.sport.rcs.oddin.util.DjMqUtils;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

import static com.panda.sport.rcs.oddin.common.Constants.*;
import static com.panda.sport.rcs.oddin.common.RedisKeyConstants.VALIDATE_PULLSINGLE_GRPC_CONNECTION;
import static com.panda.sport.rcs.oddin.util.ParamUtils.montageOrderNo;
import static com.panda.sport.rcs.oddin.util.ParamUtils.splitOrderNos;

/**
 * @Author wiker
 * @Date 2023/6/17 18:58
 **/
@Slf4j
@Service
public class PullSingleGrpcServiceImpl implements PullSingleGrpcService {
    @Resource
    private TicketGrpcHandler ticketGrpcHandler;
    @Resource
    private DjMqUtils djMqUtils;
    @Resource
    private ProducerSendMessageUtils messageUtils;
    @Resource
    private RedisClient redisClient;

    /**
     * 响应体方法
     *
     * @return
     */
    public StreamObserver<TicketResultOuterClass.TicketResultResponse> createTicketResponseStreamObserver() {
        return new StreamObserver<TicketResultOuterClass.TicketResultResponse>() {

            //拉单响应体
            @Override
            public void onNext(TicketResultOuterClass.TicketResultResponse value) {
                TicketResultVo vo = null;
                //定时任务心跳请求老是会出现id=,的情况,设定了只有请求的拉单请求才能进入下面逻辑(
                //心跳和拉单都是同一个响应体(心跳不进入拉单响应的逻辑)
                if (value.hasKeepalive()) {
                    //处理校验拉单链接是否正常的缓存
                    String validateTime = redisClient.get(VALIDATE_PULLSINGLE_GRPC_CONNECTION);
                    if (StringUtils.isNotBlank(validateTime)) {
                        redisClient.delete(VALIDATE_PULLSINGLE_GRPC_CONNECTION);
                    }
                    Timestamp timestamp = value.getKeepalive().getTimestamp();
                    log.info("收到拉单心跳回复--->{} ", DateUtil.format_sss(new Date(timestamp.getSeconds() * 1000)));
                }
                //拉单数据回调处理
                if (value.hasState() || value.hasAfter()) {
                    if (ObjectUtils.isNotEmpty(value)) {
                        //根据时间段拉取全部注单
                        if (ObjectUtil.isNotEmpty(value.getAfter()) && value.getAfter().getTicketsList().size() > 0) {
                            //数据转换
                            vo = ticketGrpcHandler.ticketResultResponse(value);
                            log.info("===推送拉单接口返回信息给大数据====topic :{},UTC+8拉单时间段:{}到至今的全部注单数据", RCS_RISK_ODDIN_SETTLE_BET_TY, JSONObject.toJSONString(value.getAfter().getAfter().getSeconds()));
                            messageUtils.sendMessage(RCS_RISK_ODDIN_SETTLE_BET_TY, vo);
                        } else {
                            //体育单个拉单响应体
                            if (StringUtils.isNotEmpty(value.getState().getId()) && !Objects.equals(value.getState().getId(), "")) {
                                vo = ticketGrpcHandler.ticketResultResponse(value);
                                if (splitOrderNos(value.getState().getId()).contains("TY")) {
                                    log.info("===推送拉单接口返回信息给体育====topic :{} value :{}", RCS_RISK_ODDIN_TICKET_RESULT_TO_TY, JSONObject.toJSONString(vo));
                                    messageUtils.sendMessage(RCS_RISK_ODDIN_TICKET_RESULT_TO_TY, vo);
                                    //电竞拉单响应体
                                } else if (splitOrderNos(value.getState().getId()).contains("DJ")) {
                                    log.info("===推送拉单接口返回信息给电竞====topic :{} value :{}", RCS_RISK_ODDIN_TICKET_RESULT_TO_DJ, JSONObject.toJSONString(vo));
                                    djMqUtils.sendMessage(RCS_RISK_ODDIN_TICKET_RESULT_TO_DJ, JSONObject.toJSONString(vo));
                                }
                            }
                        }
                    }
                }

            }

            @Override
            public void onError(Throwable t) {
                log.info("拉单接口连接异常", t);
                //发生断连服务，销毁连接池连接
                GrpcPullSingleClientPool.destroyPool();
                //删除校验链接断开的缓存
                redisClient.delete(VALIDATE_PULLSINGLE_GRPC_CONNECTION);
            }

            @Override
            public void onCompleted() {
            }
        };
    }


    /**
     * 全量拉单和单个拉单都是公用一个接口,只是拉单的入参不一样,
     * oddIn响应的数据也不一样(全量拉单拉取指定时间的所有注单信息,单个拉单拉取指定单号信息)
     *
     * @param ticketResultDto
     * @return response
     */
    @Override
    public Response pullSingle(TicketResultDto ticketResultDto) {
        //防止心跳请求也进入同步返回信息(拉单成功 or 拉单失败 )
        //清空上一次的数据
        String orderNo = "";
        String requestId = "";
        //全量拉单入参
        TicketResultOuterClass.TicketsAfter ticketsAfter = null;
        //单个拉单入参
        TicketResultOuterClass.TicketResult ticketResult = null;
        TicketResultOuterClass.TicketResultRequest request;
        //建立连接
        GrpcPullSingleClient client = GrpcPullSingleClientPool.borrowObject();
        //判断全量拉单的入参是否为null
        if (ObjectUtil.isNotEmpty(ticketResultDto.getTicketsAfterDto())) {
            ticketsAfter = getTicketsAfter(ticketResultDto);
            requestId = ticketsAfter.getRequestId();
        }
        //判断单个拉单的入参是否为null
        if (ObjectUtil.isNotEmpty(ticketResultDto.getId())) {
            ticketResult = getTicketResult(ticketResultDto);
            orderNo = ticketResultDto.getId();
        }
        //全量拉单请求
        if (ObjectUtil.isNotNull(ticketsAfter)) {
            request = TicketResultOuterClass.TicketResultRequest.newBuilder().setAfter(ticketsAfter).build();
            client.requestStreamObserver.onNext(request);
        } else {
            //单个拉单请求
            request = TicketResultOuterClass.TicketResultRequest.newBuilder().setTicket(ticketResult).build();
            client.requestStreamObserver.onNext(request);
        }
        GrpcPullSingleClientPool.returnObject(client);
        log.info("::{}:{}:拉单-发送GRPC请求参数:{}", orderNo, requestId, request);
        return Response.success("拉单成功");
    }

    /**
     * 单个拉单入参
     *
     * @param ticketResultDto
     * @return ticketResult
     */
    private TicketResultOuterClass.TicketResult getTicketResult(TicketResultDto ticketResultDto) {
        return TicketResultOuterClass.TicketResult.newBuilder().
                setId(montageOrderNo(ticketResultDto.getId(), ticketResultDto.getSourceId())).build();
    }

    /**
     * 全量拉单入参
     *
     * @param ticketResultDto
     * @return ticketsAfter
     */
    private TicketResultOuterClass.TicketsAfter getTicketsAfter(TicketResultDto ticketResultDto) {
        return TicketResultOuterClass.TicketsAfter.newBuilder().
                setRequestId(ticketResultDto.getTicketsAfterDto().getRequest_id()).
                setAfter(Timestamp.newBuilder().setSeconds(ticketResultDto.getTicketsAfterDto().getAfter().getTime())).build();
    }
}
