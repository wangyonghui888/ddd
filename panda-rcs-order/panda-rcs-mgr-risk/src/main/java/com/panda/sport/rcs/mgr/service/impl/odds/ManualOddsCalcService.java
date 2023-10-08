package com.panda.sport.rcs.mgr.service.impl.odds;

import com.alibaba.druid.sql.visitor.functions.If;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.util.UuidUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.panda.merge.api.IOutrightTradeConfigApi;
import com.panda.merge.api.ITradeMarketOddsApi;
import com.panda.merge.dto.I18nItemDTO;
import com.panda.merge.dto.MarketMarginGapDtlDTO;
import com.panda.merge.dto.OutrightTradeOddsConfigDTO;
import com.panda.merge.dto.OutrightTradeProbabilityConfigDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.StandardMarketDTO;
import com.panda.merge.dto.StandardMarketOddsDTO;
import com.panda.merge.dto.StandardMatchMarketDTO;
import com.panda.merge.dto.TradeMarketMarginGapConfigDTO;
import com.panda.sport.data.rcs.dto.ThreewayOverLoadTriggerItem;
import com.panda.sport.data.rcs.dto.TwowayDoubleOverLoadTriggerItem;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.mapper.RcsMatchMarketConfigMapper;
import com.panda.sport.rcs.mapper.RcsOddsConvertMappingMyMapper;
import com.panda.sport.rcs.mapper.StandardSportMarketOddsMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainMapper;
import com.panda.sport.rcs.mgr.service.impl.odds.api.OddsCalcApi;
import com.panda.sport.rcs.mgr.service.impl.odds.api.OddsPublicMethodApi;
import com.panda.sport.rcs.mgr.wrapper.IRcsMatchMarketConfigService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.RcsMatchMarketProbabilityConfig;
import com.panda.sport.rcs.pojo.RcsOddsConvertMappingMy;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;
import com.panda.sport.rcs.utils.DataRealtimeApiUtils;
import com.panda.sport.rcs.vo.odds.MatchMarketPlaceConfig;
import com.panda.sport.rcs.vo.odds.MatchOddsConfig;
import com.panda.sport.rcs.vo.odds.MatchPlayConfig;
import com.panda.sport.rcs.vo.odds.RcsStandardMarketDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 手动跳赔计算
* @ClassName: ManualOddsCalcService 
* @Description: TODO
* @author black  
* @date 2021年1月14日 下午5:33:09 
*
 */
@Component
@Slf4j
public class ManualOddsCalcService implements OddsCalcApi{

	@Autowired
	private RcsTournamentTemplatePlayMargainMapper rcsTournamentTemplatePlayMargainMapper;
	
	@Autowired
	private OddsPublicMethodApi oddsPublicMethodApi;
	
    @Autowired
    private RcsOddsConvertMappingMyMapper rcsOddsConvertMappingMyMapper;
    
