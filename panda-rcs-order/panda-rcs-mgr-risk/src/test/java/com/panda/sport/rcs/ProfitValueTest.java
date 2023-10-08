package com.panda.sport.rcs;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mgr.RiskBootstrap;
import com.panda.sport.rcs.mgr.operation.calc.CalcProfitRectangleAdapter;
import com.panda.sport.rcs.mgr.operation.calc.impl.AsianHandicapProfitRectangleServiceImpl;
import com.panda.sport.rcs.mgr.operation.order.CalcOrderAdapter;
import com.panda.sport.rcs.mgr.wrapper.statistics.RcsProfitMarketService;
import com.panda.sport.rcs.pojo.statistics.RcsProfitMarket;
import com.panda.sport.rcs.pojo.statistics.RcsProfitRectangle;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs
 * @Description :  TODO
 * @Date: 2020-01-14 18:13
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RiskBootstrap.class)
@Slf4j
public class ProfitValueTest {
    @Autowired
    private com.panda.sport.rcs.mgr.operation.order.impl.ProfitMarketServiceImpl profitMarketServiceImpl;

    @Autowired
    private AsianHandicapProfitRectangleServiceImpl asianHandicapProfitRectangleService;

//    @Test
    public void test() throws Exception {
        String order= "{\"createTime\":1584356481231,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":4,\"ip\":\"172.18.190.21\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betAmount1\":100,\"betNo\":\"28476449873920\",\"betTime\":1584356481231,\"createTime\":1584356481231,\"createUser\":\"系统\",\"dataSourceCode\":\"PA\",\"dateExpect\":\"2020-03-16\",\"handleAfterOddsValue\":2.03,\"handleAfterOddsValue1\":2.03,\"handleStatus\":0,\"handledBetAmout\":100,\"isRelationScore\":0,\"isValid\":1,\"marketId\":1239450043495268355,\"marketType\":\"EU\",\"marketValue\":\"-3.00\",\"matchId\":296097,\"matchInfo\":\"库里科联队 VS 卡拉雷联\",\"matchName\":\"甲级联赛\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":10300.0,\"modifyTime\":1584356481231,\"modifyUser\":\"系统\",\"oddFinally\":\"1.03\",\"oddsValue\":203000.0,\"orderNo\":\"18476449873921\",\"orderStatus\":0,\"paidAmount\":20299.999999999998046007476659724488854408264160156250000,\"paidAmount1\":202.999999999999980460074766597244888544082641601562500,\"platform\":\"PA\",\"playId\":2,\"playName\":\"大小盘\",\"playOptions\":\"Over\",\"playOptionsId\":1239450043524628482,\"playOptionsName\":\"大 -3.00\",\"recType\":0,\"recVal\":\"[[-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299],[-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299],[-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299],[-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299],[-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299],[-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299],[-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299],[-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299],[-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299],[-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299],[-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299],[-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299],[-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299,-10299]]\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"足球\",\"tournamentId\":1328,\"turnamentLevel\":10,\"uid\":131953900112781312,\"validateResult\":1}],\"modifyTime\":1584356481231,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"18476449873921\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"默认商户1\",\"uid\":131953900112781312,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean= JsonFormatUtils.fromJson(order,OrderBean.class);

        ConcurrentHashMap<Double, RcsProfitRectangle> map = new ConcurrentHashMap<Double, RcsProfitRectangle>();

        asianHandicapProfitRectangleService.logicHandle(orderBean.getItems().get(0),asianHandicapProfitRectangleService.initRcsProfitRectangle(orderBean.getItems().get(0),map,0.0,6.0),null,1);
    }




}
