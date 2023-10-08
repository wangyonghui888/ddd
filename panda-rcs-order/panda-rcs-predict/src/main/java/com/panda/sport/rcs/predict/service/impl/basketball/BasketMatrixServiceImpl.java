package com.panda.sport.rcs.predict.service.impl.basketball;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.statistics.RcsFirstMarket;
import com.panda.sport.rcs.pojo.statistics.RcsOrderBasketballMatrix;
import com.panda.sport.rcs.pojo.statistics.RcsPredictBasketballMatrix;
import com.panda.sport.rcs.predict.service.BasketBallForecastService;
import com.panda.sport.rcs.service.IRcsFirstMarketService;
import com.panda.sport.rcs.service.IRcsOrderBasketballMatrixService;
import com.panda.sport.rcs.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @description: 篮球数据保存
 * @author: lithan
 * @date: 2021-1-9 16:48:58
 **/
@Slf4j
@Service("basketMatrixService")
public class BasketMatrixServiceImpl {

    @Autowired
    IRcsOrderBasketballMatrixService orderBasketballMatrixService;

    @Autowired
    private IRcsFirstMarketService rcsFirstMarketService;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    public void forecastDataSave(OrderBean orderBean, List<RcsPredictBasketballMatrix> list) {
        OrderItem item = orderBean.getItems().get(0);
        log.info("预测数据计算-篮球矩阵-{}-{}-{}", item.getOrderNo(), item.getMatchId(), item.getPlayId());
        HashMap<String, String> map = new HashMap<>();
        map.put("time", "" + System.currentTimeMillis());
        String hashKey = getCacheKey(item);
        producerSendMessageUtils.sendMsg("mq_data_rcs_predict_basketball_matrix", "", "", JSONObject.toJSONString(list), map, hashKey);
        log.info("预测数据计算-篮球矩阵-{}-{}-{}-RCS_PREDICT_BASKETBALL_MATRIX_SAVE发送完成", item.getOrderNo(), item.getMatchId(), item.getPlayId());
    }

