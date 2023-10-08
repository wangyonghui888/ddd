package com.panda.rcs.cleanup.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TempRedisKesMapper {

     List<String> getRedisKeysLists (@Param("saveDate") String saveDate);
}
