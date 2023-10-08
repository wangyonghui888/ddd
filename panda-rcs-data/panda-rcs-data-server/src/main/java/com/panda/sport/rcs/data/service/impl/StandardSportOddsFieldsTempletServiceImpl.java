package com.panda.sport.rcs.data.service.impl;

import com.panda.sport.rcs.core.db.annotation.Master;
import com.panda.sport.rcs.data.mapper.StandardSportOddsFieldsTempletMapper;
import com.panda.sport.rcs.data.service.IStandardSportOddsFieldsTempletService;
import com.panda.sport.rcs.pojo.StandardSportMarketCategory;
import com.panda.sport.rcs.pojo.StandardSportOddsFieldsTemplet;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 标准玩法投注项表 服务实现类
 * </p>
 *
 * @author Vector
 * @since 2019-09-26
 */
@Service
public class StandardSportOddsFieldsTempletServiceImpl extends ServiceImpl<StandardSportOddsFieldsTempletMapper, StandardSportOddsFieldsTemplet> implements IStandardSportOddsFieldsTempletService {

    @Autowired
    StandardSportOddsFieldsTempletMapper standardSportOddsFieldsTempletMapper;

    @Override
    public int batchInsert(ArrayList<StandardSportOddsFieldsTemplet> standardSportOddsFieldsTemplets) {
        if(CollectionUtils.isEmpty(standardSportOddsFieldsTemplets)){return 0;}
        return standardSportOddsFieldsTempletMapper.batchInsert(standardSportOddsFieldsTemplets);
    }

    @Override
    public List<StandardSportOddsFieldsTemplet> listByListIds(ArrayList<Long> standardSportOddsFieldsTempletLongs) {
        if(!CollectionUtils.isEmpty(standardSportOddsFieldsTempletLongs)){
            return standardSportOddsFieldsTempletMapper.selectBatchIds(standardSportOddsFieldsTempletLongs);
         }
        return null;
    }

    @Override
    public int batchInsertOrUpdate(ArrayList<StandardSportOddsFieldsTemplet> standardSportOddsFieldsTemplets) {
        if(CollectionUtils.isEmpty(standardSportOddsFieldsTemplets)){return 0;}
        return standardSportOddsFieldsTempletMapper.batchInsertOrUpdate(standardSportOddsFieldsTemplets);
    }
}
