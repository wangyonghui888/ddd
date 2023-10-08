package com.panda.sport.rcs.credit.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.constants.SeriesEnum;
import com.panda.sport.rcs.credit.constants.CreditRedisKey;
import com.panda.sport.rcs.credit.constants.ErrorCode;
import com.panda.sport.rcs.credit.dto.SeriesBetPaymentDto;
import com.panda.sport.rcs.credit.service.AbstractCreditLimitService;
import com.panda.sport.rcs.credit.service.CreditLimitService;
import com.panda.sport.rcs.exeception.LogicException;
import com.panda.sport.rcs.pojo.limit.RedisUpdateVo;
import com.panda.sport.rcs.utils.SeriesUtils;
import com.panda.sport.rcs.vo.RcsBusinessPlayPaidConfigVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用串关限额服务
 * @Author : Paca
 * @Date : 2021-05-05 19:55
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Service("creditSeriesLimitService")
public class CreditSeriesLimitServiceImpl extends AbstractCreditLimitService {

    @Override
    protected List<RcsBusinessPlayPaidConfigVo> queryBetLimit(OrderBean orderBean, List<OrderItem> orderItems) {
        Long tenantId = orderBean.getTenantId();
        String creditAgentId = orderBean.getAgentId();
        Long userId = orderBean.getUid();
        Integer seriesType = orderBean.getSeriesType();
        int seriesNum = SeriesUtils.getSeriesNum(seriesType);
        String currentDateExpect = DateUtils.getDateExpect(System.currentTimeMillis());

        Map<Integer, Long> maxBetMap = Maps.newHashMap();
        for (int i = SeriesEnum.TWO.getSeriesNum(); i <= orderItems.size(); i++) {
            // 代理串关限额
            long seriesLimit = getAgentSeriesLimit(tenantId, creditAgentId, i);
            long seriesUsed = getAgentSeriesUsed(currentDateExpect, tenantId, creditAgentId, i);
            long agentSeries = seriesLimit - seriesUsed;
            log.info("信用额度，代理串关限额剩余额度：{} = {} - {}", agentSeries, seriesLimit, seriesUsed);
            // 用户串关限额
            long userSeriesLimit = getUserSeriesLimit(tenantId, creditAgentId, userId, i);
            long userSeriesUsed = getUserSeriesUsed(currentDateExpect, userId, i);
            long userSeries = userSeriesLimit - userSeriesUsed;
            log.info("信用额度，用户串关限额剩余额度：{} = {} - {}", userSeries, userSeriesLimit, userSeriesUsed);
            long remain = agentSeries >= userSeries ? userSeries : agentSeries;
            log.info("信用额度，串关剩余额度：remain={},seriesType={}", remain, i);

            Map<Integer, Long> maxBetAmountMap = getMaxBetAmountByPayment(remain, orderItems, i);
            if (i == SeriesEnum.TWO.getSeriesNum()) {
                maxBetMap.putAll(maxBetAmountMap);
            } else {
                int type = i * 1000 + 1;
                maxBetMap.put(type, maxBetAmountMap.get(type));
                if (i == orderItems.size()) {
                    int maxType = SeriesEnum.getSeriesEnumBySeriesNum(seriesNum).getSeriesJoin();
                    maxBetMap.put(maxType, maxBetAmountMap.get(maxType));
                }
            }
        }
        log.info("信用额度，最大投注额：maxBetMap={}", maxBetMap);
        List<RcsBusinessPlayPaidConfigVo> resultList = new ArrayList<>();
        maxBetMap.forEach((type, maxBetAmount) -> {
            RcsBusinessPlayPaidConfigVo vo = new RcsBusinessPlayPaidConfigVo();
            vo.setType(String.valueOf(type));
            vo.setMinBet(1L);
            vo.setOrderMaxPay(maxBetAmount / 100);
            resultList.add(vo);
        });
        return resultList;
    }

