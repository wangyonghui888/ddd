package com.panda.sport.rcs.predict.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.constants.RedisKey;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.enums.Basketball;
import com.panda.sport.rcs.enums.LNBasktballEnum;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.enums.TradeTypeEnum;
import com.panda.sport.rcs.mapper.StandardSportMarketOddsMapper;
import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.mapper.predict.RcsPredictBetOddsMapper;
import com.panda.sport.rcs.mapper.predict.RcsPredictBetStatisMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import com.panda.sport.rcs.pojo.statistics.RcsMatchDimensionStatistics;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictBetOdds;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictBetStatis;
import com.panda.sport.rcs.predict.common.Expiry;
import com.panda.sport.rcs.predict.common.ForecastPlayIds;
import com.panda.sport.rcs.predict.common.LockInfo;
import com.panda.sport.rcs.predict.predictenum.SeriesOrderBetAmountEnum;
import com.panda.sport.rcs.predict.service.*;
import com.panda.sport.rcs.predict.service.impl.basketball.BasketMatrixServiceImpl;
import com.panda.sport.rcs.predict.utils.*;
import com.panda.sport.rcs.predict.utils.thread.MatrixThreadUtil;
import com.panda.sport.rcs.predict.utils.thread.ThreadUtil;
import com.panda.sport.rcs.predict.vo.RcsPredictBetOddsVo;
import com.panda.sport.rcs.predict.vo.RcsPredictOddsPlaceNumMqVo;
import com.panda.sport.rcs.vo.operation.RealTimeVolumeBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 赛事预测Serveice  计算 货量  / Forecast 等
 * @author: lithan
 * @date: 2020-07-18 19:13
 **/
@Service
@Slf4j
public class PredictServiceImpl implements PredictService {

    final List<Integer> PLACE_VOLUME_CALC_SPORT_IDS = Arrays.asList(1, 2, 3, 4, 5,6, 7, 8, 9, 10);

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RedisUtilsNxExtend redisUtilsNxExtend;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    private RcsPredictMysqlFrequencyNacosConfig rcsPredictMysqlFrequencyNacosConfig;
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
    private VolumeControlUtils volumeControlUtils;

    @Autowired
    private PredictResetRedisKeyBo predictResetRedisKeyBo;

    /**
     * 赛事预测计算
     *
     * @param orderBean
     * @param type      1标识下单 增加计算  -1表示取消订单
     */
    @Override
    public void calculate(OrderBean orderBean, Integer type) {
        ThreadUtil.submit(() -> {
            Thread.currentThread().setName("主-" + orderBean.getOrderNo());
            log.info("::{}::预测数据计算-待处理长度：{}", orderBean.getOrderNo(), ThreadUtil.size());
            calculate(orderBean, type, 1);
        });
    }

    /**
     * 串关计算
     *
     * @param orderBean
     * @param type
     */
    @Override
    public void calculateSeries(OrderBean orderBean, Integer type) {
        OrderBean bean = new OrderBean();
        BeanUtils.copyProperties(orderBean, bean);
        bean.setItems(new ArrayList<>());

        Integer orderSeriesType = orderBean.getSeriesType();
        //串关 拆成多个单关处理
        for (OrderItem item : orderBean.getItems()) {
            //只有篮球或者足球才去计算串关
            if (item.getSportId() == 1 || item.getSportId() == 2) {
                //类似40011 3004 这种注单
                if (SeriesOrderBetAmountEnum.getSeriesEnumBySeriesJoin(orderSeriesType) != null) {
                    //几个投注项
                    SeriesOrderBetAmountEnum betAmountEnum = SeriesOrderBetAmountEnum.getSeriesEnumBySeriesJoin(orderSeriesType);
                    BigDecimal amount = new BigDecimal(orderBean.getOrderAmountTotal()).multiply(new BigDecimal(betAmountEnum.getItemNums()).divide(new BigDecimal(betAmountEnum.getSeriesMax()), 2, BigDecimal.ROUND_DOWN));
                    item.setBetAmount(amount.longValue());
                } else {
                    //2001  3001等这类注单
                    orderSeriesType = orderSeriesType / 1000;
                }

                log.info("::{}::预测数据计算串关处理开始：{}", bean.getOrderNo(), item.getBetNo());
                ThreadUtil.submit(() -> {
                    List<OrderItem> itemList = new ArrayList<>();
                    itemList.add(item);
                    OrderBean beanNew = new OrderBean();
                    BeanUtils.copyProperties(bean, beanNew);
                    beanNew.setItems(itemList);
                    log.info("::{}::预测数据计算-待处理长度：{}", bean.getOrderNo(), ThreadUtil.size());
                    calculate(beanNew, type, 2);
                });
                log.info("::{}::预测数据计算串关处理结束：{}", bean.getOrderNo(), item.getBetNo());
            }
        }
    }

    /**
     * 赛事预测计算
     *
     * @param orderBean
     * @param type       1标识下单 增加计算  -1表示取消订单
     * @param seriesType 1单关 2串关
     */
    @Override
    public void calculate(OrderBean orderBean, Integer type, Integer seriesType) {
        OrderItem item = orderBean.getItems().get(0);
        BigDecimal percentage = item.getVolumePercentage();
        log.info("::{}::预测数据计算 处理开始 货量取值 订单号 ,货量百分比:{} 原货量:{},类型{}", item.getOrderNo(), percentage, item.getBetAmount(), type);

        item.setSubPlayId(item.getSubPlayId());
        if (percentage.compareTo(BigDecimal.ZERO) == 0) {
            log.warn("::{}::预测数据计算 订单号 ,货量比为0 跳过", item.getOrderNo());
            return;
        }
        log.info("::{}::预测数据计算,货量百分比:{} 原货量:{} 元单位:{}", item.getOrderNo(), percentage, item.getBetAmount(), item.getBetAmount1());
        item.setBetAmount(BigDecimal.valueOf(item.getBetAmount()).multiply(percentage).longValue());
        log.info("::{}::预测数据计算,货量百分比:{} 后货量:{} 元单位:{}", item.getOrderNo(), percentage, item.getBetAmount(), item.getBetAmount1());
        if (StringUtils.isBlank(item.getScoreBenchmark())) {
            item.setScoreBenchmark("0:0");
        }
        int sportId = item.getSportId() == null ? 0 : item.getSportId();
        //盘口值正数 统一不需要+号
        if (StringUtils.isNotEmpty(item.getMarketValue()) && item.getMarketValue().startsWith("+")) {
            item.setMarketValue(item.getMarketValue().replace("+", ""));
        }
        if (StringUtils.isNotEmpty(item.getMarketValueNew()) && item.getMarketValueNew().startsWith("+")) {
            item.setMarketValueNew(item.getMarketValueNew().replace("+", ""));
        }
        /**
         * 数据库ws限频key
         */
        String MYSQL_WS_FREQUENCY_LIMIT = "MYSQL_WS_FREQUENCY_LIMIT.matchId.%s.playId.%s.matchType.%s.dataTypeValue.%s.seriesType.%s";
        String format = String.format(MYSQL_WS_FREQUENCY_LIMIT, item.getMatchId(), item.getPlayId(), item.getMatchType(), item.getPlaceNum(), item.getSeriesType());
        boolean nx = redisUtilsNxExtend.setNX(format, "1", rcsPredictMysqlFrequencyNacosConfig.getForecastInsertMysqlFrequency());
        log.info("::{}::货量限频  Key:{} 限频标识：{}", orderBean.getOrderNo(), format, nx);
        /**
         * 坑位级别货量
         */
        try {
            if (PLACE_VOLUME_CALC_SPORT_IDS.contains(sportId)) {
                calculatePlaceNumData(item, type, seriesType, nx);
            }
        } catch (Exception e) {
            log.error("::{}::预测数据计算 异常-坑位级别货量  异常信息：{}", orderBean.getOrderNo(), e.getMessage() + "---" + e);
        }

        /**
         * 盘口维度货量
         */
        try {
            if (item.getMatchType() == 3) {
                calculateOddsData(item, type, seriesType, nx);
            }
        } catch (Exception e) {
            log.error("::{}::预测数据计算 异常-投注项级别 异常信息：{}", orderBean.getOrderNo(), e.getMessage() + "---" + e);
        }

        if (seriesType == 2) {
            log.info("::{}::预测数据计算-串关流程提前结束  {}", item.getOrderNo(), item.getBetNo());
            return;
        }

        //玩法级别forecast
        try {
            if (sportId == 1) {
                calculatePlayData(orderBean, type, nx);
            }
        } catch (Exception e) {
            log.error("::{}::预测数据计算 异常-玩法级别forecast 异常信息：{}", orderBean.getOrderNo(), e.getMessage());
        }

        //坑位级别forecast
        try {
            if (sportId == 1) {
                calculatePlaceNumData(orderBean, type, nx);
            }
        } catch (Exception e) {
            log.error("::{}::预测数据计算 异常-玩法级别forecast 异常信息：{}", orderBean.getOrderNo(), e.getMessage());
        }
        //货量计算(最小维度)
        try {
            if (PLACE_VOLUME_CALC_SPORT_IDS.contains(sportId)) {
                calculateBetStatis(item, type, nx);
            }
        } catch (Exception e) {
            log.error("::{}::预测数据计算 异常-货量计算(最小维度) 异常信息：{}", orderBean.getOrderNo(), e.getMessage());
        }
        //forecast计算 (只包含让球 和大小的) (最小维度)
        try {
            if (sportId == 1) {
                calculateForecast(item, type, nx);
            }
        } catch (Exception e) {
            log.error("::{}::预测数据计算 异常-forecast计算  异常信息：{}", orderBean.getOrderNo(), e.getMessage());
        }

        /**
         * 篮球矩阵计算
         */
        try {
            if (sportId == 2) {
                calculateBasketballMatrix(orderBean, type, nx);
            }
        } catch (Exception e) {
            log.error("::{}::预测数据计算-篮球矩阵- 异常信息：{}", orderBean.getOrderNo(), e.getMessage());
        }

        /**
         * kir
         * 足球矩阵计算 （信用网及现金网 所有矩阵数据全部按规则存入新表 rcs_matrix_info 中）
         */
        if (sportId == 1 && item.getMatchType() != 3) {
            MatrixThreadUtil.submit(() -> {
                Thread.currentThread().setName("矩阵-" + item.getOrderNo());
                log.info("::{}::预测数据计算-矩阵排队长度：{}", item.getOrderNo(), MatrixThreadUtil.size());
                String lockKey = "rcs_matrix_data_lock_" + orderBean.getItems().get(0).getMatchId();
                lockKey = lockKey + "_" + footballMatrixService.getMatrixTypeAndPlayType(orderBean.getItems().get(0));
                LockInfo lockInfo = null;
                StopWatch sw = new StopWatch();
                sw.start();
                try {
                    lockInfo = lockService.lock(lockKey, 100, 10);
                    log.info("::{}::获取锁完成lockKey{}", orderBean.getOrderNo(), lockKey);
                    calculateFootballMatrix(orderBean, type);
                } catch (Exception e) {
                    log.error("::{}::新规则矩阵数据存入 异常：{}" + orderBean.getOrderNo(), e.getMessage());
                } finally {
                    lockService.release(lockInfo);
                    sw.stop();
                    log.info("::{}::耗时:{}释放锁完成lockKey{}", orderBean.getOrderNo(), sw.getTotalTimeMillis(), lockKey + JSONObject.toJSONString(lockInfo));
                }
            });
        }
        log.info("::{}::预测数据 处理完成", item.getOrderNo());

        //是否为篮球
        if(Long.valueOf(item.getSportId()).equals(SportIdEnum.BASKETBALL.getId())){
            String tradingTypeStatusKey = RedisKey.getTradingTypeStatusKey(item.getMatchId() + "", item.getPlayId() + "", item.getMatchType() + "");
            String tradeType = redisClient.get(tradingTypeStatusKey);
            log.info("::{}注单号,key{},tradeType{}",item.getOrderNo(),tradingTypeStatusKey,tradeType);
            log.info(LNBasktballEnum.getNameById(item.getPlayId()) +"====12");
            //是否是LN模式
            if ("4".equals(tradeType)){
                //如果该玩法是被联控的次要玩法，则会将其货量累计到对应的主玩法去
                Integer zkPlayId = LNBasktballEnum.getNameById(item.getPlayId());
                if (zkPlayId !=null){
                    //当set成主控玩法id后下次调用不会进入这里
                    //换个注单号避免重复注单号不计算 主控玩法 货量
                    try {
                        item.setPlayId(zkPlayId);
                        String key = PredictRedisKeyUtil.getPlaceNumCommonKey(item);
                        String lastMarketChangeData = redisClient.get(key+"."+item.getPlaceNum().toString()+"."+item.getPlayOptions());
                        if (StringUtils.isBlank(lastMarketChangeData)){
                            log.info("::{}::LN缓存获取为空,直接返回"+lastMarketChangeData, item.getOrderNo());
                            return;
                        }
                        log.info("lastMarketChangeKey:"+key+ "====lastMarketChangeData:" + lastMarketChangeData);
                        RcsPredictOddsPlaceNumMqVo rcsPredictOddsPlaceNumMqVo1 = JSONObject.parseObject(lastMarketChangeData, RcsPredictOddsPlaceNumMqVo.class);
                        item.setPlayId(rcsPredictOddsPlaceNumMqVo1.getPlayId());
                        item.setSubPlayId(rcsPredictOddsPlaceNumMqVo1.getSubPlayId());
                        item.setPlayOptions(rcsPredictOddsPlaceNumMqVo1.getPlayOptions());
                        item.setMarketId(rcsPredictOddsPlaceNumMqVo1.getMarketId());
                        item.setPlaceNum(rcsPredictOddsPlaceNumMqVo1.getDataTypeValue().intValue());
                        log.info("进入LN主控玩法计算货量=={}", JSON.toJSONString(rcsPredictOddsPlaceNumMqVo1));
                        calculatePlaceNumData(item, type, seriesType, nx);
                        calculateBetStatis(item, type, nx);
                        log.info("LN主控玩法计算货量-结束");
                    } catch (Exception e) {
                        log.info("LN主控玩法计算异常!");
                        log.error("LN主控玩法计算异常!"+e.getMessage(),e);
                    }
                }
            }
        }
    }

