package com.panda.sport.rcs.mgr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderDetailsDTO;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.SettleItem;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.exeception.LogicException;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mgr.paid.matrix.bean.CategoryList;
import com.panda.sport.rcs.mgr.paid.matrix.bean.CategoryVo;
import com.panda.sport.rcs.mgr.paid.matrix.bean.MatrixConstant;
import com.panda.sport.rcs.mgr.paid.matrix.bean.MatrixForecastVo;
import com.panda.sport.rcs.mgr.wrapper.RcsBusinessPlayPaidConfigService;
import com.panda.sport.rcs.mgr.wrapper.StandardMatchInfoService;
import com.panda.sport.rcs.mgr.wrapper.StandardSportMarketOddsService;
import com.panda.sport.rcs.mgr.wrapper.StandardSportMarketService;
import com.panda.sport.rcs.mgr.wrapper.StandardSportTournamentService;
import com.panda.sport.rcs.mgr.wrapper.impl.RcsPaidConfigServiceImp;
import com.panda.sport.rcs.mgr.wrapper.impl.RcsRectanglePlayServiceImpl;
import com.panda.sport.rcs.mgr.wrapper.impl.RcsStandardSportMarketSellServiceImpl;
import com.panda.sport.rcs.pojo.RcsBusinessPlayPaidConfig;
import com.panda.sport.rcs.pojo.RcsStandardSportMarketSell;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import com.panda.sport.rcs.pojo.StandardSportTournament;
import com.panda.sport.rcs.vo.RcsBusinessPlayPaidConfigVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.mgr.service.impl
 * @Description :  订单参数效验
 * @Date: 2019-12-10 21:13
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
public class ParamValidate {

    @Autowired
    RcsPaidConfigServiceImp rcsPaidConfigService;
    @Autowired
    RcsRectanglePlayServiceImpl playService;
    @Autowired
    RcsBusinessPlayPaidConfigService rcsBusinessPlayPaidConfigService;
    @Autowired
    private CategoryList categoryList;
    @Autowired
    public RcsPaidConfigServiceImp configService;
    @Autowired
    StandardSportMarketService standardSportMarketService;
    @Autowired
    StandardSportMarketOddsService standardSportMarketOddsService;
    @Autowired
    StandardMatchInfoService standardMatchInfoService;
    @Autowired
    StandardSportTournamentService standardSportTournamentService;
    @Autowired
    RcsStandardSportMarketSellServiceImpl rcsStandardSportMarketSellServiceImpl;
    @Autowired
    private RedisClient redisClient;
    @Value("${paid.single.min}")
    private long singleMinAmount;
    @Value("${paid.multi.min}")
    private long multiMinAmount;
    private LRUMap lruMap = new LRUMap(20000);

    /**
     * @Description  查询未登录最大最小限额参数验证
     * @Param [requestParam]
     * @Author  max
     * @Date  13:47 2019/12/11
     * @return void
     **/
    public void checkInitMaxBetArguments(Request<OrderBean> requestParam){
        Preconditions.checkNotNull(requestParam);
        Preconditions.checkNotNull(requestParam.getData());
        if(requestParam.getData().getTenantId() == null)
        {
            throw new LogicException("702","商户ID不能为空！");
        }
        if(requestParam.getData().getSeriesType() == null) {
            throw new LogicException("704", "串关类型不能为空！");
        }
        if(requestParam.getData().getItems().get(0).getPlayId() == null){
            throw new LogicException("605", "玩法ID不能为空！");
        }
        if(requestParam.getData().getItems().get(0).getMatchType() == null){
            throw new LogicException("607", "盘口MatchType不能为空！");
        }
        if(requestParam.getData().getItems().get(0).getOddsValue() == null || requestParam.getData().getItems().get(0).getOddsValue() == 0){
            throw new LogicException("610", "赔率OddsValue不能为空！");
        }
        if(Strings.isNullOrEmpty(requestParam.getData().getItems().get(0).getOddFinally())){
            throw new LogicException("610", "赔率OddFinally不能为空！");
        }
    }

