package com.panda.sport.rcs.mgr.wrapper.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.exeception.LogicException;
import com.panda.sport.rcs.log.LogContext;
import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.pojo.MatchStatisticsInfo;
import com.panda.sport.rcs.pojo.TOrderDetail;
import com.panda.sport.rcs.mgr.wrapper.ITOrderDetailService;
import com.panda.sport.rcs.mgr.wrapper.MatchStatisticsInfoService;
import com.panda.sport.rcs.vo.MatrixVo;
import com.panda.sport.rcs.vo.OrderDetailStatReportVo;
import com.panda.sport.rcs.vo.statistics.SettleAmountVo;
import com.panda.sport.rcs.vo.statistics.SumMatchAmountVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 投注单详细信息表 服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-03
 */
@Service
@Slf4j
public class TOrderDetailServiceImpl extends ServiceImpl<TOrderDetailMapper, TOrderDetail> implements ITOrderDetailService {

    @Autowired
    private TOrderDetailMapper orderDetailMapper;

    @Autowired
    private MatchStatisticsInfoService matchStatisticsInfoService;

    @Override
    public MatrixVo[][] getHalfMatrixByMatchId(TOrderDetail item, Date startTime, Date endTime, List<Long> playIds, List<Long> tenantIds, Integer unit, Integer size) {
        if (item.getMatchId() != null && item.getMatchId() <= 0) throw new LogicException("60019", "半场矩阵查询matchId不能为空");
        if (unit != null && unit <= 0) throw new LogicException("60019", "半场矩阵查询单位不合法");
        //if(!StandardSportMarketCategory.firstHalfMatchCategorys.contains(item.getPlayId())) throw new LogicException("60020", "半场矩阵查询玩法ID不合法");
        List<TOrderDetail> list = getMatrixList(item, startTime, endTime, playIds, tenantIds);
        if (list == null) throw new LogicException("60021", "该赛事没有任何注单记录");
        String score = getCurrentScore(item.getMatchId());
        int homeSize = Optional.ofNullable(size).orElse(5) + Integer.parseInt(score.split(":")[0]) + 1;
        int awaySize = Optional.ofNullable(size).orElse(5) + Integer.parseInt(score.split(":")[1]) + 1;
        homeSize = homeSize > 7 ? 7 : homeSize;
        awaySize = awaySize > 7 ? 7 : awaySize;
        Long[][] result = new Long[homeSize][awaySize];
        for (TOrderDetail detail : list) {
            String matrixStr = detail.getRecVal();
            Long[][] arr;
            try {
                arr = JSON.parseObject(matrixStr, Long[][].class);
            } catch (Exception e) {
                log.error("{},当前注单存入矩阵数据有误：betNo: {}", this.getClass(), detail.getBetNo());
                continue;
            }
            for (int i = 0; i < homeSize; i++) {
                for (int j = 0; j < awaySize; j++) {
                    result[i][j] = Optional.ofNullable(result[i][j]).orElse((long) 0) + Optional.ofNullable(arr[i][j]).orElse((long) 0);
                }
            }
        }
        MatrixVo[][] mtx = new MatrixVo[homeSize][awaySize];
        List<Map.Entry<String, Long>> lowerlist = new ArrayList<Map.Entry<String, Long>>();
        List<Map.Entry<String, Long>> greaterlist = new ArrayList<Map.Entry<String, Long>>();
        sortMatrix(result, homeSize, awaySize, unit, lowerlist, greaterlist);
        getLeveledMatrix(lowerlist, mtx, score);
        getLeveledMatrix(greaterlist, mtx, score);
        return mtx;
    }

    private List<TOrderDetail> getMatrixList(TOrderDetail item, Date startTime, Date endTime, List<Long> playIds, List<Long> tenantIds) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("matchId", item.getMatchId());
        map.put("playIds", playIds == null || playIds.size() == 0 ? null : playIds);
        map.put("tenantIds", tenantIds == null || tenantIds.size() == 0 ? null : tenantIds);

        if (item.getIsSettlement() != null) {
            map.put("isSettlement", item.getIsSettlement());
        }

