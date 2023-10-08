package com.panda.sport.rcs.mgr.service.impl.odds;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.util.UuidUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.common.collect.Lists;
import com.panda.merge.api.IOutrightTradeConfigApi;
import com.panda.merge.dto.MarketMarginDtlDTO;
import com.panda.merge.dto.MarketMarginGapDtlDTO;
import com.panda.merge.dto.OutrightTradeOddsConfigDTO;
import com.panda.merge.dto.OutrightTradeProbabilityConfigDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.TradeMarketAutoDiffConfigItemDTO;
import com.panda.merge.dto.TradeMarketMarginGapConfigDTO;
import com.panda.sport.data.rcs.dto.ThreewayOverLoadTriggerItem;
import com.panda.sport.data.rcs.dto.TwowayDoubleOverLoadTriggerItem;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.mapper.RcsMatchMarketConfigMapper;
import com.panda.sport.rcs.mapper.RcsStandardOutrightMatchInfoMapper;
import com.panda.sport.rcs.mapper.StandardSportMarketOddsMapper;
import com.panda.sport.rcs.mapper.statistics.RcsMatchPlayConfigMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainMapper;
import com.panda.sport.rcs.mgr.calculator.service.AmountLimitServiceAdapter;
import com.panda.sport.rcs.mgr.constant.RcsCacheContant;
import com.panda.sport.rcs.mgr.service.impl.odds.api.OddsCalcApi;
import com.panda.sport.rcs.mgr.service.impl.odds.api.OddsPublicMethodApi;
import com.panda.sport.rcs.mgr.utils.MarketUtils;
import com.panda.sport.rcs.mgr.wrapper.IRcsMatchMarketConfigService;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.RcsMatchMarketProbabilityConfig;
import com.panda.sport.rcs.pojo.RcsStandardOutrightMatchInfo;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;
import com.panda.sport.rcs.utils.DataRealtimeApiUtils;
import com.panda.sport.rcs.vo.odds.MatchMarketPlaceConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.HEAD;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 自动跳赔计算
 * @ClassName: AutomaticOddsCalcService
 * @Description: TODO
 * @author black
 * @date 2021年1月14日 下午5:32:38
 *
 */
@Component
@Slf4j
public class AutomaticOddsCalcService implements OddsCalcApi{

	@Autowired
	private StandardSportMarketOddsMapper sportMarketOddsMapper;

	@Autowired
	private OddsPublicMethodApi oddsPublicMethodApi;
	@Autowired
	public RedisClient redisClient;
	@Autowired
	private RcsMatchMarketConfigMapper rcsMatchMarketConfigMapper;
	@Reference(check = false, lazy = true, retries = 0, timeout = 5000)
	private IOutrightTradeConfigApi iOutrightTradeConfigApi;

