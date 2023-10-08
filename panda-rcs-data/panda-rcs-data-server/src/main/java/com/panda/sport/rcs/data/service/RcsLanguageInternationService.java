package com.panda.sport.rcs.data.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsLanguageInternation;

import java.util.List;

public interface RcsLanguageInternationService extends IService<RcsLanguageInternation> {

    int updateBatch(List<RcsLanguageInternation> list);

    int batchInsert(List<RcsLanguageInternation> list);

    Integer batchInsertOrUpdate(List<RcsLanguageInternation> list);

    int insertOrUpdate(RcsLanguageInternation record);

    int insertOrUpdateSelective(RcsLanguageInternation record);

    int batchInsertOrUpdateMerge(List<RcsLanguageInternation> list);

    int insertOrUpdateMerge(RcsLanguageInternation record);
}