    /**
     * @Description   查询前选项最大可投注金额验证
     * @Param [requestParam]
     * @Author  max
     * @Date  15:02 2019/12/11
     * @return void
     **/
    public void checkMaxBetArguments(Request<OrderBean> requestParam){
        Preconditions.checkNotNull(requestParam);
        Preconditions.checkNotNull(requestParam.getData());
        Preconditions.checkNotNull(requestParam.getData().getItems());
        if(requestParam.getData().getSeriesType() == 1 && requestParam.getData().getItems().size() > 1){
            throw new RcsServiceException(700110001,"单关协议格式异常，不支持多个投注项");
        }

        if(requestParam.getData().getTenantId() == null) {
            throw new RcsServiceException("商户ID不能为空！");
        }
        if(requestParam.getData().getUid() == null) {
            throw new RcsServiceException( "用户ID不能为空！");
        }
        if(requestParam.getData().getItems().get(0).getUid() == null) {
            throw new LogicException("603", "用户ID不能为空！");
        }
        if(requestParam.getData().getItems().get(0).getMarketId() == null || requestParam.getData().getItems().get(0).getMarketId() == 0){
            throw new LogicException("609", "盘口id不能为空！");
        }
        if(Strings.isNullOrEmpty(requestParam.getData().getItems().get(0).getOddFinally())){
            throw new LogicException("610", "赔率OddFinally不能为空！");
        }

        checkBaseNotNullArguments(requestParam.getData().getItems().get(0));
    }

    /**
     * @Description  订单保存参数效验
     * @Param [requestParam]
     * @Author  max
     * @Date  21:21 2019/12/10
     * @return void
     **/
    public  OrderBean checkSaveArguments(Request<OrderBean> requestParam){
        OrderBean orderBean = BeanCopyUtils.copyProperties(requestParam.getData(), OrderBean.class);
        Preconditions.checkNotNull(requestParam);
        Preconditions.checkNotNull(requestParam.getData());
        Preconditions.checkNotNull(requestParam.getData().getItems());
        orderBean.setTraceId(requestParam.getGlobalId());
        if(requestParam.getData().getSeriesType() == 1 && requestParam.getData().getItems().size() > 1){
            throw new RcsServiceException(700110001,"单关协议格式异常，不支持多个投注项");
        }

        if(Strings.isNullOrEmpty(orderBean.getOrderNo()))
        {
            throw new RcsServiceException( "订单编号不能为空！");
        }
        if(orderBean.getTenantId() == null){
            throw new RcsServiceException("商户ID不能为空！");
        }
        if(orderBean.getUid() == null){
            throw new RcsServiceException( "用户ID不能为空！");
        }

        if(Strings.isNullOrEmpty(orderBean.getItems().get(0).getBetNo())){
            throw new LogicException("601", "注单编号不能为空！");
        }
        if(Strings.isNullOrEmpty(orderBean.getItems().get(0).getOrderNo())){
            throw new LogicException("602", "订单编号不能为空！");
        }
        if(orderBean.getItems().get(0).getBetAmount() == null || orderBean.getItems().get(0).getBetAmount() == 0){
            throw new LogicException("609", "BetAmount不能为空！");
        }
        if(orderBean.getItems().get(0).getMaxWinAmount() == null || orderBean.getItems().get(0).getMaxWinAmount() == 0){
            throw new LogicException("611", "最高可赢不能为空！");
        } 
        checkBaseNotNullArguments(requestParam.getData().getItems().get(0));

//        orderBean.setItems(new ArrayList<>());
//        orderBean.getItems().addAll(requestParam.getData().getItems());
//        ExtendBean bean = buildExtendBean(requestParam.getData(), requestParam.getData().getItems().get(0));
//        orderBean.setExtendBean(bean);

        return orderBean;
    }

