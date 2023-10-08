package com.panda.sport.rcs.data.service;

import com.panda.sport.rcs.pojo.StandardMatchTeamRelation;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 保存标准赛事与球队之间的所属关系。  服务类
 * </p>
 *
 * @author Vector
 * @since 2019-09-26
 */
public interface IStandardMatchTeamRelationService extends IService<StandardMatchTeamRelation> {

    int batchInsert(ArrayList<StandardMatchTeamRelation> standardMatchTeamRelations);

    String selectMatchPosition(Long id);

    int batchInsertOrUpdate(List<StandardMatchTeamRelation> list);

}
