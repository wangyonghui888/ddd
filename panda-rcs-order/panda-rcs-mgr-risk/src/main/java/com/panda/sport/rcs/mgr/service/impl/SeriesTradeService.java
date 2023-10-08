package com.panda.sport.rcs.mgr.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.panda.sport.rcs.mapper.TUserMapper;
import com.panda.sport.rcs.mapper.statistics.StatMatchIpMapper;
import com.panda.sport.rcs.pojo.*;
import com.panda.sport.rcs.pojo.dto.StatMatchIpDto;
import com.panda.sport.rcs.pojo.statistics.StatMatchIp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.RcsMatchOrderAcceptConfigMapper;
import com.panda.sport.rcs.mapper.RcsTournamentOrderAcceptConfigMapper;
import com.panda.sport.rcs.mgr.wrapper.impl.RcsPaidConfigServiceImp;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.utils.SeriesTypeUtils;
import com.panda.sport.rcs.utils.SpliteOrderUtils;
import com.panda.sport.rcs.vo.RcsBusinessPlayPaidConfigVo;

import lombok.extern.slf4j.Slf4j;

/**
 * 串关限额需求
 * @author black
 */
@Component
@Slf4j
public class SeriesTradeService {
	@Resource
	RcsMatchOrderAcceptConfigMapper rcsMatchOrderAcceptConfigMapper;
	@Resource
	RcsTournamentOrderAcceptConfigMapper rcsTournamentOrderAcceptConfigMapper;
	@Autowired
	ParamValidate paramValidate;
	@Autowired
	public RcsPaidConfigServiceImp configService;


	@Value("${rocketmq.order.save.config}")
	private String saveOrderConfig;

	@Autowired
	private ProducerSendMessageUtils producerSendMessageUtils;

	public Response queryMaxBetMoneyBySelect(Request<OrderBean> requestParam) {
		//其它体育种类默认映射到足球
		requestParam.getData().setSportId(1);
	    String orderNo = requestParam.getData().getOrderNo();
		for(OrderItem item : requestParam.getData().getItems()){
			item.setSportId(1);
		}

		Map<String,Object> result = Maps.newHashMap();
		List<RcsBusinessPlayPaidConfigVo> li = Lists.newArrayList();
		result.put("data",li);
		try {
			OrderBean orderBean = requestParam.getData();

			if(orderBean == null || orderBean.getItems() == null || orderBean.getItems().size() < 2)
				return Response.error(70014002, "参数错误，items参数与串关类型参数不匹配");

			Integer seriesType = orderBean.getSeriesType();
			if(seriesType == 1 || seriesType < 100 )
				return Response.error(70014001, "当前业务不支持单关");

			//获取M串N中的M
			Integer type = SeriesTypeUtils.getSeriesType(seriesType);
			if(type > orderBean.getItems().size()) return Response.error(70014003, "参数错误，items与seriesType不匹配");

			List<Map<String, Object>> itemList = getMinTradeMoneyByOrder(orderBean);
			//单注串关最大赔付值
			RcsBusinessConPlayConfig conPlay1Config = getConPlayConfig(String.valueOf(orderBean.getTenantId()), "1");
			//单注串关最低投注额
			RcsBusinessConPlayConfig conPlay2Config = getConPlayConfig(String.valueOf(orderBean.getTenantId()), "2");
			//单注串关限额占单关限额比例
			RcsBusinessConPlayConfig conPlay3Config = getConPlayConfig(String.valueOf(orderBean.getTenantId()), "3");

			Integer count = SeriesTypeUtils.getCount(seriesType, type);
			if(count == 1) {//只返回对应的N串1的类型最大最小值
				RcsBusinessPlayPaidConfigVo vo = getSpliteOrderByType(type, itemList, conPlay1Config, conPlay2Config, conPlay3Config);
				putBean(vo,orderBean.getProductAmountTotal());
				li.add(vo);
			}else {//需要拆分所有单
				RcsBusinessPlayPaidConfigVo allVo = new RcsBusinessPlayPaidConfigVo();
				allVo.setMinBet(conPlay2Config.getPlayValue().longValue());
				allVo.setOrderMaxPay(Long.MAX_VALUE);
				allVo.setType(String.valueOf(seriesType));
				for(int i = 2 ; i <= type ; i ++) {//从二串一开始计算
					RcsBusinessPlayPaidConfigVo vo = getSpliteOrderByType(i, itemList, conPlay1Config, conPlay2Config, conPlay3Config);
					allVo.setOrderMaxPay(Math.min(allVo.getOrderMaxPay(),vo.getOrderMaxPay()));
					putBean(vo,orderBean.getProductAmountTotal());
					li.add(vo);
				}
				putBean(allVo,orderBean.getProductAmountTotal());
				li.add(allVo);
			}

		}catch (RcsServiceException e) {
			log.error("::{}:: queryMaxBetMoneyBySelect ERROR {}",orderNo,e.getMessage());
			return Response.error(e.getCode(),e.getMessage());
		}catch (Exception e) {
			log.error("::{}:: queryMaxBetMoneyBySelect ERROR {}",orderNo,e.getMessage());
			return Response.error(Response.FAIL,"服务处理失败");
		}
		return Response.success(result);
	}

