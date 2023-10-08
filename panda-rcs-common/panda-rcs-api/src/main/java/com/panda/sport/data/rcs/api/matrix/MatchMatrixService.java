package com.panda.sport.data.rcs.api.matrix;

import com.panda.sport.data.rcs.dto.matrix.MatrixBean;
import com.panda.sport.rcs.pojo.TOrderDetail;

import java.util.Date;
import java.util.List;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.data.rcs.api.matrix
 * @Description :  赛事矩阵查询接口
 * @Date: 2019-11-05 16:34
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface MatchMatrixService {
    /**
     *
     * @param item 参数（matchId 赛事ID，isSettlement： 1 未结算 2 已计算，matchType： 1 早盘 2 滚球盘）
     * @param matchStage 赛事阶段  1:比分全场矩阵；2:比分上半场矩阵
     * @param startTime 开始时间
     * @param endTime  结束时间
     * @param tenantIds 商户ID
     * @param playIds playId 玩法ID，
     * @param unit 单位 1 10 100 1000
     * @param size 矩阵大小 5*5 / 6*6
     * @return 赛事矩阵
     */
    MatrixBean[][] getMatrixByMatch(TOrderDetail item,String matchStage, Date startTime, Date endTime, List<Long> playIds, List<Long> tenantIds, Integer unit, Integer size);
}