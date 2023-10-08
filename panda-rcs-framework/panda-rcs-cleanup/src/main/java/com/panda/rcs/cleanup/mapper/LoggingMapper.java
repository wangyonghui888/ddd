package com.panda.rcs.cleanup.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoggingMapper {

    int deleteOperateLogByMatchIds(@Param("matchIds") List<Long> matchIds);

    int deleteTraderMessageByTime(@Param("deleteTime") String deleteTime);

    int deleteLogRecordByTime(@Param("deleteTime") String deleteTime);

    int deleteLogFomatByTime(@Param("deleteTime") String deleteTime);

    int deleteOperationLogByTime(@Param("deleteTime") String deleteTime);

}
