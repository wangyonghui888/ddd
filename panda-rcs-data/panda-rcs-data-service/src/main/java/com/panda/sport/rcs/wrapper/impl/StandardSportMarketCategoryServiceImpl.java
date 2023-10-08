package com.panda.sport.rcs.wrapper.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mapper.RcsLanguageInternationMapper;
import com.panda.sport.rcs.mapper.RcsStandardOutrightMatchInfoMapper;
import com.panda.sport.rcs.mapper.StandardSportMarketCategoryMapper;
import com.panda.sport.rcs.mapper.StandardSportMarketMapper;
import com.panda.sport.rcs.mapper.StandardSportMarketOddsMapper;
import com.panda.sport.rcs.mapper.StandardSportTournamentMapper;
import com.panda.sport.rcs.pojo.RcsLanguageInternation;
import com.panda.sport.rcs.pojo.RcsStandardOutrightMatchInfo;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.StandardSportMarketCategory;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import com.panda.sport.rcs.pojo.StandardSportTournament;
import com.panda.sport.rcs.utils.TwoLevelCacheUtil;
import com.panda.sport.rcs.vo.I18nItemVo;
import com.panda.sport.rcs.wrapper.ITOrderDetailService;
import com.panda.sport.rcs.wrapper.StandardSportMarketCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.panda.sport.rcs.cache.local.WebsocketConstants.*;
import static com.panda.sport.rcs.utils.TwoLevelCacheUtil.REDIS_PRE_KEY;


@Service
@Slf4j
public class StandardSportMarketCategoryServiceImpl extends ServiceImpl<StandardSportMarketCategoryMapper, StandardSportMarketCategory> implements StandardSportMarketCategoryService {

    @Autowired
    TwoLevelCacheUtil twoLevelCacheUtil;
    @Autowired
    private StandardSportMarketCategoryMapper standardSportMarketCategoryMapper;
    @Autowired
    private RcsLanguageInternationMapper rcsLanguageInternationMapper;
    @Autowired
    private StandardSportTournamentMapper tournamentMapper;
    @Autowired
    private RcsStandardOutrightMatchInfoMapper championMatchMapper;
    @Autowired
    private StandardSportMarketOddsMapper sportMarketOddsMapper;
    @Autowired
    private StandardSportMarketMapper sportMarketMapper;

    @Autowired
    private ITOrderDetailService orderDetailService;

    public static final String category = "Home";


    @Override
    public String getPlayName(Integer sportId, Integer categoryId, String languageType) {
        String playName = "";
        StandardSportMarketCategory category = this.queryCachedCategory(String.valueOf(sportId), categoryId.longValue());
        if (category != null && category.getNameCode() != null) {
            List<I18nItemVo> i18nItemVos = this.getCachedNamesByCode(category.getNameCode());
            playName = getLanguageName(i18nItemVos, languageType);

        }
        return playName;
    }

    @Override
    public List<I18nItemVo> getCachedNamesByCode(Long nameCode) {
        if (nameCode == null) {
            return null;
        }
        String namecode_key = "namecode:" + nameCode;
        String cachedLanguageStr = twoLevelCacheUtil.get(namecode_key, key -> {
            QueryWrapper<RcsLanguageInternation> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name_code", nameCode);
            List<RcsLanguageInternation> rcsLanguageInternations = rcsLanguageInternationMapper.selectList(queryWrapper);
            // 数据库也不存在则返回
            if (CollectionUtils.isEmpty(rcsLanguageInternations)) {
                return null;
            }
            RcsLanguageInternation rcsLanguageInternation = rcsLanguageInternations.get(0);
            Map<String, String> map = JSONObject.parseObject(rcsLanguageInternation.getText(), Map.class);
            List<I18nItemVo> resultList = new ArrayList<>();
            for (String lType : map.keySet()) {
                if ("zs".equals(lType) || "en".equals(lType)) {
                    I18nItemVo itemVo = new I18nItemVo();
                    itemVo.setLanguageType(lType);
                    itemVo.setText(map.get(lType));
                    resultList.add(itemVo);
                }
            }
            return JSONObject.toJSONString(resultList);
        });
        return JSONObject.parseArray(cachedLanguageStr, I18nItemVo.class);
    }

    @Override
    public StandardSportMarketCategory queryCachedCategory(String sportId, Long id) {

        if (id == null || id == 0L) {
            return null;
        }
        StandardSportMarketCategory category = null;
        String key = String.format(CACHE_CATEGORY, sportId, id);
        String value = twoLevelCacheUtil.get(key, key1 -> {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", id);
            map.put("sportId", sportId);
            StandardSportMarketCategory category0 = standardSportMarketCategoryMapper.queryCategoryInfoByMap(map);
            return JsonFormatUtils.toJson(category0);

        });
        return JsonFormatUtils.fromJson(value, StandardSportMarketCategory.class);
    }


    @Override
    public List<I18nItemVo> getTournmentName(Long tourId) {
        String tour_key = "tour:" + tourId.toString();
        String nameCodeRe = twoLevelCacheUtil.get(tour_key, key -> {
            Long nameCode = 0L;
            StandardSportTournament tournament = tournamentMapper.selectById(tourId);
            if (tournament != null) {
                nameCode = tournament.getNameCode();
            }
            return nameCode == null ? "" : nameCode.toString();
        });
        List<I18nItemVo> i18nItemVos = StringUtils.isNotBlank(nameCodeRe) ? this.getCachedNamesByCode(Long.parseLong(nameCodeRe)) : null;
        return i18nItemVos;
    }


