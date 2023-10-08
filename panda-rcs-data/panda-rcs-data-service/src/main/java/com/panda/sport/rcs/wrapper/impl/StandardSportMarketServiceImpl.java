package com.panda.sport.rcs.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.cache.local.GuavaCache;
import com.panda.sport.rcs.mapper.StandardSportMarketMapper;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.wrapper.StandardSportMarketService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Project Name : rcs-parent
 * @Package Name : rcs-parent
 * @Description : 标准盘口信息
 * @Author : Paca
 * @Date : 2020-11-25 13:41
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class StandardSportMarketServiceImpl extends ServiceImpl<StandardSportMarketMapper, StandardSportMarket> implements StandardSportMarketService {
    @Override
    public Map<String, Object> queryMatchMarketInfo(Map<String, Object> map) {
        return this.baseMapper.queryMatchMarketInfo(map);
    }

    @Override
    public String queryOddTemplateInfo(Long oddsFieldsTemplateId) {
        String nameCode = "";
        Object o = GuavaCache.get(String.format("guavaCache_oddTemplateInfo_%s", oddsFieldsTemplateId));
        if (null != o) {
            nameCode = String.valueOf(o);
        } else {
            nameCode = this.baseMapper.queryOddTemplateInfo(oddsFieldsTemplateId + "");
            GuavaCache.put(String.format("guavaCache_oddTemplateInfo_%s", oddsFieldsTemplateId), nameCode);
        }
        return nameCode;
    }
}
