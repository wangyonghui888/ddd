package com.panda.sport.rcs.oddin.service.handler.impl;

import com.panda.sport.data.rcs.dto.oddin.CancelOrderDto;
import com.panda.sport.data.rcs.dto.oddin.TicketDto;
import com.panda.sport.data.rcs.dto.oddin.TicketResultDto;
import com.panda.sport.data.rcs.dto.oddin.entity.Bet;
import com.panda.sport.data.rcs.dto.oddin.entity.TicketSelection;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.oddin.service.handler.ParameterValidateHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Map;

@Slf4j
@Service
public class ParameterValidateHandlerImpl implements ParameterValidateHandler {


    /**
     * 校验注单入参
     *
     * @param dto
     */
    @Override
    public void validateSaveArguments(TicketDto dto) {
        if (dto.getSourceId() == null) {
            log.error("注单orderNo:{} -dateSourceId 数据来源id不能为空", dto.getId());
            throw new RcsServiceException("dateSourceId 数据来源id不能为空");
        }
        if (StringUtils.isBlank(dto.getId())) {
            log.error("id,订单号不能为空");
            throw new RcsServiceException("id 订单编号不能为空");
        }
        if (dto.getTimestamp() == null) {
            log.error("注单orderNo:{}-timestamp 不能为空", dto.getId());
            throw new RcsServiceException("timestamp 不能为空");
        }
        if (dto.getTotalCombinations() == null) {
            log.error("注单rderNo:{}-totalCombinations 不能为空", dto.getId());
            throw new RcsServiceException("totalCombinations 不能为空");
        }
        if (StringUtils.isBlank(dto.getCurrency())) {
            log.error("注单rderNo:{}-currency 币种不能为空", dto.getId());
            throw new RcsServiceException("currency 币种不能为空");
        }
        if (dto.getAccept_odds_change() == null) {
            log.error("注单orderNo:{}-accept_odds_change是否自动接受赔率的变化不能为空", dto.getId());
            throw new RcsServiceException("accept_odds_change 是否自动接受赔率的变化不能为空");
        }
        if (CollectionUtils.isEmpty(dto.getBets())) {
            log.error("注单orderNo:{}-bets 投注列表不能为空", dto.getId());
            throw new RcsServiceException("bets 投注列表不能为空 不能为空");
        }
        for (Bet bet : dto.getBets()) {
            if (bet.getStake().getValue() == null) {
                log.error("注单orderNo:{}-bets-stake-value 投注金额不能为空", dto.getId());
                throw new RcsServiceException("bets-stake-value 投注金额不能为空");
            }
            if (bet.getStake().getType() == null) {
                log.error("注单orderNo:{}-bets-stake-type 股权类型不能为空", dto.getId());
                throw new RcsServiceException("bets-systems 股权类型不能为空");
            }
            if (ArrayUtils.isEmpty(bet.getSystems())) {
                log.error("注单orderNo:{}-bets-systems 投注中所有系统的数组不能为空", dto.getId());
                throw new RcsServiceException("bets-systems 投注中所有系统的数组不能为空");
            }
            if (StringUtils.isBlank(bet.getSelections())) {
                log.error("注单orderNo:{}-bets-selections 形成此投注的选项数组来自Selection不能为空", dto.getId());
                throw new RcsServiceException("bets-selections 形成此投注的选项数组来自Selection不能为空");
            }
        }
        if (dto.getCustomer() == null) {
            log.error("注单orderNo:{}-customer 用户信息不能为空", dto.getId());
            throw new RcsServiceException("betscustomer 用户信息不能为空");
        }
        if (StringUtils.isBlank(dto.getCustomer().getId())) {
            log.error("注单orderNo:{}-customer-id 用户id不能为空", dto.getId());
            throw new RcsServiceException("customer-id 用户id不能为空");
        }
        if (StringUtils.isBlank(dto.getCustomer().getLanguage())) {
            log.error("注单rderNo:{}-customer-language 支持语言不能为空", dto.getId());
            throw new RcsServiceException("customer-language 支持语言不能为空");
        }
        if (dto.getLocation_id() == null) {
            log.error("注单orderNo:{}-location_id 商户id不能为空", dto.getId());
            throw new RcsServiceException("location_id 商户id不能为空");
        }
//        if (dto.getKeepalive() == null) {
//            log.error("注单orderNo:{}-keepalive 不能为空", dto.getId());
//            throw new RcsServiceException("keepalive 不能为空");
//        }
//        if (dto.getKeepalive().getTimestamp() == null) {
//            log.error("注单orderNo:{}-keepalive-timestamp 不能为空", dto.getId());
//            throw new RcsServiceException("keepalive-timestamp 不能为空");
//        }
        if (MapUtils.isEmpty(dto.getSelections())) {
            log.error("注单orderNo:{}-selections 不能为空", dto.getId());
            throw new RcsServiceException("selections 不能为空");
        }
        for (Map.Entry<String, TicketSelection> entry : dto.getSelections().entrySet()) {
            TicketSelection selection = entry.getValue();
            if (StringUtils.isBlank(selection.getId())) {
                log.error("注单orderNo:{}-selections-id 不能为空", dto.getId());
                throw new RcsServiceException("selections-id 不能为空");
            }
            if (StringUtils.isBlank(selection.getOdds())) {
                log.error("注单orderNo:{}-selections-odds 不能为空", dto.getId());
                throw new RcsServiceException("selections-odds 不能为空");
            }
        }
    }

