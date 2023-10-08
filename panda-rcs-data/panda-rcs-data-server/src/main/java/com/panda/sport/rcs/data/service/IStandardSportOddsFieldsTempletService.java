package com.panda.sport.rcs.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.StandardSportOddsFieldsTemplet;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 标准玩法投注项表 服务类
 * </p>
 *
 * @author Vector
 * @since 2019-09-26
 */
public interface IStandardSportOddsFieldsTempletService extends IService<StandardSportOddsFieldsTemplet> {

    int batchInsert(ArrayList<StandardSportOddsFieldsTemplet> standardSportOddsFieldsTemplets);

    List<StandardSportOddsFieldsTemplet> listByListIds(ArrayList<Long> standardSportOddsFieldsTempletLongs);

    int batchInsertOrUpdate(ArrayList<StandardSportOddsFieldsTemplet> standardSportOddsFieldsTemplets);
}
