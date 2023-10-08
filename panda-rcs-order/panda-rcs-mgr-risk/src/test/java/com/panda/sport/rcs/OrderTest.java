package com.panda.sport.rcs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.ThreewayOverLoadTriggerItem;
import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.mgr.RiskBootstrap;
import com.panda.sport.rcs.mgr.calculator.service.impl.ThreeWayAmountLimitServiceImpl;
import com.panda.sport.rcs.mgr.mq.impl.trigger.TriggerChangeImpl;
import com.panda.sport.rcs.mgr.service.impl.MarketOddsChangeCalculationServiceImpl;
import com.panda.sport.rcs.mgr.wrapper.ITOrderService;
import com.panda.sport.rcs.mgr.wrapper.impl.TOrderServiceImpl;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.AmountTypeVo;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.TOrder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs
 * @Description :  TODO
 * @Date: 2020-01-20 18:51
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RiskBootstrap.class)
@Slf4j
public class OrderTest {

    @Autowired
    TriggerChangeImpl triggerChangeImpl;

    @Autowired
    private ITOrderService orderService;

    @Autowired
    TOrderServiceImpl tOrderService;


    @Autowired
    TOrderDetailMapper tOrderDetailMapper;

//    @Test
    public void test22(){
    	//自动  足球大小，独赢
//    	String orderNo = "{\"acceptOdds\":1,\"createTime\":1610682995553,\"currencyCode\":\"CNY\",\"deviceType\":2,\"handleStatus\":0,\"infoStatus\":1,\"ip\":\"43.243.94.205\",\"items\":[{\"betAmount\":10000,\"betAmount1\":100,\"betNo\":\"125247442370073\",\"betTime\":1610682995553,\"createTime\":1610682995553,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2021-01-15\",\"handleAfterOddsValue\":1.94,\"handleAfterOddsValue1\":1.94,\"handleStatus\":0,\"handledBetAmout\":100,\"marketId\":141705425321225268,\"marketType\":\"EU\",\"marketValue\":\"2\",\"matchId\":1876442,\"matchInfo\":\"恩比-Gallen v 瑟亚米卡\",\"matchName\":\"埃及超级联赛\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":1034.0,\"modifyTime\":1610682995553,\"modifyUser\":\"系统\",\"oddFinally\":\"1.94\",\"oddsValue\":194000.0,\"orderNo\":\"125247442370073\",\"orderStatus\":1,\"paidAmount\":2134.00,\"paidAmount1\":21.34,\"placeNum\":1,\"platform\":\"PA\",\"playId\":2,\"playName\":\"全场大小\",\"playOptions\":\"Over\",\"playOptionsId\":144404515046232272,\"playOptionsName\":\"大 2\",\"recType\":0,\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"足球\",\"tournamentId\":866,\"tradeType\":0,\"turnamentLevel\":5,\"uid\":216002668372893696,\"validateResult\":1}],\"modifyTime\":1610682995553,\"orderAmountTotal\":10000,\"orderNo\":\"125247442370073\",\"orderStatus\":1,\"productAmountTotal\":10000,\"productCount\":1,\"seriesType\":1,\"tenantId\":2,\"tenantName\":\"oubao\",\"uid\":216002668372893696,\"userFlag\":\"\",\"userTagLevel\":17,\"username\":\"\",\"validateResult\":1,\"vipLevel\":0}";
//    	String orderNo = "{\"acceptOdds\":2,\"createTime\":1610689935931,\"currencyCode\":\"CNY\",\"deviceType\":2,\"handleStatus\":0,\"infoStatus\":1,\"ip\":\"43.243.94.205\",\"items\":[{\"betAmount\":10000,\"betAmount1\":100,\"betNo\":\"32543090122753\",\"betTime\":1610689935931,\"createTime\":1610689935931,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2021-01-15\",\"handleAfterOddsValue\":2.29,\"handleAfterOddsValue1\":2.29,\"handleStatus\":0,\"handledBetAmout\":100,\"marketId\":146625247235413002,\"marketType\":\"EU\",\"matchId\":1876442,\"matchInfo\":\"恩比-Gallen v 瑟亚米卡\",\"matchName\":\"埃及超级联赛\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":12900.0,\"modifyTime\":1610689935931,\"modifyUser\":\"系统\",\"oddFinally\":\"2.29\",\"oddsValue\":229000.0,\"orderNo\":\"32543090122753\",\"orderStatus\":1,\"paidAmount\":22900.00,\"paidAmount1\":229.00,\"placeNum\":1,\"platform\":\"PA\",\"playId\":1,\"playName\":\"全场独赢\",\"playOptions\":\"1\",\"playOptionsId\":146241814130003493,\"playOptionsName\":\"恩比-Gallen\",\"recType\":0,\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"足球\",\"tournamentId\":866,\"tradeType\":0,\"turnamentLevel\":5,\"uid\":199341280913203200,\"validateResult\":1}],\"modifyTime\":1610689935931,\"orderAmountTotal\":10000,\"orderNo\":\"32543090122753\",\"orderStatus\":1,\"productAmountTotal\":10000,\"productCount\":1,\"seriesType\":1,\"tenantId\":2,\"tenantName\":\"oubao\",\"uid\":199341280913203200,\"userFlag\":\"\",\"userTagLevel\":2,\"username\":\"\",\"validateResult\":1,\"vipLevel\":0}";
    	//自动 篮球 让分
    //	String orderNo = "{\"createTime\":1610692362837,\"currencyCode\":\"CNY\",\"deviceType\":2,\"handleStatus\":0,\"infoStatus\":1,\"ip\":\"43.243.94.205\",\"items\":[{\"betAmount\":10000,\"betAmount1\":100,\"betNo\":\"153355723407221\",\"betTime\":1610692362837,\"createTime\":1610692362837,\"handleAfterOddsValue\":1.96,\"handleAfterOddsValue1\":1.96,\"handledBetAmout\":100,\"id\":380423,\"isRelationScore\":0,\"marketId\":148521403357188925,\"marketType\":\"EU\",\"marketValue\":\"-10.5\",\"matchId\":1719974,\"matchInfo\":\"上海大鯊魚 v 天津先行者\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":9600.0,\"modifyTime\":1610692362837,\"oddFinally\":\"1.96\",\"oddsValue\":196000.0,\"orderNo\":\"153355723407221\",\"orderStatus\":1,\"paidAmount\":19600.00,\"paidAmount1\":196.00,\"placeNum\":1,\"playId\":39,\"playName\":\"Asian handicap for whole match, including overtime\",\"playOptions\":\"1\",\"playOptionsId\":144085191035861737,\"playOptionsName\":\"1 -10.5\",\"recType\":1,\"recVal\":\"9600\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":2,\"sportName\":\"篮球\",\"tournamentId\":933,\"uid\":199341280913203200,\"validateResult\":1}],\"modifyTime\":1610692362837,\"orderAmountTotal\":10000,\"orderNo\":\"153355723407221\",\"orderStatus\":1,\"productAmountTotal\":10000,\"productCount\":1,\"seriesType\":1,\"tenantId\":2,\"uid\":199341280913203200,\"userTagLevel\":0,\"username\":\"\",\"validateResult\":1,\"vipLevel\":0}";
//    	String orderNo = "{\"createTime\":1610692668897,\"currencyCode\":\"CNY\",\"deviceType\":2,\"handleStatus\":0,\"infoStatus\":1,\"ip\":\"43.243.94.205\",\"items\":[{\"betAmount\":11100,\"betAmount1\":111,\"betNo\":\"354004750008321\",\"betTime\":1610692668897,\"createTime\":1610692668897,\"handleAfterOddsValue\":1.11,\"handleAfterOddsValue1\":1.11,\"handledBetAmout\":111,\"id\":380428,\"isRelationScore\":0,\"marketId\":141439470210004523,\"marketType\":\"EU\",\"matchId\":1719974,\"matchInfo\":\"上海大鯊魚 v 天津先行者\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":1221.0,\"modifyTime\":1610692668897,\"oddFinally\":\"1.11\",\"oddsValue\":111000.00000000001,\"orderNo\":\"354004750008321\",\"orderStatus\":1,\"paidAmount\":12321.00,\"paidAmount1\":123.21,\"placeNum\":1,\"playId\":37,\"playName\":\"111\",\"playOptions\":\"1\",\"playOptionsId\":140295116635259127,\"playOptionsName\":\"1\",\"recType\":1,\"recVal\":\"1221\",\"riskChannel\":1,\"scoreBenchmark\":\"\",\"sportId\":2,\"sportName\":\"篮球\",\"tournamentId\":933,\"uid\":199341280913203200,\"validateResult\":1}],\"modifyTime\":1610692668897,\"orderAmountTotal\":11100,\"orderNo\":\"354004750008321\",\"orderStatus\":1,\"productAmountTotal\":11100,\"productCount\":1,\"seriesType\":1,\"tenantId\":2,\"uid\":199341280913203200,\"userTagLevel\":0,\"username\":\"\",\"validateResult\":1,\"vipLevel\":0}";
    	
    	//OrderBean orderBean = JSONObject.parseObject(orderNo,OrderBean.class);
    	//triggerChangeImpl.orderHandle(orderBean);
    }
//    @Test
    public void getOrderTest(){
        String orderNO="4992024615867311";
       TOrder order=orderService.getOrderInfo(orderNO);
        System.out.println(order);
    }
//    @Test
    public void switchTest(){
//       BigDecimal volume=tOrderService.getMinVolumeByDeviceType(1);
//        System.out.println(volume);
    }

//    @Test
    public  void getExtendBeanTest(){
        List<String> list=new ArrayList<>();
        list.add("499225185067846");
        list.add("499225123014345");
         List<ExtendBean> orders=tOrderDetailMapper.queryOrderDetails(list);
        System.out.println(orders);
    }

