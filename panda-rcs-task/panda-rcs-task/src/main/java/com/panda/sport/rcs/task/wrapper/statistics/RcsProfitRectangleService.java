package com.panda.sport.rcs.task.wrapper.statistics;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.statistics.RcsProfitRectangle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author :  toney
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.statistics
 * @Description :  赛事维度统计
 * @Date: 2019-12-11 16:35
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsProfitRectangleService extends IService<RcsProfitRectangle> {
    /**
     * @Description  删除记录
     * @Param [matchId, playId]
     * @Author  toney
     * @Date  11:07 2019/12/21
     * @return java.lang.Integer
     **/
    Integer deleteByMatchIdAndPlayId(Long matchId, Integer playId);

    /**
     * 批量插入
     * @param map
     */
    Integer batchInsert(@Param("map") List<RcsProfitRectangle> map);
}