    @Override
    public void validateMaxBetMoneyBySelectArguments(TicketDto dto) {
        if (dto.getSourceId() == null) {
            log.error("注单orderNo:{} -dateSourceId 数据来源id不能为空", dto.getId());
            throw new RcsServiceException("dateSourceId 数据来源id不能为空");
        }
        if (StringUtils.isBlank(dto.getId())) {
            log.error("id,订单号不能为空");
            throw new RcsServiceException("id 订单编号不能为空");
        }
        if (dto.getTimestamp() == null) {
            log.error("注单orderNo:{}-timestamp 不能为空", dto.getId());
            throw new RcsServiceException("timestamp 不能为空");
        }
        if (dto.getTotalCombinations() == null) {
            log.error("注单rderNo:{}-totalCombinations 不能为空", dto.getId());
            throw new RcsServiceException("totalCombinations 不能为空");
        }
        if (StringUtils.isBlank(dto.getCurrency())) {
            log.error("注单rderNo:{}-currency 币种不能为空", dto.getId());
            throw new RcsServiceException("currency 币种不能为空");
        }
        if (dto.getAccept_odds_change() == null) {
            log.error("注单orderNo:{}-accept_odds_change是否自动接受赔率的变化不能为空", dto.getId());
            throw new RcsServiceException("accept_odds_change 是否自动接受赔率的变化不能为空");
        }
        if (CollectionUtils.isEmpty(dto.getBets())) {
            log.error("注单orderNo:{}-bets 投注列表不能为空", dto.getId());
            throw new RcsServiceException("bets 投注列表不能为空 不能为空");
        }
        for (Bet bet : dto.getBets()) {
            if (bet.getStake().getValue() == null) {
                log.error("注单orderNo:{}-bets-stake-value 投注金额不能为空", dto.getId());
                throw new RcsServiceException("bets-stake-value 投注金额不能为空");
            }
            if (bet.getStake().getType() == null) {
                log.error("注单orderNo:{}-bets-stake-type 股权类型不能为空", dto.getId());
                throw new RcsServiceException("bets-systems 股权类型不能为空");
            }
            if (ArrayUtils.isEmpty(bet.getSystems())) {
                log.error("注单orderNo:{}-bets-systems 投注中所有系统的数组不能为空", dto.getId());
                throw new RcsServiceException("bets-systems 投注中所有系统的数组不能为空");
            }
            if (StringUtils.isBlank(bet.getSelections())) {
                log.error("注单orderNo:{}-bets-selections 形成此投注的选项数组来自Selection不能为空", dto.getId());
                throw new RcsServiceException("bets-selections 形成此投注的选项数组来自Selection不能为空");
            }
        }
        if (dto.getCustomer() == null) {
            log.error("注单orderNo:{}-customer 用户信息不能为空", dto.getId());
            throw new RcsServiceException("betscustomer 用户信息不能为空");
        }
        if (StringUtils.isBlank(dto.getCustomer().getId())) {
            log.error("注单orderNo:{}-customer-id 用户id不能为空", dto.getId());
            throw new RcsServiceException("customer-id 用户id不能为空");
        }
        if (StringUtils.isBlank(dto.getCustomer().getLanguage())) {
            log.error("注单rderNo:{}-customer-language 支持语言不能为空", dto.getId());
            throw new RcsServiceException("customer-language 支持语言不能为空");
        }
       /* if (dto.getKeepalive() == null) {
            log.error("注单orderNo:{}-keepalive 不能为空", dto.getId());
            throw new RcsServiceException("keepalive 不能为空");
        }*/
//        if (dto.getKeepalive().getTimestamp() == null) {
//            log.error("注单orderNo:{}-keepalive-timestamp 不能为空", dto.getId());
//            throw new RcsServiceException("keepalive-timestamp 不能为空");
//        }
        if (MapUtils.isEmpty(dto.getSelections())) {
            log.error("注单orderNo:{}-selections 不能为空", dto.getId());
            throw new RcsServiceException("selections 不能为空");
        }
        for (Map.Entry<String, TicketSelection> entry : dto.getSelections().entrySet()) {
            TicketSelection selection = entry.getValue();
            if (StringUtils.isBlank(selection.getId())) {
                log.error("注单orderNo:{}-selections-id 不能为空", dto.getId());
                throw new RcsServiceException("selections-id 不能为空");
            }
            if (StringUtils.isBlank(selection.getOdds())) {
                log.error("注单orderNo:{}-selections-odds 不能为空", dto.getId());
                throw new RcsServiceException("selections-odds 不能为空");
            }
        }
    }

