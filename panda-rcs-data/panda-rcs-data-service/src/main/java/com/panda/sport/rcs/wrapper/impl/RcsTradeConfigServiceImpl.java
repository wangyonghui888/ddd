package com.panda.sport.rcs.wrapper.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.enums.TradeEnum;
import com.panda.sport.rcs.enums.TradeLevelEnum;
import com.panda.sport.rcs.mapper.RcsStandardOutrightMatchInfoMapper;
import com.panda.sport.rcs.mapper.RcsTradeConfigMapper;
import com.panda.sport.rcs.pojo.RcsStandardOutrightMatchInfo;
import com.panda.sport.rcs.pojo.RcsTradeConfig;
import com.panda.sport.rcs.wrapper.RcsTradeConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: jstyChandler
 * @Package: com.panda.sport.rcs.wrapper.impl
 * @ClassName: RcsTradeConfigServiceImpl
 * @Description: TODO
 * @Date: 2022/10/10 18:52
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@Slf4j
public class RcsTradeConfigServiceImpl extends ServiceImpl<RcsTradeConfigMapper, RcsTradeConfig> implements RcsTradeConfigService {

    @Autowired
    private RcsStandardOutrightMatchInfoMapper standardOutrightMatchInfoMapper;

    @Override
    public String[] getDataSource(String linkId, Long matchId, Long marketId, String dataSource) {
        String[] configArray = new String[2];
        RcsStandardOutrightMatchInfo outrightMatchInfo = standardOutrightMatchInfoMapper.selectById(matchId);
        if (StringUtils.isBlank(dataSource) || ObjectUtils.isEmpty(outrightMatchInfo)) {
            return null;
        }
        LambdaQueryWrapper<RcsTradeConfig> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RcsTradeConfig::getMatchId, matchId.toString())
                .eq(RcsTradeConfig::getTraderLevel, TradeLevelEnum.MARKET.getLevel())
                .eq(RcsTradeConfig::getTargerData, marketId.toString())
                .isNotNull(RcsTradeConfig::getDataSource)
                .orderByDesc(RcsTradeConfig::getId)
                .last("LIMIT 1");
        RcsTradeConfig config = this.getOne(wrapper);
        if(null == config){
            return null;
        }
        log.info("::{}::赛事标准赔率冠军玩法兜底查询操盘记录->datasource:{},status:{}", linkId, config.getDataSource(), config.getStatus());
        if(config.getDataSource().equals(TradeEnum.AUTO.getCode())){
            configArray[0] = "SR";
        }else{
            configArray[0] = "PA";
        }
        configArray[1] = config.getStatus()+"";
        return configArray;
    }
}
