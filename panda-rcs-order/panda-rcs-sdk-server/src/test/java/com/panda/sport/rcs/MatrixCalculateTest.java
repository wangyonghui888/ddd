package com.panda.sport.rcs;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.panda.sport.data.rcs.api.OrderPaidApiService;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.sdk.bean.DubboProtocolConfig;
import com.panda.sport.sdk.bean.DubboRegistryConfig;
import com.panda.sport.sdk.bean.DubboServiceConfig;
import com.panda.sport.sdk.bean.MatrixBean;
import com.panda.sport.sdk.core.Sdk;
import com.panda.sport.sdk.service.impl.OrderPaidApiImpl;
import com.panda.sport.sdk.service.impl.matrix.MatrixAdapter;
import com.panda.sport.sdk.util.GuiceContext;
import com.panda.sport.sdk.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs
 * @Description :  TODO
 * @Date: 2020-01-14 18:13
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@SpringBootApplication
@ComponentScan(basePackages = "com.panda.rcs.sdk")
public class MatrixCalculateTest {

    MatrixAdapter matrixAdapter;

    private static final Logger logger = LoggerFactory.getLogger(MatrixCalculateTest.class);


    /**
     * pom里面配置了  会自动执行此方法
     */
    public static void main(String[] args) {
        Properties properties = getNacosConfig();

        Map<String, String> map = new HashMap<String, String>((Map) properties);
        Map<String, Map<String, String>> paramMap = new HashMap<>(1);
        paramMap.put("sdk-properties", map);
        Sdk.initProperties(paramMap); 

        SpringApplication.run(MatrixCalculateTest.class, args);
        MatrixCalculateTest matrixCalculateTest = new MatrixCalculateTest();
        matrixCalculateTest.matrixAdapter = GuiceContext.getInstance(MatrixAdapter.class);
        matrixCalculateTest.matrixCalculate();
        System.exit(0);
    }

    private static Properties getNacosConfig() {
        try {

            PropertiesUtil propertiesUtil = new PropertiesUtil();
            String serverAddr = propertiesUtil.getValue("sdk.nacos.server.addr");
            String dataId = propertiesUtil.getValue("sdk.nacos.data.id");
            String group = propertiesUtil.getValue("sdk.nacos.group");
            String namespace = propertiesUtil.getValue("sdk.nacos.namespace");

            Properties properties = new Properties();
            properties.put("serverAddr", serverAddr);
            properties.put("namespace", namespace);

            ConfigService configService = NacosFactory.createConfigService(properties);

            String content = configService.getConfig(dataId, group, 5000);

            //转换成Properties
            InputStream inputStream = new ByteArrayInputStream(content.getBytes());
            Properties nacosProperties = new Properties();
            nacosProperties.load(inputStream);

            return nacosProperties;

        } catch (Exception e) {
            logger.info("nacos config NacosException" + e);
            logger.error("nacos config error", e);
        }
        return null;
    }

