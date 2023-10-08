package com.panda.sport.rcs.mgr.calculator.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.ThreewayOverLoadTriggerItem;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.common.FileReadUtils;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.enums.BalanceTypeEnum;
import com.panda.sport.rcs.enums.Basketball;
import com.panda.sport.rcs.enums.MarketKindEnum;
import com.panda.sport.rcs.enums.OddsTypeEnum;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.enums.TraderLevelEnum;
import com.panda.sport.rcs.enums.YesNoEnum;
import com.panda.sport.rcs.exeception.LogicException;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.RcsStandardOutrightMatchInfoMapper;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mapper.TUserMapper;
import com.panda.sport.rcs.mgr.constant.Constants;
import com.panda.sport.rcs.mgr.enums.LanguageTypeDataEnum;
import com.panda.sport.rcs.mgr.mq.impl.trigger.TriggerChangeImpl;
import com.panda.sport.rcs.mgr.utils.StringUtil;
import com.panda.sport.rcs.mgr.utils.TradeVerificationService;
import com.panda.sport.rcs.mgr.wrapper.AmountLimitService;
import com.panda.sport.rcs.mgr.wrapper.BalanceService;
import com.panda.sport.rcs.mgr.wrapper.IRcsMatchMarketConfigService;
import com.panda.sport.rcs.mgr.wrapper.IRcsTradeConfigService;
import com.panda.sport.rcs.mgr.wrapper.MarketOddsChangeCalculationService;
import com.panda.sport.rcs.mgr.wrapper.StandardSportMarketOddsService;
import com.panda.sport.rcs.mgr.wrapper.StandardSportMarketService;
import com.panda.sport.rcs.mgr.wrapper.impl.BalanceServiceImpl;
import com.panda.sport.rcs.mgr.wrapper.impl.RcsTradeConfigServiceImpl;
import com.panda.sport.rcs.mgr.wrapper.impl.TOrderServiceImpl;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.RcsStandardOutrightMatchInfo;
import com.panda.sport.rcs.pojo.RcsTradeConfig;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.TUser;
import com.panda.sport.rcs.pojo.odds.JumpMarketMsgDto;
import com.panda.sport.rcs.pojo.odds.JumpOddsLuaDto;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import com.panda.sport.rcs.utils.i18n.PlayTemplateUtils;
import com.panda.sport.rcs.vo.statistics.BalanceVo;
import com.panda.sport.rcs.vo.statistics.MarketBalanceVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author :  holly
 * @Project Name :rcs-parent
 * @Package Name :com.panda.sport.rcs.rpc.calculator.service.impl
 * @Description :操盘限额计算
 * @Date: 2019-10-22 21:36
 */
@Slf4j
public abstract class AmountLimitServiceAdapter implements AmountLimitService {
    /**
     * 加载lua脚本和计算
     * @param fileName
     * @return
     */
    public RedisClient redisClient;

    @Autowired
    StandardSportMarketOddsService standardSportMarketOddsService;

    @Autowired
    StandardSportMarketService standardSportMarketService;

    @Autowired
    BalanceService balanceService;
    @Autowired
    BalanceServiceImpl balanceServiceImpl;

    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    private RcsStandardOutrightMatchInfoMapper rcsStandardOutrightMatchInfoMapper;

    @Autowired
    IRcsMatchMarketConfigService rcsMatchMarketConfigService;

    @Autowired
    MarketOddsChangeCalculationService marketOddsChangeCalculationService;

    @Autowired
    IRcsTradeConfigService rcsTradeConfigService;
    @Autowired
    RcsTradeConfigServiceImpl rcsTradeConfigServiceImpl;

    public String SHA_KEY = null;
    public String mostOddsTypeShakey = null;
    private String jumpMarketShakey;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    private TUserMapper tUserMapper;
    @Autowired
    private TradeVerificationService tradeVerificationService;


    @Autowired
    private TOrderServiceImpl orderService;

    public AmountLimitServiceAdapter(RedisClient redisClient) {
        this.redisClient = redisClient;
        String text = FileReadUtils.readFileContent("lua/oddsCalc_V5.lua");
        String jumpMarketText = FileReadUtils.readFileContent("lua/jumpMarket.lua");
        String mostOddsTypeShakeyText = FileReadUtils.readFileContent("lua/oddsCalc_most.lua");
        mostOddsTypeShakey = redisClient.scriptLoad(mostOddsTypeShakeyText);
        SHA_KEY = redisClient.scriptLoad(text);
        jumpMarketShakey = redisClient.scriptLoad(jumpMarketText);
        if (SHA_KEY == null || jumpMarketShakey == null) {
            throw new RcsServiceException("跳水/跳盘脚本加载失败");
        }
    }

