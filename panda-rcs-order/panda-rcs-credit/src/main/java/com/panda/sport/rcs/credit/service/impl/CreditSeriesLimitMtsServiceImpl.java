package com.panda.sport.rcs.credit.service.impl;

import com.alibaba.fastjson.JSON;
import com.panda.sport.data.rcs.api.MtsApiService;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.MtsgGetMaxStakeDTO;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.credit.service.CreditLimitService;
import com.panda.sport.rcs.pojo.limit.RedisUpdateVo;
import com.panda.sport.rcs.utils.SeriesUtils;
import com.panda.sport.rcs.vo.RcsBusinessPlayPaidConfigVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用串关限额MTS服务
 * @Author : Paca
 * @Date : 2021-05-09 19:54
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Service("creditSeriesLimitMtsService")
public class CreditSeriesLimitMtsServiceImpl extends CreditSeriesLimitServiceImpl implements CreditLimitService {

    @Reference
    private MtsApiService mtsApiService;

    @Override
    protected List<RcsBusinessPlayPaidConfigVo> queryBetLimit(OrderBean orderBean, List<OrderItem> orderItems) {
        List<RcsBusinessPlayPaidConfigVo> paList = super.queryBetLimit(orderBean, orderItems);
        log.info("信用额度，MTS限额，先查询PA限额：paList={}", JSON.toJSONString(paList));
        List<ExtendBean> extendBeanList = orderItems.stream().map(orderItem -> buildExtendBean(orderBean, orderItem)).collect(Collectors.toList());
        paList.forEach(vo -> {
            // 最高可投小于最低可投时，最高可投统一设置为0
            if (vo.getOrderMaxPay() < vo.getMinBet()) {
                vo.setOrderMaxPay(0L);
                return;
            }
            Integer seriesType = NumberUtils.toInt(vo.getType());
            int seriesNum = SeriesUtils.getSeriesNum(seriesType);
            int count = SeriesUtils.getCount(seriesType, seriesNum);
            Request<MtsgGetMaxStakeDTO> request = new Request<>();
            request.setGlobalId(MDC.get("X-B3-TraceId") + "_" + seriesType);
            if (count == 1) {
                request.setData(new MtsgGetMaxStakeDTO(extendBeanList, seriesNum, false));
            } else {
                request.setData(new MtsgGetMaxStakeDTO(extendBeanList, seriesNum, true));
            }
            Long amount;
            try {
                amount = mtsApiService.getMaxStake(request).getData();
            } catch (Exception e) {
                log.error("信用额度，获取MTS限额异常", e);
                amount = 2000L;
            }
            log.info("信用额度，MTS限额：seriesType={},amount={},globalId={}", seriesType, amount, request.getGlobalId());
            if (amount < vo.getOrderMaxPay()) {
                // 最高投注额取最小值
                vo.setOrderMaxPay(amount);
            }
        });
        return paList;
    }

    @Override
    protected Map<String, Object> checkOrder(OrderBean orderBean, List<RedisUpdateVo> redisUpdateList) {
        Map<String, Object> map = super.checkOrder(orderBean, redisUpdateList);
        log.info("信用额度，MTS限额，先校验PA限额：map={}", JSON.toJSONString(map));
        if ("0".equals(String.valueOf(map.get("infoCode")))) {
            return mtsCheckOrder(orderBean);
        }
        return map;
    }

    @Override
    public int orderType() {
        return 2;
    }
}