    public void matrixCalculate() {
        try {
            /*** BothTeamsScore ***/
            matrixCalculate12();
            
            // matrixCalculate24();
            /*** Competitor1CleanSheet ***/
            matrixCalculate81();
            // matrixCalculate90();
            
            // Competitor1ExactGoals
            //matrixCalculate8();
            matrixCalculate21();
            
            /*** Competitor1NoBet ***/
            matrixCalculate77();
            /*** Competitor1OddEven ***/
            matrixCalculate78();
            /*** Competitor1Total ***/
            matrixCalculate87();
            //matrixCalculate10();
            /*** Competitor1WinNoNil ***/
            matrixCalculate82();
            /*** Competitor2CleanSheet ***/
            matrixCalculate79();
            // matrixCalculate100();
            /*** Competitor2ExactGoals ***/
            matrixCalculate9();
            // matrixCalculate22();
            /*** Competitor2NoBet ***/
            matrixCalculate91();
            /***Competitor2OddEven***/
            matrixCalculate92();
            /*** Competitor2Total ***/
            matrixCalculate11();
            //  matrixCalculate97();
            /*** Competitor2WinNoNil ***/
            matrixCalculate80();
            /*** CorrectScore ***/
            matrixCalculate7();
            /***  DoubleChance  ***/
            matrixCalculate6();
            // matrixCalculate70();
            /***  DoubleChanceBothTeamScore  ***/
            matrixCalculate107();
            /*** DrawNoBet ***/
            matrixCalculate4();
            /*** ExactGoals ***/
            matrixCalculate14();
            /*** FirstCorrectScore ***/
            matrixCalculate20();
            /*** FirstHalfExactGoals ***/
            matrixCalculate23();
            /*** GoalRange  ***/
            matrixCalculate68();
            /*** HandicapOr1X2 ***/
            matrixCalculate1();
            /*** Match1X2BothTeamScore ***/
            matrixCalculate101();
            /*** OddEven ***/
            matrixCalculate15();
            /*** Total ***/
            matrixCalculate2();
            /*** TotalAnd1X2 ***/
            matrixCalculate13();

            /*** TotalBothTeamScore ***/
            matrixCalculate102();

            /*** WiningMargin ***/
            matrixCalculate141();
            /*** CornerRange ***/
            matrixCalculate117();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public void matrixCalculate12() {
        String playId = "12";
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\"," +
                "\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\"," +
                "\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1," +
                "\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\"," +
                "\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\"," +
                "\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\"," +
                "\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\"," +
                "\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}]," +
                "\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(12);
        String selection = "Yes";
        /*** 测试 投注项  yes ***/
        orderBean.getItems().get(0).setPlayOptions(selection);
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);
        MatrixBean matrixBeanYes = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBeanYes.getMatrixValueArray()));