    public Boolean isHome(String playOptions,String marketValue) {
    	Boolean isHome = false;
        if(BaseConstants.ODD_TYPE_1.equalsIgnoreCase(playOptions) ||
                BaseConstants.ODD_TYPE_2.equalsIgnoreCase(playOptions)){
            if (StringUtils.isBlank(playOptions)){
                if (BaseConstants.ODD_TYPE_1.equalsIgnoreCase(playOptions)){
                    isHome = true;
                }
            }else if(playOptions != null &&
                    StringUtils.isNotBlank(marketValue) &&
            		playOptions.contains(marketValue)){
                isHome = true;
            }
        }else if(BaseConstants.ODD_TYPE_OVER.equalsIgnoreCase(playOptions)){
            isHome = true;
        }else if(BaseConstants.ODD_TYPE_ODD.equalsIgnoreCase(playOptions)){
            isHome = true;
        }

        return isHome;
    }

    public void executeCalcLua(OrderItem item, RcsMatchMarketConfig result, JumpOddsLuaDto luaDto) {
        log.info("::{}::,executeCalcLua start item={},result={},luaDto={}",item.getOrderNo(),
                JSONObject.toJSONString(item),
                JSONObject.toJSONString(result),
                JSONObject.toJSONString(luaDto));
        String keySuffix = luaDto.getKeySuffix();
        String dateExpect = luaDto.getDateExpect();
        if (StringUtils.isBlank(dateExpect)) {
            dateExpect = getMatchDateExpect(item);
            luaDto.setDateExpect(dateExpect);
            item.setDateExpect(dateExpect);
        }
        String key = String.format(RedisKeys.CALC_AMOUNT_ODDS_CHANGE, dateExpect, keySuffix);
        Integer isLock = 0;
        Integer isJumpMarket = NumberUtils.INTEGER_ZERO;
        String suffixKey = "{" + keySuffix + "}";
        JSONArray jsonArr = null;
        try {
            List<String> keys = new ArrayList<>();
            keys.add("A" + suffixKey);
            List<String> args = luaDto.getLuaArgs();
            //是否开启跳赔
            if (NumberUtils.INTEGER_ONE == luaDto.getIsOpenJumpOdds().intValue()) {
                log.info("::{}::,executeCalcLua lua脚本入参：shaKey={},keys={},args={}", item.getOrderNo(), SHA_KEY, keys, args);
                Object ret = null;
                if (RcsConstant.ALL_SPORT_MOST_PLAY.contains(result.getPlayId().intValue()) || 3== item.getMatchType()) {
                    ret = redisClient.evalsha(mostOddsTypeShakey, keys, args);
                } else {
                    ret = redisClient.evalsha(SHA_KEY, keys, args);
                }
                //lockVal,isChangeOdds,changeLevel,currentOptionAmount,allAmount
                //当前赔率是否锁定   ，是否需要变更赔率  ，变更级别  ，当前投注项计算金额"主客赔率均不可以为空"，当前盘口总金额
                log.info("::{}::,executeCalcLua lua脚本返回结果：result={}" ,item.getOrderNo(), JSONObject.toJSONString(ret));
                jsonArr = JSONObject.parseArray(JSONObject.toJSONString(ret));
                isLock = jsonArr.getInteger(0);
                if (2 == isLock) {
                    log.warn("::{}::赔率正在变化，直接返回",item.getOrderNo());
                    throw new RcsServiceException(7001, "赔率正在变化，直接返回!");
                }
                if (3 == isLock) {
                    log.warn("::{}::足球赔率在当前秒已经变化，不在计算货量",item.getOrderNo());
                    throw new RcsServiceException(7001, "足球赔率在当前秒已经变化，不在计算货量！");
                }
                //lockVal,isChangeOdds,changeLevel,currentOptionAmount,allAmount
                //当前赔率是否锁定   ，是否需要变更赔率  ，变更级别  ，当前投注项计算金额"主客赔率均不可以为空"，当前盘口总金额
                // 是否开启跳盘
            }
            if (NumberUtils.INTEGER_ONE == luaDto.getIsOpenJumpMarket().intValue() &&
                    (luaDto.getJumpMarketOneLimit().intValue() >= 100 || luaDto.getJumpMarketSecondLimit().intValue() >= 100)){
                isJumpMarket = executeJumpMarketLua(item, result, luaDto);
            }
            // 是否跳赔跳盘
            if (NumberUtils.INTEGER_ONE == isJumpMarket.intValue() || 1 == isLock) {
                log.info("::{}::跳盘发生已重新结算---------",item.getOrderNo());
                result.setSportId(item.getSportId());
                triggerChange(result, item, jsonArr);
            }
        } catch (RcsServiceException e) {
            if (7001 == e.getCode()) {
                throw e;
            }
        } catch (Exception e) {
            log.error("::{}::赔率计算失败，请重试信息{}",item.getOrderNo(),e.getMessage());
            throw new RcsServiceException(7002, "赔率计算失败，请重试!");
        } finally {
            if (1 == isLock) {
                redisClient.delete(key + ":lock" + suffixKey);
            }
            BalanceVo balanceVo = new BalanceVo(item.getSportId().longValue(), item.getMatchId(), item.getPlayId().longValue(), item.getPlaceNum(), item.getMarketId(),item.getSubPlayId());
            balanceService.queryBalance(1, dateExpect, luaDto.getKeySuffix(), luaDto.getBalanceOption(), balanceVo);
            MarketBalanceVo vo = new MarketBalanceVo(item.getSportId().longValue(), item.getMatchId(), item.getPlayId().longValue(), item.getPlaceNum(), item.getMarketId(),item.getSubPlayId());
            vo.setBalanceValue(balanceVo.getBalanceValue());
            vo.setCurrentSide(balanceVo.getCurrentSide());
            log.info("::{}::,发送平衡值到前端，marketId:{},VO : {}", item.getOrderNo(),item.getMarketId(),JSONObject.toJSONString(vo));
            balanceService.updateBalance(item.getMatchId(), item.getMarketId(), vo, null);
        }
    }

