package com.panda.sport.rcs.mgr.controller;

import com.panda.sport.rcs.enums.MatchTypeEnum;
import com.panda.sport.rcs.mapper.RcsBusinessConfigMapper;
import com.panda.sport.rcs.mgr.utils.PlayTypeConstants;
import com.panda.sport.rcs.mgr.wrapper.RcsBusinessPlayPaidConfigService;
import com.panda.sport.rcs.mgr.wrapper.RcsCodeService;
import com.panda.sport.rcs.mgr.wrapper.RcsLanguageInternationService;
import com.panda.sport.rcs.mgr.wrapper.StandardSportMarketCategoryService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsBusinessPlayPaidConfig;
import com.panda.sport.rcs.pojo.StandardSportMarketCategory;
import com.panda.sport.rcs.vo.HttpResponse;
import com.panda.sport.rcs.vo.I18nItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.mgr.mq.impl.BasicConfigProvider.msgConfTag;
import static com.panda.sport.rcs.mgr.wrapper.impl.RcsPaidConfigServiceImp.BUS_PLAY_CONFIG_KEY;

/**
 * @author :  kimi
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.bootstrap.controller
 * @Description :  玩法控制对外接口
 * @Date: 2019-10-04 11:17
 */
@RestController
@RequestMapping(value = "/playRestriction")
@Slf4j
public class RcsBusinessPlayPaidConfigController {
    //全场的id
    private static final int FULL_COURT = 3;
    //万分比
    private static final int EXTREME_RATIO = 10000;

    @Autowired
    private RcsBusinessPlayPaidConfigService rcsBusinessPlayPaidConfigService;
    @Autowired
    private RcsCodeService rcsCodeService;
    @Autowired
    private StandardSportMarketCategoryService standardSportMarketCategoryService;
    @Autowired
    private RcsLanguageInternationService languageInternationService;
    @Autowired
    private RcsBusinessConfigMapper rcsBusinessConfigMapper;
    @Autowired
    ProducerSendMessageUtils sendMessage;

    /**
     * @return
     * @Description 返回查询数据
     * @Param [rcsBusinessPlayPaidConfig]
     * @Author kimi
     * @Date 2019/10/4
     **/
    @RequestMapping(value = "/getRcsBusinessPlayPaidConfigList", method = RequestMethod.GET)
    public HttpResponse<List<RcsBusinessPlayPaidConfig>> getRcsBusinessPlayPaidConfigList(RcsBusinessPlayPaidConfig rcsBusinessPlayPaidConfig) {
        Map<String, Object> columnMap = new HashMap<>(4);
        Long businessId = rcsBusinessPlayPaidConfig.getBusinessId();
        Long sportId = rcsBusinessPlayPaidConfig.getSportId();
        Integer matchType = rcsBusinessPlayPaidConfig.getMatchType();
        Long playType = rcsBusinessPlayPaidConfig.getPlayType();
        if (businessId == null) {
            return HttpResponse.fail("businessId 不能为空");
        }
        if (sportId == null) {
            return HttpResponse.fail("sportId 不能为空");
        }
        if (matchType == null) {
            return HttpResponse.fail("matchType 不能为空");
        }
        if (playType == null) {
            return HttpResponse.fail("playType 不能为空");
        }
        columnMap.put("sport_id", sportId);
        columnMap.put("match_type", matchType);
        columnMap.put("business_id", businessId);
        columnMap.put("play_type", playType);
        Map<String, Object> resultMap = new HashMap(1);
        List<RcsBusinessPlayPaidConfig> rcsBusinessPlayPaidConfigList;
        try {
            rcsBusinessPlayPaidConfigList = rcsBusinessPlayPaidConfigService.getRcsBusinessPlayPaidConfigList(columnMap);
            //未进行数据的初始化
            boolean init = false;
            if (rcsBusinessPlayPaidConfigList != null && rcsBusinessPlayPaidConfigList.size() > 0) {
                for (RcsBusinessPlayPaidConfig rcsBusinessPlayPaidConfig1 : rcsBusinessPlayPaidConfigList) {
                    if (rcsBusinessPlayPaidConfig1.getPlayId() == -1) {
                        init = true;
                        break;
                    }
                }
            }
            if (!init) {
                initRcsBusinessPlayPaid(businessId, sportId, matchType, playType);
                rcsBusinessPlayPaidConfigList = rcsBusinessPlayPaidConfigService.getRcsBusinessPlayPaidConfigList(columnMap);
            }
            for (RcsBusinessPlayPaidConfig rcsBusinessPlayPaidConfig1 : rcsBusinessPlayPaidConfigList) {
                if (rcsBusinessPlayPaidConfig1.getPlayId() != -1L) {
                    StandardSportMarketCategory cachedMarketCategoryById = standardSportMarketCategoryService.getCachedMarketCategoryById(String.valueOf(sportId), (long) rcsBusinessPlayPaidConfig1.getPlayId());
                    List<I18nItemVo> cachedNamesByCode = languageInternationService.getCachedNamesByCode(cachedMarketCategoryById.getNameCode());
                    for (I18nItemVo i18nItemVo : cachedNamesByCode) {
                        if (i18nItemVo.getLanguageType().equals("zs")) {
                            rcsBusinessPlayPaidConfig1.setName(i18nItemVo.getText());
                        }
                        if (i18nItemVo.getLanguageType().equals("en")) {
                            rcsBusinessPlayPaidConfig1.setEnName(i18nItemVo.getText());
                        }
                        resultMap.put("rcsBusinessPlayPaidConfigList", rcsBusinessPlayPaidConfigList);
                    }
                } else {
                    rcsBusinessPlayPaidConfig1.setName("其他所有玩法");
                    rcsBusinessPlayPaidConfig1.setEnName("All other ways to play");
                }
            }
            //获取其他所有玩法 进行换位置 换到最后
            RcsBusinessPlayPaidConfig rcsBusinessPlayPaidConfig2 = null;
            if (rcsBusinessPlayPaidConfigList.size() > 1) {
                for (RcsBusinessPlayPaidConfig rcsBusinessPlayPaidConfig1 : rcsBusinessPlayPaidConfigList) {
                    if (rcsBusinessPlayPaidConfig1.getPlayId() == -1L) {
                        rcsBusinessPlayPaidConfig2 = rcsBusinessPlayPaidConfig1;
                        break;
                    }
                }
            }
            if (rcsBusinessPlayPaidConfigList.size() > 1) {
                rcsBusinessPlayPaidConfigList.remove(rcsBusinessPlayPaidConfig2);
                rcsBusinessPlayPaidConfigList.add(rcsBusinessPlayPaidConfig2);
            }
            resultMap.put("rcsBusinessPlayPaidConfigList", rcsBusinessPlayPaidConfigList);
            return HttpResponse.success(resultMap);
        } catch (Exception e) {
            log.error("::rcsBusinessPlayPaidConfigList{}:: ERROR{},{}",rcsBusinessPlayPaidConfig.getId(),e.getMessage(), e);
            return HttpResponse.fail(e.getMessage());
        }
    }

