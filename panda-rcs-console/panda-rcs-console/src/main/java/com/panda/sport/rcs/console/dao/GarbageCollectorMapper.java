package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.pojo.GarbageCollector;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import tk.mapper.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.console.dao
 * @Description :  TODO
 * @Date: 2020-02-10 15:31
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
public interface GarbageCollectorMapper extends MyMapper<GarbageCollector> {

    void  bathInsert(@Param("list") List<GarbageCollector> garbageCollector);

	List<Map<String, Object>> queryThreadInfo(Map<String, Object> queryMap);

}
