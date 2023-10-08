package com.panda.sport.rcs.oddin.grpc.handler.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Timestamp;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.sport.data.rcs.dto.oddin.TicketDto;
import com.panda.sport.data.rcs.dto.oddin.entity.Bet;
import com.panda.sport.data.rcs.dto.oddin.entity.TicketSelection;
import com.panda.sport.data.rcs.dto.oddin.entity.TicketsAfterState;
import com.panda.sport.data.rcs.vo.oddin.*;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.OddinEnum;
import com.panda.sport.rcs.enums.oddin.ResultingStatusEnum;
import com.panda.sport.rcs.oddin.entity.common.pojo.RcsOddinOrder;
import com.panda.sport.rcs.oddin.entity.ots.Enums;
import com.panda.sport.rcs.oddin.entity.ots.TicketMaxStake;
import com.panda.sport.rcs.oddin.entity.ots.TicketOuterClass;
import com.panda.sport.rcs.oddin.entity.ots.TicketResultOuterClass;
import com.panda.sport.rcs.oddin.enums.DataSourceEnum;
import com.panda.sport.rcs.oddin.grpc.handler.TicketGrpcHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;

import static com.panda.sport.rcs.oddin.common.Constants.*;
import static com.panda.sport.rcs.oddin.common.Constants.STAKE;
import static com.panda.sport.rcs.oddin.enums.DataSourceEnum.getCode;
import static com.panda.sport.rcs.oddin.util.ParamUtils.*;
import static com.panda.sport.rcs.oddin.util.ParamUtils.splitOrderNos;

@Slf4j
@Service
public class TicketGrpcHandlerImpl implements TicketGrpcHandler {

    @Resource
    private RedisClient redisClient;

    @Override
    public TicketVo transferResponseToVo(TicketOuterClass.TicketResponse value) {
        TicketVo vo = null;
        if (StringUtils.isNotBlank(value.toString())) {
            vo = new TicketVo();
            vo.setId(splitOrderNo(value.getState().getId()));
            vo.setTicket_status(value.getState().getTicketStatus().toString());

            RejectReasonVo reasonVo = new RejectReasonVo();
            reasonVo.setCode(value.getState().getRejectReason().getCode().toString());
            reasonVo.setMessage(value.getState().getRejectReason().getMessage());
            vo.setReject_reson(reasonVo);

            TicketVo.ExchangeRate exchangeRate = new TicketVo.ExchangeRate();
            exchangeRate.setValue(value.getState().getExchangeRate().getValue());
            vo.setExchange_rate(exchangeRate);

            TicketVo.PendingDelay pendingDelay = new TicketVo.PendingDelay();
            pendingDelay.setValue(value.getState().getPendingDelay().getValue());
            vo.setPending_delay(pendingDelay);

            TicketVo.AutoAcceptedOdds odds = new TicketVo.AutoAcceptedOdds();
            AutoAcceptedOddsVo oddsVo = new AutoAcceptedOddsVo();

            String source = value.getState().getId().split("-")[0];
            Map<String, TicketOuterClass.AutoAcceptedOdds> aaoMap = value.getState().getAutoAcceptedOddsMap();
            if (MapUtils.isNotEmpty(aaoMap)) {
                Iterator<Map.Entry<String, TicketOuterClass.AutoAcceptedOdds>> it = aaoMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, TicketOuterClass.AutoAcceptedOdds> entry = it.next();
                    TicketOuterClass.AutoAcceptedOdds acceptedOdds = entry.getValue();
                    oddsVo.setId(acceptedOdds.getId());
                    //赔率保留小数点位数 DJ:3 TY:2
                    int digit = 3;
                    if (DataSourceEnum.TY.getValue().equals(source)) {
                        digit = 2;
                    }
                    float usedOdds = new BigDecimal(acceptedOdds.getUsedOdds()).divide(new BigDecimal(TEN_THOUSAND), digit, BigDecimal.ROUND_DOWN).floatValue();
                    oddsVo.setUsedOdds(usedOdds);
                    float requestOdds = new BigDecimal(acceptedOdds.getRequestedOdds()).divide(new BigDecimal(TEN_THOUSAND), digit, BigDecimal.ROUND_DOWN).floatValue();
                    oddsVo.setRequestedOdds(requestOdds);
                }
            }
            if (DataSourceEnum.DJ.getValue().equalsIgnoreCase(source)) {
                vo.setSourceId(DataSourceEnum.DJ.getCode());
            }
            if (DataSourceEnum.TY.getValue().equals(source)) {
                vo.setSourceId(DataSourceEnum.TY.getCode());
            }
            odds.setKey(oddsVo.getId());
            odds.setValue(oddsVo);
            vo.setAuto_accepted_odds(odds);
        }

