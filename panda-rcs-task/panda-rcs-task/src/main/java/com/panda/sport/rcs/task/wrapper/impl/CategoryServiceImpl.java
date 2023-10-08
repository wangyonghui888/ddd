package com.panda.sport.rcs.task.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.constants.RedisKey;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.enums.BasketballEnum;
import com.panda.sport.rcs.enums.SportTypeEnum;
import com.panda.sport.rcs.mapper.MarketCategorySetMapper;
import com.panda.sport.rcs.mapper.RcsCodeMapper;
import com.panda.sport.rcs.pojo.RcsCode;
import com.panda.sport.rcs.task.wrapper.CategoryService;
import com.panda.sport.rcs.task.wrapper.RcsCodeService;
import com.panda.sport.rcs.vo.CategoryConVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.constants.RedisKey.EXPRIY_TIME_7_DAYS;

/**
 * @ClassName CategoryServiceImpl
 * @Description: TODO
 * @Author Enzo
 * @Date
 **/
@Service
public class CategoryServiceImpl extends ServiceImpl<RcsCodeMapper, RcsCode> implements CategoryService {
    @Autowired
    private RcsCodeService rcsCodeService;
    @Autowired
    private MarketCategorySetMapper marketCategorySetMapper;
    @Autowired
    private RedisClient redisClient;



    @Override
    public Integer getCategoryCon(Long id) {
        List<CategoryConVo> categoryConVos = selectCategoryCon(1L);
        Integer result = 0;
        if (categoryConVos.size() > 0) {
            CategoryConVo categoryConVo = categoryConVos.stream().filter(line -> JsonFormatUtils.fromJsonArray(line.getCategoryIds(), Long.class).contains(id)).findFirst().orElse(null);
            if (null != categoryConVo) {
                result = categoryConVo.getId().intValue();
            }
        }
        return result;
    }

    @Override
    public List<CategoryConVo> selectCategoryCon(Long sportId) {
        List<CategoryConVo> result = new ArrayList<>();
        Object o = redisClient.get(RedisKey.CACHE_CATEGORY_CON + sportId);
        if (o != null) {
            result = JsonFormatUtils.fromJsonArray(JsonFormatUtils.toJson(o), CategoryConVo.class);
        } else {
            List<CategoryConVo> categoryConVos = mainCategory(sportId);
            categoryConVos.addAll(marketCategorySetMapper.selectCategoryCons(sportId));
            List<CategoryConVo> collect = categoryConVos.stream().filter(filter -> StringUtils.isNotBlank(filter.getCategoryIds())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(collect)) {
                result = categoryConVos.stream().filter(filter -> StringUtils.isNotBlank(filter.getCategoryIds())).collect(Collectors.toList());
                redisClient.setExpiry(RedisKey.CACHE_CATEGORY_CON + sportId, JsonFormatUtils.toJson(categoryConVos), EXPRIY_TIME_7_DAYS);
            }
        }
        return result.stream().filter(filter -> StringUtils.isNotBlank(filter.getCategoryIds())).collect(Collectors.toList());
    }

    @Override
    public List<CategoryConVo> selectCategoryConById(Long id) {
        List<CategoryConVo> result = new ArrayList<>();
        List<CategoryConVo> rcsCodes = selectCategoryCon(1L);
        if (!CollectionUtils.isEmpty(rcsCodes)) {
            result = rcsCodes.stream().filter(filter -> JsonFormatUtils.fromJsonArray(filter.getCategoryIds(), Long.class).contains(id)).collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public List<CategoryConVo> mainCategory(Long sportId) {
        List<CategoryConVo> categoryConVos = new ArrayList<>();
        Object o = redisClient.get(RedisKey.CACHE_MAIN_CATEGORY_CON + sportId);
        if (o != null) {
            categoryConVos = JsonFormatUtils.fromJsonArray(JsonFormatUtils.toJson(o), CategoryConVo.class);
        } else {
            List<RcsCode> rcsCodes = rcsCodeService.selectRcsCods("category_con_" + sportId);
            if (!CollectionUtils.isEmpty(rcsCodes)) {
                for (RcsCode model : rcsCodes) {
                    CategoryConVo categoryConVo = new CategoryConVo();
                    categoryConVo.setId(Long.parseLong(model.getChildKey()));
                    categoryConVo.setName(model.getRemark());
                    categoryConVo.setCategoryIds(model.getValue());
                    categoryConVo.setSportId(sportId);
                    categoryConVos.add(categoryConVo);
                }
                redisClient.setExpiry(RedisKey.CACHE_MAIN_CATEGORY_CON + sportId, JsonFormatUtils.toJson(categoryConVos), EXPRIY_TIME_7_DAYS);
            }
        }
        return categoryConVos;
    }

    @Override
    public List<Long> mainCategoryIds(Long sportId) {
        List<CategoryConVo> categoryConVos = mainCategory(sportId);
        List<Long> mainCategoryIds = new ArrayList<>();
        if(!CollectionUtils.isEmpty(categoryConVos)){
            categoryConVos.stream().forEach(categoryConVo->{
                mainCategoryIds.addAll(categoryConVo.categoryIds());
            });
        }
        return mainCategoryIds;
    }
}
