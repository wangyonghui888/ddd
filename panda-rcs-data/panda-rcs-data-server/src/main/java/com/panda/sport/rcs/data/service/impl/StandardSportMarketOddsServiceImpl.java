package com.panda.sport.rcs.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.data.mapper.StandardSportMarketOddsMapper;
import com.panda.sport.rcs.data.mqSerializaBean.StandardMarketOddsMessageDTO;
import com.panda.sport.rcs.data.service.StandardSportMarketOddsService;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 赛事盘口交易项表 服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
@Service
public class StandardSportMarketOddsServiceImpl extends ServiceImpl<StandardSportMarketOddsMapper, StandardSportMarketOdds> implements StandardSportMarketOddsService {

    @Autowired
    private StandardSportMarketOddsMapper mapper;

    @Override
    public int batchSaveOrUpdate(List<StandardMarketOddsMessageDTO> listStandardSportMarketOdds) {
        if(CollectionUtils.isEmpty(listStandardSportMarketOdds)) return 0;
        return mapper.batchSaveOrUpdate(listStandardSportMarketOdds);
    }

    @Override
    public List<StandardSportMarketOdds> list(Long marketId) {
        return this.list(new LambdaQueryWrapper<StandardSportMarketOdds>()
                .eq(StandardSportMarketOdds::getMarketId, marketId));
    }
}
