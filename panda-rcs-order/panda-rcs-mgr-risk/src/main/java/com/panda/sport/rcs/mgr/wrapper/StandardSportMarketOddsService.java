package com.panda.sport.rcs.mgr.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 赛事盘口交易项表 服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
public interface StandardSportMarketOddsService extends IService<StandardSportMarketOdds> {

    List<StandardSportMarketOdds> selectByMap(Map<String, Object> map);
}