        return vo;
    }

    @Override
    public void transferOrder(RcsOddinOrder order, TicketVo vo) {
        String id = vo.getId();
        String status = vo.getTicket_status();
        String rejectReason = "";
        if (Objects.nonNull(vo.getReject_reson())) {
            rejectReason = vo.getReject_reson().getMessage();
        }
        order.setOrderNo(id);
        order.setStatus(status);
        order.setUpdateTime(new Date());
        if (StringUtils.isNotBlank(rejectReason)) {
            order.setRejectReason(rejectReason);
        }
    }


    /**
     * oddIn请求数据转换方法
     *
     * @param ticketDto
     * @return
     */
    @Override
    public TicketOuterClass.Ticket getTicket(TicketDto ticketDto) {

        String selectionId = null;
        if (MapUtils.isNotEmpty(ticketDto.getSelections())) {
            Iterator<Map.Entry<String, TicketSelection>> iterator = ticketDto.getSelections().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, TicketSelection> entry = iterator.next();
                selectionId = entry.getKey();
            }
        }
        Instant now = Instant.now();
        Integer channelCode = 1;
        if (ticketDto.getChannel() != null) {
            channelCode = ticketDto.getChannel().getCode();
        }
        //票数据
        TicketOuterClass.Ticket.Builder builder = TicketOuterClass.Ticket.newBuilder()
                // 来自运营商系统的唯一票ID
                .setId(montageOrderNo(ticketDto.getId(), ticketDto.getSourceId()))
                // UTC工单放置的时间戳
                .setTimestamp(Timestamp.newBuilder().setSeconds(now.getEpochSecond()).setNanos(now.getNano()).build())
                .setTotalCombinations(ticketDto.getTotalCombinations())
                .setCurrency(ticketDto.getCurrency())
                .setChannel(Enums.TicketChannel.forNumber(channelCode))
                .setAcceptOddsChange(Enums.AcceptOddsChange.forNumber(ticketDto.getAccept_odds_change().getCode()));
        TicketOuterClass.TicketCustomer customers = TicketOuterClass.TicketCustomer.newBuilder()
                //博彩公司唯一ID
                .setId(ticketDto.getCustomer().getId())
                //语言
                .setLanguage(ticketDto.getCustomer().getLanguage()).build();

        TicketOuterClass.TicketSelection ts = TicketOuterClass.TicketSelection.newBuilder().setId(selectionId).setForeign(BoolValue.of(ticketDto.getSelections().get(selectionId).isForeign())).setOdds(new BigDecimal(TEN_THOUSAND).multiply(new BigDecimal(ticketDto.getSelections().get(selectionId).getOdds())).longValue()).build();
        Map<String, TicketOuterClass.TicketSelection> tsMap = new HashMap<>();
        tsMap.put(selectionId, ts);

        //构建注单列表
        for (Bet bets1 : ticketDto.getBets()) {
            TicketOuterClass.BetSelection betSelections = TicketOuterClass.BetSelection.newBuilder().setId(bets1.getSelections()).build();
            TicketOuterClass.BetStake betStakes = TicketOuterClass.BetStake.newBuilder()
                    //股权类型:在支持对一张票进行多次投注之前，默认情况下股权类型应使用 SUM
                    .setType(Enums.BetStakeType.BET_STAKE_TYPE_SUM)
                    //投注的金额
                    .setValue(bets1.getStake().getValue()).build();
            TicketOuterClass.Bet bet1 = TicketOuterClass.Bet.newBuilder().setStake(betStakes).addSelections(betSelections).addSystems(bets1.getSystems()[0]).setSystems(0, (bets1.getSystems()[0])).build();
            builder.addBets(bet1);
            builder.setCustomer(customers);
            builder.putAllSelections(tsMap);
        }
        /* builder.setCustomer(customer);*/
        TicketOuterClass.Ticket ticket = builder.build();
        return ticket;
    }

    /**
     * 将赛事/盘口/用户组/赛事类型放到缓存，后续需要用到
     *
     * @param ticketDto
     */
    @Override
    public void putTyMatch2cache(TicketDto ticketDto) {
        Map<String, String> tyMatchInfo = new HashMap<>();
        tyMatchInfo.put("matchId", String.valueOf(ticketDto.getMatchId()));
        tyMatchInfo.put("marketId", String.valueOf(ticketDto.getMarketId()));
        tyMatchInfo.put("orderGroup", ticketDto.getOrderGroup());
        tyMatchInfo.put("matchType", String.valueOf(ticketDto.getMatchType()));
        String key = String.format(TY_MATCH_ID_KEY, ticketDto.getId());
        redisClient.setExpiry(key, JSONObject.toJSONString(tyMatchInfo), 5 * 60 * 1000L);
        log.info("::orderNo:{}:将TY注单的matchId缓存成功:matchId:{}:key:{}", ticketDto.getId(), ticketDto.getMatchId(), key);
    }

    @Override
    public TicketResultVo ticketResultResponse(TicketResultOuterClass.TicketResultResponse value) {
        Integer won_amount = null;
        Integer exchange_rate = null;
        TicketResultState resultState = null;
        TicketResultVo vo = new TicketResultVo();
        //全量拉单的数据
        if (value.getAfter().getTicketsList().size() > 0) {
            List<TicketResultState> resultStateList = new ArrayList<>();
            TicketsAfterState ticketsAfterState = null;
            List<TicketResultOuterClass.TicketResultState> ticketResultStates = value.getAfter().getTicketsList();
            //循环遍历获取所有得注单数据
            for (TicketResultOuterClass.TicketResultState t : ticketResultStates) {
                ticketsAfterState = new TicketsAfterState();
                resultState = new TicketResultState();
                resultState.setId(splitOrderNo(t.getId()));
                if (splitOrderNos(t.getId()).contains("TY")) {
                    won_amount = (new BigDecimal(new Long(t.getWonAmount().getValue()).intValue())).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP).intValue();

                    exchange_rate = (new BigDecimal(Integer.valueOf(Long.valueOf(t.getExchangeRate()).intValue()))).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP).intValue();
                } else {
                    won_amount = (new BigDecimal(new Long(t.getExchangeRate()).intValue())).divide(new BigDecimal(10000), 3, RoundingMode.HALF_UP).intValue();

                    exchange_rate = (new BigDecimal(Integer.valueOf(Long.valueOf(t.getExchangeRate()).intValue()))).divide(new BigDecimal(10000), 3, RoundingMode.HALF_UP).intValue();

                }
                resultState.setWon_amount(won_amount);
                resultState.setExchange_rate(exchange_rate);
                TicketResultError ticketResultError = new TicketResultError();
                ticketResultError.setMessage(t.getError().getMessage().toString());
                ticketResultError.setCode(ReasonCode.valueOf(t.getError().getCode().toString()));
                resultState.setError(ticketResultError);
                //将UTC时间转换为UTC+8的时间
                Long times = TimeUtils.timeSecondsTimeZone(t.getResultedAt().getSeconds(), -8);
                resultState.setResulted_at(times);
                resultState.setTicket_status(ResultingStatusEnum.forNumber(t.getTicketStatusValue()));
                resultStateList.add(resultState);
            }
            ticketsAfterState.setTicketResultStateList(resultStateList);
            //请求的ID用于响应拉单的请求
            ticketsAfterState.setRequest_id(value.getAfter().getRequestId());
            //响应得注单时间UTC+8
            Long times = TimeUtils.timeSecondsTimeZone(value.getAfter().getAfter().getSeconds(), 8);
            //请求的拉单时间
            ticketsAfterState.setTime(times);

            vo.setTicketsAfterState(ticketsAfterState);
            //体育/电竞拉单数据
        } else {
            resultState = new TicketResultState();
            //把拼接的注单TY/DJ标签区分截取掉
            resultState.setId(splitOrderNo(value.getState().getId()));
            //要根据TY/DJ标识通过枚举类获取到得到对应匹配的SourID值
            resultState.setSourceId(getCode(splitOrderNos(value.getState().getId())));
            resultState.setTicket_status(ResultingStatusEnum.forNumber(value.getState().getTicketStatusValue()));
            //判断是体育的注单还是电竞的注单,派奖金额/汇率保留的小数不同
            if (splitOrderNos(value.getState().getId()).contains("TY")) {
                won_amount = (new BigDecimal(new Long(value.getState().getWonAmount().getValue()).intValue())).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP).intValue();

                exchange_rate = (new BigDecimal(Integer.valueOf(Long.valueOf(value.getState().getExchangeRate()).intValue()))).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP).intValue();
            } else {
                won_amount = (new BigDecimal(new Long(value.getState().getWonAmount().getValue()).intValue())).divide(new BigDecimal(10000), 3, RoundingMode.HALF_UP).intValue();

                exchange_rate = (new BigDecimal(Integer.valueOf(Long.valueOf(value.getState().getExchangeRate()).intValue()))).divide(new BigDecimal(10000), 3, RoundingMode.HALF_UP).intValue();

            }
            //派奖金额
            resultState.setWon_amount(won_amount);
            //注单的汇率
            resultState.setExchange_rate(exchange_rate);
            TicketResultError ticketResultError = new TicketResultError();
            ticketResultError.setMessage(value.getState().getError().getMessage().toString());
            ticketResultError.setCode(ReasonCode.valueOf(value.getState().getError().getCode().toString()));
            resultState.setError(ticketResultError);
            //将UTC时间转换为UTC+8的时间
            Long times = TimeUtils.timeSecondsTimeZone(value.getState().getModifiedAt().getSeconds(), -8);
            resultState.setResulted_at(times);
            vo.setResultState(resultState);

        }
        return vo;
    }

    @Override
    public TicketStateVo queryMaxBetMoneyBySelect(TicketMaxStake.TicketMaxStakeResponse ticketMaxStakeResponse) {
        TicketStateVo ticketStateVo = new TicketStateVo();
        //注单ID
        ticketStateVo.setId(splitOrderNo(ticketMaxStakeResponse.getState().getId()));
        //接收的状态
        ticketStateVo.setTicket_status(OddinEnum.AcceptanceStatus.forNumber(ticketMaxStakeResponse.getState().getTicketStatusValue()));
        //总赔率
        ticketStateVo.setTotal_odds((ticketMaxStakeResponse.getState().getTotalOdds()));
        //拒绝的原因信息
        /* TicketRejectReason ticketRejectReason = new TicketRejectReason();*/
        TicketRejectReason ticketRejectReason = new TicketRejectReason();
        ticketRejectReason.setMessage(ticketMaxStakeResponse.getState().getRejectReason().getMessage());
        ticketRejectReason.setCode(Code.forNumber(ticketMaxStakeResponse.getState().getRejectReason().getCodeValue()));
        ticketStateVo.setReject_reason(ticketRejectReason);
        //选择的拒绝详细信息
        Map<String, TicketOuterClass.TicketSelectionRejectReason> selection_info = ticketMaxStakeResponse.getState().getSelectionInfo();
        //判断selection_info的集合大小是否大于0
        if (CollectionUtils.size(selection_info) > 0) {
            Map<String, TicketSelectionRejectReason> selection_infos = new HashMap<>();
            for (Map.Entry<String, TicketOuterClass.TicketSelectionRejectReason> entry : selection_info.entrySet()) {
                TicketSelectionRejectReason rejectReason = new TicketSelectionRejectReason();
                rejectReason.setCode(SelectionRejectReasonCode.valueOf(entry.getValue().getCode().toString()));
                rejectReason.setMessage(entry.getValue().getMessage());
                selection_infos.put(entry.getKey(), rejectReason);
            }
            ticketStateVo.setSelection_info(selection_infos);
        }
        //拒绝投注的详细信息(非必传)
        Map<String, TicketOuterClass.TicketResponseBetInfo> betInfoMap = ticketMaxStakeResponse.getState().getBetInfoMap();
        //判断betInfoMap的集合大小是否大于0
        if (CollectionUtils.size(betInfoMap) > 0) {
            Map<String, TicketResponseBetInfo> bet_info = new HashMap<>();
            //整个投注的总赔率
            long total_odds = 0L;
            //限额
            float stake = 0F;
            //汇率
            Integer exchange_rate = null;
            TicketResponseBetInfo betInfo = new TicketResponseBetInfo();
            TicketBetRejectReason ticketBetRejectReason = new TicketBetRejectReason();
            ResponseReoffer reoffer = new ResponseReoffer();
            for (Map.Entry<String, TicketOuterClass.TicketResponseBetInfo> entry : betInfoMap.entrySet()) {

                betInfo.setId(entry.getValue().getId());
                //根据返回的注单ID判断是体育的注单还是电竞的注单(限额/赔率/汇率等等保留的小数点位数不同)
                if (splitOrderNos(ticketMaxStakeResponse.getState().getId()).contains("TY")) {
                    //整个投注的总赔率
                    total_odds = new BigDecimal(entry.getValue().getTotalOdds()).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP).longValue();
                    //限额
                    stake = new BigDecimal(entry.getValue().getReoffer().getStake()).divide(STAKE, 2, RoundingMode.HALF_UP).floatValue();
                    //汇率
                    exchange_rate = new BigDecimal(Integer.valueOf(String.valueOf(ticketMaxStakeResponse.getState().getExchangeRate().getValue()))).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP).intValue();
                } else {
                    //整个投注的总赔率
                    total_odds = new BigDecimal(entry.getValue().getTotalOdds()).divide(new BigDecimal(10000), 3, RoundingMode.HALF_UP).longValue();
                    //限额
                    stake = new BigDecimal(entry.getValue().getReoffer().getStake()).divide(STAKE, 3, RoundingMode.HALF_UP).floatValue();
                    //汇率
                    exchange_rate = new BigDecimal(Integer.valueOf(String.valueOf(ticketMaxStakeResponse.getState().getExchangeRate().getValue()))).divide(new BigDecimal(10000), 3, RoundingMode.HALF_UP).intValue();
                }
                reoffer.setStake(stake);
                ticketBetRejectReason.setCode(TicketBetRejectReasonCode.valueOf(entry.getValue().getRejectReason().getCode().toString()));
                ticketBetRejectReason.setMessage(entry.getValue().getRejectReason().getMessage());
                betInfo.setTotal_odds(total_odds);
                betInfo.setReject_reason(ticketBetRejectReason);
                betInfo.setReoffer(reoffer);
                bet_info.put(entry.getKey(), betInfo);
            }
            ticketStateVo.setBet_info(bet_info);
            ticketStateVo.setExchange_rate(exchange_rate);
        }

        //用于接受投注的替代赔率
        Map<String, TicketOuterClass.AutoAcceptedOdds> oddsMap = ticketMaxStakeResponse.getState().getAutoAcceptedOddsMap();
        if (CollectionUtils.size(oddsMap) > 0) {
            Integer requested_odds = null;
            Integer used_odds = null;
            Map<String, AutoAcceptedOddsVo> acceptedOddsMap = new HashMap<>();
            AutoAcceptedOddsVo odds = new AutoAcceptedOddsVo();
            for (Map.Entry<String, TicketOuterClass.AutoAcceptedOdds> entry : oddsMap.entrySet()) {
                //选项ID
                odds.setId(entry.getValue().getId());
                if (splitOrderNos(ticketStateVo.getId()).contains("TY")) {
                    //请求的赔率
                    requested_odds = (new BigDecimal(Integer.valueOf(Long.valueOf(entry.getValue().getRequestedOdds()).toString())).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP).intValue());
                    //使用的赔率
                    used_odds = (new BigDecimal(Integer.valueOf(Long.valueOf(entry.getValue().getUsedOdds()).toString())).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP).intValue());
                } else {
                    //请求的赔率
                    requested_odds = (new BigDecimal(Integer.valueOf(Long.valueOf(entry.getValue().getRequestedOdds()).toString())).divide(new BigDecimal(10000), 3, RoundingMode.HALF_UP).intValue());
                    //使用的赔率
                    used_odds = (new BigDecimal(Integer.valueOf(Long.valueOf(entry.getValue().getUsedOdds()).toString())).divide(new BigDecimal(10000), 3, RoundingMode.HALF_UP).intValue());
                }
                odds.setRequestedOdds(requested_odds);
                odds.setUsedOdds(used_odds);
                acceptedOddsMap.put(entry.getKey(), odds);
            }
            //自动接受赔率的变化
            ticketStateVo.setAuto_accepted_odds(acceptedOddsMap);
        }
        //等待延迟
        ticketStateVo.setPending_delay(Integer.valueOf(Long.valueOf(ticketMaxStakeResponse.getState().getPendingDelay().getValue()).toString()));
        return ticketStateVo;
    }
}
