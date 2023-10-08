package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.pojo.HeartMqBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

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
public interface HeartMapper{

   void saveHeart(@Param("bean") HeartMqBean bean);

   List<HeartMqBean> heartList(@Param("bean") HeartMqBean bean,Integer pageNum,Integer pageSize);

   Long heartListCount(@Param("bean") HeartMqBean bean);

   List<HeartMqBean> heartDetailList(@Param("bean") HeartMqBean bean,@Param("pageStart")Integer pageNum,@Param("pageSize")Integer pageSize);

   Long heartDetailListCount(@Param("bean") HeartMqBean bean);

   List<HeartMqBean> heartServiceList(@Param("bean") HeartMqBean bean,@Param("pageStart")Integer pageNum,@Param("pageSize")Integer pageSize);

   Long heartServiceListCount(@Param("bean") HeartMqBean bean);

   List<HeartMqBean> queryServiceList();
}
