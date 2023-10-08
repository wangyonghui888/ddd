package com.panda.sport.rcs.mgr.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.common.NumberUtils;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.RcsBusinessSingleBetConfigMapper;
import com.panda.sport.rcs.mgr.wrapper.*;
import com.panda.sport.rcs.pojo.RcsBusinessSingleBetConfig;
import com.panda.sport.rcs.vo.BusinessSingleBetAndPlayVo;
import com.panda.sport.rcs.vo.BusinessSingleBetVo;
import com.panda.sport.rcs.vo.TournamentVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.impl
 * @Description :  TODO
 * @Date: 2019-11-22 15:04
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Service
@Slf4j
public class RcsBusinessSingleBetConfigServiceImpl extends ServiceImpl<RcsBusinessSingleBetConfigMapper, RcsBusinessSingleBetConfig> implements RcsBusinessSingleBetConfigService {
    @Autowired
    RcsBusinessSingleBetConfigService businessSingleBetConfigService;
    @Autowired
    private RcsCodeService rcsCodeService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private RcsBusinessSingleBetConfigMapper businessSingleBetConfigMapper;
    @Autowired
    private RcsBusinessMatchPaidConfigService rcsBusinessMatchPaidConfigService;
    @Autowired
    private RcsLanguageInternationService languageInternationService;
    @Autowired
    private static HashMap<String, String> hashMap;

    @Override
    public List<RcsBusinessSingleBetConfig> selectBusinessSingleBetConfigList(RcsBusinessSingleBetConfig businessSingleBetConfig) {
        QueryWrapper<RcsBusinessSingleBetConfig> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(RcsBusinessSingleBetConfig::getBusinessId, businessSingleBetConfig.getBusinessId());
        wrapper.lambda().eq(RcsBusinessSingleBetConfig::getSportId, businessSingleBetConfig.getSportId());
        wrapper.lambda().eq(RcsBusinessSingleBetConfig::getMatchType, businessSingleBetConfig.getMatchType());
        wrapper.lambda().eq(RcsBusinessSingleBetConfig::getTimePeriod, businessSingleBetConfig.getTimePeriod());
        //增加排序支持
        wrapper.orderByDesc("order_number");
        if (businessSingleBetConfig.getTournamentLevel() != null) {
            wrapper.lambda().eq(RcsBusinessSingleBetConfig::getTournamentLevel, businessSingleBetConfig.getTournamentLevel());
        }
        List<RcsBusinessSingleBetConfig> list = businessSingleBetConfigService.list(wrapper);
        if (list.size() <= 0) {
            initBusinessSingleBetConfig(businessSingleBetConfig);
            list = businessSingleBetConfigService.list(wrapper);
        }
        return list;
    }

