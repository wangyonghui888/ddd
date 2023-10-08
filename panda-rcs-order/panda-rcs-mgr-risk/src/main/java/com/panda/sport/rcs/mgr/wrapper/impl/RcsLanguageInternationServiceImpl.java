package com.panda.sport.rcs.mgr.wrapper.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.db.annotation.Master;
import com.panda.sport.rcs.mapper.RcsLanguageInternationMapper;
import com.panda.sport.rcs.mgr.wrapper.RcsLanguageInternationService;
import com.panda.sport.rcs.pojo.RcsLanguageInternation;
import com.panda.sport.rcs.vo.I18nItemVo;
import com.panda.sport.rcs.vo.LanguageInternationDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.panda.sport.rcs.constants.RedisKey.RCS_TASK_RCS_LANGUAGE_NEW_CACHE;
import static com.panda.sport.rcs.mgr.constant.RcsCacheContant.EXPRIY_TIME_2_HOURS;


@Service
public class RcsLanguageInternationServiceImpl extends ServiceImpl<RcsLanguageInternationMapper, RcsLanguageInternation> implements RcsLanguageInternationService {

    @Autowired
    private  RedisClient redisClient;
    @Autowired
    private RcsLanguageInternationMapper languageInternationMapper;
    /**
     * 根据nameCode从缓存获取所有语言集，首次不存在则从数据库加载
     *
     * @param nameCode
     * @return
     */
    @Override
    @Master
    public List<I18nItemVo> getCachedNamesByCode(Long nameCode){
        if (nameCode == null) {
            return null;
        }
        String nameCodeRedisKey = RCS_TASK_RCS_LANGUAGE_NEW_CACHE+nameCode;
        String value = redisClient.get(nameCodeRedisKey);
        List<I18nItemVo> result = new ArrayList<>();
        if(StringUtils.isEmpty(value)){
            QueryWrapper<RcsLanguageInternation> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name_code", nameCode.toString());
            queryWrapper.ne("text", "");
            Collection<RcsLanguageInternation> cachedLanguages = this.list(queryWrapper);
            // 数据库也不存在则返回
            if (CollectionUtils.isEmpty(cachedLanguages)) {
                return null;
            }
            List<I18nItemVo> resultList = Lists.newArrayListWithCapacity(cachedLanguages.size());
            //根据同样的nameCode进行分组
            for (RcsLanguageInternation language : cachedLanguages) {
                JSONObject languageTextObject = JSONObject.parseObject(language.getText());
                for(String key:languageTextObject.keySet()){
                    I18nItemVo itemVo = new I18nItemVo();
                    itemVo.setLanguageType(key);
                    itemVo.setText(languageTextObject.getString(key));
                    resultList.add(itemVo);
                }
            }
            result = resultList;
            redisClient.setExpiry(nameCodeRedisKey,JSONObject.toJSONString(resultList),EXPRIY_TIME_2_HOURS);
        }else {
            result =  JSONObject.parseArray(value,I18nItemVo.class);
        }

        return result;
    }

    /**
     * 根据nameCode从缓存获取所有语言集
     * 获取顺序：本地缓存->redis->database
     * 返回格式为MAP
     *
     * @param nameCode
     * @return
     */
    @Override
    public Map<String, String> getCachedNamesMapByCode(Long nameCode) {
        if (nameCode == null) {
            return null;
        }
        List<I18nItemVo>  i18nItemVoList = this.getCachedNamesByCode(nameCode);
        if (CollectionUtils.isEmpty(i18nItemVoList)) {
            return null;
        }
        Map<String, String> resultMap = Maps.newHashMapWithExpectedSize(5);
        for (I18nItemVo itemVo : i18nItemVoList) {
            if("zs".equals(itemVo.getLanguageType())||"en".equals(itemVo.getLanguageType())){
                resultMap.put(itemVo.getLanguageType(), itemVo.getText());
            }
        }
        return resultMap;
    }

    @Override
    public Map<Long, Map<String, String>> selectLanguageInternationByPlayId(Set<Long> playIds) {
        Map<Long, Map<String, String>> mapMap = new HashMap<>();
        if (CollectionUtils.isEmpty(playIds)) {
            return null;
        }
        List<LanguageInternationDO> languageNameCodesByPlayIds = languageInternationMapper.getLanguageNameCodesByPlayIds(playIds);
        if (!CollectionUtils.isEmpty(languageNameCodesByPlayIds)) {
            for (LanguageInternationDO languageInternationDO : languageNameCodesByPlayIds) {
                Map<String, String> map = mapMap.get(languageInternationDO.getPlayId());
                if (map == null) {
                    map = new HashMap<>();
                }
                map.put(languageInternationDO.getLanguageType(), languageInternationDO.getText());
                mapMap.put(languageInternationDO.getPlayId(), map);
            }
        }
        return mapMap;
    }
}