    /**
     * 通过 可用赔付额度 获取 最大投注额，单位：分 <br/>
     * 推导公式，例如3串4，欧赔分别为odds1、odds2、odds3，A为拆分后单个注单投注额，M为剩余赔付额度，已知M求A <br/>
     * 2串1：A*(odds1*odds2-1) + A*(odds1*odds3-1) + A*(odds2*odds3-1) = M <br/>
     * 3串1：A*(odds1*odds2*odds3-1) = M <br/>
     * 3串4：A*(odds1*odds2-1) + A*(odds1*odds3-1) + A*(odds2*odds3-1) + A*(odds1*odds2*odds3-1) = M
     *
     * @param availablePayment 可用赔付额度，单位：分
     * @param orderItems       赛事投注项信息
     * @param seriesNum        串关关数，M串N中的M
     * @return
     * @author Paca
     */
    private Map<Integer, Long> getMaxBetAmountByPayment(Long availablePayment, List<OrderItem> orderItems, int seriesNum) {
        Map<Integer, Long> resultMap = Maps.newHashMap();
        // 赛事串关组合
        Map<Integer, List<List<Integer>>> combinationMap = SeriesUtils.combination(orderItems.size());
        // 所有注单港赔之和，用来计算M串N
        BigDecimal allHkOddsSum = BigDecimal.ZERO;
        // 所有注单数量
        int allBetCount = 0;
        // 高赔
        BigDecimal highOdds = getHighOdds();
        // 分组遍历，2串1一组，3串1一组，4串1一组......M串1一组
        for (Map.Entry<Integer, List<List<Integer>>> entry : combinationMap.entrySet()) {
            // M串1中的M
            Integer key = entry.getKey();
            // M串1组成的集合
            List<List<Integer>> groupList = entry.getValue();

            List<BigDecimal> euOddsList = getEuOddsOfGroup(groupList, orderItems);
            // 每组注单港赔之和
            BigDecimal hkOddsSum = euOddsList.stream().map(euOdds -> convertEuOdds(euOdds, highOdds).subtract(BigDecimal.ONE)).reduce(BigDecimal::add).orElse(BigDecimal.ONE);
            allHkOddsSum = allHkOddsSum.add(hkOddsSum);
            // 每组注单数量
            int betCount = groupList.size();
            allBetCount += betCount;
            /*
             * 计算 一组M串1 中的A，然后通过A计算 投注总额
             * A * 每组注单港赔之和 = M，投注总额 = A * 每组注单数量
             * 投注总额 = (M / 每组注单港赔之和) * 注单数量 = M * 每组注单数量 / 每组注单港赔之和
             */
            long maxBetAmount = new BigDecimal(availablePayment * betCount).divide(hkOddsSum, 2, RoundingMode.DOWN).longValue();
            resultMap.put(key * 1000 + 1, maxBetAmount);
        }
        // 计算M串N 中的 A，然后通过A计算总投注额
        if (seriesNum > SeriesEnum.TWO.getSeriesNum()) {
            SeriesEnum seriesEnum = SeriesEnum.getSeriesEnumBySeriesNum(seriesNum);
            long maxBetAmount = new BigDecimal(availablePayment * allBetCount).divide(allHkOddsSum, 2, RoundingMode.DOWN).longValue();
            resultMap.put(seriesEnum.getSeriesJoin(), maxBetAmount);
        }
        log.info("信用额度，通过可用赔付额度获取最大投注总额：seriesNum={},resultMap={}", seriesNum, resultMap);
        return resultMap;
    }

    /**
     * 获取每一组注单的欧赔
     *
     * @param groupList
     * @param orderItems
     */
    private List<BigDecimal> getEuOddsOfGroup(List<List<Integer>> groupList, List<OrderItem> orderItems) {
        return groupList.stream().map(indexList -> getEuOddsOfBet(indexList, orderItems)).collect(Collectors.toList());
    }

    /**
     * 获取每一注单的欧赔
     *
     * @param indexList
     * @param orderItems
     * @return
     */
    private BigDecimal getEuOddsOfBet(List<Integer> indexList, List<OrderItem> orderItems) {
        return indexList.stream().map(index -> getOdds(orderItems.get(index))).reduce(BigDecimal::multiply).orElse(BigDecimal.ONE);
    }

