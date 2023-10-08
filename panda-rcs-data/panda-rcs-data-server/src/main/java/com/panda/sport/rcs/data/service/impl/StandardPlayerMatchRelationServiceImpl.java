package com.panda.sport.rcs.data.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.data.mapper.StandardPlayerMatchRelationMapper;
import com.panda.sport.rcs.data.service.IStandardPlayerMatchRelationService;
import com.panda.sport.rcs.pojo.StandardPlayerMatchRelation;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 球员与赛事 关系表.

某个球员准备参与了某场比赛，则会在该表中增加一条记录且仅会增加一条记录。 服务实现类
 * </p>
 *
 * @author Vector
 * @since 2019-09-26
 */
@Service
public class StandardPlayerMatchRelationServiceImpl extends ServiceImpl<StandardPlayerMatchRelationMapper, StandardPlayerMatchRelation> implements IStandardPlayerMatchRelationService {

}