    @Autowired
    private ProducerSendMessageUtils sendMessage;
//    @Test
    public void testMQ(){
        JSONArray jsonArray = new JSONArray();
        //把刷新的key统计去刷新对应的本地缓存
        JSONObject earlyMap = new JSONObject();
        earlyMap.put("key", "rcs:limit:sportId.6:tournamentLevel:-1.dataType.3:matchType.0:UserSingle");
        earlyMap.put("type", "merchant_single_limit");
        JSONObject liveMap = new JSONObject();
        liveMap.put("key", "rcs:limit:sportId.6:tournamentLevel:-1.dataType.3:matchType.1:UserSingle");
        liveMap.put("type", "merchant_single_limit");
        jsonArray.add(earlyMap);
        jsonArray.add(liveMap);
        //将修改的信息同步一个mq广播 清除本地缓存
        sendMessage.sendMessage("rcs_local_cache_clear_sdk,,merchant_single_limit", jsonArray);
    }


    /**
     * 藏单1.5百分比测试
     */
//    @Test
    public void hidePercentageTest(){
        // String data = "{\"acceptOdds\":2,\"createTime\":1647006200097,\"currencyCode\":\"CNY\",\"deviceType\":2,\"handleStatus\":0,\"infoStatus\":0,\"ip\":\"165.84.166.145\",\"items\":[{\"betAmount\":1000,\"betAmount1\":10,\"betNo\":\"48815887628566\",\"betTime\":1647006200097,\"createTime\":1647006200097,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2022-03-11\",\"handleAfterOddsValue\":1.63,\"handleAfterOddsValue1\":1.63,\"handleStatus\":0,\"handledBetAmout\":10,\"marketId\":144944503023610913,\"marketType\":\"EU\",\"marketValue\":\"0.5\",\"matchId\":3122991,\"matchInfo\":\"结算3.11_20:05 v 结算3.11客队\",\"matchName\":\"结算3.11_20:05\",\"matchProcessId\":0,\"matchType\":2,\"maxWinAmount\":19.83,\"modifyTime\":1647006200097,\"modifyUser\":\"系统\",\"oddFinally\":\"1.63\",\"oddsValue\":163000.0,\"orderNo\":\"3294012400198658\",\"orderStatus\":0,\"otherOddsValue\":236000.0,\"otherScore\":\"0:0\",\"paidAmount\":1630.00,\"paidAmount1\":16.30,\"placeNum\":1,\"platform\":\"PA\",\"playId\":2,\"playName\":\"全场大小\",\"playOptions\":\"Over\",\"playOptionsId\":143158755610247456,\"playOptionsName\":\"大 0.5\",\"scoreBenchmark\":\"0:0\",\"sportId\":1,\"sportName\":\"足球\",\"subPlayId\":\"2\",\"tournamentId\":835439,\"tradeType\":0,\"turnamentLevel\":1,\"uid\":407419678185623552,\"validateResult\":0},{\"betAmount\":1000,\"betAmount1\":10,\"betNo\":\"41528407946121\",\"betTime\":1647006200097,\"createTime\":1647006200097,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2022-03-11\",\"handleAfterOddsValue\":1.83,\"handleAfterOddsValue1\":1.83,\"handleStatus\":0,\"handledBetAmout\":10,\"marketId\":145093001387778761,\"marketType\":\"EU\",\"marketValue\":\"3/3.5\",\"matchId\":3120745,\"matchInfo\":\"阿斯顿维拉U23 v 南安普顿U23\",\"matchName\":\"英格兰超级联赛U23\",\"matchProcessId\":0,\"matchType\":1,\"maxWinAmount\":19.83,\"modifyTime\":1647006200097,\"modifyUser\":\"系统\",\"oddFinally\":\"1.83\",\"oddsValue\":183000.0,\"orderNo\":\"3294012400198658\",\"orderStatus\":0,\"otherOddsValue\":205000.0,\"otherScore\":\"\",\"paidAmount\":1830.00,\"paidAmount1\":18.30,\"placeNum\":1,\"platform\":\"PA\",\"playId\":2,\"playName\":\"全场大小\",\"playOptions\":\"Over\",\"playOptionsId\":148774061473384189,\"playOptionsName\":\"大 3/3.5\",\"scoreBenchmark\":\"\",\"sportId\":1,\"sportName\":\"足球\",\"subPlayId\":\"2\",\"tournamentId\":821960,\"tradeType\":0,\"turnamentLevel\":1,\"uid\":407419678185623552,\"validateResult\":0}],\"modifyTime\":1647006200097,\"orderAmountTotal\":1000,\"orderNo\":\"3294012400198658\",\"orderStatus\":0,\"productAmountTotal\":1000,\"productCount\":1,\"seriesType\":2001,\"tenantId\":2,\"tenantName\":\"试玩商户\",\"uid\":407419678185623552,\"userFlag\":\"\",\"userTagLevel\":3,\"username\":\"\",\"validateResult\":2,\"vipLevel\":0}";

        String data="{\"acceptOdds\":2,\"createTime\":1647173682442,\"currencyCode\":\"CNY\",\"deviceType\":2,\"handleStatus\":0,\"infoStatus\":0,\"ip\":\"165.84.166.145\",\"items\":[{\"betAmount\":10000,\"betAmount1\":100,\"betNo\":\"47657822684859\",\"betTime\":1647173682442,\"createTime\":1647173682442,\"createUser\":\"系统\",\"dataSourceCode\":\"SR\",\"dateExpect\":\"2022-03-13\",\"handleAfterOddsValue\":1.58,\"handleAfterOddsValue1\":1.58,\"handleStatus\":0,\"handledBetAmout\":100,\"marketId\":145637344447964544,\"marketType\":\"EU\",\"marketValue\":\"-1\",\"marketValueNew\":\"-1\",\"matchId\":3125905,\"matchInfo\":\"结算3.13主队 v 结算3.13客队\",\"matchName\":\"结算3.13_13-2\",\"matchProcessId\":0,\"matchType\":2,\"maxWinAmount\":5800.0,\"modifyTime\":1647173682442,\"modifyUser\":\"系统\",\"oddFinally\":\"0.58\",\"oddsValue\":158000.0,\"orderNo\":\"3294347364888437\",\"orderStatus\":0,\"otherOddsValue\":228000.0,\"otherScore\":\"0:0\",\"paidAmount\":15800.00,\"paidAmount1\":158.00,\"placeNum\":1,\"platform\":\"PA\",\"playId\":4,\"playName\":\"全场让球\",\"playOptions\":\"1\",\"playOptionsId\":147207461239627623,\"playOptionsName\":\"结算3.13主队  -1\",\"scoreBenchmark\":\"0:0\",\"sportId\":1,\"sportName\":\"足球\",\"subPlayId\":\"4\",\"tournamentId\":835439,\"tradeType\":0,\"turnamentLevel\":1,\"uid\":401506900518244352,\"validateResult\":0}],\"modifyTime\":1647173682442,\"orderAmountTotal\":10000,\"orderNo\":\"3294347364888437\",\"orderStatus\":0,\"productAmountTotal\":10000,\"productCount\":1,\"seriesType\":1,\"tenantId\":2,\"tenantName\":\"试玩商户\",\"uid\":401506900518244352,\"userFlag\":\"\",\"userTagLevel\":4,\"username\":\"\",\"validateResult\":2,\"vipLevel\":1}";
        OrderBean orderBean = JSONObject.parseObject(data, OrderBean.class);
        AmountTypeVo amountTypeVo = tOrderService.getVolumePercentage(orderBean, 1);
        System.out.println("============================="+JSON.toJSONString(amountTypeVo));
    }