    /**
     * Forecast计算
     *
     * @param orderItem
     * @param type      1标识下单 增加计算  -1表示取消订单
     */
    @Override
    public void calculateForecast(OrderItem orderItem, Integer type, boolean nx) {
        log.info("::{}::预测数据计算-Forecast计算开始:{}", orderItem.getOrderNo(), orderItem.getBetNo());
        ForecastService service = predictCommonService.getFootGrainedForecastService(orderItem.getPlayId());
        if (service == null) {
            log.warn("::{}::预测数据计算-Forecast计算无需处理,玩法:{}", orderItem.getOrderNo(), orderItem.getPlayId());
            return;
        }
        log.info("::{}::预测数据计算-Forecast处理开始:{},类型:{}", orderItem.getOrderNo(), service, type);
        service.forecastData(orderItem, type, nx);
        log.info("::{}::预测数据计算-Forecast处理完成:{},类型:{}", orderItem.getOrderNo(), orderItem.getBetNo(), type);

    }

    /**
     * 投注项级别实货货量、期望值以及投注笔数，
     *
     * @param item
     * @param type 1标识下单 增加计算  -1表示取消订单
     */
    @Override
    public void calculateOddsData(OrderItem item, Integer type, Integer seriesType, Boolean nx) {
        log.info("::{}::预测数据计算-投注项级别统计 处理开始:{}", item.getOrderNo(), item.getBetNo());
        String oddsTotalBetAmountTempKey = PredictRedisKeyUtil.getOddsTotalBetAmountTempKey(item, seriesType.toString());
        String oddsTotalBetAmountPayTempKey = PredictRedisKeyUtil.getOddsTotalBetAmountPayTempKey(item, seriesType.toString());
        String oddsTotalBetAmountComplexTempKey = PredictRedisKeyUtil.getOddsTotalBetAmountComplexTempKey(item, seriesType.toString());
        String oddsTotalBetNumTempKey = PredictRedisKeyUtil.getOddsTotalBetNumTempKey(item, seriesType.toString());
        if (item.getMatchType() == 1) {
            predictResetRedisKeyBo.resetOddsDataRedisKey(item, seriesType);
        }
        //获取用户标签 货量比例
        BigDecimal percentage = BigDecimal.valueOf(1);// predictCommonService.getUserTagPercentage(item.getUid());
        //log.info("预测数据计算-投注项级别货量比例{}:{}", item.getOrderNo(), percentage);
        //投注项总投注金额
        String oddsTotalBetAmountKey = PredictRedisKeyUtil.getOddsTotalBetAmountKey(item, seriesType.toString());
        BigDecimal betAmount = new BigDecimal(item.getBetAmount() * type).multiply(percentage);
        redisClient.hincrBy(oddsTotalBetAmountKey, item.getPlayOptions(), betAmount.longValue());
        Expiry.redisKeyExpiry(redisClient, item.getMatchType(), oddsTotalBetAmountKey);
        //投注项总投注金额(进球后数据清零)
        redisClient.hincrBy(oddsTotalBetAmountTempKey, PredictRedisKeyUtil.getOddsTotalCommonKey(item, item.getPlayOptions()), betAmount.longValue());
        Expiry.redisKeyExpiry(redisClient, item.getMatchType(), oddsTotalBetAmountTempKey);
        //纯赔付货量
        BigDecimal oddValue = new BigDecimal(item.getOddsValue()).divide(new BigDecimal("100000"), 2, BigDecimal.ROUND_DOWN).subtract(new BigDecimal("1"));
        BigDecimal betAmountPay = new BigDecimal(item.getBetAmount()).multiply(oddValue).multiply(percentage).multiply(new BigDecimal(type));
        String oddsTotalBetAmountPayKey = PredictRedisKeyUtil.getOddsTotalBetAmountPayKey(item, seriesType.toString());
        redisClient.hincrBy(oddsTotalBetAmountPayKey, item.getPlayOptions(), betAmountPay.longValue());
        Expiry.redisKeyExpiry(redisClient, item.getMatchType(), oddsTotalBetAmountPayKey);
        //纯赔付货量 进球后缓存清零
        redisClient.hincrBy(oddsTotalBetAmountPayTempKey, PredictRedisKeyUtil.getOddsTotalCommonKey(item, item.getPlayOptions()), betAmountPay.longValue());
        Expiry.redisKeyExpiry(redisClient, item.getMatchType(), oddsTotalBetAmountPayTempKey);

        //混合型货量
        BigDecimal betAmountComplex = betAmount;
        if (oddValue.compareTo(new BigDecimal("1")) > 0) {
            betAmountComplex = betAmountPay;
        }
        String oddsTotalBetAmountComplexKey = PredictRedisKeyUtil.getOddsTotalBetAmountComplexKey(item, seriesType.toString());
        redisClient.hincrBy(oddsTotalBetAmountComplexKey, item.getPlayOptions(), betAmountComplex.longValue());
        Expiry.redisKeyExpiry(redisClient, item.getMatchType(), oddsTotalBetAmountComplexKey);
        //进球后数据清零
        redisClient.hincrBy(oddsTotalBetAmountComplexTempKey, PredictRedisKeyUtil.getOddsTotalCommonKey(item, item.getPlayOptions()), betAmountComplex.longValue());
        Expiry.redisKeyExpiry(redisClient, item.getMatchType(), oddsTotalBetAmountComplexTempKey);

        //投注项总投注笔数
        String oddsTotalBetNumKey = PredictRedisKeyUtil.getOddsTotalBetNumKey(item, seriesType.toString());
        redisClient.hincrBy(oddsTotalBetNumKey, item.getPlayOptions(), 1 * type);
        Expiry.redisKeyExpiry(redisClient, item.getMatchType(), oddsTotalBetNumKey);
        //进球后数据清零
        redisClient.hincrBy(oddsTotalBetNumTempKey, PredictRedisKeyUtil.getOddsTotalCommonKey(item, item.getPlayOptions()), 1 * type);
        Expiry.redisKeyExpiry(redisClient, item.getMatchType(), oddsTotalBetNumTempKey);
        if (item.getBetAmount() == 0) {
            redisClient.hincrBy(oddsTotalBetNumKey, item.getPlayOptions(), -1 * type);
            redisClient.hincrBy(oddsTotalBetNumTempKey, PredictRedisKeyUtil.getOddsTotalCommonKey(item, item.getPlayOptions()), -1 * type);
            log.info("::{}::预测数据计算-投注项级别货量比例为0 笔数不加 {}", item.getOrderNo(), percentage);
        }

        //盘口投注总金额
        String marketTotalBetAmonutKey = PredictRedisKeyUtil.getMarketTotalBetAmonutKey(item, seriesType.toString());
        Long marketTotalBetAmonut = redisClient.hincrBy(marketTotalBetAmonutKey, item.getMarketId().toString(), item.getBetAmount() * type);
        Expiry.redisKeyExpiry(redisClient, item.getMatchType(), marketTotalBetAmonutKey);
        //投注项最大赔付
        String oddsTotalPaidAmonutKey = PredictRedisKeyUtil.getOddsTotalPaidAmonutKey(item, seriesType.toString());
        BigDecimal oddsPaidAmount = new BigDecimal(item.getBetAmount()).multiply(new BigDecimal(item.getOddsValue()).divide(new BigDecimal("100000"), 2, BigDecimal.ROUND_DOWN));
        log.info("::{}::预测数据计算-投注项级别统计-投注项最大赔付-盘口:{}金额:{}", item.getOrderNo(), item.getPlayOptionsId(), oddsPaidAmount);
        redisClient.hincrBy(oddsTotalPaidAmonutKey, item.getPlayOptions(), oddsPaidAmount.longValue() * type);
        Expiry.redisKeyExpiry(redisClient, item.getMatchType(), oddsTotalPaidAmonutKey);
        //投注项级别的期望值=盘口下所有下注项金额汇总-当前投注项级别最大赔付金额
        String oddsProfitAmonutKey = PredictRedisKeyUtil.getOddsProfitAmonutKey(item);
        //获取所有投注项  基于盘口 只是第一次查询数据库  后面都是查缓存
        List<StandardSportMarketOdds> list = getStandardSportMarketOdds(item.getMatchId(), item.getMarketId(), item.getMatchType());
        List<RealTimeVolumeBean> realTimeVolumeBeanList = new ArrayList<>();

        RcsPredictOddsPlaceNumMqVo rcsPredictOddsPlaceNumMqVo = new RcsPredictOddsPlaceNumMqVo();
        rcsPredictOddsPlaceNumMqVo.setLinkId(UUID.randomUUID().toString().replace("-", ""));
        rcsPredictOddsPlaceNumMqVo.setDataType(1);
        rcsPredictOddsPlaceNumMqVo.setDataTypeValue(item.getMarketId());
        rcsPredictOddsPlaceNumMqVo.setMatchId(item.getMatchId());
        rcsPredictOddsPlaceNumMqVo.setPlayId(item.getPlayId());
        rcsPredictOddsPlaceNumMqVo.setSubPlayId(item.getSubPlayId());
        rcsPredictOddsPlaceNumMqVo.setMatchType(item.getMatchType());
        rcsPredictOddsPlaceNumMqVo.setSeriesType(seriesType);
        List<RcsPredictBetOddsVo> rcsPredictBetOddsVoList = new ArrayList<>();
        List<RcsPredictBetOdds> rcsPredictBetOddsList = new ArrayList<>();

        //处理所有投注项的期望值
        for (StandardSportMarketOdds odds : list) {
            //此投注项的 期望(亏损金额)
            if (odds.getId().compareTo(item.getPlayOptionsId()) == 0) {
                BigDecimal oddsProfitAmount = new BigDecimal(item.getBetAmount()).subtract(oddsPaidAmount);
                redisClient.hincrBy(oddsProfitAmonutKey, item.getPlayOptions(), oddsProfitAmount.longValue() * type);
            } else {//期如果不是这个投注项ID 望值+投注金额
                redisClient.hincrBy(oddsProfitAmonutKey, odds.getOddsType(), item.getBetAmount() * type);
            }
            Expiry.redisKeyExpiry(redisClient, item.getMatchType(), oddsProfitAmonutKey);

            //保存
            RcsPredictBetOdds rcsPredictBetOdds = new RcsPredictBetOdds();
            rcsPredictBetOdds.setSportId(item.getSportId().longValue());
            rcsPredictBetOdds.setMatchId(item.getMatchId());
            rcsPredictBetOdds.setPlayId(item.getPlayId());
            rcsPredictBetOdds.setSubPlayId(item.getSubPlayId());
            rcsPredictBetOdds.setDataType(1);
            rcsPredictBetOdds.setDataTypeValue(item.getMarketId());
            rcsPredictBetOdds.setOddsType(odds.getOddsType());
            rcsPredictBetOdds.setMatchType(item.getMatchType());
            rcsPredictBetOdds.setStandardTournamentId(item.getTournamentId());
            Long betOrderNum = LongUtil.parseLong(redisClient.hGet(oddsTotalBetNumKey, odds.getOddsType()));

            Long profitValue = LongUtil.parseLong(redisClient.hGet(oddsProfitAmonutKey, odds.getOddsType()));
            Long totalBetAmount = LongUtil.parseLong(redisClient.hGet(oddsTotalBetAmountKey, odds.getOddsType()));
            Long totalBetAmountPay = LongUtil.parseLong(redisClient.hGet(oddsTotalBetAmountPayKey, odds.getOddsType()));
            Long totalBetAmountComplex = LongUtil.parseLong(redisClient.hGet(oddsTotalBetAmountComplexKey, odds.getOddsType()));
            Long betOrderNumTemp = LongUtil.parseLong(redisClient.hGet(oddsTotalBetNumTempKey, PredictRedisKeyUtil.getOddsTotalCommonKey(item, odds.getOddsType())));
            Long totalBetAmountTemp = LongUtil.parseLong(redisClient.hGet(oddsTotalBetAmountTempKey, PredictRedisKeyUtil.getOddsTotalCommonKey(item, odds.getOddsType())));
            Long totalBetAmountPayTemp = LongUtil.parseLong(redisClient.hGet(oddsTotalBetAmountPayTempKey, PredictRedisKeyUtil.getOddsTotalCommonKey(item, odds.getOddsType())));
            Long totalBetAmountComplexTemp = LongUtil.parseLong(redisClient.hGet(oddsTotalBetAmountComplexTempKey, PredictRedisKeyUtil.getOddsTotalCommonKey(item, odds.getOddsType())));
            Long paidAmount = LongUtil.parseLong(redisClient.hGet(oddsTotalPaidAmonutKey, odds.getOddsType()));
            rcsPredictBetOdds.setBetOrderNum(new BigDecimal(betOrderNum));
            rcsPredictBetOdds.setBetOrderNumTemp(new BigDecimal(betOrderNumTemp));
            rcsPredictBetOdds.setBetAmount(new BigDecimal(totalBetAmount).divide(BigDecimal.valueOf(100)));
            rcsPredictBetOdds.setBetAmountTemp(new BigDecimal(totalBetAmountTemp).divide(BigDecimal.valueOf(100)));
            rcsPredictBetOdds.setBetAmountPay(new BigDecimal(totalBetAmountPay).divide(BigDecimal.valueOf(100)));
            rcsPredictBetOdds.setBetAmountPayTemp(new BigDecimal(totalBetAmountPayTemp).divide(BigDecimal.valueOf(100)));
            rcsPredictBetOdds.setBetAmountComplex(new BigDecimal(totalBetAmountComplex).divide(BigDecimal.valueOf(100)));
            rcsPredictBetOdds.setBetAmountComplexTemp(new BigDecimal(totalBetAmountComplexTemp).divide(BigDecimal.valueOf(100)));
            rcsPredictBetOdds.setProfitValue(new BigDecimal(profitValue).divide(BigDecimal.valueOf(100)));
            rcsPredictBetOdds.setPaidAmount(new BigDecimal(paidAmount).divide(BigDecimal.valueOf(100)));
            rcsPredictBetOdds.setModifyTime(System.currentTimeMillis());
            rcsPredictBetOdds.setSeriesType(seriesType);
            String unique = "matchId.%s.playId.%s.matchType.%s.dataType.%s.dataTypeValue.%s.oddsType.%s.subPlayId.%s.seriesType.%s";
            String format = String.format(unique, rcsPredictBetOdds.getMatchId(), rcsPredictBetOdds.getPlayId(), rcsPredictBetOdds.getMatchType(),
                    rcsPredictBetOdds.getDataType(), rcsPredictBetOdds.getDataTypeValue(), rcsPredictBetOdds.getOddsType(),
                    rcsPredictBetOdds.getSubPlayId(), rcsPredictBetOdds.getSeriesType());
            rcsPredictBetOdds.setHashUnique(DigestUtil.md5Hex(format));
            rcsPredictBetOddsList.add(rcsPredictBetOdds);
            //双重机会 特殊处理期望值   总投注-两个投注项的赔付
            if (item.getPlayId() == 6 || item.getPlayId() == 70 || item.getPlayId() == 72) {
                //期望值 减去本投注项的赔付值
                profitValue = marketTotalBetAmonut - paidAmount;
                //根据公式 找到另一个选项的赔付值 减它
                profitValue -= getDoubeChangceOtherPaidAmount(odds, list, item, seriesType);
                rcsPredictBetOdds.setProfitValue(new BigDecimal(profitValue / 100));
                redisClient.hSet(oddsProfitAmonutKey, odds.getOddsType(), String.valueOf(profitValue));
                Expiry.redisKeyExpiry(redisClient, item.getMatchType(), oddsProfitAmonutKey);
            }

            RcsPredictBetOddsVo rcsPredictBetOddsVo = new RcsPredictBetOddsVo();
            BeanUtils.copyProperties(rcsPredictBetOdds, rcsPredictBetOddsVo);
            rcsPredictBetOddsVo.setOrderOdds(odds.getOrderOdds() == null ? 0 : odds.getOrderOdds());
            rcsPredictBetOddsVoList.add(rcsPredictBetOddsVo);

            //*************************推送数据************************************************
            RealTimeVolumeBean pushData = new RealTimeVolumeBean();
            pushData.setSportId(item.getSportId());
            pushData.setMatchId(item.getMatchId());
            pushData.setPlayId(item.getPlayId());
            pushData.setSubPlayId(item.getSubPlayId());
            pushData.setMatchMarketId(item.getMarketId());
            pushData.setMatchType(item.getMatchType().toString());
            pushData.setStandardTournamentId(item.getTournamentId());
            pushData.setBetOrderNum(new BigDecimal(betOrderNum));
            pushData.setSumMoney(new BigDecimal(totalBetAmount).divide(BigDecimal.valueOf(100)));
            pushData.setProfitValue(new BigDecimal(profitValue).divide(BigDecimal.valueOf(100)));
            pushData.setPaidAmount(new BigDecimal(paidAmount).divide(BigDecimal.valueOf(100)));
            pushData.setPlayOptionsId(odds.getId());
            pushData.setProfitValue(new BigDecimal(profitValue / 100));
            pushData.setMarketIndex(item.getPlaceNum());
            pushData.setSeriesType(seriesType);
            //ws待完善 原来推的是 realTimeVolumeBean
            producerSendMessageUtils.sendMessage(MqConstants.ORDER_AMOUNT_CHANGE_TOPIC, pushData);
            //*************************推送数据************************************************
            log.info("::{}::预测数据计算-投注项级别统计MYSQL_PROFIT_ODDS推送完成{}", item.getOrderNo(), odds.getId());
            realTimeVolumeBeanList.add(pushData);
        }
        //异步存库
        if (!CollectionUtils.isEmpty(rcsPredictBetOddsList) && nx) {
            RcsPredictBetOdds rcsPredictBetOdds = rcsPredictBetOddsList.get(0);
            HashMap<String, String> mqMap = new HashMap<>();
            mqMap.put("time", "" + System.currentTimeMillis());
            mqMap.put("orderNo", item.getOrderNo());
            String hashKey = String.format("mq_data_rcs_predict_bet_odds:%s_%s_%s_%s_%s_%s", rcsPredictBetOdds.getMatchId(), rcsPredictBetOdds.getPlayId(), rcsPredictBetOdds.getMatchType(), rcsPredictBetOdds.getDataType(), rcsPredictBetOdds.getOddsType(), rcsPredictBetOdds.getSeriesType());
            producerSendMessageUtils.sendMsg("mq_data_rcs_predict_bet_odds", "", "", JSONObject.toJSONString(rcsPredictBetOddsList), mqMap, hashKey);
            log.info("::{}::rcs_predict_bet_odds表mq入库完成:", item.getOrderNo());
        } else {
            log.info("::{}::rcs_predict_bet_odds表mq入库频率限制！", item.getOrderNo());
        }
        rcsPredictBetOddsVoList = rcsPredictBetOddsVoList.stream().sorted(Comparator.comparing(RcsPredictBetOddsVo::getOrderOdds)).collect(Collectors.toList());
        rcsPredictOddsPlaceNumMqVo.setList(rcsPredictBetOddsVoList);

        //ws待完善  出涨值/预期赔付值 触发通知
        HashMap<String, String> mqMap = new HashMap<>();
        mqMap.put("SEND_SERVICE_GROUP", "KIR_TEST_GROUP");
        producerSendMessageUtils.sendMessage("rcs_predict_odds_data", realTimeVolumeBeanList);
        log.info("::{}::预测数据计算--投注项rcs_predict_odds_data推送完成  :{}", item.getOrderNo(), item.getBetNo());

        if (nx) {
            log.info("::{}::预测数据计算-投注项rcs_predict_odds_placeNum_ws推送开始", item.getOrderNo());
            producerSendMessageUtils.sendMessage("rcs_predict_odds_placeNum_ws", "", rcsPredictOddsPlaceNumMqVo.getMatchId() + "_" + rcsPredictOddsPlaceNumMqVo.getPlayId(), rcsPredictOddsPlaceNumMqVo);
            log.info("::{}::预测数据计算--投注项rcs_predict_odds_placeNum_ws推送完成 ", item.getOrderNo());
        } else {
            log.info("::{}::预测数据计算--投注项rcs_predict_odds_placeNum_ws推送频率限制！", item.getOrderNo());
        }
        //1608把玩法和一些坑位放入缓存
        if (item.getSportId() == 1 && playIdKeys.contains(item.getPlayId())) {
            String key = PredictRedisKeyUtil.getMarketIdCommonKey(item);
            redisClient.hSet(key, item.getMarketId().toString(), JSONObject.toJSONString(rcsPredictOddsPlaceNumMqVo));
            Expiry.redisKeyExpiry(redisClient, item.getMatchType(), key);
        }
    }


