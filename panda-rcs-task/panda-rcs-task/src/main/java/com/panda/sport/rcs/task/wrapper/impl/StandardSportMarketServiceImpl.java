package com.panda.sport.rcs.task.wrapper.impl;

import java.util.List;
import java.util.Map;

import com.panda.sport.rcs.task.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.StandardSportMarketMapper;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.cache.RcsCacheContant;
import com.panda.sport.rcs.task.wrapper.StandardSportMarketService;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 盘口服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
@Slf4j
@Service
public class StandardSportMarketServiceImpl extends ServiceImpl<StandardSportMarketMapper, StandardSportMarket> implements StandardSportMarketService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    private StandardSportMarketMapper standardSportMarketMapper;

    @Override
    public Map<String, Object> queryMatchMarketInfo(Map<String, Object> map) {
        return standardSportMarketMapper.queryMatchMarketInfo(map);
    }

    @Override
    public String queryOddTemplateInfo(Long oddsFieldsTemplateId) {
        String tStr = redisUtil.get(String.valueOf(oddsFieldsTemplateId),e->queryOddTemplateInfo(String.valueOf(oddsFieldsTemplateId)));
        return tStr;
//        String nameCode = "";
//        Object o = GuavaCache.get(String.format("guavaCache_oddTemplateInfo_%s", oddsFieldsTemplateId));
//        if (null != o) {
//            nameCode = String.valueOf(o);
//        } else {
//            nameCode = standardSportMarketMapper.queryOddTemplateInfo(oddsFieldsTemplateId + "");
//            GuavaCache.put(String.format("guavaCache_oddTemplateInfo_%s", oddsFieldsTemplateId), nameCode);
//        }
//        return nameCode;
    }

    @Override
    public List<StandardSportMarket> selectStandardSportMarketByMatchIdAndPlayIdAndPlayId(Long matchId) {
        return standardSportMarketMapper.selectStandardSportMarketByMatchIdAndPlayIdAndPlayId(matchId);
    }

    @Override
    public StandardSportMarket selectStandardSportMarketByMarketId(Long marketId) {
        return standardSportMarketMapper.selectStandardSportMarketByMarketId(marketId);
    }

    @Override
    public String queryOddTemplateInfo(String templateCode) {
        String tStr = standardSportMarketMapper.queryOddTemplateInfo(templateCode);
        return tStr;
    }
}
