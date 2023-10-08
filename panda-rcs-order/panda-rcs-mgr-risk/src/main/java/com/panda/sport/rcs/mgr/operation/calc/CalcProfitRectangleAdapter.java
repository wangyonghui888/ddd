package com.panda.sport.rcs.mgr.operation.calc;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mgr.operation.calc.impl.AsianHandicapProfitRectangleServiceImpl;
import com.panda.sport.rcs.mgr.operation.calc.impl.GoalLineProfitRectangleServiceImpl;
import com.panda.sport.rcs.mgr.wrapper.RcsMarketOddsConfigService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.statistics.RcsProfitMarket;
import com.panda.sport.rcs.profit.utils.ProfitUtil;
import com.panda.sport.rcs.vo.OrderDetailStatReportVo;
import com.panda.sport.rcs.vo.statistics.ProfitDetailStatReportVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author :  toney
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.mgr.operation.calc
 * @Description :  统计
 * @Date: 2019-12-17 17:37
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
public class CalcProfitRectangleAdapter {
    /**
     * @Description 相关业务实现类
     * @Param
     * @Author toney
     * @Date 16:45 2019/10/26
     * @return
     **/
    private List<IProfitRectangle> profitRectangleList;

    @Autowired
    private AsianHandicapProfitRectangleServiceImpl asianHandicapProfitRectangleService;

    @Autowired
    private GoalLineProfitRectangleServiceImpl goalLineProfitRectangleService;

    /**
     * @return
     * @Description 初始化
     * @Param [calcOrderList]
     * @Author toney
     * @Date 16:46 2019/10/26
     **/
    public CalcProfitRectangleAdapter(List<IProfitRectangle> profitRectangles) {
        if (profitRectangles == null) {
            return;
        }
        this.profitRectangleList = profitRectangles;
    }

    @Autowired
    private RcsMarketOddsConfigService rcsMarketOddsConfigService;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;
    /**
     * 全场单双、双方是否都进球
     * @param orderBean
     * @return
     */
    private void syncData(OrderBean orderBean) {
        //拒单
        if(orderBean.getValidateResult()!=1){
            return;
        }
        for(OrderItem orderItem:orderBean.getItems()) {
            try {
                //拒单
                if(orderItem.getValidateResult() !=1){
                    continue;
                }

                List<OrderDetailStatReportVo> list = rcsMarketOddsConfigService.queryMarketStatByMarketId(orderItem.getMarketId());
                ProfitDetailStatReportVo profitDetailStatReportVo = new ProfitDetailStatReportVo();
                profitDetailStatReportVo.setData(list);
                profitDetailStatReportVo.setMarketId(orderItem.getMarketId());
                profitDetailStatReportVo.setPlayId(orderItem.getPlayId());
                profitDetailStatReportVo.setMatchId(orderItem.getMatchId());
                producerSendMessageUtils.sendMessage(MqConstants.WS_ODDS_CHANGED_TOPIC, MqConstants.WS_SCROLL_BALL_LIVE_BET_DETAIL_BY_PLAYID_12_15_CHANGED_TAG, "", profitDetailStatReportVo);
            } catch (Exception ex) {
                log.error("::{}::推送期望详情失败:{}",orderBean.getOrderNo(), ex.getMessage(), ex);
            }
        }
    }

    /**
     * @param bean 
     * @return void
     * @Description 期望详情处理
     * @Param [orderBean]
     * @Author toney
     * @Date 17:42 2019/12/17
     **/
    public void calc(OrderBean orderBean, RcsProfitMarket bean, Integer type) {
        //log.info("期望值详情最终数据处理：" + this.getClass() +"bean:"+JsonFormatUtils.toJson(orderBean));

        //只取单条数据,不要串关的
        if (orderBean.getSeriesType() != 1) {
            log.error("::{}::期望详情消息处理：串关数据不处理，bean{}",orderBean.getOrderNo(),JsonFormatUtils.toJson(orderBean));
            return;
        }
        try {
            //log.info("期望详情消息开始处理：{}",JSONObject.toJSONString(orderBean));
            if (orderBean.getItems().size() > 0) {
                Integer playId = orderBean.getItems().get(0).getPlayId();
                if (ProfitUtil.checkAsianHandicap(playId)) {
                    asianHandicapProfitRectangleService.handle(orderBean,bean, type);
                } else if (ProfitUtil.checkGoalLine(playId)) {
                    goalLineProfitRectangleService.handle(orderBean,bean, type);
                }else if(ProfitUtil.CheckOther(playId) ){
                    syncData(orderBean);
                }
            }
        } catch (Exception ex) {
            log.error("::{}::期望详情消息处理:{}",orderBean.getOrderNo(),ex.getMessage(),ex);
        }
        //log.info("期望详情消息结束处理" );
    }
}