    @Override
    public void validateCancelArguments(CancelOrderDto dto) {
        if (StringUtils.isBlank(dto.getId())) {
            log.error("撤单，id,订单号不能为空");
            throw new RcsServiceException("id 订单编号不能为空");
        }
        if (dto.getCancelReason() == null) {
            log.error("撤单orderNo:{}-cancelReason 撤单原因不能为空", dto.getId());
            throw new RcsServiceException("cancelReason 撤单原因不能为空");
        }
        if (StringUtils.isBlank(dto.getCancelReasonDetail())) {
            log.error("撤单orderNo:{}-cancelReasonDetail 撤单原因详情不能为空", dto.getId());
            throw new RcsServiceException("cancelReasonDetail 撤单原因详情不能为空");
        }
    }

    //全量拉单参数效验
    @Override
    public void validatePullSingleByTimeArguments(TicketResultDto ticketRequestDto) {
        if (StringUtils.isBlank(ticketRequestDto.getTicketsAfterDto().getRequest_id())) {
            log.error("全量拉单，requestId,请求ID不能为空");
            throw new RcsServiceException("请求requestId不能为空");
        }
        Timestamp after = ticketRequestDto.getTicketsAfterDto().getAfter();
        if (null == after) {
            log.error("全量拉单，after,拉单时间戳不能为空");
            throw new RcsServiceException("after,拉单时间戳不能为空");
        }
    }

    //单个拉单参数效验
    @Override
    public void validatePullSingleByOrderNoArguments(TicketResultDto resultDto) {
        if (StringUtils.isBlank(resultDto.getId())) {
            log.error("单个拉单，id,订单号不能为空");
            throw new RcsServiceException("id 订单编号不能为空");
        }
        if (resultDto.getSourceId() == null) {
            log.error("单个拉单orderNo:{} -dateSourceId 数据来源id不能为空", resultDto.getId());
            throw new RcsServiceException("dateSourceId 数据来源id不能为空");
        }
    }
}