        map.put("matchType", item.getMatchType());
        map.put("uid", item.getUid());
        //map.put("beginTime", Optional.ofNullable(startTime).orElse(DateUtils.dayBegin(Calendar.getInstance().getTime())).getTime());
        //map.put("endTime", Optional.ofNullable(endTime).orElse(DateUtils.dayEnd(Calendar.getInstance().getTime())).getTime());
        return orderDetailMapper.getMatrixValList(map);
    }

    private void sortMatrix(Long[][] result, int homeSize, int awaySize, int unit, List<Map.Entry<String, Long>> lowerlist, List<Map.Entry<String, Long>> greaterlist) {
        Map<String, Long> lowermap = Maps.newTreeMap();
        Map<String, Long> greatermap = Maps.newTreeMap();
        for (int i = 0; i < homeSize; i++) {
            for (int j = 0; j < awaySize; j++) {
                long cur = Optional.ofNullable(result[i][j]).orElse(0l);
                if (cur < 0) lowermap.put(i + "," + j, cur / unit);
                else greatermap.put(i + "," + j, cur / unit);
            }
        }
        // map转换成list进行排序
        List<Map.Entry<String, Long>> lowlist = new ArrayList<Map.Entry<String, Long>>(lowermap.entrySet());
        List<Map.Entry<String, Long>> greatlist = new ArrayList<Map.Entry<String, Long>>(greatermap.entrySet());
        Collections.sort(lowlist, new Comparator<Map.Entry<String, Long>>() {
            @Override
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                return (int) (o1.getValue().longValue() - o2.getValue().longValue());
            }
        });
        Collections.sort(greatlist, new Comparator<Map.Entry<String, Long>>() {
            @Override
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                return (int) (o2.getValue().longValue() - o1.getValue().longValue());
            }
        });
        lowerlist.addAll(lowlist);
        greaterlist.addAll(greatlist);
    }

    private void getLeveledMatrix(List<Map.Entry<String, Long>> list, MatrixVo[][] mtx, String score) {
        //MatrixVo[][] mtx = new MatrixVo[homeSize][awaySize];
        int k = 0, dept = list.size();
        int secondDept = (int) (dept * 0.05);
        int thirdDept = (int) (dept * 0.2);
        int forthDept = (int) (dept * 0.5);
        String[] scoreArr = score.split(":");
        Integer homeScore = Integer.parseInt(scoreArr[0]);
        Integer awayScore = Integer.parseInt(scoreArr[1]);
        Long min = 0L;
        for (Map.Entry<String, Long> entity : list) {
            String key = entity.getKey();
            Long value = Math.abs(entity.getValue());
            String[] keyArr = key.split(",");
            int i = Integer.parseInt(keyArr[0]);
            int j = Integer.parseInt(keyArr[1]);
            MatrixVo vo;
            if ((homeScore > 0 && i < homeScore) || (awayScore > 0 && j < awayScore)) {
                vo = getMatrixVo(0, 0l, Boolean.TRUE);
            } else {
                if (value.compareTo(min) >= 0) {
                    vo = getMatrixVo(NumberUtils.INTEGER_ONE, entity.getValue(), Boolean.FALSE);
                    min = value;
                } else {
                    if (k <= secondDept) {
                        vo = getMatrixVo(NumberUtils.INTEGER_TWO, entity.getValue(), Boolean.FALSE);
                    } else if (k > secondDept && k <= thirdDept) {
                        vo = getMatrixVo(Integer.valueOf(3), entity.getValue(), Boolean.FALSE);
                    } else if (k > thirdDept && k <= forthDept) {
                        vo = getMatrixVo(Integer.valueOf(4), entity.getValue(), Boolean.FALSE);
                    } else {
                        vo = getMatrixVo(Integer.valueOf(5), entity.getValue(), Boolean.FALSE);
                    }
                }
            }
            k++;
            mtx[i][j] = vo;
        }
    }

    private MatrixVo getMatrixVo(int level, Long value, Boolean isOutcome) {
        MatrixVo vo = new MatrixVo();
        vo.setLevel(level);
        vo.setValue(value / OrderItem.PlUSTIMES);
        vo.setIsOutcome(isOutcome);
        return vo;
    }
    /*@Autowired
    TOrderDetailMapper orderDetailMapper;
    @Override
    public boolean updateOrderDetailAfterRefund(TOrderDetail detail) {
        return orderDetailMapper.updateOrderDetailAfterRefund(detail);
    }*/


    /**
     * @return com.panda.sport.rcs.vo.MatrixVo[][] 默认返回 12*12 大小的矩阵
     * @Description 查询玩法管理中 赛事订单对应的比分矩阵
     * 滚球比分结算：所有矩阵进行累加
     * 最终比分结算 ：
     * 结算时的最新比分-获取结算订单信息 对应的单元格值为 结算金额 其它单元格比分为 0
     * @Param [tenantId, matchId, matchType, settleStatus, playIds, unit]
     * @Param tenantId 商户ID
     * @Param matchId 赛事ID
     * @Param matchType  类型：1 ：早盘 ，2： 滚球盘， 3： 冠军盘 , 全部 null
     * @Param settleStatus 结算状态 0 未结算 1：已结算  全部 null
     * @Param playIds 玩法ID集合
     * 单位 1 10 100 1000
     * @Author max
     * @Date 11:25 2019/11/8
     **/
    @Override
    public MatrixVo[][] queryMatrixByMatchId(List<Long> tenantIds, Long matchId, Integer matchType, Integer settleStatus, List<Long> playIds, Integer unit, Integer size) {
        if (matchId == null || matchId <= 0) {
            throw new LogicException("60019", "matchId不能为空");
        }
        if (unit != null && unit <= 0) {
            throw new LogicException("60019", "查询单位不合法");
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("matchId", matchId);
        map.put("playIds", playIds == null || playIds.size() == 0 ? null : playIds);
        map.put("tenantIds", tenantIds == null || tenantIds.size() == 0 ? null : tenantIds);
        map.put("isSettlement", settleStatus);
        map.put("matchType", matchType);
        List<TOrderDetail> list = orderDetailMapper.getMatrixValList(map);
        if (list == null) throw new LogicException("60021", "该赛事没有任何注单记录");

        String score = getCurrentScore(matchId);
        int homeSize = Optional.ofNullable(size).orElse(10) + Integer.parseInt(score.split(":")[0]) + 1;
        int awaySize = Optional.ofNullable(size).orElse(10) + Integer.parseInt(score.split(":")[1]) + 1;
        homeSize = homeSize > 13 ? 13 : homeSize;
        awaySize = awaySize > 13 ? 13 : awaySize;
        Long[][] result = new Long[homeSize][awaySize];
        for (int m = 0; m < homeSize; ++m) {
            for (int n = 0; n < awaySize; ++n) {
                result[m][n] = 0L;
            }
        }
        for (TOrderDetail detail : list) {
            //只计算可以用比分矩阵计算的订单
            if (detail.getRecType() != null && detail.getRecType() != 0) {
                continue;
            }
            //早盘已结算的不用比分矩阵
            if (detail.getIsSettlement() != null && detail.getIsSettlement() == 1 && matchType == 1) {
                continue;
            }

            String matrixStr = detail.getRecVal();
            Long[][] arr;
            try {
                arr = JSON.parseObject(matrixStr, Long[][].class);
            } catch (Exception e) {
                log.error("requestId:{},当前注单存入矩阵数据有误, betNo:{},detail:{}", LogContext.getContext().getRequestId(), detail.getBetNo(), detail);
                continue;
            }
            for (int i = 0; i < homeSize; i++) {
                for (int j = 0; j < awaySize; j++) {
                    try {
                        result[i][j] += Optional.ofNullable(arr[i][j]).orElse((long) 0);
                    } catch (Exception e) {
                        log.error("requestId:{},矩阵 arr[{}][{}] 转换失败!", LogContext.getContext().getRequestId(), i, j);
                    }
                }
            }
        }

        MatrixVo[][] mtx = new MatrixVo[homeSize][awaySize];
        List<Map.Entry<String, Long>> lowerlist = new ArrayList<Map.Entry<String, Long>>();
        List<Map.Entry<String, Long>> greaterlist = new ArrayList<Map.Entry<String, Long>>();
        sortMatrix(result, homeSize, awaySize, unit, lowerlist, greaterlist);
        getLeveledMatrix(lowerlist, mtx, score);
        getLeveledMatrix(greaterlist, mtx, score);
        return mtx;
    }

    private String getCurrentScore(Long matchId) {
        LambdaQueryWrapper<MatchStatisticsInfo> lambdaQueryWrapper = new QueryWrapper<MatchStatisticsInfo>().lambda();
        lambdaQueryWrapper.select(MatchStatisticsInfo::getScore);
        lambdaQueryWrapper.eq(MatchStatisticsInfo::getStandardMatchId, matchId).last(" limit 1");
        MatchStatisticsInfo result = matchStatisticsInfoService.getOne(lambdaQueryWrapper);
        if (result != null && !Strings.isNullOrEmpty(result.getScore())) return result.getScore();
        else return "0:0";
    }


    /**
     * @return java.util.List<com.panda.sport.rcs.vo.OrderDetailStatReportVo>
     * @Description 统计数据
     * @Param [marketId, playOptionsId]
     * @Author toney
     * @Date 16:21 2019/11/29
     **/
    @Override
    public List<OrderDetailStatReportVo> getStatReportByPlayOptions(Long marketId, Long playOptionsId,String orderNo,String matchType) {
        return orderDetailMapper.getStatReportByPlayOptions(marketId,orderNo,matchType);
    }

    /**
     * 获取已结算数据
     *
     * @param matchId
     */
    @Override
    public SettleAmountVo getSettleBetAmount(Long matchId) {
        return orderDetailMapper.getSettleBetAmount(matchId);
    }


    /**
     * 获取赛事总货量
     *
     * @param orderNo,matchId
     * @return
     */
    @Override
    public SumMatchAmountVo getMatchSumBetAmount(String orderNo,Long matchId) {
        return orderDetailMapper.getMatchSumBetAmount(orderNo,matchId);
    }

    @Override
    public List<OrderDetailStatReportVo> getMarketStatByMatchIdAndPlayId(Long matchId, Long marketCategoryId,Integer matchType) {
        return orderDetailMapper.getMarketStatByMatchIdAndPlayIdAndMatchStatus(matchId,marketCategoryId,matchType);
    }
    /**
     * @return java.util.List<com.panda.sport.rcs.vo.operation.CalcProfitDetailVo>
     * @Description 查询期望值详情
     * @Param [playName, matchId]
     * @Author toney
     * @Date 16:43 2019/12/10
     **/
    @Override
    public List<com.panda.sport.rcs.vo.operation.CalcProfitDetailVo> queryCalcProfitDetail(String playName, Long matchId) {
        return orderDetailMapper.queryCalcProfitDetail(playName, matchId);
    }
}