    public abstract void triggerChange(RcsMatchMarketConfig result,OrderItem item, JSONArray exeResultArray);

    public RedisClient getRedisClient(){
        return this.redisClient;
    }

    /**
     * 限额计算||平衡值 赛事和联赛
     * 两项盘返回负数 代表客方投注超过主方投注
     * 三项盘返回负数 代表预期盈利为亏损
     * @param item
     */
    public RcsMatchMarketConfig getConfiguredParams(OrderItem item) {
//        log.info("::{}::,{} getConfiguredParams , item:{}",item.getOrderNo(),this.getClass(),JSONObject.toJSONString(item));
        if(item == null){
            return null;
        }
        String amoutLimitKey = "rcs:amount:limit:betno:"+item.getBetNo();
        if(redisClient.exist(amoutLimitKey)) {
            log.info("::{}::,{} checkItemValid 不能重复计算同一个注单！betno:{}",item.getOrderNo(), this.getClass(),item.getBetNo());
            return null;
        }else{
            redisClient.setExpiry(amoutLimitKey,item.getBetNo(),RcsConstant.BET_EXIST_TIME);
        }
//        checkValidPlay(item);
        RcsMatchMarketConfig result = null;
        try {
            checkOrderItemArguments(item);
            //计算当前限额 || 平衡值  拆分成两个
            result = getMarketConfig(item);
            if(null == result || null == result.getHomeLevelFirstMaxAmount()){
                log.warn("::{}::,赛事或联赛未配置任何参数，使用自动数据源数据，不做计算！item:{}", item.getOrderNo(),JSONObject.toJSONString(item));
                return null;
            }
            //是否使用数据源0：自动；1：手动
            if(null == result.getDataSource()) {
                log.warn("::{}::,{} 赛事或联赛没有设置数据源，不做计算！item:{}",item.getOrderNo(),item.getMarketId(),JSONObject.toJSONString(item));
                return null;
            }
            //水差/赔率变化率
            if (MarketKindEnum.Europe.getValue().equalsIgnoreCase(result.getMarketType())){
                if((result.getHomeLevelFirstOddsRate() !=null && result.getHomeLevelFirstOddsRate().abs().compareTo(new BigDecimal("30"))>0)){
                    log.warn("::{}:: {}三项盘赔率变化率不能为空并且绝对值需要介于0-30,result:{}",item.getOrderNo(),item.getMarketId(),JSONObject.toJSONString(result));
                    return null;
                }
            }
            //水差/赔率变化率
            if (MarketKindEnum.Malaysia.getValue().equalsIgnoreCase(result.getMarketType())){
                if((result.getAwayLevelFirstOddsRate() != null && result.getAwayLevelFirstOddsRate().abs().compareTo(new BigDecimal("0.15"))>0)
                        || (result.getAwayLevelSecondOddsRate() != null && result.getAwayLevelSecondOddsRate().abs().compareTo(new BigDecimal("0.15"))>0)
                        || (result.getHomeLevelFirstOddsRate() !=null && result.getHomeLevelFirstOddsRate().abs().compareTo(new BigDecimal("0.15"))>0)
                        || (result.getHomeLevelSecondOddsRate() !=null && result.getHomeLevelSecondOddsRate().abs().compareTo(new BigDecimal("0.15"))>0)){
                    log.warn("::{}:: {}自动水差/手动赔率变化率不能为空并且绝对值需要介于0-0.15,result:{}",item.getOrderNo(),item.getMarketId(),JSONObject.toJSONString(result));
                    return null;
                }
            }
        } catch (LogicException e) {
            log.error("::{}:: checkItemValid code:{},error :{}",item.getOrderNo(),e.getCode(),e.getMessage(),e);
        }catch (Exception e) {
            log.error("::{}:: checkItemValid error :{}",item.getOrderNo(),e.getMessage(),e);
        }
        return result;
    }