    @Autowired
    ThreeWayAmountLimitServiceImpl marketOddsChangeCalculationService;
    @Test
    public void testForAoBasketball(){
        String item="{\"betAmount\":111100,\"betAmount1\":1111,\"betNo\":\"506559902505681\",\"betTime\":1688533008351,\"createTime\":1688533008351,\"handleAfterOddsValue\":4.7,\"handleAfterOddsValue1\":4.7,\"handleStatus\":0,\"handledBetAmout\":1111,\"id\":2015343,\"isRelationScore\":0,\"marketId\":146651542103510139,\"marketType\":\"EU\",\"marketValue\":\"11\",\"matchId\":3497354,\"matchInfo\":\"科技城篮球队 v Bc格洛里娅斯维亚托什恩\",\"matchProcessId\":13,\"matchType\":2,\"maxWinAmount\":411070.0,\"modifyTime\":1688533008351,\"oddFinally\":\"4.70\",\"oddsValue\":470000.0,\"orderNo\":\"5065599025056081\",\"orderStatus\":1,\"paidAmount\":522170.0,\"paidAmount1\":5221.7,\"placeNum\":1,\"playId\":209,\"playName\":\"净胜分 6项\",\"playOptions\":\"1And6-10\",\"playOptionsId\":149378592053049217,\"playOptionsName\":\"科技城篮球队-净胜6-10\",\"recType\":1,\"recVal\":\"411070\",\"riskChannel\":1,\"scoreBenchmark\":\"0:0\",\"sportId\":2,\"sportName\":\"篮球\",\"subPlayId\":\"209\",\"tournamentId\":1183471,\"uid\":506532288097200006,\"validateResult\":1,\"volumePercentage\":1.00}";
        OrderItem items=JSONObject.parseObject(item, OrderItem.class);
        String result="{\"awayLevelFirstOddsRate\":0.02,\"awayLevelSecondOddsRate\":0.04,\"awayMargin\":109.00,\"balanceOption\":0,\"dataSource\":0,\"homeLevelFirstMaxAmount\":500,\"homeLevelFirstOddsRate\":1.22,\"homeLevelSecondMaxAmount\":20000,\"homeLevelSecondOddsRate\":0.04,\"homeMargin\":109.00,\"isMultipleJumpMarket\":1,\"isMultipleJumpOdds\":1,\"isOpenJumpMarket\":1,\"isOpenJumpOdds\":1,\"margin\":109.00,\"marketId\":146651542103510139,\"marketIndex\":1,\"marketType\":\"EU\",\"matchId\":3497354,\"matchType\":0,\"maxOdds\":51.00,\"minOdds\":1.10,\"oddChangeRule\":1,\"oddsType\":\"1And6-10\",\"playId\":209,\"sportId\":2,\"subPlayId\":\"209\",\"tieMargin\":109.00,\"timeOutMargin\":109.00}";
        RcsMatchMarketConfig results=JSONObject.parseObject(result, RcsMatchMarketConfig.class);
        String js="[1,1,1,\"1111\",\"1111\",-1,1111]";
        JSONArray arr=JSONObject.parseArray(js);
        marketOddsChangeCalculationService.triggerChange(results,items,arr);
    }