    //玩法ID
    private static List<Integer> playIdKeys = Lists.newArrayList(2, 4, 18, 19);
    //篮球主控玩法id
    private static List<Integer> playIdKeys2 = Lists.newArrayList(87,97,42,18,19,198,199,40,38,39);
    private static List<Integer> SERIES_LIST = Lists.newArrayList(2001, 3001, 3004, 4001, 40011, 5001, 50026, 6001, 60057, 7001, 700120, 8001, 800247, 9001, 900502, 10001, 10001013);

    private void deleteGoldKey(OrderItem item) {
        String str = "rcs:risk:predict:palceNumTotalBetAmountTemp.match_id.%s.match_type.%s.play_id.%s";
        String str1 = "rcs:risk:predict:palceNumTotalBetAmountPayTemp.match_id.%s.match_type.%s.play_id.%s";
        String str2 = "rcs:risk:predict:palceNumTotalBetAmountComplexTemp.match_id.%s.match_type.%s.play_id.%s";
        String str3 = "rcs:risk:predict:palceNumTotalBetNumTemp.match_id.%s.match_type.%s.play_id.%s";
        String goldRedisKey = PredictRedisKeyUtil.getRcsOddsGoalRedisKey(item);
        String redisValue = redisClient.get(goldRedisKey);
        log.info("::{}::,进球标识:{},开始删除key", item.getOrderNo(), redisValue);
        if (StringUtils.isNotBlank(redisValue)) {
            Integer goldVal = Integer.valueOf(redisValue);
            //只对足球下面的 4个玩法做删除
            if (goldVal == 1 && playIdKeys.contains(item.getPlayId())) {
                playIdKeys.forEach(s -> {
                    this.delteCommonKey(item, str, str1, str2, str3, s);
                    SERIES_LIST.forEach(s2 -> {
                        String itemStr = s2 + str;
                        String itemStr1 = s2 + str1;
                        String itemStr2 = s2 + str2;
                        String itemStr3 = s2 + str3;
                        this.delteCommonKey(item, itemStr, itemStr1, itemStr2, itemStr3, s);
                    });
                });
                //删除标识，进球后在继续设置值
                redisClient.delete(goldRedisKey);
            }
        }
    }