	@Override
	@Transactional
	public Boolean maginCalc(RcsMatchMarketConfig config, ThreewayOverLoadTriggerItem item) {
		//根据marketId查询对应盘口所有赔率
		List<StandardSportMarketOdds> oddsVoList = oddsPublicMethodApi.queryOddsVoList(config);
//		List<StandardSportMarketOdds> oddsVoList = sportMarketOddsMapper.queryMarketOddsByMarket(item.getMarketId());
		log.info("::{}::,数据库盘口赔率=={}",config.getMarketId(),JSONObject.toJSONString(oddsVoList));
		if (CollectionUtils.isEmpty(oddsVoList)) {
//			log.info("该盘口没有赔率数据{}", JSONObject.toJSONString(item));
			return Boolean.FALSE;
		}
		if (3 == config.getMatchType()){
			championCalculationOddsByMargin(config, item, oddsVoList);
			return Boolean.TRUE;
		}
		oddsPublicMethodApi.setDefaultAnchor(oddsVoList);
//	        String position = oddsPublicMethodApi.getThreePositon(oddsVoList,item.getPlayOptionsId());
		log.info("AO篮球新增玩法处理赔率config={},item={}",JSONObject.toJSONString(config),JSONObject.toJSONString(item));
		//篮球独赢处理
		if (RcsConstant.ALL_SPORT_MOST_PLAY.contains(config.getPlayId().intValue())||RcsConstant.BASKETBALL_AO.contains(config.getPlayId().intValue())){
			List<RcsMatchMarketProbabilityConfig> probabilitys = rcsMatchMarketConfigMapper.getOddsTypeProbabilitys(config);
			log.info("probabilitys={}",JSONObject.toJSONString(probabilitys));
			List<RcsMatchMarketProbabilityConfig> ps = Lists.newArrayList();
			// margin优化后
			TradeMarketMarginGapConfigDTO gapConfigDTO = new TradeMarketMarginGapConfigDTO();
			gapConfigDTO.setStandardCategoryId(config.getPlayId());
			gapConfigDTO.setStandardMatchInfoId(config.getMatchId());
			gapConfigDTO.setPlaceNum(config.getMarketIndex());
			List<MarketMarginGapDtlDTO> marginGapDtlDTOList = Lists.newArrayList();
			for (StandardSportMarketOdds odds : oddsVoList) {
				if (odds.getId().longValue() == item.getPlayOptionsId()) {
					MarketMarginGapDtlDTO dtlDTO = new MarketMarginGapDtlDTO();
					BigDecimal probability = oddsPublicMethodApi.getOddsTypeProbabilitys(odds.getOddsType(),probabilitys);
					probability = probability.add(config.getHomeLevelFirstOddsRate());
					dtlDTO.setOddsType(odds.getOddsType());
					if (probability.abs().compareTo(new BigDecimal("30")) == 1){
						return Boolean.TRUE;
					}
					dtlDTO.setProbability(probability.doubleValue());
					marginGapDtlDTOList.add(dtlDTO);
					RcsMatchMarketProbabilityConfig probabilityConfig = oddsPublicMethodApi.buildProbability(config,odds.getOddsType(),probability);
					ps.add(probabilityConfig);
					break;
				}
			}
			gapConfigDTO.setChildStandardCategoryId(Long.parseLong(config.getSubPlayId()));
			gapConfigDTO.setList(marginGapDtlDTOList);
			rcsMatchMarketConfigMapper.insertOrUpdateMarketProbabilityConfig(ps);
			oddsPublicMethodApi.putTradeMarginProbabilityGap(gapConfigDTO);
			log.info("A模式调用融合接口，入参：{}",JSONObject.toJSONString(gapConfigDTO));
		}else if (!CollectionUtils.isEmpty(oddsVoList) && oddsVoList.size() == 2){
			// 两项盘逻辑
			calcuTwoOddsTypeByMargin(config,item,oddsVoList);

		} else{
			singleCalculationOddsByMargin(config, item, oddsVoList);
		}
		return Boolean.TRUE;
	}
	/**
	 * @Description   //冠军玩法
	 * @Param [config, item, oddsVoList]
	 * @Author  sean
	 * @Date   2021/6/12
	 * @return void
	 **/
	private void championCalculationOddsByMargin(RcsMatchMarketConfig config, ThreewayOverLoadTriggerItem item, List<StandardSportMarketOdds> oddsVoList) {
		log.info("::{}::,冠军玩法------>championCalculationOddsByMargin config = {},item = {},oddsVoList = {}",config.getMarketId(),
				JSONObject.toJSONString(config),JSONObject.toJSONString(item),JSONObject.toJSONString(oddsVoList));
		List<RcsMatchMarketProbabilityConfig> probabilitys = rcsMatchMarketConfigMapper.getOddsTypeProbabilitys(config);
		List<RcsMatchMarketProbabilityConfig> ps = Lists.newArrayList();
		// margin优化后
		OutrightTradeProbabilityConfigDTO gapConfigDTO = new OutrightTradeProbabilityConfigDTO();
		gapConfigDTO.setStandardMatchId(config.getMatchId());
		gapConfigDTO.setStandardMarketId(config.getMarketId());
		RcsMatchMarketProbabilityConfig probabilityConfig = null;
		for (StandardSportMarketOdds odds : oddsVoList) {
			if (odds.getId().longValue() == item.getPlayOptionsId()) {
				probabilityConfig = oddsPublicMethodApi.getChampionProbabilitys(odds.getOddsType(),probabilitys);
				// 超过30%不跳赔
				BigDecimal probability = probabilityConfig.getProbability().add(config.getHomeLevelFirstOddsRate());
				if (probability.abs().compareTo(new BigDecimal("30")) == 1){
					return ;
				}
				RcsMatchMarketProbabilityConfig championProbabilityConfig = oddsPublicMethodApi.buildProbability(config,odds.getOddsType(),probability);
				championProbabilityConfig.setOddsChangeTimes(probabilityConfig.getOddsChangeTimes());
				// 调了3次需要清配置
				if (probabilityConfig.getOddsChangeTimes() >= 3){
					championProbabilityConfig.setOddsChangeTimes(NumberUtils.INTEGER_ZERO);
//					championProbabilityConfig.setProbability(BigDecimal.ZERO);
				}
				ps.add(championProbabilityConfig);
				gapConfigDTO.setProbability(probability.doubleValue());
				gapConfigDTO.setStandardMarketOddsId(odds.getId());
				break;
			}
		}
		rcsMatchMarketConfigMapper.insertOrUpdateMarketProbabilityConfig(ps);
		DataRealtimeApiUtils.handleApi(gapConfigDTO, new DataRealtimeApiUtils.ApiCall() {
			@Override
			@Trace
			public <R> Response<R> callApi(Request request) {
				return iOutrightTradeConfigApi.putOutrightTradeProbabilityConfig(request);
			}
		});
		// 超过3次需要封盘
		if (probabilityConfig.getOddsChangeTimes() >= 3){
			oddsPublicMethodApi.closeOddType(gapConfigDTO);
			oddsPublicMethodApi.sendChampionCloseMarketMessage(config,item);
		}
	}

