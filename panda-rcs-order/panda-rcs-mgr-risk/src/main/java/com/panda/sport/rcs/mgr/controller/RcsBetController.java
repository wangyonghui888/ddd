package com.panda.sport.rcs.mgr.controller;

import com.panda.sport.rcs.mgr.wrapper.RcsBetDataService;
import com.panda.sport.rcs.pojo.dto.RcsRiskOrderDTO;
import com.panda.sport.rcs.vo.HttpResponse;
import com.panda.sports.auth.permission.AuthRequiredPermission;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.panda.sport.rcs.vo.HttpResponse.FAIL;
import static com.panda.sport.rcs.vo.HttpResponse.SUCCESS;

/*
 * @author: paul
 * @Project Name:
 * @Package Name: com.panda.sport.rcs.mgr.controller
 * @Description: 风控里注单相关的操作
 * @Date: 2023-04-24 12:30
 * @ModificationHistory Who    When    What
 */

@Slf4j
@RestController
@RequestMapping(value = "/risk/bet")
public class RcsBetController {
    private final RcsBetDataService rcsBetDataService;

    @Autowired
    public RcsBetController(RcsBetDataService rcsBetDataService) {
        this.rcsBetDataService = rcsBetDataService;
    }

    @PostMapping(value = "overflowMargin")
    @AuthRequiredPermission("rcs:risk:operate")
    public HttpResponse<List<RcsRiskOrderDTO>> getOverflowedMargin(@RequestBody List<String> orderNos) {
        try {
            if (CollectionUtils.isEmpty(orderNos)) {
                return new HttpResponse<>(SUCCESS, Collections.emptyList());
            }
            // 从大数据方先取得危险单关投注的注单
            List<RcsRiskOrderDTO> riskOrders = rcsBetDataService.getRiskyBet(orderNos);
            // 若从大数据拿不到危险单关的注单，就回传空壳数据
            if (CollectionUtils.isEmpty(riskOrders)) {
                log.info("orderNo {} 找不到任何危险单关数据", orderNos);
                return new HttpResponse<>(SUCCESS, Collections.emptyList());
            }

            return new HttpResponse<>(SUCCESS, riskOrders);
        } catch (Exception e) {
        	log.error(e.getMessage());
            return new HttpResponse<>(FAIL, e.getMessage());
        }
    }
}