    public void delteCommonKey(OrderItem item, String key1, String key2, String key3, String key4, Integer playId) {
        String stray1 = String.format(key1, item.getMatchId(), item.getMatchType(), playId);
        String stray2 = String.format(key2, item.getMatchId(), item.getMatchType(), playId);
        String stray3 = String.format(key3, item.getMatchId(), item.getMatchType(), playId);
        String stray4 = String.format(key4, item.getMatchId(), item.getMatchType(), playId);
        log.info("::{}::,开始删除key:{},{},{},{}", item.getOrderNo(), stray1, stray2, stray3, stray4);
        redisClient.delete(stray1);
        redisClient.delete(stray2);
        redisClient.delete(stray3);
        redisClient.delete(stray4);
    }

    /**
     * 盘口位置货量 /笔数
     *
     * @param item
     * @param type 1标识下单 增加计算  -1表示取消订单
     */
    @Override
    public void calculatePlaceNumData(OrderItem item, Integer type, Integer seriesType, boolean nx) {
        log.info("::{}::预测数据计算-坑位级别货量 处理开始{}", item.getOrderNo(), item.getBetNo());
        String palceNumTotalBetAmountTempKey = PredictRedisKeyUtil.getPalceNumTotalBetAmountTempKey(item, seriesType.toString());
        String palceNumTotalBetAmountPayTempKey = PredictRedisKeyUtil.getPalceNumTotalBetAmountPayTempKey(item, seriesType.toString());
        String palceNumTotalBetAmountComplexTempKey = PredictRedisKeyUtil.getPalceNumTotalBetAmountComplexTempKey(item, seriesType.toString());
        String palceNumBetNumTempKey = PredictRedisKeyUtil.getPalceNumTotalBetNumTempKey(item, seriesType.toString());
        if (item.getMatchType() == 1) {
            predictResetRedisKeyBo.resetPlaceNumRedisKey(item, seriesType);
        }
        //1608koala清空数据
        deleteGoldKey(item);
        //获取用户标签 货量比例
        BigDecimal percentage = BigDecimal.valueOf(1);// predictCommonService.getUserTagPercentage(item.getUid());
        //log.info("预测数据计算-盘口位置货量比例{}:{}", item.getOrderNo(), percentage);
        //盘口位置 投注项总投注
        String palceNumTotalBetAmountKey = PredictRedisKeyUtil.getPalceNumTotalBetAmountKey(item, seriesType.toString());
        BigDecimal betAmount = new BigDecimal(item.getBetAmount() * type).multiply(percentage);
        redisClient.hincrBy(palceNumTotalBetAmountKey, item.getPlayOptions(), betAmount.longValue());
        Expiry.redisKeyExpiry(redisClient, item.getMatchType(), palceNumTotalBetAmountKey);

        redisClient.hincrBy(palceNumTotalBetAmountTempKey, PredictRedisKeyUtil.getCommonKey(item, item.getPlayOptions()), betAmount.longValue());
        String palceNumTotalBetAmountTempValue = redisClient.hGet(palceNumTotalBetAmountTempKey, PredictRedisKeyUtil.getCommonKey(item, item.getPlayOptions()));
        Expiry.redisKeyExpiry(redisClient, item.getMatchType(), palceNumTotalBetAmountTempKey);
        //纯赔付货量
        BigDecimal oddValue = BigDecimal.valueOf(item.getHandleAfterOddsValue()).subtract(new BigDecimal("1"));
        BigDecimal betAmountPay = new BigDecimal(item.getBetAmount()).multiply(oddValue.multiply(new BigDecimal(type))).multiply(percentage);
        String palceNumTotalBetAmountPayKey = PredictRedisKeyUtil.getPalceNumTotalBetAmountPayKey(item, seriesType.toString());
        redisClient.hincrBy(palceNumTotalBetAmountPayKey, item.getPlayOptions(), betAmountPay.longValue());
        Expiry.redisKeyExpiry(redisClient, item.getMatchType(), palceNumTotalBetAmountPayKey);

        redisClient.hincrBy(palceNumTotalBetAmountPayTempKey, PredictRedisKeyUtil.getCommonKey(item, item.getPlayOptions()), betAmountPay.longValue());
        String palceNumTotalBetAmountPayTemp = redisClient.hGet(palceNumTotalBetAmountPayTempKey, PredictRedisKeyUtil.getCommonKey(item, item.getPlayOptions()));
        Expiry.redisKeyExpiry(redisClient, item.getMatchType(), palceNumTotalBetAmountPayTempKey);

        //混合型货量
        BigDecimal betAmountComplex = betAmount;
        if (oddValue.compareTo(new BigDecimal("1")) > 0) {
            betAmountComplex = betAmountPay;
        }
        String palceNumTotalBetAmountComplexKey = PredictRedisKeyUtil.getPalceNumTotalBetAmountComplexKey(item, seriesType.toString());
        redisClient.hincrBy(palceNumTotalBetAmountComplexKey, item.getPlayOptions(), betAmountComplex.longValue());
        Expiry.redisKeyExpiry(redisClient, item.getMatchType(), palceNumTotalBetAmountComplexKey);

        redisClient.hincrBy(palceNumTotalBetAmountComplexTempKey, PredictRedisKeyUtil.getCommonKey(item, item.getPlayOptions()), betAmountComplex.longValue());
        String palceNumTotalBetAmountComplexTemp = redisClient.hGet(palceNumTotalBetAmountComplexTempKey, PredictRedisKeyUtil.getCommonKey(item, item.getPlayOptions()));
        Expiry.redisKeyExpiry(redisClient, item.getMatchType(), palceNumTotalBetAmountComplexTempKey);

        //盘口位置 投注项 总投注笔数
        String palceNumBetNumKey = PredictRedisKeyUtil.getPalceNumTotalBetNumKey(item, seriesType.toString());
        redisClient.hincrBy(palceNumBetNumKey, item.getPlayOptions(), 1 * type);
        Expiry.redisKeyExpiry(redisClient, item.getMatchType(), palceNumBetNumKey);
        redisClient.hincrBy(palceNumBetNumTempKey, PredictRedisKeyUtil.getCommonKey(item, item.getPlayOptions()), 1 * type);
        String palceNumBetNumTemp = redisClient.hGet(palceNumBetNumTempKey, PredictRedisKeyUtil.getCommonKey(item, item.getPlayOptions()));
        Expiry.redisKeyExpiry(redisClient, item.getMatchType(), palceNumBetNumTempKey);
        if (type == -1) {
            if (!StringUtils.isEmpty(palceNumBetNumTemp) && Long.parseLong(palceNumBetNumTemp) < 0) {
                redisClient.hSet(palceNumBetNumTempKey, PredictRedisKeyUtil.getCommonKey(item, item.getPlayOptions()), String.valueOf(0));
            }
            if (!StringUtils.isEmpty(palceNumTotalBetAmountComplexTemp) && Long.parseLong(palceNumTotalBetAmountComplexTemp) < 0) {
                redisClient.hSet(palceNumTotalBetAmountComplexTempKey, PredictRedisKeyUtil.getCommonKey(item, item.getPlayOptions()), String.valueOf(0));
            }
            if (!StringUtils.isEmpty(palceNumTotalBetAmountPayTemp) && Long.parseLong(palceNumTotalBetAmountPayTemp) < 0) {
                redisClient.hSet(palceNumTotalBetAmountPayTempKey, PredictRedisKeyUtil.getCommonKey(item, item.getPlayOptions()), String.valueOf(0L));
            }
            if (!StringUtils.isEmpty(palceNumTotalBetAmountTempValue) && Long.parseLong(palceNumTotalBetAmountTempValue) < 0) {
                redisClient.hSet(palceNumTotalBetAmountTempKey, PredictRedisKeyUtil.getCommonKey(item, item.getPlayOptions()), String.valueOf(0));
            }
        }

        if (item.getBetAmount() == 0) {
            redisClient.hincrBy(palceNumBetNumKey, PredictRedisKeyUtil.getCommonKey(item, item.getPlayOptions()), -1 * type);
            redisClient.hincrBy(palceNumBetNumTempKey, item.getPlayOptions(), -1 * type);
            log.info("::{}::预测数据计算-盘口位置货量比例为0 笔数不加:{}", item.getOrderNo(), percentage);
        }

        RcsPredictOddsPlaceNumMqVo rcsPredictOddsPlaceNumMqVo = new RcsPredictOddsPlaceNumMqVo();
        rcsPredictOddsPlaceNumMqVo.setLinkId(UUID.randomUUID().toString().replace("-", ""));
        rcsPredictOddsPlaceNumMqVo.setDataType(2);
        rcsPredictOddsPlaceNumMqVo.setDataTypeValue(item.getPlaceNum().longValue());
        rcsPredictOddsPlaceNumMqVo.setMatchId(item.getMatchId());
        rcsPredictOddsPlaceNumMqVo.setSubPlayId(item.getSubPlayId());
        rcsPredictOddsPlaceNumMqVo.setPlayId(item.getPlayId());
        rcsPredictOddsPlaceNumMqVo.setMatchType(item.getMatchType());
        rcsPredictOddsPlaceNumMqVo.setPlayOptions(item.getPlayOptions());
        rcsPredictOddsPlaceNumMqVo.setSeriesType(seriesType);
        rcsPredictOddsPlaceNumMqVo.setMarketId(item.getMarketId());
        List<RcsPredictBetOddsVo> rcsPredictBetOddsVoList = new ArrayList<>();

        //获取所有投注项  基于盘口 只是第一次查询数据库  后面都是查缓存
        List<StandardSportMarketOdds> list = getStandardSportMarketOdds(item.getMatchId(), item.getMarketId(), item.getMatchType());
        log.info("::{}::订单获取所有投注项结果：{}", item.getOrderNo(), list.stream().map(StandardSportMarketOdds::getOddsType).collect(Collectors.joining(",")));
        List<RcsPredictBetOdds> saveRcsPredictBetOddsList = new ArrayList<>();
        //处理所有投注项的期望值
        for (StandardSportMarketOdds odds : list) {
            //保存
            RcsPredictBetOdds bean = new RcsPredictBetOdds();
            bean.setSportId(item.getSportId().longValue());
            bean.setMatchId(item.getMatchId());
            bean.setPlayId(item.getPlayId());
            bean.setSubPlayId(item.getSubPlayId());
            bean.setDataType(2);
            bean.setDataTypeValue(item.getPlaceNum().longValue());
            bean.setOddsType(odds.getOddsType());
            bean.setMatchType(item.getMatchType());
            bean.setStandardTournamentId(item.getTournamentId());
            Long betOrderNum = LongUtil.parseLong(redisClient.hGet(palceNumBetNumKey, odds.getOddsType()));
            Long totalBetAmount = LongUtil.parseLong(redisClient.hGet(palceNumTotalBetAmountKey, odds.getOddsType()));
            Long totalBetAmountPay = LongUtil.parseLong(redisClient.hGet(palceNumTotalBetAmountPayKey, odds.getOddsType()));
            Long totalBetAmountComplex = LongUtil.parseLong(redisClient.hGet(palceNumTotalBetAmountComplexKey, odds.getOddsType()));
            //koala1608需求
            Long betOrderNumTemp = LongUtil.parseLong(redisClient.hGet(palceNumBetNumTempKey, PredictRedisKeyUtil.getCommonKey(item, odds.getOddsType())));
            Long totalBetAmountTemp = LongUtil.parseLong(redisClient.hGet(palceNumTotalBetAmountTempKey, PredictRedisKeyUtil.getCommonKey(item, odds.getOddsType())));
            Long totalBetAmountPayTemp = LongUtil.parseLong(redisClient.hGet(palceNumTotalBetAmountPayTempKey, PredictRedisKeyUtil.getCommonKey(item, odds.getOddsType())));
            Long totalBetAmountComplexTemp = LongUtil.parseLong(redisClient.hGet(palceNumTotalBetAmountComplexTempKey, PredictRedisKeyUtil.getCommonKey(item, odds.getOddsType())));
            bean.setBetOrderNum(new BigDecimal(betOrderNum));
            bean.setBetAmount(new BigDecimal(totalBetAmount).divide(new BigDecimal(100), 2, RoundingMode.DOWN));
            log.info("::{}LN模式货量纯投注额:{}，key：{}",item.getOrderNo(), bean.getBetAmount(),palceNumTotalBetAmountKey+":"+odds.getOddsType());
            bean.setBetAmountPay(new BigDecimal(totalBetAmountPay).divide(new BigDecimal(100), 2, RoundingMode.DOWN));
            bean.setBetAmountComplex(new BigDecimal(totalBetAmountComplex).divide(new BigDecimal(100), 2, RoundingMode.DOWN));

            bean.setBetOrderNumTemp(new BigDecimal(betOrderNumTemp));
            bean.setBetAmountTemp(new BigDecimal(totalBetAmountTemp).divide(new BigDecimal(100), 2, RoundingMode.DOWN));
            bean.setBetAmountPayTemp(new BigDecimal(totalBetAmountPayTemp).divide(new BigDecimal(100), 2, RoundingMode.DOWN));
            bean.setBetAmountComplexTemp(new BigDecimal(totalBetAmountComplexTemp).divide(new BigDecimal(100), 2, RoundingMode.DOWN));

            bean.setPaidAmount(BigDecimal.ZERO);
            bean.setProfitValue(BigDecimal.ZERO);
            bean.setModifyTime(System.currentTimeMillis());
            bean.setSeriesType(seriesType);
            String unique = "matchId.%s.playId.%s.matchType.%s.dataType.%s.dataTypeValue.%s.oddsType.%s.subPlayId.%s.seriesType.%s";
            String format = String.format(unique, bean.getMatchId(), bean.getPlayId(), bean.getMatchType(),
                    bean.getDataType(), bean.getDataTypeValue(), bean.getOddsType(),
                    bean.getSubPlayId(), bean.getSeriesType());
            bean.setHashUnique(DigestUtil.md5Hex(format));
            saveRcsPredictBetOddsList.add(bean);

            RcsPredictBetOddsVo rcsPredictBetOddsVo = new RcsPredictBetOddsVo();
            BeanUtils.copyProperties(bean, rcsPredictBetOddsVo);
            rcsPredictBetOddsVo.setOrderOdds(odds.getOrderOdds());
            rcsPredictBetOddsVoList.add(rcsPredictBetOddsVo);
        }
        if (item.getSportId() == 2 && playIdKeys2.contains(item.getPlayId())){
            log.info("{}::LN模式货量list:{}"+item.getOrderNo(), JSON.toJSONString(rcsPredictBetOddsVoList));
        }
        String lastMarketChangeKey = String.format("rcs:risk:predict:marketChange.match_id.%s.match_type.%s.play_id.%s.seriesType.%s", item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getSeriesType());
        String lastMarketChangeData = redisClient.get(lastMarketChangeKey);
        if (!StringUtils.isEmpty(lastMarketChangeData)) {
            Map<String, Object> map = JSONObject.parseObject(lastMarketChangeData, Map.class);
            if (!map.get("lastMarketId").equals(item.getMarketId())) {
                // 盘口变动后 立马重新推送一次
                List<RcsPredictBetOdds> saveRcsPredictBetOddsList1 = JSONArray.parseArray(map.get("saveRcsPredictBetOddsList").toString(), RcsPredictBetOdds.class);
                RcsPredictOddsPlaceNumMqVo rcsPredictOddsPlaceNumMqVo1 = JSONObject.parseObject(map.get("rcsPredictOddsPlaceNumMqVo").toString(), RcsPredictOddsPlaceNumMqVo.class);
                List<RcsPredictBetOddsVo> rcsPredictBetOddsVoList1 = JSONArray.parseArray(map.get("rcsPredictBetOddsVoList").toString(), RcsPredictBetOddsVo.class);
                OrderItem orderItem = JSONObject.parseObject(map.get("orderItem").toString(), OrderItem.class);
                sendDBAndRedis(saveRcsPredictBetOddsList1, rcsPredictOddsPlaceNumMqVo1, rcsPredictBetOddsVoList1, orderItem);
                redisClient.delete(lastMarketChangeKey);
                log.info("::{}:: 当前订单与上一次订单盘口id不同立即重新推送上次盘口", item.getOrderNo());
            }
        }
        if (nx) {
            sendDBAndRedis(saveRcsPredictBetOddsList, rcsPredictOddsPlaceNumMqVo, rcsPredictBetOddsVoList, item);
        } else {
            log.info("::{}::预测数据计算--坑位级别货量rcs_predict_odds_placeNum_ws 推送频率限制！", item.getOrderNo());
            Map<String, Object> redisData = new HashMap<>();
            redisData.put("saveRcsPredictBetOddsList", saveRcsPredictBetOddsList);
            rcsPredictBetOddsVoList = rcsPredictBetOddsVoList.stream().sorted(Comparator.comparing(RcsPredictBetOddsVo::getOrderOdds)).collect(Collectors.toList());
            rcsPredictOddsPlaceNumMqVo.setList(rcsPredictBetOddsVoList);
            redisData.put("rcsPredictOddsPlaceNumMqVo", JSONObject.toJSONString(rcsPredictOddsPlaceNumMqVo));
            redisData.put("rcsPredictBetOddsVoList", JSONArray.toJSONString(rcsPredictBetOddsVoList));
            redisData.put("lastMarketId", item.getMarketId());
            redisData.put("orderItem", JSONObject.toJSONString(item));
            redisClient.set(lastMarketChangeKey, JSONObject.toJSONString(redisData));
            Expiry.redisKeyExpiryStatis(redisClient, item.getMatchType(), lastMarketChangeKey);
        }
        log.info("::{}::盘口位置rcs_predict_odds_placeNum_ws推送完成 ", item.getOrderNo());
        //把玩法和一些坑位放入缓存
        if (item.getSportId() == 1 && playIdKeys.contains(item.getPlayId())) {
            String key = PredictRedisKeyUtil.getPlaceNumCommonKey(item);
            log.info("::{}:: key:{},坑位:{},开始存入坑位数据", item.getOrderNo(), key, item.getPlaceNum());
            redisClient.hSet(key, item.getPlaceNum().toString(), JSONObject.toJSONString(rcsPredictOddsPlaceNumMqVo));
            Expiry.redisKeyExpiry(redisClient, item.getMatchType(), key);
        }
        if (item.getSportId() == 2 && playIdKeys2.contains(item.getPlayId())){
            String key = PredictRedisKeyUtil.getPlaceNumCommonKey(item);
            log.info("::{}:: key:{},坑位:{},LN篮球开始存入坑位数据,playOptions:{}", item.getOrderNo(), key, item.getPlaceNum(),item.getPlayOptions());
            //不存在时存储
            if (!redisClient.exist( key+"."+item.getPlaceNum().toString()+"."+item.getPlayOptions())){
                redisClient.set(key+"."+item.getPlaceNum().toString()+"."+item.getPlayOptions(), JSONObject.toJSONString(rcsPredictOddsPlaceNumMqVo));
                Expiry.redisKeyExpiry(redisClient, item.getMatchType(), key);
            }
        }
    }

