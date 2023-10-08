package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.pojo.ErrorMqBean;
import com.panda.sport.rcs.monitor.entity.ThreadBean;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

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
public interface ErrorMapper{

   void saveError(@Param("bean") ErrorMqBean bean);
   /**
    * @Description   //分页查询错误日志
    * @Param [bean]
    * @Author  Sean
    * @Date  20:52 2020/3/12
    * @return java.util.List<com.panda.sport.rcs.console.pojo.ErrorMqBean>
    **/
   List<ErrorMqBean> errorList(@Param("bean") ErrorMqBean bean,Integer pageNum,Integer pageSize);

   Long errorListCount(@Param("bean") ErrorMqBean bean);
   
   void saveThreadInfo(@Param("bean") ThreadBean bean);
   
   void saveMqInfo(Map<String, Object> updateMap);

}
