package com.panda.sport.rcs.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.data.mapper.StandardMatchTeamRelationMapper;
import com.panda.sport.rcs.data.service.IStandardMatchTeamRelationService;
import com.panda.sport.rcs.pojo.StandardMatchTeamRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 保存标准赛事与球队之间的所属关系。  服务实现类
 * </p>
 *
 * @author Vector
 * @since 2019-09-26
 */
@Service
public class StandardMatchTeamRelationServiceImpl extends ServiceImpl<StandardMatchTeamRelationMapper, StandardMatchTeamRelation> implements IStandardMatchTeamRelationService {

    @Autowired
    StandardMatchTeamRelationMapper standardMatchTeamRelationMapper;

    @Override
    public int batchInsert(ArrayList<StandardMatchTeamRelation> standardMatchTeamRelations) {
        if(CollectionUtils.isEmpty(standardMatchTeamRelations)){return 0;}
        return standardMatchTeamRelationMapper.batchInsert(standardMatchTeamRelations);
    }

    @Override
    public String selectMatchPosition(Long id) {
        return standardMatchTeamRelationMapper.selectMatchPosition(id);
    }

    @Override
    public int batchInsertOrUpdate(List<StandardMatchTeamRelation> list) {
        if(CollectionUtils.isEmpty(list)){return 0;}
        return standardMatchTeamRelationMapper.batchInsertOrUpdate(list);
    }
}