    private void sendDBAndRedis(List<RcsPredictBetOdds> saveRcsPredictBetOddsList, RcsPredictOddsPlaceNumMqVo rcsPredictOddsPlaceNumMqVo,
                                List<RcsPredictBetOddsVo> rcsPredictBetOddsVoList, OrderItem item) {
        //异步存库
        if (!CollectionUtils.isEmpty(saveRcsPredictBetOddsList)) {
            RcsPredictBetOdds rcsPredictBetOdds = saveRcsPredictBetOddsList.get(0);
            HashMap<String, String> mqMap = new HashMap<>();
            mqMap.put("time", "" + System.currentTimeMillis());
            String hashKey = String.format("mq_data_rcs_predict_bet_odds:%s_%s_%s_%s_%s_%s_%s", rcsPredictBetOdds.getMatchId(),
                    rcsPredictBetOdds.getPlayId(), rcsPredictBetOdds.getSubPlayId(), rcsPredictBetOdds.getMatchType(),
                    rcsPredictBetOdds.getDataType(), rcsPredictBetOdds.getOddsType(), rcsPredictBetOdds.getSeriesType());
            producerSendMessageUtils.sendMsg("mq_data_rcs_predict_bet_odds", "", "", JSONObject.toJSONString(saveRcsPredictBetOddsList), mqMap, hashKey);
            log.info("::{}::订单 rcs_predict_bet_odds表坑位mq入库完成:{}", item.getOrderNo(), hashKey);
        }
        rcsPredictBetOddsVoList = rcsPredictBetOddsVoList.stream().sorted(Comparator.comparing(RcsPredictBetOddsVo::getOrderOdds)).collect(Collectors.toList());
        rcsPredictOddsPlaceNumMqVo.setList(rcsPredictBetOddsVoList);
        if (item.getSportId() == 2 && playIdKeys2.contains(item.getPlayId())){
            log.info("{}::LN模式货量list:排序后:{}"+item.getOrderNo(), JSON.toJSONString(rcsPredictBetOddsVoList));
        }
        log.info("::{}::预测数据计算-坑位级别货量rcs_predict_odds_placeNum_ws 推送开始", item.getOrderNo());
        producerSendMessageUtils.sendMessage("rcs_predict_odds_placeNum_ws", "", rcsPredictOddsPlaceNumMqVo.getMatchId() + "_" + rcsPredictOddsPlaceNumMqVo.getPlayId(), rcsPredictOddsPlaceNumMqVo);
        log.info("::{}::预测数据计算--坑位级别货量rcs_predict_odds_placeNum_ws 推送完成!", item.getOrderNo());

    }


