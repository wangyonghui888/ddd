package com.panda.sport.rcs.mgr.operation.order.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mgr.aspect.RcsLockSeriesTypeEnum;
import com.panda.sport.rcs.mgr.aspect.RcsLockable;
import com.panda.sport.rcs.mgr.operation.order.CalcOrder;
import com.panda.sport.rcs.mgr.operation.utils.LongUtil;
import com.panda.sport.rcs.mgr.paid.annotion.Order;
import com.panda.sport.rcs.mgr.wrapper.ITOrderDetailService;
import com.panda.sport.rcs.mgr.wrapper.MarketViewService;
import com.panda.sport.rcs.mgr.wrapper.RcsMarketOddsConfigService;
import com.panda.sport.rcs.mgr.wrapper.StandardMatchInfoService;
import com.panda.sport.rcs.mgr.wrapper.StandardSportMarketOddsService;
import com.panda.sport.rcs.pojo.RcsMarketOddsConfig;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import com.panda.sport.rcs.vo.OrderDetailStatReportVo;
import com.panda.sport.rcs.vo.operation.RealTimeVolumeBean;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.rpc.operation.order.impl
 * @Description :  期望盈利值
 * @Date: 2019-10-26 15:09
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 * toney     2019-11-5     之前是二个类统计，发现更新mongodb有问题，重新梳理类库
 */
@Component
@Slf4j
@Order(100)
public class OrderHandleServiceImpl extends CalcOrderBase implements CalcOrder {
    @Autowired
    private RedisClient redisClient;

    @Autowired
    private MarketViewService marketViewService;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Autowired
    private ITOrderDetailService orderDetailService;


    @Autowired
    private StandardSportMarketOddsService standardSportMarketOddsService;

    @Autowired
    private RcsMarketOddsConfigService rcsMarketOddsConfigService;

    /**
     * @return void
     * @Param [orderBean]
     * @Author toney

     **/
    @Override
    @RcsLockable(key = "order_handle",seriesType = RcsLockSeriesTypeEnum.Single)
    public void orderHandle(OrderBean orderBean, Integer type) {
        //只取单条数据,不要串关的
        if (orderBean.getSeriesType() != 1) {
            log.warn("::{}::调用期望盈利值：串关数据不处理", orderBean.getOrderNo());
            return;
        }
        //拒单
        if (orderBean.getOrderStatus() != 1) {
            log.warn("::{}::期望值接收拒单mq，不再计算：",orderBean.getOrderNo());
            return;
        }
        log.info("::{}::调用期望盈利值,实体bean{}", orderBean.getOrderNo(), JsonFormatUtils.toJson(orderBean));
        for (OrderItem item : orderBean.getItems()) {
            useCache(item);
        }
    }