    @RequestMapping(value = "/updateRcsBusinessPlayPaidConfig", method = RequestMethod.POST)
    public HttpResponse<RcsBusinessPlayPaidConfig> updateRcsBusinessPlayPaidConfig(@RequestBody RcsBusinessPlayPaidConfig rcsBusinessPlayPaidConfig) {
        Long orderMaxPay = rcsBusinessPlayPaidConfig.getOrderMaxPay();
        Integer id = rcsBusinessPlayPaidConfig.getId();
        if (id == null) {
            return HttpResponse.fail("传入参数错误,id不能为空");
        }
        if (orderMaxPay != null) {
            return HttpResponse.fail("传入参数错误");
        }
        Long playMaxPay = rcsBusinessPlayPaidConfig.getPlayMaxPay();
        if (playMaxPay != null) {
            return HttpResponse.fail("传入参数错误");
        }
        Integer orderMaxRate = rcsBusinessPlayPaidConfig.getOrderMaxRate();
        if (orderMaxRate != null && orderMaxRate < 0) {
            return HttpResponse.fail("orderMaxRate 传入参数不能为负数");
        }
        Integer playMaxRate = rcsBusinessPlayPaidConfig.getPlayMaxRate();
        if (playMaxRate != null && playMaxRate < 0) {
            return HttpResponse.fail("playMaxRate 传入参数不能为负数");
        }
        try {
            long base = rcsCodeService.getRcsCodeList("rcsBusinessPlay", "base");
            RcsBusinessPlayPaidConfig byId = rcsBusinessPlayPaidConfigService.getById(id);
            if (byId.getPlayType() == FULL_COURT) {
                rcsBusinessPlayPaidConfig.setPlayMaxPay(base * playMaxRate / EXTREME_RATIO);
                rcsBusinessPlayPaidConfig.setOrderMaxPay(base * orderMaxRate / EXTREME_RATIO);
            } else {
                rcsBusinessPlayPaidConfig.setPlayMaxPay(base * playMaxRate / EXTREME_RATIO);
                rcsBusinessPlayPaidConfig.setOrderMaxPay(base * orderMaxRate / EXTREME_RATIO);
            }
            rcsBusinessPlayPaidConfigService.updateRcsBusinessPlayPaidConfig(rcsBusinessPlayPaidConfig);

            List<RcsBusinessPlayPaidConfig> playList = rcsBusinessConfigMapper.queryBusPlayConifgList();
            Map<Long, List<RcsBusinessPlayPaidConfig>> result = playList.stream().filter(m -> m.getBusinessId().equals(rcsBusinessPlayPaidConfig.getBusinessId())).collect(Collectors.groupingBy(RcsBusinessPlayPaidConfig::getBusinessId));
            if (result != null && result.size() > 0) {
                for (Long busId : result.keySet()) {
                    sendMessage.sendMessage(msgConfTag, BUS_PLAY_CONFIG_KEY, String.valueOf(busId), result.get(busId));
                }
            }

            return HttpResponse.success(rcsBusinessPlayPaidConfig);
        } catch (Exception e) {
            log.error("::updateRcsBusinessPlayPaidConfig{}:: ERROR{},{}",rcsBusinessPlayPaidConfig.getId(),e.getMessage(), e);
            return HttpResponse.fail(e.getMessage());
        }
    }