	/**
	 * @Description   //独赢margin算法优化
	 * @Param [config, item, oddsVoList]
	 * @Author  sean
	 * @Date   2021/5/4
	 * @return void
	 **/
	private void singleCalculationOddsByMargin(RcsMatchMarketConfig config, ThreewayOverLoadTriggerItem item, List<StandardSportMarketOdds> oddsVoList) {
		List<RcsMatchMarketProbabilityConfig> probabilitys = rcsMatchMarketConfigMapper.getOddsTypeProbabilitys(config);
		List<RcsMatchMarketProbabilityConfig> ps = Lists.newArrayList();
		log.info("::{}::,独赢margin优化------>singleCalculationOddsByMargin config = {},item = {},oddsVoList = {}",config.getMarketId(),
				JSONObject.toJSONString(config),JSONObject.toJSONString(item),JSONObject.toJSONString(oddsVoList));
		TradeMarketMarginGapConfigDTO gapConfigDTO = new TradeMarketMarginGapConfigDTO();
		gapConfigDTO.setStandardCategoryId(config.getPlayId());
		gapConfigDTO.setStandardMatchInfoId(config.getMatchId());
		gapConfigDTO.setPlaceNum(config.getMarketIndex());
		List<MarketMarginGapDtlDTO> marginGapDtlDTOList = Lists.newArrayList();
		Integer linkageMode = oddsPublicMethodApi.getLinkeAgeMode(config,oddsVoList);
		for (StandardSportMarketOdds odds : oddsVoList){
			MarketMarginGapDtlDTO dtlDTO = new MarketMarginGapDtlDTO();
			BigDecimal probability = oddsPublicMethodApi.getOddsTypeProbabilitys(odds.getOddsType(),probabilitys);
			dtlDTO.setOddsType(odds.getOddsType());
			if (ObjectUtils.isNotEmpty(config.getMargin())){
				dtlDTO.setMargin(config.getMargin().doubleValue());
			}
			dtlDTO.setAnchor(odds.getAnchor());
			if (item.getPlayOptionsId().longValue() == odds.getId()){
				probability = probability.add(config.getHomeLevelFirstOddsRate());
			}else if (linkageMode.intValue() > 0){
				probability = probability.subtract(config.getHomeLevelFirstOddsRate().divide(BigDecimal.valueOf(oddsVoList.size()-1),2,BigDecimal.ROUND_DOWN));
			}
			if (probability.abs().compareTo(new BigDecimal("30")) == 1){
				return ;
			}
//			probability = oddsPublicMethodApi.checkProbability(probability);
			dtlDTO.setProbability(probability.doubleValue());
			RcsMatchMarketProbabilityConfig probabilityConfig = oddsPublicMethodApi.buildProbability(config,odds.getOddsType(),probability);
			ps.add(probabilityConfig);
			marginGapDtlDTOList.add(dtlDTO);
		}
		gapConfigDTO.setLinkageMode(linkageMode);
		gapConfigDTO.setList(marginGapDtlDTOList);
//		if (RcsConstant.FOOTBALL_X_EU_PLAYS.contains(config.getPlayId().intValue()) ||
//				RcsConstant.BASKETBALL_X_EU_PLAYS.contains(config.getPlayId().intValue())){
//			gapConfigDTO.setChildStandardCategoryId(Long.parseLong(config.getSubPlayId()));
//		}
		if (ObjectUtils.isNotEmpty(config.getSubPlayId())){
			gapConfigDTO.setChildStandardCategoryId(Long.parseLong(config.getSubPlayId()));
		}
		rcsMatchMarketConfigMapper.insertOrUpdateMarketProbabilityConfig(ps);
		oddsPublicMethodApi.putTradeMarginGap(gapConfigDTO);
	}
	/**
	 * @Description   //两项盘计算margin
	 * @Param [config, item]
	 * @Author  sean
	 * @Date   2021/10/16
	 * @return void
	 **/
	private void calcuTwoOddsTypeByMargin(RcsMatchMarketConfig config, ThreewayOverLoadTriggerItem item,List<StandardSportMarketOdds> oddsVoList) {
		log.info("calcuTwoOddsTypeByMargin:{}",JSONObject.toJSONString(item));
		String oddsType= oddsPublicMethodApi.getBasketBallDownOddsType(oddsVoList);
		BigDecimal water = new BigDecimal(item.getAwayAutoChangeRate().toString());
		if (!oddsType.equalsIgnoreCase(config.getOddsType())){
			config.setHomeLevelFirstOddsRate(config.getHomeLevelFirstOddsRate().multiply(BigDecimal.valueOf(-1)));
		}
		// 其他球种水差
		if (RcsConstant.OTHER_CAN_TRADE_SPORT.contains(config.getSportId()) || SportIdEnum.isBasketball(config.getSportId())){
			water = config.getHomeLevelFirstOddsRate().divide(new BigDecimal(BaseConstants.MULTIPLE_BET_AMOUNT_VALUE),4,BigDecimal.ROUND_HALF_UP);

			List<MatchMarketPlaceConfig> playPlaceConfigList =  oddsPublicMethodApi.queryPlaceWaterConfigList(config,water);
			log.info("AO_篮球calcuTwoOddsTypeByMargin：{}",JSONObject.toJSONString(playPlaceConfigList));
			oddsPublicMethodApi.sendPlayWaterConfigApi(item, config,oddsType,playPlaceConfigList);
		}else {
			water = water.add(config.getHomeLevelFirstOddsRate().divide(new BigDecimal(BaseConstants.MULTIPLE_BET_AMOUNT_VALUE),4,BigDecimal.ROUND_HALF_UP));
			if (!SportIdEnum.isFootball(config.getSportId())){
				if (water.abs().compareTo(new BigDecimal("0.03")) == 1){
					log.info("::{}::,超过3不调分",config.getMarketId());
					return;
				}
			}else {
				water = oddsPublicMethodApi.checkWaterValue(water);
			}
			footBallSendWaterToDataCenter(config, item, oddsVoList, oddsType, water);
		}
//		water = oddsPublicMethodApi.checkWaterValue(water);
		item.setAwayAutoChangeRate(water.doubleValue());
//		if (SportIdEnum.isFootball(config.getSportId())){
//			// 水差推送到融合
//			footBallSendWaterToDataCenter(config, item, oddsVoList, oddsType, water);
//		}else {
//
//			List<MatchMarketPlaceConfig> playPlaceConfigList =  oddsPublicMethodApi.queryPlaceWaterConfigList(config,water);
//
//			oddsPublicMethodApi.sendPlayWaterConfigApi(item, config,oddsType,playPlaceConfigList);
//		}
	}

