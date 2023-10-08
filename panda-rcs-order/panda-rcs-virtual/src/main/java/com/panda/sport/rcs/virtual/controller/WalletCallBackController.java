package com.panda.sport.rcs.virtual.controller;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.collect.Lists;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.virtual.RcsVirtualOrderExt;
import com.panda.sport.rcs.service.IRcsVirtualOrderExtService;
import com.panda.sport.rcs.virtual.enums.CreditEnum;
import com.panda.sport.rcs.virtual.enums.SellEnum;
import com.panda.sport.rcs.virtual.enums.SolveEnum;
import com.panda.sport.rcs.virtual.service.VirtualDataServiceImpl;
import io.swagger.client.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 回调接口
 *
 * @author : lithan
 */
@RestController
@RequestMapping("/wallet")
@Slf4j
public class WalletCallBackController {

    @Autowired
    VirtualDataServiceImpl dataService;

    @Autowired
    IRcsVirtualOrderExtService rcsVirtualOrderExtService;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;


    @Autowired
    IRcsVirtualOrderExtService virtualOrderExtService;
    /**
     * 确认出售
     * (WebConfigurer的配置在这里生效)
     * @param bulkRequestSell
     * @return
     */
    @PostMapping("/sell")
    public List<SellResponse> walletSell(@RequestBody List<SellRequest> bulkRequestSell) {
        log.info("回调sell方法:{}", JSONObject.toJSONString(bulkRequestSell));
        List<SellResponse> responseList = new ArrayList<>();
        if (CollUtil.isNotEmpty(bulkRequestSell)) {
            bulkRequestSell.stream().forEach(object -> {
                SellResponse response = new SellResponse();
                responseList.add(response);
                response.setType(WalletResponse.TypeEnum.SELLRESPONSE);
                response.setTicketId(object.getTicketId());
                response.setErrorId(SellEnum.SELL_SUCCESS.getCode());
                response.setErrorMessage(SellEnum.SELL_SUCCESS.getMessage());
                response.setResult(WalletResponse.ResultEnum.SUCCESS);
//                response.setExtTransactionID();
//                response.setExtTicketId();
                response.setOldCredit(0D);
                response.setNewCredit(0D);
            });

        }
        return responseList;
    }



    /**
     * 取消,V2G拒单
     *
     * @param bulkRequestCancel
     * @return
     */
    @PostMapping("/cancel")
    public List<WalletCreditResponse> walletCancel(@RequestBody List<PayoutRequest> bulkRequestCancel) {
        log.info("回调cancel方法:{}", JSONObject.toJSONString(bulkRequestCancel));

        for (PayoutRequest payoutRequest : bulkRequestCancel) {
            Long ticketId = payoutRequest.getTicketId();
            String responseStatus = payoutRequest.getTicket().getStatus().getValue();
            //更新库
            LambdaUpdateWrapper<RcsVirtualOrderExt> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(RcsVirtualOrderExt::getTicketId, ticketId);
            RcsVirtualOrderExt ext = rcsVirtualOrderExtService.getOne(wrapper);
            if (ext == null) {
                log.info("::{}::取消订单通知订单未找到完成", ticketId);
                continue;
            }
            String orderNo = ext.getOrderNo();
            dataService.updateThirdOrder(Lists.newArrayList(orderNo), "已取消:" + ext.getRemark(), 3, responseStatus);
            log.info("::{}::取消订单后更新数据库完成", orderNo);
            //通知业务
            Map<String, Object> map = new HashMap<>();
            map.put("orderNo", orderNo);
            map.put("status", responseStatus);
            producerSendMessageUtils.sendMessage("rcs_virtual_cancel_order,," + orderNo, map);
            log.info("::{}::通知业务完成-取消订单:{}", ext.getOrderNo(), JSONObject.toJSONString(map));
        }

        List<WalletCreditResponse> responseList = new ArrayList<>();
        if (CollUtil.isNotEmpty(bulkRequestCancel)) {
            bulkRequestCancel.stream().forEach(object -> {
                WalletCreditResponse response = new WalletCreditResponse();
                responseList.add(response);
                response.setType(WalletResponse.TypeEnum.WALLETCREDITRESPONSE);
                response.setTicketId(object.getTicketId());
                response.setErrorId(CreditEnum.CREDIT_SUCCESS.getCode());
                response.setErrorMessage(CreditEnum.CREDIT_SUCCESS.getMessage());
                response.setResult(WalletResponse.ResultEnum.SUCCESS);
                response.setOldCredit(0D);
                response.setNewCredit(0D);
            });
        }
        return responseList;
    }

    /**
     * 备用
     **/
    @PostMapping("/login")
    public WalletLoginResponse walletLogin(@RequestBody WalletLoginRequest loginRequest) {
        log.info("回调login方法:{}", JSONObject.toJSONString(loginRequest));
        WalletLoginResponse response = new WalletLoginResponse();
        response.setExtToken("test");
        response.setCredit(100D);
        response.setCurrencyCode("RMB");
        response.setExtTransactionData("test");
        return response;
    }

