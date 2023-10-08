package com.panda.sport.rcs.data.service.impl;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.data.mapper.StandardSportMarketCategoryMapper;
import com.panda.sport.rcs.data.service.IStandardSportMarketCategoryService;
import com.panda.sport.rcs.pojo.StandardSportMarketCategory;

/**
 * <p>
 * 标准玩法表 服务实现类
 * </p>
 *
 * @author Vector
 * @since 2019-09-26
 */
@Service("sportMarketCategoryServiceImpl")
public class StandardSportMarketCategoryServiceImpl extends ServiceImpl<StandardSportMarketCategoryMapper, StandardSportMarketCategory> implements IStandardSportMarketCategoryService {

    @Autowired
    StandardSportMarketCategoryMapper standardSportMarketCategoryMapper;

    @Override
    public Long getLastCrtTime() {
        return standardSportMarketCategoryMapper.getLastCrtTime();
    }

    @Override
    public int batchInsert(List<StandardSportMarketCategory> removeStandardSportMarketCategories) {
        if(CollectionUtils.isEmpty(removeStandardSportMarketCategories)){return 0;}
        return standardSportMarketCategoryMapper.batchInsert(removeStandardSportMarketCategories);
    }

    @Override
    public List<StandardSportMarketCategory> listByListIds(ArrayList<Long> standsardSportMarketCategoriesLongs) {
        return standardSportMarketCategoryMapper.selectBatchIds(standsardSportMarketCategoriesLongs);
    }

    @Override
    public int batchInsertOrUpdate(List<StandardSportMarketCategory> standardSportMarketCategories) {
        if(CollectionUtils.isEmpty(standardSportMarketCategories)){return 0;}
        return standardSportMarketCategoryMapper.batchInsertOrUpdate(standardSportMarketCategories);
    }

	@Override
	public int batchInsertOrUpdateCategoryRef(ArrayList<Map<String, Object>> categoryRefList) {
		if(CollectionUtils.isEmpty(categoryRefList)){return 0;}
        return standardSportMarketCategoryMapper.batchInsertOrUpdateCategoryRef(categoryRefList);
	}

    @Override
    public int insert(StandardSportMarketCategory standardSportMarketCategory) {
        return standardSportMarketCategoryMapper.insert(standardSportMarketCategory);
    }


}