    @Autowired
    TriggerChangeImpl triggerChange;

    @Test
    public void testForAoBasketball01() {
        String or = "{\"createTime\":1688703426475,\"currencyCode\":\"CNY\",\"deviceType\":2,\"handleStatus\":0,\"infoStatus\":1,\"ip\":\"172.21.165.161\",\"ipArea\":\"局域网,局域网,\",\"items\":[{\"betAmount\":100000,\"betAmount1\":1000,\"betNo\":\"506611027943730\",\"betTime\":1688703426475,\"createTime\":1688703426475,\"handleAfterOddsValue\":1.32,\"handleAfterOddsValue1\":1.32,\"handleStatus\":0,\"handledBetAmout\":1000,\"id\":2016403,\"isRelationScore\":0,\"marketId\":144512039745225963,\"marketType\":\"EU\",\"marketValue\":\"11+\",\"matchId\":3498921,\"matchInfo\":\"Auto Basketball Test001 v Auto Basketball Test002\",\"matchProcessId\":302,\"matchType\":2,\"maxWinAmount\":32000.0,\"modifyTime\":1688703426475,\"oddFinally\":\"1.32\",\"oddsValue\":132000.0,\"orderNo\":\"5066110279434030\",\"orderStatus\":1,\"paidAmount\":132000.00,\"paidAmount1\":1320.00,\"placeNum\":1,\"playId\":209,\"playName\":\"净胜分 6项\",\"playOptions\":\"1And11+\",\"playOptionsId\":146502278257108445,\"playOptionsName\":\"Auto Basketball Test001-净胜11+\",\"recType\":1,\"recVal\":\"32000\",\"riskChannel\":1,\"scoreBenchmark\":\"43:44\",\"sportId\":2,\"sportName\":\"篮球\",\"subPlayId\":\"209\",\"tournamentId\":848212,\"uid\":506532288097200006,\"validateResult\":1,\"volumePercentage\":1.00}],\"limitType\":1,\"modifyTime\":1688703426493,\"orderAmountTotal\":100000,\"orderNo\":\"5066110279434030\",\"orderStatus\":1,\"productAmountTotal\":100000,\"productCount\":1,\"seriesType\":1,\"tenantId\":2,\"uid\":506532288097200006,\"userTagLevel\":230,\"username\":\"\",\"validateResult\":1,\"vipLevel\":0}";
        OrderBean orderBean = JSONObject.parseObject(or, OrderBean.class);
        triggerChange.orderHandle(orderBean);
    }
    @Autowired
    public MarketOddsChangeCalculationServiceImpl marketOddsChangeCalculationService1;