    /**
     * @return void
     * @Description //初始化数据
     * @Param [businessId, sportId, matchType, playType]
     * @Author kimi
     * @Date 2019/10/24
     **/

    private void initRcsBusinessPlayPaid(Long businessId, Long sportId, Integer matchType, long playType) {
        List<RcsBusinessPlayPaidConfig> list = new ArrayList<>();
        int[] ints = PlayTypeConstants.get(sportId, playType);
        try {
            for (MatchTypeEnum matchTypeEnum : MatchTypeEnum.values()) {
                RcsBusinessPlayPaidConfig rcsBusinessPlayPaidConfig = newRcsBusinessPlayPaidConfig(businessId, sportId, matchTypeEnum.getId(), playType, -1L, "其他玩法");
                list.add(rcsBusinessPlayPaidConfig);
                if (ints != null && ints.length > 0) {
                    for (int x : ints) {
                        StandardSportMarketCategory cachedMarketCategoryById = standardSportMarketCategoryService.getCachedMarketCategoryById(String.valueOf(sportId), (long) x);
                        List<I18nItemVo> cachedNamesByCode = languageInternationService.getCachedNamesByCode(cachedMarketCategoryById.getNameCode());
                        for (I18nItemVo i18nItemVo : cachedNamesByCode) {
                            if (i18nItemVo.getLanguageType().equals("zs")) {
                                RcsBusinessPlayPaidConfig rcsBusinessPlayPaidConfig1 = newRcsBusinessPlayPaidConfig(businessId, sportId, matchTypeEnum.getId(), playType, (long) x, i18nItemVo.getText());
                                list.add(rcsBusinessPlayPaidConfig1);
                            }
                        }
                    }
                }
            }
            rcsBusinessPlayPaidConfigService.insertRcsBusinessPlayPaidConfigList(list);
        } catch (Exception e) {
            log.error("::initRcsBusinessPlayPaid{}:: ERROR{},{}",businessId,e.getMessage(), e);
        }
    }


    /**
     * @return com.panda.sport.rcs.pojo.RcsBusinessPlayPaidConfig
     * @Description //新建数据库模型
     * @Param [businessId, sportId, matchType, playType, playId, name]
     * @Author kimi
     * @Date 2019/10/23
     **/

    private RcsBusinessPlayPaidConfig newRcsBusinessPlayPaidConfig(Long businessId, Long sportId, Integer matchType, Long playType, Long playId, String name) {
        //取得基础数据
        try {
            long base = rcsCodeService.getRcsCodeList("rcsBusinessPlay", "base");
            long percentage = rcsCodeService.getRcsCodeList("rcsBusinessPlay", "percentage");
            RcsBusinessPlayPaidConfig rcsBusinessPlayPaidConfig = new RcsBusinessPlayPaidConfig();
            rcsBusinessPlayPaidConfig.setBusinessId(businessId);
            rcsBusinessPlayPaidConfig.setSportId(sportId);
            rcsBusinessPlayPaidConfig.setMatchType(matchType);
            rcsBusinessPlayPaidConfig.setPlayType(playType);
            rcsBusinessPlayPaidConfig.setOrderMaxRate((int) percentage);
            rcsBusinessPlayPaidConfig.setPlayMaxRate((int) percentage);
            if (playId == null) {
                rcsBusinessPlayPaidConfig.setPlayId(-1L);
            } else {
                rcsBusinessPlayPaidConfig.setPlayId(playId);
            }
            if (name == null) {
                rcsBusinessPlayPaidConfig.setName("其他所有玩法");
            } else {
                rcsBusinessPlayPaidConfig.setName(name);
            }
            rcsBusinessPlayPaidConfig.setOrderMaxPay(base * percentage / EXTREME_RATIO);
            rcsBusinessPlayPaidConfig.setPlayMaxPay(base * percentage / EXTREME_RATIO);
            return rcsBusinessPlayPaidConfig;
        } catch (Exception e) {
            log.error("::newRcsBusinessPlayPaidConfig:: ERROR{},{}",e.getMessage(), e);
            return null;
        }
    }
}
