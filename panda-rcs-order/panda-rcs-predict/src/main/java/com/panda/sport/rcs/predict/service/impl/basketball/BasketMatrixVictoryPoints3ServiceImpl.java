package com.panda.sport.rcs.predict.service.impl.basketball;

import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.statistics.RcsPredictBasketballMatrix;
import com.panda.sport.rcs.predict.common.Expiry;
import com.panda.sport.rcs.predict.service.BasketBallForecastService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 篮球 净胜分3项  Forecast计算
 * @author: lithan
 * @date:  2021-1-15 14:47:22
 **/
@Slf4j
@Service("basketMatrixVictoryPoints3Service")
public class BasketMatrixVictoryPoints3ServiceImpl implements BasketBallForecastService {

    @Autowired
    protected RedisClient redisClient;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Resource(name = "basketMatrixService")
    private BasketMatrixServiceImpl basketMatrixService;


    /**
     * Forecast计算
     */
    @Override
    public void forecastData(OrderBean orderBean, Integer type, boolean nx) {
        List<RcsPredictBasketballMatrix> list = calculate(orderBean, type);
        if (nx) {
            basketMatrixService.forecastDataSave(orderBean, list);
        } else {
            log.info("RcsPredictBasketballMatrix 订单号：{} 入库频率限制 本次执行跳过！", orderBean.getOrderNo());
        }
    }


    public List<RcsPredictBasketballMatrix> calculate(OrderBean orderBean, Integer type) {
        OrderItem orderItem = orderBean.getItems().get(0);
        List<RcsPredictBasketballMatrix> list = new ArrayList<>();
        //中值
        int middle = basketMatrixService.getMiddleValue(orderItem.getMatchId(), orderItem.getPlayId(), 2);
        int min = middle - 50;
        int max = middle + 50;
        String orderRec = "";
        //计算每个总进球数 对应的 预期 盈利
        for (Integer i = min; i <= max; i++) {
            //List<Double> marketValueList = MarketValueUtils.splitMarketValue(orderItem.getMarketValue());
            Double result = 0D;
            //订单每个分数下的期望盈利
            BigDecimal orderScoreAmount = BigDecimal.ZERO;
//            for (Double markValue : marketValueList) {
                //预期盈利额
            BigDecimal amount = BigDecimal.ZERO;
            //投注金额
            BigDecimal betAmount = orderItem.getBetAmount1();
//                if (marketValueList.size() == 2) {
//                    betAmount = new BigDecimal(String.valueOf(orderItem.getBetAmount1())).divide(BigDecimal.valueOf(2), 2, RoundingMode.FLOOR);
//                }
            if (i  >= 6) {
                if ("1And6+".equalsIgnoreCase(orderItem.getPlayOptions())) {//赢
                    amount = betAmount.multiply(new BigDecimal(String.valueOf(orderItem.getHandleAfterOddsValue())).subtract(new BigDecimal(1))).multiply(new BigDecimal("-1"));
                } else {
                    amount = betAmount;
                }
            } else if (i  <= -6) {
                if ("2And6+".equalsIgnoreCase(orderItem.getPlayOptions())) {//赢
                    amount = betAmount.multiply(new BigDecimal(String.valueOf(orderItem.getHandleAfterOddsValue())).subtract(new BigDecimal(1))).multiply(new BigDecimal("-1"));
                } else {
                    amount = betAmount;
                }
            } else {
                //注单中奖 玩家赢  庄家输 预期盈利为负  需要乘以负一
                if ("Other".equalsIgnoreCase(orderItem.getPlayOptions())) {//赢
                    amount = betAmount.multiply(new BigDecimal(String.valueOf(orderItem.getHandleAfterOddsValue())).subtract(new BigDecimal(1))).multiply(new BigDecimal("-1"));
                } else {
                    amount = betAmount;
                }
            }
            orderScoreAmount = orderScoreAmount.add(amount);
            //如果是取消订单 取反
            amount = amount.multiply(new BigDecimal(type));
            //缓存处理  预期金额累加/减
            result = redisClient.hincrByFloat(basketMatrixService.getCacheKey(orderItem), i.toString(), amount.doubleValue());
            Expiry.redisKeyExpiry(redisClient, orderItem.getMatchType(), basketMatrixService.getCacheKey(orderItem));

//            }
            orderRec += orderScoreAmount + ",";
            //添加到集合
            RcsPredictBasketballMatrix basketballMatrix = new RcsPredictBasketballMatrix();
            basketballMatrix.setCreateTime(System.currentTimeMillis());
            basketballMatrix.setMatchId(orderItem.getMatchId());
            basketballMatrix.setMatchType(orderItem.getMatchType());
            basketballMatrix.setPlayId(orderItem.getPlayId());
            basketballMatrix.setSportId(orderItem.getSportId());
            basketballMatrix.setForecastScore(i);
            basketballMatrix.setProfitAmount(new BigDecimal(String.valueOf(result)));
            basketballMatrix.setMiddleValue(middle);
            list.add(basketballMatrix);
        }
        basketMatrixService.saveBasketballOrderMatrix(orderBean, orderRec, middle, type);

        return list;
    }
}