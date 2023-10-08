package com.panda.sport.rcs.mgr.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.champion.RcsChampionRiskConfigMapper;
import com.panda.sport.rcs.mgr.wrapper.RcsChampionRiskConfigService;
import com.panda.sport.rcs.pojo.RcsChampionRiskConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author :  kir
 * @Date: 2021-06-09
 * @Description :  冠军玩法操盘及限额管理
 */
@Service
@Slf4j
public class RcsChampionRiskConfigServiceImpl extends ServiceImpl<RcsChampionRiskConfigMapper, RcsChampionRiskConfig> implements RcsChampionRiskConfigService {

}
