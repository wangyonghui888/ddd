package com.panda.sport.rcs.predict.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.panda.sport.data.rcs.dto.PendingOrderDto;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.StandardSportMarketOddsMapper;
import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.mapper.predict.RcsPredictBetOddsMapper;
import com.panda.sport.rcs.mapper.predict.RcsPredictBetStatisMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.vo.predict.pending.RcsPredictPendingBetStatis;
import com.panda.sport.rcs.predict.common.Expiry;
import com.panda.sport.rcs.predict.common.ForecastPlayIds;
import com.panda.sport.rcs.predict.service.FootballMatrixService;
import com.panda.sport.rcs.predict.service.ForecastPendingService;
import com.panda.sport.rcs.predict.service.LastHourService;
import com.panda.sport.rcs.predict.service.PredictPendingService;
import com.panda.sport.rcs.predict.service.impl.basketball.BasketMatrixServiceImpl;
import com.panda.sport.rcs.predict.service.pending.RcsPredictPendingBetStatisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

/**
 * @description: 预约投注 赛事预测 Serveice  计算 货量  / Forecast 等
 * @author: joey
 * @date: 2022-05-23 12:13
 **/
@Service
@Slf4j
public class PredictPendingServiceImpl implements PredictPendingService {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

//    @Autowired
//    private RcsMatchDimensionStatisticsService rcsMatchDimensionStatisticsService;

    @Autowired
    private StandardSportMarketOddsMapper standardSportMarketOddsMapper;

    @Autowired
    LastHourService lastHourService;

    @Resource(name = "basketMatrixService")
    private BasketMatrixServiceImpl basketMatrixService;


    @Autowired
    PredictCommonServiceImpl predictCommonService;

    @Autowired
    RcsPredictBetOddsMapper rcsPredictBetOddsMapper;

    @Autowired
    RcsPredictBetStatisMapper rcsPredictBetStatisMapper;

    @Autowired
    TOrderDetailMapper orderDetailMapper;

    @Autowired
    FootballMatrixService footballMatrixService;

    @Autowired
    LockServiceImpl lockService;

    @Autowired
    private RcsPredictPendingBetStatisService rcsPredictPendingBetStatisService;