    /**
     * @Description   验证orderItem
     * @Param [bean]
     * @Author  max
     * @Date  10:04 2019/12/11
     * @return void
     **/
    private  void checkBaseNotNullArguments(OrderItem bean){
        if(bean.getSportId() == null)
        {
            throw new LogicException("604", "运动类型ID不能为空！");
        }
        if(bean.getPlayId() == null){
            throw new LogicException("605", "玩法ID不能为空！");
        }
        if(bean.getMatchId() == null){
            throw new LogicException("606", "比赛ID不能为空！");
        }
        if(bean.getMatchType() == null){
            throw new LogicException("607", "盘口MatchType不能为空！");
        }
        if(bean.getMatchType() == 0){
            throw new LogicException("607", "盘口MatchType参数错误！");
        }
        if(bean.getOddsValue() == null || bean.getOddsValue() == 0){
            throw new LogicException("610", "赔率OddsValue不能为空！");
        }
        if(bean.getPlayOptionsId() == null || bean.getPlayOptionsId() == 0){
            throw new LogicException("613", "投注项ID不能为空！");
        }
        if(bean.getMatchProcessId() == null)
        {
            throw new LogicException("614", "注单所属赛事阶段ID不能为空！");
        }
    }

    /**
     * @Description   根据orderItem 获取扩展 orderBean
     * @Param [bean, item]
     * @Author  max
     * @Date  11:15 2019/12/11
     * @return com.panda.sport.data.rcs.dto.ExtendBean
     **/
    public ExtendBean buildExtendBean(OrderBean bean ,OrderItem item) {
        ExtendBean extend = new ExtendBean();
        extend.setSeriesType(bean.getSeriesType());
        extend.setItemId(item.getBetNo());
        extend.setOrderId(item.getOrderNo());
        extend.setBusId(String.valueOf(bean.getTenantId()));
        extend.setHandicap(item.getMarketValue());
        extend.setCurrentScore(item.getScoreBenchmark());
        //item  1 ：早盘 ，2： 滚球盘， 3： 冠军盘
        extend.setIsScroll(String.valueOf(item.getMatchType()).equals("2") ? "1" : "0");
        extend.setMatchId(String.valueOf(item.getMatchId()));
        extend.setPlayId(item.getPlayId()+"");
        extend.setSelectId(String.valueOf(item.getPlayOptionsId()));
        extend.setSportId(String.valueOf(item.getSportId()));
        extend.setUserId(String.valueOf(item.getUid()));
        extend.setOdds(String.valueOf(item.getHandleAfterOddsValue()));
        extend.setMarketId(item.getMarketId().toString());
        //阶段
        if (item.getMatchType() == 3) {
            extend.setPlayType("0");
        }else {
            extend.setPlayType(rcsPaidConfigService.getPlayProcess(String.valueOf(item.getSportId()), String.valueOf(item.getPlayId())));
        }
        if(item.getBetAmount() != null) {
            extend.setOrderMoney(item.getBetAmount());
            extend.setCurrentMaxPaid(new BigDecimal(item.getBetAmount()).multiply((new BigDecimal(extend.getOdds()).subtract(new BigDecimal(1)))).longValue());
        }else{
            extend.setOrderMoney(0L);
            extend.setCurrentMaxPaid(0L);
        }
        extend.setItemBean(item);

        if(StringUtils.isBlank(extend.getHandicap()))
        {
            extend.setHandicap("0");
        }
        if(StringUtils.isBlank(extend.getCurrentScore())){
            extend.setCurrentScore("0:0");
        }
        extend.setDateExpect(item.getDateExpect());
        return extend;
    }