    @Override
    @Transactional
    public BusinessSingleBetAndPlayVo selectBusinessSingleBetConfigView(RcsBusinessSingleBetConfig businessSingleBetConfig) {
        List<RcsBusinessSingleBetConfig> list = selectBusinessSingleBetConfigList(businessSingleBetConfig);
        List<BusinessSingleBetVo> businessSingleBetVos = new ArrayList<>();
        if (list.size() > 0) {
            List<Integer> integers = businessSingleBetConfigMapper.selectListTournamentLevels(businessSingleBetConfig);
            for (Integer o : integers) {
                if (o == 99) {
                    o = 0;
                }
                BusinessSingleBetVo vo = new BusinessSingleBetVo();
                businessSingleBetConfig.setTournamentLevel(o);
                List<RcsBusinessSingleBetConfig> configs = selectBusinessSingleBetConfigList(businessSingleBetConfig);
                vo.setTournamentLevel(o);
                vo.setSingleBetConfigs(configs);
                businessSingleBetVos.add(vo);
            }
        }
        BusinessSingleBetAndPlayVo businessSingleBetAndPlayVo = new BusinessSingleBetAndPlayVo();
        businessSingleBetAndPlayVo.setBusinessSingleBetVoList(businessSingleBetVos);
        if (!CollectionUtils.isEmpty(businessSingleBetVos)) {
            for (BusinessSingleBetVo businessSingleBetVo : businessSingleBetVos) {
                List<RcsBusinessSingleBetConfig> singleBetConfigs = businessSingleBetVo.getSingleBetConfigs();
                if (!CollectionUtils.isEmpty(singleBetConfigs)) {
                    sort(singleBetConfigs);
                }
            }
        }
        //多语言表
        Set<Long> playIds = new HashSet<>();
        if (!CollectionUtils.isEmpty(businessSingleBetVos)) {
            BusinessSingleBetVo businessSingleBetVo = businessSingleBetVos.get(0);
            List<RcsBusinessSingleBetConfig> singleBetConfigs = businessSingleBetVo.getSingleBetConfigs();
            for (RcsBusinessSingleBetConfig rcsBusinessSingleBetConfig : singleBetConfigs) {
                playIds.add(rcsBusinessSingleBetConfig.getPlayId().longValue());
            }
        }
        Map<Long, Map<String, String>> longMapMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(playIds)) {
            longMapMap = languageInternationService.selectLanguageInternationByPlayId(playIds);
        }
        if (CollectionUtils.isEmpty(hashMap)) {
            hashMap = new HashMap<>();
            hashMap.put("zs", "其他");
            hashMap.put("en", "other");
        }
        longMapMap.put(-1L, hashMap);
        businessSingleBetAndPlayVo.setLongI18nBeanMap(longMapMap);
        return businessSingleBetAndPlayVo;
    }


    private void sort(List<RcsBusinessSingleBetConfig> singleBetConfigs) {
        Collections.sort(singleBetConfigs, new Comparator<RcsBusinessSingleBetConfig>() {
            @Override
            public int compare(RcsBusinessSingleBetConfig o1, RcsBusinessSingleBetConfig o2) {
                Integer playId = o1.getPlayId();
                if (playId == -1) {
                    return 1;
                }
                Integer playId1 = o2.getPlayId();
                if (playId1 == -1) {
                    return -1;
                }
                if (playId > playId1) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBusinessSingleBetConfig(List<RcsBusinessSingleBetConfig> businessSingleBetConfigs) {
        long base = rcsCodeService.getRcsCodeList("rcsBusinesssSingleBet", "amount");
        businessSingleBetConfigs.stream().forEach(model -> {
            if (model.getId() != null) {
                model.setOrderMaxValue(model.getOrderMaxRate().multiply(NumberUtils.getBigDecimal(base)).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
            }
        });
        return businessSingleBetConfigService.updateBatchById(businessSingleBetConfigs);
    }

    @Override
    public void initBusinessSingleBetConfig(RcsBusinessSingleBetConfig businessSingleBetConfig) {
        List<RcsBusinessSingleBetConfig> list = new ArrayList<>();
        String s = initTimePeriod(businessSingleBetConfig.getTimePeriod(), businessSingleBetConfig.getSportId());
        String[] categories = s.split(",");
        try {
            long base = rcsCodeService.getRcsCodeList("rcsBusinesssSingleBet", "amount");
            if (categories != null && categories.length > 0) {
                for (String categoryId : categories) {
                    if (StringUtils.isNotBlank(categoryId)) {
                        TournamentVo tournamentVo = new TournamentVo();
                        tournamentVo.setSportId(businessSingleBetConfig.getSportId());
                        tournamentVo.setTournamentLevel("tournamentLevel");
                        List<TournamentVo> tournamentVos = rcsBusinessMatchPaidConfigService.selectTournaments(tournamentVo);
                        if (tournamentVos.size() > 0) {
                            tournamentVos.forEach(model -> {
                                RcsBusinessSingleBetConfig config = new RcsBusinessSingleBetConfig();
                                config.setBusinessId(businessSingleBetConfig.getBusinessId());
                                config.setMatchType(businessSingleBetConfig.getMatchType());
                                config.setTimePeriod(businessSingleBetConfig.getTimePeriod());
                                config.setSportId(businessSingleBetConfig.getSportId());
                                config.setPlayId(Integer.parseInt(categoryId));
                                config.setTournamentLevel(Integer.parseInt(model.getValue()));
                                Integer rate = getPercent(Integer.parseInt(model.getValue()));
                                config.setOrderMaxRate(NumberUtils.getBigDecimal(rate));
                                config.setOrderMaxValue(NumberUtils.getBigDecimal(base).multiply(NumberUtils.getBigDecimal(rate)).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
                                config.setStatus(1);
                                list.add(config);
                            });
                        }
                    }
                }
            }
            businessSingleBetConfigService.saveBatch(list);
        } catch (Exception e) {
            log.error("::{}::初始化单关商户配置失败{}" ,businessSingleBetConfig.getBusinessId(), e.getMessage());
        }
    }

    public Integer getPercent(Integer level) {
        if (level == 1 || level.equals(1)) {
            return 100;
        } else if (level == 2 || level.equals(2)) {
            return 80;
        } else if (level == 3 || level.equals(3)) {
            return 50;
        } else if (level == 4 || level.equals(4)) {
            return 40;
        } else if (level == 5 || level.equals(5)) {
            return 30;
        } else if (level == 6 || level.equals(6)) {
            return 20;
        } else if (level >= 7) {
            return 10;
        } else {
            return 10;
        }
    }

    public String initTimePeriod(Integer timePeriod, Long sportId) {
        //足球
        String values = "";
        if (sportId == 1) {
            switch (timePeriod) {
                case 1:
                    values = "19,18,17,42,20,70,-1";
                    break;
                case 2:
                    values = "26,25,75,74,72,143,-1";
                    break;
                case 3:
                    values = "4,2,1,15,6,7,104,-1";
                    break;
                case 4:
                    values = "32,33,34,-1";
                    break;
                default:
                    values = "";
                    break;
            }
        } else if (sportId == 2) {
            switch (timePeriod) {
                case 3:
                    values = "39,38,37,40,-1";
                    break;
                case 13:
                    values = "45,46,47,48,51,52,53,54,57,58,59,60,63,64,65,66,-1";
                    break;
                case 1:
                    values = "19,18,43,42,-1";
                    break;
                case 2:
                    values = "143,26,142,75,-1";
                    break;
                default:
                    values = "";
                    break;
            }
        } else if (sportId == 5) {
            switch (timePeriod) {
                case 3:
                    values = "155,169,153,160,-1";
                    break;
                case 14:
                    values = "163,164,162,165,-1";
                    break;
                case 17:
                    values = "168,-1";
                    break;
                default:
                    values = "";
                    break;
            }
        } else if (sportId == 8) {
            switch (timePeriod) {
                case 3:
                    values = "153,173,172,-1";
                    break;
                case 17:
                    values = "175,176,177,178,-1";
                    break;
                default:
                    values = "";
                    break;
            }
        } else if (sportId == 10) {
            switch (timePeriod) {
                case 3:
                    values = "153,173,172,-1";
                    break;
                case 17:
                    values = "175,176,177,178,179,-1";
                    break;
                default:
                    values = "";
                    break;
            }
        } else if (sportId == 7) {
            switch (timePeriod) {
                case 3:
                    values = "181,182,183,153,-1";
                    break;
                case 17:
                    values = "185,186,184,187,-1";
                    break;
                default:
                    values = "";
                    break;
            }
        } else if (sportId == 3) {
            return "-1";
        }
        return values;
    }
}


