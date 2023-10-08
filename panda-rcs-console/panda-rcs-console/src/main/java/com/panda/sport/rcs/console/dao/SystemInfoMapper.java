package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.pojo.HeartMqBean;
import com.panda.sport.rcs.console.pojo.SystemInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;
import tk.mapper.MyMapper;

import java.util.List;

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
public interface SystemInfoMapper extends MyMapper<SystemInfo> {
    @Select("SELECT `server_name` serverName,ip,pid  from  rcs_monitor_heart_log  WHERE FROM_UNIXTIME(`current_time`/1000) > date_sub(now(),INTERVAL 30 SECOND)   GROUP BY server_name , ip,pid ")
    List<HeartMqBean> getServerName();

    @Insert({"insert into rcs_monitor_system_info(load_avg1, load_avg2, load_avg3, us_cpu, sy_cpu,ni_cpu,id_cpu,wa_cpu,hi_cpu,si_cpu,st_cpu,total_mem,free_mem,used_mem,cache_mem,total_swap,free_swap,used_swap,cache_swap,memory_rate,uuid,sever_name,ip,pid) " +
            "values(#{loadAvg1}, #{loadAvg2}, #{loadAvg3}, #{usCpu}, #{syCpu}, #{niCpu}, #{idCpu}, #{waCpu}, #{hiCpu}, #{siCpu}, #{stCpu}, #{totalMem}, #{freeMem}, #{usedMem}, #{cacheMem}, #{totalSwap}, #{freeSwap}, #{usedSwap}, #{cacheSwap}, #{memoryRate}, #{uuid}, #{severName}, #{ip}, #{pid})"})
    int insertSystemInfo(SystemInfo systemInfo);
}