    @Override
    protected Map<String, Object> checkOrder(OrderBean orderBean, List<RedisUpdateVo> redisUpdateList) {
        log.info("信用网订单::{}::，串关额度校验开始：{}", orderBean.getOrderNo(), JSON.toJSONString(redisUpdateList));
        Long tenantId = orderBean.getTenantId();
        String creditAgentId = orderBean.getAgentId();
        Long userId = orderBean.getUid();
        Integer seriesType = orderBean.getSeriesType();
        int seriesNum = SeriesUtils.getSeriesNum(seriesType);
        log.info("信用网订单::{}::，串关赛事场数：{}", orderBean.getOrderNo(), seriesNum);
        String currentDateExpect = DateUtils.getDateExpect(System.currentTimeMillis());
        List<OrderItem> orderItems = orderBean.getItems();

        List<SeriesBetPaymentDto> betPaymentList = calPaymentAmount(seriesType, orderItems);
        Long paymentTotal = betPaymentList.stream().map(SeriesBetPaymentDto::getBetPayment).reduce(Long::sum).orElse(0L);
        log.info("信用网订单::{}::，串关注单赔付总额：{}，赔付详情：{}", orderBean.getOrderNo(), paymentTotal, JSON.toJSONString(betPaymentList));

        // 代理串关限额
        long seriesUsed = incrAgentSeriesUsed(currentDateExpect, tenantId, creditAgentId, seriesNum, paymentTotal, redisUpdateList);
        long seriesLimit = getAgentSeriesLimit(tenantId, creditAgentId, seriesNum);
        long seriesLimitNew = new BigDecimal(seriesLimit - seriesUsed + paymentTotal).multiply(RcsConstant.LIMIT_MULTIPLE).longValue();
        log.info("信用网订单::{}::，代理串关限额扩大：{}倍，新额度：{}，原额度：{}，已用额度：{}，注单总赔付{}", orderBean.getOrderNo(),RcsConstant.LIMIT_MULTIPLE, seriesLimitNew, seriesLimit, seriesUsed, paymentTotal);
        if (paymentTotal > seriesLimitNew) {
            redisCallback(redisUpdateList);
            log.warn("信用网订单::{}::，代理串关限额拒单，扩大后代理串关限额：{} < 注单总赔付：{}，代理串关已用限额：{}", orderBean.getOrderNo(), seriesLimitNew, paymentTotal, seriesUsed);
            return checkOrderResult(orderBean, ErrorCode.LIMIT_FAILURE, "信用额度，代理串关限额拒单");
        }
        // 用户串关限额
        long userSeriesUsed = incrUserSeriesUsed(currentDateExpect, userId, seriesNum, paymentTotal, redisUpdateList);
        long userSeriesLimit = getUserSeriesLimit(tenantId, creditAgentId, userId, seriesNum);
        long userSeriesLimitNew = new BigDecimal(userSeriesLimit - userSeriesUsed + paymentTotal).multiply(RcsConstant.LIMIT_MULTIPLE).longValue();
        log.info("信用网订单::{}::，用户串关限额扩大：{}倍，新额度：{}，原额度：{}，已用额度：{}，注单总赔付{}", orderBean.getOrderNo(),RcsConstant.LIMIT_MULTIPLE, userSeriesLimitNew, userSeriesLimit, userSeriesUsed, paymentTotal);
        if (paymentTotal > userSeriesLimitNew) {
            redisCallback(redisUpdateList);
            log.warn("信用网订单::{}::，用户串关限额拒单，扩大后用户串关限额：{} < 注单总赔付：{}，代理串关已用限额：{}", orderBean.getOrderNo(), userSeriesLimitNew, paymentTotal, userSeriesUsed);
            return checkOrderResult(orderBean, ErrorCode.LIMIT_FAILURE, "信用额度，用户串关限额拒单");
        }

        return checkOrderResult(orderBean, ErrorCode.LIMIT_SUCCESS, "串关订单校验成功");
    }

