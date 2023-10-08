package com.panda.sport.rcs.task.service.profit;

import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.task.job.operation.ProfitRectangleVo;
import com.panda.sport.rcs.task.service.profit.impl.AsianHandicapProfitRectangleServiceImpl;
import com.panda.sport.rcs.task.service.profit.impl.GoalLineProfitRectangleServiceImpl;
import com.panda.sport.rcs.task.wrapper.RcsMarketOddsConfigService;
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
     *
     * @param rectangleVo
     * @return
     */
    public void syncData(ProfitRectangleVo rectangleVo) {

        try {
            List<OrderDetailStatReportVo> list = rcsMarketOddsConfigService.queryMarketStatByMarketId(rectangleVo.getMarketId());
            ProfitDetailStatReportVo profitDetailStatReportVo = new ProfitDetailStatReportVo();
            profitDetailStatReportVo.setData(list);
            profitDetailStatReportVo.setMarketId(rectangleVo.getMarketId());
            profitDetailStatReportVo.setPlayId(rectangleVo.getPlayId());
            profitDetailStatReportVo.setMatchId(rectangleVo.getMatchId());
            producerSendMessageUtils.sendMessage(MqConstants.WS_ODDS_CHANGED_TOPIC, MqConstants.WS_SCROLL_BALL_LIVE_BET_DETAIL_BY_PLAYID_12_15_CHANGED_TAG, "", profitDetailStatReportVo);
        } catch (Exception ex) {
            log.error("推送期望详情失败:", ex.getMessage(), ex);
        }

    }

    /**
     * @return void
     * @Description 期望详情处理
     * @Param [orderBean]
     * @Author toney
     * @Date 17:42 2019/12/17
     **/
    public void calc(ProfitRectangleVo rectangleVo) {
        try {
            log.info("期望详情消息开始处理：" + rectangleVo.toString());

            Integer playId = rectangleVo.getPlayId();
            if (playId == 4 || playId == 19 || playId == 113) {
                asianHandicapProfitRectangleService.handle(rectangleVo);
            } else if (playId == 2 || playId == 18 || playId == 114) {
                goalLineProfitRectangleService.handle(rectangleVo);
            } else if (playId == 12 || playId == 15 || playId == 111 || playId == 118) {
                syncData(rectangleVo);
            }

        } catch (Exception ex) {
            log.error("期望详情消息处理:", ex);
        }
        log.info("期望详情消息结束处理");
    }
}
