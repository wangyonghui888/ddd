package com.panda.sport.rcs.mgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum;
import com.panda.sport.data.rcs.dto.order.OrderBeforeHandReqVo;
import com.panda.sport.data.rcs.dto.order.OrderBeforeHandResVo;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mapper.StandardSportTournamentMapper;
import com.panda.sport.rcs.mgr.service.impl.RiskApiServiceImpl;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 查询已用限额 快捷接口
 */
@RestController
@RequestMapping(value = "/risk")
@Slf4j
public class TestController {

    @Autowired
    private RiskApiServiceImpl riskApiService;
    /**
     * @return
     */
    @RequestMapping(value = "/test")
    public Response<OrderBeforeHandResVo> test() {
        Request<OrderBeforeHandReqVo> requestParam = new Request<>();
        OrderBeforeHandReqVo vo = new OrderBeforeHandReqVo();
        vo.setOrderNo("10071390068787");
        requestParam.setData(vo);
        Response<OrderBeforeHandResVo> response = riskApiService.orderBeforeHand(requestParam);
        return response;
    }

}