    /**
     * 派彩
     *
     * @param bulkRequestPayout
     * @return
     */
    @PostMapping("/payout")
    public List<WalletCreditResponse> walletPayout(@RequestBody List<PayoutRequest> bulkRequestPayout) {
        log.info("回调进入payout方法:{}", JSONObject.toJSONString(bulkRequestPayout));
        List<WalletCreditResponse> responseList = new ArrayList<>();
        if (CollUtil.isNotEmpty(bulkRequestPayout)) {
            bulkRequestPayout.stream().forEach(object -> {
                WalletCreditResponse response = new WalletCreditResponse();
                responseList.add(response);
                response.setType(WalletResponse.TypeEnum.WALLETCREDITRESPONSE);
                response.setTicketId(object.getTicketId());
                response.setErrorId(CreditEnum.CREDIT_SUCCESS.getCode());
                response.setErrorMessage(CreditEnum.CREDIT_SUCCESS.getMessage());
                response.setResult(WalletResponse.ResultEnum.SUCCESS);
                response.setOldCredit(0D);
                response.setNewCredit(0D);
            });
        }
        return responseList;
    }

    /**
     * 备用
     *     LOCKED("LOCKED"),
     *     REJECTED("REJECTED"),
     *     OPEN("OPEN"),
     *     PENDING("PENDING"),
     *     CANCELLING("CANCELLING"),
     *     CANCELLED("CANCELLED"),
     *     WON("WON"),
     *     LOST("LOST"),
     *     PAIDOUT("PAIDOUT"),
     *     EXPIRED("EXPIRED");
     * @param bulkRequestSolve
     * @return
     */
    @PostMapping("/solve")
    public List<WalletResponse> walletSolve(@RequestBody List<SolveRequest> bulkRequestSolve) {
        log.info("进入solve方法:{}", JSONObject.toJSONString(bulkRequestSolve));

        List<WalletResponse> responseList = new ArrayList<>();
        for (SolveRequest solveRequest : bulkRequestSolve) {
            Long ticketId = solveRequest.getTicketId();
            try {
                log.info("::{}::开始处理:{}", ticketId, JSONObject.toJSONString(solveRequest));
                String responseStatus = solveRequest.getTicket().getStatus().getValue();
                //更新库
                LambdaUpdateWrapper<RcsVirtualOrderExt> wrapper = new LambdaUpdateWrapper<>();
                wrapper.eq(RcsVirtualOrderExt::getTicketId, ticketId);
                wrapper.last(" order by id desc limit 1 ");
                RcsVirtualOrderExt ext = rcsVirtualOrderExtService.getOne(wrapper);
                if (ext == null) {
                    log.info("::{}::结算通知订单未找到完成", ticketId);
                }else {
                    String orderNo = ext.getOrderNo();
                    String remark = ext.getRemark();
                    if (!remark.contains("已结算")) {
                        remark = "已结算:" + ext.getRemark();
                    }
                    dataService.updateThirdOrder(Lists.newArrayList(orderNo), remark, null, responseStatus);
                    log.info("::{}::结算后更新数据库完成", orderNo);
                    //通知业务
                    Map<String, Object> map = new HashMap<>();
                    map.put("orderNo", orderNo);
                    map.put("status", responseStatus);
                    map.put("wonData", bulkRequestSolve.get(0).getTicket().getWonData());
                    producerSendMessageUtils.sendMessage("rcs_virtual_solve_order,," + orderNo, map);
                    log.info("::{}::通知业务完成-已经结算订单:{}", ext.getOrderNo(), JSONObject.toJSONString(map));
                }
                WalletResponse response = new WalletResponse();
                responseList.add(response);
                response.setType(WalletResponse.TypeEnum.WALLETRESPONSE);
                response.setTicketId(solveRequest.getTicketId());
                response.setResult(WalletResponse.ResultEnum.SUCCESS);
                response.setErrorId(SolveEnum.SOLVE_SUCCESS.getCode());
                response.setErrorMessage(SolveEnum.SOLVE_SUCCESS.getMessage());

            } catch (Exception e) {
                log.info("::{}::处理ticketId异常:{}:{}", ticketId, e.getMessage(), e);
            }
        }
        return responseList;
    }

    /**
     * 获取第三方订单号
     * @param orderNo panda订单号
     * @return
     */
    @RequestMapping("/getTransactionId")
    public RcsVirtualOrderExt getTransactionId(@RequestParam("orderNo") String orderNo) {
        String logPrefix = "查询虚拟体育第三方id";
        if (StringUtils.isEmpty(orderNo)) {
            log.error("::{}::原始订单号不能为空！", orderNo);
            return new RcsVirtualOrderExt();
        }
        LambdaQueryWrapper<RcsVirtualOrderExt> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RcsVirtualOrderExt::getOrderNo, orderNo);
        RcsVirtualOrderExt ext = virtualOrderExtService.getOne(queryWrapper);
        if (ext == null) {
            log.info("::{}::logPrefix：{} 查询第三方信息：{}", orderNo,logPrefix, "无订单");
            return new RcsVirtualOrderExt();
        }
        RcsVirtualOrderExt res = new RcsVirtualOrderExt();
        res.setOrderNo(ext.getOrderNo());
        res.setOrderStatus(ext.getOrderStatus());
        res.setTicketId(ext.getTicketId());
        res.setResponseStatus(ext.getResponseStatus());
        res.setVirtualUserId(ext.getVirtualUserId());
        log.info("::{}::logPrefix:{},查询第三方信息：{}", orderNo,logPrefix, JSONObject.toJSONString(res));
        return res;
    }
}
