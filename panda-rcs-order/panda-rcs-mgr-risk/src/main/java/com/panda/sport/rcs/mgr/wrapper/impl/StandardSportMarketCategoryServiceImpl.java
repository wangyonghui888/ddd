package com.panda.sport.rcs.mgr.wrapper.impl;

import static com.panda.sport.rcs.constants.RedisKeys.EXP_TIME;

import java.util.HashMap;
import java.util.Map;

import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.StandardSportMarketCategoryMapper;
import com.panda.sport.rcs.mgr.wrapper.RcsCodeService;
import com.panda.sport.rcs.mgr.wrapper.StandardSportMarketCategoryService;
import com.panda.sport.rcs.pojo.RcsCode;
import com.panda.sport.rcs.pojo.StandardSportMarketCategory;

import lombok.extern.slf4j.Slf4j;

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
    /**
     * 根据ID获取缓存中的玩法，不存在则从数据库获取并缓存
     *
     * @param id
     * @return
     */
    @Override
    public StandardSportMarketCategory getCachedMarketCategoryById(String sportId , Long id) {
        if (id == null || id == 0L) {
            return null;
        }
        String key = String.format("%s_%s", sportId,id);


        StandardSportMarketCategory marketCategory =  RcsLocalCacheUtils.getValue(RedisKeys.RCSCACHE_MARKETODDS_MARKET_CATEGORY + key,
                (k)->{
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("id", id);
                    map.put("sportId", sportId);
                    return baseMapper.queryCategoryInfoByMap(map);
                },24 * 60 * 60 * 1000L);
        if(marketCategory == null){
            log.warn("Can not found cached category:{}, database also not exists.", id);
        }
        return marketCategory;
    }

    @Override
    public Integer selectPlayPhase(Long categoryId) {
        String result = "";
        String key = String.format(RedisKeys.RCS_CODE_CACHE, "category_phase_id", categoryId);
        boolean exist = redisClient.exist(key);
        if (exist) {
            result = redisClient.get(key);
        } else {
            QueryWrapper<RcsCode> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(RcsCode::getFatherKey, "category_phase");
            wrapper.lambda().eq(RcsCode::getChildKey, String.valueOf(categoryId));
            RcsCode rcsCode = rcsCodeService.getOne(wrapper);
            if (rcsCode != null) {
                result = rcsCode.getRemark();
                redisClient.setExpiry(key, rcsCode.getRemark(),EXP_TIME);
            }
        }
        if (StringUtils.isNotBlank(result)) {
            return Integer.parseInt(result);
        }
        return 0;
    }

}