    /*
	校验注单单参数
	 */
    private void checkOrderItemArguments(OrderItem bean) throws LogicException {
        if(bean.getPlayId() == null) throw new LogicException("605", "玩法ID不能为空！");
        if(bean.getMatchId() == null) throw new LogicException("606", "比赛ID不能为空！");
        if(bean.getBetAmount() == null || bean.getBetAmount() == 0) throw new LogicException("609", "BetAmout不能为空！");
        if(bean.getOddsValue() == null || bean.getOddsValue() == 0) throw new LogicException("610", "赔率OddsValue不能为空！");
        if(bean.getPlayOptionsId() == null || bean.getPlayOptionsId() == 0) throw new LogicException("613", "投注项ID不能为空！");
    }

    /**
     * 获取盘口预期赔付最大值
     * @param item
     * @return
     */
    @Override
    public abstract void sumCurrentLoadValue(OrderItem item, OrderBean orderBean);


    public <T> T getTriggerItem(OrderItem item, Class<T> targetClass)  {
        T target = BeanCopyUtils.copyProperties(item, targetClass);
        return target;
    }
    /**
     * 触发调价和平衡值清零
     * @param target
     * @return
     */
    public void triggerForOverLoad(RcsMatchMarketConfig result,ThreewayOverLoadTriggerItem target) {
    	//赔率变更改成同步
        log.warn("::{}::,orderItem.getOrderNo(),{} triggerForOverLoad 限额触发赔率调整，item：{}",result.getMarketId(), this.getClass(),JSONObject.toJSONString(target));
    	marketOddsChangeCalculationService.calculationOddsByOverLoadTrigger(result,target);

    }

    private RcsMatchMarketConfig getMarketConfig(OrderItem item) {
        log.info("getMarketConfig参数:{}",JSONObject.toJSONString(item));
        Long matchId = item.getMatchId();
        long playId = item.getPlayId().longValue();
        RcsMatchMarketConfig config = new RcsMatchMarketConfig();
        config.setMatchId(matchId);
        config.setMarketId(item.getMarketId());
        config.setPlayId(playId);
        config.setSubPlayId(item.getSubPlayId());
        config.setTournamentId(item.getTournamentId());
        if(RcsConstant.BASKETBALL_TWO_PLAYS.contains(item.getPlayId().intValue())){
            config.setMarketIndex(1);
        }else{
            config.setMarketIndex(item.getPlaceNum());
        }

        config.setSportId(item.getSportId());
        // 1-早盘，0-滚球
        if (null != item.getMatchType() && 2 == item.getMatchType()) {
            config.setMatchType(0);
        }else {
            config.setMatchType(item.getMatchType());
        }
        config.setOddsType(item.getPlayOptions());

        RcsMatchMarketConfig conf = getMatchMarketConfig(config,item.getOrderNo());
        if (conf == null) {
            return null;
        }
        Integer dataSource = NumberUtils.INTEGER_ZERO;
        if (3 == item.getMatchType()){
            RcsTradeConfig tradeConfig =getTradeConfig(item.getOrderNo(),matchId,item.getMarketId());
            if (Objects.nonNull(tradeConfig) && Objects.nonNull(tradeConfig.getDataSource())){
                dataSource = tradeConfig.getDataSource();
            }
            //封盘消息
            conf.setCloseMsg(getCloseMsg(item));
        }else {
            dataSource = getDataSource(item.getOrderNo(),matchId, playId);
        }

        conf.setDataSource(dataSource.longValue());
        conf.setSportId(item.getSportId());
        conf.setSubPlayId(item.getSubPlayId());
        conf.setMarketIndex(item.getPlaceNum());
        conf.setMatchType(config.getMatchType());
        conf.setPlayId(config.getPlayId());
        conf.setMatchId(config.getMatchId());
        conf.setOddsType(item.getPlayOptions());
        conf.setMarketId(item.getMarketId());
        return conf;
    }
    /**
     * 获取操盘类型数据
     * */
    private Integer getDataSource(String orderNo,Long matchId,Long playId){
        Integer dataSource = rcsTradeConfigService.getDataSource(matchId, playId);
        log.info("::{}::  matchId{}  playId{}  getDataSource 返回结果{}",orderNo,matchId,playId,dataSource);
        return  dataSource;
    }
    /**
     * 获取最新操盘操作配置表
     * */
   private RcsTradeConfig getTradeConfig(String OrderNo,Long matchId,Long marketId){
       RcsTradeConfig tradeConfig = rcsTradeConfigServiceImpl.getLatestStatusConfig(matchId, TraderLevelEnum.MARKET,marketId);
       log.info("::{}::  matchId{}  marketId{}  getTradeConfig 返回结果{}",OrderNo,matchId,marketId,JSONObject.toJSONString(tradeConfig));
       return  tradeConfig;
   }
   /**
    * 查询操盘限额
    * */
    private RcsMatchMarketConfig getMatchMarketConfig(RcsMatchMarketConfig config,String orderNo){
        RcsMatchMarketConfig conf = rcsMatchMarketConfigService.queryMaxBetAmount(config);
        log.info("::{}::  config{}   getMatchMarketConfig 返回结果{}",orderNo,JSONObject.toJSONString(config),JSONObject.toJSONString(conf));
        return  conf;
    }
    private String getCloseMsg(OrderItem item) {
        HashMap<String,String> hashMap=new HashMap();
        for (LanguageTypeDataEnum languageTypeDataEnum:LanguageTypeDataEnum.values()){
            StringBuilder stringBuilder=new StringBuilder();
            String type = languageTypeDataEnum.getType();
            stringBuilder.append("冠军：");
            stringBuilder.append(item.getMatchName()).append("(").append("matchManageId").append(")-");
            stringBuilder.append(item.getPlayName()).append(":").append(item.getPlayOptionsName()).append("已触发跳水关盘，请及时检查开启");
            hashMap.put(type,stringBuilder.toString());
        }
        return JSONObject.toJSONString(hashMap);
    }

