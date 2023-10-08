package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.pojo.LogRecord;
import com.panda.sport.rcs.log.interceptors.LogBean;
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
public interface LogBeanMapper extends MyMapper<LogBean> {

    int insert(@Param("bean") LogBean logBean);

    Long logRecordsCount(@Param("bean") LogRecord bean);

    List<LogBean> selectLogRecords(@Param("bean") LogRecord bean);

	Map<String, Object> queryTitleByCode(String code);
}