    /**
     * 赛事预测计算
     *
     * @param pendingOrderDto
     * @param type            1标识下单 增加计算  -1表示取消订单
     */
    @Override
    public void calculate(PendingOrderDto pendingOrderDto, Integer type) {
        Long sportId = pendingOrderDto.getSportId();
        //玩法级别forecast
        try {
            if (sportId == 1) {
                calculatePlayData(pendingOrderDto, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("预约投注 预测数据计算 异常-玩法级别forecast：" + pendingOrderDto.getOrderNo() + e.getMessage());
        }
        //货量计算(最小维度)
        try {
            if (sportId == 1 || sportId == 2|| sportId == 5 || sportId == 7 || sportId == 8 || sportId == 9 || sportId == 3) {
                calculateBetStatis(pendingOrderDto, type);
            }
        } catch (Exception e) {
            log.error("预约投注 预测数据计算 异常-货量计算(最小维度)：" + pendingOrderDto.getOrderNo() + e.getMessage());
        }
        //forecast计算 (只包含让球 和大小的) (最小维度)
        try {
            if (sportId == 1) {
                calculateForecast(pendingOrderDto, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("预约投注 预测数据计算 异常-forecast计算 ：" + pendingOrderDto.getOrderNo() + e.getMessage());
        }
    }

    /**
     * Forecast计算
     *
     * @param pendingOrderDto
     * @param type            1标识下单 增加计算  -1表示取消订单
     */
    public void calculateForecast(PendingOrderDto pendingOrderDto, Integer type) {
        log.info("预约投注 预测数据计算-{}Forecast计算开始", pendingOrderDto.getOrderNo());
        ForecastPendingService service = predictCommonService.getFootGrainedPendingForecastService(pendingOrderDto.getPlayId().intValue());
        if (service == null) {
            log.info("预约投注 预测数据计算-Forecast计算无需处理: {},玩法:{}", pendingOrderDto.getOrderNo(), pendingOrderDto.getPlayId());
            return;
        }
        log.info("预约投注 预测数据计算-Forecast {}处理开始 : {},类型:{}", pendingOrderDto.getOrderNo(), service, type);
        service.forecastData(pendingOrderDto, type);
        log.info("预约投注 预测数据计算-Forecast {}处理完成,类型:{}", pendingOrderDto.getOrderNo(), type);

    }


    /**
     * 足球玩法级别forecast
     */
    public void calculatePlayData(PendingOrderDto pendingOrderDto, Integer type) {
        ForecastPendingService service = predictCommonService.getFootPlayPendingForecastService(pendingOrderDto.getPlayId().intValue());
        if (service == null) {
            log.info("预约投注 预测数据计算-足球玩法级别forecast无需处理该玩法: {},玩法:{}", pendingOrderDto.getOrderNo(), pendingOrderDto.getPlayId());
            return;
        }
        log.info("预约投注 预测数据计算-足球玩法级别forecast {}处理开始 : {},类型:{}", pendingOrderDto.getOrderNo(), service, type);
        service.forecastData(pendingOrderDto, type);
        log.info("预约投注 预测数据计算足球玩法级别forecast{}处理完成 : 类型:{}", pendingOrderDto.getOrderNo(), type);
    }


    /**
     * 货量计算
     *
     * @param type            1标识下单 增加计算  -1表示取消订单
     * @param pendingOrderDto
     */
    public void calculateBetStatis(PendingOrderDto pendingOrderDto, Integer type) {

        log.info("预约投注 预测数据计算-{}最小维度货量开始", pendingOrderDto.getOrderNo());
        //只计算让球和大小玩法
        //2021-9-22 网球需求新增checkTennisPlay网球系列玩法货量计算
        int playId = pendingOrderDto.getPlayId().intValue();
        if (!checkFootBallPlay(playId) && !checkBasketBallPlay(playId) && !checkTennisPlay(playId) && !checkPingpongPlay(playId)
                && !checkVolleyPlay(playId) && !checkSnookerPlay(playId) && !checkBaseballPlay(playId)) {
            log.info("预约投注 预测数据计算-{}最小维度货量开始,玩法跳过", pendingOrderDto.getOrderNo());
            return;
        }
        //记录到缓存
        String betStatisKey = "rcs:risk:predict:pending:betSatis.match_id.%s.match_type.%s.play_id.%s.market_id.%s.odd_type.%s";
        betStatisKey = String.format(betStatisKey, pendingOrderDto.getMatchId(), pendingOrderDto.getMatchType(), pendingOrderDto.getPlayId(), pendingOrderDto.getMarketValue(), pendingOrderDto.getOddType());
        BigDecimal percentage = BigDecimal.valueOf(1);
        BigDecimal allAmount = new BigDecimal(pendingOrderDto.getBetAmount() * type).multiply(percentage);
        //累加 总货量
        Long betAmount = redisClient.hincrBy(betStatisKey, "totalBetAmount", allAmount.longValue());
        redisClient.expireKey(betStatisKey, Expiry.MATCH_EXPIRY);

        //纯赔付货量
        BigDecimal oddValue = new BigDecimal(pendingOrderDto.getOrderOdds()).divide(new BigDecimal(100000)).setScale(2, RoundingMode.DOWN).subtract(new BigDecimal("1"));
        BigDecimal betAmountPay = new BigDecimal(pendingOrderDto.getBetAmount()).multiply(oddValue).multiply(new BigDecimal(type)).multiply(percentage);
        //混合型货量
        BigDecimal betAmountComplex = allAmount;
        if (oddValue.compareTo(new BigDecimal("1")) > 0) {
            betAmountComplex = betAmountPay;
        }
        //累加 总货量 纯赔付额
        Long betAmountPayValue = redisClient.hincrBy(betStatisKey, "totalBetAmountPay", betAmountPay.longValue());

        //累加 总货量  混合型
        Long betAmountComplexValue = redisClient.hincrBy(betStatisKey, "totalBetAmountComplex", betAmountComplex.longValue());
        //累加 注单数量
        Long betNum = redisClient.hincrBy(betStatisKey, "totalBetNum", type);
        //累加 赔率和
        Double oddsSum = redisClient.hincrByFloat(betStatisKey, "oddsSum", Double.parseDouble(pendingOrderDto.getOrderOdds()) * type);

        //记录到数据库
        RcsPredictPendingBetStatis bean = new RcsPredictPendingBetStatis();
        bean.setSportId(pendingOrderDto.getSportId().intValue());
        bean.setMatchId(pendingOrderDto.getMatchId());
        bean.setMatchType(pendingOrderDto.getMatchType());
        bean.setPlayId(pendingOrderDto.getPlayId().intValue());
        bean.setMarketId(pendingOrderDto.getMarketId());
        bean.setOddsItem(pendingOrderDto.getOddType());
        bean.setBetScore("0:0");
        bean.setBetAmount(BigDecimal.valueOf(betAmount).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_DOWN));
        bean.setBetAmountPay(BigDecimal.valueOf(betAmountPayValue).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_DOWN));
        bean.setBetAmountComplex(BigDecimal.valueOf(betAmountComplexValue).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_DOWN));
        bean.setBetNum(betNum);
        bean.setOddsSum(new BigDecimal(oddsSum).divide(new BigDecimal("100000"), 2, BigDecimal.ROUND_DOWN));
        bean.setCreateTime(System.currentTimeMillis());
        bean.setMarketValueComplete(pendingOrderDto.getMarketValue());
        bean.setMarketValueCurrent(pendingOrderDto.getMarketValue());
        bean.setPlayOptions(pendingOrderDto.getOddsId() + "");
        //直接入库
        rcsPredictPendingBetStatisService.remove(new LambdaQueryWrapper<RcsPredictPendingBetStatis>()
                .eq(RcsPredictPendingBetStatis::getPlayId, bean.getPlayId())
                .eq(RcsPredictPendingBetStatis::getMatchId, bean.getMatchId())
                .eq(RcsPredictPendingBetStatis::getMatchType, bean.getMatchType())
                .eq(RcsPredictPendingBetStatis::getMarketId, bean.getMarketId())
                .eq(RcsPredictPendingBetStatis::getOddsItem, bean.getOddsItem())
                .eq(RcsPredictPendingBetStatis::getBetScore, bean.getBetScore())
                .eq(RcsPredictPendingBetStatis::getSubPlayId, bean.getSubPlayId())
                .eq(RcsPredictPendingBetStatis::getSportId, bean.getSportId()));
        rcsPredictPendingBetStatisService.save(bean);
        log.info("预约投注 预测数据计算最小维度货量完成{},{}", pendingOrderDto.getOrderNo(), JSONObject.toJSON(bean));
    }

    /**
     * 检查网球货量
     * 154 全场让盘
     * 155 全场让局
     * 202 全场局数
     * 163 第X盘让局
     * 164 第X盘局数
     *
     * @param plyaId
     * @return
     */
    private boolean checkTennisPlay(Integer plyaId) {
        String tennisPlayIds = "154,155,202,163,164";
        String arr[] = tennisPlayIds.split(",");
        List<String> list = Arrays.asList(arr);
        return list.contains(plyaId.toString());
    }

    /**
     * 检查排球货量
     *
     * @param plyaId
     * @return
     */
    private boolean checkVolleyPlay(Integer plyaId) {
        String tennisPlayIds = "153,159,204,162,172,173,253,254,255,256";
        String arr[] = tennisPlayIds.split(",");
        List<String> list = Arrays.asList(arr);
        return list.contains(plyaId.toString());
    }

    /**
     * 检查斯诺克货量
     *
     * @param plyaId
     * @return
     */
    private boolean checkSnookerPlay(Integer plyaId) {
        String tennisPlayIds = "185,186,181,182";
        String arr[] = tennisPlayIds.split(",");
        List<String> list = Arrays.asList(arr);
        return list.contains(plyaId.toString());
    }

    /**
     * 检查棒球货量
     * @param plyaId
     * @return
     */
    private boolean checkBaseballPlay(Integer plyaId) {
        String baseballPlayIds = "242,243,244,245,246,247,248,249,250,251,252,273,274,275,276,277,278,279,280,281,282,283,284,285,286,287,288,289,290,291,292";
        String arr[] = baseballPlayIds.split(",");
        List<String> list = Arrays.asList(arr);
        return list.contains(plyaId.toString());
    }


    /**
     * 检查乒乓球货量
     * 172 全场让分
     * 173 全场总分
     * 176 第X盘让分
     * 177 第X盘总分
     *
     * @param plyaId
     * @return
     */
    private boolean checkPingpongPlay(Integer plyaId) {
        String tennisPlayIds = "172,173,176,177";
        String arr[] = tennisPlayIds.split(",");
        List<String> list = Arrays.asList(arr);
        return list.contains(plyaId.toString());
    }

    /**
     * 检查属于半场和大小玩法
     * 18  半场大小
     * 19  半场让球
     * 26  下半场大小盘
     * 38  总分
     * 39  让分
     * 45  第1节总分
     * 46  第1节让分
     * 51  第2节总分
     * 52  第2节让分
     * 57  第3节总分
     * 58  第3节让分
     * 63  第4节总分
     * 64  第4节让分
     * 143  下半场让球盘
     *
     * @param plyaId
     * @return
     */
    private boolean checkBasketBallPlay(Integer plyaId) {
        String basketBallPlayIds = "38,39,18,19,26,45,46,51,52,57,58,63,64,26,143,198,199,87,97,88,98,145,146";
        String arr[] = basketBallPlayIds.split(",");
        List<String> list = Arrays.asList(arr);
        return list.contains(plyaId.toString());
    }


    /**
     * 足球  根据玩法
     */
    public boolean checkFootBallPlay(Integer playId) {
        //让球
        Integer letPoint[] = ForecastPlayIds.letPoint;
        if (Arrays.asList(letPoint).contains(playId)) {
            return true;
        }
        //大小
        Integer bigSmall[] = ForecastPlayIds.bigSmall;
        if (Arrays.asList(bigSmall).contains(playId)) {
            return true;
        }
        return false;
    }
}