	/**
	 * @Description   // 之前的margin算法
	 * @Param [config, item, oddsVoList, position]
	 * @Author  sean
	 * @Date   2021/5/4
	 * @return void
	 **/
	private void oldCalculationOddsByMargin(RcsMatchMarketConfig config, ThreewayOverLoadTriggerItem item, List<StandardSportMarketOdds> oddsVoList, String position) {
		BigDecimal homeMargin = item.getHomeMargin();
		BigDecimal awayMargin = item.getAwayMargin();
		BigDecimal tieMargin = item.getTieMargin();
		BigDecimal oddsRate = item.getHomeLevelFirstOddsRate();
		// 平局触发跳赔，平局+margin变化；主胜客胜-margin变化/2
		if (position.equalsIgnoreCase(BaseConstants.ODD_TYPE_TIE)){
			BigDecimal newHomeMargin = homeMargin.subtract(oddsRate.divide(BigDecimal.valueOf(2),2,BigDecimal.ROUND_DOWN));
			BigDecimal newTieMargin = tieMargin.add(oddsRate);
			BigDecimal newAwayMargin = item.getMargin().multiply(BigDecimal.valueOf(3)).subtract(newHomeMargin).subtract(newTieMargin);
			item.setHomeMargin(newHomeMargin);
			item.setAwayMargin(newAwayMargin);
			item.setTieMargin(newTieMargin);
			// 非平局触发跳赔，只有主胜和客胜需要跳赔
		}else if (position.equalsIgnoreCase(BaseConstants.ODD_TYPE_HOME)){
			BigDecimal newHomeMargin = homeMargin.add(oddsRate);
			BigDecimal newAwayMargin = awayMargin.subtract(oddsRate);
			item.setHomeMargin(newHomeMargin);
			item.setAwayMargin(newAwayMargin);
		}else if (position.equalsIgnoreCase(BaseConstants.ODD_TYPE_AWAY)){
			BigDecimal newHomeMargin = homeMargin.subtract(oddsRate);
			BigDecimal newAwayMargin = awayMargin.add(oddsRate);
			item.setHomeMargin(newHomeMargin);
			item.setAwayMargin(newAwayMargin);
		}

		List<MarketMarginDtlDTO> waterList = new ArrayList<MarketMarginDtlDTO>();

		//独赢盘会传多个margin 两项盘只需要一项
		for(StandardSportMarketOdds odd : oddsVoList){
			MarketMarginDtlDTO margin = new MarketMarginDtlDTO();
			margin.setOddsType(odd.getOddsType());
			switch(odd.getOddsType()){
				case "1":
					waterList.add(oddsPublicMethodApi.buildMargainList(odd.getOddsType(), Optional.ofNullable(item.getHomeMargin()).orElse(config.getMargin())));
					config.setHomeMargin(item.getHomeMargin());
					break;
				case "2":
					waterList.add(oddsPublicMethodApi.buildMargainList(odd.getOddsType(), Optional.ofNullable(item.getAwayMargin()).orElse(config.getMargin())));
					config.setAwayMargin(item.getAwayMargin());
					break;
				case "X":
					waterList.add(oddsPublicMethodApi.buildMargainList(odd.getOddsType(), Optional.ofNullable(item.getTieMargin()).orElse(config.getMargin())));
					config.setTieMargin(item.getTieMargin());
					break;
				case "None":
					waterList.add(oddsPublicMethodApi.buildMargainList(odd.getOddsType(), Optional.ofNullable(item.getTieMargin()).orElse(config.getMargin())));
					config.setTieMargin(item.getTieMargin());
					break;
				default:
					waterList.add(oddsPublicMethodApi.buildMargainList(odd.getOddsType(), config.getMargin()));
			}
		}
		oddsPublicMethodApi.updateMarginAndAutoChangeOdds(item,config);
		oddsPublicMethodApi.sendMargainConfigApi(waterList, item);
	}

