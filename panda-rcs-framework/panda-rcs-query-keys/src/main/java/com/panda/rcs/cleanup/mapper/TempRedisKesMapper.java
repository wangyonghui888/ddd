package com.panda.rcs.cleanup.mapper;

import com.panda.rcs.cleanup.entity.STempRedisKey;
import org.springframework.stereotype.Repository;

@Repository
public interface TempRedisKesMapper {

    int saveInfo(STempRedisKey sTempRedisKey);
}
