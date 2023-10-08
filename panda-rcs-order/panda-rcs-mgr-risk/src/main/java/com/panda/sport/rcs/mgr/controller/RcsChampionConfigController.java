package com.panda.sport.rcs.mgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.log.LogContext;
import com.panda.sport.rcs.log.annotion.format.LogFormatAnnotion;
import com.panda.sport.rcs.log.format.LogFormatBean;
import com.panda.sport.rcs.log.format.LogFormatDynamicBean;
import com.panda.sport.rcs.log.format.LogFormatPublicBean;
import com.panda.sport.rcs.mgr.enums.LogTypeEnum;
import com.panda.sport.rcs.mgr.wrapper.IRcsTradeConfigService;
import com.panda.sport.rcs.mgr.wrapper.RcsChampionRiskConfigService;
import com.panda.sport.rcs.mgr.wrapper.RcsChampionTradeConfigService;
import com.panda.sport.rcs.mgr.wrapper.StandardSportMarketService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsChampionRiskConfig;
import com.panda.sport.rcs.pojo.RcsChampionTradeConfig;
import com.panda.sport.rcs.pojo.RcsTradeConfig;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.vo.api.request.RcsChampionConfigReqVo;
import com.panda.sport.rcs.pojo.vo.api.request.RcsChampionConfigUpdateReqVo;
import com.panda.sport.rcs.pojo.vo.api.response.RcsChampionConfigResVo;
import com.panda.sport.rcs.pojo.vo.api.response.RcsChampionOddsFieldsResVo;
import com.panda.sport.rcs.utils.StringUtils;
import com.panda.sport.rcs.vo.HttpResponse;
import com.panda.sports.auth.permission.AuthRequiredPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author :  Kir
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.bootstrap.controller
 * @Description :  冠军玩法操盘及限额管理
 * @Date: 2021-06-09
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@RestController
@RequestMapping(value = "/risk/championConfig")
@Component
public class RcsChampionConfigController {

    @Autowired
    private RcsChampionRiskConfigService championRiskConfigService;

    @Autowired
    private RcsChampionTradeConfigService championTradeConfigService;

    @Autowired
    private IRcsTradeConfigService rcsTradeConfigService;

    @Autowired
    private StandardSportMarketService sportMarketService;

    @Autowired
    private RedisClient redisClient;
    @Autowired
    ProducerSendMessageUtils sendMessage;