    private List<SeriesBetPaymentDto> calPaymentAmount(Integer seriesType, List<OrderItem> orderItems) {
        List<SeriesBetPaymentDto> seriesBetPaymentList = Lists.newArrayList();
        // 下单时，要么是M串1，要么是M串N（N是最大组合数）
        // M串N中的M
        int seriesNum = SeriesUtils.getSeriesNum(seriesType);
        // M串N中的N
        int count = SeriesUtils.getCount(seriesType, seriesNum);
        // 单注投注额，单位：分
        long singleBetAmount = orderItems.get(0).getBetAmount();
        // 高赔
        BigDecimal highOdds = getHighOdds();
        // 赛事串关组合
        Map<Integer, List<List<Integer>>> combinationMap = SeriesUtils.combination(orderItems.size());
        // 分组遍历，2串1一组，3串1一组，4串1一组......M串1一组
        combinationMap.forEach((m, groupList) -> {
            // key = M串1中的M，value = M串1组成的集合
            // 如果是M串1，则只计算M串1
            if (count == 1 && m != seriesNum) {
                return;
            }
            // 遍历M串1注单
            List<SeriesBetPaymentDto> list = groupList.stream().map(indexList -> {
                // 注单的欧赔
                BigDecimal euOdds = getEuOddsOfBet(indexList, orderItems);
                // 大于高赔取赔付，否则取投注
                long betPayment = getBetPayment(singleBetAmount, euOdds, highOdds);
                SeriesBetPaymentDto betPaymentDto = new SeriesBetPaymentDto();
                betPaymentDto.setBetPayment(betPayment);
                betPaymentDto.setIndexList(indexList);
                return betPaymentDto;
            }).collect(Collectors.toList());
            seriesBetPaymentList.addAll(list);
        });
        log.info("信用额度，串关中注单赔付：{}", JSON.toJSONString(seriesBetPaymentList));
        return seriesBetPaymentList;
    }

    @Override
    protected void queryCheckOrderBean(OrderBean orderBean) {
        commonCheckOrderBean(orderBean);
        Integer seriesType = orderBean.getSeriesType();
        int size = orderBean.getItems().size();
        if (size < SeriesEnum.TWO.getSeriesNum() || size > SeriesEnum.Ten.getSeriesNum()) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "投注项items超出范围");
        }
        if (seriesType == null) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "串关类型seriesType不能为空");
        }
        int seriesNum = SeriesUtils.getSeriesNum(seriesType);
        if (seriesNum < SeriesEnum.TWO.getSeriesNum() || seriesNum > size) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "串关类型seriesType有误");
        }
    }

    @Override
    protected void queryCheckOrderItem(OrderItem orderItem) {
        commonCheckOrderItem(orderItem);
    }

    @Override
    protected void orderCheckOrderBean(OrderBean orderBean) {
        // 查询校验的参数，下单必校验
        queryCheckOrderBean(orderBean);
        if (StringUtils.isBlank(orderBean.getOrderNo())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "订单号orderNo不能为空！");
        }
        if (CreditLimitService.checkNo(orderBean.getUid())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "用户uid不能为空！");
        }
    }

    @Override
    protected void orderCheckOrderItem(OrderItem orderItem) {
        // 查询校验的参数，下单必校验
        queryCheckOrderItem(orderItem);
        if (StringUtils.isBlank(orderItem.getBetNo())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "items注单编号betNo不能为空！");
        }
        if (StringUtils.isBlank(orderItem.getOrderNo())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "items订单号orderNo不能为空！");
        }
        if (CreditLimitService.checkNo(orderItem.getUid())) {
            throw new LogicException(ErrorCode.PARAM_CHECK_EXCEPTION + "", "items用户uid不能为空！");
        }
    }

    @Override
    protected String getHighOddsKey() {
        return CreditRedisKey.SERIES_HIGH_ODDS_KEY;
    }

    @Override
    public int orderType() {
        return 1;
    }
}