    /**
     * @Description  设置矩阵Bean对象
     * @Param [bean, matrixForecastVo]
     * @Author  max
     * @Date  15:13 2019/12/11
     * @return void
     **/
    public void putBeanVal(ExtendBean bean, MatrixForecastVo matrixForecastVo) {
        CategoryVo vo = categoryList.queryCategoryVo(Integer.parseInt(Optional.ofNullable(bean.getPlayId()).orElse("0")));
        if(vo != null){
            matrixForecastVo.setCtype(MatrixConstant.MatrixCategoryType.values()[vo.getCtype()]);
            matrixForecastVo.setIsNeedBenchmark(vo.getBenchmark());
            bean.setRecType(matrixForecastVo.getCtype().ordinal());
            bean.setIsRelationScore(matrixForecastVo.getIsNeedBenchmark() == 1 );
            if(bean.getIsRelationScore()){
                bean.setHandicap(bean.getHandicap() + "-" + bean.getCurrentScore());
            }
        }
        if(bean.getHandicap() == null)
        {
            bean.setHandicap("0");
        }

        Map<String, Object> map = getMatchInfo(bean.getMatchId(),bean.getMarketId(),bean.getItemBean().getPlayOptionsId().toString());
        bean.setTournamentLevel(Integer.parseInt(String.valueOf(map.get("tournamentLevel"))));
        bean.setDateExpect(String.valueOf(map.get("dateExpect")));
        bean.setRecVal(JsonFormatUtils.toJson(matrixForecastVo.getMatrixArray()));
        bean.setTournamentId(Long.parseLong(String.valueOf(map.get("tournamentId"))));

        bean.setThirdMatchSourceId(String.valueOf(map.get("thirdMatchSourceId")));
        bean.setSpecifiers(String.valueOf(map.get("extra_info")));
        bean.setDataSourceCode(String.valueOf(map.get("data_source_code")));
        bean.setThirdTemplateSourceId(String.valueOf(map.get("third_template_source_id")));
    }

    /**
     * 将矩阵计算结果数据设置到对应的OrderItem里面
     */
    public void setResultToItemBean(ExtendBean bean, OrderItem item) {
        item.setRecType(bean.getRecType());
        item.setValidateResult(bean.getValidateResult());
        if(bean.getRecType() != null){
            if(bean.getRecType() == 0){
                item.setRecVal(bean.getRecVal());
            }else if(bean.getRecType() == 1) {
                item.setRecVal(bean.getCurrentMaxPaid() + "");
            }
        }
        if(bean.getIsRelationScore() != null){
            if(bean.getIsRelationScore()){
                item.setIsRelationScore(1);
            } else {
                item.setIsRelationScore(0);
            }
        }
    }

    /**
     * @Description  处理盈利金额
     * @Param []
     * @Author  max
     * @Date  16:24 2019/11/5
     * @return void
     **/
    public void setProfitAmount(SettleItem settleItem, OrderDetailsDTO orderDetailsDTO, ExtendBean extendBean){
        //走水或拒单
        if(settleItem.getOutCome() == 2 || settleItem.getOutCome() == 9){
            extendBean.setProfit(0L);
            return ;
        }
        //输 赢本金
        if(settleItem.getOutCome() == 3){
            extendBean.setProfit(orderDetailsDTO.getBetAmount()  * -1);
            return ;
        }
        //4-赢,赢一半
        if(settleItem.getOutCome() == 4 || settleItem.getOutCome() == 5){
            extendBean.setProfit((settleItem.getSettleAmount() - orderDetailsDTO.getBetAmount()));
            return ;
        }
        // 6-输一半
        if(settleItem.getOutCome() == 6){
            extendBean.setProfit(settleItem.getSettleAmount() * -1);
            return ;
        }
        
        if(settleItem.getSettleAmount() == null) settleItem.setSettleAmount(0l);
        extendBean.setProfit((settleItem.getSettleAmount() - orderDetailsDTO.getBetAmount()));
    }