    /**
     * 初始化     
     * @param item
     */
    private void initData(OrderItem item) {
        try {
            List<OrderDetailStatReportVo> list = orderDetailService.getStatReportByPlayOptions(item.getMarketId(), item.getPlayOptionsId(), item.getOrderNo(), item.getMatchType().toString());

            log.info("::{}::调用期望盈利值,实体bean{}",item.getOrderNo(),JsonFormatUtils.toJson(list));

            redisClient.hSet(getMarketIdTotalBetAmonutCacheKey(item.getMatchId(), item.getMatchType()), item.getMarketId().toString(), "0");
            for (OrderDetailStatReportVo vo : list) {
                RealTimeVolumeBean bean = new RealTimeVolumeBean();
                bean.setMatchId(item.getMatchId());
                bean.setPlayId(item.getPlayId());
                bean.setMatchMarketId(item.getMarketId());
                bean.setPlayOptionsId(vo.getPlayOptionsId());

                bean.setProfitValue(BigDecimal.valueOf(vo.getProfitValue()));
                bean.setBetOrderNum(BigDecimal.valueOf(vo.getBetOrderNum()));
                bean.setSumMoney(BigDecimal.valueOf(vo.getBetAmount()));


                redisClient.hSet(getOddsIdTotalBetAmountCacheKey(item.getMatchId(), item.getMatchType(), item.getMarketId()), vo.getPlayOptionsId().toString(), bean.getSumMoney().toString());
                redisClient.hSet(getOddsIdTotalBetTimesCacheKey(item.getMatchId(), item.getMatchType(), item.getMarketId()), vo.getPlayOptionsId().toString(), bean.getBetOrderNum().toString());
                redisClient.hSet(getOddsIdTotalPaidAmountCacheKey(item.getMatchId(), item.getMatchType(), item.getMarketId()), vo.getPlayOptionsId().toString(), bean.getPaidAmount().toString());


                //投注项级别的期望值=盘口下所有下注项金额汇总-当前投注项级别最大赔付金额
                redisClient.hSet(getOddsIdProfitValueCacheKey(item.getMatchId(), item.getMatchType(), item.getMarketId()), vo.getPlayOptionsId().toString(), bean.getProfitValue().toString());
                redisClient.hincrBy(getMarketIdTotalBetAmonutCacheKey(item.getMatchId(), item.getMatchType()), item.getMarketId().toString(), bean.getSumMoney().longValue());

                log.info("::{}::调用期望盈利值,实体bean{}",item.getOrderNo(),JsonFormatUtils.toJson(bean));
                syscData(bean);
            }
        }catch (Exception ex) {
            log.info("::{}::调用期望盈利值,错误异常{}、{}",item.getOrderNo(),ex.getMessage(),ex);
        }
    }


    /**
     * 同步数据
     *
     * @param bean
     */
    private void syscData(RealTimeVolumeBean bean) {
        //查表 优化
//    	if (matchInfo != null && matchInfo.getTournamentLevel() != null) {
//            bean.setStandardTournamentId(matchInfo.getStandardTournamentId().longValue());
//        }
        try {
            //旧的 暂时 注释掉
            //marketViewService.updateMatchOdds(bean);
        }catch (Exception ex) {
            log.info("调用期望盈利值,错误异常2", ex.getMessage(), ex);
            throw new RpcException("调用期望盈利值,错误异常2", ex);
        }
    }

    /**
     * 获取期望值
     * 投注项级别Id
     * 缓存计算
     *
     * @param item
     */
    private CalcData calc(OrderItem item) {
        CalcData calcData = new CalcData();
        BigDecimal paidAmount = new BigDecimal(item.getBetAmount()) .multiply(new BigDecimal(item.getOddFinally()));
        BigDecimal profitAmount = new BigDecimal(item.getBetAmount()).subtract(paidAmount);

        calcData.oddsIdTotalBetAmount = redisClient.hincrBy(getOddsIdTotalBetAmountCacheKey(item.getMatchId(), item.getMatchType(),
                item.getMarketId()), item.getPlayOptionsId().toString(), item.getBetAmount());

        calcData.oddsIdTotalBetTimes = redisClient.hincrBy(getOddsIdTotalBetTimesCacheKey(item.getMatchId(), item.getMatchType(),
                item.getMarketId()), item.getPlayOptionsId().toString(), 1);

        calcData.marketIdTotalBetAmonut = redisClient.hincrBy(getMarketIdTotalBetAmonutCacheKey(item.getMatchId(),
                item.getMatchType()), item.getMarketId().toString(), item.getBetAmount());

        calcData.oddsIdTotalPaidAmount = redisClient.hincrBy(getOddsIdTotalPaidAmountCacheKey(item.getMatchId(),
                item.getMatchType(), item.getMarketId()), item.getPlayOptionsId().toString(), paidAmount.longValue());

        //投注项级别的期望值=盘口下所有下注项金额汇总-当前投注项级别最大赔付金额
        calcData.oddsIdProfitValue = redisClient.hincrBy(getOddsIdProfitValueCacheKey(item.getMatchId(), item.getMatchType(),
                item.getMarketId()), item.getPlayOptionsId().toString(), profitAmount.longValue());

        return calcData;
    }

    @Data
    private class CalcData {
        private Long oddsIdTotalBetAmount;
        private Long oddsIdTotalBetTimes;
        private Long marketIdTotalBetAmonut;
        private Long oddsIdTotalPaidAmount;
        private Long oddsIdProfitValue;
    }


