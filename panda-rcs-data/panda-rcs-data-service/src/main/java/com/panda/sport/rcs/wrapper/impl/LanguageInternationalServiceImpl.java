package com.panda.sport.rcs.wrapper.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mapper.RcsLanguageInternationMapper;
import com.panda.sport.rcs.pojo.RcsLanguageInternation;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.utils.TwoLevelCacheUtil;
import com.panda.sport.rcs.vo.I18nItemVo;
import com.panda.sport.rcs.wrapper.LanguageInternationalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * @Project Name : rcs-parent
 * @Package Name : rcs-parent
 * @Description : 多语言国际化
 * @Author : Paca
 * @Date : 2020-11-22 10:47
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@Slf4j
public class LanguageInternationalServiceImpl implements LanguageInternationalService {

    public static final String BASE_DATA_LANGUAGE= "baseDataLanguage:";

    @Autowired
    private TwoLevelCacheUtil twoLevelCacheUtil;

    @Autowired
    RcsLanguageInternationMapper rcsLanguageInternationMapper;

    @Override
    public List<I18nItemVo> getCachedNamesByCode(Long nameCode) {
        if (nameCode == null) {
            return null;
        }
        String key = BASE_DATA_LANGUAGE + nameCode;
        String cachedLanguageStr = twoLevelCacheUtil.get(key,key1->{
            Map<String, RcsLanguageInternation> cachedLanguageMap;
            List<RcsLanguageInternation> cachedLanguages ;
            log.info("Language cache not exists, load from database, nameCode::{}::", nameCode);
            QueryWrapper<RcsLanguageInternation> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name_code", nameCode);
            cachedLanguages = rcsLanguageInternationMapper.selectList(queryWrapper);
            // 数据库也不存在则返回
            if (CollectionUtils.isEmpty(cachedLanguages)) {
                return "";
            }
            // 把新数据放入缓存
            cachedLanguageMap = Maps.newHashMap();
            for (RcsLanguageInternation rcsLanguageInternation : cachedLanguages) {
                cachedLanguageMap.put(String.valueOf(rcsLanguageInternation.getId()), rcsLanguageInternation);
            }
            return JsonFormatUtils.toJson(cachedLanguageMap) ;
        });
        Map<String, RcsLanguageInternation> cachedLanguageMap = JsonFormatUtils.fromJsonMap(cachedLanguageStr, String.class, RcsLanguageInternation.class);
        Collection<RcsLanguageInternation> cachedLanguages = cachedLanguageMap.values();
        List<I18nItemVo> resultList = Lists.newArrayListWithCapacity(cachedLanguages.size());
        for (RcsLanguageInternation rcsLanguageInternation : cachedLanguages) {
            if(!CommonUtils.isBlankOrNull(rcsLanguageInternation.getText())){
                Map<String,String> map = JSONObject.parseObject(rcsLanguageInternation.getText(), Map.class);
                for (String languageType : map.keySet()) {
                    if ("zs".equals(languageType) || "en".equals(languageType)) {
                        I18nItemVo itemVo = new I18nItemVo();
                        itemVo.setLanguageType(languageType);
                        itemVo.setText(map.get(languageType));
                        resultList.add(itemVo);
                    }
                }
            }
        }
        return resultList;
    }
}
