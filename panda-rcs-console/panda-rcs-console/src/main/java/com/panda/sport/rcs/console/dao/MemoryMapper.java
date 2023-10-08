package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.pojo.Memory;
import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Component;
import tk.mapper.MyMapper;

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
public interface MemoryMapper extends MyMapper<Memory> {
    @Insert({"insert into rcs_monitor_memory(committed, init, max, used, non_committed,non_init,non_max,non_used,ip,pid,sever_name) " +
            "values(#{committed}, #{init}, #{max}, #{used}, #{nonCommitted}, #{nonInit}, #{nonMax}, #{nonUsed}, #{ip}, #{pid}, #{severName})"})
    int insertMemory(Memory memory);
}