    /**
     * @Description  查询赛事消息
     * @Param [matchId]
     * @Author  max
     * @Date  13:37 2019/12/11
     * @return java.util.Map<java.lang.String,java.lang.Object>
     **/
    public Map<String, Object> getMatchInfo(String matchId,String marketId,String playOptionsId){
        String key = String.format("rcs:risk:getmatchinfo_%s_%s_%s", matchId, marketId, playOptionsId);
        Object data = redisClient.getObj(key,Map.class);
        if(data == null) {
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getMacthInfoById(Long.valueOf(matchId));
            if(standardMatchInfo == null){
                throw new RcsServiceException("赛事数据不存在");
            }
            Map<String, Object> map = new HashMap<String, Object>(5);
            map.put("dateExpect", DateUtils.getTimeExpect(standardMatchInfo.getBeginTime()));
            map.put("beginTime", String.valueOf(standardMatchInfo.getBeginTime()));
            map.put("tournamentId", standardMatchInfo.getStandardTournamentId().toString());
            map.put("thirdMatchSourceId", standardMatchInfo.getThirdMatchSourceId());
            map.put("data_source_code",standardMatchInfo.getDataSourceCode());
            StandardSportTournament standardSportTournament = standardSportTournamentService.getById(standardMatchInfo.getStandardTournamentId());
            if(standardSportTournament == null || standardSportTournament.getTournamentLevel() == null ) {
            	map.put("tournamentLevel", "20");
            }else {
            	map.put("tournamentLevel", standardSportTournament.getTournamentLevel().toString());
            }
            //0:未开赛, 1:滚球, 2:暂停，3:结束 ，4:关闭，5:取消，6:放弃，7:延迟，8:未知，9:延期，10:中断
            int isScroll = standardMatchInfo.getMatchStatus();
            //操盘平台为MTS
            if(isMtsPlatForm(Long.parseLong(matchId),isScroll)){
                StandardSportMarket standardSportMarket = standardSportMarketService.getById(NumberUtils.toLong(marketId, 0));
                if(null != standardSportMarket){
                    map.put("extra_info", standardSportMarket.getExtraInfo());
                }

                StandardSportMarketOdds standardSportMarketOdds = standardSportMarketOddsService.getById(NumberUtils.toLong(playOptionsId, 0));
                if(!standardSportMarketOdds.getDataSourceCode().equalsIgnoreCase("SR")){
                    log.error("SR赛事数据源，不能添加手工盘口赔率");
                    throw new RcsServiceException("SR赛事数据源，不能添加手工盘口赔率");
                }

                if(null != standardSportMarketOdds){
                    String thirdTemplateSourceId = convertThirdSource(standardSportMarketOdds.getThirdTemplateSourceId(),standardSportMarketOdds.getExtraInfo());
                    map.put("third_template_source_id",thirdTemplateSourceId);
                }
            }

            redisClient.setExpiry(key,map,24*60*60L);
            return map;
        }
        return (Map<String, Object>) data;
    }

    /**
     * 部分特殊投注  获取玩法id 和投注项id处理
     * @param thirdTemplateSourceId 例   SR:12:1722   12表示玩法   1722 表示投注项id 特殊情况值为:None
     * @param oddsExtraInfo  例 15Andsr:winning_margin:3+:115   And之前的15是玩法id  之后的字符串相当于投注项id
     * @return
     */
    private String convertThirdSource(String thirdTemplateSourceId , String oddsExtraInfo) {
        if (thirdTemplateSourceId.equals("None") || !thirdTemplateSourceId.contains(":")) {
            if(StringUtils.isEmpty(oddsExtraInfo) || !oddsExtraInfo.contains("And")){
                log.error("mts获取第三方玩法和投注项 上游数据异常"+oddsExtraInfo);
                throw new RcsServiceException("mts获取第三方投注项上游数据异常");
            }
            String [] arr = oddsExtraInfo.split("And");
            thirdTemplateSourceId =String.format("SR###%s###%s",arr[0],arr[1]);
        }else {
            thirdTemplateSourceId = thirdTemplateSourceId.replace(":","###");
        }
        return thirdTemplateSourceId;
    }


