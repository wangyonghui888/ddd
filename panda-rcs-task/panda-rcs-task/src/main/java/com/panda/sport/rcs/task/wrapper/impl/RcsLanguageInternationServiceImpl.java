package com.panda.sport.rcs.task.wrapper.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.alibaba.fastjson.TypeReference;
import com.panda.sport.rcs.pojo.RiskFp;
import com.panda.sport.rcs.task.job.danger.entity.BigDataResponseVo;
import com.panda.sport.rcs.task.utils.CommonUtil;
import com.panda.sport.rcs.task.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.sport.rcs.core.db.annotation.Master;
import com.panda.sport.rcs.mapper.RcsLanguageInternationMapper;
import com.panda.sport.rcs.pojo.RcsLanguageInternation;
import com.panda.sport.rcs.task.wrapper.RcsLanguageInternationService;
import com.panda.sport.rcs.vo.I18nItemVo;

@Slf4j
@Service
public class RcsLanguageInternationServiceImpl extends ServiceImpl<RcsLanguageInternationMapper, RcsLanguageInternation> implements RcsLanguageInternationService {


    @Autowired
    RedisUtil redisUtil;
    /**
     * 根据nameCode优先从mybatis 二级缓存获取所有语言集
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
        String iStr=redisUtil.get(String.valueOf(nameCode), key->{
            QueryWrapper<RcsLanguageInternation> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(RcsLanguageInternation::getNameCode, nameCode);
            queryWrapper.lambda().ne(RcsLanguageInternation::getText, "");
            List<RcsLanguageInternation> iCache = this.list(queryWrapper);
            return JSONObject.toJSONString(iCache);
        });
        // 数据库也不存在则返回
        if (CommonUtil.isBlankOrNull(iStr)) {
            return null;
        }
        List<RcsLanguageInternation> cachedLanguages = JSONObject.parseObject(iStr, new TypeReference<ArrayList<RcsLanguageInternation>>() {});;
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

        return resultList;
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
}
