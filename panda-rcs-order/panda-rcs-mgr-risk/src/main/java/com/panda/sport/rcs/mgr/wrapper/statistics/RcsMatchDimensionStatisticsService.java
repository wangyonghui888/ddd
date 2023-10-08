package com.panda.sport.rcs.mgr.wrapper.statistics;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.statistics.RcsMatchDimensionStatistics;
import com.panda.sport.rcs.vo.statistics.RcsMatchDimensionStatisticsVo;

import java.util.List;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.statistics
 * @Description :  TODO
 * @Date: 2019-11-05 15:59
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsMatchDimensionStatisticsService extends IService<RcsMatchDimensionStatistics> {

    /**
     * 查询近一小时内实货量
     * @param matchId
     * @return
     */
    List<RcsMatchDimensionStatisticsVo> searchNearlyOneHourRealTimeValue(Long[] matchId);

    /**
     * 推送实时数据到前台
     * @param rcsMatchDimensionStatistics
     * @return
     */
    Boolean sendSysnData(RcsMatchDimensionStatistics rcsMatchDimensionStatistics);


    /**
     * 添加
     * @param rcsMatchDimensionStatistics
     */
    void insertOrSave(RcsMatchDimensionStatistics rcsMatchDimensionStatistics);
}