        /*** 测试 投注项 NO ***/
        selection = "No";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBeanNo = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBeanNo.getMatrixValueArray()));
    }

    public void matrixCalculate24() {
        String playId = "24";
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(24);
        String selection = "Yes";
        /*** 测试 投注项  yes ***/
        orderBean.getItems().get(0).setPlayOptions(selection);
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);
        MatrixBean matrixBeanYes = matrixAdapter.process("1", playId, orderBean.getExtendBean());

        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBeanYes.getMatrixValueArray()));
        /*** 测试 投注项 NO ***/
        selection = "No";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBeanNo = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBeanNo.getMatrixValueArray()));
    }




    public void matrixCalculate81() {
        int playerIdInt = 81;
        String playId = "" + playerIdInt;
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        /*** 测试 投注项  yes ***/
        String selection = "Yes";
        orderBean.getItems().get(0).setPlayOptions(selection);
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);
        MatrixBean matrixBeanYes = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBeanYes.getMatrixValueArray()));
        /*** 测试 投注项 NO ***/
        selection = "No";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBeanNo = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBeanNo.getMatrixValueArray()));
    }


    public void matrixCalculate90() {
        int playerIdInt = 90;
        String playId = "" + playerIdInt;
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        /*** 测试 投注项  yes ***/
        orderBean.getItems().get(0).setPlayOptions("Yes");
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);
        MatrixBean matrixBeanYes = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        System.out.println(matrixBeanYes);
        /*** 测试 投注项 NO ***/
        orderBean.getItems().get(0).setPlayOptions("No");
        MatrixBean matrixBeanNo = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        System.out.println(matrixBeanNo);
    }


    public void matrixCalculate8() {
        int playerIdInt = 8;
        String playId = "" + playerIdInt;
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        /*** 测试 投注项  0 ***/
        orderBean.getItems().get(0).setPlayOptions("0");
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);
        MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        System.out.println(matrixBean0);
        /*** 测试 投注项 1 ***/
        orderBean.getItems().get(0).setPlayOptions("1");
        MatrixBean matrixBean1 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        System.out.println(matrixBean1);

        /*** 测试 投注项 2 ***/
        orderBean.getItems().get(0).setPlayOptions("2");
        MatrixBean matrixBean2 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        System.out.println(matrixBean2);
        /*** 测试 投注项 2 ***/
        orderBean.getItems().get(0).setPlayOptions("3");
        MatrixBean matrixBean3 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        System.out.println(matrixBean3);

    }


    public void matrixCalculate21() {
        int playerIdInt = 21;

        String playId = "" + playerIdInt;
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        String selection = "0";
        /*** 测试 投注项  0 ***/
        orderBean.getItems().get(0).setPlayOptions(selection);
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);
        MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));
        /*** 测试 投注项 1 ***/
        selection = "1";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean1 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean1.getMatrixValueArray()));

        /*** 测试 投注项 2 ***/
        selection = "2";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean2 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean2.getMatrixValueArray()));
        /*** 测试 投注项 3 ***/
        selection = "3+";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean3 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean3.getMatrixValueArray()));
    }

    public void matrixCalculate77() {
        int playerIdInt = 77;

        String playId = "" + playerIdInt;
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        /*** 测试 投注项  X ***/
        String selection = "X";
        orderBean.getItems().get(0).setPlayOptions(selection);
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);
        MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));

        /*** 测试 投注项 2 ***/    selection = "2";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean2 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean2.getMatrixValueArray()));
    }


    /***  Competitor1Total ***/
    public void matrixCalculate78() {
        int playerIdInt = 78;

        String playId = "" + playerIdInt;
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);
        
        String selection = "Odd";
        /*** 测试 投注项  Odd ***/
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));

        /*** 测试 投注项 Even ***/
        selection = "Even";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean2 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean2.getMatrixValueArray()));

    }


    /***  Competitor1Total ***/
    public void matrixCalculate87() {
        int playerIdInt = 87;

        String playId = "" + playerIdInt;
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        orderBean.getItems().get(0).setMarketValue("2.5");
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);

        /*** 测试 投注项  Over ***/
        String selection = "Over";
        orderBean.getItems().get(0).setPlayOptions(selection);
        orderBean.getItems().get(0).setMarketValue("2.5");
        MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));

        orderBean.getItems().get(0).setMarketValue("2");
        matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));


        /*** 测试 投注项 Under ***/
        selection = "Under";
        orderBean.getItems().get(0).setPlayOptions(selection);
        orderBean.getItems().get(0).setMarketValue("2.5");
        MatrixBean matrixBean2 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean2.getMatrixValueArray()));
        orderBean.getItems().get(0).setMarketValue("2");
        matrixBean2 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean2.getMatrixValueArray()));

    }

    /***  Competitor1Total ***/
    public void matrixCalculate10() {
        int playerIdInt = 10;

        String playId = "" + playerIdInt;
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);
        
        /*** 测试 投注项  Over ***/
        String selection = "Over";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));


        /*** 测试 投注项 Under ***/
        selection = "Under";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean2 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean2.getMatrixValueArray()));

    }

    /*** Competitor1WinNoNil ***/
    public void matrixCalculate82() {
        int playerIdInt = 82;

        String playId = "" + playerIdInt;
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);
        
        /*** 测试 投注项  Yes ***/
        String selection = "Yes";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));


        /*** 测试 投注项 No ***/
        selection = "No";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean2 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean2.getMatrixValueArray()));

    }

    /*** Competitor2CleanSheet ***/
    public void matrixCalculate79() {
        int playerIdInt = 79;

        String playId = "" + playerIdInt;
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);

        /*** 测试 投注项  Yes ***/
        String selection = "Yes";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));


        /*** 测试 投注项 No ***/
        selection =  "No";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean2 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean2.getMatrixValueArray()));
    }

    public void matrixCalculate100() {
        int playerIdInt = 100;

        String playId = "" + playerIdInt;
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);

        /*** 测试 投注项  Yes ***/
        String selection = "Yes";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));

        /*** 测试 投注项 No ***/
        selection =  "No";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean2 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean2.getMatrixValueArray()));
    }

    /*** Competitor2ExactGoals ***/
    public void matrixCalculate9() {
        int playerIdInt = 9;

        String playId = "" + playerIdInt;
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);

        /*** 测试 投注项  0 ***/
        String selection = "0";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));

        /*** 测试 投注项  1 ***/
        selection = "1";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean1 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean1.getMatrixValueArray()));


        /*** 测试 投注项  2 ***/
        selection = "2";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean2 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean2.getMatrixValueArray()));

        /*** 测试 投注项  2 ***/
        selection = "3+";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean3 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean3.getMatrixValueArray()));
        
    }

    /*** Competitor2NoBet ***/
    public void matrixCalculate91() {
        int playerIdInt = 91;

        String playId = "" + playerIdInt;
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);
        
        /*** 测试 投注项  1 ***/
        String selection = "1";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));

        /*** 测试 投注项  X ***/
        selection = "X";
        orderBean.getItems().get(0).setPlayOptions(selection);
        orderBean.setExtendBean(extendBean);
        MatrixBean matrixBean1 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean1.getMatrixValueArray()));

    }


    /***Competitor2OddEven***/
    public void matrixCalculate92() {
        int playerIdInt = 92;

        String playId = "" + playerIdInt;
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);

        /*** 测试 投注项  Odd ***/
        String selection = "Odd";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));

        /*** 测试 投注项  Even ***/
        selection =   "Even";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean1 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean1.getMatrixValueArray()));
    }

    /*** Competitor2Total ***/
    public void matrixCalculate11() {
        int playerIdInt = 11;
        String playId = "" + playerIdInt;
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);
        
        /*** 测试 投注项  Over ***/
        String selection = "Over";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));


        /*** 测试 投注项 Under ***/
        selection = "Under";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean2 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean2.getMatrixValueArray()));
    }

    public void matrixCalculate97() {

        int playerIdInt = 97;
        String playId = "" + playerIdInt;
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);

        /*** 测试 投注项  Over ***/
        String selection = "Over";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));


        /*** 测试 投注项 Under ***/
        selection = "Under";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean2 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean2.getMatrixValueArray()));

    }

    /*** Competitor2WinNoNil ***/
    public void matrixCalculate80() {
        int playerIdInt = 80;
        String playId = "" + playerIdInt;
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);

        /*** 测试 投注项  Yes ***/
        String selection = "Yes";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));

        /*** 测试 投注项 No ***/
        selection = "No";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean2 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean2.getMatrixValueArray()));
    }

    /*** CorrectScore ***/
    public void matrixCalculate7() {
        int playerIdInt = 7;
        String playId = "" + playerIdInt;
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                String selection = "" + i + ":" + j;
                /*** 测试 投注项  Yes ***/
                orderBean.getItems().get(0).setPlayOptions(selection);
                MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
                Assert.isTrue(matrixBean0.getMatrixValueArray()[i][j] == -5000, "投注项:" + selection + ".投注结果计算错误");
                //Assert.isTrue(matrixBean0.getMatrixValueArray()[i][j] == 10000, "投注项:" + selection + ".投注结果计算错误"); 
            }
        }
        /*** 测试 投注项  Yes ***/
        orderBean.getItems().get(0).setPlayOptions("Other");
        MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                String selection = "" + i + ":" + j;
                /*** 测试 投注项  Other ***/
                Assert.isTrue(matrixBean0.getMatrixValueArray()[i][j] == 10000, "投注项:" + selection + ".投注结果计算错误");
                //Assert.isTrue(matrixBean0.getMatrixValueArray()[i][j] == 10000, "投注项:" + selection + ".投注结果计算错误");
            }
        }
    }


    /***  DoubleChance  ***/
    public void matrixCalculate6() {
        int playerIdInt = 6;
        String playId = "" + playerIdInt;
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);
        
        /*** 测试 投注项  1X ***/
        String selection = "1X";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));

        /*** 测试 投注项 12 ***/
        selection = "12";
        orderBean.getItems().get(0).setPlayOptions("12");
        MatrixBean matrixBean2 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean2.getMatrixValueArray()));

        /*** 测试 投注项 X2 ***/
        selection = "X2";
        orderBean.getItems().get(0).setPlayOptions("X2");
        MatrixBean matrixBean3 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean3.getMatrixValueArray()));

    }


    /***  DoubleChanceBothTeamScore  ***/
    public void matrixCalculate107() {
        int playerIdInt = 107;
        String playId = "" + playerIdInt;
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);
        
        /*** 测试 投注项  1XAndYes ***/
        String selection = "1XAndYes";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));

        /*** 测试 投注项 12AndYes ***/
        selection = "12AndYes";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean2 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean2.getMatrixValueArray()));

        /*** 测试 投注项 X2AndYes ***/
        selection = "X2AndYes";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean3 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean3.getMatrixValueArray()));

        /*** 测试 投注项 1XAndNo ***/
        selection = "1XAndNo";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean4 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean4.getMatrixValueArray()));

        /*** 测试 投注项 12AndNo ***/
        selection = "12AndNo";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean5 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean5.getMatrixValueArray()));

        /*** 测试 投注项 X2AndNo ***/
        selection = "X2AndNo";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean6 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean6.getMatrixValueArray()));
    }

    /*** DrawNoBet ***/
    public void matrixCalculate4() {
        int playerIdInt = 4;
        String playId = "" + playerIdInt;
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);
        
        /*** 测试 投注项  1 ***/
        String selection = "1";
        orderBean.getItems().get(0).setPlayOptions(selection);
        orderBean.getItems().get(0).setMarketValue("2.5/3");
        MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));

        /*** 测试 投注项 2 ***/
        selection = "2";
        orderBean.getItems().get(0).setPlayOptions(selection);
        orderBean.getItems().get(0).setMarketValue("-1.5/2");
        MatrixBean matrixBean2 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean2.getMatrixValueArray()));
    }

    /*** matrixCalculate14 ***/

    /*** ExactGoals ***/
    public void matrixCalculate14() {
        int playerIdInt = 14;
        String playId = "" + playerIdInt;
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);
        
        /*** 测试 投注项  0  ***/
        String selection = "0";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));


        /*** 测试 投注项 1 ***/
        selection = "1";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean2 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean2.getMatrixValueArray()));

        /*** 测试 投注项 2 ***/
        selection = "2";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean3 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean3.getMatrixValueArray()));

        /*** 测试 投注项 3 ***/
        selection = "3";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean4 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean4.getMatrixValueArray()));

        /*** 测试 投注项 4 ***/
        selection = "4";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean5 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean5.getMatrixValueArray()));

        /*** 测试 投注项 5 ***/
        selection = "5";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean6 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean6.getMatrixValueArray()));


        /*** 测试 投注项 6 ***/
        selection = "6+";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean7 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean7.getMatrixValueArray()));
        
    }

    /*** FirstCorrectScore ***/
    public void matrixCalculate20() {
        int playerIdInt = 20;
        String playId = "" + playerIdInt;
        String allSelections = "0:0,1:0,2:0,3:0,0:1,1:1,2:1,0:2,1:2,0:3,Other";
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);
        
        String[] selections = allSelections.split(",");
        for (String selection : selections) {
            /*** 测试 投注项  selection ***/
            orderBean.getItems().get(0).setPlayOptions(selection);
            MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
            logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));
        }
    }

    /*** FirstHalfExactGoals ***/
    public void matrixCalculate23() {

        int playerIdInt = 23;
        String playId = "" + playerIdInt;
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
        orderBean.setExtendBean(extendBean);
        
        /*** 测试 投注项  0  ***/
        String selection = "0";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));

        /*** 测试 投注项 1 ***/
        selection = "1";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean2 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean2.getMatrixValueArray()));

        /*** 测试 投注项 2 ***/
        selection = "2";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean3 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean3.getMatrixValueArray()));

        /*** 测试 投注项 3 ***/
        selection = "3";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean4 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean4.getMatrixValueArray()));

        /*** 测试 投注项 4 ***/
        selection = "4+";
        orderBean.getItems().get(0).setPlayOptions(selection);
        MatrixBean matrixBean5 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
        logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean5.getMatrixValueArray()));
        

    }

    /*** GoalRange  ***/
    public void matrixCalculate68() {
        int playerIdInt = 68;
        String playId = "" + playerIdInt;
        String allSelections = "0-1,2-3,4-5,6+";
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        String[] selections = allSelections.split(",");
        for (String selection : selections) {
            /*** 测试 投注项  selection ***/
            orderBean.getItems().get(0).setPlayOptions(selection);
            ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
            orderBean.setExtendBean(extendBean);
            MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
            logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));

        }
    }


    /*** HandicapOr1X2 ***/
    public void matrixCalculate1() {
        int playerIdInt = 1;
        String playId = "" + playerIdInt;
        String allSelections = "1,X,2";
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        String[] selections = allSelections.split(",");
        for (String selection : selections) {
            /*** 测试 投注项  selection ***/
            orderBean.getItems().get(0).setPlayOptions(selection);
            ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
            orderBean.setExtendBean(extendBean);
            MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
            logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));
        }
        playerIdInt = 3;
        playId = "" + playerIdInt;
        orderBean.getItems().get(0).setMarketValue("-1.5");
        for (String selection : selections) {
            /*** 测试 投注项  selection ***/
            orderBean.getItems().get(0).setPlayOptions(selection);
            ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
            orderBean.setExtendBean(extendBean);
            MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
            logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));
        }
    }


    /*** Match1X2BothTeamScore ***/
    public void matrixCalculate101() {
        int playerIdInt = 101;
        String playId = "" + playerIdInt;
        String allSelections = "1AndYes,XAndYes,2AndYes,1AndNo,XAndNo,2AndNo";
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        String[] selections = allSelections.split(",");
        for (String selection : selections) {
            /*** 测试 投注项  selection ***/
            orderBean.getItems().get(0).setPlayOptions(selection);
            ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
            orderBean.setExtendBean(extendBean);
            MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
            logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));
        }
    }


    /*** OddEven ***/
    public void matrixCalculate15() {
        int playerIdInt = 15;
        String playId = "" + playerIdInt;
        String allSelections = "Odd,Even";
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        String[] selections = allSelections.split(",");
        for (String selection : selections) {
            /*** 测试 投注项  selection ***/
            orderBean.getItems().get(0).setPlayOptions(selection);
            ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
            orderBean.setExtendBean(extendBean);
            MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
            logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));
        }
    }

    /*** Total ***/
    public void matrixCalculate2() {
        int playerIdInt = 2;
        String playId = "" + playerIdInt;
        String allSelections = "Over,Under";
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        orderBean.getItems().get(0).setMarketValue("1.5");
        String[] selections = allSelections.split(",");
        for (String selection : selections) {
            /*** 测试 投注项  selection ***/
            orderBean.getItems().get(0).setPlayOptions(selection);
            orderBean.getItems().get(0).setMarketValue("2.5/3");
            ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
            orderBean.setExtendBean(extendBean);
            MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
            logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));
        }
    }

    /*** TotalAnd1X2 ***/
    public void matrixCalculate13() {
        int playerIdInt = 13;
        String playId = "" + playerIdInt;
        String allSelections = "1AndUnder,XAndUnder,2AndUnder,1AndOver,XAndOver,2AndOver";
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        orderBean.getItems().get(0).setMarketValue("2.5");
        String[] selections = allSelections.split(",");
        for (String selection : selections) {
            /*** 测试 投注项  selection ***/
            orderBean.getItems().get(0).setPlayOptions(selection);
            ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
            orderBean.setExtendBean(extendBean);
            MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
            logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));
        }
    }

    /*** TotalBothTeamScore ***/
    public void matrixCalculate102() {
        int playerIdInt = 102;
        String playId = "" + playerIdInt;
        String allSelections = "OverAndYes,OverAndNo,UnderAndYes,UnderAndNo";
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        orderBean.getItems().get(0).setMarketValue("2.5");
        String[] selections = allSelections.split(",");
        for (String selection : selections) {
            /*** 测试 投注项  selection ***/
            orderBean.getItems().get(0).setPlayOptions(selection);
            ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
            orderBean.setExtendBean(extendBean);
            MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
            logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));
        }
    }

    /*** WiningMargin ***/
    public void matrixCalculate141() {
        int playerIdInt = 141;
        String playId = "" + playerIdInt;
        String allSelections = "1And1,1And2,1And3+,2And1,2And2,2And3+,X,1And6+,2And6+,Other";
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        orderBean.getItems().get(0).setMarketValue("2.5");
        String[] selections = allSelections.split(",");
        for (String selection : selections) {
            /*** 测试 投注项  selection ***/
            orderBean.getItems().get(0).setPlayOptions(selection);
            ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
            orderBean.setExtendBean(extendBean);
            MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
            logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));
        }


    }

    
    /*** CornerRange ***/
    public void matrixCalculate117() {
        int playerIdInt = 117;
        String playId = "" + playerIdInt;
        String allSelections = "0-8,9-11,12+";
        String order = "{\"createTime\":1585618333229,\"createUser\":\"系统\",\"currencyCode\":\"CNY\",\"deviceType\":1,\"ip\":\"172.18.180.41\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":10000,\"betNo\":\"21067319910400\",\"betTime\":1585618333229,\"createTime\":1585618333229,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2020-03-31\",\"handleAfterOddsValue\":1.5,\"handleAfterOddsValue1\":1.5,\"handledBetAmout\":100,\"isValid\":1,\"marketId\":1244798098112299009,\"marketType\":\"EU\",\"matchId\":368224,\"matchInfo\":\"Redenkov·Dmitry VS 科罗列夫·帕维尔\",\"matchName\":\"Liga Pro\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":5000.0,\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"oddFinally\":\"1.50\",\"oddsValue\":150000.0,\"orderNo\":\"11067319910401\",\"platform\":\"PA\",\"playId\":1,\"playName\":\"比赛获胜\",\"playOptions\":\"1\",\"playOptionsId\":1244798098166824962,\"playOptionsName\":\"\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"乒乓球\",\"tournamentId\":4352,\"turnamentLevel\":6,\"uid\":128691269827117056,\"validateResult\":1}],\"modifyTime\":1585618333229,\"modifyUser\":\"系统\",\"orderAmountTotal\":10000,\"orderNo\":\"11067319910401\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"remark\":\"用户下注\",\"seriesType\":1,\"tenantId\":1,\"tenantName\":\"test\",\"uid\":128691269827117056,\"userFlag\":\"\",\"validateResult\":1}";
        OrderBean orderBean = JSONObject.parseObject(order, OrderBean.class);
        orderBean.getItems().get(0).setPlayId(playerIdInt);
        String[] selections = allSelections.split(",");
        for (String selection : selections) {
            /*** 测试 投注项  selection ***/
            orderBean.getItems().get(0).setPlayOptions(selection);
            ExtendBean extendBean = buildExtendBean(orderBean, orderBean.getItems().get(0));
            orderBean.setExtendBean(extendBean);
            MatrixBean matrixBean0 = matrixAdapter.process("1", playId, orderBean.getExtendBean());
            logger.info("玩法id:" + playId + ";投注项:" + selection + ";计算结果:" + JSONObject.toJSON(matrixBean0.getMatrixValueArray()));
        }
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
