package com.panda.sport.rcs.task.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsCode;
import com.panda.sport.rcs.vo.CategoryConVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;

import java.util.List;

/**
 * @ClassName CategoryService
 * @Description: TODO
 * @Author Enzo
 * @Date 2020/7/16
 **/
public interface CategoryService extends IService<RcsCode> {
    /**
     * 根据玩法Id获取玩法类别
     *
     * @param id
     * @return
     */
    public Integer getCategoryCon(Long id);

    /**
     * 查找所有玩法集
     *
     * @param
     * @return
     */
    List<CategoryConVo> selectCategoryCon(Long sportId);

    /**
     * 根据玩法ID查找玩法集
     *
     * @param id
     * @return
     */
    List<CategoryConVo> selectCategoryConById(Long id);

    List<CategoryConVo> mainCategory(Long sportId);

    List<Long> mainCategoryIds(Long sportId);

}