	@Autowired
	private StandardSportMarketOddsMapper sportMarketOddsMapper;
	@Autowired
	private IRcsMatchMarketConfigService rcsMatchMarketConfigService;
	@Autowired
	private OddsRangeService oddsRangeService;
	@Autowired
	private RcsMatchMarketConfigMapper rcsMatchMarketConfigMapper;
	@Reference(check = false, lazy = true, retries = 1, timeout = 5000)
	private ITradeMarketOddsApi tradeMarketOddsApi;
	@Reference(check = false, lazy = true, retries = 1, timeout = 5000)
	private IOutrightTradeConfigApi iOutrightTradeConfigApi;
	@Override
	@Transactional
	public Boolean maginCalc(RcsMatchMarketConfig config, ThreewayOverLoadTriggerItem item) {
		log.info("maginCalc_manual:{}",JSONObject.toJSONString(config));
			// 冠军玩法
			if (3 == config.getMatchType()){
				championCalculationOddsByMargin(config, item);
				return Boolean.TRUE;
			}
			//根据marketId查询对应盘口所有赔率
		List<StandardSportMarketOdds> oddsVoList = oddsPublicMethodApi.queryOddsVoList(config);
//			List<StandardSportMarketOdds> oddsVoList = sportMarketOddsMapper.queryMarketOddsByMarket(item.getMarketId());
			log.info("::{}::,该盘口没有赔率数据{}", config.getMarketId(),JSONObject.toJSONString(item));
			if (CollectionUtils.isEmpty(oddsVoList)) {
				return Boolean.FALSE;
			}
			oddsPublicMethodApi.setDefaultAnchor(oddsVoList);
			TradeMarketMarginGapConfigDTO gapConfigDTO = new TradeMarketMarginGapConfigDTO();
			gapConfigDTO.setStandardCategoryId(config.getPlayId());
			gapConfigDTO.setStandardMatchInfoId(config.getMatchId());
			gapConfigDTO.setPlaceNum(config.getMarketIndex());
			List<MarketMarginGapDtlDTO> marginGapDtlDTOList = Lists.newArrayList();
			Integer linkageMode = oddsPublicMethodApi.getLinkeAgeMode(config,oddsVoList);
			List<RcsMatchMarketProbabilityConfig> probabilitys = rcsMatchMarketConfigMapper.getOddsTypeProbabilitys(config);
			List<RcsMatchMarketProbabilityConfig> ps = Lists.newArrayList();
			Boolean isProbabilityOver = Boolean.FALSE;
	        // 多项盘
			 if (RcsConstant.FOOTBALL_MOST_PLAYS.contains(config.getPlayId().intValue()) ||
					 RcsConstant.FOOTBALL_X_MOST_PLAYS.contains(config.getPlayId().intValue())
			 ||RcsConstant.BASKETBALL_AO.contains(config.getPlayId().intValue())){
				// 多项盘计算
				isProbabilityOver = mostOddsTypeCalc(config, item, oddsVoList, marginGapDtlDTOList, probabilitys, ps);
			}else {
				// margin 优化算法
				isProbabilityOver = newMarginCalc(config, item, oddsVoList, marginGapDtlDTOList, linkageMode, probabilitys, ps);
			}
			if (isProbabilityOver) return Boolean.TRUE;
//			rcsMatchMarketConfigMapper.insertOrUpdateMarketProbabilityConfig(ps);
			gapConfigDTO.setLinkageMode(linkageMode);
			gapConfigDTO.setList(marginGapDtlDTOList);

			oddsPublicMethodApi.sendOddsConfigApi(item,oddsVoList, config);

		return Boolean.TRUE;
	}
	/**
	 * @Description   //冠军玩法跳分
	 * @Param [config, item, oddsVoList]
	 * @Author  sean
	 * @Date   2021/6/12
	 * @return void
	 **/
	private void championCalculationOddsByMargin(RcsMatchMarketConfig config, ThreewayOverLoadTriggerItem item) {
		log.info("冠军玩法------>championCalculationOddsByMargin config = {},item = {}",
				JSONObject.toJSONString(config),JSONObject.toJSONString(item));

		StandardMarketDTO market = sportMarketOddsMapper.selectMarketOddsByMarketIds(config);
		if (ObjectUtils.isEmpty(market) || CollectionUtils.isEmpty(market.getMarketOddsList())){
			return;
		}
		oddsPublicMethodApi.setI18nName(market);
		OutrightTradeProbabilityConfigDTO gapConfigDTO = new OutrightTradeProbabilityConfigDTO();
		gapConfigDTO.setStandardMatchId(config.getMatchId());
		gapConfigDTO.setStandardMarketId(config.getMarketId());
		market.setId(config.getMarketId().toString());
		List<RcsMatchMarketProbabilityConfig> probabilitys = rcsMatchMarketConfigMapper.getOddsTypeProbabilitys(config);
		List<RcsMatchMarketProbabilityConfig> ps = Lists.newArrayList();
		RcsMatchMarketProbabilityConfig probabilityConfig = new RcsMatchMarketProbabilityConfig();
		for (StandardMarketOddsDTO odds : market.getMarketOddsList()){
			if (odds.getOddsType().equalsIgnoreCase(config.getOddsType())){
				probabilityConfig = oddsPublicMethodApi.getChampionProbabilitys(odds.getOddsType(),probabilitys);

				BigDecimal oddsProbability = new BigDecimal(BaseConstants.MULTIPLE_BET_AMOUNT_VALUE)
						.divide(new BigDecimal(odds.getOddsValue()).divide(new BigDecimal(BaseConstants.MULTIPLE_VALUE), NumberUtils.INTEGER_TWO,BigDecimal.ROUND_DOWN),
								NumberUtils.INTEGER_TWO,BigDecimal.ROUND_DOWN);

				RcsMatchMarketProbabilityConfig championProbabilityConfig = oddsPublicMethodApi.buildProbability(config,odds.getOddsType(), BigDecimal.ZERO);
				championProbabilityConfig.setOddsChangeTimes(probabilityConfig.getOddsChangeTimes());

				oddsProbability = oddsProbability.add(config.getHomeLevelFirstOddsRate());
				// 反算赔率
				BigDecimal oddsValue = new BigDecimal(BaseConstants.MULTIPLE_BET_AMOUNT_VALUE).divide(oddsProbability,NumberUtils.INTEGER_TWO,BigDecimal.ROUND_DOWN);
				if (oddsValue.doubleValue() <= 1.01){
					oddsValue = new BigDecimal("1.01");
				}
				if (oddsValue.doubleValue() >= 1001){
					oddsValue = new BigDecimal("1001");
				}
				odds.setOddsValue(oddsValue.multiply(new BigDecimal(BaseConstants.MULTIPLE_VALUE)).intValue());
				// 超过三次需要关盘和清配置
				if (probabilityConfig.getOddsChangeTimes() >= 3){
					odds.setActive(NumberUtils.INTEGER_ZERO);
					championProbabilityConfig.setOddsChangeTimes(NumberUtils.INTEGER_ZERO);
				}
				ps.add(championProbabilityConfig);
				gapConfigDTO.setStandardMarketOddsId(Long.parseLong(odds.getId()));
				break;
			}
		}
		rcsMatchMarketConfigMapper.insertOrUpdateMarketProbabilityConfig(ps);
		// 推送赔率
		StandardMatchMarketDTO standardMatchMarketDTO = new StandardMatchMarketDTO();
		standardMatchMarketDTO.setStandardMatchInfoId(config.getMatchId());
		standardMatchMarketDTO.setMarketList(Arrays.asList(market));
		standardMatchMarketDTO.setMatchType(NumberUtils.INTEGER_ONE);
		DataRealtimeApiUtils.handleApi(standardMatchMarketDTO, new DataRealtimeApiUtils.ApiCall() {
			@Override
			@Trace
			public <R> Response<R> callApi(Request request) {
				return tradeMarketOddsApi.putTradeMarketOdds(request);
			}
		});
		if (probabilityConfig.getOddsChangeTimes() >= 3){
			oddsPublicMethodApi.closeOddType(gapConfigDTO);
			oddsPublicMethodApi.sendChampionCloseMarketMessage(config,item);
		}
	}

