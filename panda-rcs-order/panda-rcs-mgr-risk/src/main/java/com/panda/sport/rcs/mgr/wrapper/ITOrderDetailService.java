package com.panda.sport.rcs.mgr.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.pojo.TOrderDetail;
import com.panda.sport.rcs.vo.MatrixVo;
import com.panda.sport.rcs.vo.OrderDetailStatReportVo;
import com.panda.sport.rcs.vo.OrderDetailVo;
import com.panda.sport.rcs.vo.RequestMarketOrderVo;
import com.panda.sport.rcs.vo.statistics.SettleAmountVo;
import com.panda.sport.rcs.vo.statistics.SumMatchAmountVo;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 投注单详细信息表 服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-03
 */
public interface ITOrderDetailService extends IService<TOrderDetail> {

    /**
     * @param item      参数（matchId 赛事ID，isSettlement： 1 未结算 2 已计算，matchType： 1 早盘 2 滚球盘）
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param tenantIds 商户ID
     * @param playIds   playId 玩法ID，
     * @param unit      单位 1 10 100 1000
     * @param size      矩阵大小 5*5 / 6*6
     * @return 赛事矩阵
     */
    MatrixVo[][] getHalfMatrixByMatchId(TOrderDetail item, Date startTime, Date endTime, List<Long> playIds, List<Long> tenantIds, Integer unit, Integer size);

    /**
     * @return com.panda.sport.rcs.vo.MatrixVo[][] 默认返回 12*12 大小的矩阵
     * @Description 查询玩法管理中 赛事订单对应的全场比分矩阵
     * @Param [tenantId, matchId, matchType, settleStatus, playIds, unit]
     * @Param tenantId 商户ID
     * @Param matchId 赛事ID
     * @Param matchType  类型：1 ：早盘 ，2： 滚球盘， 3： 冠军盘 , 全部 null
     * @Param settleStatus 结算状态 0 未结算 1：已结算  全部 null
     * @Param playIds 玩法ID集合
     * 单位 1 10 100 1000
     * @Author max
     * @Date 11:25 2019/11/8
     */
    MatrixVo[][] queryMatrixByMatchId(List<Long> tenantIds, Long matchId, Integer matchType, Integer settleStatus, List<Long> playIds, Integer unit, Integer size);


    /**
     * 获取报表
     *
     * @param marketId
     * @param playOptionsId
     * @return
     */
    List<OrderDetailStatReportVo> getStatReportByPlayOptions(Long marketId, Long playOptionsId,String orderNo,String matchType);


    /**
     * 查询平衡值明细
     * @param matchId
     * @param marketCategoryId
     * @return
     */
    List<OrderDetailStatReportVo> getMarketStatByMatchIdAndPlayId(Long matchId, Long marketCategoryId,Integer matchType);

    /**
     * 统计结算金额
     *
     * @param matchId
     */
    SettleAmountVo getSettleBetAmount(Long matchId);

    /**
     * 获取赛事总货量
     *
     * @param orderNo,matchId
     * @return
     */
    SumMatchAmountVo getMatchSumBetAmount(String orderNo,Long matchId);

    /**
     * @return java.util.List<com.panda.sport.rcs.vo.operation.CalcProfitDetailVo>
     * @Description 查询期望值详情
     * @Param [playName, matchId]
     * @Author toney
     * @Date 16:43 2019/12/10
     **/
    List<com.panda.sport.rcs.vo.operation.CalcProfitDetailVo> queryCalcProfitDetail(String playName, Long matchId);
}
