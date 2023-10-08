package com.panda.sport.rcs.data.calc.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Maps;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.data.calc.service.MarketBuildService;
import com.panda.sport.rcs.data.mapper.RcsMatchMarketConfigMapper;
import com.panda.sport.rcs.pojo.config.BuildMarketPlaceConfig;
import com.panda.sport.rcs.pojo.config.BuildMarketPlayConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Project Name : rcs-parent
 * @Package Name : rcs-parent
 * @Description : 盘口构建服务实现
 * @Author : Paca
 * @Date : 2022-05-14 10:46
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Service
public class MarketBuildServiceImpl implements MarketBuildService {

    @Autowired
    private RcsMatchMarketConfigMapper rcsMatchMarketConfigMapper;

    @Autowired
    private RedisClient redisClient;

    @Override
    public BigDecimal getMarketHeadGap(Long matchId, Long playId) {
        BuildMarketPlayConfig config = rcsMatchMarketConfigMapper.getMarketHeadGap(matchId, playId);
        log.info("A+模式构建盘口，获取盘口差：matchId={},playId={},config={}", matchId, playId, JSON.toJSONString(config));
        if (config != null) {
            return config.getMarketHeadGap();
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BuildMarketPlayConfig queryBasketballBuildMarketConfig(Long matchId, Long playId) {
        BuildMarketPlayConfig buildMarketConfig;
        // 位置spread
        Map<Integer, BigDecimal> placeSpreadMap = Maps.newHashMap();
        placeSpreadMap.put(NumberUtils.INTEGER_ONE, RcsConstant.DEFAULT_SPREAD);
        // 位置水差
        Map<Integer, BigDecimal> placeWaterDiffMap = Maps.newHashMap();
        placeWaterDiffMap.put(NumberUtils.INTEGER_ONE, BigDecimal.ZERO);
        // 是否暂停
        boolean isTimeout = isTimeout(matchId);

        List<BuildMarketPlaceConfig> placeConfigList = rcsMatchMarketConfigMapper.getBuildMarketPlaceConfig(matchId, playId);
        log.info("A+模式构建盘口，实时位置配置：matchId={},playId={},placeConfigList={}", matchId, playId, JSON.toJSONString(placeConfigList));
        if (CollectionUtils.isNotEmpty(placeConfigList)) {
            placeConfigList.forEach(placeConfig -> {
                BigDecimal spread = isTimeout ? placeConfig.getPauseSpread() : placeConfig.getSpread();
                placeSpreadMap.put(placeConfig.getPlaceNum(), spread);
                placeWaterDiffMap.put(placeConfig.getPlaceNum(), placeConfig.getPlaceWaterDiff());
            });
            buildMarketConfig = rcsMatchMarketConfigMapper.getBuildMarketPlayConfig(matchId, playId);
            log.info("A+模式构建盘口，玩法配置：matchId={},playId={},placeConfigList={}", matchId, playId, JSON.toJSONString(buildMarketConfig));
        } else {
            List<BuildMarketPlayConfig> configList = rcsMatchMarketConfigMapper.queryBasketballBuildMarketConfig(matchId, playId);
            log.info("A+模式构建盘口，分时配置：matchId={},playId={},placeConfigList={}", matchId, playId, JSON.toJSONString(configList));
            int index;
            if (NumberUtils.INTEGER_ZERO.equals(configList.get(0).getMatchType())) {
                // 滚球
                index = configList.size() - 1;
            } else {
                // 早盘
                index = 0;
            }
            buildMarketConfig = configList.get(index);
            BigDecimal spread = isTimeout ? buildMarketConfig.getPauseSpreadValue() : buildMarketConfig.getSpreadValue();
            placeSpreadMap.put(NumberUtils.INTEGER_ONE, spread);
        }
        buildMarketConfig.setPlaceSpreadMap(placeSpreadMap);
        buildMarketConfig.setPlaceWaterDiffMap(placeWaterDiffMap);
        buildMarketConfig.setMarketHeadGap(getMarketHeadGap(matchId, playId));
        log.info("A+模式构建盘口配置：matchId={},playId={},placeConfigList={}", matchId, playId, JSON.toJSONString(buildMarketConfig));
        return buildMarketConfig;
    }

    private boolean isTimeout(Long matchId) {
        String redisKey = String.format("rcs:task:match:event:%s", matchId);
        String eventCode = redisClient.get(redisKey);
        log.info("A+模式构建盘口，事件编码：matchId={},eventCode={}", matchId, eventCode);
        return "timeout".equals(eventCode);
    }
}