    /**
     * 初始化数据
     * @param item
     */
    private void init(OrderItem item) {
        String redisValue = redisClient.hGet(getMarketIdTotalBetAmonutCacheKey(item.getMatchId(), item.getMatchType()), item.getMarketId().toString());
        BigDecimal marketValue = BigDecimal.ZERO;
        BigDecimal paidAmount = BigDecimal.ZERO;
        if (redisValue == null) {
            QueryWrapper<RcsMarketOddsConfig> queryWrapper = new QueryWrapper();
            if (item.getMatchId() != null) {
                queryWrapper.lambda().eq(RcsMarketOddsConfig::getMatchId, item.getMatchId());
            }
            if (item.getPlayId() != null) {
                queryWrapper.lambda().eq(RcsMarketOddsConfig::getMarketCategoryId, item.getPlayId());
            }
            if (item.getMarketId() != null) {
                queryWrapper.lambda().eq(RcsMarketOddsConfig::getMatchMarketId, item.getMarketId());
            }
            if (item.getTournamentId() != null) {
                queryWrapper.lambda().eq(RcsMarketOddsConfig::getStandardTournamentId, item.getTournamentId());
            }
            if (item.getMatchType() != null) {
                queryWrapper.lambda().eq(RcsMarketOddsConfig::getMatchType, item.getMatchType());
            }


            List<RcsMarketOddsConfig> list = rcsMarketOddsConfigService.list(queryWrapper);
            if (list.size() > 0) {
                for (RcsMarketOddsConfig bean : list) {
                    if (item.getPlayOptionsId().equals(bean.getMarketOddsId())) {
                        redisClient.hSet(getOddsIdTotalBetAmountCacheKey(item.getMatchId(), item.getMatchType(), item.getMarketId()), bean.getMarketOddsId().toString(),
                                bean.getBetAmount().multiply(BigDecimal.valueOf(100)).toString());

                        redisClient.hSet(getOddsIdTotalBetTimesCacheKey(item.getMatchId(), item.getMatchType(), item.getMarketId()), bean.getMarketOddsId().toString(),
                                bean.getBetOrderNum().toString());
                        //最大赔付金额
                        //期望值=盘口下总投注额-最大同赔付金额
                        redisClient.hSet(getOddsIdTotalPaidAmountCacheKey(item.getMatchId(), item.getMatchType(), item.getMarketId()), bean.getMarketOddsId().toString(),
                                String.valueOf(bean.getPaidAmount().multiply(BigDecimal.valueOf(100)).longValue()));

                        redisClient.hSet(getOddsIdProfitValueCacheKey(item.getMatchId(), item.getMatchType(), item.getMarketId()), bean.getMarketOddsId().toString(),
                                String.valueOf(bean.getProfitValue().multiply(BigDecimal.valueOf(100))));



                        paidAmount = bean.getBetAmount().subtract(bean.getProfitValue()).multiply(BigDecimal.valueOf(100));


                        redisClient.hSet(getMarketIdTotalBetAmonutCacheKey(item.getMatchId(), item.getMatchType()), item.getMarketId().toString(),
                                String.valueOf(paidAmount.longValue()));
                    }

                    marketValue.add(bean.getBetAmount());
                }

            } else if (list.size() == 0) {
                initData(item);
            }
        }
    }

