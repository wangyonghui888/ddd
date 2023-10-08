package com.panda.sport.rcs.task.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.constants.RedisKey;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mapper.MarketCategorySetMapper;
import com.panda.sport.rcs.mapper.StandardSportMarketCategoryMapper;
import com.panda.sport.rcs.pojo.RcsCode;
import com.panda.sport.rcs.pojo.StandardSportMarketCategory;
import com.panda.sport.rcs.task.utils.RcsLocalCacheUtils;
import com.panda.sport.rcs.task.utils.StandardSportMarketCategoryCacheUtils;
import com.panda.sport.rcs.task.wrapper.RcsCodeService;
import com.panda.sport.rcs.task.wrapper.StandardSportMarketCategoryService;
import com.panda.sport.rcs.vo.CategoryConVo;
import com.panda.sport.rcs.vo.CategoryTemplateVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.constants.RedisKey.*;
import static com.panda.sport.rcs.constants.RedisKeys.CATEGORY_PHASE_CACHE;
import static com.panda.sport.rcs.constants.RedisKeys.EXP_TIME;
import static com.panda.sport.rcs.constants.RedisKeys.RCSCACHE_MARKETODDS_MARKET_CATEGORY;
import static com.panda.sport.rcs.constants.RedisKeys.RCS_CODE_CACHE;