	/**
	 * 计算水差
	 */
	@Override
	@Transactional
	public Boolean waterCalc(RcsMatchMarketConfig config, ThreewayOverLoadTriggerItem twoWayDouble) {
		TwowayDoubleOverLoadTriggerItem overLoadTriggerItem = (TwowayDoubleOverLoadTriggerItem) twoWayDouble;

		log.info("::{}::,automatic赔率变更计算入参waterCalc：{}",config.getMarketId(), JSONObject.toJSONString(overLoadTriggerItem));
		//判断必要参数是否齐全
		Boolean object = oddsPublicMethodApi.checkTwoWayForNull(overLoadTriggerItem);
		if (!object) {
			log.info("::{}::,缺少必要参数：{}", config.getMarketId(), JSONObject.toJSONString(overLoadTriggerItem));
			return Boolean.FALSE;
		}

		//根据marketId查询对应盘口所有赔率
		List<StandardSportMarketOdds> oddsVoList = oddsPublicMethodApi.queryOddsVoList(config);
//		List<StandardSportMarketOdds> oddsVoList = sportMarketOddsMapper.queryMarketOddsByMarket(overLoadTriggerItem.getMarketId());
		log.info("::{}::,该盘口赔率数据{}", config.getMarketId(),JSONObject.toJSONString(oddsVoList));
		if (CollectionUtils.isEmpty(oddsVoList)) {
			return Boolean.FALSE;
		}

		String position = oddsPublicMethodApi.getTwoPositon(oddsVoList,overLoadTriggerItem.getPlayOptionsId(),config);
		log.info("::{}::,该盘口位置数据{}", config.getMarketId(),position);
		BigDecimal waterRate = BigDecimal.ZERO;
		if (position.equalsIgnoreCase(BaseConstants.ODD_TYPE_HOME)){
			waterRate = oddsPublicMethodApi.getHomeOddsRate(overLoadTriggerItem,config);
			log.info("::{}::,该盘口位置水差rate1{}", config.getMarketId(),waterRate);
		}else if (position.equalsIgnoreCase(BaseConstants.ODD_TYPE_AWAY)){
			waterRate = oddsPublicMethodApi.getAwayOddsRate(overLoadTriggerItem,config).multiply(new BigDecimal(NumberUtils.LONG_MINUS_ONE));
			log.info("::{}::,该盘口位置水差rate1{}", config.getMarketId(),waterRate);
		}
		String oddsType = oddsPublicMethodApi.getOddsType(oddsVoList,config);
		//计算水差
		BigDecimal waterValue = new BigDecimal(overLoadTriggerItem.getAwayAutoChangeRate()==null ? "0" : overLoadTriggerItem.getAwayAutoChangeRate().toString());
		log.info("计算水差waterValue:{}",waterValue);
		//篮球需要跳玩法水差
		if (RcsConstant.OTHER_CAN_TRADE_SPORT.contains(config.getSportId()) || SportIdEnum.isBasketball(config.getSportId())) {
			//篮球水差固定下盘
			oddsType = oddsPublicMethodApi.getBasketBallDownOddsType(oddsVoList);
			List<MatchMarketPlaceConfig> playPlaceConfigList =  oddsPublicMethodApi.queryPlaceWaterConfigList(config,waterRate);

			oddsPublicMethodApi.sendPlayWaterConfigApi(overLoadTriggerItem, config,oddsType,playPlaceConfigList);
			log.info("篮球需要跳玩法水差:overLoadTriggerItem{},config:{},oddsType:{},playPlaceConfigList:{}",JSONObject.toJSONString(overLoadTriggerItem),JSONObject.toJSONString(config),oddsType,playPlaceConfigList);
			return Boolean.TRUE;
		}
		waterValue = waterValue.add(waterRate);
		// 其他球种统计
		if (!SportIdEnum.isFootball(config.getSportId())){
			if (isOverChangeTimes(config, oddsType)) return Boolean.TRUE;
		}else {
			waterValue = oddsPublicMethodApi.checkWaterValue(waterValue);
		}
//		waterValue = oddsPublicMethodApi.checkWaterValue(waterValue);
		// 水差推送到融合
		footBallSendWaterToDataCenter(config, overLoadTriggerItem, oddsVoList, oddsType, waterValue);

		return Boolean.TRUE;
	}