    /**
     * 使用缓存
     *
     * @param item
     */
    private void useCache(OrderItem item) {
        /**
         * 初始化数据
         */
        //init(item);


        CalcData calcData = calc(item);


        log.info("::{}::调用期望盈利值,====>开始",item.getOrderNo());


        List<StandardSportMarketOdds> list=getStandardSportMarketOdds(item.getMatchId(),item.getMarketId());
        for (StandardSportMarketOdds odds : list) {
            //如果不是这个投注项ID
            //期望值+投注金额
            if (odds.getId().compareTo(item.getPlayOptionsId().longValue()) != 0) {
                redisClient.hincrBy(getOddsIdProfitValueCacheKey(item.getMatchId(), item.getMatchType(), item.getMarketId()), odds.getId().toString(), item.getBetAmount());
            }

            RealTimeVolumeBean bean = new RealTimeVolumeBean();
            bean.setSportId(item.getSportId());
            bean.setMatchId(item.getMatchId());
            bean.setPlayId(item.getPlayId());
            bean.setMatchMarketId(item.getMarketId());
            bean.setPlayOptionsId(odds.getId());
            bean.setMatchType(item.getMatchType().toString());

            Long betOrderNum = LongUtil.parseLong(redisClient.hGet(getOddsIdTotalBetTimesCacheKey(item.getMatchId(), item.getMatchType(), item.getMarketId()), odds.getId().toString()));
            Long profitValue = LongUtil.parseLong(redisClient.hGet(getOddsIdProfitValueCacheKey(item.getMatchId(), item.getMatchType(), item.getMarketId()), odds.getId().toString()));
            Long totalBetAmount = LongUtil.parseLong(redisClient.hGet(getOddsIdTotalBetAmountCacheKey(item.getMatchId(), item.getMatchType(), item.getMarketId()), odds.getId().toString()));
            Long paidAmount = LongUtil.parseLong(redisClient.hGet(getOddsIdTotalPaidAmountCacheKey(item.getMatchId(), item.getMatchType(), item.getMarketId()), odds.getId().toString()));

            bean.setBetOrderNum(new BigDecimal(betOrderNum));
            bean.setSumMoney(new BigDecimal(totalBetAmount).divide(BigDecimal.valueOf(100)));
            bean.setProfitValue(new BigDecimal(profitValue).divide(BigDecimal.valueOf(100)));
            bean.setPaidAmount(new BigDecimal(paidAmount).divide(BigDecimal.valueOf(100)));
            bean.setStandardTournamentId(item.getTournamentId());

            if (item.getPlayId() == 6 || item.getPlayId() == 70 || item.getPlayId() == 72) {
                profitValue=calcData.getMarketIdTotalBetAmonut() - paidAmount - getDoubeChangceOtherPaidAmount(item,odds,list);
                bean.setProfitValue(new BigDecimal(profitValue/100));
                redisClient.hSet(getOddsIdProfitValueCacheKey(item.getMatchId(), item.getMatchType(), item.getMarketId()), odds.getId().toString(),String.valueOf(profitValue));
            }


            log.info("::{}::调用期望盈利值,投注项{},实体bean：{}",item.getOrderNo(),odds.getOddsType(), JsonFormatUtils.toJson(bean));
            syscData(bean);
        }
        log.info("::{}::调用期望盈利值,处理订单====>结束",item.getOrderNo());
    }

    /**
     * 获取盘口下值
     * @param marketId
     * @return
     */
    private List<StandardSportMarketOdds> getStandardSportMarketOdds(Long matchId,Long marketId){
        String keyName=String.format(RedisKeys.PROFIT_PLAYOPTION_ID,matchId,marketId );
        String json =redisClient.get(keyName);
        if(StringUtils.isEmpty(json)) {
            QueryWrapper<StandardSportMarketOdds> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("market_id", marketId);
            List<StandardSportMarketOdds> list = standardSportMarketOddsService.list(queryWrapper);
            if(list != null && list.size() >0) {
                //缓存时间2小时
                redisClient.setExpiry(keyName, JSONObject.toJSONString(list), 60 * 60 * 2L);
            }
            return list;
        }else
        {
            return JSONObject.parseObject(json,new TypeReference<List<StandardSportMarketOdds>>(){});
        }
    }

    /**
     * 获取双重机会最大赔偿值
     * @param item
     * @param list
     * @return
     */
    private Long getDoubeChangceOtherPaidAmount(OrderItem item,StandardSportMarketOdds odd, List<StandardSportMarketOdds> list) {
        for (StandardSportMarketOdds odds : list) {
            if (("12".equals(odds.getOddsType())&&"1X".equals(odd.getOddsType()))
                    ||    ("X2".equals(odds.getOddsType())&&"12".equals(odd.getOddsType()))
                    ||    ("1X".equals(odds.getOddsType())&&"X2".equals(odd.getOddsType()))
            ) {
                Long paidBetAmount =LongUtil.parseLong(redisClient.hGet(getOddsIdTotalPaidAmountCacheKey(item.getMatchId(), item.getMatchType(), item.getMarketId()), odds.getId().toString()));
                return paidBetAmount;
            }
        }
        return 0L;
    }


