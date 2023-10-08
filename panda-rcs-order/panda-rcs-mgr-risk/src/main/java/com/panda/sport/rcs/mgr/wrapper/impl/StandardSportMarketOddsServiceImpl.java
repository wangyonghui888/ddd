package com.panda.sport.rcs.mgr.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.StandardSportMarketOddsMapper;
import com.panda.sport.rcs.mgr.wrapper.StandardSportMarketOddsService;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    @Override
    public List<StandardSportMarketOdds> selectByMap(Map<String, Object> map) {
        return this.baseMapper.selectByMap(map);
    }
}
