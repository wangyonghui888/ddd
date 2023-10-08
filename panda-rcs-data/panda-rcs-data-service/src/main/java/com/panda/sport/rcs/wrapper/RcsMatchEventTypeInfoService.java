package com.panda.sport.rcs.wrapper;


import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.bean.RcsMatchEventTypeInfo;
import com.panda.sport.rcs.pojo.dto.MatchEventInfoDTO;

import java.util.List;

public interface RcsMatchEventTypeInfoService extends IService<RcsMatchEventTypeInfo> {


    int updateBatch(List<RcsMatchEventTypeInfo> list);

    int batchInsert(List<RcsMatchEventTypeInfo> list);

    int insertOrUpdate(RcsMatchEventTypeInfo record);

    int insertOrUpdateSelective(RcsMatchEventTypeInfo record);

    RcsMatchEventTypeInfo getOneInfo(MatchEventInfoDTO matchEventInfoDTO);
}