    /**
     * 从数据库获取初始数据
     * @param item
     */
    private void initDb(OrderItem item){
        List<OrderDetailStatReportVo> list= orderDetailService.getStatReportByPlayOptions(item.getMarketId(),item.getPlayOptionsId(),item.getOrderNo(),item.getMatchType().toString());

        log.info("调用期望盈利值,实体bean{}", JsonFormatUtils.toJson(list));
        for(OrderDetailStatReportVo vo:list) {
            if (vo.getMarketId() != null) {
                redisClient.hincrBy(getOddsIdTotalBetAmountCacheKey(item.getMatchId(),item.getMatchType(),item.getMarketId()), vo.getPlayOptionsId().toString(), item.getBetAmount() == null ? 0L : item.getBetAmount());
                redisClient.hincrBy(getOddsIdTotalBetTimesCacheKey(item.getMatchId(),item.getMatchType(),item.getMarketId()), vo.getPlayOptionsId().toString(), 1);
                redisClient.hincrBy(getMarketIdTotalBetAmonutCacheKey(item.getMatchId(),item.getMatchType()), vo.getMarketId().toString(), vo.getBetAmount() == null ? 0L : vo.getBetAmount());
                redisClient.hincrBy(getOddsIdTotalPaidAmountCacheKey(item.getMatchId(),item.getMatchType(),item.getMarketId()), item.getPlayOptionsId().toString(), vo.getPaidAmount() == null ? 0L : vo.getPaidAmount());
                redisClient.hincrBy(getOddsIdProfitValueCacheKey(item.getMatchId(),item.getMatchType(),item.getMarketId()), vo.getPlayOptionsId().toString(), vo.getProfitValue() == null ? 0L : vo.getProfitValue());
            }
        }
    }

    /**
     * 获取盘口下投注项下期望值
     * @param marketId 盘口
     * @return
     */
    private String getOddsIdProfitValueCacheKey(Long matchId,Integer matchType,Long marketId){
        return String.format( RedisKeys.ODDS_ID_PROFIT_VALUE,matchId,matchType,marketId);
    }

    /**
     * @Description   获取投注次数缓存
     * @Param [matchType, marketId]
     * @Author  toney
     * @Date  14:59 2020/1/27
     * @return java.lang.String
     **/
    private String getOddsIdTotalBetTimesCacheKey(Long matchId,Integer matchType,Long marketId){
        return String.format(RedisKeys.ODDS_ID_TOTAL_BET_TIMES,matchId,matchType,marketId);
    }

    /**
     * 获取盘口下投注项下最大赔付额
     * @param marketId
     * @return
     */
    private String getOddsIdTotalPaidAmountCacheKey(Long matchId,Integer matchType,Long marketId){
        return String.format(RedisKeys.ODDS_ID_TOTAL_PAID_AMOUNT,matchId,matchType,marketId);

    }

    /**
     * 获取盘口下投注项下总货量
     * @return
     */
    private String getOddsIdTotalBetAmountCacheKey(Long matchId,Integer matchType,Long marketId){
        return String.format(RedisKeys.ODDS_ID_TOTAL_BET_AMOUNT,matchId,matchType,marketId);
    }

    /**
     * 获取盘口下总货量
     * @return
     */
    private String getMarketIdTotalBetAmonutCacheKey(Long matchId,Integer matchType){
        return String.format(RedisKeys.MARKET_ID_TOTAL_BET_AMONUT,matchId,matchType);
    }

    public static void main(String[] args) {
        BigDecimal divide = BigDecimal.valueOf(690L).divide(BigDecimal.valueOf(100));
        System.out.printf(divide.toString());
    }
}