    @Override
    public List<I18nItemVo> championMatchNameAllLanguage(Long matchId) {
        String matchId_key = "champion:" + matchId.toString();
        String nameCodeRe = twoLevelCacheUtil.get(matchId_key, key -> {
            RcsStandardOutrightMatchInfo outrightMatchInfo = championMatchMapper.selectById(matchId);
            Long nameCode = 0L;
            if (outrightMatchInfo != null) {
                nameCode = outrightMatchInfo.getNameCode();
            }
            return nameCode == null ? "" : nameCode.toString();
        });
        List<I18nItemVo> i18nItemVos = StringUtils.isNotBlank(nameCodeRe) ? this.getCachedNamesByCode(Long.parseLong(nameCodeRe)) : null;

        return i18nItemVos;
    }

    @Override
    public String queryChampionOptionValue(Long playOptionsId, String languageType) {
        String nameCodeRe = CHAMPION_OPTION_CACHE.get(playOptionsId, key -> {
            StandardSportMarketOdds odds = sportMarketOddsMapper.selectById(playOptionsId);
            Long nameCode = 0L;
            if (odds != null) {
                nameCode = odds.getNameCode();
            }
            return nameCode == null ? "" : nameCode.toString();
        });
        List<I18nItemVo> i18nItemVos = StringUtils.isNotBlank(nameCodeRe) ? this.getCachedNamesByCode(Long.parseLong(nameCodeRe)) : null;
        String oddsName = getLanguageName(i18nItemVos, languageType);
        return oddsName;
    }

    @Override
    public StandardSportMarket queryCacheMarket(Long marketId) {
        StandardSportMarket market = null;
        log.info("::{}::根据marketId获取赛事信息", marketId);
        if (marketId != null) {
            String key = CACHE_MARKET + marketId;
            String redisKey = String.format(REDIS_PRE_KEY, key);
            String value = twoLevelCacheUtil.getRedisClient().get(redisKey);
            log.info("::{}::获取到缓存赛事信息->{}", marketId,JSONObject.toJSON(value));
            if (StringUtils.isBlank(value)||"null".equalsIgnoreCase(value)) {
                value = twoLevelCacheUtil.getLongTimeCache(key, key1 -> {
                    StandardSportMarket sportMarket = sportMarketMapper.selectById(marketId);
                    if (sportMarket != null) {
                        log.info("::{}::获取到数据库赛事信息->{}", marketId, JSONObject.toJSON(sportMarket));
                        return JsonFormatUtils.toJson(sportMarket);
                    } else {
                        return "";
                    }
                });
            }
            market = JsonFormatUtils.fromJson(value, StandardSportMarket.class);
        }
        return market;
    }

    @Override
    public List<I18nItemVo> queryChampionPlayName(Long marketId) {
        StandardSportMarket market = this.queryCacheMarket(marketId);
        if (market != null) {
            Long nameCode = market.getNameCode();
            List<I18nItemVo> i18nItemVos = this.getCachedNamesByCode(nameCode);
            return i18nItemVos;
        }
        return new ArrayList();
    }

    @Override
    public StandardSportMarketOdds queryCacheMarketOdds(Long playOptionsId) {
        String jsonOdds = SPECIAL_OPTION_CACHE.get(playOptionsId, key -> {
            StandardSportMarketOdds odds = sportMarketOddsMapper.selectById(playOptionsId);
            String oddsStr = "";
            if (odds != null) {
                oddsStr = JsonFormatUtils.toJson(odds);
            }
            return oddsStr;
        });
        if (StringUtils.isNotBlank(jsonOdds)) {
            return JsonFormatUtils.fromJson(jsonOdds, StandardSportMarketOdds.class);
        }
        return null;
    }

    @Override
    public String specialOddsName(Integer playId, String languageType, StandardSportMarketOdds odds) {
        String oddsName = "";

        if (odds != null) {
            //赛事nameCode
            String playerId = odds.getAddition1();
            String oddsType = odds.getOddsType();
            if (playId.equals(337)) {
                String BK = " by KO";
                String BD = " by Decision";
                String BS = " Submission";
                String X = "Draw";
                if (oddsType.contains("BK")) oddsName = BK;
                if (oddsType.contains("BD")) oddsName = BD;
                if (oddsType.contains("BS")) oddsName = BS;

                if (oddsType.contains("X")) {
                    oddsName = X;
                } else {
                    String playerName = orderDetailService.queryOddsPlayer(Long.parseLong(playerId), languageType);
                    oddsName = playerName + oddsName;
                }

            }
            if (playId.equals(339)) {
                playerId = odds.getAddition2();
                String round = odds.getAddition3();
                String playerName = "";
                if (StringUtils.isNotBlank(playerId)) {
                    playerName = orderDetailService.queryOddsPlayer(Long.parseLong(playerId), languageType);
                }
                if (oddsType.contains("AndDecision")) {
                    oddsName = playerName + " by Decision";
                } else if (oddsType.contains("X")) {
                    oddsName = "draw";
                } else {
                    oddsName = playerName + " to Win In Round " + round;
                }
            }
        }

        return oddsName;
    }


    String getLanguageName(List<I18nItemVo> i18nItemVos, String languageType) {
        String text = "";
        if (!CollectionUtils.isEmpty(i18nItemVos)) {
            text = i18nItemVos.stream().filter(fi -> StringUtils.isNotBlank(fi.getLanguageType()) && languageType.equals(fi.getLanguageType())).map(I18nItemVo::getText).findFirst().orElse(null);
        }
        return text;
    }
}