	/**
	 * @Description   //margin 优化
	 * @Param [config, item, oddsVoList, marginGapDtlDTOList, linkageMode, probabilitys, ps]
	 * @Author  sean
	 * @Date   2021/5/15
	 * @return void
	 **/
	private Boolean newMarginCalc(RcsMatchMarketConfig config, ThreewayOverLoadTriggerItem item, List<StandardSportMarketOdds> oddsVoList, List<MarketMarginGapDtlDTO> marginGapDtlDTOList, Integer linkageMode, List<RcsMatchMarketProbabilityConfig> probabilitys, List<RcsMatchMarketProbabilityConfig> ps) {
		// 独赢margin算法
//		BigDecimal totalMargin = BigDecimal.ZERO;
//		BigDecimal anchorMargin = BigDecimal.ZERO;
		BigDecimal currentOddsProbability = BigDecimal.ONE;
//		StandardSportMarketOdds odd = oddsPublicMethodApi.getNotAnchor(oddsVoList,config);
		for (StandardSportMarketOdds odds : oddsVoList){

//			MarketMarginGapDtlDTO dtlDTO = new MarketMarginGapDtlDTO();
//			dtlDTO.setAnchor(odds.getAnchor());
//			dtlDTO.setOddsType(odds.getOddsType());
//			dtlDTO.setMargin(config.getMargin().doubleValue());
//			BigDecimal probability = BigDecimal.ZERO;
//			if (oddsVoList.size() == 3){
//				probability = oddsPublicMethodApi.getOddsTypeProbabilitys(odds.getOddsType(),probabilitys);
//			}
			if (odds.getOddsValue() == 0){
				continue;
			}
			// 算概率
			BigDecimal oddsProbability = new BigDecimal(BaseConstants.MULTIPLE_BET_AMOUNT_VALUE)
					.divide(new BigDecimal(odds.getOddsValue()).divide(new BigDecimal(BaseConstants.MULTIPLE_VALUE), NumberUtils.INTEGER_TWO,BigDecimal.ROUND_DOWN),
							NumberUtils.INTEGER_TWO,BigDecimal.ROUND_DOWN);
			BigDecimal oddsValue = new BigDecimal(odds.getOddsValue());
			// 变动后的概率
			if (odds.getId().longValue() == item.getPlayOptionsId()){
//				probability = probability.add(config.getHomeLevelFirstOddsRate());
				oddsProbability = oddsProbability.add(config.getHomeLevelFirstOddsRate());
				// 当前投注项跳分后的赔率
				currentOddsProbability = oddsProbability;
				// 反算赔率
				oddsValue = new BigDecimal(BaseConstants.MULTIPLE_BET_AMOUNT_VALUE).divide(oddsProbability,NumberUtils.INTEGER_TWO,BigDecimal.ROUND_DOWN);
				odds.setOddsValue(oddsValue.multiply(new BigDecimal(BaseConstants.MULTIPLE_VALUE)).intValue());
			}else if (oddsVoList.size() == 2){
				continue;
			}else if (linkageMode.intValue() > 0){
//				probability = probability.subtract(config.getHomeLevelFirstOddsRate().divide(new BigDecimal(oddsVoList.size()-1),2,BigDecimal.ROUND_DOWN));
				oddsProbability = oddsProbability.subtract(config.getHomeLevelFirstOddsRate().divide(new BigDecimal(oddsVoList.size()-1),2,BigDecimal.ROUND_DOWN));
				// 反算赔率
				oddsValue = new BigDecimal(BaseConstants.MULTIPLE_BET_AMOUNT_VALUE).divide(oddsProbability,NumberUtils.INTEGER_TWO,BigDecimal.ROUND_DOWN);
				odds.setOddsValue(oddsValue.multiply(new BigDecimal(BaseConstants.MULTIPLE_VALUE)).intValue());
			}
//			if (probability.abs().compareTo(new BigDecimal("30")) == 1){
//				return Boolean.TRUE;
//			}
//			probability = oddsPublicMethodApi.checkProbability(probability);
//			oddsProbability = oddsPublicMethodApi.checkProbability(oddsProbability);
			// 汇总概率
//			totalMargin = totalMargin.add(oddsProbability);
//			dtlDTO.setProbability(probability.doubleValue());
//			marginGapDtlDTOList.add(dtlDTO);
			// 描点margin
//			if (!odds.getOddsType().equalsIgnoreCase(odd.getOddsType())){
//				anchorMargin = anchorMargin.add(oddsProbability);
//			}
//			RcsMatchMarketProbabilityConfig probabilityConfig = oddsPublicMethodApi.buildProbability(config,odds.getOddsType(),probability);
//			ps.add(probabilityConfig);
		}
		//是否超过margin范围 超过范围需要调整
//		if (totalMargin.subtract(config.getMargin()).abs().compareTo(new BigDecimal(NumberUtils.INTEGER_TWO)) == 1){
//			for (StandardSportMarketOdds odds : oddsVoList){
//				if (odds.getOddsType().equalsIgnoreCase(odd.getOddsType())){
//					BigDecimal probability = BigDecimal.ZERO;
//					if (totalMargin.compareTo(config.getMargin()) == 1){
//						probability = config.getMargin().add(new BigDecimal(NumberUtils.INTEGER_TWO)).subtract(anchorMargin);
//					}else {
//						probability =config.getMargin().subtract(new BigDecimal(NumberUtils.INTEGER_TWO)).subtract(anchorMargin);
//					}
//					BigDecimal oddsValue = new BigDecimal(BaseConstants.MULTIPLE_BET_AMOUNT_VALUE).divide(probability,NumberUtils.INTEGER_TWO,BigDecimal.ROUND_DOWN);
//					odds.setOddsValue(oddsValue.multiply(new BigDecimal(BaseConstants.MULTIPLE_VALUE)).intValue());
//					break;
//				}
//			}
//		}
		// 根据margin反算赔率
		if (oddsVoList.size() == 2){
			for (StandardSportMarketOdds odds : oddsVoList){
				if (odds.getId().longValue() != item.getPlayOptionsId()){
					// 变动后的概率
					BigDecimal probability = config.getMargin().subtract(currentOddsProbability);
					// 反算赔率
					BigDecimal oddsValue = new BigDecimal(BaseConstants.MULTIPLE_BET_AMOUNT_VALUE).divide(probability,NumberUtils.INTEGER_TWO,BigDecimal.ROUND_DOWN);
					odds.setOddsValue(oddsValue.multiply(new BigDecimal(BaseConstants.MULTIPLE_VALUE)).intValue());
				}
			}
		}
		return Boolean.FALSE;
	}
	/**
	 * @Description   //多项盘
	 * @Param [config, item, oddsVoList, marginGapDtlDTOList, probabilitys, ps]
	 * @Author  sean
	 * @Date   2021/5/15
	 * @return void
	 **/
	private Boolean mostOddsTypeCalc(RcsMatchMarketConfig config, ThreewayOverLoadTriggerItem item, List<StandardSportMarketOdds> oddsVoList, List<MarketMarginGapDtlDTO> marginGapDtlDTOList, List<RcsMatchMarketProbabilityConfig> probabilitys, List<RcsMatchMarketProbabilityConfig> ps) {
		log.info("mostOddsTypeCalc_多项盘:{}",JSONObject.toJSONString(config));
		MarketMarginGapDtlDTO dtlDTO = new MarketMarginGapDtlDTO();
		for (StandardSportMarketOdds odds : oddsVoList){
			if (odds.getId().longValue() == item.getPlayOptionsId()){
				BigDecimal probability = oddsPublicMethodApi.getOddsTypeProbabilitys(odds.getOddsType(),probabilitys);
				probability = probability.add(config.getHomeLevelFirstOddsRate());
//				if (probability.abs().compareTo(new BigDecimal("30")) == 1){
//					return Boolean.TRUE;
//				}
//				probability = oddsPublicMethodApi.checkProbability(probability.add(config.getHomeLevelFirstOddsRate()));
				dtlDTO.setProbability(probability.doubleValue());
				dtlDTO.setOddsType(odds.getOddsType());
//				dtlDTO.setMargin(config.getMargin().doubleValue());
//				dtlDTO.setAnchor(odds.getAnchor());
				marginGapDtlDTOList.add(dtlDTO);

				RcsMatchMarketProbabilityConfig probabilityConfig = oddsPublicMethodApi.buildProbability(config,odds.getOddsType(), probability);
				ps.add(probabilityConfig);

				BigDecimal oddsProbability = new BigDecimal(BaseConstants.MULTIPLE_BET_AMOUNT_VALUE)
						.divide(new BigDecimal(odds.getOddsValue()).divide(new BigDecimal(BaseConstants.MULTIPLE_VALUE), NumberUtils.INTEGER_TWO,BigDecimal.ROUND_DOWN),
								NumberUtils.INTEGER_TWO,BigDecimal.ROUND_DOWN);
				oddsProbability = oddsProbability.add(config.getHomeLevelFirstOddsRate());
				// 反算赔率
				BigDecimal oddsValue = new BigDecimal(BaseConstants.MULTIPLE_BET_AMOUNT_VALUE).divide(oddsProbability,NumberUtils.INTEGER_TWO,BigDecimal.ROUND_DOWN);
				odds.setOddsValue(oddsValue.multiply(new BigDecimal(BaseConstants.MULTIPLE_VALUE)).intValue());
				break;
			}
		}
		return Boolean.FALSE;
	}