    /**
     * 篮球矩阵计算
     *
     * @param type 1标识下单 增加计算  -1表示取消订单
     */
    @Override
    public void calculateBasketballMatrix(OrderBean orderBean, Integer type, boolean nx) {
        OrderItem item = orderBean.getItems().get(0);
        BasketBallForecastService service = basketMatrixService.getBasketBallForecastService(item.getPlayId());
        if (service == null) {
            log.info("::{}::预测数据计算-篮球无需处理该玩法 ,玩法:{}", item.getOrderNo(), item.getPlayId());
            return;
        }
        log.info("::{}::预测数据计算-篮球矩阵 处理开始 : {},类型:{}", item.getOrderNo(), service, type);
        service.forecastData(orderBean, type, nx);
        if (item.getMatchType() == 1) {
            redisClient.expireKey(basketMatrixService.getCacheKey(item), 30 * 24 * 60 * 60);
        } else {
            redisClient.expireKey(basketMatrixService.getCacheKey(item), 24 * 60 * 60);
        }
        log.info("::{}::预测数据计算-篮球矩阵 处理完成 : {},类型:{}", item.getOrderNo(), item.getBetNo(), type);
    }

    /**
     * 足球矩阵计算
     * kir
     *
     * @param type 1标识下单 增加计算  -1表示取消订单
     */
    @Override
    public void calculateFootballMatrix(OrderBean orderBean, Integer type) {
        OrderItem item = orderBean.getItems().get(0);
        log.info("::{}::足球矩阵计算:{},类型:{}", orderBean.getOrderNo(), JSONObject.toJSONString(item), type);
        try {
            //早盘和滚球存矩阵
            footballMatrixService.footballMatrixData(item, type, orderBean.getTenantId());
        } catch (Exception e) {
            log.error("::{}::足球矩阵计算出错:{},  {}", orderBean.getOrderNo(), e.getMessage(), e);
        }
        log.info("::{}::足球矩阵计算完成", orderBean.getOrderNo());
    }