    protected String getUserType(Long userId) {
        // 用户特殊限额类型,0-无,1-标签限额,2-特殊百分比限额,3-特殊单注单场限额,4-特殊vip限额
        String key = RcsConstant.RCS_TRADE_USER_SPECIAL_BET_LIMIT_CONFIG + userId;
        String value = redisClient.hGet(key, "type");
        log.info("跳水跳盘用户类型：key={},field=type,value={}", key, value);
        return value;
    }

    protected BigDecimal getPercentageOfTagVolume(Long userId, OrderBean orderBean) {
        BigDecimal volumePercentage = orderService.getVolumePercentage(orderBean,true);
        return volumePercentage;
    }

    private String getMatchDateExpect(OrderItem item) {
        Long beginTime = System.currentTimeMillis();
        if (3 == item.getMatchType()){
            LambdaQueryWrapper<RcsStandardOutrightMatchInfo> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(RcsStandardOutrightMatchInfo::getId, item.getMatchId())
                    .select(RcsStandardOutrightMatchInfo::getStandrdOutrightMatchBegionTime);
            RcsStandardOutrightMatchInfo standardMatchInfo = rcsStandardOutrightMatchInfoMapper.selectOne(wrapper);
            if (standardMatchInfo == null) {
                throw new RcsServiceException("赛事不存在：" + item.getMatchId());
            }
            beginTime = standardMatchInfo.getStandrdOutrightMatchBegionTime();
        }else {
            LambdaQueryWrapper<StandardMatchInfo> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(StandardMatchInfo::getId, item.getMatchId())
                    .select(StandardMatchInfo::getBeginTime);
            StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectOne(wrapper);
            if (standardMatchInfo == null) {
                throw new RcsServiceException("赛事不存在：" + item.getMatchId());
            }
            beginTime = standardMatchInfo.getBeginTime();
        }
        if (beginTime == null) {
            beginTime = System.currentTimeMillis();
        }
        return DateUtils.getDateExpect(beginTime);
    }