	@Override
	@Transactional
	public Boolean waterCalc(RcsMatchMarketConfig config, ThreewayOverLoadTriggerItem item) {
		log.info("::{}::,auto_赔率变更计算入参：{}", config.getMarketId(),JSONObject.toJSONString(item));
		
			TwowayDoubleOverLoadTriggerItem overLoadTriggerItem = (TwowayDoubleOverLoadTriggerItem) item;
	        //判断必要参数是否齐全
			if (ObjectUtils.isEmpty(overLoadTriggerItem.getLimitLevel())) {
				log.info("::{}::,缺少必要参数：{}", config.getMarketId(),JSONObject.toJSONString(overLoadTriggerItem));
	            return Boolean.FALSE;
	        }
			
	        //根据marketId查询对应盘口所有赔率
		List<StandardSportMarketOdds> oddsVoList = oddsPublicMethodApi.queryOddsVoList(config);
//	        List<StandardSportMarketOdds> oddsVoList = sportMarketOddsMapper.queryMarketOddsByMarket(overLoadTriggerItem.getMarketId());
			log.info("::{}::,auto_oddsVoList赔率变更计算入参：{}", config.getMarketId(),JSONObject.toJSONString(oddsVoList));
	        if (CollectionUtils.isEmpty(oddsVoList)) {
//	            log.info("该盘口没有赔率数据{}", JSONObject.toJSONString(overLoadTriggerItem));
	            return Boolean.FALSE;
	        }
	        String position = oddsPublicMethodApi.getTwoPositon(oddsVoList,overLoadTriggerItem.getPlayOptionsId(),config);
			BigDecimal changeOdds = BigDecimal.valueOf(0);
	        if (position.equalsIgnoreCase(BaseConstants.ODD_TYPE_HOME)){
	        	changeOdds = oddsPublicMethodApi.getHomeOddsRate(overLoadTriggerItem,config);
	        }else if (position.equalsIgnoreCase(BaseConstants.ODD_TYPE_AWAY)){
	        	changeOdds = oddsPublicMethodApi.getAwayOddsRate(overLoadTriggerItem,config).multiply(new BigDecimal(NumberUtils.LONG_MINUS_ONE));
	        }
	        
	        //篮球、网球、冰球、乒乓球 手动，将水差发送到队列计算赔率
	        if (config.getSportId() == 2 || config.getSportId() == 5 || config.getSportId() == 4 || config.getSportId() == 8) {
	        	String oddsType = oddsPublicMethodApi.getOddsType(oddsVoList,config);
	        	List<MatchMarketPlaceConfig> playPlaceConfigList =  oddsPublicMethodApi.queryPlaceWaterConfigList(config,changeOdds);
	        	if (!CollectionUtils.isEmpty(playPlaceConfigList)){
					playPlaceConfigList.forEach(e -> {
						if (e.getPlaceNum().intValue() == config.getMarketIndex()){
							e.setSpread(config.getMargin().toPlainString());
						}
					});
				}
	        	// 发送玩法水差
				oddsPublicMethodApi.sendPlayWaterConfigApi(overLoadTriggerItem, config, oddsType, playPlaceConfigList);
				log.info("发送玩法水差成功:{}",JSONObject.toJSONString(overLoadTriggerItem));
	        	return Boolean.TRUE;
	        }
		RcsTournamentTemplatePlayMargain template = rcsTournamentTemplatePlayMargainMapper.queryTournamentAdjustRangeByPlayId(config);
//		commonSpreadCalcu(overLoadTriggerItem, oddsVoList, position, changeOdds);
//		if (!ObjectUtils.isEmpty(template) &&
//				!ObjectUtils.isEmpty(template.getIsSpecialPumping()) &&
//				template.getIsSpecialPumping() == 1){
//			// 特殊抽水
//			oddsRangeService.caluOddsBySpread(oddsVoList,config,template,changeOdds);
//		}
		if (!ObjectUtils.isEmpty(template) &&
				!ObjectUtils.isEmpty(template.getIsSpecialPumping()) &&
				template.getIsSpecialPumping() == 1){
			BigDecimal spread = oddsRangeService.getSpicalSpread(oddsVoList,config,template);
			overLoadTriggerItem.setMargin(spread);
			// 使用特殊抽水计算新赔率
			commonSpreadCalcu(overLoadTriggerItem, oddsVoList, position, changeOdds);
			// 特殊抽水
			oddsRangeService.caluOddsBySpread(oddsVoList,config,template,changeOdds);
		}else {
			commonSpreadCalcu(overLoadTriggerItem, oddsVoList, position, changeOdds);
		}
		oddsPublicMethodApi.sendOddsConfigApi(item,oddsVoList, config);
		return Boolean.TRUE;
	}