    /**
     * 足球玩法级别forecast
     */
    @Override
    public void calculatePlayData(OrderBean orderBean, Integer type, boolean nx) {
        OrderItem item = orderBean.getItems().get(0);
        ForecastService service = predictCommonService.getFootPlayForecastService(item.getPlayId());
        if (service == null) {
            log.info("::{}::预测数据计算-足球玩法级别forecast无需处理该玩法 ,玩法:{}", item.getOrderNo(), item.getPlayId());
            return;
        }
        log.info("::{}::预测数据计算-足球玩法级别forecast 处理开始 : {},类型:{}", item.getOrderNo(), service, type);
        service.forecastData(item, type, nx);
        log.info("::{}::预测数据计算足球玩法级别forecast 处理完成 : {},类型:{}", item.getOrderNo(), item.getBetNo(), type);
    }

    public void calculatePlaceNumData(OrderBean orderBean, Integer type, boolean nx) {
        OrderItem item = orderBean.getItems().get(0);
        ForecastService service = predictCommonService.getFootPlaceNumForecastService(item.getPlayId());
        if (service == null) {
            log.warn("::{}::预测数据计算-足球玩法级别forecast无需处理该玩法 玩法:{}", item.getOrderNo(), item.getPlayId());
            return;
        }
        log.info("::{}::预测数据计算-足球玩法级别forecast 处理开始 : {},类型:{}", item.getOrderNo(), service, type);
        service.forecastData(item, type, nx);
        log.info("::{}::预测数据计算足球玩法级别forecast 处理完成 : {},类型:{}", item.getOrderNo(), item.getBetNo(), type);
    }


    /**
     * 赛事级别的计算
     *
     * @param item
     * @param type 1标识下单 增加计算  -1表示取消订单
     */
    @Override
    public void calculateMatchData(OrderItem item, Integer type) {
        log.info("::{}::赛事id:{},预测数据计算-赛事级别,处理开始:{}", item.getOrderNo(), item.getMatchId(), JSONObject.toJSONString(item));
        //赛事总投注金额
        String matchTotalBetAmonutKey = PredictRedisKeyUtil.getMatchTotalBetAmonutKey();
        //Long matchTotalBetAmonut = redisClient.hincrBy(matchTotalBetAmonutKey, item.getMatchId().toString(), item.getBetAmount() * type);

        //赛事总投注新key
        Long matchTotalBetAmonut = 0L;
        String newMatchTotalBetAmonutKey = PredictRedisKeyUtil.MATCH_TOTAL_BET_AMONUT_KEY + item.getMatchId();
        long matchBetAmount = item.getBetAmount() * type;
        log.info("::{}::赛事id:{},预测数据计算，投注金额:{}", item.getOrderNo(), item.getMatchId(), matchBetAmount);
        String newMatchTotalBetAmonut = redisClient.get(newMatchTotalBetAmonutKey);
        log.info("::{}::赛事id:{},预测数据计算，投注累计金额before:{}", item.getOrderNo(), item.getMatchId(), newMatchTotalBetAmonut);
        //兼容老数据
        if (StringUtils.isBlank(newMatchTotalBetAmonut)) {
            newMatchTotalBetAmonut = redisClient.hGet(matchTotalBetAmonutKey, item.getMatchId().toString());
            log.info("::{}::赛事id:{},投注累计金额，旧缓存取值:{}", item.getOrderNo(), item.getMatchId(), newMatchTotalBetAmonut);
            if (StringUtils.isNotBlank(newMatchTotalBetAmonut)) {
                redisClient.set(newMatchTotalBetAmonutKey, newMatchTotalBetAmonut);
            }
        }
        matchTotalBetAmonut = redisClient.incrBy(newMatchTotalBetAmonutKey, matchBetAmount);
        log.info("::{}::赛事id:{},预测数据计算，投注累计金额after:{}", item.getOrderNo(), item.getMatchId(), matchTotalBetAmonut);
        Expiry.redisKeyExpiry(redisClient, item.getMatchType(), newMatchTotalBetAmonutKey);

        //赛事总投注笔数
        String matchTotalBetNumKey = PredictRedisKeyUtil.getMatchTotalBetNumKey();
        //Long matchTotalBetTimes = redisClient.hincrBy(matchTotalBetNumKey, item.getMatchId().toString(), 1 * type);

        Long matchTotalBetTimes = 0L;
        int matchBetNum = 1 * type;
        log.info("::{}::赛事id:{},预测数据计算，matchBetNum:{}", item.getOrderNo(), item.getMatchId(), matchBetNum);
        //赛事总投注笔数新key
        String newMatchTotalBetNumKey = PredictRedisKeyUtil.MATCH_TOTAL_BET_NUM_KEY + item.getMatchId();
        String totalMatchBetNum = redisClient.get(newMatchTotalBetNumKey);
        log.info("::{}::赛事id:{},预测数据计算，投注累计数量before:{}", item.getOrderNo(), item.getMatchId(), totalMatchBetNum);
        if (StringUtils.isBlank(totalMatchBetNum)) {
            totalMatchBetNum = redisClient.hGet(matchTotalBetNumKey, item.getMatchId().toString());
            log.info("::{}::赛事id:{},投注累计数量，旧缓存取值:{}", item.getOrderNo(), item.getMatchId(), newMatchTotalBetAmonut);
            if (StringUtils.isNotBlank(totalMatchBetNum)) {
                redisClient.set(newMatchTotalBetNumKey, totalMatchBetNum);
            }
        }
        matchTotalBetTimes = redisClient.incrBy(newMatchTotalBetNumKey, matchBetNum);
        log.info("::{}::赛事id:{},预测数据计算，投注累计数量after:{}", item.getOrderNo(), item.getMatchId(), matchTotalBetTimes);
        Expiry.redisKeyExpiry(redisClient, item.getMatchType(), newMatchTotalBetNumKey);

        RcsMatchDimensionStatistics bean = new RcsMatchDimensionStatistics();
        bean.setMatchId(item.getMatchId());
        bean.setTotalOrderNums(matchTotalBetTimes);
        bean.setTotalValue(BigDecimal.valueOf(matchTotalBetAmonut));
        bean.setModifyTime(System.currentTimeMillis());
        bean.setCreateTime(System.currentTimeMillis());


        //已结算总货量 CalSettleServiceImpl计算
        String volumeBetAmountKey = PredictRedisKeyUtil.MATCH_TOTAL_SETTLED_VOLUME_KEY + item.getMatchId();
        String sumSettleBetAmountObj = redisClient.get(volumeBetAmountKey);
        log.info("::{}::赛事id:{},预测数据计算，已结算总货量:{}", item.getOrderNo(), item.getMatchId(), sumSettleBetAmountObj);
        //兼容老数据
        if (StringUtils.isBlank(sumSettleBetAmountObj)) {
            String getMatchSettledTotalBetAmonutKey = PredictRedisKeyUtil.getMatchSettledTotalBetAmonutKey();
            sumSettleBetAmountObj = redisClient.hGet(getMatchSettledTotalBetAmonutKey, item.getMatchId().toString());
            log.info("::{}::赛事id:{},预测数据计算，已结算总货量old:{}", item.getOrderNo(), item.getMatchId(), sumSettleBetAmountObj);
        }
        Expiry.redisKeyExpiry(redisClient, item.getMatchType(), volumeBetAmountKey);

        Long sumSettleBetAmount = 0L;
        if (StringUtils.isNotBlank(sumSettleBetAmountObj)) {
            sumSettleBetAmount = Long.valueOf(sumSettleBetAmountObj);
        }
        bean.setSettledRealTimeValue(new BigDecimal(sumSettleBetAmount));


        //已结算盈利新缓存key
        String newMatchsettledProfitKey = PredictRedisKeyUtil.MATCH_SETTLED_PROFIT_KEY + item.getMatchId();
        String settledProfitObj = redisClient.get(newMatchsettledProfitKey);
        log.info("::{}::赛事id:{},预测数据计算，已结算盈利:{}", item.getOrderNo(), item.getMatchId(), settledProfitObj);
        //兼容老数据
        if (StringUtils.isBlank(settledProfitObj)) {
            //已结算盈利老缓存key
            String matchsettledProfitKey = PredictRedisKeyUtil.getMatchsettledProfitKey();
            settledProfitObj = redisClient.hGet(matchsettledProfitKey, item.getMatchId().toString());
            log.info("::{}::赛事id:{},预测数据计算，已结算盈利old:{}", item.getOrderNo(), item.getMatchId(), settledProfitObj);
        }
        Long settledProfit = 0L;
        if (StringUtils.isNotBlank(settledProfitObj)) {
            settledProfit = Long.valueOf(settledProfitObj);
        }
        bean.setSettledProfitValue(new BigDecimal(settledProfit));

        log.info("::{}::预测数据计算-赛事级别 {}计算完成 : {}", item.getOrderNo(), item.getBetNo(), JSONObject.toJSON(bean));

        //异步存库
        HashMap<String, String> mqMap = new HashMap<>();
        mqMap.put("time", "" + System.currentTimeMillis());
        String hashKey = bean.getMatchId().toString();
        producerSendMessageUtils.sendMsg(RcsConstant.RCS_MATCH_DIMENSION_STATISTICS, "", "", JSONObject.toJSONString(bean), mqMap, hashKey);

        log.info("::{}::预测数据计算-赛事级别WS推送完成 :{}", item.getOrderNo(), item.getBetNo());
    }

