package com.panda.sport.rcs.oddin.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.oddin.CancelOrderDto;
import com.panda.sport.data.rcs.dto.oddin.TicketDto;
import com.panda.sport.data.rcs.dto.oddin.TicketResultDto;
import com.panda.sport.data.rcs.dto.oddin.entity.Bet;
import com.panda.sport.data.rcs.vo.oddin.TicketCancelVo;
import com.panda.sport.data.rcs.vo.oddin.TicketStateVo;
import com.panda.sport.rcs.oddin.entity.common.pojo.RcsOddinOrder;
import com.panda.sport.rcs.oddin.entity.common.pojo.RcsOddinOrderDj;
import com.panda.sport.rcs.oddin.entity.common.pojo.RcsOddinOrderTy;
import com.panda.sport.rcs.oddin.entity.ots.TicketCancel;
import com.panda.sport.rcs.oddin.entity.ots.TicketMaxStake;
import com.panda.sport.rcs.oddin.enums.DataSourceEnum;
import com.panda.sport.rcs.oddin.grpc.*;
import com.panda.sport.rcs.oddin.grpc.handler.TicketGrpcHandler;
import com.panda.sport.rcs.oddin.service.handler.ParameterValidateHandler;
import com.panda.sport.rcs.oddin.service.handler.RcsOddinOrderDjHandler;
import com.panda.sport.rcs.oddin.service.TicketOrderService;
import com.panda.sport.rcs.oddin.service.handler.RcsOddinOrderTyHandler;
import com.panda.sport.rcs.oddin.service.handler.TicketOrderHandler;
import com.panda.sport.rcs.oddin.util.SendMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.panda.sport.rcs.oddin.common.Constants.*;

/**
 * @author Z9-conway
 */
@Slf4j
@Service
public class TicketOrderServiceImpl implements TicketOrderService {
    @Resource
    private RcsOddinOrderDjHandler rcsOddinOrderDjHandler;
    @Resource
    private RcsOddinOrderTyHandler rcsOddinOrderTyHandler;
    @Resource
    private TicketGrpcHandler ticketGrpcHandler;
    @Resource
    private TicketGrpcService ticketGrpcOrderService;
    @Resource
    private TicketOrderHandler ticketOrderHandler;
    @Resource
    private ParameterValidateHandler parameterValidateHandler;
    @Resource
    private FutureGrpcService grpcFutureService;
    @Resource
    private PullSingleGrpcService grpcPullSingleService;
    @Resource
    private SendMessageUtils rocketProducer;


