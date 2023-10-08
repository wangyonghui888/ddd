package com.panda.sport.rcs.mgr.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Maps;
import com.panda.merge.api.ITradeMarketOddsApi;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.StandardMarketDTO;
import com.panda.merge.dto.StandardMatchMarketDTO;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mgr.service.MarketStatusService;
import com.panda.sport.rcs.mgr.utils.BeanFactory;
import com.panda.sport.rcs.mgr.utils.MarketUtils;
import com.panda.sport.rcs.mgr.wrapper.IRcsTradeConfigService;
import com.panda.sport.rcs.pojo.RcsTradeConfig;
import com.panda.sport.rcs.utils.DataRealtimeApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.rpc.RpcException;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Project Name: panda-rcs-trade-group
 * @Package Name: com.panda.sport.rcs.trade.wrapper.impl
 * @Description : 盘口状态服务实现类
 * @Author : Paca
 * @Date : 2020-07-17 11:03
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Service
public class MarketStatusServiceImpl implements MarketStatusService {

    /**
     * 默认盘口位置数量
     */
    private static final int DEFAULT_MARKET_PLACE_AMOUNT = 10;

    @Autowired
    private IRcsTradeConfigService rcsTradeConfigService;

    @Reference(check = false, lazy = true, retries = 1, timeout = 5000)
    private ITradeMarketOddsApi tradeMarketOddsApi;

    @Override
    public Response updateMarketOddsNew(StandardMatchMarketDTO standardMatchMarketDTO) {
        log.info("::{}::修改盘口赔率：{}",standardMatchMarketDTO.getStandardMatchInfoId(),JsonFormatUtils.toJson(standardMatchMarketDTO));
        Long matchId = standardMatchMarketDTO.getStandardMatchInfoId();

        List<StandardMarketDTO> marketList = standardMatchMarketDTO.getMarketList();
        if (CollectionUtils.isNotEmpty(marketList)) {
            Long categoryId = marketList.get(0).getMarketCategoryId();
            Map<String, String> marketPlaceStatusMap = getMarketPlaceStatusOfManage(matchId, categoryId);
            Integer matchStatus = rcsTradeConfigService.getMatchStatus(matchId);
            marketList.forEach(market -> {
                Integer placeNum = market.getPlaceNum();
                if (!MarketUtils.isOpen(matchStatus)) {
                    market.setStatus(matchStatus);
                } else if (marketPlaceStatusMap.containsKey(placeNum + "")) {
                    Integer status = Integer.valueOf(marketPlaceStatusMap.get(placeNum + ""));
                    if (!MarketUtils.isOpen(status)) {
                        market.setStatus(status);
                    }
                }
            });
        }
        return putTradeMarketOdds(matchId, marketList);
    }

    /**
     * 获取风控后台盘口位置状态，与赛事状态无关
     *
     * @param matchId
     * @param categoryId
     * @return key=盘口位置,value=状态
     * @author paca
     */
    private Map<String, String> getMarketPlaceStatusOfManage(final Long matchId, final Long categoryId) {
        Map<String, String> map = Maps.newHashMap();
        // 查询玩法下所有盘口位置最新状态配置
        Map<Integer, RcsTradeConfig> marketPlaceConfigMap = rcsTradeConfigService.getMarketPlaceStatus(matchId, categoryId);
        // 查询玩法最新的状态
        RcsTradeConfig categoryConfig = rcsTradeConfigService.getPlayStatusConfig(matchId, categoryId);
        // 查询玩法集最新的状态
        RcsTradeConfig categorySetConfig = rcsTradeConfigService.getPlaySetStatusByPlayId(matchId, categoryId);
        // 盘口位置，最多10个位置
        for (int placeNum = 1; placeNum <= DEFAULT_MARKET_PLACE_AMOUNT; placeNum++) {
            RcsTradeConfig marketConfig = marketPlaceConfigMap.getOrDefault(placeNum, BeanFactory.defaultMarketPlaceStatus());
            Integer status = marketConfig.getStatus();
            if (MarketUtils.isOpen(status)) {
                status = getMarketPlaceStatusOfManage(marketConfig, categoryConfig, categorySetConfig);
            }
            map.put(placeNum + "", status.toString());
        }
        return map;
    }

    /**
     * 获取风控后台盘口位置状态
     *
     * @param marketPlaceConfig
     * @param categoryConfig
     * @param categorySetConfig
     * @return
     * @author Paca
     */
    private Integer getMarketPlaceStatusOfManage(RcsTradeConfig marketPlaceConfig, RcsTradeConfig categoryConfig, RcsTradeConfig categorySetConfig) {
        if (marketPlaceConfig == null) {
            marketPlaceConfig = BeanFactory.defaultMarketPlaceStatus();
        }
        if (categoryConfig == null) {
            categoryConfig = BeanFactory.defaultCategoryStatus();
        }
        if (categorySetConfig == null) {
            categorySetConfig = BeanFactory.defaultCategorySetStatus();
        }
        // 通过数据库自增ID判断时间顺序
        // 盘口位置状态在此始终为 开
        final Integer marketConfigId = marketPlaceConfig.getId();
        final Integer categoryConfigId = categoryConfig.getId();
        final Integer categorySetConfigId = categorySetConfig.getId();
        if (marketConfigId.compareTo(categoryConfigId) > 0 && marketConfigId.compareTo(categorySetConfigId) > 0) {
            // 盘口位置最后操作，取盘口位置状态
            return marketPlaceConfig.getStatus();
        }
        if (marketConfigId.compareTo(categoryConfigId) > 0 && !MarketUtils.isOpen(categoryConfig.getStatus())) {
            // 盘口位置操作在玩法操作之后，且玩法状态 非开
            return marketPlaceConfig.getStatus();
        }
        if (!MarketUtils.isOpen(categoryConfig.getStatus())) {
            // 玩法状态 非开，取玩法状态
            return categoryConfig.getStatus();
        }
        if (categoryConfigId.compareTo(categorySetConfigId) > 0) {
            // 玩法最后操作，取玩法状态
            return categoryConfig.getStatus();
        }
        return categorySetConfig.getStatus();
    }

    /**
     * 调用融合RPC接口，操盘标准盘口及赔率数据处理
     *
     * @param matchId
     * @param marketList
     * @return
     */
    private Response putTradeMarketOdds(Long matchId, List<StandardMarketDTO> marketList) {
        StandardMatchMarketDTO standardMatchMarketDTO = new StandardMatchMarketDTO();
        standardMatchMarketDTO.setStandardMatchInfoId(matchId);
        standardMatchMarketDTO.setMarketList(marketList);
        try {
            return DataRealtimeApiUtils.handleApi(standardMatchMarketDTO, new DataRealtimeApiUtils.ApiCall() {
                @Override
                @Trace
                public <R> Response<R> callApi(Request request) {
                    return tradeMarketOddsApi.putTradeMarketOdds(request);
                }
            });
        } catch (RpcException | RcsServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new RpcException("调用接口 ITradeMarketOddsApi.putTradeMarketOdds 出错", e);
        }
    }

}