	private void putBean(RcsBusinessPlayPaidConfigVo vo ,Long betAmount) {
		if(betAmount != null ) {//下单校验，带入真正的下单金额
			betAmount = new BigDecimal(String.valueOf(betAmount)).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP).longValue();
			Boolean isPass = betAmount <= vo.getOrderMaxPay() && betAmount >= vo.getMinBet();
			if(betAmount > vo.getOrderMaxPay()) {
				vo.setErrorMsg(String.format("下注金额%s大于最大限额%s", betAmount,vo.getOrderMaxPay()));
			}
//			if(betAmount < vo.getMinBet()) {
//				vo.setErrorMsg(String.format("下注金额%s小于最小限额%s", betAmount,vo.getMinBet()));
//			}

			vo.setIsPass(isPass);
		}
	}

	public Response<Map<String, Object>> saveOrderAndValidateMaxPaid(Request<OrderBean> requestParam) {
		Map<String, Object> result = new HashMap<String, Object>();
		Integer status = 1;
		Integer validateResult = 1;
		String orderNo=requestParam.getData().getOrderNo();
		try {
			Response response = queryMaxBetMoneyBySelect(requestParam);
			if (!response.isSuccess()) return response;
			Map<String, Object> dataMap = (Map<String, Object>) response.getData();
			List<RcsBusinessPlayPaidConfigVo> li = (List<RcsBusinessPlayPaidConfigVo>) dataMap.get("data");
			boolean orderScroll = orderScroll(requestParam.getData());
			if (orderScroll) {
				status = 2;
				validateResult = 0;
			}
			for (RcsBusinessPlayPaidConfigVo vo : li) {
				if (String.valueOf(requestParam.getData().getSeriesType()).equals(vo.getType())) {
					result.put(requestParam.getData().getOrderNo(), vo.getIsPass());
					result.put(requestParam.getData().getOrderNo() + "_error_msg", vo.getErrorMsg());
					requestParam.getData().setValidateResult(validateResult);
					for (OrderItem orderItem : requestParam.getData().getItems()) {
						orderItem.setValidateResult(validateResult);
					}
					producerSendMessageUtils.sendMessage(saveOrderConfig, requestParam.getData());
					producerSendMessageUtils.sendMessage(MqConstants.WS_ORDER_BET_RECORD_TOPIC + "," + MqConstants.WS_ORDER_BET_RECORD_TAG, requestParam.getData());
					break;
				}
			}
		} catch (RcsServiceException e) {
			log.error("::{}:: queryMaxBetMoneyBySelect ERROR {}",orderNo,e.getMessage());
			return Response.error(e.getCode(), e.getMessage());

		} catch (Exception e) {
			log.error("::{}:: queryMaxBetMoneyBySelect ERROR {}",orderNo,e.getMessage());
			return Response.error(Response.FAIL, "服务处理失败");
		}
		result.put("status", status);
		return Response.success(result);
	}
	/**
	 * 校验是否有滚球
	 *
	 * @param orderBean
	 * @return
	 */
	private boolean orderScroll(OrderBean orderBean) {

		//是否滚球
		boolean scrollflag = false;
		//是否有手工接单
		boolean modleFlag = false;

		for (OrderItem item : orderBean.getItems()) {
			//查询赛事配置
			LambdaQueryWrapper<RcsMatchOrderAcceptConfig> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(RcsMatchOrderAcceptConfig::getMatchId, item.getMatchId());
			RcsMatchOrderAcceptConfig rcsMatchOrderAcceptConfig = rcsMatchOrderAcceptConfigMapper.selectOne(queryWrapper);

			//查询联赛配置1
			LambdaQueryWrapper<RcsTournamentOrderAcceptConfig> eventWrapper = new LambdaQueryWrapper<>();
			eventWrapper.eq(RcsTournamentOrderAcceptConfig::getTournamentId, item.getTournamentId());
			RcsTournamentOrderAcceptConfig rcsTournamentOrderAcceptConfig = rcsTournamentOrderAcceptConfigMapper.selectOne(eventWrapper);

			//如果没有配置1
			if (rcsMatchOrderAcceptConfig == null && rcsTournamentOrderAcceptConfig == null) {
				continue;
			}
			//中场休息
			if(rcsMatchOrderAcceptConfig !=null && rcsMatchOrderAcceptConfig.getHalfTime()==1){
				continue;
			}
			if(rcsTournamentOrderAcceptConfig  !=null &&  rcsTournamentOrderAcceptConfig.getHalfTime()==1){
				continue;
			}

			//如果是手工接单
			if(rcsMatchOrderAcceptConfig!=null && rcsMatchOrderAcceptConfig.getMode()==1  ){
				modleFlag = true;
			}
			if(rcsTournamentOrderAcceptConfig!=null && rcsTournamentOrderAcceptConfig.getMode()==1  ){
				modleFlag = true;
			}

			//出现任何滚球赛事  需走滚球接拒单流程逻辑
			if (item.getMatchType() == 2) {
				scrollflag = true;
			}
		}
		log.info("::{}:: 串关滚球判断完成scrollflag:{} modleFlag:{}",orderBean.getOrderNo(),scrollflag,modleFlag);
		return scrollflag;
	}
	public Response validateOrderMaxPaid(Request<OrderBean> requestParam) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Response response = queryMaxBetMoneyBySelect(requestParam);
			if(!response.isSuccess()) return response;
			Map<String,Object> dataMap = (Map<String, Object>) response.getData();
			List<RcsBusinessPlayPaidConfigVo> li = (List<RcsBusinessPlayPaidConfigVo>) dataMap.get("data");
			for(RcsBusinessPlayPaidConfigVo vo : li) {
				if(String.valueOf(requestParam.getData().getSeriesType()).equals(vo.getType())) {
					result.put(requestParam.getData().getOrderNo(), vo.getIsPass());
					result.put(requestParam.getData().getOrderNo() + "_error_msg",vo.getErrorMsg());
					break;
				}
			}
		}catch (RcsServiceException e) {
			return Response.error(e.getCode(),e.getMessage());
		}catch (Exception e) {
			log.info(e.getMessage(),e);
			return Response.error(Response.FAIL,"服务处理失败");
		}
		
		return Response.success(result);
	}
	
	private RcsBusinessPlayPaidConfigVo getSpliteOrderByType(int index,List<Map<String, Object>> itemList,RcsBusinessConPlayConfig conPlay1Config,RcsBusinessConPlayConfig conPlay2Config,
			RcsBusinessConPlayConfig conPlay3Config) {
		RcsBusinessPlayPaidConfigVo vo = new RcsBusinessPlayPaidConfigVo();
		vo.setType(index * 100 + "1");
		vo.setOrderMaxPay(Long.MAX_VALUE);
		vo.setMinBet(conPlay2Config.getPlayValue().longValue());
		SpliteOrderUtils.spliteOrder(itemList, index, 0, 0, new ArrayList<Map<String, Object>>(), new SpliteOrderUtils.ApiCall<Map<String, Object>>() {
			@Override
			public void execute(List<Map<String, Object>> list) {
				BigDecimal oddsBigDicimal = new BigDecimal(1);
				//获取当前串关中单注玩法
				Integer minMoney = Integer.MAX_VALUE;
				//获取赛事联赛单注串关最大赔付
				Integer minConMoney = Integer.MAX_VALUE;
				
				for(Map<String, Object> info : list) {
					minMoney = Math.min(minMoney , Integer.parseInt(String.valueOf(info.get("money"))));
					minConMoney = Math.min(minConMoney , Integer.parseInt(String.valueOf(info.get("conMaxMoney"))));
					oddsBigDicimal = oddsBigDicimal.multiply(new BigDecimal(String.valueOf(info.get("odds")))).divide(new BigDecimal("100000"));
				}
				BigDecimal tempMoney = new BigDecimal(minMoney).divide(new BigDecimal("100"),2, BigDecimal.ROUND_HALF_UP)
						.multiply(conPlay3Config.getPlayValue()).divide(new BigDecimal("100"),2, BigDecimal.ROUND_HALF_UP);
				vo.setOrderMaxPay(Math.min(vo.getOrderMaxPay(), tempMoney.longValue()));
				
				minConMoney = Math.min(conPlay1Config.getPlayValue().intValue(), minConMoney);
				tempMoney = new BigDecimal(String.valueOf(minConMoney)).divide(oddsBigDicimal,2, BigDecimal.ROUND_HALF_UP);
				vo.setOrderMaxPay(Math.min(vo.getOrderMaxPay(), tempMoney.longValue()));
			}
		});
		
		return vo;
	}
	
	/**
	 * 获取串关配置 rcs_business_con_play_config
	 * @param busId
	 * @param type
	 * @return
	 */
    private RcsBusinessConPlayConfig getConPlayConfig(String busId,String type) {
        //获取配置数据
    	RcsBusinessConPlayConfig config = configService.getConPlayConfig(busId,type);
        //数据库没有配置数据，返回0
        if (config == null ) {
            log.warn(String.format("没有找到商户id:%s;type:%s;串关额度管理的配置项",
            		busId,type));
            return null;
        }
        return  config;
    }
	
	/**
	 * 获取配置中每个赛事对于最小投注金额
	 * @param orderBean
	 */
	private List<Map<String, Object>> getMinTradeMoneyByOrder(OrderBean orderBean) {
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		for(OrderItem item : orderBean.getItems()) {
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> info = paramValidate.getMatchInfo(String.valueOf(item.getMatchId()),item.getMarketId().toString(),item.getPlayOptionsId().toString());
			Integer tournamentLevel = 0;//未评级
			if(info != null && !info.containsKey("tournamentLevel")) {//获取联赛级别
				tournamentLevel = Integer.parseInt(String.valueOf(info.get("tournamentLevel")));
			}
			ExtendBean extendBean = paramValidate.buildExtendBean(orderBean, item);
			RcsBusinessSingleBetConfig config = getPlayConfig(extendBean, tournamentLevel);
			
			//获取联赛单关最大赔付
			RcsBusinessMatchPaidConfig matchConfig = configService.getMatchPaidConfig(String.valueOf(orderBean.getTenantId()), String.valueOf(item.getSportId()), 
					String.valueOf(tournamentLevel));
			map.put("money", config == null ? 0 : config.getOrderMaxValue().intValue());
			map.put("odds", item.getOddsValue());
			map.put("matchId", item.getMatchId());
			map.put("conMaxMoney", matchConfig == null || matchConfig.getMatchMaxConPayVal() == null  ? 0 : matchConfig.getMatchMaxConPayVal().divide(new BigDecimal("100")).intValue());
			result.add(map);
		}
		
		if(result.size() != orderBean.getItems().size())
			throw new RcsServiceException(70014004,"每个赛事只能串一单");
		
		
		return result;
	}

	private RcsBusinessSingleBetConfig getPlayConfig(ExtendBean bean,Integer tournamentLevel) {
		RcsBusinessSingleBetConfig playConfig = configService.getSingleBetConfig(bean.getBusId(), bean.getSportId(), 
				bean.getIsScroll(), bean.getPlayType(), bean.getPlayId(), String.valueOf(tournamentLevel));
		if(playConfig == null )  return null;
		return playConfig;
	}
	
	public static void main(String[] args) {
		System.out.println(Integer.parseInt("05"));
	}

}
