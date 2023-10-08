package com.panda.sport.rcs.task.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.StandardSportMarketCategory;
import com.panda.sport.rcs.vo.CategoryConVo;
import com.panda.sport.rcs.vo.CategoryTemplateVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
     *
     * @param id
     * @return
     */
    StandardSportMarketCategory getCachedMarketCategoryById(String sportId, Long id);

    StandardSportMarketCategory queryCachedCategory(String sportId, Long id);

    /**
     * 根据玩法ID查找对应玩法阶段
     *
     * @param categoryId
     * @return
     */
    Integer selectPlayPhase(Long categoryId);

    List<Integer> selectCategoryIds(List<Integer> playPhases);

    /**
     * 查找玩法模板
     *
     * @param sportId
     * @param categoryId
     * @return
     */
    CategoryTemplateVo queryCategoryTemplate(Long sportId, Long categoryId);

    CategoryConVo selectCategorySet(Long sportId, Long categoryConId, Long categoryId);
    /**
     * 获取code玩法集对应玩法ID
     * @param playSetCode
     * @return
     */
    List<Long> queryCategoryIds(String playSetCode);
}