    @Test
    public void test(){
        String a37 = "{\"awayAutoChangeRate\":0.0,\"awayMargin\":104.00,\"dataSource\":1,\"fixDirectionEnum\":\"DESC\",\"homeLevelFirstOddsRate\":1.33,\"homeMargin\":104.00,\"margin\":104.00,\"marketId\":142110763403524000,\"marketType\":\"EU\",\"matchId\":3497401,\"matchType\":2,\"maxOdds\":\"51.00\",\"minOdds\":\"1.10\",\"msg\":\"Bataan Risers v Batangas City Athletics(match_manage_id):净胜分 3项已触发早盘跳水封盘，请及时检查开启。\",\n" +
                "\"placeNum\":1,\"playId\":200,\"playOptionsId\":146348284014263537,\"tieMargin\":104.00}";

        ThreewayOverLoadTriggerItem c2=JSONObject.parseObject(a37,ThreewayOverLoadTriggerItem.class);
        String config ="{\"awayLevelFirstOddsRate\":0.02,\"awayLevelSecondOddsRate\":0.04,\"awayMargin\":104.00,\"balanceOption\":0,\"dataSource\":1,\"homeLevelFirstMaxAmount\":100,\"homeLevelFirstOddsRate\":1.33,\"homeLevelSecondMaxAmount\":20000,\"homeLevelSecondOddsRate\":0.04,\"homeMargin\":104.00,\"isMultipleJumpMarket\":1,\"isMultipleJumpOdds\":1,\"isOpenJumpMarket\":1,\"isOpenJumpOdds\":1,\"margin\":104.00,\"marketId\":142110763403524000,\"marketIndex\":1,\"marketType\":\"EU\",\"matchId\":3497401,\"matchType\":0,\"maxOdds\":51.00,\"minOdds\":1.10,\"oddChangeRule\":1,\"oddsType\":\"2And6+\",\"playId\":200,\"sportId\":2,\"subPlayId\":\"200\",\"tieMargin\":104.00,\"timeOutMargin\":106.00}";

        RcsMatchMarketConfig c1=JSONObject.parseObject(config,RcsMatchMarketConfig.class);
        marketOddsChangeCalculationService1.calculationOddsByOverLoadTrigger(c1,c2);
    }

}