    /**
     * @Description   查询玩法配置列表
     * @Param [requestParam, item, type]
     * @Author  max
     * @Date  14:46 2019/12/11
     * @return java.util.List<com.panda.sport.rcs.pojo.RcsBusinessPlayPaidConfig>
     **/
    public List<RcsBusinessPlayPaidConfig> getConfigList(Request<OrderBean> requestParam, OrderItem item , Integer playId){
        List<RcsBusinessPlayPaidConfig> list = new ArrayList<>();
        RcsBusinessPlayPaidConfig playConfig = configService.getPlayPaidConfig(requestParam.getData().getTenantId().toString(), item.getSportId().toString(),
                item.getMatchType()%2==0?"1":"0", rcsPaidConfigService.getPlayProcess(String.valueOf(item.getSportId()), String.valueOf(item.getPlayId())), playId.toString());
        list.add(playConfig);
        return list;
    }


    /**
     * @Description   获取玩法配置configVo
     * @Param [requestParam, item, config]
     * @Author  max
     * @Date  14:51 2019/12/11
     * @return com.panda.sport.rcs.vo.RcsBusinessPlayPaidConfigVo
     **/
    public RcsBusinessPlayPaidConfigVo getConfigVo(Request<OrderBean> requestParam,OrderItem item ,RcsBusinessPlayPaidConfig config ){
        RcsBusinessPlayPaidConfigVo vo = new RcsBusinessPlayPaidConfigVo();
        vo.setPlayId((Long.valueOf(item.getPlayId())));
        if(requestParam.getData().getSeriesType() == 1){
            vo.setMinBet(singleMinAmount);
        }
        if(requestParam.getData().getSeriesType() == 2){
            vo.setMinBet(multiMinAmount);
        }
        BigDecimal oddsValue = new BigDecimal(item.getHandleAfterOddsValue().toString()).subtract(new BigDecimal("1"));
        if(oddsValue.compareTo(BigDecimal.ZERO) == 0){
            vo.setOrderMaxPay(config.getOrderMaxPay());
        }
        else {
            vo.setOrderMaxPay(new BigDecimal(config.getOrderMaxPay()).divide(oddsValue,2, RoundingMode.HALF_UP).longValue());
        }
        return vo;
    }

    /**
     * @Description  判断操盘平台是否MTS
     * @Param [matchId]
     * @Author  max
     * @Date  12:04 2020/1/18
     * @return java.lang.Boolean
     **/
    public Boolean isMtsPlatForm(Long matchId , int isScroll) {
        RcsStandardSportMarketSell marketSell = null;

        String key = "rcs:risk:marketsell_"+matchId;
        Object data = redisClient.get(key);

        if (data == null) {
            LambdaQueryWrapper<RcsStandardSportMarketSell> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(RcsStandardSportMarketSell::getMatchInfoId, matchId);
            marketSell = rcsStandardSportMarketSellServiceImpl.getOne(queryWrapper);
            redisClient.setExpiry(key, marketSell,10L);
        }else{
            marketSell =  JsonFormatUtils.fromJson(data.toString(),RcsStandardSportMarketSell.class);
        }
        if(marketSell == null){
            return false;
        }
        //赛前操盘平台
        String riskManager = marketSell.getPreRiskManagerCode();

        /**
         * 类型：1 ：早盘 ，2： 滚球盘， 3： 冠军盘
         * 滚球操盘平台
         */
        if(isScroll==2){
            riskManager = marketSell.getLiveRiskManagerCode();
        }
        log.info("获取赛事操盘类型matchInf:{},isScroll:{},riskManager:{}", matchId,isScroll,riskManager);
        if (marketSell != null && "SR".equalsIgnoreCase(riskManager)) {
            return true;
        }

        return false;
    }
}
