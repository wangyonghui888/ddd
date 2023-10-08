package com.panda.sport.rcs.wrapper.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mapper.StandardSportTeamMapper;
import com.panda.sport.rcs.mongo.I18nBean;
import com.panda.sport.rcs.pojo.StandardSportTeam;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.utils.TwoLevelCacheUtil;
import com.panda.sport.rcs.vo.I18nItemVo;
import com.panda.sport.rcs.vo.SportTeam;
import com.panda.sport.rcs.wrapper.LanguageInternationalService;
import com.panda.sport.rcs.wrapper.StandardSportTeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Project Name : rcs-parent
 * @Package Name : rcs-parent
 * @Description : 标准球队信息
 * @Author : Paca
 * @Date : 2020-11-22 10:35
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@Slf4j
public class StandardSportTeamServiceImpl extends ServiceImpl<StandardSportTeamMapper, StandardSportTeam> implements StandardSportTeamService {

    @Autowired
    TwoLevelCacheUtil twoLevelCacheUtil;

    @Autowired
    StandardSportTeamMapper standardSportTeamMapper;

    @Autowired
    private LanguageInternationalService languageInternationalService;

    public static final String TEAMS_LANGUAGE = "teamsLanguage:";

    @Override
    public Map<String, I18nBean> selectTeamsByMatchId(Long matchId) {
        Map<String, SportTeam> cacheMap = null;
        String key =TEAMS_LANGUAGE + matchId;
        String value = twoLevelCacheUtil.get(key, key1-> {
                List<SportTeam> list = standardSportTeamMapper.selectTeamsByMatchId(matchId);
                if (CollectionUtils.isNotEmpty(list)) {
                    Map<String, SportTeam> cacheMap1 = list.stream().collect(Collectors.toMap(SportTeam::getMatchPosition, Function.identity()));
                    return JsonFormatUtils.toJson(cacheMap1);
                }
                return "";
        });
        if (CommonUtils.isNotBlankAndNull(value)) {
            cacheMap = JsonFormatUtils.fromJsonMap(value, String.class, SportTeam.class);
        } else {
//            log.info("pushMarketOdds-flag-4-a");
            List<SportTeam> list = this.baseMapper.selectTeamsByMatchId(matchId);
//            log.info("pushMarketOdds-flag-4-b");
            if (CollectionUtils.isNotEmpty(list)) {
                cacheMap = list.stream().collect(Collectors.toMap(SportTeam::getMatchPosition, Function.identity()));
//                log.info("pushMarketOdds-flag-5-a");
                twoLevelCacheUtil.getRedisClient().setExpiry(String.format(TwoLevelCacheUtil.REDIS_PRE_KEY,key.toString()), cacheMap, 60 * 60L);
//                log.info("pushMarketOdds-flag-5-b");
            }
        }
        if (CollectionUtils.isNotEmpty(cacheMap)) {
            Map<String, I18nBean> map = Maps.newHashMap();
            cacheMap.forEach((k, v) -> {
                I18nBean i18nBean = new I18nBean();
                List<I18nItemVo> list = languageInternationalService.getCachedNamesByCode(v.getNameCode());
                if (CollectionUtils.isNotEmpty(list)) {
                    list.forEach(i18nItemVo -> {
                        String type = i18nItemVo.getLanguageType();
                        String text = i18nItemVo.getText();
                        if ("zs".equals(type)) {
                            i18nBean.setZs(text);
                        }
                        if ("en".equals(type)) {
                            i18nBean.setEn(text);
                        }
                        if ("jc".equals(type)) {
                            i18nBean.setJc(text);
                        }
                        if ("zh".equals(type)) {
                            i18nBean.setZh(text);
                        }
                    });
                }
                map.put(k, i18nBean);
            });
            return map;
        }
        return null;
    }
}