    /**
     * 获取让球玩法的中值
     * @param matchId 赛事
     * @param playId 玩法
     * @param type 类型  1 大小 2 分差
     * @return
     */
    public int getMiddleValue(Long matchId, Integer playId, Integer type) {
        //全场
        Integer all[] = new Integer[]{37, 38, 39, 40, 41, 200, 209, 210, 211, 212, 218, 217, 141, 5, 4, 3, 2, 1, 15};
        //上半场
        Integer beforeHalf[] = new Integer[]{17, 18, 19, 42, 43, 219};
        //下半场
        Integer afterHalf[] = new Integer[]{25, 26, 75, 142, 143};
        //第一节
        Integer one[] = new Integer[]{44, 45, 46, 47, 48, 49};
        //第二节
        Integer two[] = new Integer[]{50, 51, 52, 53, 54, 55};
        //第三节
        Integer three[] = new Integer[]{56, 57, 58, 59, 60, 61};
        //第四节
        Integer four[] = new Integer[]{62, 63, 64, 65, 66, 67};

        int middle = 0;
        //大小  读取全场大小的初盘
        try {
            LambdaQueryWrapper<RcsFirstMarket> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RcsFirstMarket::getStandardMatchId, matchId);
            wrapper.eq(RcsFirstMarket::getType, 1);
            if (type == 1) {
                wrapper.eq(RcsFirstMarket::getPlayId, 38);
            }
            //分差  读取全场初盘
            if (type == 2) {
                wrapper.eq(RcsFirstMarket::getPlayId, 39);
            }
            RcsFirstMarket firstMarket = rcsFirstMarketService.getOne(wrapper);
            middle = Double.valueOf(firstMarket.getValue()).intValue();

            //如果是半场玩法
            if (Arrays.asList(beforeHalf).contains(playId) || Arrays.asList(afterHalf).contains(playId)) {
                middle = middle / 2;
            }
            //如果是小节玩法
            if (Arrays.asList(one).contains(playId) || Arrays.asList(two).contains(playId) || Arrays.asList(three).contains(playId) || Arrays.asList(four).contains(playId)) {
                middle = middle / 4;
            }
        } catch (Exception e) {
            log.info("预测数据计算-篮球矩阵-中值读取异常:{},{}", e.getMessage(), e);
            throw new RcsServiceException("中值读取异常" + e.getMessage());
        }
        log.info("中值返回:{}", middle);
        log.info("预测数据计算-篮球矩阵-中值返回{}-{}-{}", matchId, playId, middle);
        return middle;
    }

    /**
     *
     */
    public BasketBallForecastService getBasketBallForecastService(Integer playId) {
        BasketBallForecastService service = null;
        //分差 让分
        Integer difference[] = new Integer[]{39, 4, 19, 58, 46, 64, 52, 143};
        if (Arrays.asList(difference).contains(playId)) {
            return SpringContextUtils.getBean("basketBallAsianHandicapService");
        }
        //大小
        Integer greaterLess[] = new Integer[]{38, 2, 18, 57, 45, 63, 51, 26};
        if (Arrays.asList(greaterLess).contains(playId)) {
            return SpringContextUtils.getBean("basketMatrixGoalLineService");
        }
        //独赢
        Integer win[] = new Integer[]{37, 5, 43, 60, 48, 66, 54, 142};
        if (Arrays.asList(win).contains(playId)) {
            return SpringContextUtils.getBean("basketMatrixWinService");
        }
        //全场胜平负
        Integer allWin[] = new Integer[]{1, 17, 56, 44, 62, 50, 25};
        if (Arrays.asList(allWin).contains(playId)) {
            return SpringContextUtils.getBean("basketMatrixAllWinService");
        }
        //单双
        Integer singleDouble[] = new Integer[]{40, 15, 42, 59, 47, 65, 53, 75};
        if (Arrays.asList(singleDouble).contains(playId)) {
            return SpringContextUtils.getBean("basketMatrixSingleDoubleService");
        }
        //让分胜平负
        Integer LetPoints[] = new Integer[]{3};
        if (Arrays.asList(LetPoints).contains(playId)) {
            return SpringContextUtils.getBean("basketBallLetPointsService");
        }
        //是否加时
        Integer isOverTime[] = new Integer[]{41};
        if (Arrays.asList(isOverTime).contains(playId)) {
            return SpringContextUtils.getBean("basketMatrixIsOvertimeService");
        }
        //净胜分
        Integer victoryPoints[] = new Integer[]{219, 61, 49, 67, 55};
        if (Arrays.asList(victoryPoints).contains(playId)) {
            return SpringContextUtils.getBean("basketMatrixVictoryPointsService");
        }
        //净胜分3项
        Integer victoryPoints3[] = new Integer[]{200, 141};
        if (Arrays.asList(victoryPoints3).contains(playId)) {
            return SpringContextUtils.getBean("basketMatrixVictoryPoints3Service");
        }
        //净胜分6项
        Integer victoryPoints6[] = new Integer[]{209};
        if (Arrays.asList(victoryPoints6).contains(playId)) {
            return SpringContextUtils.getBean("basketMatrixVictoryPoints6Service");
        }
        //净胜分7项
        Integer victoryPoints7[] = new Integer[]{210};
        if (Arrays.asList(victoryPoints7).contains(playId)) {
            return SpringContextUtils.getBean("basketMatrixVictoryPoints7Service");
        }
        //净胜分12项
        Integer victoryPoints12[] = new Integer[]{211};
        if (Arrays.asList(victoryPoints12).contains(playId)) {
            return SpringContextUtils.getBean("basketMatrixVictoryPoints12Service");
        }
        //净胜分14项
        Integer victoryPoints14[] = new Integer[]{212};
        if (Arrays.asList(victoryPoints14).contains(playId)) {
            return SpringContextUtils.getBean("basketMatrixVictoryPoints14Service");
        }
        //总分区间 不含加时
        Integer allScope[] = new Integer[]{218};
        if (Arrays.asList(allScope).contains(playId)) {
            return SpringContextUtils.getBean("basketMatrixAllScopeService");
        }
        //准确总分大小BasketMatrixAccurateServiceImpl
        Integer Accurate[] = new Integer[]{217};
        if (Arrays.asList(Accurate).contains(playId)) {
            return SpringContextUtils.getBean("basketBallAccurateService");
        }
        return null;
    }


    public String getCacheKey(OrderItem orderItem) {
        String key = "rcs:risk:predict:basketMatrixBall.match_id.%s.match_type.%s.play_id.%s";
        key = String.format(key, orderItem.getMatchId(), orderItem.getMatchType(), orderItem.getPlayId());
        return key;
    }

    /**
     * @param orderBean
     * @param rec 矩阵数据
     */
    public void saveBasketballOrderMatrix(OrderBean orderBean, String rec ,int middle ,int type) {
        OrderItem item = orderBean.getItems().get(0);
        log.info("预测数据计算-篮球矩阵 rcs_order_basketball_matrix 保存开始:{}", item.getOrderNo());
        try {
            if (type == -1) {
                LambdaQueryWrapper<RcsOrderBasketballMatrix> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(RcsOrderBasketballMatrix::getOrderNo, item.getOrderNo());
                orderBasketballMatrixService.remove(wrapper);
                log.info("预测数据计算-篮球矩阵取消 rcs_order_basketball_matrix 取消完成:{}", item.getOrderNo());
                return;
            }
            rec = rec.substring(0, rec.length() - 1);
            RcsOrderBasketballMatrix bean = new RcsOrderBasketballMatrix();
            bean.setBetTime(item.getBetTime());
            bean.setCreateTime(System.currentTimeMillis());
            bean.setInitMarket(middle);
            bean.setMatchId(item.getMatchId());
            bean.setMatchType(item.getMatchType());
            bean.setOrderNo(item.getOrderNo());
            bean.setPlayId(item.getPlayId());
            bean.setSportId(item.getSportId());
            bean.setTenantId(orderBean.getTenantId());
            bean.setTournamentId(item.getTournamentId());
            bean.setRecVal(rec);
            orderBasketballMatrixService.save(bean);
            log.info("预测数据计算-篮球矩阵 rcs_order_basketball_matrix 保存完成:{}", item.getOrderNo());
        } catch (Exception e) {
            log.info("预测数据计算-篮球矩阵 rcs_order_basketball_matrix 保存异常:{},{},{}", item.getOrderNo(), e.getMessage(), e);
            throw new RcsServiceException("预测数据计算-篮球矩阵 rcs_order_basketball_matrix保存异常" + e.getMessage());
        }
    }
}