    public static ExecutorService saveOrderExt = new ThreadPoolExecutor(500, 500,
            0, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1024),
            new ThreadFactoryBuilder().setNameFormat("o01-saveOrder-%d").build(), new ThreadPoolExecutor.AbortPolicy());

    public static ExecutorService djTicketExt = new ThreadPoolExecutor(64, 256,
            0, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1024),
            new ThreadFactoryBuilder().setNameFormat("o01-djTicket-%d").build(), new ThreadPoolExecutor.AbortPolicy());
    @Override
    public Response saveOrder(Request<TicketDto> requestParam) {
        TicketDto dto = requestParam.getData();
        log.info("::{}::投注-注单入参={}", dto.getId(), JSONUtil.toJsonStr(requestParam));
        /**
         * TODO 校验参数化
         */
        parameterValidateHandler.validateSaveArguments(dto);
//        rocketProducer.sendMessage(RCS_RISK_ODDIN_TICKET, "rcs_risk_oddin_ticket_group", dto.getId(), dto);
        djTicketExt.execute(() -> ticket(dto));
        return Response.success();
    }

    @Override
    public void ticket(TicketDto dto) {
        //统计开始请求注单到请求结束消耗的时间
        dto.setTimestamp(new Timestamp(System.currentTimeMillis()));
        //由于体育是直接从sdk走mq过来的，没有校验过入参，所以在此校验参数
        if (dto.getSourceId().equals(DataSourceEnum.TY.getCode())) {
            parameterValidateHandler.validateSaveArguments(dto);
        }
        //商户折扣
        String discount = null;
        try {
            //获取商户折扣率
            discount = ticketOrderHandler.getDiscount(dto.getLocation_id(), dto.getSourceId());
            log.info("::{}::{}::投注-注单折扣为:{},tenantId:{}", MDC.get("linkId"), dto.getId(), discount, dto.getLocation_id());
        } catch (Exception e) {
            //如果获取异常商户折扣率就默认为1；
            discount = "1";
            log.error("::{}::{}::投注-获取商户折扣信息出错,默认:{},tenantId:{}", MDC.get("linkId"), dto.getId(), discount, dto.getLocation_id());
        }
        //循环遍历投注列表
        List<Bet> betList = dto.getBets();
        if (CollectionUtils.isNotEmpty(betList)) {
            List<Bet> list = new ArrayList<>();
            for (Bet bet : betList) {
                //投注金额*10000
                int stake = new BigDecimal(TEN_THOUSAND).multiply(new BigDecimal(bet.getStake().getValue())).multiply(new BigDecimal(discount)).intValue();
                //打折后的订单金额
                bet.setRealAmount(stake);
                //折扣率
                bet.setDiscount(discount);
                //投注金额
                bet.getStake().setValue(stake);
                list.add(bet);
            }
            dto.setBets(list);
        }

        /**
         * TODO 插入订单数据
         */
        saveOrderExt.execute(() -> ticketOrderHandler.saveRcsGtsOrderExt(dto));

        /**
         * 判断，如果是体育的注单请求,并且是早盘赛事
         */
        if (DataSourceEnum.TY.getCode().equals(dto.getSourceId()) && 1 == dto.getMatchType()) {
            ticketOrderHandler.sendEarlyCancelMq(dto);
        }

        /**
         * TODO 调用oddin注单接口
         */
        try {
            log.info("::{}::注单其他流程已经走完，下一步进行调用数据商注单接口", dto.getId());
            ticketGrpcOrderService.ticket(dto);
        } catch (Exception e) {
            log.info("::{}::{}::投注-注单请求ODDIN接口出错", MDC.get("linkId"), dto.getId(), e);
        }
    }


    @Override
    public Response cancelOrder(Request<CancelOrderDto> requestParam) {
        log.info("::{}::撤单-入参={}", requestParam.getData().getId(), JSONObject.toJSONString(requestParam));
        CancelOrderDto dto = requestParam.getData();
        parameterValidateHandler.validateCancelArguments(dto);
        TicketCancel.TicketCancelResponse response = grpcFutureService.cancelOrder(dto);
        TicketCancelVo vo = new TicketCancelVo();
        vo.setStatus(response.getStatus().toString());
        vo.setCancel_rejection_message(response.getCancelRejectionMessage());
        vo.setCancel_rejection_reason(response.getCancelRejectionReason().toString());
        if (DataSourceEnum.TY.getCode().equals(dto.getSourceId())) {
            ticketOrderHandler.removeEarlyOrderBettingStatus(dto.getId());
        }
        log.info("::{}::撤单-返回结果={}", dto.getId(), JSONUtil.toJsonStr(vo));
        return Response.success(vo);
    }

    @Override
    public Response pullSingle(Request<TicketResultDto> ticketRequestDto) {
        log.info("::{}::单个拉单-入参={}", ticketRequestDto.getData().getId(), JSONUtil.toJsonStr(ticketRequestDto));
        TicketResultDto dto = ticketRequestDto.getData();
        try {
            //参数效验
            parameterValidateHandler.validatePullSingleByOrderNoArguments(dto);
            //请求oddIn的拉单接口
            return grpcPullSingleService.pullSingle(dto);
        } catch (Exception e) {
            log.error("::{}::单个拉单失败:{}", ticketRequestDto.getData().getId(), e.getStackTrace());
            return Response.error(Response.FAIL, "单个拉单失败", e.getMessage());
        }
    }

    /**
     * 更新订单状态
     *
     * @param order
     * @param sourceId
     */
    @Override
    public void updateOrder(RcsOddinOrder order, Integer sourceId) {

        try {
            if (DataSourceEnum.DJ.getCode().equals(sourceId)) {
                log.info("===ordreNo:{},电竞注单回调，更新订单状态，入参为 ：{}===", order.getOrderNo(), JSONObject.toJSONString(order));
                saveOrderExt.execute(() -> rcsOddinOrderDjHandler.update((RcsOddinOrderDj) order));
            } else if (DataSourceEnum.TY.getCode().equals(sourceId)) {
                log.info("===orderNo:{},体育注单回调，更新订单状态，入参为 ：{}===", order.getOrderNo(), JSONObject.toJSONString(order));
                saveOrderExt.execute(() -> rcsOddinOrderTyHandler.update((RcsOddinOrderTy) order));
            }
        } catch (Exception e) {
            log.warn("==orderNo:{},注单结果返回更新数据库失败 :{}", order.getOrderNo(), e);
        }
    }

    /**
     * 获取oddin最大限额
     *
     * @param requestParam
     * @return
     */
    @Override
    public Response queryMaxBetMoneyBySelect(Request<TicketDto> requestParam) {
        long startTime = System.currentTimeMillis();
        MDC.put("X-B3-TraceId", requestParam.getGlobalId());
        MDC.put("linkId", requestParam.getGlobalId() + " , " + requestParam.getData().getCustomer().getId());
        Map<String, Object> result = new HashMap<>();
        TicketDto ticketDto = requestParam.getData();
        TicketStateVo ticketStateVo = null;
        log.info("::{}::限额-最大限额开始:{}", requestParam.getData().getCustomer().getId(), JSONObject.toJSONString(requestParam));
        //限额请求参数校验
        try {
            /**
             * TODO 校验参数化
             */
            parameterValidateHandler.validateMaxBetMoneyBySelectArguments(ticketDto);
            //投注金额*10000
            List<Bet> betList = ticketDto.getBets();
            if (CollectionUtils.isNotEmpty(betList)) {
                List<Bet> list = new ArrayList<>();
                for (Bet bet : betList) {
                    int stake = new BigDecimal(TEN_THOUSAND).multiply(new BigDecimal(bet.getStake().getValue())).intValue();
                    bet.getStake().setValue(stake);
                    list.add(bet);
                }
                ticketDto.setBets(list);
            }
            //调用oddIn限额接口返回限额数据
            TicketMaxStake.TicketMaxStakeResponse ticketMaxStakeResponse = grpcFutureService.queryMaxBetMoneyBySelect(ticketDto);
            ticketStateVo = ticketGrpcHandler.queryMaxBetMoneyBySelect(ticketMaxStakeResponse);
            result.put("ticketStateVo", ticketStateVo);
            log.info("::{}::限额-耗时:{}毫秒,返回:{}", requestParam.getData().getCustomer().getId(), System.currentTimeMillis() - startTime, JSONObject.toJSONString(result));
            return Response.success(ticketStateVo);
        } catch (Exception e) {
            log.error("::{}::oddin最大投注限额查询异常:", requestParam.getData().getCustomer().getId(), e);
            return Response.error(Response.FAIL, "获取最大限额失败");
        }
    }

    /**
     * 根据注单时间全量拉单
     *
     * @param ticketRequestDto
     * @return
     */

    @Override
    public Response pullSingleTime(Request<TicketResultDto> ticketRequestDto) {
        log.info("全量拉单请求入参：{}", JSONUtil.toJsonStr(ticketRequestDto));
        TicketResultDto dto = ticketRequestDto.getData();
        try {
            //全量拉单参数校验
            parameterValidateHandler.validatePullSingleByTimeArguments(dto);
            //请求oddin的拉单接口
            return grpcPullSingleService.pullSingle(dto);
            //返回拉单接口状态
        } catch (Exception e) {
            log.error("::{}::全量拉单失败:{}", ticketRequestDto.getData().getTicketsAfterDto().getRequest_id(), e.getMessage());
            return Response.error(Response.FAIL, "全量拉单失败", e.getMessage());
        }
    }

}