	private void footBallSendWaterToDataCenter(RcsMatchMarketConfig config, ThreewayOverLoadTriggerItem overLoadTriggerItem, List<StandardSportMarketOdds> oddsVoList, String oddsType, BigDecimal waterValue) {

		overLoadTriggerItem.setAwayAutoChangeRate(waterValue.doubleValue());

		List<TradeMarketAutoDiffConfigItemDTO> waterList = new ArrayList<TradeMarketAutoDiffConfigItemDTO>();

//	        String oddsType = oddsPublicMethodApi.getOddsType(oddsVoList,config);
		for(StandardSportMarketOdds odds : oddsVoList) {
			if(oddsType.equals(odds.getOddsType())) {
				waterList.add(oddsPublicMethodApi.buildWarterList(overLoadTriggerItem.getPlayId() ,overLoadTriggerItem.getMarketId(),odds.getOddsType(), waterValue,config.getSubPlayId()));
			}else {
				waterList.add(oddsPublicMethodApi.buildWarterList(overLoadTriggerItem.getPlayId() ,overLoadTriggerItem.getMarketId(),odds.getOddsType(), BigDecimal.ZERO,config.getSubPlayId()));
			}
		}

		oddsPublicMethodApi.updateMarginAndAutoChangeOdds(overLoadTriggerItem,config);
		oddsPublicMethodApi.sendWaterConfigApi(waterList, overLoadTriggerItem,config);
		config.setAwayAutoChangeRate(waterValue.toString());
		log.info("footBallSendWaterToDataCenter:{},waterList:{}",JSONObject.toJSONString(config),JSONObject.toJSONString(waterList));
	}
	private boolean isOverChangeTimes(RcsMatchMarketConfig config, String oddsType) {
		String time = redisClient.hGet(String.format(RcsConstant.RCS_MARKET_TIMES,config.getMatchId()),config.getMarketId().toString());
//<<<<<<< HEAD
//		log.info("跳分次数key={}，盘口id = {}，次数={}",String.format(RcsConstant.RCS_MARKET_TIMES,config.getMarketId().toString(),config.getMatchId()),time);
//=======
		log.info("::{}::,跳分次数key={}，次数={}",config.getMarketId(),String.format(RcsConstant.RCS_MARKET_TIMES,config.getMatchId()),time);
		Integer times = NumberUtils.INTEGER_ZERO;
		if (StringUtils.isNotBlank(time)){
			times = Integer.parseInt(time);
		}
		if (config.getOddsType().equalsIgnoreCase(oddsType)){
			times += 1;
		}else {
			times -= 1;
		}
		redisClient.hSet(String.format(RcsConstant.RCS_MARKET_TIMES,config.getMatchId()),config.getMarketId().toString(),times.toString());
		redisClient.expireKey(String.format(RcsConstant.RCS_MARKET_TIMES,config.getMatchId()), RcsConstant.BET_EXIST_TIME.intValue());
		if (times >5 || times < -5){
			log.info("::{}::,超过次数times={}",config.getMarketId(),String.format(RcsConstant.RCS_MARKET_TIMES,config.getMatchId()));
			return true;
		}
		return false;
	}
}
