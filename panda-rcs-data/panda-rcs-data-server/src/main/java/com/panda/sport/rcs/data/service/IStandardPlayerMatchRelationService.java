package com.panda.sport.rcs.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.StandardPlayerMatchRelation;

/**
 * <p>
 * 球员与赛事 关系表.

某个球员准备参与了某场比赛，则会在该表中增加一条记录且仅会增加一条记录。 服务类
 * </p>
 *
 * @author Vector
 * @since 2019-09-26
 */
public interface IStandardPlayerMatchRelationService extends IService<StandardPlayerMatchRelation> {

}
