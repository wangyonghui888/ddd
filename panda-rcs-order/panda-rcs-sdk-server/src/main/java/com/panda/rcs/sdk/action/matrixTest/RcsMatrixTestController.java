package com.panda.rcs.sdk.action.matrixTest;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.sdk.bean.MatrixBean;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.service.impl.matrix.MatrixAdapter;
import com.panda.sport.sdk.util.GuiceContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

/**
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.bootstrap.controller
 * @Description :  TODO
 * @Date: 2019-10-07 10:32
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@RestController
@RequestMapping(value = "matrixTest")
public class RcsMatrixTestController {

    MatrixAdapter matrixAdapter;

    @PostConstruct
    public void init() {
        matrixAdapter = GuiceContext.getInstance(MatrixAdapter.class);
    }

    @RequestMapping(value = "testMatrix")
    public String testMatrixCalculate(OrderTestEntity entity) {
        try {
            String order = "{\"createTime\":1589182306451,\"currencyCode\":\"RMB\",\"deviceType\":2,\"handleStatus\":0,\"infoStatus\":0,\"ip\":\"203.90.246.98\",\"ipArea\":\"中国,香港,\",\"items\":[{\"betAmount\":183400,\"betAmount1\":1834,\"betNo\":\"29454477615104\",\"betTime\":1589182306451,\"createTime\":1589182306451,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-05-11\",\"handleAfterOddsValue\":2.09,\"handleAfterOddsValue1\":2.09,\"handleStatus\":0,\"handledBetAmout\":1834,\"marketId\":1259679700079910914,\"marketType\":\"EU\",\"marketValue\":\"-0.5\",\"marketValueNew\":\"-0.5\",\"matchId\":280669,\"matchInfo\":\"戈梅利 火车头 VS NFK明斯克\",\"matchName\":\"白俄罗斯甲级联赛\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":199906.0,\"modifyTime\":1589182306451,\"modifyUser\":\"系统\",\"oddFinally\":\"2.09\",\"oddsValue\":209000.0,\"orderNo\":\"19454477615105\",\"orderStatus\":0,\"paidAmount\":383306.00,\"paidAmount1\":3833.06,\"platform\":\"PA\",\"playId\":4,\"playName\":\"让球盘\",\"playOptions\":\"1\",\"playOptionsId\":1259679700105076739,\"playOptionsName\":\"-0.5\",\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"足球\",\"tournamentId\":9955,\"turnamentLevel\":8,\"uid\":147247935887257600,\"validateResult\":0}],\"modifyTime\":1589182306451,\"orderAmountTotal\":183400,\"orderNo\":\"19454477615105\",\"orderStatus\":0,\"productAmountTotal\":183400,\"productCount\":1,\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":147247935887257600,\"userFlag\":\"\",\"validateResult\":0}";
            System.out.println(order);
            OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
            /*** 根据入参设置 玩法 和投注项 ***/
            if(entity.getScoreBenchmark()!=null) {
                orderBean.getItems().get(0).setScoreBenchmark(entity.getScoreBenchmark());
            }
            if(entity.getPlayId()!=null) {
                orderBean.getItems().get(0).setPlayId(entity.getPlayId());
            }
            if(entity.getPlayOptions()!=null) {
                orderBean.getItems().get(0).setPlayOptions(entity.getPlayOptions());
            }
            if(entity.getMarketValueNew()!=null) {
                orderBean.getItems().get(0).setMarketValueNew(entity.getMarketValueNew());
            }
            if(entity.getMarketValue()!=null) {
                orderBean.getItems().get(0).setMarketValue(entity.getMarketValue());
            }
            ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
            orderBean.setExtendBean(extendBean);
            MatrixBean bean = matrixAdapter.process("1", entity.getPlayId().toString(), orderBean.getExtendBean());
            if (bean.getRecType() == 1) {
                return "不支持矩阵计算";
            }

            StringBuilder sb = new StringBuilder("<html><body>");

            sb.append("<h1>备注：1：输  2：输半  3：赢  4：赢半   5：走水</h1><br>");

            sb.append("<table>");

            String rowTitle = "<th>比分矩阵</th>";
            String tablle = "";
            for (int homeIndex = 0; homeIndex < bean.getMatrixStatusArray().length; homeIndex++) {
                Integer[] rows = bean.getMatrixStatusArray()[homeIndex];
                tablle = tablle + "<tr>";
                rowTitle = rowTitle + String.format("<th style=\"width:30px;\">%s</th>", homeIndex);
                String colTitle = "";
                for (int awayIndex = 0; awayIndex < rows.length; awayIndex++) {
                    if (awayIndex == 0) colTitle = colTitle + String.format("<th>%s</th>", homeIndex);
                    colTitle = colTitle + String.format("<th style=\"width:30px;\">%s</th>", rows[awayIndex]);
                }
                tablle = tablle + colTitle + "</tr>";
            }
            sb.append(rowTitle).append(tablle);
            sb.append("</table></body></html>");
            return sb.toString();
        }
        catch (RcsServiceException e) {
            return e.getErrorMassage();
        }
    }
    
    
    @RequestMapping(value = "testMatrix2")
    public String testMatrixCalculate2(OrderTestEntity entity) {
        return "<html><body><table><tr>	<th>1</th>	<th>2</th>	<th>3</th>	<th>4</th>	<th>5</th>	<th>6</th>	<th>7</th>	<th>8</th>	<th>9</th>		<th>10</th>	<th>11</th>		<th>12</th>	<th>13</th>	</tr><tr>	<th>1111</th>	<th>2</th>	<th>3</th>	<th>4</th>	<th>5</th>	<th>6</th>	<th>7</th>	<th>8</th>	<th>9</th>		<th>10</th>	<th>11</th>		<th>12</th>	<th>13</th>	</tr></table></body></html>";
    }

    /**
     * @return com.panda.sport.data.rcs.dto.ExtendBean
     * @Description 根据orderItem 获取扩展 orderBean
     * @Param [bean, item]
     * @Author max
     * @Date 11:15 2019/12/11
     **/
    public ExtendBean buildExtendBean(OrderBean bean, OrderItem item) {
        ExtendBean extend = new ExtendBean();
        extend.setSeriesType(bean.getSeriesType());
        extend.setItemId(item.getBetNo());
        extend.setOrderId(item.getOrderNo());
        extend.setBusId(String.valueOf(bean.getTenantId()));
        extend.setHandicap(item.getMarketValue());
        extend.setCurrentScore(item.getScoreBenchmark());
        //item  1 ：早盘 ，2： 滚球盘， 3： 冠军盘
        extend.setIsScroll(String.valueOf(item.getMatchType()).equals("2") ? "1" : "0");
        extend.setMatchId(String.valueOf(item.getMatchId()));
        extend.setPlayId(item.getPlayId() + "");
        extend.setSelectId(String.valueOf(item.getPlayOptionsId()));
        extend.setSportId(String.valueOf(item.getSportId()));
        extend.setUserId(String.valueOf(item.getUid()));
        extend.setOdds(String.valueOf(item.getHandleAfterOddsValue()));
        extend.setMarketId(item.getMarketId().toString());
        //阶段
        extend.setPlayType("1");
        if (item.getBetAmount() != null) {
            extend.setOrderMoney(item.getBetAmount());
            extend.setCurrentMaxPaid(new BigDecimal(item.getBetAmount()).multiply((new BigDecimal(extend.getOdds()).subtract(new BigDecimal(1)))).longValue());
        } else {
            extend.setOrderMoney(0L);
            extend.setCurrentMaxPaid(0L);
        }
        extend.setItemBean(item);

        if (StringUtils.isBlank(extend.getHandicap())) {
            extend.setHandicap("0");
        }
        if (StringUtils.isBlank(extend.getCurrentScore())) {
            extend.setCurrentScore("0:0");
        }

        return extend;
    }

}