/**
 * <p>
 * 标准玩法表 服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
@Service
@Slf4j
public class StandardSportMarketCategoryServiceImpl extends ServiceImpl<StandardSportMarketCategoryMapper, StandardSportMarketCategory> implements StandardSportMarketCategoryService {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RcsCodeService rcsCodeService;

    @Autowired
    private StandardSportMarketCategoryMapper standardSportMarketCategoryMapper;

    @Autowired
    private MarketCategorySetMapper marketCategorySetMapper;

    /**
     * 根据ID获取缓存中的玩法，不存在则从数据库获取并缓存
     *
     * @param id
     * @return
     */
    @Override
    public StandardSportMarketCategory getCachedMarketCategoryById(String sportId, Long id) {
        if (id == null || id == 0L) {
            return null;
        }

        String key = String.format("%s_%s", sportId, id);
        StandardSportMarketCategory standardSportMarketCategory = StandardSportMarketCategoryCacheUtils.timedCache.get(RedisKeys.RCSCACHE_MARKETODDS_MARKET_CATEGORY + key, false);
        if (standardSportMarketCategory != null) {
            return standardSportMarketCategory;
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);
        map.put("sportId", sportId);
        StandardSportMarketCategory marketCategory = standardSportMarketCategoryMapper.queryCategoryInfoByMap(map);
        if (marketCategory != null) {
            log.info("Can not found cached category:{}, reload from database.", id);
            StandardSportMarketCategoryCacheUtils.timedCache.put(RedisKeys.RCSCACHE_MARKETODDS_MARKET_CATEGORY + key, marketCategory);
        } else {
            log.error("Can not found database category:{},.", id);
        }
        log.warn("Can not found cached category:{}, database also not exists.", id);
        return marketCategory;
    }

    @Override
    public StandardSportMarketCategory queryCachedCategory(String sportId, Long id) {
        if (id == null || id == 0L) {
            return null;
        }
        StandardSportMarketCategory category = null;
        String key = String.format(CACHE_CATEGORY, sportId, id);
        String value = RcsLocalCacheUtils.getValue(key, redisClient::get, 5 * 60000L);
        if (StringUtils.isBlank(value)) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", id);
            map.put("sportId", sportId);
            category = standardSportMarketCategoryMapper.queryCategoryInfoByMap(map);
            if (null != category) {
                redisClient.setExpiry(key, JsonFormatUtils.toJson(category), EXPRIY_TIME_7_DAYS);
            }
        } else {
            category = JsonFormatUtils.fromJson(value, StandardSportMarketCategory.class);

        }
        return category;
    }

    @Override
    public Integer selectPlayPhase(Long categoryId) {
        String result = "";
        String key = String.format(RCS_CODE_CACHE, "category_phase_id", categoryId);
        boolean exist = redisClient.exist(key);
        if (exist) {
            result = redisClient.get(key);
        } else {
            if (StringUtils.isNotBlank(redisClient.get(CATEGORY_PHASE_CACHE))) {
                List<RcsCode> rcsCodes = JsonFormatUtils.fromJsonArray(redisClient.get(CATEGORY_PHASE_CACHE), RcsCode.class);
                for (RcsCode rcsCode : rcsCodes) {
                    if (String.valueOf(categoryId).equals(rcsCode.getChildKey())) {
                        result = rcsCode.getRemark();
                    }
                }
            } else {
                List<RcsCode> ballPhase = rcsCodeService.selectRcsCods("play_phase");
                if (ballPhase.size() > 0) {
                    redisClient.setExpiry(CATEGORY_PHASE_CACHE, JsonFormatUtils.toJson(ballPhase), EXP_TIME);
                }
            }
        }
        if (StringUtils.isNotBlank(result)) {
            return Integer.parseInt(result);
        }
        return 0;
    }

    @Override
    public List<Integer> selectCategoryIds(List<Integer> playPhases) {
        List<Integer> categoryIds = new ArrayList<>();
        List<RcsCode> collect = new ArrayList<>();
        if (StringUtils.isNotBlank(redisClient.get(CATEGORY_PHASE_CACHE))) {
            List<RcsCode> rcsCodes = JsonFormatUtils.fromJsonArray(redisClient.get(CATEGORY_PHASE_CACHE), RcsCode.class);
            if (null != playPhases && playPhases.size() > 0) {
                collect = rcsCodes.stream().filter(line -> playPhases.contains(Integer.parseInt(line.getRemark()))).collect(Collectors.toList());
            } else {
                collect = rcsCodes;
            }
        } else {
            QueryWrapper<RcsCode> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(RcsCode::getFatherKey, "play_phase");
            if (null != playPhases && playPhases.size() > 0) {
                wrapper.lambda().in(RcsCode::getRemark, playPhases);
            }
            collect = rcsCodeService.list(wrapper);
        }
        if (collect.size() > 0) {
            collect.stream().forEach(model -> {
                categoryIds.add(Integer.parseInt(model.getChildKey()));
            });
        }
        return categoryIds;
    }

    @Override
    public CategoryTemplateVo queryCategoryTemplate(Long sportId, Long categoryId) {
        CategoryTemplateVo templateVo = new CategoryTemplateVo();
        try {
            List<CategoryTemplateVo> templateList = this.baseMapper.queryCategoryTemplate(sportId);
            if (!CollectionUtils.isEmpty(templateList)) {
                for (CategoryTemplateVo template : templateList) {
                    if (template.getCategoryId().equals(categoryId)) {
                        templateVo = template;
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取模板失败", e);
        }
        return templateVo;
    }

    @Override
    public CategoryConVo selectCategorySet(Long sportId, Long categoryConId, Long categoryId) {
        List<CategoryConVo> categoryConVos = new ArrayList<>();
        CategoryConVo categoryConVo = new CategoryConVo();
        try {
            String key = RCS_CATEGORY_CONS + sportId;
            String value = redisClient.get(key);
            if (StringUtils.isNotBlank(value)) {
                categoryConVos = JsonFormatUtils.fromJsonArray(value, CategoryConVo.class);
            } else {
                categoryConVos = marketCategorySetMapper.selectCategorySet(sportId, categoryConId);
                redisClient.setExpiry(key, JsonFormatUtils.toJson(categoryConVos), EXPRIY_TIME_7_DAYS);
            }
            if (categoryConVos.size() > 0)
                categoryConVo = categoryConVos.stream().filter(filter -> categoryId.equals(filter.getCategoryId())).findFirst().orElse(null);
        } catch (Exception e) {
            log.error("获取玩法设置失败", e);
        }
        return categoryConVo;
    }

    @Override
    public List<Long> queryCategoryIds(String playSetCode) {
        List<Long> categoryIds = null;

        try {
            String key = RCS_CATEGORYSET_IDS + playSetCode;
            String value = redisClient.get(key);
            if (StringUtils.isNotBlank(value)) {
                categoryIds = JsonFormatUtils.fromJsonArray(value, Long.class);
            } else {
                categoryIds = marketCategorySetMapper.queryCategoryIds(1L, playSetCode);
                redisClient.setExpiry(key, JsonFormatUtils.toJson(categoryIds), EXPRIY_TIME_7_DAYS);
            }
        } catch (Exception e) {
            log.error("玩法集code查询玩法失败", e);
        }
        return categoryIds;
    }


}