    private Integer executeJumpMarketLua(OrderItem orderItem, RcsMatchMarketConfig config, JumpOddsLuaDto luaDto) {
        log.info("::{}::,executeJumpMarketLua start orderItem={},config={},luaDto={}",orderItem.getOrderNo(),
                JSONObject.toJSONString(orderItem),
                JSONObject.toJSONString(config),
                JSONObject.toJSONString(luaDto));
        Integer isJumpMarket = NumberUtils.INTEGER_ZERO;
        if (!SportIdEnum.isBasketball(orderItem.getSportId()) || !(Basketball.Main.isHandicapOrTotal(orderItem.getPlayId().longValue()))) {
            log.warn("::{}::,篮球主要玩法让分和大小才支持跳盘",orderItem.getOrderNo());
            return isJumpMarket;
        }
        String dateExpect = luaDto.getDateExpect();
        if (StringUtils.isBlank(dateExpect)) {
            dateExpect = getMatchDateExpect(orderItem);
            luaDto.setDateExpect(dateExpect);
            orderItem.setDateExpect(dateExpect);
        }

        try {
            List<String> keys = Lists.newArrayList(orderItem.getOrderNo() + "{" + luaDto.getKeySuffix() + "}");
            List<String> args = luaDto.getJumpMarketLuaArgs();
            log.info("::{}::,jumpMarket.lua脚本入参：jumpMarketShakey={},keys={},args={}", orderItem.getOrderNo(),jumpMarketShakey, keys, args);
            Object luaResult = redisClient.evalsha(jumpMarketShakey, keys, args);
            String jsonString = JSONObject.toJSONString(luaResult);
            log.info("::{}::,jumpMarket.lua脚本返回结果：luaResult={}",orderItem.getOrderNo(), jsonString);
            JSONArray luaResultArray = JSONObject.parseArray(jsonString);
            // 是否跳盘，0-不跳盘，1-跳盘
            isJumpMarket = luaResultArray.getIntValue(0);
            if (isJumpMarket == 1) {
                // 跳盘并清跳盘平衡值
                jumpMarket(orderItem, config, luaDto, luaResultArray);
                log.info("跳盘并清跳盘平衡值isJumpMarket:{}",JSONObject.toJSONString(config));
            }
            jumpMarketBalance(orderItem, luaDto, luaResultArray);
        } catch (Exception e) {
            log.error("::{}::跳盘异常{}",orderItem.getOrderNo(),e.getMessage(), e);
        }
        return isJumpMarket;
    }