    /**
     * @Description 获取冠军操盘及限额配置界面内容
     * @Author  kir
     **/
    @RequestMapping(value = "/getInfo",method = RequestMethod.POST)
    public HttpResponse<Map<String, Object>> getInfo(@RequestBody RcsChampionConfigReqVo reqVo){
        try {
            Map<String, Object> map = new HashMap<>();
            //限额配置及跳水注额信息
            RcsChampionConfigResVo resVo = new RcsChampionConfigResVo();
            //投注项信息
            List<RcsChampionOddsFieldsResVo> oddsFieldsResVoList = championTradeConfigService.selectOddsFieldsList(reqVo.getMarketId());
            //panda的margin值
            BigDecimal oddsValueSum = BigDecimal.ZERO;
            //三方下发的margin值
            BigDecimal originalOddsValueSum = BigDecimal.ZERO;
            //第一步：查询赛事基本信息
            this.setMatchInfo(resVo, reqVo, reqVo.getMarketId());
            //第二步：查询限额配置及跳水注额
            this.setConfigInfo(resVo, reqVo);
            //第三步：查询投注项内容，并且为每一个投注项设置货量
            this.setOddsFieldsInfo(reqVo, oddsFieldsResVoList);
            //第四步：根据所有投注项的赔率计算margin值
            for (RcsChampionOddsFieldsResVo rcsChampionOddsFieldsResVo : oddsFieldsResVoList) {
                if (rcsChampionOddsFieldsResVo.getOddsValue().compareTo(BigDecimal.ZERO) != 0 && rcsChampionOddsFieldsResVo.getOddsFiedsStatus() != 0 && resVo.getStatus() == 0) {
                    if (oddsValueSum.compareTo(BigDecimal.ZERO) == 0) {
                        oddsValueSum = BigDecimal.ONE.divide(rcsChampionOddsFieldsResVo.getOddsValue(), 6, BigDecimal.ROUND_DOWN);
                    } else {
                        oddsValueSum = BigDecimal.ONE.divide(rcsChampionOddsFieldsResVo.getOddsValue(), 6, BigDecimal.ROUND_DOWN).add(oddsValueSum);
                    }
                }
                if (rcsChampionOddsFieldsResVo.getOriginalOddsValue().compareTo(BigDecimal.ZERO) != 0 && rcsChampionOddsFieldsResVo.getOddsFiedsStatus() != 0 && resVo.getStatus() == 0) {
                    if (originalOddsValueSum.compareTo(BigDecimal.ZERO) == 0) {
                        originalOddsValueSum = BigDecimal.ONE.divide(rcsChampionOddsFieldsResVo.getOriginalOddsValue(), 6, BigDecimal.ROUND_DOWN);
                    } else {
                        originalOddsValueSum = BigDecimal.ONE.divide(rcsChampionOddsFieldsResVo.getOriginalOddsValue(), 6, BigDecimal.ROUND_DOWN).add(originalOddsValueSum);
                    }
                }
            }
            //margin值只保留两位小数
            map.put("originalOddsValueSum", originalOddsValueSum.divide(new BigDecimal(1), 4, BigDecimal.ROUND_HALF_UP));
            map.put("oddsValueSum", oddsValueSum.divide(new BigDecimal(1), 4, BigDecimal.ROUND_HALF_UP));
            map.put("config", resVo);
            map.put("oddsFieldsList", oddsFieldsResVoList);
            return HttpResponse.success(map);
        }catch (Exception e){
            log.error("赛事ID::{}::获取冠军操盘及限额配置界面内容{}",reqVo.getMatchId(),e.getMessage(),e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }


    /**
     * @Description 修改跳水注额和跳水概率变化及部分限额
     * @Author  kir
     **/
    @RequestMapping(value = "/updateRisk",method = RequestMethod.POST)
    @AuthRequiredPermission("rcs:trade:operate")
    @LogFormatAnnotion
    public HttpResponse<Integer> updateRisk(@RequestBody RcsChampionConfigUpdateReqVo reqVo){

        if (null == reqVo.getOneOddsAmount() || reqVo.getOneOddsAmount().equals("")){
            log.error("OneOddsAmount不能为空");
            return HttpResponse.error(-1, "OneOddsAmount不能为空");
        }
        if (null == reqVo.getOneTotalOddsAmount() || reqVo.getOneTotalOddsAmount().equals("")){
            log.error("OneTotalOddsAmount不能为空");
            return HttpResponse.error(-1, "OneTotalOddsAmount不能为空");
        }
        if (null == reqVo.getOneProbability() || reqVo.getOneProbability().equals("")){
            log.error("OneProbability不能为空");
            return HttpResponse.error(-1, "OneProbability不能为空");
        }
        if (null == reqVo.getTwoProbability() || reqVo.getTwoProbability().equals("")){
            log.error("TwoProbability不能为空");
            return HttpResponse.error(-1, "TwoProbability不能为空");
        }
        if (null == reqVo.getThreeProbability() || reqVo.getThreeProbability().equals("")){
            log.error("ThreeProbability不能为空");
            return HttpResponse.error(-1, "ThreeProbability不能为空");
        }

        try {
            //1.跳水注额，跳水概率变化
            QueryWrapper<RcsChampionRiskConfig> rcsChampionRiskConfig = new QueryWrapper<>();
            rcsChampionRiskConfig.eq("market_id", reqVo.getMarketId());
            if(!ObjectUtils.isEmpty(reqVo.getPlayId())){
                rcsChampionRiskConfig.eq("play_id", reqVo.getPlayId());
            }
            RcsChampionRiskConfig one = championRiskConfigService.getOne(rcsChampionRiskConfig);
            BigDecimal oldValue = new BigDecimal(0.0);
            BigDecimal newValue = new BigDecimal(0.0);
            List<LogFormatBean>  logEntities = Lists.newArrayList();
            DecimalFormat df = new DecimalFormat("#");
            DecimalFormat df1 = new DecimalFormat("#.#");
            if(ObjectUtils.isEmpty(one)){
                //不存在则新增
                one = new RcsChampionRiskConfig();
                one.setMarketId(reqVo.getMarketId());
                one.setMatchId(reqVo.getMatchId());
                if(!ObjectUtils.isEmpty(reqVo.getPlayId())){
                    one.setPlayId(reqVo.getPlayId());
                }
                if(!ObjectUtils.isEmpty(reqVo.getOneTotalOddsAmount())){
                    oldValue = new BigDecimal(500000);
                    if(Double.compare(oldValue.doubleValue(), reqVo.getOneTotalOddsAmount().doubleValue()) != 0) {
                        LogFormatBean logFormatBean = new LogFormatBean("单项累计跳水注额", df.format(oldValue), df.format(reqVo.getOneTotalOddsAmount()));
                        logEntities.add(logFormatBean);
                    }
                }
                if(!ObjectUtils.isEmpty(reqVo.getOneOddsAmount())){
                    oldValue = new BigDecimal(100000);
                    if(Double.compare(oldValue.doubleValue(), reqVo.getOneOddsAmount().doubleValue()) != 0) {
                        LogFormatBean logFormatBean = new LogFormatBean("单枪跳水注额", df.format(oldValue), df.format(reqVo.getOneOddsAmount()));
                        logEntities.add(logFormatBean);
                    }
                }
                if(!ObjectUtils.isEmpty(reqVo.getOneProbability())){
                    oldValue = new BigDecimal(0.5);
                    if(Double.compare(oldValue.doubleValue(), reqVo.getOneProbability().doubleValue()) != 0) {
                        LogFormatBean logFormatBean = new LogFormatBean("一次跳水概率", df1.format(oldValue), df1.format(reqVo.getOneProbability()));
                        logEntities.add(logFormatBean);
                    }
                }
                if(!ObjectUtils.isEmpty(reqVo.getTwoProbability())){
                    oldValue = new BigDecimal(1);
                    if(Double.compare(oldValue.doubleValue(), reqVo.getTwoProbability().doubleValue()) != 0) {
                        LogFormatBean logFormatBean = new LogFormatBean("二次跳水概率", df1.format(oldValue), df1.format(reqVo.getTwoProbability()));
                        logEntities.add(logFormatBean);
                    }
                }
                if(!ObjectUtils.isEmpty(reqVo.getThreeProbability())){
                    oldValue = new BigDecimal(1.5);
                    if(Double.compare(oldValue.doubleValue(), reqVo.getThreeProbability().doubleValue()) != 0) {
                        LogFormatBean logFormatBean = new LogFormatBean("三次跳水概率", df1.format(oldValue), df1.format(reqVo.getThreeProbability()));
                        logEntities.add(logFormatBean);
                    }
                }
                one.setOneTotalOddsAmount(reqVo.getOneTotalOddsAmount());
                one.setOneOddsAmount(reqVo.getOneOddsAmount());
                one.setOneProbability(reqVo.getOneProbability());
                one.setTwoProbability(reqVo.getTwoProbability());
                one.setThreeProbability(reqVo.getThreeProbability());
                championRiskConfigService.save(one);
            }else{
                //存在则修改；前端有传值则修改
                if(!ObjectUtils.isEmpty(reqVo.getOneTotalOddsAmount())){
                    oldValue = one.getOneTotalOddsAmount();
                    newValue = reqVo.getOneTotalOddsAmount();
                    one.setOneTotalOddsAmount(newValue);
                    if(Double.compare(oldValue.doubleValue(), newValue.doubleValue()) != 0){
                        LogFormatBean logFormatBean = new LogFormatBean("单项累计跳水注额", df.format(oldValue), df.format(newValue));
                        logEntities.add(logFormatBean);
                    }
                }
                if(!ObjectUtils.isEmpty(reqVo.getOneOddsAmount())){
                    oldValue = one.getOneOddsAmount();
                    newValue = reqVo.getOneOddsAmount();
                    one.setOneOddsAmount(newValue);
                    if(Double.compare(oldValue.doubleValue(), newValue.doubleValue()) != 0){
                        LogFormatBean logFormatBean = new LogFormatBean("单枪跳水注额", df.format(oldValue), df.format(newValue));
                        logEntities.add(logFormatBean);
                    }
                }
                if(!ObjectUtils.isEmpty(reqVo.getOneProbability())){
                    oldValue = one.getOneProbability();
                    newValue = reqVo.getOneProbability();
                    one.setOneProbability(newValue);
                    if(Double.compare(oldValue.doubleValue(), newValue.doubleValue()) != 0){
                        LogFormatBean logFormatBean = new LogFormatBean("一次跳水概率", df1.format(oldValue), df1.format(newValue));
                        logEntities.add(logFormatBean);
                    }
                }
                if(!ObjectUtils.isEmpty(reqVo.getTwoProbability())){
                    oldValue = one.getTwoProbability();
                    newValue = reqVo.getTwoProbability();
                    one.setTwoProbability(newValue);
                    if(Double.compare(oldValue.doubleValue(), newValue.doubleValue()) != 0){
                        LogFormatBean logFormatBean = new LogFormatBean("二次跳水概率", df1.format(oldValue), df1.format(newValue));
                        logEntities.add(logFormatBean);
                    }
                }
                if(!ObjectUtils.isEmpty(reqVo.getThreeProbability())){
                    oldValue = one.getThreeProbability();
                    newValue = reqVo.getThreeProbability();
                    one.setThreeProbability(newValue);
                    if(Double.compare(oldValue.doubleValue(), newValue.doubleValue()) != 0){
                        LogFormatBean logFormatBean = new LogFormatBean("三次跳水概率", df1.format(oldValue), df1.format(newValue));
                        logEntities.add(logFormatBean);
                    }
                }
                championRiskConfigService.updateById(one);
            }
            LogFormatPublicBean publicBean = new LogFormatPublicBean(LogTypeEnum.CHAMPION_TYPE.getCode()+"", "参数设置", String.valueOf(reqVo.getMatchId()));
            Map<String, Object> dynamicBean = new HashMap<String, Object>();
            String objIdValue = reqVo.getMatchId() + "-" + reqVo.getMarketId();
            dynamicBean.put("obj_id", objIdValue);
            dynamicBean.put("click_case", "参数设置");
            addFormatBeanLog(publicBean, dynamicBean, logEntities);
            log.info("::{}:: updateRisk 更新完成", reqVo.getMatchId());
            return HttpResponse.success(200);
        }catch (Exception e){
            log.error("::{}::updateRisk ERROR{},{}",e.getMessage(),e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }

    public void addFormatBeanLog(LogFormatPublicBean publicBean , Object dynamicBean  , List<LogFormatBean> beanList ) {
        if(publicBean == null || beanList == null || beanList.size() <= 0) return;
        List<Map<String, Object>> formatList = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = JSONObject.parseObject(JSONObject.toJSONString(publicBean) , new TypeReference<Map<String, Object>>(){});
        Map<String, Object> dynamicMap = LogFormatDynamicBean.parseAnno(dynamicBean);
        map.put("dynamicBean", dynamicMap);

        for(LogFormatBean formatBean : beanList) {
            if(!StringUtils.isBlank(formatBean.getFormat())) {
                formatBean.setOldVal(String.format(formatBean.getFormat(), formatBean.getOldVal()));
                formatBean.setNewVal(String.format(formatBean.getFormat(), formatBean.getNewVal()));
            }

            Map<String, Object> temMap = JSONObject.parseObject(JSONObject.toJSONString(map) , new TypeReference<Map<String, Object>>(){});
            temMap.putAll(JSONObject.parseObject(JSONObject.toJSONString(formatBean) , new TypeReference<Map<String, Object>>(){}));
            formatList.add(temMap);
        }
        if(formatList.size() > 0){
            sendMessage.sendMessage("RCS_LOG_FORMAT", null, publicBean.getUid(), formatList);
        }

    }

    /**
     * @Description 修改用户单项赔付限额
     * @Author  kir
     **/
    @RequestMapping(value = "/updateTrade",method = RequestMethod.POST)
    @AuthRequiredPermission("rcs:risk:operate")
    @LogFormatAnnotion
    public HttpResponse<Integer> updateTrade(@RequestBody RcsChampionConfigUpdateReqVo reqVo){
        //2.限额，用户单注赔付限额
        QueryWrapper<RcsChampionTradeConfig> rcsChampionTradeConfig = new QueryWrapper<>();
        rcsChampionTradeConfig.eq("market_id", reqVo.getMarketId());
        rcsChampionTradeConfig.eq("type", reqVo.getLimitType());
        if(!ObjectUtils.isEmpty(reqVo.getPlayId())){
            rcsChampionTradeConfig.eq("play_id", reqVo.getPlayId());
        }
        if(!ObjectUtils.isEmpty(reqVo.getOddsFiedsId())){
            rcsChampionTradeConfig.eq("odds_fields_id", reqVo.getOddsFiedsId());
        }
        RcsChampionTradeConfig one = championTradeConfigService.getOne(rcsChampionTradeConfig);
        BigDecimal oldValue = new BigDecimal(0);
        StringBuilder changDesc = new StringBuilder();
        Map<String, Object> dynamicBean = new HashMap<String, Object>();
        String objIdValue = reqVo.getMatchId() + "-" + reqVo.getMarketId();
        dynamicBean.put("obj_id", objIdValue);
        dynamicBean.put("click_case", "参数设置");
        if(ObjectUtils.isEmpty(one)){
            one = new RcsChampionTradeConfig();
            one.setMarketId(reqVo.getMarketId());
            one.setMatchId(reqVo.getMatchId());
            if(!ObjectUtils.isEmpty(reqVo.getPlayId())){
                one.setPlayId(reqVo.getPlayId());
            }
            one.setType(reqVo.getLimitType());
            one.setAmount(reqVo.getAmount());
            if(reqVo.getLimitType().equals(4)){
                changDesc.append("用户单项赔付限额");
                dynamicBean.put("oddsFiedsId",reqVo.getOddsFiedsId());
                one.setOddsFieldsId(reqVo.getOddsFiedsId());
            }else{
                if(reqVo.getLimitType().equals(1)){
                    oldValue = new BigDecimal(10000000);
                    changDesc.append("商户玩法累计赔付限额");
                }else if(reqVo.getLimitType().equals(2)){
                    oldValue = new BigDecimal(1000000);
                    changDesc.append("用户玩法累计赔付限额");
                }else if(reqVo.getLimitType().equals(3)){
                    oldValue = new BigDecimal(500000);
                    changDesc.append("用户单注投注赔付限额");
                }
            }
            championTradeConfigService.save(one);
        }else{
            oldValue = one.getAmount();
            one.setAmount(reqVo.getAmount());
            if(reqVo.getLimitType().equals(4)){
                changDesc.append("用户单项赔付限额");
                dynamicBean.put("oddsFiedsId",reqVo.getOddsFiedsId());
                one.setOddsFieldsId(reqVo.getOddsFiedsId());
            }else{
                if(reqVo.getLimitType().equals(1)){
                    changDesc.append("商户单场赔付限额");
                }else if(reqVo.getLimitType().equals(2)){
                    changDesc.append("用户玩法累计赔付限额");
                }else if(reqVo.getLimitType().equals(3)){
                    changDesc.append("用户单注投注赔付限额");
                }
            }
            championTradeConfigService.updateById(one);
        }
        String key = String.format("rcs:champion:limit:%s:%s", reqVo.getMatchId(), reqVo.getMarketId());
        redisClient.delete(key);
        log.info("删除冠军玩法限额缓存：key=" + key);
        if(Double.compare(oldValue.doubleValue(), reqVo.getAmount().doubleValue()) != 0){
            LogFormatPublicBean publicBean = new LogFormatPublicBean(LogTypeEnum.CHAMPION_TYPE.getCode()+"", "参数设置", String.valueOf(reqVo.getMatchId()));
            DecimalFormat df =new DecimalFormat("#");
            LogFormatBean logFormatBean = new LogFormatBean(changDesc.toString(), df.format(oldValue),  df.format(reqVo.getAmount()));
            LogContext.getContext().addFormatBean(publicBean, dynamicBean, logFormatBean);
            log.info("::{}:: updateTrade 完成日志记录{}、原值:{},新值:{}", reqVo.getMatchId(), changDesc,  df.format(oldValue), df.format(reqVo.getAmount()));
        }
        return HttpResponse.success(200);
    }

    /**
     * 对赛事基本信息进行赋值
     * @param resVo
     * @param reqVo
     * @param marketId
     */
    private void setMatchInfo(RcsChampionConfigResVo resVo, RcsChampionConfigReqVo reqVo, String marketId){
        resVo.setMarketId(marketId);
        resVo.setMatchId(reqVo.getMatchId());
        resVo.setPlayId(reqVo.getPlayId());
        //赛事状态，用于查询货量
        Integer marketType = championTradeConfigService.selectMatchStatus(reqVo.getMarketId());
        resVo.setMarketType(marketType);
        QueryWrapper<RcsTradeConfig> tradeConfigWrapper = new QueryWrapper<>();
        tradeConfigWrapper.eq("match_id", reqVo.getMatchId());
        tradeConfigWrapper.eq("trader_level", 3);
        tradeConfigWrapper.eq("targer_data", marketId);
        tradeConfigWrapper.orderByDesc("crt_time");
        tradeConfigWrapper.last("limit 1");
        RcsTradeConfig tradeConfig = rcsTradeConfigService.getOne(tradeConfigWrapper);
        if(ObjectUtils.isEmpty(tradeConfig)){
            resVo.setStatus(0);
            resVo.setDataSource(0);
        }else{
            resVo.setStatus(tradeConfig.getStatus());
            resVo.setDataSource(tradeConfig.getDataSource());
        }

        QueryWrapper<StandardSportMarket> standardSportMarket = new QueryWrapper<>();
        standardSportMarket.eq("id", marketId);
        StandardSportMarket one = sportMarketService.getOne(standardSportMarket);

        if(ObjectUtils.isEmpty(one)){
            resVo.setThirdMarketSourceStatus(0);
        }else{
            resVo.setThirdMarketSourceStatus(one.getThirdMarketSourceStatus());
        }

    }

    /**
     * 对限额配置及跳水注额进行构造
     * @param resVo
     * @param reqVo
     */
    private void setConfigInfo(RcsChampionConfigResVo resVo, RcsChampionConfigReqVo reqVo){
        //根据赛事id和玩法id获取对应冠军玩法操盘及限额配置信息 type=1,2,3的list
        //若有查询到数据则赋值，若未查询到则 oneTotalOddsAmount=500000 oneOddsAmount=100000 oneProbability=0.5 twoProbability=1
        //threeProbability=1.5 merchantPlaysTotalAmount=10000000 userPlaysAmount=1000000 userOneAmount=500000
        QueryWrapper<RcsChampionTradeConfig> tradeWrapper = new QueryWrapper<>();
        tradeWrapper.eq("market_id", reqVo.getMarketId());
        List<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(2);
        integers.add(3);
        tradeWrapper.in("type", integers);
        List<RcsChampionTradeConfig> tradeList = championTradeConfigService.list(tradeWrapper);
        if(tradeList.size()>0){
            for (RcsChampionTradeConfig rcsChampionTradeConfig : tradeList) {
                if(rcsChampionTradeConfig.getType().equals(1)){
                    resVo.setMerchantPlaysTotalAmount(rcsChampionTradeConfig.getAmount());
                }
                if(rcsChampionTradeConfig.getType().equals(2)){
                    resVo.setUserPlaysAmount(rcsChampionTradeConfig.getAmount());
                }
                if(rcsChampionTradeConfig.getType().equals(3)){
                    resVo.setUserOneAmount(rcsChampionTradeConfig.getAmount());
                }
            }
            //判断是否为空 为空则赋默认值
            if(ObjectUtils.isEmpty(resVo.getMerchantPlaysTotalAmount())){
                resVo.setMerchantPlaysTotalAmount(new BigDecimal(10000000));
            }
            if(ObjectUtils.isEmpty(resVo.getUserPlaysAmount())){
                resVo.setUserPlaysAmount(new BigDecimal(1000000));
            }
            if(ObjectUtils.isEmpty(resVo.getUserOneAmount())){
                resVo.setUserOneAmount(new BigDecimal(500000));
            }
        }else{
            resVo.setMerchantPlaysTotalAmount(new BigDecimal(10000000));
            resVo.setUserPlaysAmount(new BigDecimal(1000000));
            resVo.setUserOneAmount(new BigDecimal(500000));
        }
        QueryWrapper<RcsChampionRiskConfig> riskWrapper = new QueryWrapper<>();
        riskWrapper.eq("market_id", reqVo.getMarketId());
        RcsChampionRiskConfig one = championRiskConfigService.getOne(riskWrapper);
        if(!ObjectUtils.isEmpty(one)){
            if(!ObjectUtils.isEmpty(one.getOneTotalOddsAmount())){
                resVo.setOneTotalOddsAmount(one.getOneTotalOddsAmount());
            }else{
                resVo.setOneTotalOddsAmount(new BigDecimal(500000));
            }
            if(!ObjectUtils.isEmpty(one.getOneOddsAmount())){
                resVo.setOneOddsAmount(one.getOneOddsAmount());
            }else{
                resVo.setOneOddsAmount(new BigDecimal(100000));
            }
            if(!ObjectUtils.isEmpty(one.getOneProbability())){
                resVo.setOneProbability(one.getOneProbability());
            }else{
                resVo.setOneProbability(new BigDecimal(0.5));
            }
            if(!ObjectUtils.isEmpty(one.getTwoProbability())){
                resVo.setTwoProbability(one.getTwoProbability());
            }else{
                resVo.setTwoProbability(new BigDecimal(1));
            }
            if(!ObjectUtils.isEmpty(one.getThreeProbability())){
                resVo.setThreeProbability(one.getThreeProbability());
            }else{
                resVo.setThreeProbability(new BigDecimal(1.5));
            }
        }else{
            resVo.setOneTotalOddsAmount(new BigDecimal(500000));
            resVo.setOneOddsAmount(new BigDecimal(100000));
            resVo.setOneProbability(new BigDecimal(0.5));
            resVo.setTwoProbability(new BigDecimal(1));
            resVo.setThreeProbability(new BigDecimal(1.5));
        }
    }

    /**
     * 对投注项内容进行构造
     * @param reqVo
     * @param oddsFieldsResVoList
     */
    private void setOddsFieldsInfo(RcsChampionConfigReqVo reqVo, List<RcsChampionOddsFieldsResVo> oddsFieldsResVoList){
        //根据赛事id和玩法id获取对应冠军玩法的盘口id
        //根据上面获取的marketId查询standard_sport_market_odds表获取投注项列表
        //根据赛事id和玩法id以及type为4(用户单项限额)查询rcs_champion_trade_config表获取对应的 用户单项赔付限额
        //有数据则构造到上面所查到的oddsFieldsResVoList中去，无数据则值设置为1000000（需求文档默认值），得到新的投注列表集合
        QueryWrapper<RcsChampionTradeConfig> tradeWrapperForOddsFields = new QueryWrapper<>();
        tradeWrapperForOddsFields.eq("market_id", reqVo.getMarketId());
        tradeWrapperForOddsFields.eq("type", 4);
        List<RcsChampionTradeConfig> tradeListForOddsFields = championTradeConfigService.list(tradeWrapperForOddsFields);
        if(tradeListForOddsFields.size()>0){
            for (RcsChampionOddsFieldsResVo rcsChampionOddsFieldsResVo : oddsFieldsResVoList) {
                Map<String, RcsChampionTradeConfig> collect = tradeListForOddsFields.stream().collect(Collectors.toMap(e -> e.getOddsFieldsId(), e -> e));
                RcsChampionTradeConfig rcsChampionTradeConfig = collect.get(rcsChampionOddsFieldsResVo.getOddsFiedsId());
                if(ObjectUtils.isEmpty(rcsChampionTradeConfig)){
                    //如果没有设置值则不赋值
                    //rcsChampionOddsFieldsResVo.setAmount(new BigDecimal(1000000));
                }else{
                    rcsChampionOddsFieldsResVo.setAmount(rcsChampionTradeConfig.getAmount());
                }
            }
        }else{
//            如果没有设置值则不赋值
//            for (RcsChampionOddsFieldsResVo rcsChampionOddsFieldsResVo : oddsFieldsResVoList) {
//                rcsChampionOddsFieldsResVo.setAmount(new BigDecimal(1000000));
//            }
        }

        //为每一个投注项设置货量
        //根据赛事id和玩法id以及盘口id查询rcs_predict_bet_odds表获取货量及纯赔付额，并封装至oddsFieldsResVoList中去
        List<Map<String, Object>> betAmountList = championTradeConfigService.selectBetAmount(reqVo.getMatchId(), reqVo.getPlayId(), reqVo.getMarketId());
        if(betAmountList.size()>0){
            //判断一下是否包含新的货量数据
            Boolean hasUniqueHashValue = false;
            for(Map<String,Object> m : betAmountList){
                if(m.get("hashUniqe") != null){
                    log.info("货量表rcs_predict_bet_odds包含新索引数据");
                    hasUniqueHashValue =true;
                    break;
                }
            }
            for (RcsChampionOddsFieldsResVo rcsChampionOddsFieldsResVo : oddsFieldsResVoList) {
                try {
                    Map<String, Map<String, Object>> collect = new HashMap<>();
                    if(hasUniqueHashValue){
                        //剔除旧数据 使用新数据
                        collect = betAmountList.stream().filter(e->e.get("hashUniqe")!=null).collect(Collectors.toMap(e -> String.valueOf(e.get("oddsType")), e -> e));
                    }else{
                        //继续使用旧数据
                        collect = betAmountList.stream().collect(Collectors.toMap(e -> String.valueOf(e.get("oddsType")), e -> e));
                    }
                    //Map<String, Map<String, Object>> collect = betAmountList.stream().collect(Collectors.toMap(e -> String.valueOf(e.get("oddsType")), e -> e));
                    Map<String, Object> stringBigDecimalMap = collect.get(rcsChampionOddsFieldsResVo.getOddsType());
                    if(ObjectUtils.isEmpty(stringBigDecimalMap)){
                        rcsChampionOddsFieldsResVo.setBetOrderNum(new BigDecimal(0));
                        rcsChampionOddsFieldsResVo.setBetAmountPay(new BigDecimal(0));
                        rcsChampionOddsFieldsResVo.setBetAmount(new BigDecimal(0));
                    }else{
                        rcsChampionOddsFieldsResVo.setBetOrderNum(new BigDecimal(stringBigDecimalMap.get("betOrderNum").toString()));
                        rcsChampionOddsFieldsResVo.setBetAmountPay(new BigDecimal(stringBigDecimalMap.get("betAmountPay").toString()));
                        rcsChampionOddsFieldsResVo.setBetAmount(new BigDecimal(stringBigDecimalMap.get("betAmount").toString()));
                    }
                }catch (Exception e)    {
                    log.error("::setOddsFieldsInfo:: ERROR{}",e.getMessage());
                }
            }
        }else{
            for (RcsChampionOddsFieldsResVo rcsChampionOddsFieldsResVo : oddsFieldsResVoList) {
                rcsChampionOddsFieldsResVo.setBetOrderNum(new BigDecimal(0));
                rcsChampionOddsFieldsResVo.setBetAmountPay(new BigDecimal(0));
                rcsChampionOddsFieldsResVo.setBetAmount(new BigDecimal(0));
            }
        }
    }
}
