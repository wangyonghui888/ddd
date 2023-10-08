package com.panda.sport.rcs.mgr.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.champion.RcsChampionTradeConfigMapper;
import com.panda.sport.rcs.mgr.wrapper.RcsChampionTradeConfigService;
import com.panda.sport.rcs.pojo.RcsChampionTradeConfig;
import com.panda.sport.rcs.pojo.vo.api.response.RcsChampionOddsFieldsResVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author :  kir
 * @Description :  冠军玩法操盘及限额管理
 * @Date: 2021-06-09
 */
@Service
@Slf4j
public class RcsChampionTradeConfigServiceImpl extends ServiceImpl<RcsChampionTradeConfigMapper, RcsChampionTradeConfig> implements RcsChampionTradeConfigService {

    @Autowired
    private RcsChampionTradeConfigMapper mapper;

    @Override
    public Integer selectMatchStatus(String marketId) {
        return mapper.selectMatchStatus(marketId);
    }

    @Override
    public List<RcsChampionOddsFieldsResVo> selectOddsFieldsList(String marketId) {
        return mapper.selectOddsFieldsList(marketId);
    }

    @Override
    public List<Map<String, Object>> selectBetAmount(String matchId, Integer playId, String marketId) {
        return mapper.selectBetAmount(matchId, playId, marketId);
    }
}