    private void jumpMarket(OrderItem orderItem, RcsMatchMarketConfig config, JumpOddsLuaDto luaDto, JSONArray luaResultArray) {
        log.info("jumpMarket_orderItem:{},config:{},luaDto:{},luaResultArray:{}",JSONObject.toJSONString(orderItem),JSONObject.toJSONString(config),
                JSONObject.toJSONString(luaDto),
                JSONObject.toJSONString(luaResultArray));
        // 累计跳盘/一级跳盘 次数
        int oneLevelTimes = luaResultArray.getIntValue(1);
        // 单枪跳盘/二级跳盘 次数
        int twoLevelTimes = luaResultArray.getIntValue(2);
        String oddsType = orderItem.getPlayOptions();
        // true-大小，false-让分
        boolean isTotal = OddsTypeEnum.isTotalOddsType(oddsType);
        boolean isHome = OddsTypeEnum.isHomeOddsType(oddsType);
        BigDecimal home;
        BigDecimal away;
        if (luaDto.getOddChangeRule() == 1 && luaDto.getIsMultipleJumpMarket() == 1) {
            // 累计差值跳盘，倍数跳盘
            home = config.getHomeLevelSecondMarketRate().multiply(new BigDecimal(twoLevelTimes)).add(config.getHomeLevelFirstMarketRate().multiply(new BigDecimal(oneLevelTimes)));
            away = config.getAwayLevelSecondMarketRate().multiply(new BigDecimal(twoLevelTimes)).add(config.getAwayLevelFirstMarketRate().multiply(new BigDecimal(oneLevelTimes)));
        } else {
            if (luaDto.getOddChangeRule() == 0) {
                // 累计/单枪跳分
                home = oneLevelTimes > 0 ? config.getHomeCumulativeMarketRate() : config.getHomeSingleMarketRate();
                away = oneLevelTimes > 0 ? config.getAwayCumulativeMarketRate() : config.getAwaySingleMarketRate();
            } else {
                // 累计差值跳分
                home = oneLevelTimes > 0 ? config.getHomeLevelFirstMarketRate() : config.getHomeLevelSecondMarketRate();
                away = oneLevelTimes > 0 ? config.getAwayLevelFirstMarketRate() : config.getAwayLevelSecondMarketRate();
            }
        }
        BigDecimal marketAdjustRange = isHome ? home : away;
        BigDecimal marketAdjustSymbol;
        StandardSportMarket mainMarketInfo = standardSportMarketService.selectMainMarketInfo(orderItem.getMatchId(),orderItem.getPlayId().longValue(),orderItem.getSubPlayId());
        if (ObjectUtils.isEmpty(mainMarketInfo) || StringUtils.isBlank(mainMarketInfo.getAddition1())) {
            log.error("::{}::,盘口值为空:{}", orderItem.getOrderNo(),JSONObject.toJSONString(config));
            return;
        }
        BigDecimal marketValue = new BigDecimal(mainMarketInfo.getAddition1());
        if (isTotal) {
            marketAdjustSymbol = isHome ? BigDecimal.ONE : BigDecimal.ONE.negate();
        } else {
            if (orderItem.getPlaceNum() > 1) {
                marketValue = StringUtil.toBigDecimal(mainMarketInfo.getAddition1());
            }
            // true-主让，false-客让
            boolean isHomeHandicap = marketValue.compareTo(BigDecimal.ZERO) <= 0;
            marketAdjustSymbol = (isHomeHandicap && isHome) || (!isHomeHandicap && !isHome) ? BigDecimal.ONE : BigDecimal.ONE.negate();
        }

        if (marketAdjustRange == null) {
            marketAdjustRange = BigDecimal.ZERO;
        }
        if (marketAdjustRange.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("::{}::,盘口值调整幅度为0，不自动跳盘：{}",orderItem.getOrderNo(), marketAdjustRange);
            return;
        }
        log.info("::{}::,计算后的marketAdjustRange={}，marketAdjustSymbol={}",orderItem.getOrderNo(),marketAdjustRange.toString(),marketAdjustSymbol.toString());
        config.setMarketAdjustRange(marketAdjustRange.multiply(marketAdjustSymbol));
        // 原始盘口值
        BigDecimal marketHeadGap = ObjectUtils.isEmpty(mainMarketInfo.getMarketHeadGap()) ? new BigDecimal(NumberUtils.DOUBLE_ZERO) : mainMarketInfo.getMarketHeadGap();
        config.setMarketHeadGap(marketAdjustSymbol);
        // 原始盘口值<=0 + 就是加盘口差，- 就是减盘口差;原始盘口值>0,+ 就是减盘口差，- 就是加盘口差
        if (marketValue.compareTo(new BigDecimal(NumberUtils.DOUBLE_ZERO)) <= NumberUtils.INTEGER_ZERO) {
            config.setMarketHeadGap(marketAdjustSymbol.multiply(new BigDecimal(NumberUtils.INTEGER_MINUS_ONE)));
        }
        BigDecimal totalChange = marketAdjustRange.multiply(config.getMarketHeadGap());
        marketHeadGap = marketHeadGap.add(totalChange);
        log.info("::{}::,marketHeadGap={}",orderItem.getOrderNo(),marketHeadGap.toString());
        BigDecimal newMarketValue = TradeVerificationService.getNewMainMarketValue(marketValue,totalChange,marketAdjustRange,mainMarketInfo.getAddition5(),config.getPlayId()).stripTrailingZeros();

        log.info("::{}::,新盘口差={},新盘口值={}", orderItem.getOrderNo(),marketHeadGap, newMarketValue);
        if (YesNoEnum.isYes(luaResultArray.getIntValue(0))
                && NumberUtils.INTEGER_TWO.toString().equals(orderItem.getMatchType())
                && (RcsConstant.MAIN_BASKETBALL_TOTAL.contains(config.getPlayId()) || RcsConstant.BASKETBALL_X_MARKET_PLAYS.contains(config.getPlayId().intValue()))) {
            // 滚球大小玩法，需要校验比分，盘口值必须大于比分之和
            int scoreSum = tradeVerificationService.getBasketballScoreSum(config.getMatchId(), config.getPlayId());
            log.info("::{}::,当前比分之和：{}" , orderItem.getOrderNo(),scoreSum);
            if (newMarketValue.compareTo(new BigDecimal(scoreSum)) <= 0) {
                throw new RcsServiceException(String.format("大小盘盘口值小于等于当前比分之和，跳盘失败：newMarketValue=%s,scoreSum=%s", newMarketValue, scoreSum));
            }
        }
        config.setMarketHeadGap(marketHeadGap);
//
//        String betStage;
//        Integer matchType = orderItem.getMatchType();
//        if (matchType != null && matchType == 2) {
//            betStage = "live";
//        } else {
//            betStage = "pre";
//        }
//        JumpMarketMsgDto msgDto = new JumpMarketMsgDto();
//        msgDto.setSportId(orderItem.getSportId());
//        msgDto.setTournamentId(orderItem.getTournamentId());
//        msgDto.setMatchId(orderItem.getMatchId());
//        msgDto.setPlayId(orderItem.getPlayId());
//        msgDto.setMarketId(orderItem.getMarketId());
//        msgDto.setPlaceNum(orderItem.getPlaceNum());
//        msgDto.setMarketAdjustRange(marketAdjustRange);
//        msgDto.setMarketAdjustSymbol(marketAdjustSymbol);
//        msgDto.setBetStage(betStage);
//        msgDto.setMarketType(config.getMarketType());
//        msgDto.setDateExpect(orderItem.getDateExpect());
//        msgDto.setOddsType(oddsType);
//        producerSendMessageUtils.sendMessage("RCS_TRADE_JUMP_MARKET", msgDto.generateTag(), msgDto.generateKey(), msgDto);
    }