    /**
     * 获取另一个投注项的赔付 用于公式计算
     * 按公式算吧  别多想
     * 计算公式如下
     * 选项	赔率	投注量	    期望
     * 1x	1.37	100	25.75	总投注量-1x的赔付值-12的的赔付值
     * 12	1.25	65	34.77	总投注量-12的赔付值-2x的的赔付值
     * 2x	1.62	79	-20.98	总投注量-2x的赔付值-1x的的赔付值
     *
     * @param list
     * @return
     */
    private Long getDoubeChangceOtherPaidAmount(StandardSportMarketOdds currentOdd, List<StandardSportMarketOdds> list, OrderItem item, Integer seriesType) {
        Map<String, Long> map = new HashMap<>();
        for (StandardSportMarketOdds bean : list) {
            map.put(bean.getOddsType(), bean.getId());
        }
        Long otherOddsId = 0L;
        if ("1X".equals(currentOdd.getOddsType())) {
            otherOddsId = map.get("12");
        } else if ("12".equals(currentOdd.getOddsType())) {
            otherOddsId = map.get("X2");
        } else if ("X2".equals(currentOdd.getOddsType())) {
            otherOddsId = map.get("1X");
        }
        String oddsTotalPaidAmonutKey = PredictRedisKeyUtil.getOddsTotalPaidAmonutKey(item, seriesType.toString());
        Long oddsTotalPaidAmonut = LongUtil.parseLong(redisClient.hGet(oddsTotalPaidAmonutKey, otherOddsId.toString()));
        return oddsTotalPaidAmonut;
    }


    /**
     * 获取盘口下所有投注项
     *
     * @param marketId
     * @return
     */
    private List<StandardSportMarketOdds> getStandardSportMarketOdds(Long matchId, Long marketId, Integer matchType) {
        QueryWrapper<StandardSportMarketOdds> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("market_id", marketId);
        if (matchType == 3) {
            return standardSportMarketOddsMapper.selectList(queryWrapper);
        }
        String keyName = String.format(RedisKeys.PROFIT_PLAYOPTION_ID, matchId, marketId);
        String json = redisClient.get(keyName);
        if (StringUtils.isEmpty(json)) {
            List<StandardSportMarketOdds> list = standardSportMarketOddsMapper.selectList(queryWrapper);
            if (list != null && list.size() > 0) {
                //缓存时间24小时
                redisClient.setExpiry(keyName, JSONObject.toJSONString(list), 60 * 60 * 24L);
            }
            return list;
        } else {
            return JSONObject.parseObject(json, new TypeReference<List<StandardSportMarketOdds>>() {
            });
        }
    }

    /**
     * 货量计算
     *
     * @param type 1标识下单 增加计算  -1表示取消订单
     * @param item
     */
    @Override
    public void calculateBetStatis(OrderItem item, Integer type, boolean nx) {

        log.info("::{}::预测数据计算-最小维度货量开始{}", item.getOrderNo(), item.getBetNo());
        //只计算让球和大小玩法
        //2021-9-22 网球需求新增checkTennisPlay网球系列玩法货量计算
        if (!checkFootBallPlay(item.getPlayId()) && !checkBasketBallPlay(item.getPlayId()) && !checkTennisPlay(item.getPlayId())
                && !checkPingpongPlay(item.getPlayId()) && !checkVolleyPlay(item.getPlayId()) && !checkSnookerPlay(item.getPlayId())
                && !checkBaseballPlay(item.getPlayId()) && !checkBadmintonPlay(item.getPlayId()) && !checkIcehockeyPlay(item.getPlayId())) {
            log.info("::{}::预测数据计算-最小维度货量开始{},玩法跳过", item.getOrderNo(), item.getBetNo());
            return;
        }
        String betStatisKey;
        //记录到缓存
        if (!SportIdEnum.isFootball(item.getSportId())) {
            betStatisKey = "rcs:risk:predict:betSatis.match_id.%s.match_type.%s.play_id.%s.sub_play_id.%s.market_id.%s.odds_item.%s.0:0";
            betStatisKey = String.format(betStatisKey, item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getSubPlayId(), item.getMarketId(), item.getPlayOptions());
        } else {
            betStatisKey = "rcs:risk:predict:betSatis.match_id.%s.match_type.%s.play_id.%s.sub_play_id.%s.market_id.%s.odds_item.%s.bet_score.%s";
            betStatisKey = String.format(betStatisKey, item.getMatchId(), item.getMatchType(), item.getPlayId(), item.getSubPlayId(), item.getMarketId(), item.getPlayOptions(), StringUtils.isEmpty(item.getScoreBenchmark()) ? "0:0" : item.getScoreBenchmark());
        }
        // 早盘检测redis
        if (item.getMatchType() == 1) {
            predictResetRedisKeyBo.resetStatisRedisKey(betStatisKey);
        }
        //获取用户标签 货量比例
        BigDecimal percentage = BigDecimal.valueOf(1);// predictCommonService.getUserTagPercentage(item.getUid());

        BigDecimal allAmount = new BigDecimal(item.getBetAmount() * type).multiply(percentage);
        //累加 总货量
        Long betAmount = redisClient.hincrBy(betStatisKey, "totalBetAmount", allAmount.longValue());
        Expiry.redisKeyExpiryStatis(redisClient, item.getMatchType(), betStatisKey);
        //纯赔付货量
        BigDecimal oddValue = BigDecimal.valueOf(item.getHandleAfterOddsValue()).subtract(new BigDecimal("1"));
        BigDecimal betAmountPay = new BigDecimal(item.getBetAmount()).multiply(oddValue).multiply(new BigDecimal(type)).multiply(percentage);
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
        Long betNum = redisClient.hincrBy(betStatisKey, "totalBetNum", 1 * type);
        //累加 赔率和
        Double oddsSum = redisClient.hincrByFloat(betStatisKey, "oddsSum", item.getOddsValue() * type);

        //记录到数据库
        RcsPredictBetStatis bean = new RcsPredictBetStatis();
        bean.setSportId(item.getSportId());
        bean.setMatchId(item.getMatchId());
        bean.setMatchType(item.getMatchType());
        bean.setPlayId(item.getPlayId());
        bean.setSubPlayId(item.getSubPlayId());
        bean.setMarketId(item.getMarketId());
        bean.setOddsItem(item.getPlayOptions());
        if (item.getMatchType() == 2 && SportIdEnum.isFootball(item.getSportId())) {
            bean.setBetScore(StringUtils.isEmpty(item.getScoreBenchmark()) ? "0:0" : item.getScoreBenchmark());
        } else {
            bean.setBetScore("0:0");
        }
        bean.setBetAmount(BigDecimal.valueOf(betAmount).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_DOWN));
        bean.setBetAmountPay(BigDecimal.valueOf(betAmountPayValue).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_DOWN));
        bean.setBetAmountComplex(BigDecimal.valueOf(betAmountComplexValue).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_DOWN));
        bean.setBetNum(betNum);
        bean.setOddsSum(new BigDecimal(oddsSum).divide(new BigDecimal("100000"), 2, BigDecimal.ROUND_DOWN));
        bean.setCreateTime(System.currentTimeMillis());
        bean.setMarketValueComplete(item.getMarketValueNew());
        bean.setMarketValueCurrent(item.getMarketValue());
        bean.setPlayOptions(item.getPlayOptionsId().toString());

        HashMap<String, String> mqMap = new HashMap<>();
        mqMap.put("time", "" + System.currentTimeMillis());
        String lastMarketIdDataKey = "rcs:risk:predict:betSatis:lastMarket.match_id.%s.match_type.%s.play_id.%s";
        lastMarketIdDataKey = String.format(lastMarketIdDataKey, bean.getMatchId(), bean.getMatchType(), bean.getPlayId());
        String lastMarketIdData = redisClient.get(lastMarketIdDataKey);
        if (!StringUtils.isEmpty(lastMarketIdData)) {
            RcsPredictBetStatis rcsPredictBetStatis = JSONObject.parseObject(lastMarketIdData, RcsPredictBetStatis.class);
            if (!Objects.equals(rcsPredictBetStatis.getMarketId(), bean.getMarketId())) {
                producerSendMessageUtils.sendMsg("mq_data_rcs_predict_bet_statis", "", "", lastMarketIdData, mqMap, betStatisKey);
                redisClient.delete(lastMarketIdDataKey);
            }
        }
        //异步存库
        String hashKey = betStatisKey;
        if (nx) {
            producerSendMessageUtils.sendMsg("mq_data_rcs_predict_bet_statis", "", "", JSONObject.toJSONString(bean), mqMap, hashKey);
            log.info("::{}::rcs_predict_bet_statis表mq入库完成: key:{}", item.getOrderNo(), hashKey);
        } else {
            redisClient.set(lastMarketIdDataKey, JSONObject.toJSONString(bean));
            Expiry.redisKeyExpiryStatis(redisClient, bean.getMatchType(), lastMarketIdDataKey);
            log.info("::{}::rcs_predict_bet_statis表,频率限制 跳过入库！", item.getOrderNo());
        }
        log.info("::{}::预测数据计算-最小维度货量完成{}", item.getOrderNo(), item.getBetNo());
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
     *
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
     * 检查羽毛球货量
     * 172 全场让分
     * 173 全场总分
     * 176 第X盘让分
     * 177 第X盘总分
     *
     * @param plyaId
     * @return
     */
    private boolean checkBadmintonPlay(Integer plyaId) {
        String badmintonPlayIds = "172,173,176,177";
        String arr[] = badmintonPlayIds.split(",");
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
     * 4全场让球
     * 2全场总分
     * 295全场大小（含加时）
     * 268单节让球
     * 262单节总分
     * 263单节主队总分
     * 264单节客队总分
     *
     * @param plyaId
     * @return
     */
    private boolean checkIcehockeyPlay(Integer plyaId) {
        String basketBallPlayIds = "2,4,295,268,262,263,264";
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