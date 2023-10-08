package com.panda.sport.rcs.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.StandardSportMarketCategory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标准玩法表 服务类
 * </p>
 *
 * @author Vector
 * @since 2019-09-26
 */
public interface IStandardSportMarketCategoryService extends IService<StandardSportMarketCategory> {
    /**
     * @MethodName:
     * @Description: 得到最后的插入时间
     * @Param:
     * @Return:
     * @Author: Vector
     * @Date: 2019/9/30
     **/
    Long getLastCrtTime();

    int batchInsert(List<StandardSportMarketCategory> removeStandardSportMarketCategories);

    List<StandardSportMarketCategory> listByListIds(ArrayList<Long> standsardSportMarketCategoriesLongs);

    int batchInsertOrUpdate(List<StandardSportMarketCategory> standardSportMarketCategories);

    int batchInsertOrUpdateCategoryRef(ArrayList<Map<String, Object>> categoryRefList);

    int insert(StandardSportMarketCategory standardSportMarketCategory);


}