    private void jumpMarketBalance(OrderItem orderItem, JumpOddsLuaDto luaDto, JSONArray luaResultArray) {
        log.info("jumpMarketBalance----->{}",JSONObject.toJSONString(orderItem));
        // 是否跳盘，0-否，1-是
        int isJumpMarket = luaResultArray.getIntValue(0);
        // 当前投注项累计
        BigDecimal jumpMarketCurrent = StringUtil.toBigDecimal(luaResultArray.getString(3));
        // 所有投注项累计
        BigDecimal jumpMarketTotal = StringUtil.toBigDecimal(luaResultArray.getString(4));
        // 倍跳余值
        BigDecimal jumpMarketDiff = StringUtil.toBigDecimal(luaResultArray.getString(5));
        BalanceVo balanceVo = new BalanceVo(orderItem.getSportId().longValue(), orderItem.getMatchId(), orderItem.getPlayId().longValue(), orderItem.getPlaceNum(), orderItem.getMarketId(),orderItem.getSubPlayId());
        if (YesNoEnum.isYes(isJumpMarket)) {
            if (luaDto.getOddChangeRule() == 1 && luaDto.getIsMultipleJumpMarket() == 1) {
                // 累计差值跳盘，倍数跳盘，lua脚本中已清平衡值，平衡值推送WS
                balanceVo.setJumpMarketBalance(jumpMarketDiff.longValue());
                balanceVo.setJumpMarketOddsType(orderItem.getPlayOptions());
                balanceVo.setGlobalId(balanceVo.generateKey());
                balanceVo.setSubPlayId(orderItem.getSubPlayId());
                producerSendMessageUtils.sendMessage(MqConstants.REALTIME_SYNC_BALANCE_TOPIC, MqConstants.REALTIME_SYNC_BALANCE_TAG, balanceVo.getGlobalId(), balanceVo);
            } else {
                // 清除玩法下所有位置平衡值，并推送WS
                balanceService.clearAllBalance(BalanceTypeEnum.JUMP_MARKET.getType(), orderItem.getSportId().longValue(), orderItem.getMatchId(), orderItem.getPlayId().longValue(), orderItem.getDateExpect(),orderItem.getSubPlayId());
            }
        } else {
            // 当前位置最新平衡值推送WS
            BigDecimal other = jumpMarketTotal.subtract(jumpMarketCurrent);
            if (jumpMarketCurrent.compareTo(other) >= 0) {
                long balance = jumpMarketCurrent.subtract(other).longValue();
                balanceVo.setJumpMarketBalance(balance);
                balanceVo.setJumpMarketOddsType(orderItem.getPlayOptions());
            } else {
                long balance = other.subtract(jumpMarketCurrent).longValue();
                balanceVo.setJumpMarketBalance(balance);
                balanceVo.setJumpMarketOddsType(getOtherSideOddsType(orderItem.getPlayId().longValue(), orderItem.getPlayOptions()));
            }
            balanceVo.setGlobalId(balanceVo.generateKey());
            producerSendMessageUtils.sendMessage(MqConstants.REALTIME_SYNC_BALANCE_TOPIC, MqConstants.REALTIME_SYNC_BALANCE_TAG, balanceVo.getGlobalId(), balanceVo);
        }
    }

    private String getOtherSideOddsType(Long playId, String oddsType) {
        if (Basketball.isHandicap(playId)) {
            if (OddsTypeEnum.isHome(oddsType)) {
                return OddsTypeEnum.AWAY;
            } else if (OddsTypeEnum.isAway(oddsType)) {
                return OddsTypeEnum.HOME;
            }
        }
        if (Basketball.isTotal(playId)) {
            if (OddsTypeEnum.isOver(oddsType)) {
                return OddsTypeEnum.UNDER;
            } else if (OddsTypeEnum.isUnder(oddsType)) {
                return OddsTypeEnum.OVER;
            }
        }
        if (Basketball.isOddEven(playId)) {
            if (OddsTypeEnum.isOdd(oddsType)) {
                return OddsTypeEnum.EVEN;
            } else if (OddsTypeEnum.isEven(oddsType)) {
                return OddsTypeEnum.ODD;
            }
        }
        if (Basketball.isYesNo(playId)) {
            if (OddsTypeEnum.isYes(oddsType)) {
                return OddsTypeEnum.NO;
            } else if (OddsTypeEnum.isNo(oddsType)) {
                return OddsTypeEnum.YES;
            }
        }
        if (OddsTypeEnum.isHome(oddsType)) {
            return OddsTypeEnum.AWAY;
        } else if (OddsTypeEnum.isAway(oddsType)) {
            return OddsTypeEnum.HOME;
        }
        return oddsType;
    }

}
