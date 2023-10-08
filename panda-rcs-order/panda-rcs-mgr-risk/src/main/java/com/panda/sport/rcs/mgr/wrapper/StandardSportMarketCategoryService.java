package com.panda.sport.rcs.mgr.wrapper;


import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.StandardSportMarketCategory;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标准玩法表 服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
public interface StandardSportMarketCategoryService extends IService<StandardSportMarketCategory> {

    /**
     * 根据ID获取缓存中的玩法，不存在则从数据库获取并缓存
     * @param id
     * @return
     */
    StandardSportMarketCategory getCachedMarketCategoryById(String sportId ,Long id);

    /**
     * 根据玩法ID查找对应玩法阶段
     * @param categoryId
     * @return
     */
    Integer selectPlayPhase(Long categoryId);

}
