package com.panda.sport.rcs.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.merge.dto.StandardMarketOddsDTO;
import com.panda.sport.rcs.data.mapper.StandardSportMarketMapper;
import com.panda.sport.rcs.data.mqSerializaBean.StandardMarketMessageDTO;
import com.panda.sport.rcs.data.service.StandardSportMarketService;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.cache.RcsCacheContant;
import com.panda.sport.rcs.pojo.dto.StandardMarketPlaceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public int insertOrUpdate(StandardSportMarket standardSportMarket) {
        return baseMapper.insertOrUpdate(standardSportMarket);
    }

    @Override
    public int batchInsertOrUpdate(List<StandardMarketMessageDTO> list) {
        if(CollectionUtils.isEmpty(list)) return 0;
        return baseMapper.batchInsertOrUpdate(list);
    }

    @Override
    public Map<Integer, Long> getOddsFieldsTemplateId(Long playId) {
        return RcsCacheContant.ODDS_FIELDS_TEMPLATE_ID_CACHE.get(playId, id -> {
            List<StandardMarketOddsDTO> list = this.baseMapper.selectOddsFieldsTempletId(playId);
            if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isEmpty(list)) {
                return Maps.newHashMap();
            }
            return list.stream().collect(Collectors.toMap(StandardMarketOddsDTO::getOrderOdds, StandardMarketOddsDTO::getOddsFieldsTemplateId));
        });
    }

    @Override
    public StandardMarketPlaceDto getMainMarketPlaceInfo(Long matchId, Long playId) {
        List<StandardMarketPlaceDto> list = this.baseMapper.selectMarketPlaceInfo(matchId, Lists.newArrayList(playId), 1);
        if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }
}