	private void commonSpreadCalcu(TwowayDoubleOverLoadTriggerItem overLoadTriggerItem, List<StandardSportMarketOdds> oddsVoList, String position, BigDecimal changeOdds) {
		//下盘的赔率
		BigDecimal downOdds = BigDecimal.valueOf(0);
		Long downId = 0L;
		String playOptionsId = overLoadTriggerItem.getPlayOptionsId().toString();
		//计算小球的赔率
		for (StandardSportMarketOdds vo : oddsVoList){
			String oddsId = vo.getId().toString();
			String sourceOddsValue = oddsPublicMethodApi.getMYOddsValue(vo.getOddsValue().toString());
			BigDecimal resultOdds = new BigDecimal(sourceOddsValue);

			//主队触发跳赔，如果投注项是主队：客队赔率 + 客队赔率变化
			if (position.equalsIgnoreCase(BaseConstants.ODD_TYPE_HOME)){
				//上盘-赔率变化，下盘赔率 + 赔率变化 重新计算上盘
				if (!oddsId.equalsIgnoreCase(playOptionsId)){
					downOdds = oddsPublicMethodApi.calculationOdds(resultOdds,changeOdds);
					downId = vo.getId();
					break;
				}
			}
			//客队触发跳赔，如果投注项是客队：客队赔率 - 客队赔率变化
			if (position.equalsIgnoreCase(BaseConstants.ODD_TYPE_AWAY)){
				//下盘-赔率变化，下盘赔率-赔率变化 重新计算上盘
				if (oddsId.equalsIgnoreCase(playOptionsId)){
					downOdds = oddsPublicMethodApi.calculationOdds(resultOdds,changeOdds);
					downId = vo.getId();
					break;
				}
			}
		}
		// 再算上盘的变赔
		BigDecimal upperOdds = oddsPublicMethodApi.getUpperOdds(overLoadTriggerItem,downOdds);

		for (StandardSportMarketOdds vo : oddsVoList){
			BigDecimal odds = BigDecimal.valueOf(0);
			if (vo.getId().longValue() == downId){
				odds = downOdds;
			}else{
				odds = upperOdds;
			}
			QueryWrapper<RcsOddsConvertMappingMy> wrapper = new QueryWrapper<>();
			wrapper.lambda().eq(RcsOddsConvertMappingMy::getMalaysia, odds.toPlainString());
			wrapper.lambda().select(RcsOddsConvertMappingMy::getEurope);
			RcsOddsConvertMappingMy mapping = rcsOddsConvertMappingMyMapper.selectOne(wrapper);
			if ((!ObjectUtils.isEmpty(mapping)) && StringUtils.isNotBlank(mapping.getEurope())){
				odds = new BigDecimal(mapping.getEurope()).multiply(new BigDecimal(BaseConstants.MULTIPLE_VALUE));
			}
			vo.setOddsValue(odds.intValue());
		}
	}

}
